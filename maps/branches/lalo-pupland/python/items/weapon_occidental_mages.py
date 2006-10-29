import Crossfire
import random

me = Crossfire.WhoAmI()
ac = Crossfire.WhoIsActivator()
r  = random.random()

if (r <= 0.01):
    ac.Write("Your weapon suddenly seems lighter !")
    me.Damage=me.Damage+10
    me.Identified=0
    me.BeenApplied=0
elif (r <= 0.02):
    ac.Write("Your weapon suddenly seems darker !")
    me.Damage=me.Damage-10
    me.Identified=0
    me.BeenApplied=0
elif (r <= 0.03):
    ac.Write("Your weapon suddenly seems lighter !")
    me.Damage=me.Damage+10
    me.Identified=0
    me.BeenApplied=0
elif (r <= 0.04):
    ac.Write("Your weapon suddenly seems colder !")
    me.AttackType=Crossfire.AttackTypeCold() + Crossfire.AttackTypePhysical())
    me.Identified=0
    me.BeenApplied=0
elif (r <= 0.05):
    ac.Write("Your weapon suddenly seems warmer !")
    me.AttackType=Crossfire.AttackTypeFire() + Crossfire.AttackTypePhysical())
    me.Identified=0
    me.BeenApplied=0
elif (r <= 0.06):
    ac.Write("Your weapon suddenly emits sparks !")
    me.AttackType=Crossfire.AttackTypeElectricity() + Crossfire.AttackTypePhysical())
    me.Identified=0
    me.BeenApplied=0
