package zattooplugin;

/**
 * The type Helper.
 *
 * @author Dieter Stockhausen
 * @since   1.5.0.0
 */
public class Helper {
    private static String propertyPath = null;

    /**
     * Gets property path.
     *
     * @return the property path
     */
    public static String getPropertyPath() {
        if (propertyPath == null) {
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
        return propertyPath;
    }

    /**
     * Help text to tooltip string.
     *
     * @param helpText      the help text
     * @param maxLineLength the max line length
     * @return the string
     */
    public static String helpTextToTooltip(String helpText, int maxLineLength) {
        helpText = helpText.trim();
        if (helpText.indexOf(' ') < 0 || helpText.indexOf(' ') > maxLineLength)
            return helpText;

        String tooltip = "";
        int foundLast = 0;
        int len = helpText.length();
        while (true) {
            int foundCurrent = helpText.lastIndexOf(' ', foundLast + maxLineLength);
            if (foundCurrent == -1 || foundCurrent == foundLast) {
                tooltip += helpText.substring(foundLast, foundLast + maxLineLength).trim() + "-<br>";
                foundLast = foundLast + maxLineLength;
            } else {
                tooltip += helpText.substring(foundLast, foundCurrent).trim() + "<br>";
                foundLast = foundCurrent;
                if (len - foundCurrent < maxLineLength) {
                    tooltip += helpText.substring(foundCurrent).trim();
                    return "<html>" + tooltip + "</html>";
                }
            }
        }
    }
}
