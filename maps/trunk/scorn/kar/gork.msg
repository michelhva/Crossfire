{
  "location" : "gork_quest",
  "rules": [
  {
  "match" : "hi|hello",
  "pre" : [],
  "post" : [["gork_speak","hoard"]],
  "msg" : ["Youse want Gork's hoards?  I crush you..."]
  }, {
  "match" : "gork|hoard|yes|want",
  "pre" : [["gork_speak","hoard"]],
  "post" : [["gork_speak","mork"]],
  "msg" : ["Gork not like you...  I not as nice as Mork."]
  }, {
  "match" : "mork|nice",
  "pre" : [["gork_speak","mork"]],
  "post" : [["gork_speak","mork2"]],
  "msg" : ["Mork be Gork's friend.  Mork live in tower."]
  }, {
  "match" : "friend",
  "pre" : [["gork_speak","mork2"]],
  "post" : [["gork_speak","treasure"]],
  "msg" : ["Mork share Gork's treasure."]
  }, {
  "match" : "tower",
  "pre" : [["gork_speak","mork2"]],
  "post" : [],
  "msg" : ["Mork live in tower, Mork big, Mork friend."]
  }, {
  "match" : "treasure",
  "pre" : [["gork_speak","treasure"]],
  "post" : [["gork_speak",""],["can_get_key","1"]],
  "msg" : ["Only Gork can open treasure door."]
  }, {
  "match" : "*",
  "pre" : [],
  "post": [["gork_speak",""]],
  "msg" : ["Gork kill you!"]
  }
]}
