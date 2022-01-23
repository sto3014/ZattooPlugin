package zattooplugin;

import devplugin.*;
import tvbrowser.core.ChannelList;
import util.browserlauncher.Launch;
import util.io.ExecutionHandler;
import util.io.IOUtilities;
import util.misc.OperatingSystem;
import util.ui.Localizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ZattooPlugin extends Plugin {
    private static final String ICON_NAME = "zattoo";
    private static final String ICON_CATEGORY = "apps";
    private static final boolean PLUGIN_IS_STABLE = true;
    private static final Version PLUGIN_VERSION = new Version(1, 4, 0, true);
    private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooPlugin.class);
    private static final Logger mLog = Logger.getLogger(ZattooPlugin.class.getName());
    private ImageIcon mIcon;
    private static ZattooPlugin mInstance;
    private ZattooSettings mSettings;
    private PluginsProgramFilter mFilters;
    private ZattooChannelProperties mChannelIds;
    private final HashSet<Program> mSwitchPrograms = new HashSet();
    private PluginTreeNode mRootNode;
    private ThemeIcon mThemeIcon;
    private Timer mTimer;
    private ProgramReceiveTarget mProgramReceiveTarget;
    private static CustomChannelProperties customChannelProperties = null;

    public ZattooPlugin() {
        mProgramReceiveTarget = new ProgramReceiveTarget(this, mLocalizer.msg("receiveTarget", "Show on Zattoo"), "ZATTOO_TARGET");
        mInstance = this;
    }

    public static Version getVersion() {
        return PLUGIN_VERSION;
    }

    public Properties storeSettings() {
        return mSettings.storeSettings();
    }

    public void loadSettings(Properties properties) {
        mSettings = new ZattooSettings(properties);
        changeCountry(mSettings.getCountry());
    }

    public void changeCountry(String country) {
        mSettings.setCountry(country);
        mChannelIds = new ZattooChannelProperties(country, mSettings.getCustomChannelProperties(),
                mSettings.isRereadCustomChannelProperties());
    }

    public void changeSourceCountry(String sourceCountry) {
        mSettings.setSourceCountry(sourceCountry);
    }

    public void changeUpdate(int update) {
        mSettings.setUpdate(update);
    }

    public void changeMerge(int merge) {
        mSettings.setMerge(merge);
    }

    public void changeCustomChannelProperties(String customChannelProperties) {
        mSettings.setCustomChannelProperties(customChannelProperties);
        mChannelIds = new ZattooChannelProperties(mSettings.getCountry(), customChannelProperties,
                mSettings.isRereadCustomChannelProperties());
    }

    public void changeRereadCustomChannelProperties(boolean reread) {
        mSettings.setRereadCustomChannelProperties(reread);
        mChannelIds = new ZattooChannelProperties(mSettings.getCountry(), mSettings.getCustomChannelProperties(),
                reread);
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
            return program.isOnAir() && isChannelSupported(program.getChannel()) ? getSwitchActionMenu(program.getChannel()) : null;
        }
    }

    private ActionMenu getRememberActionMenu(final Program program) {
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mSwitchPrograms.add(program);
                        program.mark(ZattooPlugin.this);
                        updateRootNode();
                    }
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

    private ActionMenu getSwitchActionMenu(final Channel channel) {
        AbstractAction action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        openChannel(channel);
                    }
                });
            }
        };
        action.putValue("Name", mLocalizer.msg("contextMenuSwitch", "Switch channel"));
        action.putValue("SmallIcon", getPluginIcon());
        return new ActionMenu(action);
    }

    private void openChannel(Channel channel) {
        String id = getChannelId(channel);

        if (id != null) {
            String url = "https://zattoo.com/live/" + id;
            mLog.log(Level.INFO, "Open URL " + url);
            Launch.openURL(url);
        }
    }

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
                    return isProgramSupported(program);
                }
            };
        }

        return new PluginsProgramFilter[]{mFilters};
    }

    public Class<? extends PluginsFilterComponent>[] getAvailableFilterComponentClasses() {
        return new Class[]{ZattooFilterComponent.class};
    }

    public boolean isChannelSupported(Channel channel) {
        return getChannelId(channel) != null;
    }

    public static boolean canUseLocalPlayer() {
        return OperatingSystem.isMacOs() || OperatingSystem.isWindows();
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

    private void updateRootNode() {
        getRootNode().clear();
        Iterator i$ = mSwitchPrograms.iterator();

        while (i$.hasNext()) {
            Program program = (Program) i$.next();
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
        mTimer = new Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Date today = new Date();
                int now = IOUtilities.getMinutesAfterMidnight();
                int delay = 2;
                Program startProgram = null;
                synchronized (mSwitchPrograms) {
                    Iterator i$ = mSwitchPrograms.iterator();

                    while (i$.hasNext()) {
                        Program program = (Program) i$.next();
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

    public boolean receivePrograms(Program[] programArr, ProgramReceiveTarget receiveTarget) {
        Program[] arr$ = programArr;
        int len$ = programArr.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            Program program = arr$[i$];
            if (isProgramSupported(program)) {
                mSwitchPrograms.add(program);
                program.mark(this);
            }
        }

        updateRootNode();
        return true;
    }

    private boolean isProgramSupported(Program program) {
        return isChannelSupported(program.getChannel()) && !program.isExpired() && !program.isOnAir();
    }


    public void updateCustomChannels(boolean replace, boolean mergeAndReplace) {
        Channel[] channels = ChannelList.getSubscribedChannels();
        if (customChannelProperties == null)
            customChannelProperties = new CustomChannelProperties(mSettings);
        if (replace) {
            customChannelProperties.clear(false);
        }
        if (replace || mergeAndReplace) {
            for (Channel channel : channels) {
                String id = getChannelId(channel);
                String zattooChannel = (id == null) ? "" : id;
                customChannelProperties.setChannel(channel, zattooChannel);
            }
        } else {
            for (Channel channel : channels) {
                if (!customChannelProperties.containsKey(channel)) {
                    String id = getChannelId(channel);
                    String zattooChannel = (id == null) ? "" : id;
                    customChannelProperties.setChannel(channel, zattooChannel);
                }
            }
        }
        customChannelProperties.storePropertyFile();
    }
}
