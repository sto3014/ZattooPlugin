package zattooplugin;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import devplugin.SettingsTab;
import util.ui.Localizer;

import javax.swing.*;

public final class ZattooSettingsTab implements SettingsTab {
   private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooSettingsTab.class);
   private JComboBox mCountry;
   private ZattooSettings mSettings;
   private JRadioButton mRbLocalPlayer;
   private JRadioButton mRbWebPlayer;
   private JRadioButton mRbPrism;

   public ZattooSettingsTab(ZattooSettings settings) {
      this.mSettings = settings;
   }

   public JPanel createSettingsPanel() {
      PanelBuilder builder = new PanelBuilder(new FormLayout(FormSpecs.RELATED_GAP_COLSPEC.encode() + "," + FormSpecs.PREF_COLSPEC.encode() + "," + FormSpecs.RELATED_GAP_COLSPEC.encode() + "," + FormSpecs.PREF_COLSPEC.encode() + "," + FormSpecs.GLUE_COLSPEC.encode(), ""));
      CellConstraints cc = new CellConstraints();
      ZattooCountry[] countries = new ZattooCountry[]{
              new ZattooCountry("de", mLocalizer.msg("country_de", "Germany")),
              new ZattooCountry("ch", mLocalizer.msg("country_ch", "Switzerland")),
              new ZattooCountry("at", mLocalizer.msg("country_at", "Austria")),
      };
      this.mCountry = new JComboBox(countries);
      this.mCountry.setSelectedItem(new ZattooCountry(this.mSettings.getCountry(), ""));
      builder.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
      builder.appendRow(FormSpecs.PREF_ROWSPEC);
      builder.nextRow();
      builder.add(new JLabel(mLocalizer.msg("country", "Country:")), cc.xy(2, builder.getRow()));
      builder.add(this.mCountry, cc.xy(4, builder.getRow()));
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
      return builder.getPanel();
   }

   public void saveSettings() {
      ZattooPlugin.getInstance().changeCountry(((ZattooCountry)this.mCountry.getSelectedItem()).getCode());
      if (this.mRbLocalPlayer.isSelected() && ZattooPlugin.canUseLocalPlayer()) {
         this.mSettings.setUseLocalPlayer();
      } else if (this.mRbPrism.isSelected()) {
         this.mSettings.setUsePrismPlayer();
      } else {
         this.mSettings.setUseWebPlayer();
      }

   }

   public Icon getIcon() {
      return ZattooPlugin.getInstance().getPluginIcon();
   }

   public String getTitle() {
      return mLocalizer.msg("title", "Zattoo");
   }
}
