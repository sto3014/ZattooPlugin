package zattooplugin;

import java.util.Properties;

public class ZattooSettings {
    //   private static final String KEY_PLAYER = "PLAYER";
    private static final String KEY_COUNTRY = "COUNTRY";
    //   private static final int PLAYER_LOCAL = 0;
//   private static final int PLAYER_WEB = 1;
//   private static final int PLAYER_PRISM = 2;
    private Properties mProperties;

    public ZattooSettings(Properties properties) {
        this.mProperties = properties;
    }

    public Properties storeSettings() {
        return this.mProperties;
    }

    private int getProperty(String key, int defaultValue) {
        return Integer.valueOf(this.mProperties.getProperty(key, String.valueOf(defaultValue)));
    }

    public String getCountry() {
        return this.mProperties.getProperty("COUNTRY", "de");
    }

    public void setCountry(String country) {
        this.mProperties.setProperty("COUNTRY", country);
    }


}
