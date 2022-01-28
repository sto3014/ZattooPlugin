# Zattoo plugin for TV-Browser

Zattoo Plugin 1.0 was developed by Bodo Tasche and Michael Keppler but after version 1.0.2.0 no longer maintained.
The current plugin is based on their work.

## Features

---

* Opens current programs in Zattoo
* Mark programs to be opened in Zattoo on start.
* New in 1.5: Country channel list for Austria
* New in 1.5: Create your own channel list

## Requirements

---

* TV-Browser 4.2.4+
* Windows 7+ and maxOS is supported.
* Linux should work but wasn't tested.

## Installation

Manuall installation
---

1. Download the file ZattooPlugin.jar from
   [GitHub](https://github.com/sto3014/ZattooPlugin/raw/main/target/ZattooPlugin.jar).
2. Copy the file into the TV-Browser plugin directory:

| Operating System| Path|   
|----|----|   
| Windows 7, 8, 8.1, 10 | C:\User\[user name]\AppData\Roaming\TV-Browser\[TV-Browser version]\plugins|   
| Mac OS X| /Users/[user name]/Library/Application Support/TV-Browser/plugins|  
| Linux| /home/[user name]/.tvbrowser/ (until 4.2) <br /> /home/[user name]/.config/tvbrowser/ (since 4.2.1)|  

See also [Wiki TV-Browser](https://wiki.tvbrowser.org/index.php/Homeverzeichnis)

3. Restart TV-Browser

## Usage

---
Click with the right mouse button on a programm, you get new popup menu:

* For programs which are currently running you get `Open in Zattoo now`  
  ![](./images/OpenInZattoo.png)  
  This menu opens a new page in your web-browser and displays the channel in Zattoo
* For programs which are not started yet you get `Open in Zattoo on start`  
![](./images/OpenInZatooOnStart.png)  
  When the program start, a new page will be opened in your web-browser and displays the channel in Zattoo.
  
If you do not get the expected Zattoo menu, you must use a different channel list. See [Plugin Settings](#plugin_settings) for details.

## Plugin Settings

---
For a program to be opened in Zattoo, the TV-Browser channel must know the Zattoo channel name. This information is
configured in the country channel lists. Therefore, you must choose one country list in the `Country ` combo box. The default
country list is `Germany`. If your Zattoo account is registered in Switzerland or Austria you should select the appropriated 
list.  
![](./images/CountrySettings.png)

But even if one of the three country list fits your country, not all of your subscribed channels may show up the 
Zattoo menu.
So, if your country is not listed at all, or if not all of your channels are valid for Zattoo you must use 
the `Own channel list`. This list is empty by default, and you must set it up. To do so, please follow the 
instructions in the `Create your own channel list` and `Adopt your own channel list` area in the TV-Browser
settings panel.


## Acknowledgements

---
Special thanks to Bodo Tasche and Michael Keppler for developing version 1.0. This good work has made my job 
so much easier.
  

