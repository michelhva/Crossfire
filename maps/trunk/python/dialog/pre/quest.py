# -*- coding: utf-8 -*-
#quest.py
# This is one of the files that can be called by an npc_dialog, 
# The following code runs when a dialog has a pre rule of 'quest'
# The syntax is ["quest", "questname", "queststage"]
# All arguments are required, questname must be a quest that is 
# defined by one of the .quests files queststage must be a step 
# number in that quest
# To deliver a True verdict, the player must be at or past queststage on questname

questname = args[0]
stage = args[1]
if stage == "complete":
    # todo: implement this
    pass
if character.QuestGetState(questname) < int(stage):
    verdict = False