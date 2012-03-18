{
  "location" : "friendlypeep",
  "rules": [
  {
  "match" : ["strong"],
  "pre" : [["level", "10"]],
  "post": [],
  "msg" : ["you are mighty."]
  },{
  "match" : ["strong"],
  "pre" : [],
  "post": [],
  "msg" : ["you are wimpy"]
  },{
  "match" : ["crash"],
  "pre" : [],
  "post": [["giveitem", "Royal Guard", "2"]],
  "msg" : ["Here, have a couple of pet knights"]
    },{
  "match" : ["*"],
  "pre" : [["token", "seenbefore", "yes"], ["item", "money", "100"]],
  "post": [["takeitem", "money", "12"]],
  "msg" : ["Hello again $you, you are rich, gissa bob."]
  },{
  "match" : ["*"],
  "pre" : [["token", "seenbefore", "yes"]],
  "post": [],
  "msg" : ["Hello again $you."]
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["settoken", "seenbefore", "yes"]],
  "msg" : ["Hello I'm $me."]
  }
 
]}