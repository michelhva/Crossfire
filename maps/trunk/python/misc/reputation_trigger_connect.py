"""
reputation_trigger_connect.py -- trigger connections based on reputation

Use in a check_inv trigger with a event_trigger.

Arguments:
    faction - name of faction to check against
    threshold - number between -100 and 100
    conn_geq - connection to trigger if reputation greater or equal than thresh
    conn_lt - connection to trigger if reputation less than thresh

If any connection is 0, the connection will not be used.
"""

import CFReputation
import Crossfire

def check():
    player = Crossfire.WhoIsActivator()
    if player.Type != Crossfire.Type.PLAYER:
        return
    params = Crossfire.ScriptParameters()
    args = params.split()
    faction = args[0]
    thresh = int(args[1])
    conn_geq = int(args[2])
    conn_lt = int(args[3])
    
    rep = CFReputation.reputation(player.Name, faction)
    if len(rep) > 0:
        if rep[0][1] >= thresh:
            if conn_geq != 0:
                player.Map.TriggerConnected(conn_geq, 1, player)
        elif conn_lt != 0:
            player.Map.TriggerConnected(conn_lt, 1, player)

check()
