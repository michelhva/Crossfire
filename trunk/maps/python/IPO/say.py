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
priceBag = 5
pricePackage = 50
priceCarton = 200
priceFactor = 50	# platinum to silver conversion

# Map storage
storage_map = '/planes/IPO_storage'
storage_x = 2
storage_y = 2

# Post office sack name (one word without space)
sackName = 'package'

import CFPython
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
		message = 'How can I help you ? Here is a quick list of commands:\n\n- pen   (%s platinum)\n- literacy    (%s platinum)\n- mailscroll <friend>   (%s platinum)\n- bag <friend>   (%s platinum)\n- package <friend>   (%s platinum)\n- carton <friend>   (%s platinum)\n- send <friend>\n- receive'%(priceWritingPen,priceScrollOfLiteracy,priceMailScroll,priceBag,pricePackage,priceCarton)
		CFPython.Say(whoami,message)

elif text[0] == 'pen':
	if (CFPython.PayAmount(activator, priceWritingPen*priceFactor)):
		CFPython.Say(whoami, 'Here is your IPO Writing Pen')
		id = CFPython.CreateObject('writing pen', (x, y))
		CFPython.SetName(id, 'IPO Writing Pen')
		CFPython.SetValue(id, 0)
	else:
		CFPython.Say(whoami, 'You need %s platinum for an IPO Writing Pen'%priceWritingPen)

elif text[0] == 'literacy':
	if (CFPython.PayAmount(activator,priceScrollOfLiteracy*priceFactor)):
        	CFPython.Say(whoami, 'Here is your IPO Scroll of Literacy')
        	id = CFPython.CreateObject('scroll of literacy', (x, y))
		CFPython.SetName(id, 'IPO Scroll of Literacy')
		CFPython.SetValue(id, 0)
	else:
		CFPython.Say(whoami, 'You need %s platinum for an IPO Scroll of Literacy'%priceScrollOfLiteracy)


elif text[0] == 'mailscroll':
	if len(text)==2:
		if log.info(text[1]):
			if (CFPython.PayAmount(activator, priceMailScroll*priceFactor)):
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
			if log.info(text[1]):
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

elif text[0] == 'bag' or text[0] == 'package' or text[0] == 'carton':
	if (len(text) == 2):
		if log.info(text[1]):
			if text[0] == 'bag':
				price = priceBag
				max = 5000
				item = 'r_sack'
			elif text[0] == 'package':
				price = pricePackage
				max = 50000
				item = 'r_sack'
			else:
				price = priceCarton
				max = 100000
				item = 'r_sack'

			if ( CFPython.PayAmount(activator, price*priceFactor) ):
				box = CFPython.CreateObject(item, (x, y))
				CFPython.SetName(box, sackName + ' T: ' + text[1] + ' F: ' + activatorname)
				CFPython.SetWeightLimit(box, max)
				CFPython.SetStrength(box, 0)
				CFPython.Say(whoami, 'Here is your %s'%text[0])
				CFPython.InsertObjectInside(box, activator)
			else:
				CFPython.Say(whoami, 'You need %s platinum to buy a %s'%( price, text[0] ) )

		else:
			CFPython.Say(whoami, 'I don\'t know any %s'%text[1])

	else:
		CFPython.Say(whoami, 'Send a %s to who?'%text[0] )

elif text[0] == 'send':
	if len(text) == 2:
		inv = CFPython.CheckInventory(activator,sackName)
		map = 0
		if inv != 0:
			while inv != 0:
				next = CFPython.GetNextObject(inv)
				text2=string.split(CFPython.GetName(inv))
				if text2[0]==sackName and text2[1]=='T:' and text2[3]=='F:' and text2[2] == text[1]:
					map = CFPython.ReadyMap(storage_map)
					if map == 0:
						CFPython.Say(whoami, 'I\'m sorry but the post can\'t send your package now.')
					else:
						CFPython.Teleport(inv, map, storage_x, storage_y)
						CFPython.Say(whoami, 'Package sent')
				inv = next
		else:
			CFPython.Say(whoami, 'No package to send.')
	else:
		CFPython.Say(whoami, 'Send packages to who?')
elif text[0] == 'receive':
	map = CFPython.ReadyMap(storage_map)
	if ( map != 0 ):
		item = CFPython.GetObjectAt(map, storage_x, storage_y)
		count = 0
		while item != 0:
			previous = CFPython.GetPreviousObject(item)
			text2 = string.split(CFPython.GetName(item))
			if ( len(text2) == 5 ) and ( text2[0] == sackName ) and ( text2[2] == activatorname ):
				CFPython.InsertObjectInside(item,activator)
				count = count + 1
			item = previous
		if ( count == 0 ):
			CFPython.Say(whoami, 'No package for you, sorry.')
		else:
			CFPython.Say(whoami, 'Here you go.')
	else:
		CFPython.Say(whoami, 'Sorry, our package delivery service is currently in strike. Please come back later.')
else:
	CFPython.Say(whoami, 'Do you need help?')
