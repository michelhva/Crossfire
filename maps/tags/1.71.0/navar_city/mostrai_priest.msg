{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["npctoken", "given", "1"]],
  "post" : [],
  "msg" : ["Hello."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "navar/smugglers", "=10"]],
  "post" : [["quest", "navar/smugglers", "20"], ["givecontents", "NPC_Gift_Box"], ["setnpctoken", "given", "1"]],
  "msg" : ["Then I can help you....the smugglers are headquartered in one of the warehouses.  Which one I do not know, but it may lead to their entrance to the city.  Here is the key to their front door."]
  }, {
  "match" : ["yes"],
  "pre" : [],
  "post" : [["givecontents", "NPC_Gift_Box"], ["setnpctoken", "given", "1"]],
  "msg" : ["Then I can help you....the smugglers are headquartered in one of the warehouses.  Which one I do not know, but it may lead to their entrance to the city.  Here is the key to their front door."]
  }, {
  "match" : ["no"],
  "pre" : [],
  "post" : [],
  "msg" : ["Oh, ok."]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Are you here about the smugglers?"],
  "replies" : [["yes", "Yes.", "1"], ["no", "No, not at all.", "1"]]
  }
]}
