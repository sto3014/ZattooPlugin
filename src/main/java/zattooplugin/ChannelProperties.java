package zattooplugin;

import devplugin.Channel;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since 1.0.0.0
 */
public abstract class ChannelProperties {
    private String mFileName;
    protected Properties mProperties;
    protected boolean isCustom;

    /**
     * Instantiates a new Channel properties.
     *
     * @param country                 the country
     * @param customChannelProperties the custom channel properties
     */
    protected ChannelProperties(String country, String customChannelProperties) {
        isCustom = country.equals("custom");
        if (isCustom) {
            mFileName = customChannelProperties;
        } else
            mFileName = "channels_" + country + ".properties";
    }

    /**
     * Initialize properties.
     */
    private void initializeProperties() {
        if (mProperties == null || (isCustom)) {
            InputStream stream;
            if (isCustom) {
                try {
                    mProperties = new CustomChannelProperties(mFileName).getProperties();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                stream = getClass().getResourceAsStream(mFileName);
                try {
                    mProperties = new Properties();
                    mProperties.load(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            checkProperties();
        }
    }

    /**
     * Check properties.
     */
    protected abstract void checkProperties();

    /**
     * Gets property.
     *
     * @param channel the channel
     * @return the property
     */
    public String getProperty(Channel channel) {
        try {
            initializeProperties();
            String channelCountry = channel.getBaseCountry();
            String channelName = channel.getDefaultName();
            Enumeration keys = mProperties.propertyNames();

            while (true) {
                String key;
                int separatorIndex;
                do {
                    if (!keys.hasMoreElements()) {
                        return null;
                    }

                    key = (String) keys.nextElement();
                    separatorIndex = key.indexOf(44);
                } while (separatorIndex <= 0);

                String[] countries = key.substring(0, separatorIndex).split("\\W");
                String name = "(?i)" + key.substring(separatorIndex + 1).replaceAll("\\*", ".*");
                String[] arr$ = countries;
                int len$ = countries.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    String country = arr$[i$];
                    if (channelCountry.equalsIgnoreCase(country) && channelName.matches(name)) {
                        String property = mProperties.getProperty(key);
                        if (isValidProperty(property)) {
                            return property;
                        }

                        property = mProperties.getProperty(property);
                        if (isValidProperty(property)) {
                            return property;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Is valid property boolean.
     *
     * @param var1 the var 1
     * @return the boolean
     */
    protected abstract boolean isValidProperty(String var1);
}
