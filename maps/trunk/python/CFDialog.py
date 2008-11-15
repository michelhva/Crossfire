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

# What is CFDialog?
# =================
#
# This is a small set of utility classes, to help you create complex dialogs.
# It is made for those who do not want to bother about complex programming,
# but just want to make a few dialogs that are better than the @match system
# used in the server.
#
# How to use CFDialog
# ===================
#
# First, create a script that imports the DialogRule and Dialog classes. Add
# the following line at the beginning of your script:
#
#   from CFDialog import DialogRule, Dialog
#
# Next, build the dialog by creating a sequence of several rules made up of
# keywords, answers, preconditions, and postconditions.  Optionally, define
# prefunctions or postfunctions to enhance the capabilities of the rule.
#
# - Keywords are what the rule answers to.  For example, if you want a rule to
#   trigger when the player says "hi", then "hi" must appear in the keyword
#   list.  One or more keywords are specified in a string list in the form
#   ["keyword1", "keyword2" ...].  A "*" character is a special keyword that
#   means: "match everything", and is useful to create rules that provide
#   generic answers no matter what the player character says.
#
#   NOTE:  Like the @match system, CFDialog converts both keywords and the
#          things the player says to lowercase before checking for a match,
#          so it is never necessary to include multiple keywords that only
#          differ in case.
#
# - Answers are what the rule will respond, or say, to the player when it is
#   triggered.  This is what the NPC replies to the player. Answers are stored
#   in a list of one or more strings in the form ["Answer1", "Answer2" ...].
#   When there is more than one answer in that list, each time the rule is
#   triggered, a single random reply will be selected from the list.
#
# - Preconditions are flags that must match specific values in order for a
#   rule to be triggered. These flags persist across gaming sessions and are
#   useful for tracking the state of a conversation with an NPC.  Because of
#   this, it is possible for the same word to elicit different NPC responses
#   depending on how flags have been set.  If dialogs are set to use identical
#   locations, the flags and preconditions can be used by other NPC dialogs so
#   that other NPCs can detect that the player heard specific information from
#   another NPC.  The flags can also be used to help an individual NPC
#   remember what he has said to the player in the past.  Flag settings are
#   stored in the player file, so they persist as long as the character exists
#   in the game.  Each rule contains a list of one or more preconditions, and
#   each of the individual preconditions is itself a list of a flag name and
#   one or more values in the following format: [["flag1", "value1", "value2"
#   ...], ["flag2", "value3"] ...] where "..." indicates that the pattern may
#   be repeated. The flag name is always the first item in a precondition
#   list.  ":" and ";" characters are forbidden in the flag names and values.
#   For a rule to be triggered, all its preconditions must be satisfied by
#   settings in the player file.  To satisfy a precondition, one of the
#   precondition values must match the identified flag setting in the player
#   file. The default value of any precondition that has not been specifically
#   set in the player file is "0".  If one of the precondition values is set
#   to "*", a match is not required.
#
# - Postconditions are state changes to apply to the player file flags after
#   the rule triggers. The postcondition is a nested list that has the same
#   format as the precondition list except that each postcondition list only
#   contains one value.  This is because the other main difference is that
#   whereas a precondition checks a player file to see if a flag has a certain
#   value, the postcondition causes a value to be stored into the player file,
#   and it does not make sense to store more than one value into a single
#   flag.  A value of "*" means that the player file flag will not be changed.
#
# - A prefunction is an optional callback function that will be called when a
#   rule's preconditions are all matched, but before the rule is validated.
#   The callback can do additional tests, and should return 1 to allow the
#   rule to be selected, or 0 to block the rule.  The function arguments are
#   the player and the actual rule being tested.
#
# - A postfunction is an optional callback that is called when a rule has been
#   applied, and after the message is said. It can do additional custom
#   processing. The function arguments are the player and the actual rule
#   having been used.
#
# Once the rules are all defined, assemble them into a dialog.  Each dialog
# involves somebody who triggers it, somebody who answers, and also a unique
# name so it cannot be confused with other dialogs.  Typically, the "one who
# triggers" will be the player, and the "one who answers" is an NPC the player
# was taking to. You are free to choose whatever you want for the dialog name,
# as long as it contains no whitespace or special characters, and as long as
# it is not used by another dialog.  You can then add the rules you created to
# the dialog. Rules are parsed in a given order, so you must add the most
# generic answer last.
#
# A simple example
# ================
#
# If I want to create a dialog for an old man, I might want him to respond to
# "hello" or "hi" differently the first time the player meets the NPC, and
# differently for subsequent encounters. In this example, grandpa greets the
# player cordially the first time, but grumbles subequent times (because he's
# like that, you know :)). This example grandpa also has a generic answer for
# what ever else is said to him.  In the example, the player is stored in
# 'player', and the old man in 'grandpa', and the player said is in 'message'.
#
## Dialog creation:
# speech = Dialog(player, grandpa, "test_grandpa_01")
#
## The first rule is the "hello" answer, so we place it at index 0 of the
## rules list. The precondition is that we never said hello before. The
## postcondition saves a value of "1" into a player file flag named "hello"
## so grandpa remembers he has already met this player before.
#
# prer = [["hello","0"]]
# postr = [["hello", "1"]]
# rmsg = ["Hello, lad!","Hi, young fellow!","Howdy!"]
# speech.addRule(DialogRule(["hello","hi"], prer, rmsg, postr),0)
#
## The second rule is the answer to a greeting if he as already met the player
## before.  Notice that "*" is used for the postcondition value, meaning that
## the flag will remain set as it was prior to the rule triggering.
#
# prer = [["hello","1"]]
# postr = [["hello", "*"]]
# rmsg = ["I've heard, you know, I'm not deaf *grmbl*"]
# speech.addRule(DialogRule(["hello","hi"], prer, rmsg, postr),1)
#
## Finally, the generic answer is written. This is the last rule of the list.
## We don't need to match any condition, and don't need to change any flags,
## so we use "*" in both cases this time.
#
# prer = [["hello","*"]]
# postr = [["hello", "*"]]
# rmsg = ["What ?", "Huh ?", "What do you want ?"]
# speech.addRule(DialogRule(["*"], prer, rmsg, postr),2)
#
# A more complex example
# ======================
#
# A ./misc/npc_dialog.py script has been written that uses CFDialog, but
# allows the dialog data to be written in a slightly different format.
# ../scorn/kar/gork.msg is an example that uses multiple keywords and multiple
# precondition values.  Whereas the above example has a linear and predicable
# conversation paths, note how a conversation with Gork can fork, merge, and
# loop back on itself.  The example also illustrates how CFDialog can allow
# dialogs to affect how other NPCs react to a player.  ../scorn/kar/mork.msg
# is a completely different dialog, but it is part of a quest that requires
# the player to interact with both NPCs in a specific way before the quest
# prize can be obtained.  With the old @match system, once the player knew
# the key words, he could short-circuit the conversation the map designer
# intended to occur.  CFDialog constrains the player to follow the proper
# conversation thread to qualify to receive the quest reward.
#
import Crossfire
import string
import random

class DialogRule:
    def __init__(self, keywords, presemaphores, messages, postsemaphores, prefunction = None, postfunction = None):
        self.__keywords = keywords
        self.__presems = presemaphores
        self.__messages = messages
        self.__postsems= postsemaphores
        self.__prefunction = prefunction
        self.__postfunction = postfunction

    # The keyword is a string.  Multiple keywords may be defined in the string
    # by delimiting them with vertical bar (|) characters.  "*" is a special
    # keyword that matches anything.
    def getKeyword(self):
        return self.__keywords

    # Messages are stored in a list of strings.  One or more messages may be
    # defined in the list.  If more than one message is present, a random
    # string is returned.
    def getMessage(self):
        msg = self.__messages
        l = len(msg)
        r = random.randint(0,l-1)
        return msg[r]

    # Return the preconditions of a rule.  They are a list of one or more lists
    # that specify a flag name to check, and one or more acceptable values it
    # may have in order to allow the rule to be triggered.
    def getPreconditions(self):
        return self.__presems

    # Return the postconditions for a rule.  They are a list of one or more
    # lists that specify a flag to be set in the player file and what value it
    # should be set to.
    def getPostconditions(self):
        return self.__postsems

    # A prefunction is a callback that is run in order to implement complex
    # preconditions.  The value returned by the called function determines
    # whether or not the rule trigger.
    def getPrefunction(self):
        return self.__prefunction

    # A postfunction is a callback that is run only if the rule is triggered,
    # and only after the answer has been given to the player.  It may be used
    # to trigger some particular action as a result of the player saying the
    # right (or wrong) thing at a particular point in a dialog.
    def getPostfunction(self):
        return self.__postfunction

class Dialog:
    # A character is the source that supplies keywords that drive the dialog.
    # The speaker is the NPC that responds to the keywords. A location is an
    # unique identifier that is used to distinguish dialogs from each other.
    def __init__(self, character, speaker, location):
        self.__character = character
        self.__location = location
        self.__speaker = speaker
        self.__rules = []

    # Create rules of the DialogRule class that define dialog flow. An index
    # defines the order in which rules are processed.  FIXME: addRule could
    # very easily create the index.  It is unclear why this mundane activity
    # is left for the dialog maker.
    def addRule(self, rule, index):
        self.__rules.insert(index,rule)

    # A function to call when saying something to an NPC to elicit a response
    # based on defined rules. It iterates through the rules and determines if
    # the spoken text matches a keyword.  If so, the rule preconditions and/or
    # prefunctions are checked.  If all conditions they define are met, then
    # the NPC responds, and postconditions, if any, are set.  Postfunctions
    # also execute if present.
    def speak(self, msg):
        for rule in self.__rules:
            if self.isAnswer(msg, rule.getKeyword())==1:
                if self.matchConditions(rule)==1:
                    self.__speaker.Say(rule.getMessage())
                    self.setConditions(rule)
                    return 0
        return 1

    # Determine if the message sent to an NPC matches a string in the keyword
    # list. The match check is case-insensitive, and succeeds if a keyword
    # string is found in the message.  This means that the keyword string(s)
    # only need to be a substring of the message in order to trigger a reply.
    def isAnswer(self, msg, keywords):
        for ckey in keywords:
            if ckey=="*" or string.find(msg.lower(), ckey.lower()) != -1:
                return 1
        return 0

    # Check the preconditions specified in rule have been met.  Preconditions
    # are lists of one or more conditions to check.  Each condition specifies
    # a player file flag to check, and a list of one or more values that allow
    # the condition check to succeed.  If all preconditions are met, and if a
    # prefunction has been defined, it is also called to implement more
    # complex conditions that determine if the rule is allowed to trigger.
    def matchConditions(self, rule):
        for condition in rule.getPreconditions():
            status = self.getStatus(condition[0])
            values=condition[1:]
            for value in values:
                if (status==value) or (value=="*"):
                    break
            else:
                return 0
        if rule.getPrefunction() <> None:
                return rule.getPrefunction()(self.__character, rule)
        return 1

    # If a rule triggers, this function is called to make identified player
    # file changes, and to call any declared postfunctions to implement more
    # dramatic effects than the setting of a flag in the player file.
    def setConditions(self, rule):
        for condition in rule.getPostconditions():
            key = condition[0]
            val = condition[1]
            if val!="*":
                self.setStatus(key,val)
        if rule.getPostfunction() <> None:
                rule.getPostfunction()(self.__character, rule)

    # Search the player file for a particular flag, and if it exists, return
    # its value.  Flag names are combined with the unique dialog "location"
    # identifier, and are therefore are not required to be unique.  This also
    # prevents flags from conflicting with other non-dialog-related contents
    # in the player file.
    def getStatus(self, key):
        character_status=self.__character.ReadKey("dialog_"+self.__location);
        if character_status=="":
            return "0"
        pairs=string.split(character_status,";")
        for i in pairs:
            subpair=string.split(i,":")
            if subpair[0]==key:
                return subpair[1]
        return "0"

    # Store a flag in the player file and set it to the specified value.  Flag
    # names are combined with the unique dialog "location" identifier, and are
    # therefore are not required to be unique.  This also prevents flags from
    # conflicting with other non-dialog-related contents in the player file.
    def setStatus(self, key, value):
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

