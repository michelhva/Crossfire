import CFPython
import CFGuilds

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
mymap = CFPython.GetMap(activator)
mapname = CFPython.GetName(mymap)
trank = 0

points=CFPython.GetEventOptions(whoami,1) # 1 is apply event

if points:
    guild = CFGuilds.SearchGuilds(activatorname)
    if guild:
        CFGuilds.CFGuild.add_questpoints(activatorname,points)
    else:
        pass
else:
    print 'Error, no points specified in %s on map %s' %(whoami,mapname)

