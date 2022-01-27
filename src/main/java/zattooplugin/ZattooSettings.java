package zattooplugin;

import java.util.Properties;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since  1.0.0.0
 */
public class ZattooSettings {
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_SOURCE_COUNTRY = "SOURCE_COUNTRY";
    private static final String KEY_CUSTOM_CHANNEL_FILE_NAME = "CUSTOM_CHANNEL_FILE_NAME";
    private static final String KEY_UPDATE = "UPDATE";
    public static final int UPDATE_BY_REPLACE = 0;
    public static final int UPDATE_BY_MERGE = 1;
    private static final String KEY_MERGE = "MERGE";
    public static final int MERGE_AND_REPLACE = 0;
    public static final int MERGE_ONLY_NEW = 1;

    private final Properties mProperties;

    /**
     * Instantiates a new Zattoo settings.
     *
     * @param properties the properties
     */
    public ZattooSettings(Properties properties) {
        mProperties = properties;

        // for testing purpose only
        boolean reset = false;

        if (mProperties.getProperty(KEY_COUNTRY) == null || reset)
            mProperties.setProperty(KEY_COUNTRY, "de");

        if (mProperties.getProperty(KEY_SOURCE_COUNTRY) == null || reset)
            mProperties.setProperty(KEY_SOURCE_COUNTRY, "de");


        if (mProperties.getProperty(KEY_CUSTOM_CHANNEL_FILE_NAME) == null || reset)
            mProperties.setProperty(KEY_CUSTOM_CHANNEL_FILE_NAME, CustomChannelProperties.getPropertyPath()
                    + "channels_custom.properties" );

        if (mProperties.getProperty(KEY_UPDATE) == null || reset)
            mProperties.setProperty(KEY_UPDATE, Integer.toString(UPDATE_BY_REPLACE));

        if (mProperties.getProperty(KEY_MERGE) == null || reset)
            mProperties.setProperty(KEY_MERGE, Integer.toString(MERGE_AND_REPLACE));

        deleteOldProperties();
    }

    private void deleteOldProperties(){
        // since 1.0.3.0
        final String KEY_PLAYER = "PLAYER";

        // since 1.5.0.0
        final String KEY_LEARN_MODE = "LEARN_MODE";
        final String KEY_ADD_NEW_CHANNELS = "ADD_NEW_CHANNELS";
        final String KEY_REREAD_CUSTOM_CHANNEL_PROPERTIES = "REREAD_CUSTOM_CHANNEL_PROPERTIES";
        final String KEY_SHOW_HELP_AS_TOOLTIP = "SHOW_HELP_AS_TOOLTIP";
        final String KEY_CUSTOM_CHANNEL_PROPERTIES= "CUSTOM_CHANNEL_PROPERTIES";

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

        if (mProperties.getProperty(KEY_CUSTOM_CHANNEL_PROPERTIES) != null)
            mProperties.remove(KEY_CUSTOM_CHANNEL_PROPERTIES);

    }

    /**
     * Store settings properties.
     *
     * @return the properties
     */
    public Properties storeSettings() {
        return mProperties;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return mProperties.getProperty(KEY_COUNTRY);
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        mProperties.setProperty(KEY_COUNTRY, country);
    }

    /**
     * Gets source country.
     *
     * @return the source country
     */
    public String getSourceCountry() {
        return mProperties.getProperty(KEY_SOURCE_COUNTRY);
    }

    /**
     * Sets source country.
     *
     * @param sourceCountry the source country
     */
    public void setSourceCountry(String sourceCountry) {
        mProperties.setProperty(KEY_SOURCE_COUNTRY, sourceCountry);
    }

    /**
     * Gets custom channel properties.
     *
     * @return the custom channel properties
     */
    public String getCustomChannelFileName() {
        return mProperties.getProperty(KEY_CUSTOM_CHANNEL_FILE_NAME);
    }


    /**
     * Sets update.
     *
     * @param update the update
     */
    public void setUpdate(int update) {
        mProperties.setProperty(KEY_UPDATE, Integer.toString(update));
    }

    /**
     * Sets merge.
     *
     * @param merge the merge
     */
    public void setMerge(int merge) {
        mProperties.setProperty(KEY_MERGE, Integer.toString(merge));
    }

    /**
     * Gets update.
     *
     * @return the update
     */
    public int getUpdate() {
        return Integer.parseInt(mProperties.getProperty(KEY_UPDATE));
    }

    /**
     * Gets merge.
     *
     * @return the merge
     */
    public int getMerge() {
        return Integer.parseInt(mProperties.getProperty(KEY_MERGE));
    }

    /**
     * Gets update by replace.
     *
     * @return the update by replace
     */
    public boolean getUpdateByReplace() {
        return getUpdate() == UPDATE_BY_REPLACE;
    }

    /**
     * Gets update by merge.
     *
     * @return the update by merge
     */
    public boolean getUpdateByMerge() {
        return getUpdate() == UPDATE_BY_MERGE;
    }

    /**
     * Gets merge and replace.
     *
     * @return the merge and replace
     */
    public boolean getMergeAndReplace() {
        return getMerge() == MERGE_AND_REPLACE;
    }

    /**
     * Gets merge only new.
     *
     * @return the merge only new
     */
    public boolean getMergeOnlyNew() {
        return getMerge() == MERGE_ONLY_NEW;
    }

}
