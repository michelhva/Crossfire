# greet_message.py
#
# Copyright 2007 by Nicolas Weeger
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
#
#
# This script makes the monster/living thing containing it display
# a message when it sees an enemy for the first time.
# Message is specified in event object's options field. It can contain %m
# and %e, which will be replaced by respectively dead item's name and enemy's
# name.

import Crossfire

# This script only works when greeting monster is WhoAmI

whoami = Crossfire.WhoAmI()
msg = Crossfire.ScriptParameters()

def do_enemy():
	old = whoami.ReadKey('greet_enemy')
	msg = Crossfire.ScriptParameters()
	if (old == str(whoami.Enemy.Count)):
		return;

	msg = msg.replace('%m', whoami.Name)
	msg = msg.replace('%e', whoami.Enemy.Name)
	whoami.Say(msg)
	whoami.WriteKey('greet_enemy', str(whoami.Enemy.Count), 1)


if whoami.Enemy != None and msg != '':
	do_enemy()
