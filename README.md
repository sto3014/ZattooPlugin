# Zattoo plugin for TV-Browser

Zattoo Plugin 1.0 was developed by Bodo Tasche and Michael Keppler. After version 1.0.2.0 it was no longer maintained.
The current plugin is based on their work.

## Features

---

* Opens current program in Zattoo
* Mark programs to be opened in Zattoo on start.
* Filter by supported channels
* New in 1.5: Country channel list for Austria
* New in 1.5: Create your own channel list

## Requirements

---

* TV-Browser 4.2.4+  
  The plugin was only tested under 4.2.4. The TV-Browers library is of version 3.2.1. I.e., this plugin should run on 
  older versions as well.
* Windows 7+ and maxOS is supported.
* Linux not tested.
* Others not tested.


## Installation

---

### Manuall installation

1. Download the file ZattooPlugin.jar from
   [GitHub](https://github.com/sto3014/ZattooPlugin/raw/main/target/ZattooPlugin.jar).
2. Copy ZattooPlugin.jar into the TV-Browser plugin directory:

| Operating System| Path|   
|----|----|   
| Windows 7+ | C:\Users\[username]\AppData\Roaming\TV-Browser\[TV-Browser version]\plugins|   
| Mac OS X| /Users/[username]/Library/Application Support/TV-Browser/plugins|  
| Linux| /home/[username]/.tvbrowser/plugins (until 4.2) <br /> /home/[username]/.config/tvbrowser/plugins (since 4.2.1)|  
| Others| /home/[username]/.tvbrowser/plugins |  


See also [Wiki TV-Browser](https://wiki.tvbrowser.org/index.php/Homeverzeichnis)

3. Restart TV-Browser
4. Confirm installation was successful
    * Go to TV-Browser preferences
    * Under `Plugins` you should see an entry named `Zattoo`  
  <img src="./images/Preferences.png" width="200"/>
      
## Usage

---
### Connect to Zattoo
With the right mouse button click on a program. The popup dialog shows Zattoo menus:

* For programs which are currently running you get `Open in Zattoo now`  
  <img src="./images/OpenInZattoo.png" width="400"/>  
  This menu opens a new page in your web-browser and displays the channel in Zattoo
* For programs which are not started yet you get `Open in Zattoo on start`  
  <img src="./images/OpenInZatooOnStart.png" width="400"/>  
  When the program starts, a new page will be opened in your web-browser and displays the channel in Zattoo.
  Remarks: This information will not be stored when TV-Browser is closed.  
  
If you do not get the expected Zattoo menu, you must use a different channel list. See [Plugin Settings](#plugin_settings) for details.

### Filters

The filer `Zattoo: Supported Channels` shows only channels for which are configured in a channel list (
country or your own list)

### Favorites

When defining favorites you can configure to send the programs to Zattoo (`Pass programs to:`. Thereby, these programs will be opened
when they start.  
Remarks: This information will not be stored when TV-Browser is closed.

## Plugin Settings

---
For a program to be opened in Zattoo, the TV-Browser channel must know the Zattoo channel name. This information is
configured in the country channel lists. Therefore, you must choose one country list in the `Country ` combo box. The default
country list is `Germany`. If your Zattoo account is registered in Switzerland or Austria you should select the appropriated 
list.  
<img src="./images/CountrySettings.png" width="500"/>


But even if one of the three country list fits your country, not all of your subscribed channels may show up the 
Zattoo menu.
So, if your country is not listed at all, or if not all of your channels are valid for Zattoo you must use 
the `Own channel list`. This list is empty by default, and you must set it up. To do so, please follow the 
instructions in the `Create your own channel list` and `Adopt your own channel list` area in the TV-Browser
settings panel.

Your own channel list is stored in a file. Yan can edit/save it in the `Adopt your own channel list` area in the TV-Browser
settings panel. If your prefer, you can edit it in a text editor of your choice.

|    Operating system |Customized channel file     |
|------|-----|
|Windows 7+|C:\Users\[username]\AppData\Roaming\TV-Browser\[version]\java.zattooplugin.CustomChannelProperties.prop  |
|Mac OS X|/users/[username]/Library/Preferences/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.prop |
|Linux| /home/[username]/.config/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.prop    |
|Others| /home/[username]/.tvbrowser/[version]/java.zattooplugin.CustomChannelProperties.prop   |


If there is a format error in your file, only the correct entries will be used. But you can see all entries in the
plugin-settings. If you store the file here the plugin tells you the lines which are not valid.


## Hints and known issues

---
### Predefined channel lists
The existing country channel list for Germany, Switzerland and Austria are not complete and not free of old (not valid)
entries. The Germany and Switzerland list did not change since 1.0.2.0 and therefore, some channels are missing, or
even names have changed.  
The Austria list, at the time, has only valid entries but not all 96 channels which
are provided by Zattoo for Austria were included:
* 3sat
* arte
* ATV
* ATV 2
* BR Nord
* Das Erste (ARD)
* HR
* MDR Sachsen
* NDR Niedersachsen
* One
* ORF 1
* ORF 2
* ORF 3
* PHOENIX
* Puls 4
* Puls 24
* RBB Berlin
* ServusTV Österreich
* SWR RP
* SWR SR
* WDR
* ZDF
* ZDF Neo

### Some programs (channels) are not available for Zattoo
You need to use your own channel list. Please go to your plugin settings in TV-Browser and follow the
instructions in the `Create your own channel list` and `Adopt your own channel list` area in the TV-Browser
settings panel.

### My country is not available as a predefined channel list
Same procedure as if only some channels are missing 
[My country is not available as a predefined channel list](#my_country_is_not_available_as_a_predefined_channel_list).  
Yes, you must select a channel list as `source`, even if you expect that it will not fit at all. As a result, all
channels which you described are included in your own channels list, but maybe without any Zattoo channel names.

## Acknowledgements

---
Special thanks to Bodo Tasche and Michael Keppler for developing version 1.0. This good work has made my job 
so much easier.
  

