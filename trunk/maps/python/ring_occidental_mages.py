import CFPython
import sys
import random

me = CFPython.WhoAmI()
ac = CFPython.WhoIsActivator()
r  = random.random()

if (CFPython.IsApplied(me)):
    if   (r <= 0.01):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetDexterity(me, CFPython.GetDexterity(me)+1)
    elif (r <= 0.02):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetIntelligence(me, CFPython.GetIntelligence(me)+1)
    elif (r <= 0.03):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetConstitution(me, CFPython.GetConstitution(me)+1)
    elif (r >= 0.99):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetDexterity(me, CFPython.GetDexterity(me)+1)
    elif (r >= 0.98):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetIntelligence(me, CFPython.GetIntelligence(me)+1)
    elif (r >= 0.97):
        CFPython.SetIdentified(me,0)
        CFPython.SetCursed(me, 1)
        CFPython.SetConstitution(me, CFPython.GetConstitution(me)+1)
