1 Install
2 Bug reporting
3 Credits

1 Install:
All files go within the crossfire server folder

Step 1: Copy the map files to ./share/maps/guilds/<guildname> 
(<guildname> is not the folder name, but rather whatever guild name you are using)

* With the current map and server there are three guilds, they are: GUILD_TEMPLATE, PoisonedDagger, and GreenGoblin.  
* Whatever name you give the folder should work, but it will still use GUILD_TEMPLATE.  
* You can change what guild it uses by editing the map files.  

Modify Map Files:

There are 6 objects that need to be changed, or 11 objects if you want to use the optional Python based Guild Storage hall.  The first three are on the main floor, the next two are in the guild_hq and the final one is in hallofjoining. Withe the Storage Hall, three objects are found on the main floor and the last two are in the basement.

It's not that hard, but you will need a map editor.  You find the object that has the script, click edit data, and change the line "script options" (which currently is "GUILD_TEMPALTE") to the guild you wish to use.  And make sure you use the same one for all of them or it won't work.

Here's a quick HOWTO for using the map editor to make these changes:

1.) Left click on the object (ex: sign) so it's selected
2.) Click on the Scripts tab
3.) Click Edit Data button
4.) Change the line "script options" (which currently is "GUILD_TEMPALTE") to the guild you wish to use.  And make sure you use the same one for all of them or it won't work.

mainfloor map:
1:The guardian, its a sign out front of the building *under* the bridge. (x15, y25)
2:The message sign, look for it in the upper right corner of the map. (x31, y7)
3:The Big Lever, also in the upper right section. (x15, y16)

guild hq map:
4:The oracle in the centre of the room, in the guild HQ. (x7, y7)
5:The guild master's sign straight above the oracle at the wall. (x7, y1)
(Notice, GUILD_TEMPLATE_GM_board - leave the _GM_board in place)

hallofjoining map:
6:One of the switches, called "Load" and located on the left, in the hallofjoining. (x8, y11)

storage_hall map:
7:The guardian, its a sign out front of the storage hall and *under* the bridge. (x10, y27)
8:"Check Inv" object found under the first entrance gate. (x10, y27)
9:"Check Inv" object found under the middle gate at the top of the map. (x10, y6)

storage_hall.0: (basement)
10:"Check Inv" object found under the stairs. (x3, y1)
11:"Check Inv" object found under the middle gate at the bottom of the map. (x3, y6)

Or, if you prefer a text editor or want to double check your changes..

mainfloor map:
* Line 4637
* Line 9420
* Line 9525

guildhq map:
* Line 1177 (GUILD_TEMPLATE_GM_board - leave the _GM_board in place)
* Line 1224

hallofjoining map:
* Line 1857



IMPORTANT special cases:

GuildList:
* Either replace the GUILD_TEMPLATE text or add the new guild to the list
(This is a text file)

All Map Files:
In all the guild map files, update the Region setting (region Template), it's found at the fourth (4th) line in each file. In the map editor, click on Map -> Map Properties (or hit control-m) as another way to update the Region seting.
See the "regions" file found at ./share/maps/ for more information.
  

Step 2: 
Copy the scripts to ./share/maps/python/guilds

Step 3: 
Choose where you want the guild and edit the world map, place a guildhall ot that location, and set the exit path to /path/to/guilds/<guildname>; edit the mainfloor map exit (x15, y29 - set to /Edit/This/Exit/Path in the template) back to the world map as well.  Otherwise, players may not be able to enter the guild hall map or they may exit out to some other location in the game.

2 Bug reporting:
Any problems installing and making it work or just general bugs you find, please email them to jehloq@yahoo.com, I will be more than willing to help figure out why it isn't working right and correct any bugs you find.

3 Credits
Creators:
majorwoo
Avion
Date:    11/19/2004
Guild work rooms created by: Rick Tanner
Editor: CrossfireEditor
Date:    3/19/2007
Map finished by:  Alestan jehloq@yahoo.com
Date:     8/27/2007

