# Script for seen event 
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
#Updated to use new path functions in CFPython, and broken into tiny bits by -Todd Mitchell
#
# seen		      - tells player information from logger

import CFPython

import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))

import CFLog

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
x=CFPython.GetXPosition(activator)
y=CFPython.GetYPosition(activator)

log = CFLog.CFLog()
text = string.split(CFPython.WhatIsMessage())

if text[0] == 'seen':
	if len(text)==2:
		if log.exist(text[1]):
			ip, date, count = log.info(text[1])
			CFPython.Say(whoami, "I have seen '%s' joining %d times, last at %s." % (text[1], count, date))
		else:
			CFPython.Say(whoami, "I have never seen '%s' joining" % text[1])
	else:
		CFPython.Say(whoami, 'Usage "seen <friend>"')


else:
	CFPython.Say(whoami, 'You looking for someone?')
