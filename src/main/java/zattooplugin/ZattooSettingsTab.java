package zattooplugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import devplugin.Channel;
import devplugin.SettingsTab;
import tvbrowser.core.ChannelList;
import util.i18n.Localizer;
import util.misc.OperatingSystem;
import util.ui.UiUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

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
    private JButton mUpdateBtn;
    private JButton mVerifyBtn;


    private JRadioButton mUpdateByReplace;
    private JRadioButton mMergeAndReplace;
    private CustomChannelProperties mCustomChannelProperties;

    private JPanel mFilePanel;

    private JCheckBox mUseOnlySubscribedChannels;


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
        mFilePanel = createFilePanel();
        builderMain.add(mFilePanel, cc.xy(1, 5));
        return builderMain.getPanel();
    }

    public void saveSettings() {
        if (mSaveBtn.isEnabled()) {
            if (saveEditor(mEditor, mCustomChannelProperties)) {
                JFrame frame = new JFrame("Info");
                JOptionPane.showMessageDialog(frame,
                        mLocalizer.msg("saveOnExit", "saveOnExit"));
                mSaveBtn.setEnabled(false);
            }
        }
        String currentCountry = ((ZattooCountry) mCountry.getSelectedItem()).getCode();
        String sourceCountry = ((ZattooCountry) mSourceCountry.getSelectedItem()).getCode();

        if (mUpdateCustomChannels.isSelected()) {
            JFrame frame = new JFrame("Error");
            JOptionPane.showMessageDialog(frame,
                    mLocalizer.msg("noUpdateOnExit", "saveBeforeUpdate"));
            mUpdateCustomChannels.setEnabled(false);
            mUpdateBtn.setEnabled(false);
            return;
        }

        if (!mSettings.getCountry().equals(currentCountry))
            ZattooPlugin.getInstance().changeCountry(currentCountry);
        if (!mSettings.getSourceCountry().equals(sourceCountry))
            ZattooPlugin.getInstance().changeSourceCountry(sourceCountry);
        if (mSettings.getUpdateByReplace() != mUpdateByReplace.isSelected())
            ZattooPlugin.getInstance().changeUpdate(mUpdateByReplace.isSelected() ? ZattooSettings.UPDATE_BY_REPLACE : ZattooSettings.UPDATE_BY_MERGE);
        if (mSettings.getMergeAndReplace() != mMergeAndReplace.isSelected())
            ZattooPlugin.getInstance().changeMerge(mMergeAndReplace.isSelected() ? ZattooSettings.MERGE_AND_REPLACE : ZattooSettings.MERGE_ONLY_NEW);
        if (mSettings.getUseOnlySubscribedChannels() != mUseOnlySubscribedChannels.isSelected())
            ZattooPlugin.getInstance().changeUseOnlySubscribedChannels(mUseOnlySubscribedChannels.isSelected());
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
        JEditorPane textGeneralHint = UiUtilities.createHtmlHelpTextArea(mLocalizer.msg("countryHint", "countryHint"));
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
        mUpdateCustomChannels.addActionListener(e -> {
            if (mUpdateCustomChannels.isSelected())
                mUpdateBtn.setEnabled(true);
            else
                mUpdateBtn.setEnabled(false);
        });
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

        // useOnlySubscribedChannels
        mUseOnlySubscribedChannels = new JCheckBox(mLocalizer.msg("useOnlySubscribedChannels", "useOnlySubscribedChannels"));
        mUseOnlySubscribedChannels.setSelected(mSettings.getUseOnlySubscribedChannels());
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        builder.add(mUseOnlySubscribedChannels, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        //  button bar
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.setRow(builder.getRowCount());
        // Update button
        mUpdateBtn = new JButton(mLocalizer.msg("updateBtn", "Update"));
        mUpdateBtn.setEnabled(false);
        mUpdateBtn.addActionListener(e -> {
            if (mCustomChannelProperties != null) {
                if (mSaveBtn.isEnabled()) {
                    String content = mEditor.getText();
                    try {
                        mCustomChannelProperties.load(content);
                    } catch (PropertyException ex) {
                        JFrame frame = new JFrame("Error");
                        JOptionPane.showMessageDialog(frame,
                                ex.getMessage());
                        return;
                    }
                }
                updateCustomChannels(mUpdateByReplace.isSelected(),
                        mMergeAndReplace.isSelected(), mUseOnlySubscribedChannels.isSelected(), mCustomChannelProperties, ((ZattooCountry) mSourceCountry.getSelectedItem()).getCode());
                setEditor(mEditor, mCustomChannelProperties);
                mUpdateBtn.setEnabled(false);
                mUpdateCustomChannels.setSelected(false);
                if (mCustomChannelProperties.getProperties().size() > 0)
                    mVerifyBtn.setEnabled(true);
                else
                    mVerifyBtn.setEnabled(false);
            }
        });

        ButtonBarBuilder buttonBar = new ButtonBarBuilder();
        buttonBar.addButton(new JButton[]{mUpdateBtn});
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

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
            mCustomChannelProperties = new CustomChannelProperties();
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
                    if (mCustomChannelProperties.getProperties().size() > 0)
                        mVerifyBtn.setEnabled(true);
                    else
                        mVerifyBtn.setEnabled(false);
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
                if (mCustomChannelProperties.getProperties().size() > 0)
                    mVerifyBtn.setEnabled(true);
                else
                    mVerifyBtn.setEnabled(false);
            }
        });
        // Verify button
        mVerifyBtn = new JButton(mLocalizer.msg("verifyBtn", "Verify"));
        if (mCustomChannelProperties != null && mCustomChannelProperties.getProperties().size() > 0)
            mVerifyBtn.setEnabled(true);
        else
            mVerifyBtn.setEnabled(false);

        mVerifyBtn.addActionListener(e -> {
            verifyZattooNames(mEditor, ((ZattooCountry) mCountry.getSelectedItem()).getCode());
        });


        ButtonBarBuilder buttonBar = new ButtonBarBuilder();
        buttonBar.addButton(new JButton[]{mSaveBtn, mResetBtn, mVerifyBtn});
        builder.add(buttonBar.getPanel(), cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JLabel labelPropertyFile = new JLabel(CustomChannelProperties.PROPERTY_FILE_NAME);
        builder.add(labelPropertyFile, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));


        // Property file: editor
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        String content = "";

        mEditor = new JTextArea();
        mEditor.setRows(15);
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
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
            mEditor.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        }
        Document mDocument = mEditor.getDocument();

        mDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
                if (mEditor.getText().isEmpty())
                    mVerifyBtn.setEnabled(false);
                else
                    mVerifyBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
                if (mEditor.getText().isEmpty())
                    mVerifyBtn.setEnabled(false);
                else
                    mVerifyBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                mSaveBtn.setEnabled(true);
                mResetBtn.setEnabled(true);
                if (mEditor.getText().isEmpty())
                    mVerifyBtn.setEnabled(false);
                else
                    mVerifyBtn.setEnabled(true);
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

            jTextArea.setCaretPosition(0);
            DefaultCaret caret = (DefaultCaret) jTextArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
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
        jTextArea.setCaretPosition(0);

        return true;
    }

    /**
     * Update custom channels.
     *
     * @param replace         the replace
     * @param mergeAndReplace the merge and replace
     */
    public boolean updateCustomChannels(boolean replace, boolean mergeAndReplace, boolean useOnlySubscribedChannels,
                                        CustomChannelProperties customChannelProperties, String countryCode) {
        Channel[] channels;
        if (useOnlySubscribedChannels)
            channels = ChannelList.getSubscribedChannels();
        else
            channels = ChannelList.getAvailableChannels();

        ZattooChannelProperties zattooChannelProperties = new ZattooChannelProperties(countryCode);

        try {
            if (customChannelProperties == null)
                return false;
            if (replace) {
                customChannelProperties.clear(false);
            }
            if (replace || mergeAndReplace) {

                for (Channel channel : channels) {
                    String id = zattooChannelProperties.getProperty(channel);
                    if (!useOnlySubscribedChannels) {
                        if (id != null)
                            customChannelProperties.setChannel(channel, id, false);
                    } else {
                        String zattooChannel = (id == null) ? "" : id;
                        customChannelProperties.setChannel(channel, zattooChannel, false);
                    }
                }
            } else {
                for (Channel channel : channels) {
                    String id = zattooChannelProperties.getProperty(channel);
                    if (!useOnlySubscribedChannels) {
                        if (id != null) {
                            String value = customChannelProperties.getProperty(channel);
                            if (value == null || value.trim().isEmpty()) {
                                customChannelProperties.setChannel(channel, id, false);
                            }
                        }
                    } else {
                        String value = customChannelProperties.getProperty(channel);
                        if (value == null || value.trim().isEmpty()) {
                            String zattooChannel = (id == null) ? "" : id;
                            customChannelProperties.setChannel(channel, zattooChannel, false);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // will never happen
        }
        return true;
    }

    private void verifyZattooNames(JTextArea textArea, String countryCode) {
        try {
            if (countryCode.equals(CustomChannelProperties.COUNTRY_CODE))
                return;
            if (textArea.getText().isEmpty())
                return;
            if (!pingHost("zattoo.com", 443, 10000))
                return;

            textArea.getHighlighter().removeAllHighlights();
            StringTokenizer stringTokenizer = new StringTokenizer(textArea.getText(), System.lineSeparator());
            int lineNo = -1;
            String zattooChannels = "";
            // URL
            URLConnection connection = new URL("https://zattoo.com/" + countryCode + "/sender").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            zattooChannels = scanner.next();
            scanner.close();

//            WebClient webClient = new WebClient();
//            webClient.getOptions().setThrowExceptionOnScriptError(false);
//            HtmlPage myPage = null;
//            String msg="";
//            try {
//                myPage = webClient.getPage(new URL("https://zattoo.com/live/xxx"));
//            } catch (Exception e) {
//                msg = e.getMessage();
//            }
//
//            // convert page to generated HTML and convert to document
//            org.jsoup.nodes.Document doc;
//            doc = Jsoup.parse(myPage.asXml());
//            // File
////            InputStream inputStream = getClass().getResourceAsStream("zattoo_sender_" + countryCode + ".html");
////            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
////            StringBuilder stringBuilder = new StringBuilder();
////            int c = 0;
////            while ((c = bufferedReader.read()) != -1) {
////                stringBuilder.append((char) c);
////            }
////            zattooChannels = stringBuilder.toString();

            int firstLineNo = -1;
            while (stringTokenizer.hasMoreTokens()) {
                lineNo++;
                String line = stringTokenizer.nextToken();
                StringTokenizer keyValuePair = new StringTokenizer(line, "=");
                if (keyValuePair.countTokens() == 2) {
                    keyValuePair.nextToken();
                    String zattooChannel = keyValuePair.nextToken();
                    if (!zattooChannels.contains("\"/live/" + zattooChannel + "\"")) {
                        if (firstLineNo < 0) firstLineNo = lineNo;
                        int startIndex = textArea.getLineEndOffset(lineNo) - (zattooChannel.length() + 1);
                        int endIndex = textArea.getLineEndOffset(lineNo);
                        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                        textArea.getHighlighter().addHighlight(startIndex, endIndex, painter);
                    }
                }
            }
            if (firstLineNo >= 0) {
                textArea.setCaretPosition(textArea.getLineStartOffset(firstLineNo));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    public static boolean pingURL(String url, int timeout) {
        //url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }
}
