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
#   NOTE:  Answers may contain line breaks.  To insert one, use "\n".
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
#   in the game.  Each rule contains a list of one or more preconditions, if
#   any.  Supply an empty list [] if no preconditions exist, but otherwise,
#   each of the preconditions is required to be a list that contains at least
#   a flag name and one or more values in the following format: [["flag1",
#   "value1", "value2" ...], ["flag2", "value3"] ...] where "..." indicates
#   that the pattern may be repeated. The flag name is always the first item
#   in a precondition list.  ":" and ";" characters are forbidden in the flag
#   names and values.  For a rule to be triggered, all its preconditions must
#   be satisfied by settings in the player file.  To satisfy a precondition,
#   one of the precondition values must match the identified flag setting in
#   the player file. The default value of any precondition that has not been
#   specifically set in the player file is "0".  If one of the precondition
#   values is set to "*", a match is not required.
#
# - Postconditions are state changes to apply to the player file flags after
#   the rule triggers.  If a rule is not intended to set a flag, supply an
#   empty list [] when specifying postconditions, otherwise, postconditions
#   are supplied in a nested list that has the same format as the precondition
#   list except that each postcondition list only contains one value.  This is
#   because the other main difference is that whereas a precondition checks a
#   player file to see if a flag has a certain value, the postcondition causes
#   a value to be stored into the player file, and it does not make sense to
#   store more than one value into a single flag.  A value of "*" means that
#   the player file flag will not be changed.
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
# The following link points to a page on the Crossfire Wiki shows all the
# details needed to actually place this example in an actual game map:
#
#   http://wiki.metalforge.net/doku.php/cfdialog?s=cfdialog#a_simple_example
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
# Debugging
# =========
#
# When debugging, if changes are made to this file, the Crossfire Server must
# be restarted for it to register the changes.

import Crossfire
import string
import random
import sys
import CFItemBroker

class DialogRule:
    def __init__(self, keywords, presemaphores, messages, postsemaphores, suggested_response = None, required_response = None):
        self.__keywords = keywords
        self.__presems = presemaphores
        self.__messages = messages
        self.__postsems = postsemaphores
        self.__suggestions = suggested_response
        self.__requirements = required_response

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
        r = random.randint(0, l - 1)
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

    # Return the possible responses to this rule
    # This is when a message is sent.
    def getSuggests(self):
        return self.__suggestions

    # Return the required responses to this rule, this is like a suggestion, 
    # except that no other response will make sense in context (eg, a yes/no question)
    def getRequires(self):
        return self.__requirements

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
        self.__rules.insert(index, rule)

    # A function to call when saying something to an NPC to elicit a response
    # based on defined rules. It iterates through the rules and determines if
    # the spoken text matches a keyword.  If so, the rule preconditions and/or
    # prefunctions are checked.  If all conditions they define are met, then
    # the NPC responds, and postconditions, if any, are set.  Postfunctions
    # also execute if present.
    # some variable substitution is done on the message here, $me and $you 
    # are replaced by the names of the npc and the player respectively
    def speak(self, msg):
        for rule in self.__rules:
            if self.isAnswer(msg, rule.getKeyword()) == 1:
                print "Checking whether to say: ", rule.getMessage()
                if self.matchConditions(rule) == 1:
                    message = rule.getMessage()
                    message = message.replace('$me', self.__speaker.QueryName())
                    message = message.replace('$you', self.__character.QueryName())

                    self.__speaker.Say(message)
                    if rule.getRequires() == None:
                        if rule.getSuggests() != None:
                            self.__speaker.Say(rule.getSuggests())
                    else:
                        self.__speaker.Say(rule.getRequires())
                    self.setConditions(rule)
                    return 0
        return 1

    # Determine if the message sent to an NPC matches a string in the keyword
    # list. The match check is case-insensitive, and succeeds if a keyword
    # string is found in the message.  This means that the keyword string(s)
    # only need to be a substring of the message in order to trigger a reply.
    def isAnswer(self, msg, keywords):
        for ckey in keywords:
            if ckey == "*" or msg.lower().find(ckey.lower()) != -1:
                return 1
        return 0

    # Check the preconditions specified in rule have been met.  Preconditions
    # are lists of one or more conditions to check.  Each condition specifies
    # a check to perform and the options it should act on.
    # separate functions are called for each type of check.
    def matchConditions(self, rule):
        
        for condition in rule.getPreconditions():
            #try:
                print 'attempting to match rule', condition
                if condition[0] == 'quest':
                    print self.matchquest(condition[1:])
                    if self.matchquest(condition[1:]) == 0:
                        return 0
                elif condition[0] == 'item':
                    print self.matchitem(condition[1:])
                    if self.matchitem(condition[1:]) == 0:
                        return 0
                elif condition[0] == 'level':
                    print self.matchlevel(condition[1:])
                    if self.matchlevel(condition[1:]) == 0:
                        return 0
                elif condition[0] == 'age':
                    print self.checkage(condition[1:])
                    if self.checkage(condition[1:]) == 0:
                        return 0
                elif condition[0] == 'token':
                    print self.checktoken(condition[1:])
                    if self.checktoken(condition[1:]) == 0:
                        return 0
                else:
                    Crossfire.Log(Crossfire.LogError, "CFDialog: Preconditon called with unknown action.")
            #except:
            #    Crossfire.Log(Crossfire.LogDebug, "CFDialog: Bad Precondition")
            #    return 0
        return 1

    # Checks whether the token arg[0] has been set to any of the values in args[1] onwards
    def checktoken(self, args):
        status = self.getStatus(args[0])
        for value in args[1:]:
            if (status == value) or (value == "*"):
                return 1
                           
        return 0
    
    # This returns 1 if the token in args[0] was set at least
    # args[1] years
    # args[2] months
    # args[3] days
    # args[4] hours
    # args[5] minutes
    # ago (in game time).
    def checkage(self, args):
        # maximum months, days, hours, as defined by the server.
        # minutes per hour is hardcoded to approximately 60
        MAXTIMES = [Crossfire.Time.MONTHS_PER_YEAR, Crossfire.Time.WEEKS_PER_MONTH*Crossfire.Time.DAYS_PER_WEEK, 
                    Crossfire.Time.HOURS_PER_DAY, 60]
        # we have three times to consider, the old time, the current time, and the desired time difference.
        if len(args) != 6:
            return 0
        markername = args[0]
        oldtime = self.getStatus(markername).split("-")
        oldtime = map(int, oldtime)
        if len(oldtime) !=5:
            
            # The marker hasn't been set yet
            return 0

        desireddiff = map(int, args[1:])
        currenttime = (Crossfire.GetTime())[:5]
        actualdiff = []
 
        for i in range(5):
            actualdiff.append(currenttime[i]-oldtime[i])
            
        for i in range(4,0,-1):
        # let's tidy up desireddiff first
            if desireddiff[i] > MAXTIMES[i-1]:
                desireddiff[i-1] += desireddiff[i] // MAXTIMES[i-1]
                desireddiff[i] %= MAXTIMES[i-1]
        # Then actualdiff
            if actualdiff[i] < 0:
                actualdiff[i] += MAXTIMES[i-1]
                actualdiff[i-1] -=1

        print 'tidied up desired difference', desireddiff        
        print 'actual difference', actualdiff
        for i in range(5):
            if actualdiff[i] < desireddiff[i]:
                return 0
        return 1        
        

    # is passed a list, returns 1 if the character has at least args[1] of an item called args[0]
    # if args[1] isn't given, then looks for 1 of the item.
    def matchitem(self, args):
        itemname = args[0]
        if len(args) == 2:
            quantity = args[1]
        else:
            quantity = 1
        if itemname == "money":
            if self.__character.Money >= int(quantity):
                return 1
            else:
                return 0
        inv = self.__character.CheckInventory(itemname)
        if inv:
            if inv.Quantity >= int(quantity):
                return 1
        return 0

    # is passed a list, returns 1 if the player is at least at stage args[1] in quest args[0]
    def matchquest(self, args):
        questname = args[0]
        stage = args[1]
        print 'I am looking for stage ', stage, ' current stage is ', self.__character.QuestGetState(questname)
        if stage == "complete":
            # todo: implement this
            pass
        if self.__character.QuestGetState(questname) < int(stage):
            return 0
        return 1

    # is passed a list, returns 1 if the player is at least at level args[0] either overall or in the skill corresponding to any of the following arguments.
    def matchlevel(self, args):
        targetlevel = int(args[0])
        if len(args) == 1:
            if self.__character.Level >= targetlevel:
                return 1
            else:
                return 0
        else:
            pass
            #TODO

    # If a rule triggers, this function is called to make identified player
    # file changes, and to call any declared postfunctions to implement more
    # dramatic effects than the setting of a flag in the player file.
    def setConditions(self, rule):
        for condition in rule.getPostconditions():
         #   try:
            if 1:
                print 'trying to apply', condition
                action = condition[0]
                if action == 'quest':
                    self.setquest(condition[1:])
                elif action == 'connection':
                    self.__speaker.Map.TriggerConnected(int(condition[1]), 1, self.__speaker)
                elif action == 'takeitem':
                    self.takeitem(condition[1:])
                elif action == 'giveitem':
                    self.giveitem(condition[1:], False)
                elif action == 'givecontents':
                    self.giveitem(condition[1:], True)
                elif action == 'marktime':
                    self.marktime(condition[1:])
                elif action == 'settoken':
                    self.setStatus(condition[1],condition[2])
                else:
                    Crossfire.Log(Crossfire.LogError, "CFDialog: Post Block called with unknown action.")
            else:
            #except:
                Crossfire.Log(Crossfire.LogError, "CFDialog: Bad Postcondition")
                Crossfire.Log(Crossfire.LogError, sys.exc_info()[0])
                return 0

    def marktime(self, args):
        markername = args[0]
        timestamp = map(str, (Crossfire.GetTime())[:5])
        self.setStatus(markername, "-".join(timestamp))

    # moves player to stage args[1] of quest args[0]
    def setquest(self, args):
        questname = args[0]
        stage = args[1]
        if self.__character.QuestGetState(questname) == 0:
            print 'starting quest', questname, ' at stage ', stage
            self.__character.QuestStart(questname, int(stage))
        elif int(stage) > self.__character.QuestGetState(questname):
            print 'advancing quest', questname, 'to stage ', stage
            self.__character.QuestSetState(questname, int(stage))
        else:
            Crossfire.Log(Crossfire.LogError, "CFDialog: Tried to advance a quest backwards.")

    # places args[1] copies of item called args[0], into the inventory of the player. 
    # the item must be in the inventory of the NPC first for this to work.
    # if args[1] is not specified, assume this means 1 copy of the item.
    # if contents is 'true' then we don't give the player the item, but treat this item as a container, 
    # and give the player the exact contents of the container.
    def giveitem(self, args, contents):
        itemname = args[0]
        if len(args) == 2:
            quantity = int(args[1])
        else:
            quantity = 1
        if itemname == "money":
            # we can't guarentee that the player has any particular type of coin already
            # so create the object first, then add 1 less than the total.
            if quantity >= 50:
                id = self.__character.CreateObject('platinum coin')
                CFItemBroker.Item(id).add(int(quantity/50))
            if quantity % 50 > 0:
                id = self.__character.CreateObject('gold coin')
                CFItemBroker.Item(id).add(int((quantity % 50)/10))
            if quantity % 50 > 0:
                id = self.__character.CreateObject('silver coin')
                CFItemBroker.Item(id).add(int(quantity % 10))
        else:
            # what we will do, is increase the number of items the NPC is holding, then
            # split the stack into the players inventory.
            # first we will check if there is an NPC_Gift_Box, and look in there.
            lookin = self.__speaker.CheckInventory("NPC_Gift_Box")
            if lookin:
                inv = lookin.CheckInventory(itemname)
                if not inv:
                    # ok, the NPC has no 'Gift Box', we'll check the other items.
                    inv = self.__speaker.CheckInventory(itemname)
            else:
                inv = self.__speaker.CheckInventory(itemname)

            if inv:
                if contents:
                    nextob=inv.Inventory
                    while nextob:
                        # when giving the contents of a container, always give the 
                        # number of items in the container, not the quantity number.
                        quantity = nextob.Quantity
                        if quantity == 0:
                            # if quantity is 0, then we need to set it to one, otherwise bad things happen.
                            nextob.Quantity = 1
                            quantity = 1
                        newob = nextob.Clone(0)
                        newob.Quantity = quantity
                        newob.InsertInto(self.__character)
                        nextob=nextob.Below
                else:
                    if quantity == 0:
                        nextob.Quantity = 2
                        quantity = 1
                    else:
                        CFItemBroker.Item(inv).add(quantity+1)
                    newob = inv.Split(quantity)

                    newob.InsertInto(self.__character)
            else:
                # ok, we didn't find any 
                Crossfire.Log(Crossfire.LogError, "Dialog script tried to give a non-existant item to a player")
    
    # removes args[1] copies of item called args[0], this should only be used if 
    # you have checked the player has those items beforehand.
    # if args[1] is zero, will take all copies of the item from the first matching stack.
    # if it is not specified, take 1 copy of the item.
    def takeitem(self, args):
        itemname = args[0]
        if len(args) == 2:
            quantity = args[1]
        else:
            quantity = 1
        print 'trying to take ', quantity, ' of item ', itemname
        if itemname == "money":
            paid = self.__character.PayAmount(int(quantity))
            if paid == 0:
                Crossfire.Log(Crossfire.LogError, "Tried to make player pay more than they had")
        else:
            inv = self.__character.CheckInventory(itemname)
            if inv:
                if quantity == 0:
                    CFItemBroker.Item(inv).subtract(inv.Quantity)
                else:
                    CFItemBroker.Item(inv).subtract(int(quantity))
                # we might have been wearing an item that was taken.
                self.__character.Fix()
            else:
                Crossfire.Log(Crossfire.LogError, "Dialog script tried to remove non-existant item from player")


    # Search the player file for a particular flag, and if it exists, return
    # its value.  Flag names are combined with the unique dialog "location"
    # identifier, and are therefore are not required to be unique.  This also
    # prevents flags from conflicting with other non-dialog-related contents
    # in the player file.
    def getStatus(self, key):
        character_status=self.__character.ReadKey("dialog_"+self.__location);
        if character_status == "":
            return "0"
        pairs=character_status.split(";")
        for i in pairs:
            subpair=i.split(":")
            if subpair[0] == key:
                return subpair[1]
        return "0"

    # Store a flag in the player file and set it to the specified value.  Flag
    # names are combined with the unique dialog "location" identifier, and are
    # therefore are not required to be unique.  This also prevents flags from
    # conflicting with other non-dialog-related contents in the player file.
    def setStatus(self, key, value):
        if value == "*":
            return
        ishere = 0
        finished = ""
        character_status = self.__character.ReadKey("dialog_"+self.__location);
        if character_status != "":
            pairs = character_status.split(";")
            for i in pairs:
                subpair = i.split(":")
                if subpair[0] == key:
                    subpair[1] = value
                    ishere = 1
                if finished != "":
                    finished = finished+";"
                finished = finished + subpair[0] + ":" + subpair[1]
        if ishere == 0:
            if finished != "":
                finished = finished + ";"
            finished = finished + key + ":" + value
        self.__character.WriteKey("dialog_" + self.__location, finished, 1)
