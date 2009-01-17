import Crossfire

#Crossfire.SetReturnValue( 1 )

whoami = Crossfire.WhoAmI()
who = Crossfire.WhoIsActivator()

whoami.Say("exit applied by %s"%who.Name)
