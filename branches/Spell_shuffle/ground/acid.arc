# a specific type of spell effect (cone) which operates
# as a ground object.
Object acid
type 102
subtype 7
level 1
move_on walk
wc -30
name acid
face acid.111
animation acid
smoothface acid.111 acid_S.111
smoothface acid.112 acid_S.112
smoothface acid.113 acid_S.113
smoothface acid.114 acid_S.114
smoothface acid.115 acid_S.115
smoothlevel 28
speed 0.2
no_pick 1
attacktype 64
duration 60
dam 3
is_floor 1
move_block boat swim
end
#
Object permanent_acid
type 102
subtype 7
level 1
move_on walk
wc -30
lifesave 1
name acid
face acid.111
animation permanent_acid
speed 0.2
no_pick 1
smoothlevel 28
smoothface acid.111 acid_S.111
smoothface acid.112 acid_S.112
smoothface acid.113 acid_S.113
smoothface acid.114 acid_S.114
smoothface acid.115 acid_S.115
attacktype 64
hp 1
dam 3
is_floor 1
move_block boat swim
end
