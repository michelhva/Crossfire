# Script for say event of Imperial Bank Tellers 
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
# Updated to use new path functions in CFPython and broken and 
# modified a bit by -Todd Mitchell


import CFPython

import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))

import string
import random
import CFBank
import CFItemBroker

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
x=CFPython.GetXPosition(activator)
y=CFPython.GetYPosition(activator)


#EASILY SETTABLE PARAMETERS
service_charge=5  #service charges for transactions as a percent
exchange_rate=10000 #exchange rate for silver (value 1)
bankdatabase="ImperialBank_DB"

fees=(service_charge/100)+1
bank = CFBank.CFBank(bankdatabase)

text = string.split(CFPython.WhatIsMessage())
thanks_message = ['Thank you for banking the Imperial Way.', 'Thank you, please come \
again.', 'Thank you, please come again.','Thank you for banking the Imperial Way.', \
'Thank you for your patronage.', 'Thank you, have a nice day.', 'Thank you. "Service" \
is our middle name.', 'Thank you. "Service" is our middle name.', 'Thank you for your \
patronage.', 'Thank you, have a nice day.', 'Thank you.  Hows about a big slobbery \ kiss?']



if text[0] == 'help' or text[0] == 'yes':
		message ='You can:\n-deposit,-withdraw,-balance,-exchange \
                  \nAll transactions are in imperial notes\n(1 : 1000 gold coins). \
                  \nA service charge of %d percent will be placed on all deposits' \
		%(service_charge)

elif text[0] == 'deposit':
	if len(text)==2:
		if (CFPython.PayAmount(activator, (int(text[1])*exchange_rate)*fees)):
			bank.deposit(activatorname, int(text[1]))
			message = '%d imperials deposited to bank account.  %s' \
			%(int(text[1]),random.choice(thanks_message))
		else:
			message = 'You would need %d gold'%((int(text[1])*(exchange_rate/10))*fees)
	else:
		message = 'Usage "deposit <amount in imperials>"'

elif text[0] == 'withdraw':
	if len(text)==2:
		if (bank.withdraw(activatorname, int(text[1]))):
			message = '%d imperials withdrawn from bank account.  %s' \
			%(int(text[1]),random.choice(thanks_message))
			id = CFPython.CreateObject('imperial', (x, y))
			CFPython.SetQuantity(id, int(text[1]))
		else:
			message = 'Not enough imperials on your account'
	else:
		message = 'Usage "withdraw <amount in imperials>"'

elif text[0] == 'exchange':
    if len(text)==2:
        inv=CFPython.CheckInventory(activator,'imperial')
        if inv:
            pay = CFItemBroker.ItemBroker(inv).subtract(int(text[1]))
            if pay:
                id = CFPython.CreateObject('platinum coin', (x, y))
                CFPython.SetQuantity(id, int(text[1])*(exchange_rate/50))
                message = random.choice(thanks_message)
            else:
                message = 'Sorry, you do not have %d imperials' %int(text[1])
        else:
            message = 'Sorry, you do not have any imperials'
    else:
        message = 'Usage "exchange <amount>" (imperials to platimum coins)' 

elif text[0] == 'balance':
    balance = bank.getbalance(activatorname)
    message = 'Amount in bank: %d Ip'%(balance)

else:
	message = 'Do you need help?'

CFPython.Say(whoami, message)
