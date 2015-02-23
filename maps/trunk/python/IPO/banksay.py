# banksay.py -- implements 'say' event for bank tellers
# Created by: Joris Bontje <jbontje@suespammers.org>
#
# Updated to use new path functions in CFPython and broken and
# modified a bit by -Todd Mitchell

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
# Return a string representing the given amount in silver.
def strAmount(amount):
    return Crossfire.CostStringFromValue(amount)

def processCheck():
    """Process a check in the player's inventory and return its value."""
    # Try to find the first check in the player's inventory.
    check = activator.CheckInventory("check")
    if check is None:
        whoami.Say("Come back when you have a check to deposit.")
        return 0

    payer = check.Name.split("'")[0]
    contents = check.Message.split("\n")

    # Don't let the player reuse a used check.
    if contents[0] == "CANCELED":
        whoami.Say("This check has already been used.")
        return 0

    # Make sure the check is written correctly (beware trailing newline).
    if (len(contents) < 4 + 1):
        whoami.Say("This check wasn't written correctly.")
        return 0
    else:
        payee = contents[1]
        amountString = contents[2].split(" ")
        signer = contents[3]
        try:
            amount = int(amountString[0]) * getExchangeRate(amountString[1])
            if amount <= 0:
                whoami.Say("No negative checks are allowed!")
                return 0
        except:
            whoami.Say("How much money is that?")
            return 0

    # Verify signature on check.
    if payer != signer:
        whoami.Say("The signature on this check is invalid.")
        mail.send(1, payer, "The-Imperial-Bank-Of-Skud",
                "Dear Sir or Madam:\nIt has come to our attention that an attempt to cash an improperly signed check from you was made by %s. We apologize for any trouble this may cause you.\n\nSincerely,\nThe Imperial Bank of Skud" \
                        % activator.Name)
        return 0

    # Only the payee can process the check (unless made out to anyone).
    if payee != activator.Name:
        if payee.upper() == "BEARER":
            payee = activator.Name
        else:
            whoami.Say("This check wasn't made out to you!")
            mail.send(1, payee, "The-Imperial-Bank-of-Skud",
                    "Dear Sir or Madam:\nIt has come to our attention that someone has attempted to cash a check made out to you. The check was made out by %s, but %s attempted to cash it. We apologise for any trouble this may cause, and would like to remind you that we're always at your service.\n\nSincerely,\nThe Imperial Bank of Skud" % (payer, activator.Name))
            return 0

    payerObj = Crossfire.FindPlayer(payer)
    if payerObj == None:
        whoami.Say("We don't know anyone named %s!" % payer)
        return 0
    if CFBank.withdraw(payerObj, amount):
        # Void the check to prevent further use.
        check.Name += " (used)"
        check.Message = "CANCELED\n" + check.Message
        return amount
    else:
        whoami.Say("It seems that %s doesn't have enough money." % payer)
        return 0

def get_inventory(obj):
    """An iterator for a given object's inventory."""
    current_item = obj.Inventory
    while current_item != None:
        next_item = current_item.Below
        yield current_item
        current_item = next_item

def deposit_box_close():
    """Find the total value of items in the deposit box and deposit."""
    total_value = 0
    for obj in get_inventory(whoami):
        if obj.Name != 'Apply' and obj.Name != 'Close':
            total_value += obj.Value * obj.Quantity
            obj.Teleport(activator.Map, 15, 3)

    # Calculate the amount of money actually deposited (due to fees).
    deposit_value = int(total_value / fees)
    CFBank.deposit(activator, deposit_value)
    whoami.Say("A fee of %d silver has been charged on this transaction." \
            % (total_value - deposit_value))

# ----------------------------------------------------------------------------
# Print a help message for the player.
def cmd_help():
    message = "The Bank of Skud can help you keep your money safe. In addition, you will be able to access your money from any bank in the world! A small service charge of %d percent will be placed on all deposits, though. What would you like to do?" % service_charge

    if activator.DungeonMaster:
        message += "\n\nAs the DM, you can also clear the bank's profits by using 'reset-profits'."

    Crossfire.AddReply("balance", "I want to check my balance.")
    Crossfire.AddReply("deposit", "I'd like to deposit some money.")
    Crossfire.AddReply("withdraw", "I'd like to withdraw some money.")
    Crossfire.AddReply("profits", "How much money has the Bank of Skud made?")

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Teach the player how to use checks.
def cmd_help_checks():
    message = "You'll need to order a checkbook if you haven't already. On the first line, write who you want to pay. On the next line, write the amount you want to pay. Finally, sign the check with your name on the last line."
    Crossfire.AddReply("order", "I want to order 100 checks (2 platinum).")
    whoami.Say(message)

def cmd_show_profits():
    """Say the total bank profits."""
    message = "To date, the Imperial Bank of Skud has made %s in profit." \
            % strAmount(bank.getbalance(Skuds))
    whoami.Say(message)

def cmd_reset_profit():
    """Reset the total bank profits."""
    if activator.DungeonMaster:
        bank.withdraw(Skuds, bank.getbalance(Skuds))
        whoami.Say("Profits reset!")
    else:
        whoami.Say("Only the dungeon master can reset our profits!")

def cmd_balance(argv):
    """Find out how much money the player has in his/her account."""
    balance = CFBank.balance(activator)
    if len(argv) >= 2:
        # Give balance using the desired coin type.
        coinName = getCoinNameFromArgs(argv[1:])
        exchange_rate = getExchangeRate(coinName)
        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return
        if balance != 0:
            balance /= exchange_rate * 1.0;
            whoami.Say("You have %.3f %s in the bank." % (balance, coinName))
        else:
            whoami.Say("Sorry, you have no balance.")
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
            CFBank.deposit(activator, int(actualAmount / fees))
            bank.deposit(Skuds, actualAmount - int(actualAmount / fees))

            message = "%d %s received, %s deposited to your account. %s" \
                    % (amount, coinName, strAmount(int(actualAmount / fees)),
                            random.choice(thanks_message))
        else:
            message = "But you don't have that much in your inventory!"
    elif len(text) == 2:
        if text[1] == 'check':
            transaction = processCheck()

            if transaction != 0:
                CFBank.deposit(activator, transaction)
                message = "%s deposited to your account." % \
                        strAmount(transaction)
            else:
                message = "Here's your check back."
        else:
            message = "What kind of money would you like to deposit?"
    else:
        message = "What would you like to deposit?"
        Crossfire.AddReply("deposit <amount> <coin type>", "Some money.")
        Crossfire.AddReply("deposit check", "A check, please.")

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Withdraw money from the player's account.
def cmd_withdraw(argv):
    argc = len(argv)

    # Withdraw a certain number of a certain coin.
    if argc >= 3:
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
        if CFBank.withdraw(activator, amount * exchange_rate):
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
        message = "How much money would you like to withdraw?"
        Crossfire.AddReply("withdraw <amount> <coin name>", "")

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
# Sell the player a personalized check book with checks.
def cmd_order_checks():
    if CFBank.withdraw(activator, 100):
        # Create a new checkbook and perform sanity checking.
        checkbook = Crossfire.CreateObjectByName("checkbook")
        checks = Crossfire.CreateObjectByName("check")

        if checkbook is None or checks is None:
            whoami.Say("Hmm... I seem to have run out of checks today. Please come back some other time.")
            Crossfire.Log(Crossfire.LogError, "Failed to create checks.")
            CFBank.deposit(activator, 100)
            return

        # Set various properties on the newly created checks.
        checkbook.Name = activator.Name + "'s Checkbook"
        checkbook.NamePl = activator.Name + "'s Checkbooks"
        checks.Name = activator.Name + "'s Check"
        checks.NamePl = activator.Name + "'s Checks"
        checks.Message = "Pay to the Order Of:"
        checks.Quantity = 100
        checks.InsertInto(checkbook)

        # Give the new check book to the player.
        checkbook.Teleport(activator.Map, x, y)
        message = "Here you go, 2 platinum withdrawn, enjoy!"
    else:
        message = "Each check book (100 checks) costs two platinum." \
                "You do not have enough money in your bank account."

    whoami.Say(message)

# ----------------------------------------------------------------------------
# Cash checks.
# TODO: Make sure this works as expected.
def cmd_cash(text):
    whoami.Say("Sorry, I can't do that yet.")

# ----------------------------------------------------------------------------
# Script execution begins here.

# Find out if the script is being run by a deposit box or an employee.
if whoami.Name.find('Deposit Box') > -1:
    ScriptParm = Crossfire.ScriptParameters()
    if ScriptParm == 'Close':
        deposit_box_close()
else:
    text = Crossfire.WhatIsMessage().split()

    if text[0] == "learn":
        cmd_help()
    elif text[0] == "checks":
        cmd_help_checks()
    elif text[0] == "profits":
        cmd_show_profits()
    elif text[0] == "reset-profits":
        cmd_reset_profit()
    elif text[0] == "balance":
        cmd_balance(text)
    elif text[0] == "deposit":
        cmd_deposit(text)
    elif text[0] == "withdraw":
        cmd_withdraw(text)
    elif text[0] == "order":
        cmd_order_checks()
    else:
        whoami.Say("Hello, what can I help you with today?")
        Crossfire.AddReply("learn", "I want to learn how to use the bank.")
        Crossfire.AddReply("checks", "Can you teach me about checks?")

    # Close bank database (required) and return.
    bank.close()
    Crossfire.SetReturnValue(1)
