package zattooplugin;

import devplugin.Channel;

import java.io.*;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The type Custom channel properties.
 *
 * @author Dieter Stockhausen
 * @since   1.5.0.0
 */
public class CustomChannelProperties {
    private Properties properties = null;
    private File propertyFile = null;
    boolean hasErrors = false;
    private static final char[] specialChars = {'.', '^', '$', '*', '+', '-', '?', '(', ')', '[',  ']',  '{', '}',  '|',  '—', '/'};


    /**
     * Instantiates a new Custom channel properties.
     *
     * @param settings the settings
     */
    public CustomChannelProperties(ZattooSettings settings) {
        propertyFile = new File(settings.getCustomChannelProperties());
        properties = new Properties();
        if (propertyFile.exists())
            try {
                properties.load(new FileInputStream(propertyFile));
            } catch (IOException e) {
                e.printStackTrace();
                hasErrors = true;
            }
    }

    /**
     * Sets channel.
     *
     * @param channel       the channel
     * @param zattooChannel the zattoo channel
     * @return the channel
     */
    public boolean setChannel(Channel channel, String zattooChannel) {
        return setChannel(channel, zattooChannel, true);
    }

    /**
     * Sets channel.
     *
     * @param channel       the channel
     * @param zattooChannel the zattoo channel
     * @param store         the store
     * @return the channel
     */
    public boolean setChannel(Channel channel, String zattooChannel, boolean store) {
        if (hasErrors)
            return false;
        properties.setProperty(getKey(channel), zattooChannel == null ? "" : zattooChannel);
        if (store) {
            return storePropertyFile();
        } else {
            return true;
        }
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     * @return the channel
     */
    public boolean setChannel(Channel channel) {
        return setChannel(channel, true);
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     * @param store   the store
     * @return the channel
     */
    public boolean setChannel(Channel channel, boolean store) {
        if (hasErrors)
            return false;
        properties.setProperty(getKey(channel), "");
        if (store)
            return storePropertyFile();
        else
            return true;
    }


    /**
     * Remove channel boolean.
     *
     * @param channel the channel
     * @param store   the store
     * @return the boolean
     */
    public boolean removeChannel(Channel channel, boolean store) {
        if (hasErrors)
            return false;
        properties.remove(getKey(channel));
        if (store)
            return storePropertyFile();
        else
            return true;
    }

    /**
     * Remove channel boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    public boolean removeChannel(Channel channel) {
        return removeChannel(channel, true);
    }

    /**
     * Remove channels boolean.
     *
     * @param channels the channels
     * @param store    the store
     * @return the boolean
     */
    public boolean removeChannels(Channel[] channels, boolean store) {
        if (hasErrors)
            return false;
        for (Channel channel : channels) {
            removeChannel(channel, false);
        }
        if (store)
            return storePropertyFile();
        else
            return true;
    }

    /**
     * Remove channels boolean.
     *
     * @param channels the channels
     * @return the boolean
     */
    public boolean removeChannels(Channel[] channels) {
        return removeChannels(channels, true);
    }

    /**
     * Clear boolean.
     *
     * @param store the store
     * @return the boolean
     */
    public boolean clear(boolean store) {
        if (hasErrors)
            return false;
        properties.clear();
        if (store)
            return storePropertyFile();
        else
            return true;
    }

    /**
     * Clear boolean.
     *
     * @return the boolean
     */
    public boolean clear() {
        return clear(true);
    }

    /**
     * Gets key.
     *
     * @param channel the channel
     * @return the key
     */
    private String getKey(Channel channel) {
        // mask regex chars
        String channelName = channel.getDefaultName();
        for ( char specialChar : specialChars) {
            channelName = channelName.replaceAll(Pattern.quote( specialChar + "" ), "\\\\" + specialChar);
        }
        // replace non ascii
        channelName = channelName.replaceAll("[^\\x00-\\x7F]", ".");
        return channel.getBaseCountry() + "," + channelName;
    }

    /**
     * Contains key boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    public boolean containsKey ( Channel channel) {
        if ( hasErrors)
            return false;
        return properties.containsKey(getKey(channel));
    }

    /**
     * Store property file boolean.
     *
     * @return the boolean
     */
    public boolean storePropertyFile() {
        if (hasErrors)
            return false;
        try {
            properties.store(new FileWriter(propertyFile), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
