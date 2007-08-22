# CFDialog.py - Dialog helper class
#
# Copyright (C) 2007 Yann Chachkoff
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
# The author can be reached via e-mail at lauwenmark@gmail.com
#

# What is this about ?
#=======================
# This is a small set of utility classes, to help you creating
# complex dialogs. It is made for those who do not want to
# bother about complex programming, but just want to make a few
# dialogs that are better than the @match system used in the
# server.
#
# How to use this.
#=======================
# First, you need to import DialogRule and Dialog classes. Add the
# following line at the beginning of your script:
# from CFDialog import DialogRule, Dialog
# Then, you can go to the dialogs themselves. A dialog is made of
# several rules. Each rule is made of keywords, preconditions,
# postconditions, and answers.
# - Keywords are what the rule will be an answer to. For example, if
#   you want a rule to be triggered when the player will say "hi",
#   then "hi" is the keyword to use. You can associate more than a
#   keyword to a rule by concatenating them with the "|" character.
#   Finally, "*" is a special keyword that means: "match everything".
#   "*" is useful to provide generic answers.
# - Answers are what will be said when the rule is triggered. This is
#   what the NPC replies to the player. Answers are stored in a list.
#   When there is more than one answer in that list, one will be
#   selected at random.
# - Preconditions are flags that must match for the rule to be triggered.
#   Each precondition has a name and a value. The default value of a
#   precondition is "0". The flags are stored into each player, and will
#   survive between gaming sessions. They are useful to set the point in
#   a dialog reached by a player - you can see those as an "NPC memory".
#   All conditions must have a name and a value. The ":" and ";" characters
#   are forbidden. For a rule to be triggered, all the player's flags should
#   match the preconditions. If you give "*" as the value for a precondition,
#   it means that there is no need to match it.
#   A precondition is a list in the form: [key, value]. All preconditions
#   are stored in a list.
#   - Postconditions are the status changes to apply to the player's
#   conditions after the rule has been triggered. Their format is similar to
#   preconditions. A value of "*" means that the condition will not be touched.
#
# Once you have defined your rules, you have to assemble them into a dialog.
# Each dialog involves somebody who triggers it, somebody who answers, and
# has a unique name so it cannot be confused with other dialogs.
# Typically, the "one who triggers" will be the player, and the "one who
# answers" is an NPC the player was taking to. You are free to chose whatever
# you want for the dialog name, as long as it contains no space or special
# characters, and is not used by another dialog.
# You can then add the rules you created to the dialog. Rules are parsed in
# a given order, so you must add the most generic answer last.
#
# A simple example
#=================
# I want to create a dialog for an old man. If I say "hello" or "hi" for the
# first time, grandpa will greet me. If I say it for the second time, he'll
# grumble (because he's like that, you know :)). I also need a generic answer
# if I say whatever else.
# In this example, the player is stored in 'player', and the old man in 'grandpa'.
# What the player said is in 'message'.
#
## Dialog creation:
# speech = Dialog(player, grandpa, "test_grandpa_01")
#
## The first rule is the "hello" answer, so we place it at index 0 of the
## rules list. The precondition is that we never said hello before.
## The postcondition is to mark "hello" as "1", to remember we already
## greeted grandpa.
# prer = [["hello","0"]]
# postr = [["hello", "1"]]
# rmsg = ["Hello, lad!","Hi, young fellow!","Howdy!"]
# speech.addRule(DialogRule("hello|hi", prer, rmsg, postr),0)
#
## The second rule is the answer to an hello if we already said it before.
## Notice that we used "*" for the postcondition value, meaning that we
## are leaving it as it is.
# prer = [["hello","1"]]
# postr = [["hello", "*"]]
# rmsg = ["I've heard, you know, I'm not deaf *grmbl*"]
# speech.addRule(DialogRule("hello|hi", prer, rmsg, postr),1)
#
## And finally, the generic answer. This is the last rule of the list.
## We don't need to match any condition, and we don't need to change them,
## so we use "*" in both cases this time.
# prer = [["hello","*"]]
# postr = [["hello", "*"]]
# rmsg = ["What ?", "Huh ?", "What do you want ?"]
# speech.addRule(DialogRule("*", prer, rmsg, postr),2)
#
import Crossfire
import string
import random

class DialogRule:
    def __init__(self,keyword,presemaphores, message, postsemaphores):
        self.__keyword = keyword
        self.__presems = presemaphores
        self.__message = message
        self.__postsems= postsemaphores
    def getKeyword(self):
        return self.__keyword
    def getMessage(self):
        msg = self.__message
        l = len(msg)
        r = random.randint(0,l-1)
        return msg[r]
    def getPreconditions(self):
        return self.__presems
    def getPostconditions(self):
        return self.__postsems

class Dialog:
    def __init__(self,character,speaker,location):
        self.__character = character
        self.__location = location
        self.__speaker = speaker
        self.__rules = []

    def addRule(self, rule, index):
        self.__rules.insert(index,rule)

    def speak(self, msg):
        for rule in self.__rules:
            if self.isAnswer(msg, rule.getKeyword())==1:
                if self.matchConditions(rule)==1:
                    self.__speaker.Say(rule.getMessage())
                    self.setConditions(rule)
                    return 0
    def isAnswer(self,msg, keyword):
        if keyword=="*":
            return 1
        keys=string.split(keyword,"|")
        for ckey in keys:
            if ckey==string.lower(msg):
                return 1
        return 0

    def matchConditions(self,rule):
        for condition in rule.getPreconditions():
            status = self.getStatus(condition[0])
            if status!=condition[1]:
                if condition[1]!="*":
                    return 0
        return 1

    def setConditions(self,rule):
        for condition in rule.getPostconditions():
            key = condition[0]
            val = condition[1]
            if val!="*":
                self.setStatus(key,val)

    def getStatus(self,key):
        character_status=self.__character.ReadKey("dialog_"+self.__location);
        if character_status=="":
            return "0"
        pairs=string.split(character_status,";")
        for i in pairs:
            subpair=string.split(i,":")
            if subpair[0]==key:
                return subpair[1]
        return "0"

    def setStatus(self,key, value):
        character_status=self.__character.ReadKey("dialog_"+self.__location);
        finished=""
        ishere=0
        if character_status!="":
            pairs=string.split(character_status,";")
            for i in pairs:
                subpair=string.split(i,":")
                if subpair[0]==key:
                    subpair[1]=value
                    ishere=1
                if finished!="":
                    finished=finished+";"
                finished=finished+subpair[0]+":"+subpair[1]
        if ishere==0:
            if finished!="":
                finished=finished+";"
            finished=finished+key+":"+value
        self.__character.WriteKey("dialog_"+self.__location, finished, 1)
