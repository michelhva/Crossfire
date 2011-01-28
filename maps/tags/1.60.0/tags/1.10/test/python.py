import Crossfire
import random
#import CFLog

Crossfire.SetReturnValue( 1 )

whoami=Crossfire.WhoAmI()

def do_help():
	whoami.Say('Usage: say <test name>\nAvailable tests:')
	whoami.Say(' - arch: archetypes-related tests')
	whoami.Say(' - maps: maps-related tests')
	whoami.Say(' - party: party-related tests')
	whoami.Say(' - region: party-related tests')
	whoami.Say(' - ref: some checks on objects references')
	whoami.Say(' - mark: marked item')
	whoami.Say(' - memory: storage-related tests')
	whoami.Say(' - time: time of day tests')
	whoami.Say(' - timer: timer activation test')
	whoami.Say(' - timer_kill: kill specified timer')
	whoami.Say(' - misc: other tests')
	whoami.Say(' - exp')
	whoami.Say(' - const: constants and such')
	whoami.Say(' - move')

def do_arch():
	archs = Crossfire.GetArchetypes()
	whoami.Say('%d archetypes'%len(archs))
	which = random.randint(0,len(archs))
	arch = archs[which]
	whoami.Say('random = %s'%arch.Name)

	arch = Crossfire.WhoIsActivator().Archetype
	whoami.Say('your archetype is %s'%arch.Name)

def do_maps():
	maps = Crossfire.GetMaps()
	whoami.Say('%d maps loaded'%len(maps))
	for map in maps:
		whoami.Say('%s   -> %d players'%(map.Name,map.Players))
#activator=Crossfire.WhoIsActivator()

def do_party():
	parties = Crossfire.GetParties()
	whoami.Say('%d parties'%len(parties))
	for party in parties:
		whoami.Say('%s'%(party.Name))
		players = party.GetPlayers()
		for player in players:
			whoami.Say('   %s'%player.Name)
	if len(parties) >= 2:
		Crossfire.WhoIsActivator().Party = parties[1]
		whoami.Say('changed your party!')

def do_region():
	whoami.Say('Known regions, region for current map is signaled by ***')
	cur = whoami.Map.Region
	whoami.Say('This map\'s region is %s'%(cur.Name))
	regions = Crossfire.GetRegions()
	whoami.Say('%d regions'%len(regions))
	for region in regions:
		if cur == region:
			whoami.Say('*** %s - %s'%(region.Name,region.Longname))
		else:
			whoami.Say('%s - %s'%(region.Name,region.Longname))
	parent = cur.GetParent()
	if parent:
		whoami.Say('Parent is %s'%parent.Name)
	else:
		whoami.Say('Region without parent')

def do_activator():
	who = Crossfire.WhoIsActivator()
	who2 = Crossfire.WhoIsOther()
	who3 = Crossfire.WhoAmI()
	who = 0
	who2 = 0
	who3 = 0
	whoami.Say('let\'s hope no reference crash!')

def do_marker():
	who = Crossfire.WhoIsActivator()
	obj = who.MarkedItem
	if obj:
		whoami.Say(' your marked item is: %s'%obj.Name)
		mark = obj.Below
	else:
		whoami.Say(' no marked item')
		mark = who.Inventory
	while (mark) and (mark.Invisible):
		mark = mark.Below
	who.MarkedItem = mark
	whoami.Say('Changed marked item!')

def do_memory():
	whoami.Say('Value save test')
	dict = Crossfire.GetPrivateDictionary()
	if dict.has_key('s'):
		x = dict['s']
		whoami.Say(' x was %d'%x)
		x = x + 1
	else:
		x = 0
		whoami.Say(' new x')

	dict['s'] = x
		

def do_resist():
	whoami.Say('Resistance test')
	who = Crossfire.WhoIsActivator()
	for r in range(25):
		whoami.Say(' %d -> %d'%(r,who.GetResist(r)))

def do_basics():
	whoami.Say('Basic test')
	who = Crossfire.WhoIsActivator()
	whoami.Say('type = %d'%who.Type)

def do_time():
	cftime = Crossfire.GetTime()
	whoami.Say('Year: %d'%cftime[0])
	whoami.Say('Month: %d'%cftime[1])
	whoami.Say('Day: %d'%cftime[2])
	whoami.Say('Hour: %d'%cftime[3])
	whoami.Say('Minute: %d'%cftime[4])
	whoami.Say('Day of week: %d'%cftime[5])
	whoami.Say('Week of year: %d'%cftime[6])
	whoami.Say('Season: %d'%cftime[7])

def do_timer():
	id = whoami.CreateTimer(3,1)
	if id >= 0:
		whoami.Say('The countdown started with a 3 second delay, timerid = %d'%id)
	else:
		whoami.Say('Timer failure: %d'%id)

def do_timer_kill():
	if ( len(topic) < 2 ):
		whoami.Say('Kill which timer?')
	else:
		timer = int(topic[1])
		whoami.Say('DestroyTimer %d'%timer);
		res = Crossfire.DestroyTimer(timer)
		whoami.Say(' => %d'%res)

def do_misc():
	inv = whoami.Inventory
	if inv != 0:
		whoami.Say("First inv = %s"%inv.Name)
		whoami.Say("Inv.Env = %s"%inv.Env.Name)
	else:
		whoami.Say("Empty inv??")

def do_exp():
	who = Crossfire.WhoIsActivator()
	if ( len(topic) < 2 ):
		whoami.Say("Your exp is %d"%who.Exp)
		whoami.Say("Syntax is: exp <value> [option] [skill]")
	else:
		value = int(topic[1])
		skill = ""
		arg = 0
		if ( len(topic) > 2 ):
			arg = int(topic[2])
			if ( len(topic) > 3):
				i = 3
				while ( i < len(topic) ):
					skill = skill + topic[i] + ' '
					i = i + 1
				skill = skill.rstrip()
		who.AddExp(value, skill, arg)
		whoami.Say("ok, added %d exp to %s"%(value,skill))

def do_const():
	whoami.Say("%s => %d"%(Crossfire.DirectionName[Crossfire.Direction.NORTH],Crossfire.Direction.NORTH))
	whoami.Say("Player type => %d"%Crossfire.Type.PLAYER)
	whoami.Say("Move Fly High => %d"%Crossfire.Move.FLY_HIGH)

def dump_move(title, move):
	moves = [
		Crossfire.Move.WALK,
		Crossfire.Move.FLY_LOW,
		Crossfire.Move.FLY_HIGH,
		Crossfire.Move.FLYING,
		Crossfire.Move.SWIM,
		Crossfire.Move.BOAT ]
	s = title + ':'
	for t in moves:
		if move & t:
			s = s + ' ' + Crossfire.MoveName[t]
	return s

def do_move():
	who = Crossfire.WhoIsActivator()
	whoami.Say(dump_move("movetype", who.MoveType))

whoami.Say( 'plugin test' )

topic = Crossfire.WhatIsMessage().split()
#whoami.Say('topic = %s'%topic)
#whoami.Say('topic[0] = %s'%topic[0])
if topic[0] == 'arch':
	do_arch()
elif topic[0] == 'maps':
	do_maps()
elif topic[0] == 'party':
	do_party()
elif topic[0] == 'region':
	do_region()
elif topic[0] == 'mark':
	do_marker()
elif topic[0] == 'ref':
	do_activator()
elif topic[0] == 'memory':
	do_memory()
elif topic[0] == 'resist':
	do_resist()
elif topic[0] == 'basics':
	do_basics()
elif topic[0] == 'time':
	do_time()
elif topic[0] == 'timer':
	do_timer()
elif topic[0] == 'timer_kill':
	do_timer_kill()
elif topic[0] == 'misc':
	do_misc()
elif topic[0] == 'exp':
	do_exp()
elif topic[0] == 'const':
	do_const()
elif topic[0] == 'move':
	do_move()
else:
	do_help()
