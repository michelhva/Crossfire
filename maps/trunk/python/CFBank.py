# CFBank.py -- CFBank class
# Created by: Joris Bontje <jbontje@suespammers.org>

# CFBank uses the 'shelve' Python library to store persistent data. The
# shelved dictionary cannot be read and written to concurrently (so no '+=' or
# 'append' operations).
#
# The original implementation opened the database with 'writeback' set and
# called 'sync()' after each write operation. The current implementation does
# not set 'writeback' and instead requires 'close()' to be called before data
# is saved. This is the method recommended by the Python reference manual.

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
