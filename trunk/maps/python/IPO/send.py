# send.py - Script for close event of mailbox
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
import string
from time import localtime, strftime, time

mail = CFMail.CFMail()
date = strftime("%a, %d %b %Y %H:%M:%S CEST", localtime(time()))
activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
whoami=Crossfire.WhoAmI()
idlist=[]

inv = whoami.CheckInventory("mailscroll")
if inv != None:
	while inv!=None:
		print("INV:%s" %inv.Name)
		text=string.split(inv.Name)
		if text[0]=='mailscroll' and text[1]=='T:' and text[3]=='F:':
			idlist.append(inv)
			toname=text[2]
			fromname=text[4]
			message='From: %s\nTo: %s\nDate: %s\n\n%s\n' % (fromname, toname, date, inv.Message[:-1])
			activator.Write('mailscroll to '+toname+' sent.')
			mail.send(1, toname, fromname, message)
		elif text[0]=='mailscroll' and text[1]=='F:' and text[3]=='T:':
			idlist.append(inv)
			fromname=text[2]
			toname=text[4]
			message=inv.Message[:-1]+'\n'
			mail.send(1, toname, fromname, message)
		else:
			print "ID: %d"%inv
			print "Name: "+inv.Name
		inv=inv.Below

inv = whoami.CheckInventory("mailwarning")
if inv != None:
	while inv!=None:
		text=string.split(inv.Name)
		if text[0]=='mailwarning' and text[1]=='T:' and text[3]=='F:':
			idlist.append(inv)
			toname=text[2]
			fromname=text[4]
			message='From: %s\nTo: %s\nDate: %s\n\n%s\n' % (fromname, toname, date, inv.Message[:-1])
			activator.Write('mailwarning to '+toname+' sent.')
			mail.send(3, toname, fromname, message)
		elif text[0]=='mailwarning' and text[1]=='F:' and text[3]=='T:':
			idlist.append(inv)
			fromname=text[2]
			toname=text[4]
			message=inv.Message[:-1]+'\n'
			mail.send(3, toname, fromname, message)
		else:
			print "ID: %d"%inv
			print "Name: "+inv.Name
		inv=inv.Below

for inv in idlist:
	inv.Remove()
