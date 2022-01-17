package zattooplugin;

import devplugin.*;
import util.browserlauncher.Launch;
import util.exc.ErrorHandler;
import util.io.ExecutionHandler;
import util.io.IOUtilities;
import util.misc.OperatingSystem;
import util.ui.Localizer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ZattooPlugin extends Plugin {
   private static final String ICON_NAME = "zattoo";
   private static final String ICON_CATEGORY = "apps";
   private static final boolean PLUGIN_IS_STABLE = true;
   private static final Version PLUGIN_VERSION = new Version(1, 3, 0, true);
   private static final Localizer mLocalizer = Localizer.getLocalizerFor(ZattooPlugin.class);
   private static final Logger mLog = Logger.getLogger(ZattooPlugin.class.getName());
   private ImageIcon mIcon;
   private static ZattooPlugin mInstance;
   private ZattooSettings mSettings;
   private PluginsProgramFilter mFilters;
   private ZattooChannelProperties mChannelIds;
   private HashSet<Program> mSwitchPrograms = new HashSet();
   private PluginTreeNode mRootNode;
   private ThemeIcon mThemeIcon;
   private Timer mTimer;
   private ProgramReceiveTarget mProgramReceiveTarget;

   public ZattooPlugin() {
      this.mProgramReceiveTarget = new ProgramReceiveTarget(this, mLocalizer.msg("receiveTarget", "Show on Zattoo"), "ZATTOO_TARGET");
      mInstance = this;
   }

   public static Version getVersion() {
      return PLUGIN_VERSION;
   }

   public Properties storeSettings() {
      return this.mSettings.storeSettings();
   }

   public void loadSettings(Properties properties) {
      this.mSettings = new ZattooSettings(properties);
      this.changeCountry(this.mSettings.getCountry());
   }

   public void changeCountry(String country) {
      try {
         this.mChannelIds = new ZattooChannelProperties("channels_" + country, this.mSettings);
         this.mSettings.setCountry(country);
      } catch (Exception var3) {
         mLog.log(Level.WARNING, "Could not load File for Country " + country + ".", var3);
      }

   }

   public PluginInfo getInfo() {
      return new PluginInfo(ZattooPlugin.class, mLocalizer.msg("pluginName", "Zattoo"), mLocalizer.msg("description", "Switches channels in Zattoo"), "Bodo Tasche, Michael Keppler", "GPL");
   }

   public SettingsTab getSettingsTab() {
      return new ZattooSettingsTab(this.mSettings);
   }

   public Icon getPluginIcon() {
      if (this.mIcon == null) {
         this.mIcon = this.createImageIcon("apps", "zattoo", 16);
      }

      return this.mIcon;
   }

   public ActionMenu getContextMenuActions(Program program) {
      if (getPluginManager().getExampleProgram().equals(program)) {
         return this.getRememberActionMenu(program);
      } else if (this.isProgramSupported(program)) {
         return this.getRememberActionMenu(program);
      } else {
         return program.isOnAir() && this.isChannelSupported(program.getChannel()) ? this.getSwitchActionMenu(program.getChannel()) : null;
      }
   }

   private ActionMenu getRememberActionMenu(final Program program) {
      AbstractAction action = new AbstractAction() {
         public void actionPerformed(ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  ZattooPlugin.this.mSwitchPrograms.add(program);
                  program.mark(ZattooPlugin.this);
                  ZattooPlugin.this.updateRootNode();
               }
            });
         }
      };
      action.putValue("Name", mLocalizer.msg("contextMenuRemember", "Switch channel when program starts"));
      action.putValue("SmallIcon", this.getPluginIcon());
      return new ActionMenu(action);
   }

   public ActionMenu getContextMenuActions(Channel channel) {
      return channel != null && this.isChannelSupported(channel) ? this.getSwitchActionMenu(channel) : null;
   }

   private ActionMenu getSwitchActionMenu(final Channel channel) {
      AbstractAction action = new AbstractAction() {
         public void actionPerformed(ActionEvent evt) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  ZattooPlugin.this.openChannel(channel);
               }
            });
         }
      };
      action.putValue("Name", mLocalizer.msg("contextMenuSwitch", "Switch channel"));
      action.putValue("SmallIcon", this.getPluginIcon());
      return new ActionMenu(action);
   }

   private void openChannel(Channel channel) {
      String id = this.getChannelId(channel);
      if (id != null) {
         String url = "http://zattoo.com/watch/" + id;
         ExecutionHandler executionHandler = null;
         if (this.mSettings.getUseWebPlayer()) {
            mLog.log(Level.INFO,"Open URL " + url);
            Launch.openURL(url);
         } else if (this.mSettings.getUsePrismPlayer()) {
            executionHandler = new ExecutionHandler("-uri " + url + " -name tvbrowser-zattoo", "prism");
         } else {
            String zattooURI = "zattoo://channel/" + id;
            if (OperatingSystem.isLinux()) {
               executionHandler = new ExecutionHandler(zattooURI, "zattoo-uri-handler");
            } else if (OperatingSystem.isMacOs()) {
               executionHandler = new ExecutionHandler(zattooURI, "open");
            } else {
               executionHandler = new ExecutionHandler(new String[]{"rundll32.exe", "url.dll,FileProtocolHandler", zattooURI});
            }
         }

         if (executionHandler != null) {
            try {
               executionHandler.execute(false);
            } catch (IOException var6) {
               var6.printStackTrace();
               ErrorHandler.handle(mLocalizer.msg("error.zatto", "Could not start zattoo"), var6);
            }
         }

      }
   }

   private String getChannelId(Channel channel) {
      String id = this.mChannelIds.getProperty(channel);
      if (id == null) {
         mLog.log(Level.INFO, "No zattoo channel mapping found for " + channel.getUniqueId());
         return null;
      } else {
         int comma = id.indexOf(44);
         if (!this.mSettings.getUseLocalPlayer() && comma >= 0) {
            return id.substring(comma + 1).trim();
         } else if (!this.mSettings.getUseLocalPlayer() && comma == -1) {
            return null;
         } else {
            return comma >= 0 ? id.substring(0, comma) : id;
         }
      }
   }

   public static ZattooPlugin getInstance() {
      return mInstance;
   }

   public PluginsProgramFilter[] getAvailableFilter() {
      if (this.mFilters == null) {
         this.mFilters = new PluginsProgramFilter(this) {
            public String getSubName() {
               return ZattooPlugin.mLocalizer.msg("supportedChannels", "Supported channels");
            }

            public boolean accept(Program program) {
               return ZattooPlugin.this.isProgramSupported(program);
            }
         };
      }

      return new PluginsProgramFilter[]{this.mFilters};
   }

   public Class<? extends PluginsFilterComponent>[] getAvailableFilterComponentClasses() {
      return new Class[]{ZattooFilterComponent.class};
   }

   public boolean isChannelSupported(Channel channel) {
      return this.getChannelId(channel) != null;
   }

   public static boolean canUseLocalPlayer() {
      return OperatingSystem.isMacOs() || OperatingSystem.isWindows();
   }

   public boolean canUseProgramTree() {
      return true;
   }

   public PluginTreeNode getRootNode() {
      if (this.mRootNode == null) {
         this.mRootNode = new PluginTreeNode(this);
         this.mRootNode.getMutableTreeNode().setIcon(this.getPluginIcon());
      }

      return this.mRootNode;
   }

   private void updateRootNode() {
      this.getRootNode().clear();
      Iterator i$ = this.mSwitchPrograms.iterator();

      while(i$.hasNext()) {
         Program program = (Program)i$.next();
         this.mRootNode.addProgramWithoutCheck(program);
      }

      this.mRootNode.update();
   }

   public ThemeIcon getMarkIconFromTheme() {
      if (this.mThemeIcon == null) {
         this.mThemeIcon = new ThemeIcon("apps", "zattoo", 16);
      }

      return this.mThemeIcon;
   }

   public void handleTvBrowserStartFinished() {
      this.mTimer = new Timer(60000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            Date today = new Date();
            int now = IOUtilities.getMinutesAfterMidnight();
            int delay = 2;
            Program startProgram = null;
            synchronized(ZattooPlugin.this.mSwitchPrograms) {
               Iterator i$ = ZattooPlugin.this.mSwitchPrograms.iterator();

               while(i$.hasNext()) {
                  Program program = (Program)i$.next();
                  if (program.getDate().compareTo(today) == 0) {
                     int startTime = program.getStartTime();
                     if (now >= startTime - delay) {
                        startProgram = program;
                        break;
                     }
                  }
               }

               if (startProgram != null) {
                  ZattooPlugin.this.mSwitchPrograms.remove(startProgram);
                  startProgram.unmark(ZattooPlugin.this);
                  ZattooPlugin.this.updateRootNode();
                  ZattooPlugin.this.openChannel(startProgram.getChannel());
               }

            }
         }
      });
      this.mTimer.start();
   }

   public boolean canReceiveProgramsWithTarget() {
      return true;
   }

   public ProgramReceiveTarget[] getProgramReceiveTargets() {
      return new ProgramReceiveTarget[]{this.mProgramReceiveTarget};
   }

   public boolean receivePrograms(Program[] programArr, ProgramReceiveTarget receiveTarget) {
      Program[] arr$ = programArr;
      int len$ = programArr.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Program program = arr$[i$];
         if (this.isProgramSupported(program)) {
            this.mSwitchPrograms.add(program);
            program.mark(this);
         }
      }

      this.updateRootNode();
      return true;
   }

   private boolean isProgramSupported(Program program) {
      return this.isChannelSupported(program.getChannel()) && !program.isExpired() && !program.isOnAir();
   }
}
