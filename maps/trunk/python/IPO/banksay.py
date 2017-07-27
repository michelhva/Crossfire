"""
Created by: Joris Bontje <jbontje@suespammers.org>

This module implements banking in Crossfire. It provides the 'say' event for
bank tellers, as well as a deposit box for quickly depositing money.
"""

import random

import Crossfire
import CFBank
import CFItemBroker

activator = Crossfire.WhoIsActivator()
whoami = Crossfire.WhoAmI()
x = activator.X
y = activator.Y

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

def getCoinNameFromArgs(argv):
    """Piece together several arguments to form a coin name."""
    coinName = str.join(" ", argv)
    # Remove the trailing 's' from the coin name.
    if coinName[-1] == 's':
        coinName = coinName[:-1]
    return coinName

def getExchangeRate(coinName):
    """Return the exchange rate for the given type of coin."""
    if coinName.upper() in CoinTypes:
        return CoinTypes[coinName.upper()]
    else:
        return None

def get_inventory(obj):
    """An iterator for a given object's inventory."""
    current_item = obj.Inventory
    while current_item != None:
        next_item = current_item.Below
        yield current_item
        current_item = next_item

def do_deposit(player, amount):
    """Deposit the given amount for the player."""
    with CFBank.open() as bank:
        bank.deposit(player.Name, amount)
        whoami.Say("%s credited to your account." \
                % Crossfire.CostStringFromValue(amount))

def deposit_box_close():
    """Find the total value of items in the deposit box and deposit."""
    total_value = 0
    for obj in get_inventory(whoami):
        if obj.Name != 'Apply' and obj.Name != 'Close':
            total_value += obj.Value * obj.Quantity
            obj.Teleport(activator.Map, 15, 3)
    do_deposit(activator, total_value)

def cmd_help():
    """Print a help message for the player."""
    whoami.Say("The Bank of Skud can help you keep your money safe. In addition, you will be able to access your money from any bank in the world! What would you like to do?")

    Crossfire.AddReply("balance", "I want to check my balance.")
    Crossfire.AddReply("deposit", "I'd like to deposit some money.")
    Crossfire.AddReply("withdraw", "I'd like to withdraw some money.")

def cmd_balance(argv):
    """Find out how much money the player has in his/her account."""
    balance = 0
    with CFBank.open() as bank:
        balance = bank.getbalance(activator.Name)
    if len(argv) >= 2:
        # Give balance using the desired coin type.
        coinName = getCoinNameFromArgs(argv[1:])
        exchange_rate = getExchangeRate(coinName)
        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return
        if balance != 0:
            balance /= exchange_rate * 1.0
            whoami.Say("You have %.3f %s in the bank." % (balance, coinName))
        else:
            whoami.Say("Sorry, you have no balance.")
    else:
        whoami.Say("You have %s in the bank." % \
                Crossfire.CostStringFromValue(balance))

def cmd_deposit(text):
    """Deposit a certain amount of money."""
    if len(text) >= 3:
        coinName = getCoinNameFromArgs(text[2:])
        exchange_rate = getExchangeRate(coinName)
        amount = int(text[1])
        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return
        if amount < 0:
            whoami.Say("Regulations prohibit negative deposits.")
            return

        # Make sure the player has enough cash on hand.
        actualAmount = amount * exchange_rate
        if activator.PayAmount(actualAmount):
            do_deposit(activator, actualAmount)
        else:
            whoami.Say("But you don't have that much in your inventory!")
    else:
        whoami.Say("What would you like to deposit?")
        Crossfire.AddReply("deposit <amount> <coin type>", "Some money.")

def cmd_withdraw(argv):
    """Withdraw money from the player's account."""
    if len(argv) >= 3:
        coinName = getCoinNameFromArgs(argv[2:])
        exchange_rate = getExchangeRate(coinName)
        amount = int(argv[1])
        if exchange_rate is None:
            whoami.Say("Hmm... I've never seen that kind of money.")
            return
        if amount <= 0:
            whoami.Say("Sorry, you can't withdraw that amount.")
            return

        # Make sure the player has sufficient funds.
        with CFBank.open() as bank:
            if bank.withdraw(activator.Name, amount * exchange_rate):
                message = "%d %s withdrawn from your account. %s" \
                        % (amount, coinName, random.choice(thanks_message))

                # Drop the money and have the player pick it up.
                withdrawal = activator.Map.CreateObject(
                        ArchType.get(coinName.upper()), x, y)
                CFItemBroker.Item(withdrawal).add(amount)
                activator.Take(withdrawal)
            else:
                message = "I'm sorry, you don't have enough money."
    else:
        message = "How much money would you like to withdraw?"
        Crossfire.AddReply("withdraw <amount> <coin name>", "This much!")

    whoami.Say(message)

def main_employee():
    text = Crossfire.WhatIsMessage().split()
    if text[0] == "learn":
        cmd_help()
    elif text[0] == "balance":
        cmd_balance(text)
    elif text[0] == "deposit":
        cmd_deposit(text)
    elif text[0] == "withdraw":
        cmd_withdraw(text)
    else:
        whoami.Say("Hello, what can I help you with today?")
        Crossfire.AddReply("learn", "I want to learn how to use the bank.")

# Find out if the script is being run by a deposit box or an employee.
if whoami.Name.find('Deposit Box') > -1:
    ScriptParm = Crossfire.ScriptParameters()
    if ScriptParm == 'Close':
        deposit_box_close()
else:
    Crossfire.SetReturnValue(1)
    try:
        main_employee()
    except ValueError:
        whoami.Say("I don't know how much money that is.")
