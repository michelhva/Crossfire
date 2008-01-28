import Crossfire
import random

me = Crossfire.WhoAmI()
ac = Crossfire.WhoIsActivator()
r  = random.random()

# Event is called before object is applied, so changing our properties just before it's actually
# applied instead of when removed
if (me.Applied == 0):
	if   (r <= 0.01):
		me.Cursed= 1
		me.Dex = me.Dex + 1
		me.Identified=0
	elif (r <= 0.02):
		me.Cursed= 1
		me.Int = me.Int + 1
		me.Identified=0
	elif (r <= 0.03):
		me.Cursed= 1
		me.Con = me.Con + 1
		me.Identified=0
	elif (r >= 0.97):
		me.Cursed= 1
		me.Dex = me.Dex - 1
		me.Identified=0
	elif (r >= 0.98):
		me.Cursed= 1
		me.Int = me.Int - 1
		me.Identified=0
	elif (r >= 0.99):
		me.Cursed= 1
		me.Con n= me.Con - 1
		me.Identified=0
