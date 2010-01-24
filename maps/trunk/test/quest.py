# basic quest-related tests

import Crossfire

msg = Crossfire.WhatIsMessage()
me = Crossfire.WhoAmI()
who = Crossfire.WhoIsActivator()

qn = 'darcap/Spike' # quest name to use, if not defined things may work weirdly/crash!

if (msg == 'st'):
	state = who.QuestGetState(qn)
	me.Say('quest status:%s'%state)
elif (msg == 'ch'):
	who.QuestSetState(qn, 3)
elif (msg == 'co'):
	who.QuestEnd(qn)
elif (msg == 'wc'):
	me.Say('was completed: %s'%who.QuestWasCompleted(qn))
elif (msg == 'qs'):
	who.QuestStart(qn, 1)
else:
	me.Say('...')

