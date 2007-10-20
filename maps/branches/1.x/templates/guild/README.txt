1 Install
2 Bug reporting
3 Credits

1 Install:
All files go within the crossfire server folder
Step 1: Copy the map files to ./share/maps/guilds/<guildname> (<guildname> is not the folder 	name, but rather whatever guild name you are using)
	#(Note:  With the current map and server there are three guilds, they are: 		GUILD_TEMPLATE, PoisonedDagger, and GreenGoblin.  Whatever name you give the folder 	should work, but it will still use GUILD_TEMPLATE.  You can change what guild it uses by 	editing the map files.  There are 8 objects that need to be changed.  The first 5 are on 	the main floor, the next two are in the guildhq and the final one is in hallofjoining.  	It's not that hard, but you will need a map editor.  You find the object that has the 		script, click edit data, and change the line "script options" (which currently is 	"GUILD_TEMPALTE") to the guild you wish to use.  And make sure you use the same one for 	all of them or it won't work.
	1:The guardian sign, its out front of the building on the bridge.
	2:It's the altar in the lower right corner of the map.
	3:It's Jack, the beholder in the main hall.
	4:The message sign, look for it in the upper right corner of the map.
	5:The Big Lever, also in the upper right section.
	6:The oracle in the centre of the room, in the guild HQ,
	7:The guild master's sign straight above the oracle at the wall.
	8:"Load" the switch on the left in the hallofjoining.)  
Step 2: Copy the scripts to ./share/maps/python/guilds
Step 3: Choose where you want the guild and edit the world map, place a guildhall, and set the exit path to /guilds/<guildname>
#(Note 2: (don't worry it's short) you must have python plugin installed for the guild to work)

2 Bug reporting
Any problems installing and making it work or just general bugs you find, please email them to jehloq@yahoo.com, I will be more than willing to help figure out why it isn't working right and correct any bugs you find.

3 Credits
Creators:
majorwoo
Avion
Date:    11/19/2004
Guild work rooms created by: Rick Tanner
Editor: CrossfireEditor
Date:    3/19/2007
Map finised by:  Alestan jehloq@yahoo.com
Date:     8/27/2007

#(Note 3: I tried to keep as much as possible the same as the origional maps, the scripts had some small issues and most of the things you can buy used imperials or simply had "x" listed.  So i went through and replaced the "x"s with real things and swaped the imperials for amberium.)