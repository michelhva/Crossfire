import Crossfire
import CFGuilds
import sys
import string
activator=Crossfire.WhoIsActivator()
whoami=Crossfire.WhoAmI()
mymap=Activator.Map
myx=whoami.X
message="start"
activator.Say(message)
whoami.Say("working")
myy=whoami.Y
guildname=Crossfire.ScriptParameters()
if (guildname):
	guild=CFGuilds.CFGuild(guildname)
	guildrecord=CFGuilds.CFGuildHouses().info(guildname)
	found=0
	record=guild.info(activatorname)
	if whoami.Name=="Novice lounge":
		if record['Rank']=="Novice" or record['Rank']=="Journeyman" or record['Rank']== "Guildman" or record['Rank']=="Master" or record['Rank']=="Guildmaster":
			x=20
			y=21
			activator.Teleport(mymap, int(x), int(y)			

