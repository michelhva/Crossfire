{
  "location" : "test/includegirl",
  "rules": [
  {
  "match" : ["1", "one"],
  "pre" : [],
  "post" : [],
  "msg" : ["You said 1."]
  },{
  "include" : ["/test/quest_handling/girl2.msg"]
  },{
  "include" : ["girl3.msg", "girl4.msg"]
  },{
  "include" : ["/test/quest_handling/girl7.msg", "girl6.msg"],
  "pre" : [["token", "can count high", "1"]]
  },{
  "match" : ["*"],
  "pre" : [],
  "post" : [["settoken", "can count high", "1"]],
  "msg" : ["You didn't say a number to me."]
  }
]}
