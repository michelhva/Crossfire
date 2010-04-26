{
  "location" : "scorn",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["age", "tenminutes", "0", "0", "0", "80", "0"]],
  "post": [["marktime", "tenminutes"]],
  "msg" : ["Oh no, you waited too long, and I got bored, let's try again shall we? Speak to me again in 10 minutes"]
  },{
  "match" : ["*"],
  "pre" : [["age", "tenminutes", "0", "0", "0", "0", "10"]],
  "post": [["settoken", "tenminutes", "0"], ["giveitem", "biccie"]],
  "msg" : ["Well done, have a biccie."]  
  },{
  "match" : ["*"],
  "pre" : [["token", "tenminutes", "0"]],
  "post": [["marktime", "tenminutes"]],
  "msg" : ["Speak to me again in 10 minutes, and I will give you a reward."]
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["marktime", "tenminutes"]],
  "msg" : ["Oh no!, you spoke to me too soon, no biccie for you. Speak to me again in 10 minutes."]    
  }
]}