1 Install
2 Bug reporting
3 Credits

1 Install:
Both the install process and the maps themselves require python, so make sure you have that, version 2.5 or later.
There are 5 files involved in the automatic install, convert.py, convertall.py, GuildList, GuildLocations, and filelist.py. GuildList has the list of guilds for the server.  GuildLocations is what is used by the install script for setting up the maps.  It has 9 columns in it, the first is the name of the guild, no spaces.  The second is the region of the guild, the third is the destination folder for the maps, the fourth is the exit location (usually the world map), the fifth and sixth are the x and y coords within the exit map, the seventh eighth and ninth are the exit location for the storage hall.  If field seven is 'same', then it uses the same exit map as for the guild hall itself.

filelist.py has a list of which files to process for each guild hall.
convert.py takes all the files in filelist.py and customises them to the specific guild hall, then outputs them into a new (or overwrites an existing) folder in the current working directory.  The output folder is the name of the guildhall.
It also writes them into the destination dir.  It takes the arguments from argv, in the same order that they are listed in GuildLocations.  
convertall.py reads the lines from GuildLocations and runs them, line by line, through convert.py

Generally speaking, configuring GuildList and GuildLocations and the running convertall.py is all that is needed.  If that doesn't work, then I suggest looking at how convert.py functions before trying to make a work around.


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
Guild Storage hall created by: Chad
Maps finished by:  Alestan jehloq@yahoo.com
Date:     8/27/2007

