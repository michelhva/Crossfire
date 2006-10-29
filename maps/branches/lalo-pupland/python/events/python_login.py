# python_login.py - handler for global login event
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

import Crossfire
import CFMail
import CFLog

activator = Crossfire.WhoIsActivator()
name = activator.Name
ip = Crossfire.WhatIsMessage()

log = CFLog.CFLog()

if log.info(name):
    log.login_update(name, ip)
else:
    log.create(name)

mail = CFMail.CFMail()
total = mail.countmail(name)
if total > 0:
	activator.Write('You have some mail waiting for you')
else:
	activator.Write('No mail...')

