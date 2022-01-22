package zattooplugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import devplugin.Channel;
import devplugin.SettingsTab;
import tvbrowser.core.ChannelList;
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
    private JCheckBox mUpdateByReplace;
    private JCheckBox mUpdateByMerge;
    private JCheckBox mMergeAndReplace;
    private JCheckBox mMergeOnlyNew;

    public ZattooSettingsTab(ZattooSettings settings) {
        this.mSettings = settings;
    }

    public JPanel createSettingsPanel() {
        PanelBuilder builder = new PanelBuilder(new FormLayout(
                FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
                        FormSpecs.PREF_COLSPEC.encode() + "," +
                        FormSpecs.RELATED_GAP_COLSPEC.encode() + "," +
                        FormSpecs.PREF_COLSPEC.encode() + "," +
                        FormSpecs.GLUE_COLSPEC.encode(), ""));
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
        builder.add(this.mCountry, cc.xy(4, builder.getRow()));

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
        builder.add(mUpdateCustomChannels, cc.xy(2, builder.getRow()));

        // Update by replace
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        mUpdateByReplace = new JRadioButton(mLocalizer.msg("updateCustomChannels", "updateCustomChannels"));
        builder.add(mUpdateByReplace, cc.xy(2, builder.getRow()));

        // Update by merge

        // Merge and replace
        // Merge only new

        // Reread list
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        mReread = new JCheckBox(mLocalizer.msg("reread", "Reread customized list of channels"), this.mSettings.isRereadCustomChannelProperties());
        builder.add(mReread, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        // Property file: title
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFileHeader = new JLabel(mLocalizer.msg("propertyFile", "Configuration file") + ":");
        builder.add(labelPropertyFileHeader, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFile = new JLabel( "     " + mSettings.getCustomChannelProperties());
        builder.add(labelPropertyFile, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JTextArea textPropertyFileHint = UiUtilities.createHelpTextArea(mLocalizer.msg("propertyFileHint", "Hint"));
        textPropertyFileHint.setAlignmentY(0.5F);
        textPropertyFileHint.setAlignmentY(0.5F);
        textPropertyFileHint.setOpaque(true);
        textPropertyFileHint.setEnabled(true);
        builder.add(textPropertyFileHint, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

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
        builder.add(new JScrollPane(mEditor), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
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
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        // Help section
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JTextArea helpText = UiUtilities.createHelpTextArea(mLocalizer.msg("helpcustom", "help"));
        helpText.setAlignmentY(0.5F);
        helpText.setAlignmentY(0.5F);
        helpText.setOpaque(true);
        helpText.setEnabled(true);
        builder.add(helpText, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
        return builder.getPanel();
    }

    public void saveSettings() {
        ZattooPlugin.getInstance().changeCountry(((ZattooCountry) this.mCountry.getSelectedItem()).getCode());
        ZattooPlugin.getInstance().changeRereadCustomChannelProperties(this.mReread.isSelected());

        if (((ZattooCountry) this.mCountry.getSelectedItem()).getCode().contains("custom"))
            ZattooPlugin.getInstance().changeLearnMode(false);
        else {
            ZattooPlugin.getInstance().changeLearnMode(this.mUpdateCustomChannels.isSelected());
            if (this.mUpdateCustomChannels.isSelected()) {
                Channel[] subscribedChannels = ChannelList.getSubscribedChannels();
                ZattooPlugin.getInstance().setCustomChannels(subscribedChannels);
                ZattooPlugin.getInstance().changeLearnMode(false);
            }
        }
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
