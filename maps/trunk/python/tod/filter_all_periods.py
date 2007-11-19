# filter_all_periods.py
#
# Copyright 2007 by David Delbecq
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
#
#
# Uses JSON notation for parameters
# This script make the event it is attached to (not global!)
# works only in specific moment of year/day
# exemple, to make an "apply" work only on 
# Morning of each Day of the moon occuring during The Season of the Blizzard:
# 
# arch event_apply
# title Python
# slaying /python/tod/filter_all_periods.py
# msg
# {
# "when":["Moon","The Season of the Blizzard","Morning"]
# }
# endmsg
# end
import Crossfire
import string
from CFTimeOfDay import TimeOfDay
import cjson
parameters = cjson.decode(Crossfire.WhatIsEvent().Message)
#default: allow operation (0)
Crossfire.SetReturnValue(0)
if TimeOfDay().matchAny(parameters["when"]):
	Crossfire.SetReturnValue(1)
