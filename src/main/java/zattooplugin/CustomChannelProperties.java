package zattooplugin;

import devplugin.Channel;

import java.io.*;
import java.util.Properties;

public class CustomChannelProperties {
    private Properties properties = null;
    private File propertyFile = null;
    boolean hasErrors = false;

    public CustomChannelProperties(String propertyFileName) {
        propertyFile = getPropertyFile(propertyFileName);
        if ( propertyFile == null) {
            hasErrors = true;
        } else {
            properties = new Properties();
            if (propertyFile.exists())
                try {
                    properties.load(new FileInputStream(propertyFile));
                } catch (IOException e) {
                    e.printStackTrace();
                    hasErrors = false;
                }
        }
    }

    private File getPropertyFile(String propertyFileName) {
        File propertyDir = new File(Helper.getPropertyPath());
        if (!propertyDir.exists())
            if ( ! propertyDir.mkdirs() )
                return null;
        return new File(Helper.getPropertyPath() + propertyFileName);
    }

    public void setChannel(Channel channel, String zattooChannel) {
        if ( hasErrors)
            return;
        properties.setProperty( getKey(channel), zattooChannel == null ? "" : zattooChannel);
        storePropertyFile();
    }
    public void setChannel(Channel channel) {
        if ( hasErrors)
            return;
        properties.setProperty(getKey(channel), "");
        storePropertyFile();
    }

    private void storePropertyFile() {
        try {
            properties.store(new FileWriter(propertyFile), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getKey(Channel channel){
        return channel.getBaseCountry() + "," + channel.getDefaultName().replaceAll("\\ ", "\\ ");
    }
}
