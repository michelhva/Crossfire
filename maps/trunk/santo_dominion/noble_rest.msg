{
  "rules": [
  {
  "match" : ["hi", "hello"],
  "pre" : [["quest", "santo_dominion/well", "=0"]],
  "post" : [["setnpctoken", "state", "ask"]],
  "msg" : ["Welcome to The Noble Resting.  A fine place to sleep and dine.\n\nSay, you seem a decent fellow, could I ask you something?"],
  "replies" : [["yes", "Yes, what is it?", "1"], ["no", "No, sorry.", "0"]]
  }, {
  "match" : ["no"],
  "pre" : [["npctoken", "state", "ask"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["Sorry for bothering you."]
  }, {
  "match" : ["yes"],
  "pre" : [["npctoken", "state", "ask"]],
  "post" : [["setnpctoken", "state", "propose"]],
  "msg" : ["Well, actually... there is a weird noise coming from the well nearby.\n\nDo you think you could investigate?"],
  "replies" : [["sure", "Of course, count on me!", "1"], ["nope", "Sorry, but I'm just too busy lately.", "1"]]
  }, {
  "match" : ["sure"],
  "pre" : [["npctoken", "state", "propose"]],
  "post" : [["quest", "santo_dominion/well", "10"], ["setnpctoken", "state", "0"]],
  "msg" : ["Oh, thank you very much!"]
  }, {
  "match" : ["nope"],
  "pre" : [["npctoken", "state", "propose"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["Well, it was worth asking..."]
  }, {
  "match" : ["hi", "hello"],
  "pre" : [["quest", "santo_dominion/well", "10-30"]],
  "post" : [["setnpctoken", "state", "found"]],
  "msg" : ["Welcome to The Noble Resting.  A fine place to sleep and dine.\n\nSo, did you find the origin of the noise?"],
  "replies" : [["yes", "Yes, I did.", "1"], ["no", "No, not yet.", "1"]]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "santo_dominion/well", "=30"], ["item", "quest_item_santo_dominion_well"], ["npctoken", "state", "found"]],
  "post" : [["quest", "santo_dominion/well", "40"], ["takeitem", "quest_item_santo_dominion_well"]],
  "msg" : ["Oh, thank you very much! Bad bee, making all that noise.\n\nGive it to me, I'll punish it..."]
  }, {
  "match" : ["yes"],
  "pre" : [["npctoken", "state", "found"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["So what was it?"]
  }, {
  "match" : ["no"],
  "pre" : [["npctoken", "state", "found"]],
  "post" : [["setnpctoken", "state", "0"]],
  "msg" : ["Well, if you have time, please keep on searching..."]
  }, {
  "match" : ["hi", "hello"],
  "pre" : [],
  "post" : [],
  "msg" : ["Welcome to The Noble Resting.  A fine place to sleep and dine."]
  }, {
  "match" : ["room", "rest", "inn", "sleep"],
  "pre" : [],
  "post" : [],
  "msg" : ["We have rooms available up ahead and around the corner.  Stay as long as you like."]
  }, {
  "match" : ["food", "meal", "eat", "dine"],
  "pre" : [],
  "post" : [],
  "msg" : ["You just missed our serving hour.  Perhaps there is a small meal still available."]
  }, {
  "match" : ["stairs", "upstairs"],
  "pre" : [],
  "post" : [],
  "msg" : ["Our second floor has additional guest rooms and two rooftop decks for relaxing."]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Well good day to you!"]
  }
]}
