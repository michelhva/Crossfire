import Crossfire

who = Crossfire.WhoAmI()
pl = Crossfire.WhoIsActivator()
msg = Crossfire.WhatIsMessage()

found = pl.CheckArchInventory(msg)

who.Say('check for find_arch_by_name')

if (found != None):
    who.Say('found %s'%found.Name)
else:
    who.Say('didn\'t find %s'%msg)
