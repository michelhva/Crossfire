#CFweardisguise.py
# A little script to insert an informational force into the player inventory
# if a article is applied and remove the force if it is unapplied.
# For example if you put on a priest robe it will insert the option value into
# a force slaying field which can be checked against on a map.
#
# This script is meant for items that can be worn or carried really
# I can't say how it will react if you hook it to other types of objects.

import CFPython

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()

option=CFPython.GetEventOptions(whoami,1) # 1 is apply event
    
if option:
    inv = CFPython.CheckInventory(activator, option) #Remove any previous disguise
    if inv:
        CFPython.RemoveObject(inv)
        #print "removing tag"      

    if not CFPython.IsApplied(whoami): #is the object is being applied
        tag = CFPython.CreateInvisibleObjectInside(activator, option)
        CFPython.SetName(tag, option)
        #print "adding tag"
    
