# These archetypes are presently modeled after the contents found in pit.arc in
# the arch/trunk/connect/Hole directory.  The difference is that this archetype
# is a 64 x 64 bit multi-tile HOLE.
#
# This is a "correct" HOLE-based manhole modelled after connect/Hole/pit.arc
# The animation does not work.  The hole function does work, and is aligned
# with the manhole graphic on the head tile.
#
Object manhole_closed_1
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.114
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 1
wc 4
maxsp 0
visibility 50
magicmap black
end
More
Object manhole_closed_1a
face manhole.114
x 1
end
More
Object manhole_closed_1b
face manhole.114
y 1
end
More
Object manhole_closed_1c
face manhole.114
x 1
y 1
end
#
# This is a "correct" HOLE-based manhole modelled after connect/Hole/pit.arc
# The animation does not work.  The hole function does work, and is aligned
# with the manhole graphic on the head tile.
#
Object manhole_open_1
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.111
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 1
move_on walk
wc 0
maxsp 1
visibility 50
magicmap black
end
More
Object manhole_open_1a
face manhole.111
x 1
end
More
Object manhole_open_1b
face manhole.111
y 1
end
More
Object manhole_open_1c
face manhole.111
x 1
y 1
end
#
# This animation works, but the x1, y1, x1 y1 pieces have the wrong face, so
# the player view of the closed manhole is messed up.  The animation takes
# place at x-1, y-1 instead of at x,y, and the hole is not aligned with the
# location of the head tile (which is misplaced).
#
Object manhole_closed_2
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.114
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 0
wc 4
maxsp 0
visibility 50
magicmap black
end
More
Object manhole_closed_2a
face manhole.111
x 1
end
More
Object manhole_closed_2b
face manhole.111
y 1
end
More
Object manhole_closed_2c
face manhole.111
x 1
y 1
end
#
# This animation does not work even though it is set up just like the
# manhole_closed_2 arch.
#
Object manhole_open_2
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.111
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 0
move_on walk
wc 0
maxsp 1
visibility 50
magicmap black
end
More
Object manhole_open_2a
face manhole.114
x 1
end
More
Object manhole_open_2b
face manhole.114
y 1
end
More
Object manhole_open_2c
face manhole.114
x 1
y 1
end
#
# These animations work, and look correct in-game, though the hole that
# things drop through is not aligned with the manhole which is the same
# as manhole_closed_1 except that the graphic is not corrupted.  All that
# changed from the above is that the faces for the non-head pieces have
# been removed.  Gridarta places the head at x,y and places "no face"
# indications at at x+1,y, x,y+1, and x+1,y+1.
#
# Specifying no face instead of blank.111 also works, except that in
# Gridarta, the non-head pieces are "no face" instead of yellow blanks. 
#
Object manhole_closed_3
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.114
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 0
wc 4
maxsp 0
visibility 50
magicmap black
end
More
Object manhole_closed_3a
blank.111
x 1
end
More
Object manhole_closed_3b
blank.111
y 1
end
More
Object manhole_closed_3c
blank.111
x 1
y 1
end
#
# This animation works with the same symptoms as manhole_closed_3.
#
Object manhole_open_3
name manhole
type 94
activate_on_push 1
activate_on_release 1
no_pick 1
face manhole.111
anim
manhole.111
manhole.112
manhole.113
manhole.114
mina
is_animated 0
move_on walk
wc 0
maxsp 1
visibility 50
magicmap black
end
More
Object manhole_open_2a
blank.111
x 1
end
More
Object manhole_open_2b
blank.111
y 1
end
More
Object manhole_open_2c
blank.111
x 1
y 1
end
