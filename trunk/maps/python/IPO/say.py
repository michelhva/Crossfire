# Script for say event of IPO employees 
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
#Updated to use new path functions in CFPython -Todd Mitchell
#
# help                - gives information about usage
# pen                 - drops IPO Writing Pen on the floor
# literacy            - drops IPO Scroll of Literacy on the floor
# mailscroll <friend> - drops mailscroll to <friend> on the floor
# mailwarning <foo>   - drops mailwarning to <foo> on the floor

# Constant price values
priceWritingPen=100
priceScrollOfLiteracy=5000
priceMailScroll=5

import CFPython

import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))

import string
import CFLog

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
x=CFPython.GetXPosition(activator)
y=CFPython.GetYPosition(activator)

log = CFLog.CFLog()
text = string.split(CFPython.WhatIsMessage())

if text[0] == 'help' or text[0] == 'yes':
		message = 'How can I help you ? Here is a quick list of commands:\n\n- pen   (%s platinum)\n- literacy    (%s platinum)\n- mailscroll <friend>   (%s platinum)\n- seen <friend>   (free)\n'%(priceWritingPen,priceScrollOfLiteracy,priceMailScroll)
		CFPython.Say(whoami,message)

elif text[0] == 'pen':
	if (CFPython.PayAmount(activator, priceWritingPen*50)):
		CFPython.Say(whoami, 'Here is your IPO Writing Pen')
		id = CFPython.CreateObject('writing pen', (x, y))
		CFPython.SetName(id, 'IPO Writing Pen')
		CFPython.SetValue(id, 0)
	else:
		CFPython.Say(whoami, 'You need %s platinum for an IPO Writing Pen'%priceWritingPen)

elif text[0] == 'literacy':
	if (CFPython.PayAmount(activator,priceScrollOfLiteracy*50)):
        	CFPython.Say(whoami, 'Here is your IPO Scroll of Literacy')
        	id = CFPython.CreateObject('scroll of literacy', (x, y))
		CFPython.SetName(id, 'IPO Scroll of Literacy')
		CFPython.SetValue(id, 0)
	else:
		CFPython.Say(whoami, 'You need %s platinum for an IPO Scroll of Literacy'%priceScrollOfLiteracy)


elif text[0] == 'mailscroll':
	if len(text)==2:
		if log.exist(text[1]):
			if (CFPython.PayAmount(activator, priceMailScroll*50)):
				CFPython.Say(whoami, 'Here is your mailscroll')
				id = CFPython.CreateObject('scroll', (x, y))
				CFPython.SetName(id, 'mailscroll T: '+text[1]+' F: '+ activatorname)
				CFPython.SetValue(id, 0)
			else:
				CFPython.Say(whoami, 'You need %s platinum for a mailscroll'%priceMailScroll)
		else:
			CFPython.Say(whoami, 'I don\'t know any %s'%text[1])

	else:
		CFPython.Say(whoami, 'Usage "mailscroll <friend>"')


elif text[0] == 'mailwarning':
	if (CFPython.IsDungeonMaster(activator)):
		if len(text)==2:
			if log.exist(text[1]):
				CFPython.Say(whoami, 'Here is your mailwarning')
				id = CFPython.CreateObject('diploma', (x, y))
				CFPython.SetName(id, 'mailwarning T: '+text[1]+' F: '+ activatorname)
				CFPython.SetValue(id, 0)
			else:
				CFPython.Say(whoami, 'I don\'t know any %s'%text[1])

		else:
			CFPython.Say(whoami, 'Usage "mailwarning <foo>"')
	else:
		CFPython.Say(whoami, 'You need to be DM to be able to use this command')


elif text[0] == 'seen':
	if len(text)==2:
		if log.exist(text[1]):
			ip, date, count = log.info(text[1])
			CFPython.Say(whoami, "I have seen '%s' joining %d times, last at %s." % (text[1], count, date))
		else:
			CFPython.Say(whoami, "I have never seen '%s' joining" % text[1])
	else:
		CFPython.Say(whoami, 'Usage "seen <friend>"')

else:
	CFPython.Say(whoami, 'Do you need help?')
