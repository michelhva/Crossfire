import CFPython
import CFGuilds

whoami=CFPython.WhoAmI()
guildname=CFPython.GetEventOptions(whoami,1) # 1 is 'apply' event

def find_player(object):
    while (CFPython.GetType(object) != 1) : #1 is type 'Player'
        object = CFPython.GetPreviousObject(object)
        if not object:
            return 0
    return object
   
activator=CFPython.WhoIsActivator()
map = CFPython.GetMap(activator)

players = []
names = []

if (guildname):
    #find players by coords
    ob1=CFPython.GetObjectAt(map,33,24)
    ob2=CFPython.GetObjectAt(map,33,26)
    objects = [ob1, ob2]
    for object in objects:
        temp = find_player(object)
        if temp:
            players.append(temp)
    players.append(activator)
    
    for player in players:
        names.append(CFPython.GetName(player))
        
    if len(players) == 3:
        print '%s,%s and %s found guild %s' %(names[0], names[1], names[2], guildname)

        CFGuilds.CFGuildHouses().establish(guildname)
        #Masterize them
        for player, name in zip(players, names):
            CFGuilds.CFGuild(guildname).add_member(name, 'GuildMaster')
            guildmarker = CFPython.CreateInvisibleObjectInside(player, guildname)
            CFPython.SetName(guildmarker, guildname)
            CFPython.SetSlaying(guildmarker, 'GuildMaster')

            #teleport them
            CFPython.Teleport(player,map,int(11),int(16))
            message = "You have purchased the %s guild.  Rule it wisely.  (I would type 'save' right about now...)"
        
    else:
        message = 'To purchase a guild requires two additional persons to stand on the alcoves above.'
else:
    print 'Guild Purchase Error: %s, %s' %(guildname, activatorname)
    message = 'Guild Purchase Error, please notify a DM'
    
CFPython.Write(message,whoami)
    
