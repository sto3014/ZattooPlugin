package zattooplugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import devplugin.SettingsTab;
import util.i18n.Localizer;
import util.misc.OperatingSystem;
import util.ui.UiUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since 1.0.0.0
 */
public final class ZattooSettingsTab implements SettingsTab {
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooSettingsTab.class);

    private JComboBox<ZattooCountry> mCountry;
    private JComboBox<ZattooCountry> mSourceCountry;

    private final ZattooSettings mSettings;
    private JCheckBox mUpdateCustomChannels;
    private JTextArea mEditor;
    private JButton mSaveBtn;
    private JButton mResetBtn;
    private JRadioButton mUpdateByReplace;
    private JRadioButton mMergeAndReplace;
    private CustomChannelProperties mCustomChannelProperties;


    /**
     * Instantiates a new Zattoo settings tab.
     *
     * @param settings the settings
     */
    public ZattooSettingsTab(ZattooSettings settings) {
        mSettings = settings;
    }

    /**
     * Help text to tooltip string.
     *
     * @param helpText      the help text
     * @param maxLineLength the max line length
     * @return the string
     */
    public static String helpTextToTooltip(String helpText, int maxLineLength) {
        helpText = helpText.trim();
        if (helpText.indexOf(' ') < 0 || helpText.indexOf(' ') > maxLineLength)
            return helpText;

        String tooltip = "";
        int foundLast = 0;
        int len = helpText.length();
        while (true) {
            int foundCurrent = helpText.lastIndexOf(' ', foundLast + maxLineLength);
            if (foundCurrent == -1 || foundCurrent == foundLast) {
                tooltip += helpText.substring(foundLast, foundLast + maxLineLength).trim() + "-<br>";
                foundLast = foundLast + maxLineLength;
            } else {
                tooltip += helpText.substring(foundLast, foundCurrent).trim() + "<br>";
                foundLast = foundCurrent;
                if (len - foundCurrent < maxLineLength) {
                    tooltip += helpText.substring(foundCurrent).trim();
                    return "<html>" + tooltip + "</html>";
                }
            }
        }
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
        if (mSaveBtn.isEnabled()) {
            if (saveEditor(mEditor, mCustomChannelProperties))
                mSaveBtn.setEnabled(false);
        }
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


    /**
     * Create country panel j panel.
     *
     * @return the j panel
     */
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
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        builder.add(new JLabel(mLocalizer.msg("country", "Country:")), cc.xy(2, builder.getRow()));
        builder.add(mCountry, cc.xy(4, builder.getRow()));

        return builder.getPanel();
    }

    /**
     * Create custom channel panel j panel.
     *
     * @return the j panel
     */
    private JPanel createCustomChannelPanel() {
        CellConstraints cc = new CellConstraints();
        PanelBuilder builder = new PanelBuilder(
                new FormLayout("5dlu,10dlu,5dlu,10dlu,5dlu,10dlu,5dlu,pref,0px:g"
                        , ""));

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
        JRadioButton mUpdateByMerge = new JRadioButton(mLocalizer.msg("updateByMerge", "updateByMerge"), mSettings.getUpdateByMerge());
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
        JRadioButton mMergeOnlyNew = new JRadioButton(mLocalizer.msg("mergeonlynew", "mergeonlynew"), mSettings.getMergeOnlyNew());
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

    /**
     * Create file panel j panel.
     *
     * @return the j panel
     */
    private JPanel createFilePanel() {
        CellConstraints cc = new CellConstraints();
        try {
            mCustomChannelProperties = new CustomChannelProperties(mSettings.getCustomChannelFileName());
        } catch (IOException e) {
            JFrame frame = new JFrame(mLocalizer.msg("error", "error"));
            JOptionPane.showMessageDialog(frame,
                    mLocalizer.msg("error.customFile", "error.customFile") + "\n" + e.getMessage());
            mCustomChannelProperties = null;
        }
        PanelBuilder builder = new PanelBuilder(
                new FormLayout("5dlu,pref,5dlu,pref,0px:g"
                        , ""));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        JTextArea textPropertyFileHint = UiUtilities.createHelpTextArea(mLocalizer.msg("propertyFileHint", "Hint"));
        builder.add(textPropertyFileHint, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        //  button bar
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        // Save button
        mSaveBtn = new JButton(mLocalizer.msg("saveBtn", "Save"));
        mSaveBtn.setEnabled(false);
        mSaveBtn.addActionListener(e -> {
            if (mCustomChannelProperties != null) {
                if (saveEditor(mEditor, mCustomChannelProperties)) {
                    mSaveBtn.setEnabled(false);
                    mResetBtn.setEnabled(false);
                }

            }
        });
        // Reset button
        mResetBtn = new JButton(mLocalizer.msg("resetBtn", "Reset"));
        mResetBtn.setEnabled(false);
        mResetBtn.addActionListener(e -> {
            if (mCustomChannelProperties != null) {
                mEditor.setText(mCustomChannelProperties.toString());
                mResetBtn.setEnabled(false);
                mSaveBtn.setEnabled(false);
            }
        });

        ButtonBarBuilder buttonBar = new ButtonBarBuilder();
        buttonBar.addButton(new JButton[]{mSaveBtn, mResetBtn});
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFile = new JLabel(mSettings.getCustomChannelFileName());
        builder.add(labelPropertyFile, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));


        // Property file: editor
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        String content = "";
        mEditor = new JTextArea();
        if (!setEditor(mEditor, mCustomChannelProperties)) {
            mEditor.setEnabled(false);
        }
        mEditor.setBackground(Color.WHITE);
        mEditor.setOpaque(true);
        if (OperatingSystem.isMacOs()) {
            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
            mEditor.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        }
        Document mDocument = mEditor.getDocument();

        mDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
            }
        });
        builder.add(new JScrollPane(mEditor), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mEditor.setCaretPosition(0);
            }
        });

        JPanel panel = builder.getPanel();
        Border border = new TitledBorder(mLocalizer.msg("propertyFile", "Configuration file"));
        panel.setBorder(border);
        return panel;
    }

    private boolean saveEditor(JTextArea jTextArea, CustomChannelProperties customChannelProperties) {
        if (jTextArea == null || customChannelProperties == null) return false;
        String content = jTextArea.getText();
        try {
            customChannelProperties.load(content);
            customChannelProperties.storeProperties();
            jTextArea.setText(customChannelProperties.toString());
            return true;
        } catch (Exception ex) {
            JFrame frame = new JFrame("Error");
            JOptionPane.showMessageDialog(frame,
                    ex.getMessage());
            return false;
        }
    }

    private boolean setEditor(JTextArea jTextArea, CustomChannelProperties customChannelProperties) {
        if (jTextArea == null || customChannelProperties == null) return false;
        String content = customChannelProperties.toString();
        jTextArea.setText(content);
        return true;
    }
}
