package zattooplugin;

import java.util.Properties;

public class ZattooSettings {
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_LEARN_MODE = "LEARN_MODE";
    private static final String KEY_CUSTOM_CHANNEL_PROPERTIES = "CUSTOM_CHANNEL_PROPERTIES";
    private static final String KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES = "REREAD_CUSTOM_CHANNEL_PROPERTIES";
    private static final String KEY_UPDATE ="UPDATE";
    private static final int UPDATE_BY_REPLACE = 0;
    private static final int UPDATE_BY_MERGE = 1;
    private static final String KEY_MERGE ="MERGE";
    private static final int MERGE_AND_REPLACE = 0;
    private static final int MERGE_ONLY_NEW = 1;

    // Until 1.0.2.0
    private static final String KEY_PLAYER = "PLAYER";

    private Properties mProperties;

    public ZattooSettings(Properties properties) {

        this.mProperties = properties;
        if ( mProperties.getProperty(KEY_COUNTRY) == null)
            mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, "de");

        if ( mProperties.getProperty(KEY_LEARN_MODE) == null)
            mProperties.setProperty(KEY_LEARN_MODE, "false");

        if ( mProperties.getProperty(KEY_CUSTOM_CHANNEL_PROPERTIES) == null)
            mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, Helper.getPropertyPath() + "channels_custom.properties");

        if (mProperties.getProperty(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES) ==null)
            mProperties.setProperty(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES, "false");

        // Delete old properties
        if ( mProperties.getProperty(KEY_PLAYER) != null )
            mProperties.remove(KEY_PLAYER);
    }

    public Properties storeSettings() {
        return this.mProperties;
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

    public void setCustomChannelProperties(String customChannelProperties) {
        this.mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, customChannelProperties );
    }
    public String getCustomChannelProperties() {
        String properties = this.mProperties.getProperty(KEY_CUSTOM_CHANNEL_PROPERTIES );
        if (properties == null)
            properties = Helper.getPropertyPath() + "channels_custom.properties";
        return properties;
    }

    public boolean isRereadCustomChannelProperties() {
        return this.mProperties.getProperty(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES, "false").equalsIgnoreCase("true");
    }

    public void setRereadCustomChannelProperties(boolean reread) {
        this.mProperties.setProperty(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES, reread ? "true" : "false");
    }

    public void setUpdate( int update){
        this.mProperties.setProperty(KEY_UPDATE, Integer.toString(update));
    }

    public void setMerge( int merge){
        this.mProperties.setProperty(KEY_MERGE, Integer.toString(merge));
    }

    public int getUpdate(){
        return Integer.parseInt(this.mProperties.getProperty(KEY_UPDATE, Integer.toString(UPDATE_BY_REPLACE)));
    }

    public int getMerge(){
        return Integer.parseInt(this.mProperties.getProperty(KEY_MERGE, Integer.toString(MERGE_AND_REPLACE)));
    }

    public boolean getUpdateByReplace(){
        return getUpdate() == UPDATE_BY_REPLACE;
    }
    public boolean getUpdateByMerge(){
        return getUpdate() == UPDATE_BY_MERGE;
    }

    public boolean getMergeAndReplace(){
        return getMerge() == MERGE_AND_REPLACE;
    }
    public boolean getMergeOnlyNew(){
        return getMerge() == MERGE_ONLY_NEW;
    }

}
