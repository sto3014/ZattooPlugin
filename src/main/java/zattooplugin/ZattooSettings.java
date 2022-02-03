package zattooplugin;

import java.util.Properties;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since 1.0.0.0
 */
public class ZattooSettings {
    private static final String KEY_COUNTRY = "COUNTRY";
    private static final String KEY_SOURCE_COUNTRY = "SOURCE_COUNTRY";
    private static final String KEY_UPDATE = "UPDATE";
    public static final int UPDATE_BY_REPLACE = 0;
    public static final int UPDATE_BY_MERGE = 1;
    private static final String KEY_MERGE = "MERGE";
    public static final int MERGE_AND_REPLACE = 0;
    public static final int MERGE_ONLY_NEW = 1;
    public static final String KEY_USE_ONLY_SUBSCRIBED_CHANNELS="USE_ONLY_SUBSCRIBED_CHANNELS";

    private final Properties mProperties;

    /**
     * Instantiates a new Zattoo settings.
     *
     * @param properties the properties
     */
    public ZattooSettings(Properties properties) {
        mProperties = properties;

        if (mProperties.getProperty(KEY_COUNTRY) == null)
            mProperties.setProperty(KEY_COUNTRY, "de");

        if (mProperties.getProperty(KEY_SOURCE_COUNTRY) == null)
            mProperties.setProperty(KEY_SOURCE_COUNTRY, "de");

        if (mProperties.getProperty(KEY_UPDATE) == null)
            mProperties.setProperty(KEY_UPDATE, Integer.toString(UPDATE_BY_MERGE));

        if (mProperties.getProperty(KEY_MERGE) == null)
            mProperties.setProperty(KEY_MERGE, Integer.toString(MERGE_ONLY_NEW));



        deleteOldProperties();
    }

    private void deleteOldProperties() {
        // since 1.0.3.0
        final String KEY_PLAYER = "PLAYER";

        if (mProperties.getProperty(KEY_PLAYER) != null)
            mProperties.remove(KEY_PLAYER);
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

    public boolean getUseOnlySubscribedChannels() {
        return mProperties.getProperty(KEY_USE_ONLY_SUBSCRIBED_CHANNELS, "true").equalsIgnoreCase("true");
    }

    public void setUseOnlySubscribedChannels( boolean useOnlySubscribedChannels) {
         mProperties.setProperty(KEY_USE_ONLY_SUBSCRIBED_CHANNELS, useOnlySubscribedChannels ? "true" : "false");
    }



}
