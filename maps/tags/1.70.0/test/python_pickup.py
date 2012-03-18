import Crossfire

Crossfire.SetReturnValue( 1 )

whoami = Crossfire.WhoAmI()
who = Crossfire.WhoIsActivator()
where = Crossfire.WhoIsOther()

whoami.Say("i'm %s pickup by %s"%(whoami.Name, who.Name))
if (where) :
	whoami.Say("i'm about to be put into %s"%where.Name)
else:
	whoami.Say('put nowhere')

