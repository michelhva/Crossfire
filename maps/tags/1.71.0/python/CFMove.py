# CFMove.py - various move-related functions.
#
# Copyright (C) 2007 Nicolas Weeger
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


'''Those represents the x/y offsets to move to a specific direction.'''
dir_x = [  0, 0, 1, 1, 1, 0, -1, -1, -1 ]
dir_y = [ 0, -1, -1, 0, 1, 1, 1, 0, -1 ]

def get_object_to(obj, x, y):
	'''This tries to move obj to the (x, y) location.
	Return value is:
	 * 0: object is on the spot
	 * 1: object moved towards the goal
	 * 2: object's movement was blocked.
	 '''
	# Move returns 0 for couldn't move, 1 for moved.
	return obj.MoveTo(x, y)
