package zattooplugin;

import devplugin.Channel;

import java.io.*;
import java.util.Properties;
import java.util.regex.Pattern;

public class CustomChannelProperties {
    private Properties properties = null;
    private File propertyFile = null;
    boolean hasErrors = false;
    private static final char[] specialChars = {'.', '^', '$', '*', '+', '-', '?', '(', ')', '[',  ']',  '{', '}',  '|',  '—', '/'};


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

    public boolean setChannel(Channel channel, String zattooChannel) {
        return setChannel(channel, zattooChannel, true);
    }

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

    public boolean setChannel(Channel channel) {
        return setChannel(channel, true);
    }

    public boolean setChannel(Channel channel, boolean store) {
        if (hasErrors)
            return false;
        properties.setProperty(getKey(channel), "");
        if (store)
            return storePropertyFile();
        else
            return true;
    }


    public boolean removeChannel(Channel channel, boolean store) {
        if (hasErrors)
            return false;
        properties.remove(getKey(channel));
        if (store)
            return storePropertyFile();
        else
            return true;
    }

    public boolean removeChannel(Channel channel) {
        return removeChannel(channel, true);
    }

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

    public boolean removeChannels(Channel[] channels) {
        return removeChannels(channels, true);
    }

    public boolean clear(boolean store) {
        if (hasErrors)
            return false;
        properties.clear();
        if (store)
            return storePropertyFile();
        else
            return true;
    }

    public boolean clear() {
        return clear(true);
    }

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

    public boolean containsKey ( Channel channel) {
        if ( hasErrors)
            return false;
        return properties.containsKey(getKey(channel));
    }

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
