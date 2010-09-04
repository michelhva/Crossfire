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
ToGuild=sys.argv[1]
ToRegion=sys.argv[2]

# set to 1 to put a copy of generated files in a subdirectory of templates/guild
local_copy = 0


Ctl=0
if len(sys.argv)>=7:
        ToFolder=sys.argv[3]
        ExitPath=sys.argv[4]
        ExitX,ExitY=sys.argv[5],sys.argv[6]
        Ctl=1
        if sys.argv[7]=="same":
                sys.argv[7]=sys.argv[4]
        StorageExit=sys.argv[7]
        StorageX,StorageY=sys.argv[8:]

if local_copy:
  os.system('mkdir '+ToGuild)

for i in filelist:
        fromfile=open(i, 'r')
        filecontents=fromfile.read()
        fromfile.close()
        filecontents=filecontents.replace('GUILD_TEMPLATE', ToGuild)
        if Ctl==1:
                filecontents=filecontents.replace("region Template","region "+ToRegion).replace("TemplateExit", ExitPath).replace("TemplateHP", ExitX).replace("TemplateSP", ExitY).replace("Exit+1X", StorageX).replace("ExitY",StorageY).replace("ExitX",StorageX).replace("ExitPath",StorageExit)

        if local_copy:
          tofile=open('./'+ToGuild+'/'+i, 'w')
          tofile.write(filecontents)
          tofile.close()

        if Ctl==1:
                secondtofile=open('../../'+ToFolder+"/"+i,'w')
                secondtofile.write(filecontents)
                secondtofile.close()