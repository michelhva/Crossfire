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

# The names of the most common coin types (must be least to greatest value).
# Do not include the least valuable coin (currently "SILVER").
commonCoinNames = ["GOLD", "PLATINUM", "JADE", "AMBERIUM"]

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
# Return a string representing the given amount in silver.
def strAmount(amount):
    # Find the most valuable coin type we can use (amount / value >= 1).
    # The [::-1] syntax is a hack to reverse 'commonCoinNames'.
    for coinName in commonCoinNames[::-1]:
        value = CoinTypes[coinName]
        if amount >= value:
            # Do not display the decimal point if it is not necessary.
            realValue = float(amount) / value
            if realValue != int(realValue):
                return "%.3f %s" % (realValue, coinName.lower())
            else:
                return "%d %s" % (int(realValue), coinName.lower())

    # If no suitable coin was found, use the base value (silver).
    return "%d %s" % (amount, "silver")

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
    message = "You can check your 'balance', 'deposit' or 'withdraw' your money, 'exchange' currency, 'cash' or buy 'checks', and find out the bank's 'profits'.\n\nAll transactions will be in imperial notes (1 note = 1000 gold) unless otherwise specified. A service charge of %d percent will be placed on all deposits." % service_charge

    if activator.DungeonMaster:
        message += "\n\nAs the DM, you can also clear the bank's profits by using 'reset-profits'."

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Show the profits made by the bank.
def cmd_show_profits():
    message = "To date, the Imperial Bank of Skud has made %s in profit." \
            % strAmount(bank.getbalance(Skuds))
    whoami.Say(message)

# ----------------------------------------------------------------------------
# Erase the profits made by the bank.
def cmd_reset_profit():
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
            message = "You have %.3f %s in the bank." % (balance, coinName)
        else:
            message = "Sorry, you have no balance."

        whoami.Say(message);
    else:
        whoami.Say("You have " + strAmount(balance) + " in the bank.")

# ----------------------------------------------------------------------------
# Deposit a certain amount of money or the value of a check.
# TODO: Look over checking code to make sure everything's okay.
def cmd_deposit(text):
    if len(text) >= 3:
        coinName = getCoinNameFromArgs(text[2:])
        exchange_rate = getExchangeRate(coinName)
        amount = int(text[1])

        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return

        # Don't let the player deposit negative money.
        if amount <= 0:
            whoami.Say("Regulations prohibit negative deposits.")
            return

        # Make sure the player has enough cash on hand.
        actualAmount = amount * exchange_rate
        if activator.PayAmount(actualAmount):
            bank.deposit(activatorname, int(actualAmount / fees))
            bank.deposit(Skuds, actualAmount - int(actualAmount / fees))

            message = "%d %s received, %s deposited to your account. %s" \
                    % (amount, coinName, strAmount(int(actualAmount / fees)),
                            random.choice(thanks_message))
        else:
            message = "But you don't have that much in your inventory!"

    elif len(text) == 2:
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
        else:
            message = "What type of money would you like to deposit?"
    else:
        message = "Usage:\n" \
                "\tdeposit <amount> <coin type>\n" \
                "\tdeposit check"

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
# TODO: Clean up code here.
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
# Send a checkbook to the player via the IPS.
# TODO: Make this work (check the maps!).
def cmd_checks():
    if bank.withdraw(activatorname, 100):
        mailmap = Crossfire.ReadyMap('/planes/IPO_storage')

        if not mailmap:
            whoami.Say("It seems that the IPO is on strike. " \
                    "Your payment has been refunded.")
            bank.deposit(activatorname, 100)
            return

        # Create an IPO package but don't mail it until we're ready.
        package = activator.Map.CreateObject('package', 1, 1)
        package.Name = "IPO-package F: The-Imperial-Bank-of-Skud T: %s" \
                % activator.Name

        # Create a check based on a template in the bank map.
        check = mailmap.ObjectAt(int(5), int(0))

        if check is None:
            whoami.Say("Hmm... I can't seem find my checkbook press... " \
                    "Your payment has been refunded.")
            bank.deposit(activatorname, 100)
            return

        # Mail the package.
        package.Teleport(mailmap, 2, 2)

        check.Name = str(activator.Name + "'s Checkbook")
        cheques = check.CheckArchInventory('bankcard')
        cheques.Name = str(activator.Name + "'s Check")
        cheques.NamePl = str(activator.Name + "'s Checks")
        chequenew = check.InsertInto(package)

        message = "Your checks have been mailed. Thank you!"
    else:
        message = "Checks cost 100 silver (2 platinum). " \
                "You do not have enough money in your bank account."

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Cash checks.
# TODO: Make sure this works as expected.
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

    # At the moment, the ATMs seem buggy and steal the players' money.
    # Temporarily disable ATMs until we fix the issue.

    if ScriptParm == 'Close':
        #depositBoxClose()
        pass
    else:
        whoami.Say("We're sorry, ATMs are out of order.")
        #depositBoxOpen()
else:
    text = Crossfire.WhatIsMessage().split()

    if text[0] == 'help' or text[0] == 'yes':
        cmd_help()
    elif text[0] == 'profits':
        cmd_show_profits()
    elif text[0] == 'reset-profits':
        cmd_reset_profit()
    elif text[0] == 'balance':
        cmd_balance(text)
    elif text[0] == 'deposit':
        cmd_deposit(text)
    elif text[0] == 'withdraw':
        cmd_withdraw(text)
    elif text[0] == 'exchange':
        cmd_exchange(text)
    elif text[0] == 'checks':
        cmd_checks()
    elif text[0] == 'cash':
        cmd_cash(text)
    else:
        whoami.Say("Do you need help?")

    # Close bank database (required) and return.
    bank.close()
    Crossfire.SetReturnValue(1)
