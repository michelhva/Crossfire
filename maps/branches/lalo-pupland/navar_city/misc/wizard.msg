{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "scorn/Rhyz", "30"]],
  "post" : [],
  "msg" : ["Hello $you, nice to see you again."]
  }, {
  "match" : ["sure"],
  "pre" : [["npctoken", "state", "2"], ["item", "empty bottle"]],
  "post" : [["takeitem", "empty bottle", "1"], ["quest", "scorn/Rhyz", "30"], ["giveitem", "potion of gem essence"]],
  "msg" : ["Here you go, take this gem essence."]
  }, {
  "match" : ["sure"],
  "pre" : [["npctoken", "state", "2"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["Hum, where is that empty bottle of yours?"]
  }, {
  "match" : ["*"],
  "pre" : [["npctoken", "state", "2"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["Well, come back later then."]
  }, {
  "match" : ["gem"],
  "pre" : [["npctoken", "state", "1"]],
  "post" : [["setnpctoken", "state", "2"]],
  "msg" : ["Ha, glad to see he's making progress.\n\nWell, it's easy, give me a bottle, and I'll fill it for you."],
  "replies" : [["sure", "Sure, here is an empty bottle.", "0"], ["later", "Sorry I don't have any empty bottle for now.", "0"]]
  }, {
  "match" : ["hi", "hello"],
  "pre" : [["quest", "scorn/Rhyz", "10-20"]],
  "post" : [["setnpctoken", "state", "1"]],
  "msg" : ["Hello there."],
  "replies" : [["gem", "Rhyz sent me to get some gem essence for his amulet.", "0"]]
  }, {
  "match" : ["hi", "hello"],
  "pre" : [],
  "post" : [],
  "msg" : ["Hello there."]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Leave me alone!"]
  }
]}
