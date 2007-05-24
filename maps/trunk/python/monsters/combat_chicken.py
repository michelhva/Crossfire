# Script for the Combat Chicken for Sentrio's farmhouse (/lake_country/sentrio_farmhouse).
#
# Copyright 2007 Nicolas Weeger
# Released as GPL
#
# This script is supposed to be called for the time event.

import Crossfire
import random

key_target = 'chicken_target'		# where the chicken is trying to go
key_food = 'chicken_food'		# currently eaten food
key_attacked = 'chicken_attacked'	# if set, chicken has normal monster behaviour - so it reacts when attacked
stay_on_floor = 'small_stones'		# what ground it'll stay on
# what the chicken will eat, and food increase
eat = { 'orc\'s livers' : 5, 'orc\'s hearts' : 6, 'goblin\'s livers' : 1, 'goblin\'s hearts' : 2 }

# those should be in some common library
dir_x = [  0, 0, 1, 1, 1, 0, -1, -1, -1 ]
dir_y = [ 0, -1, -1, 0, 1, 1, 1, 0, -1 ]

# this should be in some common library
def coordinates_to_dir(x, y):
	q = 0
	if(y == 0):
		q = -300 * x;
	else:
		q = int(x * 100 / y);
	if(y>0):
		if(q < -242):
			return 3
		if (q < -41):
			return 2
		if (q < 41):
			return 1
		if (q < 242):
			return 8 ;
		return 7
	
	if (q < -242):
		return 7
	if (q < -41):
		return 6
	if (q < 41):
		return 5
	if (q < 242):
		return 4
	return 3

# returns floor with specific name
def has_floor(x, y, name):
	obj = Crossfire.WhoAmI().Map.ObjectAt(x, y)
	while obj != None:
		if obj.Floor == 1 and obj.ArchName == name:
			return True
		obj = obj.Above
	return False

# returns some eligible food on specified spot
def find_food(chicken, x, y):
	obj = chicken.Map.ObjectAt(x, y)
	while obj != None:
		#print obj.Name
		if eat.has_key(obj.NamePl):
			return obj
		obj = obj.Above
	return None

# main chicken handler
def move_chicken():
	chicken = Crossfire.WhoAmI()
	if chicken.Enemy != None:
		# chicken won't let itself get killed easily!
		chicken.WriteKey(key_attacked, '1', 1)
	
	if chicken.ReadKey(key_attacked) != '':
		return
	
	Crossfire.SetReturnValue(1)
	if chicken.Map.Darkness >= 3:
		# too dark, night is for sleeping
		return
	
	target = chicken.ReadKey(key_target)
	if target != '':
		x = int(target.split('|')[0])
		y = int(target.split('|')[1])
		if chicken.X != x or chicken.Y != y:
			chicken.Move(coordinates_to_dir(chicken.X - x, chicken.Y - y))
			return
		# target found, let's try to eat it
		food = find_food(chicken, x, y)
		chicken.WriteKey(key_target, '', 1)
		if food != None:
			chicken.Map.Print('The %s eats the %s!'%(chicken.Name, food.Name))
			got = chicken.ReadKey(key_food)
			if got == '':
				got = 0
			else:
				got = int(got)
			got = got + eat[food.NamePl]
			# drop an egg?
			if random.randint(1, 100) <= ( got * 2 ):
				egg = chicken.Map.CreateObject('chicken_egg', chicken.X, chicken.Y)
				egg.Name = 'Chicken Combat egg'
				egg.NamePl = 'Chicken Combat eggs'
				egg.Quantity = 1
				chicken.Map.Print('The %s lays an egg!'%chicken.Name)
				got = 0
			chicken.WriteKey(key_food, str(got), 1)
			food.Quantity = food.Quantity - 1
			return
	else:
		# try to find some food
		#chicken.Map.Print('find food...')
		food = None
		for x in range(-3, 4):
			for y in range(-3, 4):
				food = find_food(chicken, chicken.X + x, chicken.Y + y)
				#chicken.Map.Print('find food %d %d...'%(chicken.X + x, chicken.Y + y))
				if food != None:
					target = '%d|%d'%(food.X, food.Y)
					chicken.WriteKey(key_target, target, 1)
					#chicken.Map.Print('got food %s'%target)
					break
			if food != None:
				break
	
	# nothing found, random walk
	for test in [1, 10]:
		dir = random.randint(1, 8)
		if (has_floor(chicken.X + dir_x[dir], chicken.Y + dir_y[dir], stay_on_floor)):
			chicken.Move(dir)
			Crossfire.SetReturnValue(1)
			return
	

move_chicken()
