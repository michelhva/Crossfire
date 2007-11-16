# moment_only.py
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
# works only in specific moment of year/day. Periods are separated
# by comas. See wiki doc list of possible values
# exemple, to make an "apply" work only during Blizzard, new year and every morning:
# 
# arch event_apply
# title Python
# slaying /python/tod/filter_one_period.py
# name The Season of New Year,The Season of the Blizzard,Morning
# end
import Crossfire
import string

now = Crossfire.GetTime()
parameters = string.split(Crossfire.ScriptParameters(),",")
current = [Crossfire.GetMonthName(now[1]),Crossfire.GetWeekdayName(now[5]),Crossfire.GetSeasonName(now[7]),Crossfire.GetPeriodofdayName(now[8])]
#Crossfire.Log(Crossfire.LogDebug , "Seasons to check for are %s" %parameters)
#Crossfire.Log(Crossfire.LogDebug , "now is %s" %current)

#default: cancel operation (<>0). If non empty intersection, we have a match, set to continur operation (0)
Crossfire.SetReturnValue(1)
if (set(parameters) & set(current)):
	Crossfire.SetReturnValue(0)


