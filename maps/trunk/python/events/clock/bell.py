# -*- coding: utf-8 -*-
#
# This script makes players aware of the time, tracking the current time
import Crossfire

def ring_bell():
    players = Crossfire.GetPlayers()
    for player in players:
        if player.Map == None:
            continue
        if player.Map.Region == None:
            continue
        if player.Map.Region.Name == 'scorn':
            player.Message("You hear the bells of the various temples of Scorn.")
        elif player.Map.Region.Name == 'darcap':
            player.Message("You hear the bell of St Andreas.")
        elif player.Map.Region.Name == 'navar':
            player.Message("You hear the bell of all the temples of Navar.")

dict = Crossfire.GetPrivateDictionary()
hour = Crossfire.GetTime()[3]

if not 'init' in dict.keys():
    dict['init'] = 1
    dict['last'] = hour
    Crossfire.Log(Crossfire.LogDebug, "Bell init")
else:
    last = dict['last']
    if (hour != last):
        dict['last'] = hour
        Crossfire.Log(Crossfire.LogDebug, "Bell ringing")
        ring_bell()
