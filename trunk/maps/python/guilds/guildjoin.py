# guildjoin.py - operates perilous chair for Hall of Joining in crossfire guilds
#
# Copyright (C) 2004 Todd Mitchell
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# The author can be reached via e-mail at temitchell@sourceforge.net
#

import CFPython
import CFGuilds

def find_player(object):
    while (CFPython.GetType(object) != 1) : #1 is type 'Player'
        object = CFPython.GetPreviousObject(object)
        if not object:
            return 0
    return object
   
activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
map = CFPython.GetMap(activator)
whoami=CFPython.WhoAmI()

guildname=CFPython.GetEventOptions(whoami,1) # 1 is 'apply' event

if (guildname):
    guild = CFGuilds.CFGuild(guildname)
    #find players by coords
    ob=CFPython.GetFirstObjectOnSquare(map,9,16)
    player = find_player(ob)
    if player: # look for player
        charname=CFPython.GetName(player)
        in_guild = CFGuilds.SearchGuilds(charname)
        if in_guild == 0:
            if guild.info(charname):
                #already a member
                message = '%s is already a member.' %charname    
            else:
                guild.add_member(charname, 'Initiate')
                message = 'Added %s to the guild' %charname
        else:
            message = 'It appears that %s is already a member of the %s guild' %(charname, in_guild)
    else:
        message = 'No one is in the chair!'
else:
    print 'Guild Join Error: %s' %(guildname)
    message = 'Guild Join Error, please notify a DM'
    
CFPython.Say(whoami, message)
    
