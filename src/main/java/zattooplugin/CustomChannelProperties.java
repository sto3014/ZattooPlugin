package zattooplugin;

import devplugin.Channel;
import util.i18n.Localizer;
import util.misc.OperatingSystem;

import java.io.*;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The type Custom channel properties.
 *
 * @author Dieter Stockhausen
 * @since 1.5.0.0
 */
public class CustomChannelProperties {
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(CustomChannelProperties.class);
    private static String propertyPath = null;

    private Properties properties = null;
    private File propertyFile = null;
    private static final char[] specialChars = {'.', '^', '$', '*', '+', '-', '?', '(', ')', '[', ']', '{', '}', '|', '—', '/'};


    /**
     * Instantiates a new Custom channel properties.
     *
     * @param fileName the file name
     * @throws IOException the io exception
     */
    public CustomChannelProperties(String fileName) throws IOException {
        propertyFile = new File(fileName);
        properties = new Properties();
        if (propertyFile.exists()) {
            properties.load(new FileInputStream(propertyFile));
        }
    }

    /**
     * Gets property path.
     *
     * @return the property path
     */
    public static String getPropertyPath() {
        if (propertyPath == null) {
            String homeDir = System.getProperty("user.home");
            propertyPath = "";
            if (OperatingSystem.isMacOs()) {
                propertyPath = homeDir + "/Library/Application Support/TV-Browser/plugins/ZattooPlugin/";
            } else {
                if (OperatingSystem.isWindows()) {
                    propertyPath = homeDir + "\\AppData\\Roaming\\TV-Browser\\plugins\\ZattooPlugin\\";

                } else {
                    propertyPath = homeDir + ".config/tvbrowser/plugins/ZattooPlugin/";
                }
            }
        }
        return propertyPath;
    }

    public static String encodeToAscii(String utf8){
        final CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
        final StringBuilder result = new StringBuilder();
        for (final Character character : utf8.toCharArray()) {
            if (asciiEncoder.canEncode(character)) {
                result.append(character);
            } else {
                result.append("\\u");
                result.append(Integer.toHexString(0x10000 | character).substring(1).toUpperCase());
            }
        }
        return  result.toString();
    }

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets channel.
     *
     * @param channel       the channel
     * @param zattooChannel the zattoo channel
     * @return the channel
     * @throws IOException the io exception
     */
    public boolean setChannel(Channel channel, String zattooChannel) throws IOException {
        return setChannel(channel, zattooChannel, true);
    }

    /**
     * Sets channel.
     *
     * @param channel       the channel
     * @param zattooChannel the zattoo channel
     * @param store         the store
     * @return the channel
     * @throws IOException the io exception
     */
    public boolean setChannel(Channel channel, String zattooChannel, boolean store) throws IOException {
        properties.setProperty(getKey(channel), zattooChannel == null ? "" : zattooChannel);
        if (store) {
            return storeProperties();
        } else {
            return true;
        }
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     * @return the channel
     * @throws IOException the io exception
     */
    public boolean setChannel(Channel channel) throws IOException {
        return setChannel(channel, true);
    }

    /**
     * Sets channel.
     *
     * @param channel the channel
     * @param store   the store
     * @return the channel
     * @throws IOException the io exception
     */
    public boolean setChannel(Channel channel, boolean store) throws IOException {
        properties.setProperty(getKey(channel), "");
        if (store)
            return storeProperties();
        else
            return true;
    }


    /**
     * Remove channel boolean.
     *
     * @param channel the channel
     * @param store   the store
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean removeChannel(Channel channel, boolean store) throws IOException {
        properties.remove(getKey(channel));
        if (store)
            return storeProperties();
        else
            return true;
    }

    /**
     * Remove channel boolean.
     *
     * @param channel the channel
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean removeChannel(Channel channel) throws IOException {
        return removeChannel(channel, true);
    }

    /**
     * Remove channels boolean.
     *
     * @param channels the channels
     * @param store    the store
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean removeChannels(Channel[] channels, boolean store) throws IOException {
        for (Channel channel : channels) {
            removeChannel(channel, false);
        }
        if (store)
            return storeProperties();
        else
            return true;
    }

    /**
     * Remove channels boolean.
     *
     * @param channels the channels
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean removeChannels(Channel[] channels) throws IOException {
        return removeChannels(channels, true);
    }

    /**
     * Clear boolean.
     *
     * @param store the store
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean clear(boolean store) throws IOException {
        properties.clear();
        if (store)
            return storeProperties();
        else
            return true;
    }

    /**
     * Clear boolean.
     *
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean clear() throws IOException {
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
        for (char specialChar : specialChars) {
            channelName = channelName.replaceAll(Pattern.quote(specialChar + ""), "\\\\" + specialChar);
        }
        // replace non ascii
        //channelName = channelName.replaceAll("[^\\x00-\\x7F]", ".");
        channelName = encodeToAscii(channelName);
        return channel.getBaseCountry() + "," + channelName;
    }

    /**
     * Contains key boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    public boolean containsKey(Channel channel) {
        return properties.containsKey(getKey(channel));
    }

    /**
     * Store property file boolean.
     *
     * @return the boolean
     * @throws IOException the io exception
     */
    public boolean storeProperties() throws IOException {
        if (!propertyFile.exists()) {
            File parentFile = propertyFile.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new IOException("Folder " + parentFile.getAbsolutePath() + " could not be created.");
                }
            }
        }
        properties.store(new FileWriter(propertyFile), null);
        return true;
    }

    /**
     * Convert to properties properties.
     *
     * @param content the content
     * @return the properties
     * @throws PropertyException the property format exception
     */
    public void load(String content) throws PropertyException {
        StringTokenizer lines = new StringTokenizer(content, System.lineSeparator());
        Properties props = new Properties();
        int lineNo = 0;
        String key="";
        String value="";
        boolean hasErrors = false;
        String errorMessages = "";
        while (lines.hasMoreElements()) {
            String line = lines.nextToken().trim();
            lineNo += 1;
            if (line.startsWith("#"))
                continue;
            if (line.length() == 0)
                continue;
            if (line.indexOf("=") != line.lastIndexOf("=")) {
                errorMessages += mLocalizer.msg("error.1.format1", "error.1.format1") + " " + lineNo + ": " +
                        mLocalizer.msg("error.1.format2", "error.1.format2") + " \"" + line +
                        "\" " + mLocalizer.msg("error.1.format3", "error.1.format3") + "\n";
                hasErrors=true;
                continue;
            }

            StringTokenizer keyValuePair = new StringTokenizer(line, "=");
            if (keyValuePair.countTokens() == 1) {
                key = keyValuePair.nextToken().trim();

            } else {
                key = keyValuePair.nextToken().trim();
                value = keyValuePair.nextToken().trim();
            }
            if (new StringTokenizer(key, ",").countTokens() < 2) {
                errorMessages +=  mLocalizer.msg("error.1.format1", "error.1.format1") + " " + lineNo + ": " +
                                mLocalizer.msg("error.1.format2", "error.1.format2") + " \"" + key +
                                "\" " + mLocalizer.msg("error.1.format3", "error.1.format3") + "\n";
                hasErrors=true;
                continue;
            }
            props.put(encodeToAscii(key), value);
        }
        if ( hasErrors)
            throw new PropertyException(errorMessages);
        properties = props;
    }

    public String toString() {
        String content = "";
        Enumeration keys = properties.propertyNames();
        Vector<String> keyList = new Vector<String>();
        while (keys.hasMoreElements()) {
            keyList.add((String) keys.nextElement());
        }
        Collections.sort(keyList);
        keys = keyList.elements();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String keyUnescaped = Unescape.unescape_perl_string(key);
            content += keyUnescaped + "=" + properties.getProperty(key) + System.lineSeparator();
        }
        return content;
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return properties.isEmpty();
    }

}
