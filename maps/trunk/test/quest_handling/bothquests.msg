{
  "location" : "bothquest",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["token", "gave crown", "yes"]],
  "post": [],
  "msg" : ["You have already been crowned"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "50"], ["quest", "testquest2", "30"]],
  "post": [["giveitem", "crown of win"], ["settoken", "gave crown", "yes"]],
  "msg" : ["You completed both quests, well done, the crown of win is yours"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "50"], ["quest", "testquest2", "10"]],
  "post": [],
  "msg" : ["You have opened the door, but still need to recover the apples"]  
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "30"], ["quest", "testquest", "10"]],
  "post": [],
  "msg" : ["You have recovered the apples, but still need to complete the sword quest"]    
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest", "50"]],
  "post": [],
  "msg" : ["You have opened the door, but there is another quest for you to do still"]  
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "30"]],
  "post": [],
  "msg" : ["You have recovered the apples, but there is another quest for you to do still"]    
},{
  "match" : ["*"],
  "pre" : [],
  "post": [],
  "msg" : ["There are two quests here, complete them both and I will reward you."]
  }
]}