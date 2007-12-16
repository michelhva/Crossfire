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
# This is a simple script that make use of CFDialog.py and receive it's
# parameters from a JSON inside the event message
# exemple
#{
#  "location" : "test_grandpa_01",
#  "rules": [
#  {
#    "match" : "hello|hi",
#    "pre" : [["hello","0"]],
#    "post" : [["hello","1"]],
#    "msg" : ["Hello, lad!","Hi, young fellow!","Howdy!"]
#  },
#  {
#    "match": "hello|hi",
#    "pre" :[["hello","1"]],
#    "post" :[["hello", "*"]],
#    "msg" : ["I've heard, you know, I'm not deaf *grmbl*"]
#  },
#  {
#    "match" : "*",
#    "pre" : [["hello","*"]],
#    "post" : [["hello", "*"]],
#    "msg" : ["What ?", "Huh ?", "What do you want ?"]
#  }
#]}
# The pre and post in rule are variable to test and set depending on that dialog rule.
# For example, first rule applie if "hello" is set to "0"  (default) and immediatly 
# sets it to "1" when rule is used.
# the Double [ around pre and post are needed. The pre and post are
# arrays of arrays. Each item in pre and post is an array of [variable,value]
# The match is the rule to apply to what user says

import Crossfire

from CFDialog import DialogRule, Dialog

import cjson
event = Crossfire.WhatIsEvent()
player = Crossfire.WhoIsActivator()
npc = Crossfire.WhoAmI()
parameters = cjson.decode(event.Message)
location = parameters["location"];
speech = Dialog(player, npc, location)
index=0;

for jsonRule in parameters["rules"]:
    speech.addRule(DialogRule(jsonRule["match"], jsonRule["pre"], jsonRule["msg"], jsonRule["post"]),index)
    index=index+1
        
if speech.speak(Crossfire.WhatIsMessage()) == 0:
    Crossfire.SetReturnValue(1)
