# Script for entering guild houses
#
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
# authors: majorwoo josh@woosworld.net, Avion temitchell@sourceforge.net

import CFPython
import CFGuilds

import sys
import string

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
mymap = CFPython.GetMap(activator)
x=15
y=29
whoami=CFPython.WhoAmI()
guildname=CFPython.GetEventOptions(whoami,6) # 6 is say event

if (guildname):

    guild = CFGuilds.CFGuild(guildname)
    text = string.split(CFPython.WhatIsMessage())
    guildrecord = CFGuilds.CFGuildHouses().info(guildname)
    found = 0 
    if text[0] == 'enter' or text[0] == 'Enter':

            if guildrecord['Status'] == 'inactive':
                message = 'This guild is currently inactive and available to be bought.'

            elif guildrecord['Status'] == 'suspended':
                message = 'This guild is currently under suspension.\nPlease see a DM for more information'

            else:
                if guildrecord['Status'] == 'probation':
                    CFPython.Write('This guild is currently under probation.\nPlease see a DM for more information', activator)

                record = guild.info(activatorname) #see if they are on the board
                if record:
                    #check their status
                    if record['Status'] == 'suspended':
                        message = 'You are currently suspended from the guild'
                    elif record['Status'] == 'probation':
                        message = 'Granted, but you are on probation'
                        y=22
                    else:
                        message = 'Entry granted for %s' %activatorname
                        y=22   
                else:
                    message = 'You try my patience %s.  BEGONE!' %activatorname
                CFPython.Teleport(activator,mymap,int(x),int(y)) #teleport them

    elif text[0] == 'buy' or text[0] == 'Buy':
        if guildrecord['Status'] == 'inactive':
            in_guild = CFGuilds.SearchGuilds(activatorname)
            if in_guild == 0:
                x = 30
                y = 22
                message = "Proceed, but know ye that three are required to found a guild and the cost is high"
                CFPython.Teleport(activator,mymap,int(x),int(y)) #teleport them
            else:
                message = "Sorry you already belong to the %s guild.  You must quit that guild before founding your own." %in_guild
        else:
            message = 'This guild is already owned.'
    else:
        message = 'This is the entry to the great %s guild.  Enter or begone!' %guildname
        
else:
    message = 'Guild Guardian Error, please notify a DM'
    
CFPython.Say(whoami,message)

