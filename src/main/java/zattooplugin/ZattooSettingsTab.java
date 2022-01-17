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
      return builder.getPanel();
   }

   public void saveSettings() {
      ZattooPlugin.getInstance().changeCountry(((ZattooCountry)this.mCountry.getSelectedItem()).getCode());

   }

   public Icon getIcon() {
      return ZattooPlugin.getInstance().getPluginIcon();
   }

   public String getTitle() {
      return mLocalizer.msg("title", "Zattoo");
   }
}
