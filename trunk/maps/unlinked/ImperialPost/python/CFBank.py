# CFBank.py - CFBank class
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

import shelve

class CFBank:

	bankdb_file = '/home/crossfire/var/crossfire/crossfirebank'
	bankdb = {}

	def __init__(self):
		self.bankdb = shelve.open(self.bankdb_file)

	def deposit(self, user, amount):
		if not self.bankdb.has_key(user):
			self.bankdb[user]=amount
		else:
			temp=self.bankdb[user]
			self.bankdb[user]=temp+amount

	def withdraw(self, user, amount):
		if self.bankdb.has_key(user):
			if (self.bankdb[user] >= amount):
				temp=self.bankdb[user]
				self.bankdb[user]=temp-amount
				return 1
		return 0

	def getbalance(self,user):
        	if self.bankdb.has_key(user):
            		return self.bankdb[user]
        	else:
            		return 0
