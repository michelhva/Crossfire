{
  "location" : "gork_quest",
  "rules": [
  {
  "match" : "gork",
  "pre" : [["can_get_key","1"]],
  "post" : [["mork_speak","gork"]],
  "msg" : ["You know Gork?  You friend of Gork?"]
  }, {
  "match" : "yes|friend",
  "pre" : [["can_get_key","1"],["mork_speak","gork"]],
  "post" : [["mork_speak","key"]],
  "msg" : ["I bet Gork send you to get key.  Did he?"]
  }, {
  "match" : "yes",
  "pre" : [["can_get_key","1"],["mork_speak","key"]],
  "post" : [["mork_speak",""],["can_get_key","0"]],
  "msg" : ["Ok, I give you key.  Here, it in corner."],
  "connected" : 9
  }, {
  
  
  
  
  "match" : "hello|hi",
  "pre" : [],
  "post" : [],
  "msg" : ["Geesh, kill all my friends, that's not very nice.
Where did you learn your manners?"]
  }, {
  "match" : "friends",
  "pre" : [],
  "post" : [],
  "msg" : ["They were just my pals."]
  }, {
  "match" : "pals",
  "pre" : [],
  "post" : [],
  "msg" : ["I'm tired of talking to you, you racist...
GO AWAY!"]
  }, {
  "match" : "*",
  "pre" : [],
  "post" : [],
  "msg" : ["Assasin!"]
  }
]}
