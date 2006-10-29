import Crossfire
import random

me = Crossfire.WhoAmI()
ac = Crossfire.WhoIsActivator()
r  = random.random()

if (me.Applied):
    if   (r <= 0.01):
        me.Identified=0
        me.Cursed= 1
        me.Dexterity= me.Dexterity+1
    elif (r <= 0.02):
        me.Identified=0
        me.Cursed= 1
        me.Intelligence= me.Intelligence+1
    elif (r <= 0.03):
        me.Identified=0
        me.Cursed= 1
        me.Constitution= me.Constitution+1
    elif (r >= 0.99):
        me.Identified=0
        me.Cursed= 1
        me.Dexterity= me.Dexterity-1
    elif (r >= 0.98):
        me.Identified=0
        me.Cursed= 1
        me.Intelligence= me.Intelligence-1
    elif (r >= 0.97):
        me.Identified=0
        me.Cursed= 1
        me.Constitution= me.Constitution-1
