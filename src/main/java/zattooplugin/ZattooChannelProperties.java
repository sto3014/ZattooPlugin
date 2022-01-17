package zattooplugin;

public class ZattooChannelProperties extends ChannelProperties {
   //private ZattooSettings mSettings;

   public ZattooChannelProperties(String fileName, ZattooSettings zattooSettings) {
      super(fileName);
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
         return true;
      }
   }
}
