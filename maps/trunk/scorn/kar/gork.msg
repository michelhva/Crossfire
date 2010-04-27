{
  "location" : "gork_quest",
  "rules": [
  {
  "match" : ["hi","hello"],
  "pre" : [],
  "post" : [["settoken", "gork_speak","hoard"]],
  "msg" : ["Youse want Gork's hoards?  I crush you..."]
  }, {
  "match" : ["gork","hoard","yes","want"],
  "pre" : [["token", "gork_speak","hoard","key","pulp"]],
  "post" : [["settoken", "gork_speak","mork"]],
  "msg" : ["Gork not like you...  I not as nice as Mork."]
  }, {
  "match" : ["mork","nice"],
  "pre" : [["token", "gork_speak","friend","key","mork","tower","treasure"]],
  "post" : [["settoken", "gork_speak","friend"]],
  "msg" : ["Mork be Gork's friend.  Mork live in tower."]
  }, {
  "match" : ["friend"],
  "pre" : [["token", "gork_speak","friend","tower"]],
  "post" : [["settoken", "gork_speak","treasure"]],
  "msg" : ["Mork share Gork's treasure."]
  }, {
  "match" : ["tower"],
  "pre" : [["token", "gork_speak","friend","tower"]],
  "post" : [["settoken", "gork_speak","tower"]],
  "msg" : ["Mork live in tower.  Mork big, Mork friend."]
  }, {
  "match" : ["share","treasure"],
  "pre" : [["token", "gork_speak","treasure"]],
  "post" : [["settoken","gork_speak","key"],["settoken","can_get_key","1"]],
  "msg" : ["Only Gork and Mork can open treasure door."]
  }, {
  "match" : ["crush","door","open","treasure"],
  "pre" : [["token","gork_speak","hoard","key"]],
  "post" : [["settoken","gork_speak","pulp"]],
  "msg" : ["Gurrr... Gork beat you to pulp!"]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post": [["settoken","gork_speak","*"]],
  "msg" : ["Gork kill you!"]
  }
]}
