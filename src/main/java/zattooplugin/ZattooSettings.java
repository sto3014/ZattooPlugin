package zattooplugin;

import java.util.Properties;

public class ZattooSettings {
   private static final String KEY_PLAYER = "PLAYER";
   private static final String KEY_COUNTRY = "COUNTRY";
   private static final int PLAYER_LOCAL = 0;
   private static final int PLAYER_WEB = 1;
   private static final int PLAYER_PRISM = 2;
   private Properties mProperties;

   public ZattooSettings(Properties properties) {
      this.mProperties = properties;
   }

   public Properties storeSettings() {
      return this.mProperties;
   }

   private int getPlayer() {
      return this.getProperty("PLAYER", ZattooPlugin.canUseLocalPlayer() ? 0 : 1);
   }

   private int getProperty(String key, int defaultValue) {
      return Integer.valueOf(this.mProperties.getProperty(key, String.valueOf(defaultValue)));
   }

   public boolean getUseWebPlayer() {
      return 1 == this.getPlayer();
   }

   public String getCountry() {
      return this.mProperties.getProperty("COUNTRY", "de");
   }

   public void setCountry(String country) {
      this.mProperties.setProperty("COUNTRY", country);
   }

   public void setUseWebPlayer() {
      this.setPlayer(1);
   }

   private void setPlayer(int player) {
      this.setProperty("PLAYER", player);
   }

   private void setProperty(String key, int value) {
      this.mProperties.setProperty(key, String.valueOf(value));
   }

   public void setUseLocalPlayer() {
      this.setPlayer(0);
   }

   public void setUsePrismPlayer() {
      this.setPlayer(2);
   }

   public boolean getUsePrismPlayer() {
      return this.getPlayer() == 2;
   }

   public boolean getUseLocalPlayer() {
      return this.getPlayer() == 0 && ZattooPlugin.canUseLocalPlayer();
   }
}
