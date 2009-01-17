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

import Crossfire
import CFGuilds

import sys
import string

activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
mymap = activator.Map
gdues=open('share/maps/python/guilds/guildbalance.py', 'r')
balance = gdues.readline (1)
gdues.close()
whoami=Crossfire.WhoAmI()
guildname=Crossfire.ScriptParameters() # 6 is say event
x=1
y=7
x1=18
y1=4
x2=18
y2=6
if (guildname):

    guild = CFGuilds.CFGuild(guildname)
    text = string.split(Crossfire.WhatIsMessage())
    guildrecord = CFGuilds.CFGuildHouses().info(guildname)
    found = 0
    if text[0] == 'dues' or text[0] == 'Dues':

            

            
            

            
            
           

            record = guild.info(activatorname) #see if they are on the board
            if record:
                    #check their status
               if record['Status'] == 'suspended':
                  message = 'You are currently suspended from the guild'
               else:
                 if record['Status'] == 'probation':
                        message = 'You are on probation, you may not recieve dues.'
                 else:
                      if balance >= 1001:
                         balance1 = int (balance) - 1000
                         gdues1=open('share/maps/python/guilds/guildbalance.py', 'w')
                         balance2 = str (balance1)
                         gdues1.write (balance2)
                         gdues1.close()
                         message = 'Paying out now.'
                         activator.Teleport(mymap,int(x2),int(y2))
                         activator.Teleport (mymap,int(x),int(y))

                      else: 
                           if balance >= 101:
                              balance1 = int (balance) - 100 
                              gdues1=open('share/maps/python/guilds/guildbalance.py', 'w')
                              balance2 = str (balance1)
                              gdues1.write (balance2)
                              gdues1.close()
                              message = 'Paying out now.'
                              activator.Teleport(mymap,int(x1),int(y1))
                              activator.Teleport (mymap, int (x),int(y))
                           else:
                             message = 'Insufficient funds, try again later.'
            else:
                     message = 'You are not in the guild, %s.  Leave now!' %activatorname


    elif text[0] == 'buy' or text[0] == 'Buy':
        if guildrecord['Status'] == 'inactive':
            in_guild = CFGuilds.SearchGuilds(activatorname)
            if in_guild == 0:
                x = 30
                y = 22
                message = "Proceed, but know ye that three are required to found a guild and the cost is high"
                activator.Teleport(mymap,int(x),int(y)) #teleport them
            else:
                message = "Sorry you already belong to the %s guild.  You must quit that guild before founding your own." %in_guild
        else:
            message = 'This guild is already owned.'
    else:
        message = 'This is the entry to the great %s guild.  Enter or begone!' %guildname

else:
    message = 'Guild Guardian Error, please notify a DM'

whoami.Say(message)