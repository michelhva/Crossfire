# Script for say event of IPO employees 
#
# Copyright (C) 2002 Joris Bontje
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
# The author can be reached via e-mail at jbontje@suespammers.org
#
#Updated to use new path functions in CFPython and broken apart by -Todd Mitchell


import CFPython

import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))

import string
import CFBank

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
x=CFPython.GetXPosition(activator)
y=CFPython.GetYPosition(activator)

bank = CFBank.CFBank()

text = string.split(CFPython.WhatIsMessage())


if text[0] == 'help' or text[0] == 'yes':
		message = 'Here is a quick list of commands:\n\n- deposit\n- withdraw\n- balance\nAll transactions are in Imperials\n(1 Ip = 1000 platinum coins).'
		CFPython.Say(whoami,message)

elif text[0] == 'deposit':
	if len(text)==2:
		if (CFPython.PayAmount(activator, int(text[1])*50000)):
			bank.deposit(activatorname, int(text[1]))
			CFPython.Say(whoami, 'Deposited to bank account')
		else:
			CFPython.Say(whoami, 'You need %d platinum'%(int(text[1])*1000))
	else:
		CFPython.Say(whoami, 'Usage "deposit <amount Ip>"')

elif text[0] == 'withdraw':
	if len(text)==2:
		if (bank.withdraw(activatorname, int(text[1]))):
			CFPython.Say(whoami, 'Withdrawn from bank account')
			id = CFPython.CreateObject('platinum coin', (x, y))
			CFPython.SetQuantity(id, int(text[1])*1000)
		else:
			CFPython.Say(whoami, 'Not enough Imperials on your account')
	else:
		CFPython.Say(whoami, 'Usage "withdraw <amount Ip>"')

elif text[0] == 'balance':
    balance = bank.getbalance(activatorname)
    CFPython.Say(whoami, 'Amount on bank: %d Ip'%balance)

else:
	CFPython.Say(whoami, 'Do you need help?')
