package zattooplugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import devplugin.SettingsTab;
import util.ui.Localizer;
import util.ui.UiUtilities;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;

public final class ZattooSettingsTab implements SettingsTab {
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooSettingsTab.class);
    private JComboBox mCountry;
    private JComboBox mSourceCountry;

    private ZattooSettings mSettings;
    private JCheckBox mUpdateCustomChannels;
    private JCheckBox mAddNewChannels;
    private JCheckBox mShowHelpAsTooltip;
    private JCheckBox mReread;
    private JTextArea mEditor;
    private JButton mSaveBtn;
    private Document mDocument;
    private JRadioButton mUpdateByReplace;
    private JRadioButton mUpdateByMerge;
    private JRadioButton mMergeAndReplace;
    private JRadioButton mMergeOnlyNew;

    public ZattooSettingsTab(ZattooSettings settings) {
        mSettings = settings;
    }

    public JPanel createSettingsPanel() {

        CellConstraints cc = new CellConstraints();
        PanelBuilder builderMain = new PanelBuilder(
                new FormLayout("0px:g"
                        , "pref,10dlu,pref,10dlu,pref"), new JPanel());
        builderMain.add(createCountryPanel(), cc.xy(1, 1));
        builderMain.add(createCustomChannelPanel(), cc.xy(1, 3));
        builderMain.add(createFilePanel(), cc.xy(1, 5));
        return builderMain.getPanel();
    }

    public void saveSettings() {
        String currentCountry = ((ZattooCountry) mCountry.getSelectedItem()).getCode();
        String sourceCountry = ((ZattooCountry) mSourceCountry.getSelectedItem()).getCode();
        if (mUpdateCustomChannels.isSelected()) {
            ZattooPlugin.getInstance().changeCountry(sourceCountry);
            ZattooPlugin.getInstance().updateCustomChannels(mUpdateByReplace.isSelected(), mMergeAndReplace.isSelected());
        }
        ZattooPlugin.getInstance().changeCountry(currentCountry);
        ZattooPlugin.getInstance().changeSourceCountry(sourceCountry);
        ZattooPlugin.getInstance().changeUpdate(mUpdateByReplace.isSelected() ? ZattooSettings.UPDATE_BY_REPLACE : ZattooSettings.UPDATE_BY_MERGE);
        ZattooPlugin.getInstance().changeMerge(mMergeAndReplace.isSelected() ? ZattooSettings.MERGE_AND_REPLACE : ZattooSettings.MERGE_ONLY_NEW);
    }

    public Icon getIcon() {
        return ZattooPlugin.getInstance().getPluginIcon();
    }

    public String getTitle() {
        return mLocalizer.msg("title", "Zattoo");
    }

    private Properties convertToProperties(String content) throws PropertyFormatException {
        StringTokenizer lines = new StringTokenizer(content, System.lineSeparator());
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
            content += key + "=" + properties.getProperty(key) + System.lineSeparator();
        }
        return content;
    }

    private JPanel createCountryPanel() {
        CellConstraints cc = new CellConstraints();

        PanelBuilder builder = new PanelBuilder(
                new FormLayout("5dlu,pref,5dlu,pref,0px:g"
                        , ""));

        // Hints
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        JTextArea textGeneralHint = UiUtilities.createHelpTextArea(mLocalizer.msg("countryHint", "countryHint"));
        builder.add(textGeneralHint, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        ZattooCountry[] countries = new ZattooCountry[]{
                new ZattooCountry("de", mLocalizer.msg("country_de", "Germany")),
                new ZattooCountry("ch", mLocalizer.msg("country_ch", "Switzerland")),
                new ZattooCountry("at", mLocalizer.msg("country_at", "Austria")),
                new ZattooCountry("custom", mLocalizer.msg("country_custom", "Customized list of channels")),
        };


        mCountry = new JComboBox(countries);
        mCountry.setSelectedItem(new ZattooCountry(mSettings.getCountry(), ""));
//        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        builder.add(new JLabel(mLocalizer.msg("country", "Country:")), cc.xy(2, builder.getRow()));
        builder.add(mCountry, cc.xy(4, builder.getRow()));

//        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
//        builder.appendRow(FormSpecs.PREF_ROWSPEC);
//        builder.nextRow(2);
//        mShowHelpAsTooltip = new JCheckBox(
//                mLocalizer.msg("showHelpAsTooltip", "showHelpAsTooltip"),
//                mSettings.getShowHelpAsTooltip());
//        builder.add(mShowHelpAsTooltip, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));


        return builder.getPanel();
    }

    private JPanel createCustomChannelPanel() {
        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(
                new FormLayout("5dlu,10dlu,5dlu,10dlu,5dlu,10dlu,5dlu,pref,0px:g"
                        , ""));
        // Separator
//        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
//        builder.appendRow(FormSpecs.PREF_ROWSPEC);
//        builder.nextRow();
//        builder.addSeparator(mLocalizer.msg("customchannels", "Customized list of channels"));

        // Hints
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        JTextArea textGeneralHint = UiUtilities.createHelpTextArea(mLocalizer.msg("customHint", "customHint"));
        builder.add(textGeneralHint, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        // Update custom channels
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        mUpdateCustomChannels = new JCheckBox(mLocalizer.msg("updateCustomChannels", "updateCustomChannels"), false);
        builder.add(mUpdateCustomChannels, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        // Update by replace
        //builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mUpdateByReplace = new JRadioButton(mLocalizer.msg("updateByReplace", "updateByReplace"), mSettings.getUpdateByReplace());
        builder.add(mUpdateByReplace, cc.xyw(4, builder.getRow(), builder.getColumnCount() - 3));
        // Update by merge
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mUpdateByMerge = new JRadioButton(mLocalizer.msg("updateByMerge", "updateByMerge"), mSettings.getUpdateByMerge());
        builder.add(mUpdateByMerge, cc.xyw(4, builder.getRow(), builder.getColumnCount() - 3));
        // Group
        ButtonGroup buttonGroupUpdate = new ButtonGroup();
        buttonGroupUpdate.add(mUpdateByReplace);
        buttonGroupUpdate.add(mUpdateByMerge);

        // Merge and replace
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mMergeAndReplace = new JRadioButton(mLocalizer.msg("mergeandreplace", "mergeandreplace"), mSettings.getMergeAndReplace());
        builder.add(mMergeAndReplace, cc.xyw(6, builder.getRow(), builder.getColumnCount() - 5));
        // Merge only new
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(1);
        mMergeOnlyNew = new JRadioButton(mLocalizer.msg("mergeonlynew", "mergeonlynew"), mSettings.getMergeOnlyNew());
        builder.add(mMergeOnlyNew, cc.xyw(6, builder.getRow(), builder.getColumnCount() - 5));
        // Group
        ButtonGroup buttonGroupMerge = new ButtonGroup();
        buttonGroupMerge.add(mMergeAndReplace);
        buttonGroupMerge.add(mMergeOnlyNew);

        // From country list
        ZattooCountry[] countries = new ZattooCountry[]{
                new ZattooCountry("de", mLocalizer.msg("country_de", "Germany")),
                new ZattooCountry("ch", mLocalizer.msg("country_ch", "Switzerland")),
                new ZattooCountry("at", mLocalizer.msg("country_at", "Austria")),
        };
        mSourceCountry = new JComboBox(countries);
        mSourceCountry.setSelectedItem(new ZattooCountry(mSettings.getSourceCountry(), ""));
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        builder.add(new JLabel(mLocalizer.msg("sourcecountry", "Source Country:")), cc.xyw(4, builder.getRow(), 3));
        builder.add(mSourceCountry, cc.xy(8, builder.getRow()));
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        JPanel panel = builder.getPanel();
        Border border = new TitledBorder(mLocalizer.msg("createChannelsList", "createChannelsList"));
        panel.setBorder(border);
        return panel;
    }

    private JPanel createFilePanel() {
        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(
                new FormLayout("5dlu,10dlu,0px:g"
                        , ""));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        JTextArea textPropertyFileHint = UiUtilities.createHelpTextArea(mLocalizer.msg("propertyFileHint", "Hint"));
        builder.add(textPropertyFileHint, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        JLabel labelPropertyFile = new JLabel(mSettings.getCustomChannelProperties() + ":");
        builder.add(labelPropertyFile, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));


        // Property file: editor
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        String content = "";
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(mSettings.getCustomChannelProperties()));
            content = convertToString(properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            content = "Error: could not find file " + mSettings.getCustomChannelProperties();
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
        mSaveBtn = new JButton(mLocalizer.msg("saveBtn", "Save"));
        mSaveBtn.setEnabled(false);
        mSaveBtn.addActionListener(new ActionListener() {
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
        buttonBar.addButton(new JButton[]{mSaveBtn});
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
        JPanel panel = builder.getPanel();
        Border border = new TitledBorder(mLocalizer.msg("propertyFile", "Configuration file"));
        panel.setBorder(border);
        return panel;
    }
}
