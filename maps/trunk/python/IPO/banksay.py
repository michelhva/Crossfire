# banksay.py -- implements 'say' event for bank tellers
# Created by: Joris Bontje <jbontje@suespammers.org>
#
# Updated to use new path functions in CFPython and broken and
# modified a bit by -Todd Mitchell

import random

import Crossfire
import CFBank
import CFItemBroker

# Set up a few easily-settable configuration variables.
bank = CFBank.CFBank('ImperialBank_DB')
service_charge = 1  # Service charge for transactions (as a percentage)
fees = service_charge / 100.0 + 1

# Set up variables for a few commonly-accessed objects.
activator = Crossfire.WhoIsActivator()
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

# TODO: Look over checking code to make sure everything's okay.
def cmd_deposit(text):
    """Deposit a certain amount of money."""
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
    else:
        message = "What would you like to deposit?"
        Crossfire.AddReply("deposit <amount> <coin type>", "Some money.")

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
    else:
        whoami.Say("Hello, what can I help you with today?")
        Crossfire.AddReply("learn", "I want to learn how to use the bank.")

    # Close bank database (required) and return.
    bank.close()
    Crossfire.SetReturnValue(1)
