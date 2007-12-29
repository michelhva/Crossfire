# These archetypes are presently modeled after the contents found in pit.arc in
# the arch/trunk/connect/Hole directory.  The difference is that this archetype
# is a 64 x 64 bit multi-tile HOLE.
#
# This animation works, but the x1, y1, x1 y1 pieces have the wrong face, so
# the player view of the closed manhole is messed up.  Not only that, but the
# animation takes place at x-1, y-1 instead of at x,y.
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
is_animated 0
wc 4
maxsp 0
visibility 50
magicmap black
end
More
Object manhole_closed_1a
face manhole.111
x 1
end
More
Object manhole_closed_1b
face manhole.111
y 1
end
More
Object manhole_closed_1c
face manhole.111
x 1
y 1
end
#
# This animation does not work, but is a copy of arch/connect/Hole/pit.arc so
# should work the same as that pit.
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
is_animated 0
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
