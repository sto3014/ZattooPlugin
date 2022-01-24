package zattooplugin;

import java.util.Properties;

public class ZattooSettings {
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_SOURCE_COUNTRY = "SOURCE_COUNTRY";
    private static final String KEY_CUSTOM_CHANNEL_PROPERTIES = "CUSTOM_CHANNEL_PROPERTIES";
    private static final String KEY_UPDATE = "UPDATE";
    public static final int UPDATE_BY_REPLACE = 0;
    public static final int UPDATE_BY_MERGE = 1;
    private static final String KEY_MERGE = "MERGE";
    public static final int MERGE_AND_REPLACE = 0;
    public static final int MERGE_ONLY_NEW = 1;


    // Until 1.0.2.0
    private static final String KEY_PLAYER = "PLAYER";

    // Until 1.0.3.0
    private static final String KEY_LEARN_MODE = "LEARN_MODE";

    private static final String KEY_ADD_NEW_CHANNELS = "ADD_NEW_CHANNELS";
    private static final String KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES = "REREAD_CUSTOM_CHANNEL_PROPERTIES";
    private static final String KEY_SHOW_HELP_AS_TOOLTIP = "SHOW_HELP_AS_TOOLTIP";

    private Properties mProperties;

    public ZattooSettings(Properties properties) {

        mProperties = properties;
        if (mProperties.getProperty(KEY_COUNTRY) == null)
            mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, "de");


        if (mProperties.getProperty(KEY_CUSTOM_CHANNEL_PROPERTIES) == null)
            mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, Helper.getPropertyPath() + "channels_custom.properties");

        if (mProperties.getProperty(KEY_UPDATE) == null)
            mProperties.setProperty(KEY_UPDATE, Integer.toString(UPDATE_BY_REPLACE));

        if (mProperties.getProperty(KEY_MERGE) == null)
            mProperties.setProperty(KEY_MERGE, Integer.toString(MERGE_AND_REPLACE));

        // Delete old properties
        if (mProperties.getProperty(KEY_PLAYER) != null)
            mProperties.remove(KEY_PLAYER);

        if (mProperties.getProperty(KEY_LEARN_MODE) != null)
            mProperties.remove(KEY_LEARN_MODE);

        if (mProperties.getProperty(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES) != null)
            mProperties.remove(KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES);

        if (mProperties.getProperty(KEY_ADD_NEW_CHANNELS) != null)
            mProperties.remove(KEY_ADD_NEW_CHANNELS);

        if (mProperties.getProperty(KEY_SHOW_HELP_AS_TOOLTIP) != null)
            mProperties.remove(KEY_SHOW_HELP_AS_TOOLTIP);

    }

    public Properties storeSettings() {
        return mProperties;
    }

    public String getCountry() {
        return mProperties.getProperty(KEY_COUNTRY, "de");
    }

    public void setCountry(String country) {
        mProperties.setProperty(KEY_COUNTRY, country);
    }

    public String getSourceCountry() {
        return mProperties.getProperty(KEY_SOURCE_COUNTRY, "de");
    }

    public void setSourceCountry(String sourceCountry) {
        mProperties.setProperty(KEY_SOURCE_COUNTRY, sourceCountry);
    }

    public void setCustomChannelProperties(String customChannelProperties) {
        mProperties.setProperty(KEY_CUSTOM_CHANNEL_PROPERTIES, customChannelProperties);
    }

    public String getCustomChannelProperties() {
        String properties = mProperties.getProperty(KEY_CUSTOM_CHANNEL_PROPERTIES);
        if (properties == null)
            properties = Helper.getPropertyPath() + "channels_custom.properties";
        return properties;
    }


    public void setUpdate(int update) {
        mProperties.setProperty(KEY_UPDATE, Integer.toString(update));
    }

    public void setMerge(int merge) {
        mProperties.setProperty(KEY_MERGE, Integer.toString(merge));
    }

    public int getUpdate() {
        return Integer.parseInt(mProperties.getProperty(KEY_UPDATE, Integer.toString(UPDATE_BY_REPLACE)));
    }

    public int getMerge() {
        return Integer.parseInt(mProperties.getProperty(KEY_MERGE, Integer.toString(MERGE_AND_REPLACE)));
    }

    public boolean getUpdateByReplace() {
        return getUpdate() == UPDATE_BY_REPLACE;
    }

    public boolean getUpdateByMerge() {
        return getUpdate() == UPDATE_BY_MERGE;
    }

    public boolean getMergeAndReplace() {
        return getMerge() == MERGE_AND_REPLACE;
    }

    public boolean getMergeOnlyNew() {
        return getMerge() == MERGE_ONLY_NEW;
    }

}
