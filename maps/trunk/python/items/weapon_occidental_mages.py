import Crossfire
import random

me = Crossfire.WhoAmI()
ac = Crossfire.WhoIsActivator()
r  = random.random()

if (r <= 0.01):
	ac.Write("Your weapon suddenly seems lighter!")
	me.Dam = me.Dam + 10
	me.BeenApplied=0
	me.Identified=0
elif (r <= 0.02):
	ac.Write("Your weapon suddenly seems darker!")
	me.Dam = me.Dam - 10
	me.BeenApplied=0
	me.Identified=0
elif (r <= 0.03):
	ac.Write("Your weapon suddenly seems lighter!")
	me.Dam = me.Dam + 10
	me.Identified=0
	me.BeenApplied=0
elif (r <= 0.04):
	ac.Write("Your weapon suddenly seems colder!")
	me.AttackType = Crossfire.AttackType.COLD + Crossfire.AttackType.PHYSICAL
	me.Identified=0
	me.BeenApplied=0
elif (r <= 0.05):
	ac.Write("Your weapon suddenly seems warmer!")
	me.AttackType=Crossfire.AttackType.FIRE + Crossfire.AttackType.PHYSICAL
	me.Identified=0
	me.BeenApplied=0
elif (r <= 0.06):
	ac.Write("Your weapon suddenly emits sparks!")
	me.AttackType=Crossfire.AttackType.ELECTRICITY + Crossfire.AttackType.PHYSICAL
	me.Identified=0
	me.BeenApplied=0
