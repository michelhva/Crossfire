# CFLog.py - CFLog class
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
from time import localtime, strftime, time

class CFLog:

	logdb_file = '/home/crossfire/var/crossfire/crossfirelog'
	logdb = {}

	def __init__(self):
		self.logdb = shelve.open(self.logdb_file)

	def create(self, name):
		date = strftime("%a, %d %b %Y %H:%M:%S CEST", localtime(time()))
		count=1
		self.logdb[name]=['unknown', date, count]

	def update(self, name, ip):
		date = strftime("%a, %d %b %Y %H:%M:%S CEST", localtime(time()))
		count=0
		if self.logdb.has_key(name):
			oldip, olddate, count=self.logdb[name]
		count+=1
		self.logdb[name]=[ip, date, count]

	def remove(self, name):
		if self.logdb.has_key(name):
			del self.logdb[name]
	
	def exist(self, name):
		if self.logdb.has_key(name):
			return 1
		else:
			return 0

	def info(self, name):
		if self.exist(name):
			ip, date, count=self.logdb[name]
			return ip, date, count
