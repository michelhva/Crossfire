# CFBank.py -- CFBank class
# Created by: Joris Bontje <jbontje@suespammers.org>
#
# Updated to use new path functions in CFPython -Todd Mitchell

import os.path
import shelve

import Crossfire

class CFBank:
    bankdb = {}

    def __init__(self, bankfile):
        self.bankdb_file = os.path.join(Crossfire.LocalDirectory(),
                bankfile)
        self.bankdb = shelve.open(self.bankdb_file, writeback=True)

    def deposit(self, user, amount):
        if not user in self.bankdb:
            self.bankdb[user] = amount
        else:
            balance = self.bankdb[user]
            self.bankdb[user] = balance + amount
        self.bankdb.sync()

    def withdraw(self, user, amount):
        if user in self.bankdb:
            if self.bankdb[user] >= amount:
                balance = self.bankdb[user]
                self.bankdb[user] = balance - amount
                self.bankdb.sync()
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
            self.bankdb.sync()
            return 1
        else:
            return 0
