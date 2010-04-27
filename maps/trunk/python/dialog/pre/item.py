# -*- coding: utf-8 -*-
#item.py
# This is one of the files that can be called by an npc_dialog, 
# The following code runs when a dialog has a pre rule of 'item'
# The syntax is ["item", "itemname", "numberrequired"]
# numberrequired is optional, if it is missing then 1 is assumed.
# To deliver a True verdict, the player must have at least numberrequired 
# copies of an item with name 'itemname'
# if the itemname is 'money' then as a special case, the check 
# is against the total value of coin held, in silver. In this 
# case the value of the coin must exceed numberrequired silver, 
# in any denominations
itemname = args[0]
if len(args) == 2:
    quantity = args[1]
else:
    quantity = 1
if itemname == "money":
    if character.Money < int(quantity):
        verdict = False
else:
    inv = character.CheckInventory(itemname)
    if inv:
        if inv.Quantity < int(quantity):
            verdict = False
    else:
        verdict = False