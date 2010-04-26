{
  "location" : "connectiontest",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "testquest" "50"]],
  "post": [],
  "msg" : ["I've already opened the door."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest" "40"]],
  "post": [["connection", "4"], ["quest", "testquest" "50"]],
  "msg" : ["I'll just open the door for you."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest" "10"]["token", "pester", "1"]],
  "post": [],
  "msg" : ["I gave you a biccie already, $you; now do your quest."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest" "10"]],
  "post": [["settoken", "pester", "1"],["giveitem", "biccie"]],
  "msg" : ["You haven't got far enough in the quest yet, but have a biccie."]
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["giveitem", "money", "74"]],
  "msg" : ["Hello, have 74 money"]
  }
]}