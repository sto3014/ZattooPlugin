# Zattoo plugin for TV-Browser

Zattoo Plugin 1.0 was developed by Bodo Tasche and Michael Keppler. After version 1.0.2.0 it was no longer maintained.
The current plugin is based on their work.

## Features

---

* Opens current program in Zattoo
* Mark programs to be opened in Zattoo on start.
* Filter by supported channels
* New in 1.5: Add country channel list for Austria
* New in 1.5: Add possibility to create your own channel list
* Update in 1.5: Channel lists for Germany and Switzerland were updated.

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

### Get prepared

If you have a Zattoo account for Germany you are all set.  
If you have a Zattoo account for Switzerland or Austria, just go to the Zattoo plugin settings and change the `Country:`
combo box to your country.

### Switch to Zattoo

With the right mouse button click on a program. The popup dialog shows a Zattoo menu:

* For programs which are currently running you see `Open in Zattoo now`  
  <img src="./images/OpenInZattoo.png" width="400"/>  
  This menu opens a new page in your web-browser and displays the channel in Zattoo
* For programs which are not yet started, you see `Open in Zattoo on start`  
  <img src="./images/OpenInZatooOnStart.png" width="400"/>  
  When the program starts, a new page will be opened in your web-browser and displays the channel in Zattoo. Remarks:
  This information will not be stored when TV-Browser is closed.

If you don't get the expected Zattoo menu, the selected channel is not available in Zattoo of your country, or the
channel is just not configured to know the Zattoo channel In latter case, you can define your own channel list.

### Predefined channel lists

The existing country channel list for Germany, Switzerland and Austria should cover all channel information for your
country.

|Country     | No. of Zattoo channels    | Zattoo Website     |  No. of linked TVB channels<sup>1</sup> |
|------------|:---------------------------:|--------------------|:-----------------------------:|
|Germany     | 141 | [Zattoo Germany](https://zattoo.com/de/sender)  | 66<sup>3</sup>|
|Switzerland | 282 | [Zattoo Switzerland](https://zattoo.com/ch/sender) |94<sup>2</sup>|
|Austria     | 98  | [Zattoo Austria](https://zattoo.com/at/sender)|59<sup>3</sup>|

<sup>1</sup> Data plugins EPGfree-data and [EPG-Plus](https://epg-plus.com) were used.  
<sup>2</sup> EPG-Plus provides 35 channels. So it is worth a look.  
<sup>3</sup> EPG-Plus provides only 4 channels which are supported by Zattoo for Germany/Austria

### Define your own channel list

There are three use cases for an own channel list:

#### Use case 1: Zattoo menu is not shown

You subscribed a channel in TV-Browser but no Zattoo menu appears for this channel. The basic reason for this is quite
simple: The chosen country channel list does not include the Zattoo channel name. And why is it?: Zattoo added the
channel recently. What to do?

* Go to your plugin settings
* Create your own channel list:
    * Set the `Country` to `Own channel list`
    * Check the `Update own channel list` box
    * Choose a `Source` country
    * Press `Apply Update` button

By applying the update, for every subscribed channel a configuration is added in the editor below. On the left side of
the equation sign you see the country and the name of the TV-Browser channel (Example: de,arte). Please do not change
it. On the right side you see the name of the Zattoo channel.   
The channel for which the Zattoo menu does not appear will be listed, but the right side is empty. To get the name for
the channel you must go into the Zattoo WebApp and start the desired channel. At the end of the URL the name is
displayed. Example for Arte:  
The Zattoo URL for Arte is `https://zattoo.com/live/DE_arte`. I.e., the correct Zattoo channel name is "De_arte" and the
correct configuration entry must look like this: `de,arte=DE_arte`.

#### Use case 2: Zattoo menu is shown, but the channel does not exist in Zattoo

You use the correct country channel list, and you get the Zattoo menu, but Zattoo does not find the desired channel.
This means, that the configuration in the channel list is wrong. For predefined list this should not happen. But maybe
Zattoo recently renames a channel without making a redirect. Whatever, if it is happens it is up to you to follow the
sames steps as in [Use case 1: Zattoo menu is not shown](#use-case-1:-zattoo-menu-is not-shown) and correct the Zattoo
channel name.

#### Use case 3: Subscribing all channels of a country channel list.

A side effect of defining your own channel list is, that an import-file for TV-Browser channels are created as well. In
the first two use cases this was not relevant, because the created list should only include entries for already
subscribed channels. But maybe you want to subscribe all German channels for which a Zattoo configuration is available.
Here is what you have to do:

* Go to your plugin settings
* Create your own channel list:
    * Check the `Update own channel list` box
    * Choose a `Source` country
    * Un-check `Update only channels which are subscribed`
    * Press `Apply Update` button

Remarks: For this use case it is not necessary to set the `Country` to `Own channel list`.

By applying the update all channels from your chosen country list are written into the following file:

| Operating system | Customized channel file |  
|---|---|  
|Windows 7+ | C:\Users\[username]\AppData\Roaming\TV-Browser\[version]\java.zattooplugin.CustomChannelProperties.txt |  
|Mac OS X   | /users/[username]/Library/Preferences/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.txt |  
|Linux      | /home/[username]/.config/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.txt |  
|Others     | /home/[username]/.tvbrowser/[version]/java.zattooplugin.CustomChannelProperties.txt |

Now:

* Go to `General settings` `Channels`
* Press `Export/import channels`
* Import java.zattooplugin.CustomChannelProperties.txt from your local settings directory listed above.

### Filters

The filer `Zattoo: Supported Channels` shows only channels which have a valid entry in current channel list (
country or your own list).  
Remarks: If channels were just added and their program is still updating, these channels will not be displayed. This
happens only for new added channels.

### Favorites

When defining favorites you can configure to send the programs to Zattoo (`Pass programs to:`). Thereby, these programs
will be opened when they start.  
Remarks: This information will not be stored when TV-Browser is closed.

## Plugin Settings

---
For detailed configuration please see [Usage](#usage) first.

### General settings

* `Country`  
  The `Country` drop-down list contains three predefined channel configurations for `Germany`, `Switzerland`
  and `Austria`
  . The `Own channel list` is empty by default and can be used for customization.

### Create your own channel list

* `Update own channel list (only once)`  
  If checked, your own channel list will be updated when pressing `Apply updates`. If you press `OK`, `Cancel`
  or `Apply` in the main window, no update will happen.  
  After the update, `Update own channel list (only once)` will be un-checked.

The merge/replace process depends on if `Update only channels which are subscribed` is checked or not:

&#9745; `Update only channels which are subscribed`

* `Replace`  
  All existing entries in your channel list will be removed first.  
  Then all your subscribed channels will be added to your channel list. If a Zattoo channel name is found in the
  selected `Source` list it will be added. Otherwise, the Zattoo channel name will be empty.
* `Merge`  
  Existing entries and entries from the selected `Source` list will be merged.
    * `Add and replace`  
      All your subscribed channels will be added to your channel list. This happens even when there was no Zattoo
      channel name found in selected `Source` list. I.e., if an existing entry has a Zattoo channel name configured it
      might be removed.   
      Existing entries which are not part of your subscribed channels are not changed.
    * `Add only new`  
      All your subscribed channels will be added to your channel list. They will overwrite existing entries only if the
      existing entries do not have a Zattoo channel name configured. Zattoo channel names found in selected `Source`
      list will be added.  
      Existing entries which are not part of your subscribed channels are not changed.

&#9744; `Update only channels which are subscribed`

* `Replace`  
  All existing entries in your channel list will be removed first.  
  Then all channels from the selected `Source` list will be added to your channel list.
* `Merge`  
  Existing entries and entries from the selected `Source` list will be merged.
    * `Add and replace`  
      All channels from the selected `Source` list will be added to your channel list. I.e., existing entry which have
      already a Zattoo channel name configured will be overwritten with new Zattoo channel names.   
      Existing entries which are not part of the selected `Source` list are not changed.
    * `Add only new`  
      All channels from the selected `Source` list will be added to your channel list. They will overwrite existing
      entries only if the existing entries do not have a Zattoo channel name configured. Existing entries which are not
      part of your subscribed channels are not changed.

The `Apply update` button just updates the list below. If you press `Cancel` in the main window the changes are lost.

### Adopt your own channel list

Your channel list is stored in a file. You can edit/save it in the `Adopt your own channel list` area in the TV-Browser
settings panel. If your prefer, you can edit it in a text editor of your choice.

|    Operating system |Customized channel file     |
|------|-----|
|Windows 7+|C:\Users\[username]\AppData\Roaming\TV-Browser\[version]\java.zattooplugin.CustomChannelProperties.prop  |
|Mac OS X|/users/[username]/Library/Preferences/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.prop |
|Linux| /home/[username]/.config/TV-Browser/[version]/java.zattooplugin.CustomChannelProperties.prop    |
|Others| /home/[username]/.tvbrowser/[version]/java.zattooplugin.CustomChannelProperties.prop   |

If there is a format error in your file, only the correct entries will be used. But you can see all entries in the
plugin-settings. If you store the file here the plugin tells you the lines which are not valid.

The `Verify Zattoo names` button looks if given Zattoo channel names exist on the Zattoo channel page:

* [German channel page](https://zattoo.com/de/sender)
* [Swiss channel page](https://zattoo.com/ch/sender)
* [Austrian channel page](https://zattoo.com/at/sender)

Which channel page is used depends on the selected `Source` channel list.  
If the Zattoo channel name is not found it will be highlighted in red. Unfortunately, this is not a guarantee that the
channel name is valid. On the Austrian page there are two channels which are misspelled. Therefore, the
channels `kabel_eins_doku_at` and `mtvgermany` are highlighted in red, even they are correct. The same for the German
page where `deutschewelle_de` is misspelled.

The `Save` button stores your changes in your local settings. If you press `OK` or `Apply` in the main Window, your
changes are saved as well. The `Reset` button can only undo changes which where not saved before.
---

## Acknowledgements

---
Special thanks to Bodo Tasche and Michael Keppler for developing version 1.0. This good work has made my job so much
easier.
  

