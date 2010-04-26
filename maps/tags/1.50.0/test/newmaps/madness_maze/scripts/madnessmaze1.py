#This is the madness maze, created by Alestan.  Like all of crossfire, it is distributed under the GNU lisence.
#Please report bugs to Alestan@meflin.com  
#Created: 1 Aug 2008
import Crossfire
import CFGuilds
import CFItemBroker
import random
import string

activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
whoami=Crossfire.WhoAmI()
if whoami==Guardian:
    text=string.split(Crossfire.WhatIsMessage())
    if text[0]=="1":
        message= "This is the Madness maze, leave before it is too late"
        whoami.Say(message)
        message= "5. Too late?"
        whoami.Say(message)
    if text[0]=="2":
        message="I am the Guardian of the Madness Maze.  As you see, I am armless, not much of a guardian eh?"
        whoami.Say(message)
        message="6. What happened to your arms?"
        whoami.Say(message)
    if text[0]=="3":
        message= "Many entered here, few return, those that do are... changed...  For the good of all, it has been closed.  I am here to give warning."
        whoami.Say(message)
        message="7. Changed?"
        whoami.Say(message)
    if text[0]=='4':
        message= "Please, go ahead, kill me, I want to die."
        whoami.Say(message)
    if text[0]=='5':
        message="If you go in there, you won't come back."
        whoami.Say(message)
    if text[0]=="6"
        message="They cut them off, seemed to think I might be tempted by the maze, like I woulda been.  Figured I could warn people without arms, but I couldn't disarm the traps without arms."
        whoami.Say(message)
    if text[0]=="7":
        message= "That's what I said, changed.  Rarely for the better."
        whoami.Say(message)
    else:
        message="I have some information you need."
        whoami.Say(message)
        message="(You may reply with the following, just reply with the number.)"
        whoami.Say(message)
        message="1. What is this place?"
        whoami.Say(message)
        message="2. Who are you?"
        whoami.Say(message)
        message="3.What information?"
        whoami.Say(message)
        message="4. Die you freak!"