#! /usr/bin/python
# -*- coding: utf-8 -*-

# This script generates the various files for a guild, and installs them
# to the correct location.
# It should be launched with templates/guilds as working directory.
#
# Files that will be copied are specified in 'filelist.py'.
#
# Parameters are in the form:
#   name region directory entrance x y storage x y
# with:
# - name: guild name, like PoisonedDagger; must not contain spaces
# - region: what region to give the maps of the guild (eg "scorn")
# - directory: where to put maps for this guild, relative to the maps's root directory
# - entrance: map with the entrance to the guild, relative to maps's root directory
# - x and y: coordinates in 'entrance' with the guild entrance
# - storage: map with the storage room's entrance, can be 'same' to be equal to 'entrance'
# - x and y: coordinates in 'storage' for the storage room's entrance

import os, sys
from filelist import filelist
from optparse import OptionParser
parser = OptionParser()
parser.add_option("--local-copy",dest="local_copy",help="puts a copy of generated files in a templates/guild/<guildname>",default=False,action="store_true")
parser.add_option("--no-install-copy",dest="install_copy",help="installs a remote copy to the destination directory.  If false, only configures the files for installation.",default=True,action="store_false")
(options, args) = parser.parse_args(sys.argv)
ToGuild=args[1]
ToRegion=args[2]

# set to 1 to put a copy of generated files in a subdirectory of templates/guild
local_copy = options.local_copy


Ctl=0
if len(sys.argv)>=7:
        ToFolder=args[3]
        ExitPath=args[4]
        ExitX,ExitY=args[5],args[6]
        Ctl=1
        if args[7]=="same":
                args[7]=args[4]
        StorageExit=args[7]
        StorageX,StorageY=args[8],args[9]
        GuildName = ' '.join(args[10:])

if local_copy:
  os.system('mkdir '+ToGuild)

for i in filelist:
        fromfile=open(i, 'r')
        filecontents=fromfile.read()
        fromfile.close()
        filecontents=filecontents.replace('GUILD_TEMPLATE', ToGuild)
        if Ctl==1:
                filecontents=filecontents.replace("region Template","region "+ToRegion).replace("TemplateExit", ExitPath).replace("TemplateHP", ExitX).replace("TemplateSP", ExitY).replace("Exit+1X", StorageX).replace("ExitY",StorageY).replace("ExitX",StorageX).replace("ExitPath",StorageExit)
                filecontents=filecontents.replace("[Template]",GuildName)

        if local_copy:
          tofile=open('./'+ToGuild+'/'+i, 'w')
          tofile.write(filecontents)
          tofile.close()

        if Ctl==1:
                secondtofile=open('../../'+ToFolder+"/"+i,'w')
                secondtofile.write(filecontents)
                secondtofile.close()