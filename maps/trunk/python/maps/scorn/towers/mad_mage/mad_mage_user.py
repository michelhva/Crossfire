import Crossfire
import os

whoami = Crossfire.WhoAmI()
whoisother = Crossfire.WhoIsOther()
parms = Crossfire.WhatIsMessage()

if parms=="give":
    key = whoisother.ReadKey("mad_mage_marker")
    if key:
        file = open(os.path.join(Crossfire.DataDirectory(), Crossfire.MapDirectory(), 'python/maps/scorn/towers/mad_mage',key), "r")
        for line in file:
            whoami.Say(line)
        file.close()
    else:
        whoami.Say("Yes?")
