# Script for the lockable door and key.
# Ideas courtesy Yann Chachkoff.
#
# Copyright 2012 Nicolas Weeger
# Released as GPL
#
# Lockable doors have 2 components:
# - doors, which have the 'lockable 1' attribute
# - locking keys, which have a apply handler pointing to this script
#
# Rules are as follow:
# - the 'slaying' field is used to match doors and keys
# - locking keys start blank (empty slaying)
# - applying a blank key to an unlocked door locks the door and assigns the door to the key,
#  the key gets a '(used)' appended to its name
# - applying a valid key to a locked/unlocked door unlocks/locks
# - locked doors must be alive, unlocked mustn't be
# - the 'other_arch' field is used to link locked and unlocked variants
# - the following fields are changed when un/locking takes place: name, move_block,
#  move_allow, face. This allows customization of the doors (prevent walk but not fly, for instance)
#
# Right now, the following archetypes use this system:
# lockable_vdoor, lockable_vdoor_locked, lockable_hdoor, lockable_hdoor_locked, locking_key
#
# Feel free to use those as a base.
#
# Note: using a DOOR (type 23) instead of a LOCKED_DOOR (type 20) doesn't seem to
# work, since any key can remove the door - not the desired behaviour.

import Crossfire

Crossfire.SetReturnValue(1)

def get_door(me, direction):
    map = me.Map
    x = me.X
    y = me.Y

    if direction==2:
        ob = map.ObjectAt(x+1, y-1)
    elif direction==3:
        ob = map.ObjectAt(x+1, y)
    elif direction==4:
        ob = map.ObjectAt(x+1, y+1)
    elif direction==5:
        ob = map.ObjectAt(x, y+1)
    elif direction==6:
        ob = map.ObjectAt(x-1, y+1)
    elif direction==7:
        ob = map.ObjectAt(x-1, y)
    elif direction==8:
        ob = map.ObjectAt(x-1, y-1)
    else:
        ob = map.ObjectAt(x, y-1)
    return ob

def give_properties(who, lock):
  if lock == who.Archetype.Clone.Alive:
    arch = who.Archetype
  else:
    arch = who.OtherArchetype
  who.MoveType = arch.Clone.MoveType
  who.MoveBlock = arch.Clone.MoveBlock
  who.MoveAllow = arch.Clone.MoveAllow
  who.Alive = arch.Clone.Alive
  who.Name = arch.Clone.Name
  who.Face = arch.Clone.Face

def handle_key():
  key = Crossfire.WhoAmI()
  who = Crossfire.WhoIsActivator()

  door = get_door(who, who.Facing)

  while door != None:
    if door.ReadKey('lockable') == '1':
      break
    door = door.Above

  if door == None:
    who.Write('There is no lock here')
  else:
    if door.Alive:
      # door is locked, check if key matches
      if door.Slaying != key.Slaying:
        who.Write("You can't use this %s on this %s"%(key.Name, door.Name))
      else:
        who.Write('You unlock the %s'%(door.Name))
        give_properties(door, 0)
    else:
      # door is unlocked, a key can lock if blank or matching
      if key.Slaying == '' or key.Slaying == None or key.Slaying == door.Slaying:
        if key.Slaying == '' or key.Slaying == None:
          key.Slaying = door.Slaying
          key.Name = key.Name + " (used)"
        who.Write('You lock the %s'%(door.Name))
        give_properties(door, 1)
      else:
        who.Write("You can't use this %s on this %s"%(key.Name, door.Name))

handle_key()
