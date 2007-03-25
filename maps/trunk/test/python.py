import Crossfire
import random
#import CFLog

Crossfire.SetReturnValue( 1 )

whoami=Crossfire.WhoAmI()
who = Crossfire.WhoIsActivator()

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
	whoami.Say(' - bed')
	whoami.Say(' - readkey')
	whoami.Say(' - writekey')
	whoami.Say(' - speed')
	whoami.Say(' - owner')
	whoami.Say(' - friendlylist')
	whoami.Say(' - create')
	whoami.Say(' - directory')

def do_arch():
	archs = Crossfire.GetArchetypes()
	whoami.Say('%d archetypes'%len(archs))
	which = random.randint(0,len(archs))
	arch = archs[which]
	whoami.Say('random = %s'%arch.Name)
	head = ''
	more = ''
	next = ''
	if (arch.Head):
		head = arch.Head.Name
	if (arch.More):
		more = arch.More.Name
	if (arch.Next):
		next = arch.Next.Name
	whoami.Say(' head = %s, more = %s, clone = %s, next = %s'%(head, more, arch.Clone.Name, next))

	arch = who.Archetype
	whoami.Say('your archetype is %s'%arch.Name)

def do_maps():
	whoami.Say('Current map is %s'%who.Map.Name)
	maps = Crossfire.GetMaps()
	whoami.Say('%d maps loaded'%len(maps))
	for map in maps:
		whoami.Say('%s   -> %d players'%(map.Name,map.Players))
#activator=Crossfire.WhoIsActivator()

def do_party():
	parties = Crossfire.GetParties()
	whoami.Say('%d parties'%len(parties))
	for party in parties:
		whoami.Say('%s (%s)'%(party.Name, party.Password))
		players = party.GetPlayers()
		for player in players:
			whoami.Say('   %s'%player.Name)
	if len(parties) >= 2:
		who.Party = parties[1]
		whoami.Say('changed your party!')

def do_region():
	whoami.Say('Known regions, region for current map is signaled by ***')
	cur = whoami.Map.Region
	whoami.Say('This map\'s region is %s [msg: %s]'%(cur.Name, cur.Message))
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
	who2 = Crossfire.WhoIsOther()
	who3 = Crossfire.WhoAmI()
	who = 0
	who2 = 0
	who3 = 0
	whoami.Say('let\'s hope no reference crash!')

def do_marker():
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
	for r in range(25):
		whoami.Say(' %d -> %d'%(r,who.GetResist(r)))

def do_basics():
	whoami.Say('Basic test')
	whoami.Say(' your type is %d'%who.Type)
	whoami.Say(' your level is %d'%who.Level)
	whoami.Say(' your nrof is %d'%who.Quantity)
	whoami.Say(' your weight is %d'%who.Weight)
	whoami.Say(' your name is %s'%who.Name)
	whoami.Say(' your archname is %s'%who.ArchName)
	whoami.Say(' your title is %s'%who.Title)
	whoami.Say(' your ip is %s'%who.IP)
	whoami.Say(' my name is %s'%whoami.Name)

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
		res = Crossfire.DestroyTimer(timer)
		whoami.Say('Timer %d removed with code %d'%(timer,res))

def do_misc():
	inv = whoami.Inventory
	if inv != 0:
		whoami.Say("First inv = %s"%inv.Name)
		whoami.Say("Inv.Env = %s"%inv.Env.Name)
	else:
		whoami.Say("Empty inv??")

def do_inventory():
	whoami.Say('You have:');
	inv = who.Inventory
	while inv:
		whoami.Say('%s (type = %d, subtype = %d)'%(inv.Name, inv.Type, inv.Subtype))
		inv = inv.Below

def do_exp():
	if ( len(topic) < 2 ):
		whoami.Say("Your exp is %d, perm is %d, mult is %d"%(who.Exp, who.PermExp, who.ExpMult))
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
	whoami.Say(dump_move("movetype", who.MoveType))

def do_bed():
	whoami.Say("bed = %s at (%d, %d)"%(who.BedMap, who.BedX, who.BedY))
	whoami.Say("changing to +1 -1")
	who.BedX = who.BedX + 1
	who.BedY = who.BedY - 1
	whoami.Say("bed = %s at (%d, %d)"%(who.BedMap, who.BedX, who.BedY))
	whoami.Say("resetting.")
	who.BedX = who.BedX - 1
	who.BedY = who.BedY + 1

def do_readkey():
	if (len(topic) < 2):
		whoami.Say('read what key?')
		return;
	whoami.Say('key %s = %s'%(topic[1], who.ReadKey(topic[1])))

def do_writekey():
	if (len(topic) < 3):
		whoami.Say('syntax is writekey key add_update [value]')
		return
	val = ''
	if (len(topic) > 3):
		val = topic[3]
	
	whoami.Say('writekey returned %d'%who.WriteKey(topic[1], val, int(topic[2])))
	
def do_speed():
	whoami.Say('Your speed is %f and your speed_left %f'%(who.Speed, who.SpeedLeft))
#	who.Speed = 0.2
	who.SpeedLeft = -50
	whoami.Say('Changed your speed, now %f and %f'%(who.Speed, who.SpeedLeft))

def do_owner():
	whoami.Say('Not implemented.');

def do_friendlylist():
	friends = Crossfire.GetFriendlyList()
	for ob in friends:
		if (ob.Owner):
			n = ob.Owner.Name
		else:
			n = ''
		whoami.Say(' - %s (%s)'%(ob.Name, n))

def do_create():
	first = Crossfire.CreateObjectByName('gem')
	if (first):
		whoami.Say('created gem: %s'%first.Name)
		first.Teleport(whoami.Map, 2, 2)
	second = Crossfire.CreateObjectByName('diamond')
	if (second):
		whoami.Say('created diamond: %s'%second.Name)
		second.Teleport(whoami.Map, 2, 2)

def do_directory():
	whoami.Say('map = %s'%Crossfire.MapDirectory());
	whoami.Say('unique = %s'%Crossfire.UniqueDirectory());
	whoami.Say('temp = %s'%Crossfire.TempDirectory());
	whoami.Say('config = %s'%Crossfire.ConfigDirectory());
	whoami.Say('local = %s'%Crossfire.LocalDirectory());
	whoami.Say('player = %s'%Crossfire.PlayerDirectory());
	whoami.Say('data = %s'%Crossfire.DataDirectory());

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
elif topic[0] == 'inv':
	do_inventory()
elif topic[0] == 'bed':
	do_bed()
elif topic[0] == 'readkey':
	do_readkey()
elif topic[0] == 'writekey':
	do_writekey()
elif topic[0] == 'speed':
	do_speed()
elif topic[0] == 'owner':
	do_owner()
elif topic[0] == 'friendlylist':
	do_friendlylist()
elif topic[0] == 'create':
	do_create()
elif topic[0] == 'directory':
	do_directory()
else:
	do_help()
