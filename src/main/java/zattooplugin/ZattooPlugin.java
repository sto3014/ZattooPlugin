package zattooplugin;

import devplugin.*;
import util.browserlauncher.Launch;
import util.io.IOUtilities;
import util.i18n.Localizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Zattoo settings.
 *
 * @author Bodo Tasche, Michael Keppler
 * @since 1.0.0.0
 */
public final class ZattooPlugin extends Plugin {
    private static final Version PLUGIN_VERSION = new Version(1, 50, 2, true);
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooPlugin.class);
    private static final Logger mLog = Logger.getLogger(ZattooPlugin.class.getName());
    private ImageIcon mIcon;
    private static ZattooPlugin mInstance;
    private ZattooSettings mSettings;
    private PluginsProgramFilter mFilters;
    private ZattooChannelProperties mChannelIds;
    private final HashSet<Program> mSwitchPrograms = new HashSet<>();
    private PluginTreeNode mRootNode;
    private ThemeIcon mThemeIcon;
    private final ProgramReceiveTarget mProgramReceiveTarget;
    private static CustomChannelProperties customChannelProperties = null;

    /**
     * Instantiates a new Zattoo plugin.
     */
    public ZattooPlugin() {
        mProgramReceiveTarget = new ProgramReceiveTarget(this, mLocalizer.msg("receiveTarget", "Show on Zattoo"), "ZATTOO_TARGET");
        mInstance = this;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public static Version getVersion() {
        return PLUGIN_VERSION;
    }

    public Properties storeSettings() {
        return mSettings.storeSettings();
    }

    public void loadSettings(Properties properties) {
        mSettings = new ZattooSettings(properties);
        // Must be explicitly set, because it has a side effect on ZattooChannelProperties.
        // Same to mSettings.getCustomChannelFileName(), but it will be set
        // in changeCountry() as well.
        changeCountry(mSettings.getCountry());
    }

    /**
     * Change country.
     *
     * @param country the country
     */
    public void changeCountry(String country) {
        mSettings.setCountry(country);
        mChannelIds = new ZattooChannelProperties(country);
    }

    /**
     * Change source country.
     *
     * @param sourceCountry the source country
     */
    public void changeSourceCountry(String sourceCountry) {
        mSettings.setSourceCountry(sourceCountry);
    }

    /**
     * Change update.
     *
     * @param update the update
     */
    public void changeUpdate(int update) {
        mSettings.setUpdate(update);
    }

    /**
     * Change merge.
     *
     * @param merge the merge
     */
    public void changeMerge(int merge) {
        mSettings.setMerge(merge);
    }

    public void changeUseOnlySubscribedChannels( boolean useOnlySubscribedChannels){
        mSettings.setUseOnlySubscribedChannels(useOnlySubscribedChannels);
    }

    public PluginInfo getInfo() {
        return new PluginInfo(ZattooPlugin.class,
                mLocalizer.msg("pluginName", "Zattoo"),
                mLocalizer.msg("description", "Switches channels in Zattoo"),
                mLocalizer.msg("author", "Bodo Tasche, Michael Keppler, since 1.0.3.0 Dieter Stockhausen"), "GPL");
    }

    public SettingsTab getSettingsTab() {
        return new ZattooSettingsTab(mSettings);
    }

    /**
     * Gets plugin icon.
     *
     * @return the plugin icon
     */
    public Icon getPluginIcon() {
        if (mIcon == null) {
            mIcon = createImageIcon("apps", "zattoo", 16);
        }

        return mIcon;
    }

    public ActionMenu getContextMenuActions(Program program) {
        if (getPluginManager().getExampleProgram().equals(program)) {
            return getRememberActionMenu(program);
        } else if (isProgramSupported(program)) {
            return getRememberActionMenu(program);
        } else {
            if (!program.isOnAir()) {
                return null;
            } else if (!isChannelSupported(program.getChannel())) {
                return null;
            } else
                return getSwitchActionMenu(program.getChannel());
        }
    }

    /**
     * Gets remember action menu.
     *
     * @param program the program
     * @return the remember action menu
     */
    private ActionMenu getRememberActionMenu(final Program program) {
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    mSwitchPrograms.add(program);
                    program.mark(ZattooPlugin.this);
                    updateRootNode();
                });
            }
        };
        action.putValue("Name", mLocalizer.msg("contextMenuRemember", "Switch channel when program starts"));
        action.putValue("SmallIcon", getPluginIcon());
        return new ActionMenu(action);
    }

    public ActionMenu getContextMenuActions(Channel channel) {
        return channel != null && isChannelSupported(channel) ? getSwitchActionMenu(channel) : null;
    }

    /**
     * Gets switch action menu.
     *
     * @param channel the channel
     * @return the switch action menu
     */
    private ActionMenu getSwitchActionMenu(final Channel channel) {
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(() -> openChannel(channel));
            }
        };
        action.putValue("Name", mLocalizer.msg("contextMenuSwitch", "Switch channel"));
        action.putValue("SmallIcon", getPluginIcon());
        return new ActionMenu(action);
    }

    /**
     * Open channel.
     *
     * @param channel the channel
     */
    private void openChannel(Channel channel) {
        String id = getChannelId(channel);

        if (id != null) {
            String url = "https://zattoo.com/live/" + id;
            mLog.log(Level.INFO, "Open URL " + url);
            Launch.openURL(url);
        }
    }

    /**
     * Gets channel id.
     *
     * @param channel the channel
     * @return the channel id
     */
    private String getChannelId(Channel channel) {
        String id = mChannelIds.getProperty(channel);
        if (id == null) {
            mLog.log(Level.INFO, "No zattoo channel mapping found for " + channel.getUniqueId());
            return null;
        } else {
            int comma = id.indexOf(44);
            // Support old  property format (1.0.2.0 and before)
            if (comma >= 0) {
                id = id.substring(comma + 1).trim();
            }
            return id;
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ZattooPlugin getInstance() {
        return mInstance;
    }

    public PluginsProgramFilter[] getAvailableFilter() {
        if (mFilters == null) {
            mFilters = new PluginsProgramFilter(this) {
                public String getSubName() {
                    return ZattooPlugin.mLocalizer.msg("supportedChannels", "Supported channels");
                }

                public boolean accept(Program program) {
                    return isChannelSupported(program.getChannel());
                }
            };
        }

        return new PluginsProgramFilter[]{mFilters};
    }

    public Class<? extends PluginsFilterComponent>[] getAvailableFilterComponentClasses() {
        return new Class[]{ZattooFilterComponent.class};
    }

    /**
     * Is channel supported boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    public boolean isChannelSupported(Channel channel) {
        return getChannelId(channel) != null;
    }

    public boolean canUseProgramTree() {
        return true;
    }

    public PluginTreeNode getRootNode() {
        if (mRootNode == null) {
            mRootNode = new PluginTreeNode(this);
            mRootNode.getMutableTreeNode().setIcon(getPluginIcon());
        }

        return mRootNode;
    }

    /**
     * Update root node.
     */
    private void updateRootNode() {
        getRootNode().clear();

        for (Program program : mSwitchPrograms) {
            mRootNode.addProgramWithoutCheck(program);
        }

        mRootNode.update();
    }

    public ThemeIcon getMarkIconFromTheme() {
        if (mThemeIcon == null) {
            mThemeIcon = new ThemeIcon("apps", "zattoo", 16);
        }

        return mThemeIcon;
    }

    public void handleTvBrowserStartFinished() {
        Timer mTimer = new Timer(60000, e -> {
            Date today = new Date();
            int now = IOUtilities.getMinutesAfterMidnight();
            int delay = 2;
            Program startProgram = null;
            synchronized (mSwitchPrograms) {

                for (Program program : mSwitchPrograms) {
                    if (program.getDate().compareTo(today) == 0) {
                        int startTime = program.getStartTime();
                        if (now >= startTime - delay) {
                            startProgram = program;
                            break;
                        }
                    }
                }

                if (startProgram != null) {
                    mSwitchPrograms.remove(startProgram);
                    startProgram.unmark(ZattooPlugin.this);
                    updateRootNode();
                    openChannel(startProgram.getChannel());
                }

            }
        });
        mTimer.start();
    }

    public boolean canReceiveProgramsWithTarget() {
        return true;
    }

    public ProgramReceiveTarget[] getProgramReceiveTargets() {
        return new ProgramReceiveTarget[]{mProgramReceiveTarget};
    }

    public boolean receivePrograms(int eventType, Program[] programArr, ProgramReceiveTarget receiveTarget) {
        for (Program program : programArr) {
            if (isProgramSupported(program)) {
                mSwitchPrograms.add(program);
                program.mark(this);
            }
        }

        updateRootNode();
        return true;
    }

    /**
     * Is program supported boolean.
     *
     * @param program the program
     * @return the boolean
     */
    private boolean isProgramSupported(Program program) {
        return isChannelSupported(program.getChannel()) && !program.isExpired() && !program.isOnAir();
    }


}
