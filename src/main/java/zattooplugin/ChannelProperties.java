package zattooplugin;

import devplugin.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public abstract class ChannelProperties {
   private String mFileName;
   protected Properties mProperties;

   protected ChannelProperties() {
      this("channels.properties");
   }

   protected ChannelProperties(String fileName) {
      this.mFileName = fileName;
      if (!this.mFileName.toLowerCase().endsWith(".properties")) {
         this.mFileName = this.mFileName + ".properties";
      }

   }

   private void initializeProperties() {
      if (this.mProperties == null) {
         InputStream stream = this.getClass().getResourceAsStream(this.mFileName);
         this.mProperties = new Properties();

         try {
            this.mProperties.load(stream);
         } catch (IOException var3) {
            var3.printStackTrace();
         }

         this.checkProperties();
      }

   }

   protected abstract void checkProperties();

   public String getProperty(Channel channel) {
      try {
         this.initializeProperties();
         String channelCountry = channel.getBaseCountry();
         String channelName = channel.getDefaultName();
         Enumeration keys = this.mProperties.propertyNames();

         while(true) {
            String key;
            int separatorIndex;
            do {
               if (!keys.hasMoreElements()) {
                  return null;
               }

               key = (String)keys.nextElement();
               separatorIndex = key.indexOf(44);
            } while(separatorIndex <= 0);

            String[] countries = key.substring(0, separatorIndex).split("\\W");
            String name = "(?i)" + key.substring(separatorIndex + 1).replaceAll("\\*", ".*");
            String[] arr$ = countries;
            int len$ = countries.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               String country = arr$[i$];
               if (channelCountry.equalsIgnoreCase(country) && channelName.matches(name)) {
                  String property = this.mProperties.getProperty(key);
                  if (this.isValidProperty(property)) {
                     return property;
                  }

                  property = this.mProperties.getProperty(property);
                  if (this.isValidProperty(property)) {
                     return property;
                  }
               }
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
         return null;
      }
   }

   protected abstract boolean isValidProperty(String var1);
}
