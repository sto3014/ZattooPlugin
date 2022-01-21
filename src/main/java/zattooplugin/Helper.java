package zattooplugin;

public class Helper {
    private static String propertyPath = null;

    public static String getPropertyPath() {
        if ( propertyPath == null) {
            String homeDir = System.getProperty("user.home");
            String os = System.getProperty("os.name");
            propertyPath = "";
            if (os.toLowerCase().contains("mac")) {
                propertyPath = homeDir + "/Library/Application Support/TV-Browser/plugins/ZattooPlugin/";

            } else {
                if (os.toLowerCase().contains("windows")) {
                    propertyPath = homeDir + "\\AppData\\Roaming\\TV-Browser\\plugins\\ZattooPlugin\\";

                } else {
                    propertyPath = homeDir + ".config/tvbrowser/plugins/ZattooPlugin/";
                }
            }
        }
        return  propertyPath;
    }
}
