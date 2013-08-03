# banksay.py -- implements 'say' event for bank tellers
# Created by: Joris Bontje <jbontje@suespammers.org>
#
# Updated to use new path functions in CFPython and broken and
# modified a bit by -Todd Mitchell

import string
import random

import Crossfire
import CFBank
import CFItemBroker
import CFMail

# Set up a few easily-settable configuration variables.
bank = CFBank.CFBank('ImperialBank_DB')
service_charge = 1  # Service charge for transactions (as a percentage)
fees = service_charge / 100.0 + 1

# Set up variables for a few commonly-accessed objects.
mail = CFMail.CFMail()
activator = Crossfire.WhoIsActivator()
activatorname = activator.Name
whoami = Crossfire.WhoAmI()
x = activator.X
y = activator.Y

# Account to record bank fees. Let's see how much the bank is being used.
Skuds = 'Imperial-Bank-Of-Skud' + str('Imperial-Bank-Of-Skud'.__hash__())

# Associate coin names with their corresponding values in silver.
CoinTypes = {
    'SILVER': 1,
    'GOLD': 10,
    'PLATINUM': 50,
    'JADE': 5000,
    'AMBERIUM': 500000,
    'IMPERIAL NOTE': 10000,
    '10 IMPERIAL NOTE': 100000,
    '100 IMPERIAL NOTE': 1000000,
    }

# Associate coin names with their corresponding archetypes.
ArchType = {
    'SILVER': 'silvercoin',
    'GOLD': 'goldcoin',
    'PLATINUM': 'platinacoin',
    'JADE': 'jadecoin',
    'AMBERIUM': 'ambercoin',
    'IMPERIAL NOTE': 'imperial',
    '10 IMPERIAL NOTE': 'imperial10',
    '100 IMPERIAL NOTE': 'imperial100',
    }

# Define several 'thank-you' messages which are chosen at random.
thanks_message = [
    'Thank you for banking the Imperial Way.',
    'Thank you for banking the Imperial Way.',
    'Thank you, please come again.',
    'Thank you, please come again.',
    'Thank you for your patronage.',
    'Thank you for your patronage.',
    'Thank you, have a nice day.',
    'Thank you, have a nice day.',
    'Thank you. "Service" is our middle name.',
    'Thank you. "Service" is our middle name.',
    'Thank you. Hows about a big slobbery kiss?',
    ]

# ----------------------------------------------------------------------------
# Piece together several arguments to form a coin name.
def getCoinNameFromArgs(argv):
    coinName = ""

    # Take the arguments and piece together the full coin name.
    for argument in argv:
        coinName += argument + ' '

    # Remove the trailing space and 's' from the coin name.
    coinName = coinName[:len(coinName) - 1]

    if coinName[len(coinName) - 1] == 's':
        coinName = coinName[:len(coinName) - 1]

    return coinName

# ----------------------------------------------------------------------------
# Return the exchange rate for the given type of coin.
def getExchangeRate(coinName):
    # Try to find exchange rate, set to None if we can't.
    if coinName.upper() in CoinTypes:
        return int(CoinTypes.get(coinName.upper()))
    else:
        return None

# ----------------------------------------------------------------------------
# Called when the deposit box (ATM) is opened.
# TODO: Fix the ATM.
def depositBoxOpen():
    balance = bank.getbalance(activatorname)
    Total = balance

    if balance >= 1000000:

        t = whoami.CreateObject('imperial100')

        bank.withdraw(activatorname, 1000000)
        balance = bank.getbalance(activatorname)
    if balance >= 1000000 / 2:
        t = whoami.CreateObject('ambercoin')
        bank.withdraw(activatorname, 1000000 / 2)
        balance = bank.getbalance(activatorname)
    if balance >= 1000000 / 2:
        t = whoami.CreateObject('ambercoin')
        bank.withdraw(activatorname, 1000000 / 2)
        balance = bank.getbalance(activatorname)

    if balance >= 100000:
        t = whoami.CreateObject('imperial10')
        bank.withdraw(activatorname, 100000)
        balance = bank.getbalance(activatorname)
    if balance >= 100000:
        t = whoami.CreateObject('imperial10')
        bank.withdraw(activatorname, 100000)
        balance = bank.getbalance(activatorname)
    if balance >= 100000:
        t = whoami.CreateObject('imperial10')
        bank.withdraw(activatorname, 100000)
        balance = bank.getbalance(activatorname)
    if balance >= 100000:
        t = whoami.CreateObject('imperial10')
        bank.withdraw(activatorname, 100000)
        balance = bank.getbalance(activatorname)
    if balance >= 100000:
        t = whoami.CreateObject('imperial10')
        bank.withdraw(activatorname, 100000)
        balance = bank.getbalance(activatorname)

    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000:
        t = whoami.CreateObject('imperial')
        bank.withdraw(activatorname, 10000)
        balance = bank.getbalance(activatorname)
    if balance >= 10000 / 2:
        t = whoami.CreateObject('jadecoin')
        bank.withdraw(activatorname, 10000 / 2)
        balance = bank.getbalance(activatorname)
    if balance >= 10000 / 2:
        t = whoami.CreateObject('jadecoin')
        bank.withdraw(activatorname, 10000 / 2)
        balance = bank.getbalance(activatorname)
    if balance >= 5000:
        t = whoami.CreateObject('platinacoin')
        bank.withdraw(activatorname, 5000)
        t.Quantity = 100
        balance = bank.getbalance(activatorname)
    if balance >= 1000:
        t = whoami.CreateObject('goldcoin')
        bank.withdraw(activatorname, 1000)
        t.Quantity = 100
        balance = bank.getbalance(activatorname)
    if balance >= 1000:
        t = whoami.CreateObject('silvercoin')
        bank.withdraw(activatorname, 1000)
        t.Quantity = 1000
        balance = bank.getbalance(activatorname)
    balance = bank.getbalance(activatorname)
    Total = Total - balance
    t = activator.CreateObject('force')
    t.Name = 'SkudCtrl'
    t.Title = str(Total)

    # tnew=t.InserInto(whoami)

# ----------------------------------------------------------------------------
# Called when the deposit box (ATM) is closed.
# TODO: Fix the ATM.
def depositBoxClose():
    t = activator.CheckInventory('SkudCtrl')

    Total = float(t.Title)
    Total = long(Total)

    t.Quantity = 0

    MyInv = whoami.Inventory
    Value = 0
    while MyInv != None:
        if MyInv.Name != 'Apply' and MyInv.Name != 'Close':

            Value += MyInv.Value * MyInv.Quantity
            MyInv1 = MyInv.Below
            MyInv.Teleport(activator.Map, 15, 3)
            MyInv = MyInv1
        else:
            MyInv = MyInv.Below

    bank.deposit(activatorname, Value)
    Difference = abs(Value - Total)

    Fee = Difference - Difference / fees
    bank.withdraw(activatorname, int(Fee))

    whoami.Say('A Service charge of ' + str(int(Fee))
               + ' silver coins has been charged on this transaction.'
               )

# ----------------------------------------------------------------------------
# Print a help message for the player.
def cmd_help():
    message = "You can check your 'balance', 'deposit' or 'withdraw' money, 'exchange' your currency, 'cash' a check, 'transfer' funds, buy 'checks', or find out how much 'profits' this bank has made.\n\nAll transactions are in imperial notes (1 note = 1000 gold). A service charge of %d percent will be placed on all deposits." % service_charge

    if activator.DungeonMaster:
        message += "\n\nAs the DM, you can also 'zero-balance' the profit that the bank has made."

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Show the profits made by the bank.
def cmd_show_profits():
    message = "To date, the Imperial Bank of Skud has made %s " \
            "platinum in profit." % str(bank.getbalance(Skuds) / 50.0)
    whoami.Say(message)

# ----------------------------------------------------------------------------
# Erase the profits made by the bank.
def cmd_zero_balance():
    if activator.DungeonMaster:
        bank.withdraw(Skuds, bank.getbalance(Skuds))
        message = "Profits erased!"
    else:
        message = "Only the dungeon master can wipe our profits!"

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Find out how much money the player has in his/her account.
def cmd_balance(argv):
    balance = bank.getbalance(activatorname)

    if len(argv) >= 2:
        coinName = getCoinNameFromArgs(argv[1:])
        exchange_rate = getExchangeRate(coinName)

        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return

        if balance != 0:
            balance /= exchange_rate * 1.0;
            message = "You have %s %s in the bank." % (str(balance), coinName)
        else:
            message = "Sorry, you have no balance."

        whoami.Say(message);
    else:
        # No need to reimplement this command; just recurse.
        cmd_balance(["balance", "silver"])

# ----------------------------------------------------------------------------
# Deposit money or checks.
def cmd_deposit(text):
    if len(text) == 2:
        if text[1] == 'check':
            whoami.Say('x')
            inv = activator.CheckInventory('bankcard')
            whoami.Say('x')
            if inv:
                whoami.Say('y')
                name1 = string.split(inv.Name, "'")
                payer = name1[0]
                information = inv.Message
                information = string.split(information, '\n')
                if information[0] != 'CANCELED':

                    payee = string.split(information[0], ' ')

        #   whoami.Say(str(payee))

                    payee = payee[5]
                    if payee != activator.Name:
                        if payee.upper() != 'BEARER':
                            message = \
                                'This check is not made out to you!'

                            mail.send(1, payee,
                                    'The-Imperial-Bank-of-Skud',
                                    'Dear Sir or Madam:' + '\n'
                                    + 'It has come to our attention that an attempt to cash a check made out to you by someone other than you has been made.  The check was made out by'
                                     + payer
                                    + '; and, the attempt to cash it was made by'
                                     + activator.Name
                                    + '.  We apologise for any trouble this may cause, and would like to inform you that we are always at your service.'
                                     + '''

''' + 'Sincerly,'
                                    + '\n'
                                    + 'The Imperial Bank of Skud')
                            mail.send(1, payer,
                                    'The-Imperial-Bank-of-Skud',
                                    'Dear Sir or Madam:'
                                    + '\nIt has come to our attention that an attempt to cash a check made out by you to '
                                     + payee + ' has been made by '
                                    + activator.Name
                                    + '.  We apologise for any trouble this may cause; and, we would like to inform you that we are always at your service.'
                                     + '\n' + '\n' + 'Sincerly,'
                                    + '\n'
                                    + ' The Imperial Bank of Skud')
                        if payee.upper() == 'BEARER':
                            payee = activator.Name
                    if payee == activator.Name:
                        balanceavailable = bank.getbalance(payer)
                        amount = string.split(information[1], ' ')
                        signer = string.split(information[2])
                        signer = signer[1]
                        if payer == signer:
                            cointype = amount[1]

                    # print amount
            # ........whoami.Say(str(amount))

                            cointypecoin = amount[2]
                            amount = int(amount[1])

                    # print cointype
                    # print amount

                # ....conversionfactor=1
                # ....cointype1='silvercoin'

                            if cointypecoin == 'Silver':
                                conversionfactor = 1
                                cointype1 = 'silvercoin'
                            elif cointypecoin == "'Gold'":
                                conversionfactor = 10
                                cointype1 = 'goldcoin'
                            elif cointypecoin == "'Platinum'":
                                conversionfactor = 50
                                cointype1 = 'platinacoin'
                            elif cointypecoin == 'Jade':
                                conversionfactor = 5000
                                cointype1 = 'jadecoin'
                            elif cointypecoin == "'Amberium'":

                                conversionfactor = 500000
                                cointype1 = 'amberiumcoin'
                            elif cointypecoin == "'Imperial'":
                                conversionfactor = 10000
                                cointype1 = 'imperial'
                            cashonhand = balanceavailable
                            quantity = amount * conversionfactor
                            if cashonhand >= quantity:
                                if amount <= 0:
                                    message = \
"I'm sorry, We do not accept checks with a zero balance."
                                elif amount == 1:
                                    message = \
'Okay, %s %s transfered to your account.' % (cointype, cointypecoin)
                                    inv.Message = 'CANCELED\n' \
+ inv.Message
                                    inv.Name = inv.Name \
+ ' (CANCELED)'
                                    bank.withdraw(payer, quantity)
                                    bank.deposit(payee, quantity)
                                else:

                                    message = \
'Okay, %s %s transfered to your account.' % (str(amount),
    cointypecoin)
                                    inv.Message = 'CANCELED\n' \
+ inv.Message
                                    inv.Name = inv.Name \
+ ' (CANCELED)'
                                    bank.withdraw(payer, quantity)
                                    bank.deposit(payee, quantity)
                            else:
                                message = \
"I'm sorry, but it appears that %s does not have enough money in his account to cover this transaction." \
% payer
                        else:
                            message = \
                                'The signature on this check is invalid.'
                            mail.send(1, payer,
                                    'The-Imperial-Bank-Of-Skud',
                                    'Dear Sir or Madam:\nIt has come to our attention that an attempt to cash an improperly signed check was made.  The check was made out to '
                                     + payee
                                    + 'and the attempt was made by '
                                     + activator.Name
                                    + '.  The value of the check was '
                                     + str(amount[1]) + ' '
                                    + str(amount[2]) + ' '
                                    + str(amount[3])
                                    + '.  We apologise for any trouble this may cause and would like to inform you that we are always at your service.'
                                     + '\n' + 'Sincerly,' + '\n'
                                    + 'The Imperial Bank of Skud')
                else:
                    message = 'This check has already been cashed!'
            else:
                message = 'Come back when you have a check to cash.'
                whoami.Say('z')

    if len(text) >= 3:
        amount = int(text[1])
        Type = ''
        for i in text[2:]:
            Type += i + ' '
        Type = Type[:len(Type) - 1]

        if Type[len(Type) - 1] == 's':
            Type = Type[:len(Type) - 1]

        try:

            exchange_rate = CoinTypes.get(Type.upper())
            if amount <= 0:
                message = \
                    'Usage "deposit <amount> <cointype>\n or deposit <issuer>\'s check"'
            elif activator.PayAmount(int(amount * exchange_rate)):

                bank.deposit(activatorname, int(amount
                             * exchange_rate / fees))
                bank.deposit(Skuds, int(amount * exchange_rate
                             - amount * exchange_rate / fees))

                message = \
                    '%d %s received, %d %s deposited to bank account. %s' \
                    % (amount, Type, int(amount / fees), Type,
                       random.choice(thanks_message))
            else:
                message = 'You would need %d gold' % (amount
                        * (exchange_rate / 10))
        except:
            message = \
                'Valid CoinTypes are Silver, Gold, Platinum, Jade, Amberium, Imperial Note, 10 Imperial Note, 100 Imperial Note.  \nPlease by sure to specify one of these types.'
    else:

        # message = 'Usage "deposit <amount> <cointype>\n or deposit <issuer>\'s check"'

        pass

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Withdraw money from the player's account.
def cmd_withdraw(argv):
    argc = len(argv)

    # Withdraw a certain number of imperial notes.
    if argc == 2:
        message = "Sorry, I don't know how to do that yet."
    # Withdraw a certain number of a certain coin.
    elif argc >= 3:
        coinName = getCoinNameFromArgs(argv[2:])
        exchange_rate = getExchangeRate(coinName)
        amount = int(argv[1])

        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return

        # Don't let the player withdraw negative money.
        if amount <= 0:
            whoami.Say("Hey, you can't withdraw a negative amount!")
            return

        # Make sure the player has sufficient funds.
        if bank.withdraw(activatorname, amount * exchange_rate):
            message = "%d %s withdrawn from your account. %s" \
                    % (amount, coinName, random.choice(thanks_message))

            # Drop the money and have the player pick it up.
            withdrawal = activator.Map.CreateObject(ArchType.get( \
                coinName.upper()), x, y)
            CFItemBroker.Item(withdrawal).add(amount)
            activator.Take(withdrawal)
        else:
            message = "I'm sorry, you don't have enough money."
    else:
        message = "Usage:\n" \
                "\twithdraw <amount in imperials>\n" \
                "\twithdraw <amount> <coin type>"

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Exchange money.
def cmd_exchange(text):
    if len(text) == 2:
        amount = int(text[1])
        if amount <= 0:
            message = \
                'Usage "exchange <amount>" (imperials to platinum coins)'
        elif amount > 10000:
            message = \
                'Sorry, we do not exchange more than 10000 imperials all at once.'
        else:
            inv = activator.CheckInventory('imperial')
            if inv:
                pay = CFItemBroker.Item(inv).subtract(amount)
                if pay:
                    exchange_rate = 10000

                    # Drop the coins on the floor, then try
                    # to pick them up. This effectively
                    # prevents the player from carrying too
                    # many coins.

                    id = activator.Map.CreateObject('platinum coin'
                            , x, y)
                    CFItemBroker.Item(id).add(int(amount
                            * (exchange_rate / 50)))
                    activator.Take(id)

                    message = random.choice(thanks_message)
                else:
                    message = 'Sorry, you do not have %d imperials' \
                        % amount
            else:
                message = 'Sorry, you do not have any imperials'
    else:
        message = \
            'Usage "exchange <amount>" (imperials to platinum coins)'

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Send checks.
def cmd_checks(text):
    balance = bank.getbalance(activatorname)
    if balance >= 100:
        bank.withdraw(activatorname, 100)

        mailmap = Crossfire.ReadyMap('/planes/IPO_storage')

        if mailmap:
            pack = activator.Map.CreateObject('package', 1, 1)
            pack.Name = \
                'IPO-package F: The-Imperial-Bank-of-Skud T: ' \
                + activator.Name
            pack.Teleport(mailmap, 2, 2)
            cheque = mailmap.ObjectAt(int(5), int(0))
            message = \
                'Your checks will be mailed to you shortly.  Thank you for your patronage.'
            cheque.Name = str(activator.Name + "'s Checkbook")
            cheques = cheque.CheckArchInventory('bankcard')
            cheques.Name = str(activator.Name + "'s Check")
            cheques.NamePl = str(activator.Name + "'s Checks")
            chequenew = cheque.InsertInto(pack)
    else:

        message = \
            'You do not have sufficient funds in your bank account.'

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Cash checks.
def cmd_cash(text):
    inv = activator.CheckInventory('bankcard')

    if inv:
        name1 = string.split(inv.Name, "'")
        payer = name1[0]
        information = inv.Message
        information = string.split(information, '\n')
        if information[0] != 'CANCELED':
            payee = string.split(information[0], ' ')

#   whoami.Say(str(payee))

            payee = payee[5]
            if payee != activator.Name:
                if payee.upper() != 'BEARER':
                    message = 'This check is not made out to you!'

                    mail.send(1, payee, 'The-Imperial-Bank-of-Skud'
                              , 'Dear Sir or Madam:' + '\n'
                              + 'It has come to our attention that an attempt to cash a check made out to you by someone other than you has been made.  The check was made out by'
                               + payer
                              + '; and, the attempt to cash it was made by'
                               + activator.Name
                              + '.  We apologise for any trouble this may cause, and would like to inform you that we are always at your service.'
                               + '''

''' + 'Sincerly,' + '\n'
                              + 'The Imperial Bank of Skud')
                    mail.send(1, payer, 'The-Imperial-Bank-of-Skud'
                              , 'Dear Sir or Madam:'
                              + '\nIt has come to our attention that an attempt to cash a check made out by you to '
                               + payee + ' has been made by '
                              + activator.Name
                              + '.  We apologise for any trouble this may cause; and, we would like to inform you that we are always at your service.'
                               + '\n' + '\n' + 'Sincerly,' + '\n'
                              + ' The Imperial Bank of Skud')
                if payee.upper() == 'BEARER':
                    payee = activator.Name
            if payee == activator.Name:
                balanceavailable = bank.getbalance(payer)
                amount = string.split(information[1], ' ')
                signer = string.split(information[2])
                signer = signer[1]
                if payer == signer:
                    cointype = amount[1]

            # print amount
    # ........whoami.Say(str(amount))

                    cointypecoin = amount[2]
                    amount = int(amount[1])

            # print cointype
            # print amount

        # ....conversionfactor=1
        # ....cointype1='silvercoin'

                    if cointypecoin == 'Silver':
                        conversionfactor = 1
                        cointype1 = 'silvercoin'
                    elif cointypecoin == "'Gold'":
                        conversionfactor = 10
                        cointype1 = 'goldcoin'
                    elif cointypecoin == "'Platinum'":
                        conversionfactor = 50
                        cointype1 = 'platinacoin'
                    elif cointypecoin == 'Jade':
                        conversionfactor = 5000
                        cointype1 = 'jadecoin'
                    elif cointypecoin == "'Amberium'":

                        conversionfactor = 500000
                        cointype1 = 'amberiumcoin'
                    elif cointypecoin == "'Imperial'":
                        conversionfactor = 10000
                        cointype1 = 'imperial'
                    cashonhand = balanceavailable
                    quantity = amount * conversionfactor
                    if cashonhand >= quantity:
                        if amount <= 0:
                            message = \
                                "I'm sorry, We do not accept checks with a zero balance."
                        elif amount == 1:
                            message = 'Okay, here is your %s %s.' \
                                % (cointype, cointypecoin)
                            inv.Message = 'CANCELED\n' + inv.Message
                            inv.Name = inv.Name + ' (CANCELED)'
                            bank.withdraw(payer, quantity)

                            id = \
                                activator.Map.CreateObject(cointype1,
                                    x, y)
                            activator.Take(id)
                        else:

                            message = 'Okay, here are your %s %s.' \
                                % (str(amount), cointypecoin)
                            inv.Message = 'CANCELED\n' + inv.Message
                            inv.Name = inv.Name + ' (CANCELED)'
                            bank.withdraw(payer, quantity)
                            id = \
                                activator.Map.CreateObject(cointype1,
                                    x, y)
                            CFItemBroker.Item(id).add(int(amount))
                            activator.Take(id)
                    else:

                        message = \
                            "I'm sorry, but it appears that %s does not have enough money in his account to cover this transaction." \
                            % payer
                else:
                    message = \
                        'The signature on this check is invalid.'
                    mail.send(1, payer, 'The-Imperial-Bank-Of-Skud'
                              ,
                              'Dear Sir or Madam:\nIt has come to our attention that an attempt to cash an improperly signed check was made.  The check was made out to '
                               + payee
                              + 'and the attempt was made by '
                              + activator.Name
                              + '.  The value of the check was '
                              + str(amount[1]) + ' '
                              + str(amount[2]) + ' '
                              + str(amount[3])
                              + '.  We apologise for any trouble this may cause and would like to inform you that we are always at your service.'
                               + '\n' + 'Sincerly,' + '\n'
                              + 'The Imperial Bank of Skud')
        else:
            message = 'This check has already been cashed!'
    else:
        message = 'Come back when you have a check to cash.'

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Script execution begins here.

# Find out if the script is being run by a deposit box or an employee.
if whoami.Name.find('Deposit Box') > -1:
    ScriptParm = Crossfire.ScriptParameters()

    if ScriptParm == 'Close':
        depositBoxClose()
    else:
        depositBoxOpen()
else:
    text = Crossfire.WhatIsMessage().split()

    if text[0] == 'help' or text[0] == 'yes':
        cmd_help()
    elif text[0] == 'profits':
        cmd_show_profits()
    elif text[0] == 'zero-balance':
        cmd_zero_balance()
    elif text[0] == 'balance':
        cmd_balance(text)
    elif text[0] == 'deposit':
        cmd_deposit(text)
    elif text[0] == 'withdraw':
        cmd_withdraw(text)
    elif text[0] == 'exchange':
        cmd_exchange(text)
    elif text[0] == 'checks':
        cmd_checks(text)
    elif text[0] == 'cash':
        cmd_cash(text)
    else:
        whoami.Say("Do you need help?")

    # Close bank database (required) and return.
    bank.close()
    Crossfire.SetReturnValue(1)
