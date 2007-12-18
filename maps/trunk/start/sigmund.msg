{
  "location" : "newbie_sigmund",
  "rules": [
  {
  "comment" : "**** bindings object thread ****",
  "match" : "yes|bind",
  "pre" : [["proposal","bindings"]],
  "post" : [["proposal",""]],
  "msg" : ["Binding is the way to bind a keyboard
key to a crossfire action. Lots of
actions are available in crossfire. Use
the 'help commands' to know about them.
To bind the command 'apply', type
bind apply
then hit the key you want to bind apply
to. Various clients for crossfire 
also provide a custom menu to easily
manipulate bindings."]
  }, {
  "comment" : "**** apply object thread ****",
  "match" : "apply|more",
  "pre" : [["proposal","apply"]],
  "post" : [["proposal","bindings"]],
  "msg" : ["Some objects in crossfire reacts to 'Applying'.
To apply an object (lever, sign, ...), just
stand above it and type
apply
This will apply the object you are
standing upon. Of course, it might
be quite cumbersome to do that again
and again. The easiest way is to use
the binding of the client. Do you
want to know about that?"]
  }, {
  "comment" : "**** saving thread ****",
  "match": "yes|saving",
  "pre" :[["proposal","saving"]],
  "post" :[["proposal",""]],
  "msg" : ["You can not use a bed to reality
until you have earned at least
one experience point. You must
use a bed to reality at least
once to have you character saved.
You can find beds to reality
just southwest of here.
You may also want to get
extra information on how to
apply an object in crossfire."]
  }, {
  "comment" : "**** bed to reality thread ****",
  "match": "yes|more",
  "pre" :[["proposal","bed"]],
  "post" :[["proposal","bed2"]],
  "msg" : ["A bed to reality is the way you quit
the crossfire world to get back to
your real life. While you are in
real life, your character is asleep.
Nothing can happens to it. But
beware for items! Want to know more?"]
  }, {
  "match": "yes|more",
  "pre" :[["proposal","bed2"]],
  "post" :[["proposal","saving"]],
  "msg" : ["All items you could have
left on the ground may disappear
or be stolen by other players. If
you want to keep them with you,
they must be put in your inventory
or in your permanent apartments.
Do you want me to tell you about
player saving?"]
  }, {
  
  
  "comment" : "**** food thread ****",
  "match": "yes|food|eat",
  "pre" :[["proposal","food"]],
  "post" :[["proposal",""]],
  "msg" : ["There are plenty of snacks in
the wild world (and in shops).
Before going to adventure, always
bring your bag of healthy food
with you. If you forget to eat,
you might become a bit crazy and
eat whatever come by your hand. 
Take care of poisonned food my lad!
I think i saw a safe snack nearby :)
"]
  }, {


  "comment" : "**** healing thread ****",
  "match": "yes|heal",
  "pre" :[["proposal","heal"]],
  "post" :[["proposal","heal2"]],
  "msg" : ["You have hit points in crossfire.
If you don't heal after losing life points,
you might die. Your character heals
naturally. You can also use
potions or spells to heal faster.
Praying on your god's altar might
also proves usefull. You want me
to instruct you on dangers in
crossfire world? Or, maybe, on
natural healing?"]
  }, {
  "match": "danger|damage",
  "pre" :[["proposal","heal2"]],
  "post" :[["proposal","food"]],
  "msg" : ["Most wild monsters deal damages.
Some of them deal damage faster
than other ones. Traps and spells 
can also deal damage. And food too!
Not only is there poisoned food
in crossfire, but you *need* to
eat! Wanna get some info on eating?
"]
  }, {
  "match": "natural|heal",
  "pre" :[["proposal","heal2"]],
  "post" :[["proposal","food"]],
  "msg" : ["You heal naturally on
bigworld. Your wounds will close
naturally. As one say, to stay
healthy, eat often. Or maybe that
was \"one orc chop a day, always
healthy\"? Anyway, there is a
wide variety of snacks for
adventurers, but be careful. 
Wanna get some info on food?
"]
  }, {
  "match": "yes",
  "pre" :[["proposal","heal2"]],
  "post" :[],
  "msg" : ["Yes what?",
  "Yes i want natural healing?
Yes i want to know about
danerous areas? Are you sure
you are listening to me?",
  "I once killed a kobold, you know?"]
  }, {
  "comment" : "list of generic subjects player might want to abord. 
               They all end with a 'proposal' of subject, that launch
               the subject thread is user wants to",
  "match": "bed|reality",
  "pre" :[["dialog","started"]],
  "post" :[["proposal","bed"]],
  "msg" : ["A bed to reality is the way you quit
the crossfire world. 
Want to know about it?"]
  }, {
  "match": "bind",
  "pre" :[["dialog","started"]],
  "post" :[["proposal","bindings"]],
  "msg" : ["Bindings links your keyboard
to specific crossfire commands. 
Want to know about it?"]
  }, {
  "match": "apply|use|item",
  "pre" :[["dialog","started"]],
  "post" :[["proposal","apply"]],
  "msg" : ["You need to learn how to use
items if you want to interract
with the crossfire world. 
Want to learn how to do it?"]
  }, {
  "match": "heal|wound",
  "pre" :[["dialog","started"]],
  "post" :[["proposal","heal"]],
  "msg" : ["Healing is important, if you don't
heal, D.E.A.T.H will get you!
Want to know about it?"]
  }, {
  "match" : "no",
  "pre" : [["dialog","started"]],
  "post" : [["proposal",""]],
  "msg" : ["That's ok pal, want something more?",
"So you think you know everything?\nThose children!",
"Adventurers, all the same, no time\n for listening old wise Sigmund"]
  }, {
  "match" : "spell|cast",
  "pre" : [["dialog","started"]],
  "post" : [["proposal",""]],
  "msg" : ["I am a old wise man, and as a
the old wise man, i give you
this advice. Stay away from magic!
It burns your soul, kill you teammates
and, i you use the magic of the gods,
it can destroy you. Wild beasts
sometimes use magical powers, be
carefull and if possible use amulets
or rings to protect you. My grand
grand cousin has a magic shop, 
\"The Lord of The Rings\", it full
of usefull stuffs. A bit expensive
however!"]
  }, {
  "match" : "*",
  "pre" : [["dialog","0"]],
  "post" : [["dialog", "started"]],
  "msg" : ["Hello, dear newbie. I am a NPC.
This mean you can interact with
me. To do this, just speak with me.
Like all NPCs in crossfire, i react
to some words you could say when
you are near me (using the
'say <message>' command)."]
  }, {
  "match" : "*",
  "pre" : [["dialog","started"]],
  "post" : [["proposal","bed"]],
  "msg" : ["I know everything about what
you should do next, where to heal,
how to bind keys, how to use items,
beds to reality and experience.
Would you like me to tell you
about beds to reality? That's 
important in crossfire."]
  }
]}

