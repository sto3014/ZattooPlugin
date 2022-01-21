package zattooplugin;

import java.util.Properties;

public class ZattooSettings {
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_LEARN_MODE = "LEARN_MODE";
    // Until 1.0.2.0
    private static final String KEY_PLAYER = "PLAYER";

    private Properties mProperties;

    public ZattooSettings(Properties properties) {

        this.mProperties = properties;
        // Delete old properties
        if ( mProperties.getProperty(KEY_PLAYER) != null )
            mProperties.remove(KEY_PLAYER);
    }

    public Properties storeSettings() {
        return this.mProperties;
    }

    private int getProperty(String key, int defaultValue) {
        return Integer.valueOf(this.mProperties.getProperty(key, String.valueOf(defaultValue)));
    }

    public String getCountry() {
        return this.mProperties.getProperty(KEY_COUNTRY, "de");
    }
    public void setCountry(String country) {
        this.mProperties.setProperty(KEY_COUNTRY, country);
    }

    public boolean isLearnMode() {
        return this.mProperties.getProperty(KEY_LEARN_MODE, "false").equalsIgnoreCase("true");
    }
    public void setLearnMode(boolean learnMode) {
        this.mProperties.setProperty(KEY_LEARN_MODE, learnMode ? "true" : "false");
    }



}
