# Script for say event of IPO message board
#
# Copyright (C) 2002 Joris Bontje
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
# The author can be reached via e-mail at jbontje@suespammers.org
#
# help                - gives information about usage
#
#Updated to use new path functions in CFPython -Todd Mitchell

import CFPython
import CFBoard

import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))
import string

board = CFBoard.CFBoard()

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()

boardname=CFPython.GetEventOptions(whoami,6) # 6 is say event
print "Activated %s" %boardname

if (boardname):

	text = string.split(CFPython.WhatIsMessage(), ' ', 1)

	if text[0] == 'help' or text[0] == 'yes':
		message='Help for %s\nList of commands:\n\n- list\n- write <message>\n- remove <id>\n'%boardname
		CFPython.Write(message, activator)

	elif text[0] == 'write':
		if len(text)==2:
			board.write(boardname, activatorname, text[1])
			CFPython.Write('Added to %s'%boardname, activator)
		else:
			CFPython.Write('Usage "write <text>"', activator)

	elif text[0] == 'list':
		total = board.countmsg(boardname)
		if total > 0:
			CFPython.Write('Content of %s:'%boardname, activator)
			elements = board.list(boardname)
			element = []
			id = 1
			for element in elements:
				author, message = element
				CFPython.Write('<%d> (%s) %s'%(id,author,message), activator)
				id=id+1
		else:
			CFPython.Write('%s is empty'%boardname, activator)

	elif text[0] == 'remove':
		if len(text)==2:
			if board.getauthor(boardname,int(text[1]))==activatorname or CFPython.IsDungeonMaster(activator):
				if board.delete(boardname, int(text[1])):
					CFPython.Write('Removed from %s'%boardname, activator)
				else:
					CFPython.Write('Doesn\'t exist on %s'%boardname, activator)
			else:
				CFPython.Write('Access denied', activator)
		else:
			CFPython.Write('Usage "remove <id>"', activator)

	else:
		CFPython.Write('Do you need help?', activator)

else:
	CFPython.Write('Board Error', activator)
