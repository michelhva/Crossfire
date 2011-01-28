import random
import Crossfire
import CFGuilds
import sys
import string
#sys.stderr=open("/home/alestan/Output.log", 'a')
activator=Crossfire.WhoIsActivator()
whoami=Crossfire.WhoAmI()

activatorname=activator.Name
mymap = activator.Map
def find_player(object):
    while (object.Type != 1) : #1 is type 'Player'
        object = object.Above
        if not object:
            return 0
    return object

Corpse = activator.Map.ObjectAt(int (21), int (0))
x4=random.randint(21, 23)
y4=random.randint(22,24)

Curse = activator.Map.ObjectAt(int(x4),int(y4))



x3=1
y3=8

if (1==1):
 if (activator.DungeonMaster ==1):
	ApprovedClearanceLevel = 5




 Clearancerq=Crossfire.ScriptParameters() # 6 is say event
 x1 = activator.X
 Y1 = activator.Y
 x= 26
 y=0

 text = string.split(Clearancerq)
 guildname = text[0]
 guild=CFGuilds.CFGuild(guildname)
 guildrecord=CFGuilds.CFGuildHouses().info(guildname)
 ClearanceRequested=(text[1])
 ActionRequired=text[2]
 if (guild.info(activatorname)!=0):
  text1=string.split(str(guild.info(activatorname)))


  ClearanceApproved = (text1[5])

 # whoami.Say(ClearanceApproved)
  if (ClearanceApproved):
   if (ClearanceApproved == "'Initiate',"):
	ApprovedClearanceLevel = 1
   elif (ClearanceApproved == "'Novice',"):
	ApprovedClearanceLevel = 2
   elif (ClearanceApproved == "'Guildman',"):
	ApprovedClearanceLevel = 3
   elif (ClearanceApproved == "'Journeyman',"):
	ApprovedClearanceLevel = 4
   elif (ClearanceApproved == "'Master',"):
	ApprovedClearanceLevel = 5
   elif (ClearanceApproved == "'GuildMaster',"):
	ApprovedClearanceLevel = 6
 else:
	ApprovedClearanceLevel = 0
 if (activator.DungeonMaster ==1):
	ApprovedClearanceLevel = 6
#whoami.Say(str(ApprovedClearanceLevel))

 if (ClearanceRequested == "Initiate"):
	RequiredClearanceLevel = 1
 elif (ClearanceRequested == "Novice"):
	RequiredClearanceLevel = 2
 elif (ClearanceRequested == "Guildman"):
	RequiredClearanceLevel = 3
 elif (ClearanceRequested == "Journeyman"):
	RequiredClearanceLevel = 4
 elif (ClearanceRequested == "Master"):
	RequiredClearanceLevel = 5
 elif (ClearanceRequested == "GuildMaster"):
	RequiredClearanceLevel = 6
#whoami.Say(str(RequiredClearanceLevel))

 if (ApprovedClearanceLevel >= RequiredClearanceLevel):
	Approved = 'Access granted'
 else:
	Approved = 'Access denied'


 if (Approved != 'Access granted'):
     if (ActionRequired == "A"):
        activator.Teleport(mymap,int(40),int(22))
     elif (ActionRequired == "D"):
	Corpse.Name = str("%s's body" %(activator.Name))
	Corpse.Race = str("%s's Curse" %(activator.Name))
	Corpse.Weight = 1
	Curse.Name = str("%s's Curse" %(activator.Name))
	Corpse.Teleport(mymap, activator.X, activator.Y)
	Curse.InsertInto(activator)
	Curse1=activator.CheckArchInventory("amulet")
	#whoami.Say(str(Curse1))
	#whoami.Say(str(Curse))
	Curse1.Applied = 1


        activator.Teleport(mymap,int(23),int(0))
	



#        whoami.Say('y')
#	    whoami.Say(Approved)
	#activator.Teleport(mymap,int(x1),int(Y1))
# else:
#	whoami.Say(Approved)
	
#else:
#	whoami.Say('Say enter to request entry')
