package zattooplugin;

import java.io.File;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since  1.0.0.0
 */
public class ZattooChannelProperties extends ChannelProperties {
   /**
    * Instantiates a new Zattoo channel properties.
    *
    * @param country                 the country
    */
   public ZattooChannelProperties(String country ) {
      super(country);
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
