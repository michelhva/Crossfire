"""
Created by: Joris Bontje <jbontje@suespammers.org>

This module stores bank account information. Player accounts are stored in
the player file using the 'balance' key. Other accounts (for guilds) are
stored in the original bank file using the 'shelve' library.

Since the original implementation stored player accounts using the 'shelve'
library as well, this module also converts old bank accounts to new ones.
"""

import os.path
import shelve

import Crossfire

class CFBank:
    def __init__(self, bankfile):
        self.bankdb_file = os.path.join(Crossfire.LocalDirectory(),
                bankfile)
        self.bankdb = shelve.open(self.bankdb_file)

    def deposit(self, user, amount):
        if not user in self.bankdb:
            self.bankdb[user] = amount
        else:
            balance = self.bankdb[user]
            self.bankdb[user] = balance + amount

    def withdraw(self, user, amount):
        if user in self.bankdb:
            if self.bankdb[user] >= amount:
                balance = self.bankdb[user]
                self.bankdb[user] = balance - amount
                return 1
        return 0

    def getbalance(self, user):
        if user in self.bankdb:
            return self.bankdb[user]
        else:
            return 0

    def remove_account(self, user):
        if user in self.bankdb:
            del self.bankdb[user]
            Crossfire.Log(Crossfire.LogDebug,
                          "%s's bank account removed." % user)
            return 1
        else:
            return 0

    def close(self):
        self.bankdb.close()


def convert_bank(player):
    """Move a player's balance from the bank file to the player file."""
    bank = CFBank('ImperialBank_DB')
    old_balance = bank.getbalance(player.Name)
    if old_balance > 0:
        Crossfire.Log(Crossfire.LogInfo,
                "Converting bank account for %s with %d silver" \
                        % (player.Name, old_balance))
        player.WriteKey("balance", str(old_balance), 1)
        bank.remove_account(player.Name)
    bank.close()
    return old_balance

def balance(player):
    """Return the balance of the given player's bank account."""
    try:
        balance_str = player.ReadKey("balance")
        return int(balance_str)
    except ValueError:
        # If 'balance' key does not exist, try to convert from bank file.
        return convert_bank(player)

def deposit(player, amount):
    """Deposit the given amount to the player's bank account."""
    if amount < 0:
        raise ValueError("Deposits must be positive")
    new_balance = balance(player) + int(amount)
    player.WriteKey("balance", str(new_balance), 1)

def withdraw(player, amount):
    """Withdraw the given amount from the player's bank account."""
    if amount < 0:
        raise ValueError("Withdrawals must be positive")
    new_balance = balance(player) - int(amount)
    if new_balance < 0:
        return False
    else:
        player.WriteKey("balance", str(new_balance), 1)
        return True
