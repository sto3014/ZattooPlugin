package zattooplugin;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import devplugin.SettingsTab;
import util.ui.Localizer;
import util.ui.UiUtilities;

import javax.swing.*;
import java.awt.*;

public final class ZattooSettingsTab implements SettingsTab {
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooSettingsTab.class);
    private JComboBox mCountry;
    private ZattooSettings mSettings;
    private JCheckBox mLearnMode;

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

        mLearnMode = new JCheckBox(mLocalizer.msg("learnMode", "Learn mode"), this.mSettings.isLearnMode());
        if (this.mSettings.getCountry().contains("custom")) mLearnMode.setEnabled(false);
        builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        builder.addSeparator(mLocalizer.msg("customchannels", "Customized list of channels"));
        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        builder.add(mLearnMode, cc.xy(2, builder.getRow()));

        builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
        builder.appendRow(FormSpecs.PREF_ROWSPEC);
        builder.nextRow(2);
        JTextArea editor = UiUtilities.createHelpTextArea(mLocalizer.msg("helpcustom","help"));
        editor.setOpaque(true);
        editor.setEnabled(true);
        //builder.add(new JScrollPane(editor), cc.xy(2,builder.getRow()));
        builder.add(editor, cc.xy(3,builder.getRow()));


        //builder.add(new JLabel(mLocalizer.msg("helpcustom","help")), cc.xy(2, builder.getRow()));


      /*
      this.mRbLocalPlayer = new JRadioButton(mLocalizer.msg("localPlayer", "Use local player"), this.mSettings.getUseLocalPlayer());
      this.mRbLocalPlayer.setEnabled(ZattooPlugin.canUseLocalPlayer());
      this.mRbWebPlayer = new JRadioButton(mLocalizer.msg("webPlayer", "Use web player"), this.mSettings.getUseWebPlayer());
      this.mRbPrism = new JRadioButton(mLocalizer.msg("prism", "Mozilla Prism"), this.mSettings.getUsePrismPlayer());
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(this.mRbLocalPlayer);
      buttonGroup.add(this.mRbWebPlayer);
      buttonGroup.add(this.mRbPrism);
      builder.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
      builder.appendRow(FormSpecs.PREF_ROWSPEC);
      builder.nextRow(2);
      builder.addSeparator(mLocalizer.msg("player", "Player"));
      builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
      builder.appendRow(FormSpecs.PREF_ROWSPEC);
      builder.nextRow(2);
      builder.add(this.mRbLocalPlayer, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
      builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
      builder.appendRow(FormSpecs.PREF_ROWSPEC);
      builder.nextRow(2);
      builder.add(this.mRbWebPlayer, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
      builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
      builder.appendRow(FormSpecs.PREF_ROWSPEC);
      builder.nextRow(2);
      builder.add(this.mRbPrism, cc.xyw(2, builder.getRow(), builder.getColumnCount() - 1));
      */
        return builder.getPanel();
    }

    public void saveSettings() {
        ZattooPlugin.getInstance().changeCountry(((ZattooCountry) this.mCountry.getSelectedItem()).getCode());
        ZattooPlugin.getInstance().changeLearnMode(this.mLearnMode.isSelected());
    }

    public Icon getIcon() {
        return ZattooPlugin.getInstance().getPluginIcon();
    }

    public String getTitle() {
        return mLocalizer.msg("title", "Zattoo");
    }
}
