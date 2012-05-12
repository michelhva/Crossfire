{
  "rules": [
  {
  "match" : ["entrance"],
  "pre" : [["npctoken", "given", "1"]],
  "post" : [],
  "msg" : ["Yes, the entrance, it is in the lower levels of the Lighthouse, but you have to get to the top level to go down to it."]
  }, {
  "match" : ["entrance"],
  "pre" : [["quest", "navar/smugglers", "=30"]],
  "post" : [["quest", "navar/smugglers", "40"], ["givecontents", "NPC_Gift_Box"], ["setnpctoken", "given", "1"]],
  "msg" : ["Yes, the entrance, it is in the lower levels of the Lighthouse, but you have to get to the top level to go down to it. Here is the key to the lighthouse."]
  }, {
  "match" : ["entrance"],
  "pre" : [],
  "post" : [["givecontents", "NPC_Gift_Box"], ["setnpctoken", "given", "1"]],
  "msg" : ["Yes, the entrance, it is in the lower levels of the Lighthouse, but you have to get to the top level to go down to it. Here is the key to the lighthouse."]
  }, {
  "match" : ["smugglers"],
  "pre" : [],
  "post" : [],
  "msg" : ["Yes, the smugglers, they locked me in here to die of starvation since I overheard where their secret entrance to the city is."]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Praise Valriel you have come to let me out... those damned smugglers."]
  }
]}
