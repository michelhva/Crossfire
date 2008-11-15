{
  "location" : "gork_quest",
  "rules": [
  {
  "match" : ["hi","hello"],
  "pre" : [],
  "post" : [["gork_speak","hoard"]],
  "msg" : ["Youse want Gork's hoards?  I crush you..."]
  }, {
  "match" : ["gork","hoard","yes","want"],
  "pre" : [["gork_speak","hoard","key","pulp"]],
  "post" : [["gork_speak","mork"]],
  "msg" : ["Gork not like you...  I not as nice as Mork."]
  }, {
  "match" : ["mork","nice"],
  "pre" : [["gork_speak","friend","key","mork","tower","treasure"]],
  "post" : [["gork_speak","friend"]],
  "msg" : ["Mork be Gork's friend.  Mork live in tower."]
  }, {
  "match" : ["friend"],
  "pre" : [["gork_speak","friend","tower"]],
  "post" : [["gork_speak","treasure"]],
  "msg" : ["Mork share Gork's treasure."]
  }, {
  "match" : ["tower"],
  "pre" : [["gork_speak","friend","tower"]],
  "post" : [["gork_speak","tower"]],
  "msg" : ["Mork live in tower.  Mork big, Mork friend."]
  }, {
  "match" : ["share","treasure"],
  "pre" : [["gork_speak","treasure"]],
  "post" : [["gork_speak","key"],["can_get_key","1"]],
  "msg" : ["Only Gork and Mork can open treasure door."]
  }, {
  "match" : ["crush","door","open","treasure"],
  "pre" : [["gork_speak","hoard","key"]],
  "post" : [["gork_speak","pulp"]],
  "msg" : ["Gurrr... Gork beat you to pulp!"]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post": [["gork_speak",""]],
  "msg" : ["Gork kill you!"]
  }
]}
