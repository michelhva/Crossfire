{
  "location" : "testquest",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "testquest", "50"]],
  "post": [],
  "msg" : ["The old man has opened the door for you, well done."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest" "40"], ["token", "pester", "1"]],
  "post": [],
  "msg" : ["I told you already, speak to the old man"]  
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "40"]],
  "post": [["settoken", "pester", "1"]],
  "msg" : ["Speak to the old man, and he'll open the door for you"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "20"], ["item", "testquesttarget"]]
  "post": [["takeitem","testquesttarget"],["quest", "testquest", "40"]]
  "msg" : ["Thank you for returning my sword, Speak to the old man, and he'll open the door for you"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "30"]],
  "post": [],
  "msg" : ["You found a sword? it's mine, hand it over."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "20"]],
  "post": [],
  "msg" : ["You found my sword? Please give it to me."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "10"]],
  "post": [],
  "msg" : ["Have you got my sword yet?."]
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["quest", "testquest", "10"]],
  "msg" : ["Fetch the sword and bring it back to me."]
  }
]}