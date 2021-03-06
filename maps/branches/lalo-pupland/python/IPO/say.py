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

# Constant price values (all prices in platinum coins)
priceWritingPen = 100
priceScrollOfLiteracy = 5000
priceMailScroll = 5
priceBag = 5
pricePackage = 50
priceCarton = 200
priceFactor = 50	# platinum to silver conversion

# Map storage
storage_map = '/planes/IPO_storage'
storage_x = 2
storage_y = 2

# Post office sack name (one word without space)
sackNames = [ 'IPO-bag', 'IPO-package', 'IPO-carton' ]

import Crossfire
import string
import CFLog

activator = Crossfire.WhoIsActivator()
activatorname = activator.Name
whoami = Crossfire.WhoAmI()
x = activator.X
y = activator.Y

log = CFLog.CFLog()
text = string.split(Crossfire.WhatIsMessage())

if text[0] == 'help' or text[0] == 'yes':
	# split the help message in two parts to prevent the server from truncating it.
	message = 'How can I help you?\nHere is a quick list of commands I understand:\n\n- pen   (%s platinum)\n- literacy    (%s platinum)\n- mailscroll <friend>   (%s platinum)\n- bag <friend>   (%s platinum)\n- package <friend>   (%s platinum)'%(priceWritingPen, priceScrollOfLiteracy, priceMailScroll, priceBag, pricePackage)
	whoami.Say(message)

	message = '- carton <friend>   (%s platinum)\n- send <friend>\n- receive'%(priceCarton)
	if activator.DungeonMaster:
		message += '\n- mailwarning <player>'
	whoami.Say(message)


elif text[0] == 'pen':
	if activator.PayAmount(priceWritingPen*priceFactor):
		whoami.Say('Here is your IPO Writing Pen')
		id = activator.Map.CreateObject('writing pen', x, y)
		id.Name = 'IPO Writing Pen'
		id.Value = 0
	else:
		whoami.Say('You need %s platinum for an IPO Writing Pen'%priceWritingPen)


elif text[0] == 'literacy':
	if activator.PayAmount(priceScrollOfLiteracy*priceFactor):
        	whoami.Say('Here is your IPO Scroll of Literacy')
        	id = activator.Map.CreateObject('scroll of literacy', x, y)
		id.Name = 'IPO Scroll of Literacy'
		id.NamePl = 'IPO Scrolls of Literacy'
		id.Value = 0
	else:
		whoami.Say('You need %s platinum for an IPO Scroll of Literacy'%priceScrollOfLiteracy)


elif text[0] == 'mailscroll':
	if len(text) == 2:
		if log.info(text[1]):
			if activator.PayAmount(priceMailScroll*priceFactor):
				whoami.Say('Here is your mailscroll')
				id = activator.Map.CreateObject('scroll', x, y)
				id.Name = 'mailscroll T: '+text[1]+' F: '+activatorname
				id.NamePl = 'mailscrolls T: '+text[1]+' F: '+activatorname
				id.Value = 0
			else:
				whoami.Say('You need %s platinum for a mailscroll'%priceMailScroll)
		else:
			whoami.Say('I don\'t know %s'%text[1])

	else:
		whoami.Say('Usage "mailscroll <friend>"')


elif text[0] == 'mailwarning':
	if activator.DungeonMaster:
		if len(text) == 2:
			if log.info(text[1]):
				whoami.Say('Here is your mailwarning')
				id = activator.Map.CreateObject('diploma', x, y)
				id.Name = 'mailwarning T: '+text[1]+' F: '+activatorname
				id.NamePl = 'mailwarnings T: '+text[1]+' F: '+activatorname
				id.Value = 0
			else:
				whoami.Say('I don\'t know any %s'%text[1])

		else:
			whoami.Say('Usage "mailwarning <player>"')
	else:
		whoami.Say('You need to be DM to be able to use this command')


elif text[0] == 'bag' or text[0] == 'package' or text[0] == 'carton':
	if len(text) == 2:
		if log.info(text[1]):
			if text[0] == 'bag':
				price = priceBag
				max = 5000
				item = 'bag'
			elif text[0] == 'package':
				price = pricePackage
				max = 50000
				item = 'package'
			else:
				price = priceCarton
				max = 100000
				item = 'carton_box_1'

			if activator.PayAmount(price*priceFactor):
				box = activator.CreateObject(item)
				box.Name = 'IPO-'+text[0]+' T: '+text[1]+' F: '+activatorname
				box.WeightLimit = max
				box.Str = 0
				whoami.Say('Here is your %s'%text[0])
			else:
				whoami.Say('You need %s platinum to buy a %s'%(price, text[0]))

		else:
			whoami.Say('I don\'t know any %s'%text[1])

	else:
		whoami.Say('Send a %s to who?'%text[0] )


elif text[0] == 'send':
	if len(text) == 2:
		count = 0
		for sackName in sackNames:
			inv = activator.CheckInventory(sackName)
			while inv:
				next = inv.Below
				text2 = string.split(inv.Name)
				if len(text2) == 5 and text2[0] == sackName and text2[1] == 'T:' and text2[3] == 'F:' and text2[2] == text[1]:
					map = Crossfire.ReadyMap(storage_map)
					if map:
						# rename container to prevent sending it multiple times
						inv.Name = sackName+' F: '+text2[4]+' T: '+text2[2]

						inv.Teleport(map, storage_x, storage_y)
						count = count+1
					else:
						whoami.Say('I\'m sorry but the post can\'t send your package now.')
				inv = next
		if count <= 0:
			whoami.Say('No package to send.')
		elif count == 1:
			whoami.Say('Package sent.')
		else:
			whoami.Say('%d packages sent.'%count)
	else:
		whoami.Say('Send packages to who?')


elif text[0] == 'receive':
	map = Crossfire.ReadyMap(storage_map)
	if map:
		count = 0
		for sackName in sackNames:
			item = map.ObjectAt(storage_x, storage_y)
			while item:
				previous = item.Above
				text2 = string.split(item.Name)
				if len(text2) == 5 and text2[0] == sackName and text2[4] == activatorname:
					item.Name = item.Name+' (used)'
					item.InsertInto(activator)
					count = count+1
				item = previous
		if count <= 0:
			whoami.Say('No package for you, sorry.')
		else:
			whoami.Say('Here you go.')
	else:
		whoami.Say('Sorry, our package delivery service is currently in strike. Please come back later.')


else:
	whoami.Say('Do you need help?')
Crossfire.SetReturnValue(1)
