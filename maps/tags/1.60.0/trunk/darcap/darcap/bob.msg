{
  "location" : "darcap/darcap/bob",
  "rules": [
  {
  "match" : ["roots"],
  "pre" : [["quest", "darcap/Spike", "=20"]],
  "post" : [["quest", "darcap/Spike", "30"], ["givecontents", "pouch"]],
  "msg" : ["Ha yes, the roots!\n\nI almost forgot.\n\nHere, take them, and give them to Molthir for me, please."],
  "replies" : []
  },{
  "match" : ["day"],
  "pre" : [],
  "post" : [],
  "msg" : ["Yes, every day. Now either buy something or leave. I'm busy."],
  "replies" : []
  },{
  "match" : ["bakery", "food"],
  "pre" : [],
  "post" : [],
  "msg" : ["Yep, I get fresh fish every day."],
  "replies" : []
  },{
  "match" : ["name"],
  "pre" : [],
  "post" : [],
  "msg" : ["My name is Bob.  I own this place."],
  "replies" : []
  },{
  "match" : ["sage"],
  "pre" : [],
  "post" : [],
  "msg" : ["I don't know.  Go ask them!"],
  "replies" : []
  },{
  "match" : ["order", "tower"],
  "pre" : [],
  "post" : [],
  "msg" : ["To open the Tower, you must uses each part in order of the sage's names.  Good Luck."],
  "replies" : []
  },{
  "match" : ["*"],
  "pre" : [["quest", "darcap/Spike", "=20"]],
  "post" : [],
  "msg" : ["Hello. Would you like to buy some fish?"],
  "replies" : [["roots", "Do you have Molthir's smaprh roots?", "2"]]
  },{
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Hello. Would you like to buy some fish?"],
  "replies" : []
  }
]}
