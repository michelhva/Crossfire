import Crossfire
import CFGuilds

import sys
import string

activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
mymap = activator.Map
def find_player(object):
    while (object.Type != 1) : #1 is type 'Player'
        object = object.Above
        if not object:
            return 0
    return object




whoami=Crossfire.WhoAmI()
texta=string.split(Crossfire.WhatIsMessage())
if (texta[0] == 'enter') or (texta[0] == 'Enter'):
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
 if (guild.info(activatorname)!=0):
  text1=string.split(str(guild.info(activatorname)))


  ClearanceApproved = (text1[5])

  whoami.Say(ClearanceApproved)
  if (ClearanceApproved):
   if (ClearanceApproved == "'Initiate',"):
	ApprovedClearanceLevel = 0
   elif (ClearanceApproved == "'Novice',"):
	ApprovedClearanceLevel = 1
   elif (ClearanceApproved == "'Guildman',"):
	ApprovedClearanceLevel = 2
   elif (ClearanceApproved == "'Journeyman',"):
	ApprovedClearanceLevel = 3
   elif (ClearanceApproved == "'Master',"):
	ApprovedClearanceLevel = 4
   elif (ClearanceApproved == "'GuildMaster',"):
	ApprovedClearanceLevel = 5
 else:
	ApprovedClearanceLevel = 0
 if (activator.DungeonMaster ==1):
	ApprovedClearanceLevel = 5
#whoami.Say(str(ApprovedClearanceLevel))

 if (ClearanceRequested == "Initiate"):
	RequiredClearanceLevel = 0
 elif (ClearanceRequested == "Novice"):
	RequiredClearanceLevel = 1
 elif (ClearanceRequested == "Guildman"):
	RequiredClearanceLevel = 2
 elif (ClearanceRequested == "Journeyman"):
	RequiredClearanceLevel = 3
 elif (ClearanceRequested == "Master"):
	RequiredClearanceLevel = 4
 elif (ClearanceRequested == "GuildMaster"):
	RequiredClearanceLevel = 5
#whoami.Say(str(RequiredClearanceLevel))

 if (ApprovedClearanceLevel >= RequiredClearanceLevel):
	Approved = 'Access granted'
 else:
	Approved = 'Access denied'


 if (Approved == 'Access granted'):
	activator.Teleport(mymap,int(21),int(y))
	whoami.Say(Approved)
	activator.Teleport(mymap,int(x1),int(Y1))
 else:
	whoami.Say(Approved)
else:
	whoami.Say('Say enter to request entry')
