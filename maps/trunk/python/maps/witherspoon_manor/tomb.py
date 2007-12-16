'''
Script for the ghost in Witherspoon manor (south-west of Scorn).

This script is called when the player steps on the correct spot where the body is buried.

Associated script is ghost.py which should be in the same directory.

Both scripts are linked, so if you change something make sure to change both scripts!
'''

import Crossfire

def can_dig(pl):
	'''Returns True if the player can dig, False else. Give the relevant message.'''
	if pl.CheckArchInventory('skill_clawing') != None:
		pl.Write('Using your claws, you quickly dig.')
		return True
	if pl.CheckArchInventory('shovel_1') != None:
		pl.Write('You dig with your shovel.')
		return True
	
	pl.Write('You\'d dig, but you have nothing to dig with...')
	return False

def find_player():
	'''Find the player stepping on the detector'''
	test = Crossfire.WhoAmI().Above
	while test != None:
		if test.Type == Crossfire.Type.PLAYER:
			return test
		test = test.Above
	return None

def main():
	pl = find_player()
	if pl == None:
		return
	
	if pl.ReadKey('dialog_witherspoon_ghost') != 'witherspoon_ghost:wait':
		return
	
	if pl.ReadKey('witherspoon_tomb') != '':
		# Already dig, no need to give more items
		return
	
	pl.Write('You notice the earth here is kind of bumpy.')
	
	#ok, so two choices for the player: if she got clawing, easy to dig. Else need a shovel.
	dig = can_dig(pl)
	if dig == 0:
		return
	
	#don't want the player to dig again! Will be reset by the ghost later on
	pl.WriteKey('witherspoon_tomb', 'dig', 1)
	
	body = pl.CreateObject('corpse')
	body.Name = 'tortured body'
	body.NamePl = 'tortured bodies'
	
	pl.Write('You find a body!')

main()
