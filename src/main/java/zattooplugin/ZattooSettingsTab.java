package zattooplugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import devplugin.SettingsTab;
import util.ui.Localizer;
import util.ui.UiUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

public final class ZattooSettingsTab implements SettingsTab {
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooSettingsTab.class);
    private JComboBox mCountry;
    private ZattooSettings mSettings;
    private JCheckBox mUpdateCustomChannels;
    private JCheckBox mReread;
    private JTextArea mEditor;
    private JButton mSaveBtn;
    private Document mDocument;
    private JRadioButton mUpdateByReplace;
    private JRadioButton mUpdateByMerge;
    private JRadioButton mMergeAndReplace;
    private JRadioButton mMergeOnlyNew;

    public ZattooSettingsTab(ZattooSettings settings) {
        this.mSettings = settings;
    }

    public JPanel createSettingsPanel() {
//        PanelBuilder builder = new PanelBuilder(new FormLayout(
//                FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
//                        FormSpecs.PREF_COLSPEC.encode() + "," +
//                        FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
//                        FormSpecs.PREF_COLSPEC.encode() + "," +
//                        FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
//                        FormSpecs.PREF_COLSPEC.encode() + "," +
//                        FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
//                        FormSpecs.PREF_COLSPEC.encode() + "," +
//                        FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
//                        FormSpecs.PREF_COLSPEC.encode() + "," +
//                        FormSpecs.GLUE_COLSPEC.encode(), ""));
        PanelBuilder builder = new PanelBuilder(
                new FormLayout( "5dlu,20dlu,5dlu,10dlu,5dlu,10dlu,0px:g"
                , ""));
        CellConstraints cc = new CellConstraints();
        ZattooCountry[] countries = new ZattooCountry[]{
                new ZattooCountry("de", mLocalizer.msg("country_de", "Germany")),
                new ZattooCountry("ch", mLocalizer.msg("country_ch", "Switzerland")),
                new ZattooCountry("at", mLocalizer.msg("country_at", "Austria")),
                new ZattooCountry("custom", mLocalizer.msg("country_custom", "Customized list of channels")),
        };
        this.mCountry = new JComboBox(countries);
        this.mCountry.setSelectedItem(new ZattooCountry(this.mSettings.getCountry(), ""));
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow();
        builder.add(new JLabel(mLocalizer.msg("country", "Country:")), cc.xy(2, builder.getRow()));
        builder.add(this.mCountry, cc.xyw(4, builder.getRow(),4));
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);

        // Separator
        builder.addSeparator(mLocalizer.msg("customchannels", "Customized list of channels"));

        // Update custom channels
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        mUpdateCustomChannels = new JCheckBox(mLocalizer.msg("updateCustomChannels", "updateCustomChannels"), false);
        builder.add(mUpdateCustomChannels, cc.xyw(2, builder.getRow(), 5));

        // Update by replace
        //builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mUpdateByReplace = new JRadioButton(mLocalizer.msg("updateByReplace", "updateByReplace"),mSettings.getUpdateByReplace());
        builder.add(mUpdateByReplace,cc.xyw(4, builder.getRow(), 3));
        // Update by merge
        //builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mUpdateByMerge = new JRadioButton(mLocalizer.msg("updateByMerge", "updateByMerge"), mSettings.getUpdateByMerge());
        builder.add(mUpdateByMerge,cc.xyw(4, builder.getRow(), 3));
        // Group
        ButtonGroup buttonGroupUpdate = new ButtonGroup();
        buttonGroupUpdate.add(mUpdateByReplace);
        buttonGroupUpdate.add(mUpdateByMerge);

        // Merge and replace
        //builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mMergeAndReplace = new JRadioButton(mLocalizer.msg("mergeandreplace", "mergeandreplace"),mSettings.getMergeAndReplace());
        builder.add(mMergeAndReplace,cc.xy(6, builder.getRow()));
        // Merge only new
        //builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mMergeOnlyNew = new JRadioButton(mLocalizer.msg("mergeonlynew", "mergeonlynew"),mSettings.getMergeOnlyNew());
        builder.add(mMergeOnlyNew,cc.xy(6, builder.getRow()));
        // Group
        ButtonGroup buttonGroupMerge = new ButtonGroup();
        buttonGroupMerge.add(mMergeAndReplace);
        buttonGroupMerge.add(mMergeOnlyNew);

        // Reread list
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        mReread = new JCheckBox(mLocalizer.msg("reread", "Reread customized list of channels"), this.mSettings.isRereadCustomChannelProperties());
        builder.add(mReread, cc.xyw(2, builder.getRow(), 5));

        // Property file: title
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFileHeader = new JLabel(mLocalizer.msg("propertyFile", "Configuration file") + ":");
        builder.add(labelPropertyFileHeader, cc.xyw(2, builder.getRow(), 5));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFile = new JLabel( "     " + mSettings.getCustomChannelProperties());
        builder.add(labelPropertyFile, cc.xyw(2, builder.getRow(), 6));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JTextArea textPropertyFileHint = UiUtilities.createHelpTextArea(mLocalizer.msg("propertyFileHint", "Hint"));
        textPropertyFileHint.setAlignmentY(0.5F);
        textPropertyFileHint.setAlignmentY(0.5F);
        textPropertyFileHint.setOpaque(true);
        textPropertyFileHint.setEnabled(true);
        builder.add(textPropertyFileHint, cc.xyw(2, builder.getRow(), 6));

        // Property file: editor
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        File file = new File(mSettings.getCustomChannelProperties());
        String content = "";
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(mSettings.getCustomChannelProperties()));
            content = convertToString(properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            content = "Error: could not find file " + mSettings.isRereadCustomChannelProperties();
            mEditor.setEnabled(false);
        } catch (IOException e) {
            mEditor.setEnabled(false);
        }
        mEditor = new JTextArea(content);
        mEditor.setBackground(Color.WHITE);
        mEditor.setOpaque(true);
        mDocument = mEditor.getDocument();
        mDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);

            }
        });
        builder.add(new JScrollPane(mEditor), cc.xyw(2, builder.getRow(), 6));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mEditor.setCaretPosition(0);
            }
        });

        // Save button
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        this.mSaveBtn = new JButton(mLocalizer.msg("saveBtn", "Save"));
        this.mSaveBtn.setEnabled(false);
        this.mSaveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String content = mEditor.getText();

                try {
                    Properties properties = convertToProperties(content);
                    properties.store(new FileWriter(mSettings.getCustomChannelProperties()), null);
                    mSaveBtn.setEnabled(false);
                } catch (Exception ex) {
                    JFrame frame = new JFrame("Error");
                    JOptionPane.showMessageDialog(frame,
                            ex.getMessage());
                }
            }
        });
        ButtonBarBuilder buttonBar = new ButtonBarBuilder();
        buttonBar.addButton(new JButton[]{this.mSaveBtn});
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), 5));

        // Help section
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JTextArea helpText = UiUtilities.createHelpTextArea(mLocalizer.msg("helpcustom", "help"));
        helpText.setAlignmentY(0.5F);
        helpText.setAlignmentY(0.5F);
        helpText.setOpaque(true);
        helpText.setEnabled(true);
        builder.add(helpText, cc.xyw(2, builder.getRow(), 5));
        return builder.getPanel();
    }

    public void saveSettings() {
        ZattooPlugin.getInstance().changeCountry(((ZattooCountry) this.mCountry.getSelectedItem()).getCode());
        ZattooPlugin.getInstance().changeRereadCustomChannelProperties(this.mReread.isSelected());
    }

    public Icon getIcon() {
        return ZattooPlugin.getInstance().getPluginIcon();
    }

    public String getTitle() {
        return mLocalizer.msg("title", "Zattoo");
    }

    private Properties convertToProperties(String content) throws PropertyFormatException {
        StringTokenizer lines = new StringTokenizer(content, "\n");
        Properties properties = new Properties();
        int lineNo = 0;
        while (lines.hasMoreElements()) {
            String line = lines.nextToken().trim();
            lineNo += 1;
            if (line.startsWith("#"))
                continue;
            if (line.length() == 0)
                continue;
            if (line.indexOf("=") != line.lastIndexOf("="))
                throw new PropertyFormatException("Format error line " + lineNo + ": line has more than one equal sign.");
            StringTokenizer keyValuePair = new StringTokenizer(line, "=");
            if (keyValuePair.countTokens() == 1)
                properties.put(keyValuePair.nextToken().trim(), "");
            else
                properties.put(keyValuePair.nextToken().trim(), keyValuePair.nextToken().trim());
        }
        return properties;
    }

    private String convertToString(Properties properties) {
        String content = "";
        Enumeration keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            content += key + "=" + properties.getProperty(key) + "\n";
        }
        return content;
    }
}
