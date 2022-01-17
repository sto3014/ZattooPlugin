package zattooplugin;

public class ZattooChannelProperties extends ChannelProperties {
   private ZattooSettings mSettings;

   public ZattooChannelProperties(String fileName, ZattooSettings zattooSettings) {
      super(fileName);
      this.mSettings = zattooSettings;
   }

   protected void checkProperties() {
   }

   protected boolean isValidProperty(String id) {
      if (id == null) {
         return false;
      } else if (id.length() == 0) {
         return false;
      } else if (id.startsWith("=")) {
         return false;
      } else {
         int comma = id.indexOf(44);
         if (!this.mSettings.getUseLocalPlayer() && comma >= 0) {
            return id.substring(comma + 1).trim().length() > 0;
         } else {
            return this.mSettings.getUseLocalPlayer() || comma != -1;
         }
      }
   }
}
