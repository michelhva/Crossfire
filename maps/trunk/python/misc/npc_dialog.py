# npc_dialog.py - Dialog helper class
#
# Copyright (C) 2007 David Delbecq
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
# This is a simple script that makes use of CFDialog.py and that receives
# parameters from a JSON inside the event message. Alternatively, the JSON
# parameters, if >= 4096 characters, can be stored in a separate file.
# Use the classical script parameter to specify relative location of dialog.
#
# An example of a map file entry is:
#
# arch guildmaster
# name Sigmund
# msg
# 
# endmsg
# x 11
# y 7
# resist_physical 100
# resist_magic 100
# weight 50000000
# friendly 1
# stand_still 1
# arch event_say
# name start/sigmund.msg
# title Python
# slaying /python/misc/npc_dialog.py
# end
# end
#
# An example of a JSON dialog similar to the one described in CFDialog.py is:
#
# {
#   "location" : "test_grandpa_01",
#   "rules": [
#   {
#     "match" : "hello|hi",
#     "pre" : [["hello","0"]],
#     "post" : [["hello","1"]],
#     "msg" : ["Hello, lad!","Hi, young fellow!","Howdy!"]
#   },
#   {
#     "match": "hello|hi",
#     "pre" :[["hello","1"]],
#     "post" :[["hello", "*"]],
#     "msg" : ["I've heard, you know, I'm not deaf *grmbl*"]
#   },
#   {
#     "match" : "*",
#     "pre" : [["hello","*"]],
#     "post" : [["hello", "*"]],
#     "msg" : ["What ?", "Huh ?", "What do you want ?"]
#   }
# ]}
#
# "match" is what CFDialog describes as a keyword, and corresponds with what
# the player/character says that the dialog will respond to.
#
# "pre" is a list of CFDialog preconditions that identifies flags that must be
# set to a particular value in order to trigger a response if a match is
# detected.
#
# "post" is a list of CFDialog postconditions that specify flags that are to be
# set if a response is triggered.
#
# Above, the first rule is applied if the player/character says "hello" or "hi"
# and if the "hello" flag is set to "0" (default).  When the rule is applied,
# the "hello" flag is then set to "1".
#
# The Double square braces ([[]]) around "pre" and "post" are required. "pre"
# and "post" are arrays of arrays. Each item in "pre" and "post" is an array of
# [variable,value].
#
# "msg" defines one or more responses that will be given if the rule triggers.
# When more than one "msg" value is set up, the NPC randomly selects which one
# to say each time the rule is applied.
#
# A relatively complex example of an npc_dialog.py dialog is given in the Gork
# treasure room quest.  See ../scorn/kar/gork.msg in particular as it
# demonstrates how multiple precondition flag values may be exploited to
# produce non-linear and variable-path conversations that are less likely to
# frustrate a player.

import Crossfire
import os
from CFDialog import DialogRule, Dialog
import cjson

# Avoid DeprecationWarning: raising a string exception is deprecated by making
# a user-defined exception handler.
# 
class NPC_Dialog_Error(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

def ruleConnected(character,rule):
        m = character.Map
        m.TriggerConnected(rule.connected,1)
event = Crossfire.WhatIsEvent()
player = Crossfire.WhoIsActivator()
npc = Crossfire.WhoAmI()
if (Crossfire.ScriptParameters() != None):
    filename = os.path.join(Crossfire.DataDirectory(),Crossfire.MapDirectory(),Crossfire.ScriptParameters())
    try:
        f = open(filename,'rb')
    except:
        NPC_Dialog_Error('Unable to read %s' % filename)
    else:
        Crossfire.Log(Crossfire.LogDebug,"Reading from file %s" %filename)
        parameters=cjson.decode(f.read())
        f.close()
else:
    parameters = cjson.decode(event.Message)
location = parameters["location"];
speech = Dialog(player, npc, location)
index=0;

for jsonRule in parameters["rules"]:
    if (jsonRule.has_key("connected")):
        rule = DialogRule(jsonRule["match"], jsonRule["pre"], jsonRule["msg"], jsonRule["post"],None,ruleConnected);
        rule.connected = jsonRule["connected"]
    else:
        rule = DialogRule(jsonRule["match"], jsonRule["pre"], jsonRule["msg"], jsonRule["post"])
    speech.addRule(rule,index)
    index=index+1
        
if speech.speak(Crossfire.WhatIsMessage()) == 0:
    Crossfire.SetReturnValue(1)

