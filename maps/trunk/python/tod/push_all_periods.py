# push_all_periods.py
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
# This script make the event it is attached to (not global!)
# trigger a connected value at specific moment of year/day.
# It will behave as if a button was "pushed" when entering period
# and "released" after the period. Typical use is to use it as
# event_time on a living object (that objects that triggers
# time events). 
# This script ensure button remains pushed for all period, and 
# get release after period. This works even is map is loaded
# in middle of period or map gets released from memory and put in 
# cache. In those case, event will just ensure that "current" state 
# correspond to expected state and correct status if needed.
# Note: the event must be inside an object
# which can find a path, using inventory, to a toplavel
# object that is on a Map. 
# 
# exemple, to make an "push" active only Mornings of The Season of New Year:
# 
# arch event_time
# title Python
# slaying /python/tod/push_all_periods.py
# msg
# {
# "connected":69
# "when":["Morning","The Season of New Year"]
# }
# endmsg
# end
# parameters are separated by comas. First one
# is connected value to trigger, other ones are
# one or more periods where state must become "pushed"
import Crossfire
import string
from CFTimeOfDay import TimeOfDay
import cjson
event = Crossfire.WhatIsEvent()
parameters = cjson.decode(event.Message)
alreadymatched = (event.Value!=0)
connected = int(parameters["connected"]))
match = TimeOfDay().matchAll(parameters["when"])
#pushdown if needed
if (match & (not alreadymatched)):
    op = event
    while (op.Env):
        op=op.Env
    map = op.Map
    map.TriggerConnected(connected,1,Crossfire.WhoAmI())
    event.Value=1
#release if needed
if ( (not match) & alreadymatched):
    op = event
    while (op.Env):
        op=op.Env
    map = op.Map
    map.TriggerConnected(connected,0,Crossfire.WhoAmI())
    event.Value=0

