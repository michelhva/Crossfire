# Script for say event of guild member board
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
import CFLog

import sys
import string

activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
whoami=Crossfire.WhoAmI()
isDM=activator.DungeonMaster

log=CFLog.CFLog()
guildname=Crossfire.ScriptParameters() # 6 is say event
print "Activated %s" %guildname

if (guildname):
    guild = CFGuilds.CFGuild(guildname)
    guildhouse = CFGuilds.CFGuildHouses()
    text = string.split(Crossfire.WhatIsMessage())

    if guild.info(activatorname) == 0 and isDM == 0:
	    message = 'You don\'t belong to this guild!'
    elif text[0] == 'help' or text[0] == 'yes':
        if isDM:
            message = '\nList of commands:\n-list\n-add <name>\n-remove <member>\n-info <member>\n-promote <member>\n-demote <member>\n-status <member> <status>\n-guildstatus <status>'
        else:
            message='\nList of commands:\n-list\n-remove <member>\n-info <member>\n-promote <member>\n-demote <member>\n-status <member> <status>'

    elif text[0] == 'info':
        if len(text)==2:
            record = guild.info(text[1])
            if record:
                message = '%s' %record
            else:
                message =  '%s is not a member' %text[1]
        else:
            message = 'Usage "info <member_name>"'

    elif text[0] == 'remove':
        if len(text)==2:
            if guild.info(text[1]):
                message = 'Removed %s from the guild' %text[1]
                #delete them
                guild.remove_member(text[1])
            else:
                #if we didn't find them on the board
                message = '%s was not a member' %text[1]
        else:
            message = 'Usage "remove <member_name>"'

    elif text[0] == 'list':
        list = guild.list_members()
        for member in list:
            activator.Write(member)
        message = 'Total members = ' + str(len(list))

    elif text[0] == 'promote':
        if len(text)==2:
            record = guild.info(text[1])
            if record:
                if guild.promote_member(text[1]):
                    record = guild.info(text[1]) #refresh record
                    message = '%s promoted to %s' %(text[1], record['Rank'])
                else:
                    message = 'You cannot promote %s' %text[1]
            else:
                message = '%s is not a member' %text[1]
        else:
            message = 'Usage "promote <member_name>"'

    elif text[0] == 'demote':
        if len(text)==2:
            record = guild.info(text[1])
            if record:
                if guild.demote_member(text[1]):
                    record = guild.info(text[1]) #refresh record
                    message = '%s demoted to %s' %(text[1], record['Rank'])
                else:
                    message = 'You cannot demote %s' %text[1]
            else:
                message = '%s is not a member' %text[1]
        else:
            message = 'Usage "demote <member_name>"'

    elif text[0] == 'status':
        if len(text)==3:
            record = guild.info(text[1])
            if record:
                if guild.change_status(text[1],text[2]):
                    record = guild.info(text[1]) #refresh record
                    message = '%s now has status of %s' %(text[1], record['Status'])
                else:
                    message = '%s is not a valid status' %text[2]
            else:
                message = '%s is not a member' %text[1]
        else:
            message = 'Usage "status <member_name> <status>\n%s"' %str(guild.status)

# DM commands
    #add user directly
    elif text[0] == 'add' and isDM:
        if len(text)==2:
            #check if they are a player
            if log.info(text[1]):
                #see if they are on the board already
                if guild.info(text[1]):
                    #already a member
                    message = '%s is already a member.' %text[1]
                else:
                    guild.add_member(text[1], 'Initiate')
                    message = 'Added %s to the guild' %text[1]
            else:
                message = 'Sorry, I don\'t know any %s' %text[1]
        else:
            message = 'Usage "add <membername>"'

    #change guild status
    elif text[0] == 'guildstatus' and isDM:
        if len(text)==2:
            record = guildhouse.info(guildname)
            if record:
                if guildhouse.change_status(guildname,text[1]):
                    record = guildhouse.info(text[1]) #refresh record
                    message = '%s now has status of %s' %(guildname, record['Status'])
                else:
                    message = '%s is not a valid status' %text[1]
            else:
                message = '%s is not a guild' %guildname
        else:
            message = 'Usage "guildstatus <status>\n%s"' %str(guildhouse.status)

    else:
        message = 'What did you need?'

else:
    message = 'Board Error'
whoami.Say(message)

