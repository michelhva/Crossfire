import CFPython
import sys
import random

me = CFPython.WhoAmI()
ac = CFPython.WhoIsActivator()
r  = random.random()

if (r <= 0.01):
    CFPython.Write("Your weapon suddenly seems lighter !",ac)
    CFPython.SetDamage(me,CFPython.GetDamage(me)+10)
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
elif (r <= 0.02):
    CFPython.Write("Your weapon suddenly seems darker !",ac)
    CFPython.SetDamage(me,CFPython.GetDamage(me)-10)
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
elif (r <= 0.03):
    CFPython.Write("Your weapon suddenly seems lighter !",ac)
    CFPython.SetDamage(me,CFPython.GetDamage(me)+10)
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
elif (r <= 0.04):
    CFPython.Write("Your weapon suddenly seems colder !",ac)
    CFPython.SetAttackType(me,CFPython.AttackTypeCold() + CFPython.AttackTypePhysical())
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
elif (r <= 0.05):
    CFPython.Write("Your weapon suddenly seems warmer !",ac)
    CFPython.SetAttackType(me,CFPython.AttackTypeFire() + CFPython.AttackTypePhysical())
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
elif (r <= 0.06):
    CFPython.Write("Your weapon suddenly emits sparks !",ac)
    CFPython.SetAttackType(me,CFPython.AttackTypeElectricity() + CFPython.AttackTypePhysical())
    CFPython.SetIdentified(me,0)
    CFPython.SetBeenApplied(me,0)
