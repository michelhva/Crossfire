# Script for Lursendis the gourmet (/wolfsburg/lursendis).
# Idea courtesy Yann Chachkoff.
#
# The script assumes you have:
# * Lursendis
# * a place where the player will drop the special omelet
#
# Copyright 2007 Nicolas Weeger
# Released as GPL
#
# This script is supposed to be called for the time event of Lursendis.

import Crossfire
import CFMove
import random

key_status = 'gourmet_status'
st_getting = 'getting'
st_eating = 'eating'
key_eating_step = 'eating_step'
plate_x = 2
plate_y = 6

banquet_path = '/python/items/banquet.py'
banquet_archetype = 'tome'
event_archetype = 'event_apply'

color = Crossfire.MessageFlag.NDI_GREEN	# color to display messages

def check_plate():
	obj = whoami.Map.ObjectAt(plate_x, plate_y)
	while obj != None:
		if obj.NamePl == 'Farnass\'s Special Caramels' and obj.Slaying == 'Farnass\'s Special Caramel':
			if whoami.ReadKey(key_status) == st_getting:
				whoami.Map.Print('%s grabs %s and starts eating with an obvious pleasure.'%(whoami.Name, obj.Name))
				obj.Quantity = obj.Quantity - 1
				whoami.WriteKey(key_status, st_eating, 1)
				whoami.WriteKey(key_eating_step, str(random.randint(5, 10)), 1)
				Crossfire.SetReturnValue(1)
				return
			
			whoami.Say('Oh! Could this be...')
			whoami.WriteKey(key_status, st_getting, 1)
			Crossfire.SetReturnValue(1)
			return

		obj = obj.Above
	
	if whoami.ReadKey(key_status) == st_getting:
		# we were on the spot, but no more omelet...
		whoami.WriteKey(key_status, '', 1)

def create_book():
	book = whoami.Map.CreateObject(banquet_archetype, whoami.X, whoami.Y)
	book.Name = 'Unforgettable Banquet of %s'%whoami.Name
	book.NamePl = 'Unforgettable Banquets of %s'%whoami.Name
	event = book.CreateObject(event_archetype)
	event.Slaying = banquet_path
	event.Title = Crossfire.WhatIsEvent().Title

def move_gourmet():
	st = whoami.ReadKey(key_status)
	if st == st_getting:
		move = CFMove.get_object_to(whoami, plate_x, plate_y)
		if move == 0:
			check_plate()
			return
		elif move == 2:
			whoami.Say('Get outta my way!')
		Crossfire.SetReturnValue(1)
		return
	elif st == st_eating:
		step = int(whoami.ReadKey(key_eating_step)) - 1
		if step == 0:
			whoami.WriteKey(key_eating_step, '', 1)
			whoami.WriteKey(key_status, '', 1)
			whoami.Say('Now that\'s what I call a caramel! Thank you very much!')
			whoami.Say('Here, take this as a token of my gratitude.')
			create_book()
			return
		whoami.WriteKey(key_eating_step, str(step), 1)
		Crossfire.SetReturnValue(1)
		return
	
	check_plate()

whoami = Crossfire.WhoAmI()
move_gourmet()
