# -*- coding: utf-8 -*-
# Script handling various things for Darcap Manor.
#
# It is used:
# - in the blue zone, to count how many kobolds are on the map
# - for the potion, to explode walls in the treasure room

import Crossfire

def blue_count_kobolds():
    map = Crossfire.WhoAmI().Map
    count = 0
    for x in range(map.Width):
        for y in range(map.Height):
            below = map.ObjectAt(x, y)
            while below:
                if below.Name == 'kobold':
                    count = count + 1
                    break
                below = below.Above

    return count

def blue_check():

    if blue_count_kobolds() != 4:
        Crossfire.SetReturnValue(1)
        Crossfire.WhoIsActivator().Message('The level will only activate if there are exactly 4 kobolds on the map')
        return

def potion_find_wall(map, x, y, name):
    item = map.ObjectAt(x, y)
    while item:
        if item.ArchName == name:
            return item
        item = item.Above

    return None

def potion_check():
    """Handle the Darcap Manor's potion being thrown. Check if walls to destroy in the treasure room."""

    # note: duplication with /CFMove.py, should be factorised at some point
    dir_x = [  0, 0, 1, 1, 1, 0, -1, -1, -1 ]
    dir_y = [ 0, -1, -1, 0, 1, 1, 1, 0, -1 ]

    env = Crossfire.WhoAmI().Env

    if env.Map.Path != '/darcap/darcap/manor.treasure':
        return

    x = env.X + dir_x[ env.Direction ]
    y = env.Y + dir_y[ env.Direction ]

    if y != 4 and y != 8 and y != 12:
        return

    if x != 9 and x != 10:
        return

    left = potion_find_wall(env.Map, 9, y, 'cwall_mural_1_1')
    right = potion_find_wall(env.Map, 10, y, 'cwall_mural_1_2')

    if left == None or right == None:
        # hu?
        return

    left.Remove()
    right.Remove()

    env.Map.CreateObject('rubble', 9, y)
    env.Map.CreateObject('rubble', 10, y)

    Crossfire.WhoAmI().Remove()

    env.Map.Print('The wall explodes!')

if Crossfire.ScriptParameters() == 'blue':
    blue_check()
elif Crossfire.ScriptParameters() == 'potion':
    potion_check()
