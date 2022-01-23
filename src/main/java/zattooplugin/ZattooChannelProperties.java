package zattooplugin;

import java.io.File;

public class ZattooChannelProperties extends ChannelProperties {
   public ZattooChannelProperties(String country, String customChannelProperties, boolean rereadCustomChannelProperties ) {
      super(country, customChannelProperties, rereadCustomChannelProperties);
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
