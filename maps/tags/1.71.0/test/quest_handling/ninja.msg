{
  "location" : "test/quest_handling/ninja",
  "rules": [
  {
  "match" : ["another"],
  "pre" : [],
  "post" : [["animate", "/test/quest_handling/ninja.animation", "Another"]],
  "msg" : ["Sure!"],
  "replies" : []
  },{
  "match" : ["anim"],
  "pre" : [],
  "post" : [["animate", "/test/quest_handling/ninja.animation"]],
  "msg" : ["Sure, let's rock!"],
  "replies" : [["another", "Can you do something else?", "2"]]
  },{
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Hello, I'm a ninja."],
  "replies" : [["anim", "Would you test the animation system for me, please?", "2"]]
  }
]}
