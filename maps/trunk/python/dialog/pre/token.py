# -*- coding: utf-8 -*-
#token.py
# This is one of the files that can be called by an npc_dialog, 
# The following code runs when a dialog has a pre rule of 'token'
# The syntax is 
# ["token", "tokenname", "possiblevalue1", "possiblevalue2", etc]
# To deliver a True verdict, the token tokenname must be set to one of the 
# 'possiblevalue' arguments. This will normally have been done 
# with a previous use of settoken

status = self.getStatus(args[0])
for value in args[1:]:
    if (status == value) or (value == "*"):
        pass
    else:
        verdict = False
        break
