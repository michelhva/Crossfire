{
  "location" : "scorn",
  "rules": [
  {
  "match" : ["treasure", "random"],
  "pre" : [["token", "polite", "yes"]],
  "post": [["givecontents", "pouch"], ["settoken", "polite", "no"]],
  "msg" : ["Here you go."]  
  },{
  "match" : ["treasure", "random"],
  "pre" : [],
  "post": [["settoken", "choice", "treasure"]],
  "msg" : ["Say Please"]  
  },{
  "match" : ["please"],
  "pre" : [["token", "choice", "dice"]],
  "post": [["giveitem", "Furry dice", "2"], ["settoken", "choice", "none"], ["settoken", "polite", "no"]],
  "msg" : ["Ok then, here you go"]  
  },{
  "match" : ["please"],
  "pre" : [["token", "choice", "treasure"]],
  "post": [["givecontents", "pouch"], ["settoken", "choice", "none"], ["settoken", "polite", "no"]],
  "msg" : ["Ok then, here you go"]  
  },{
  "match" : ["please"],
  "pre" : [],
  "post": [["settoken", "polite", "yes"]],
  "msg" : ["What do you want?"]  
  },{
  "match" : ["dice", "furry"],
  "pre" : [["token", "polite", "yes"]],
  "post": [["giveitem", "Furry dice", "2"], ["settoken", "polite", "no"]],
  "msg" : ["Here you go."]  
  },{
  "match" : ["dice", "furry"],
  "pre" : [],
  "post": [["settoken", "choice", "dice"]],
  "msg" : ["Say Please"]  
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["settoken", "choice", "offer"]],
  "msg" : ["I have 2 items, which I can give you, a random treasure, or some furry dice, but if you kill me, you will get neither. Which would you like?"]
  }
]}