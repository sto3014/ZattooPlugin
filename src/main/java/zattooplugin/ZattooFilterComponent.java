package zattooplugin;

import devplugin.PluginsFilterComponent;
import devplugin.Program;
import util.ui.Localizer;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ZattooFilterComponent extends PluginsFilterComponent {
   private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooFilterComponent.class);

   public ZattooFilterComponent() {
   }

   public String getUserPresentableClassName() {
      return mLocalizer.msg("name", "Zattoo channels");
   }

   public boolean accept(Program program) {
      return ZattooPlugin.getInstance().isChannelSupported(program.getChannel());
   }

   public int getVersion() {
      return 0;
   }

   public void read(ObjectInputStream arg0, int arg1) throws IOException, ClassNotFoundException {
   }

   public void write(ObjectOutputStream arg0) throws IOException {
   }

   public JPanel getSettingsPanel() {
      JPanel descPanel = new JPanel();
      descPanel.add(new JLabel(mLocalizer.msg("desc", "Accepts all programs of channels which are supported by Zattoo.")));
      return descPanel;
   }
}
