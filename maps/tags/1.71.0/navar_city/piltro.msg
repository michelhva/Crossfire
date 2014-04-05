{
  "location" : "navar",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "navar/smugglers", "=70"]],
  "post" : [],
  "msg" : ["Hello $you."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "navar/smugglers", "=60"]],
  "post" : [["quest", "navar/smugglers", "70"], ["givecontents", "NPC_Gift_Box"]],
  "msg" : ["Thank you very much!\n\nHere, take that as a token of gratitude from Navar."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/smugglers", "=60"]],
  "post" : [],
  "msg" : ["So, did you make any progress in fighting the smugglers?"],
  "replies" : [["yes", "Yes, I killed the chief, so it should be quieter from now on.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/smugglers", "10-50"]],
  "post" : [],
  "msg" : ["So, did you make any progress in fighting the smugglers?"]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "navar/rescue-daughters", "=60"]],
  "post" : [["quest", "navar/smugglers", "10"]],
  "msg" : ["Thank you very much. You might want to talk to the Dwarven priests in the church, they do know something that they won't tell us, something about being worthy."]
  }, {
  "match" : ["no"],
  "pre" : [["quest", "navar/rescue-daughters", "=60"]],
  "post" : [],
  "msg" : ["Ha well, it was worth asking..."]
  }, {
  "match" : ["how"],
  "pre" : [["quest", "navar/rescue-daughters", "=60"]],
  "post" : [],
  "msg" : ["We have tried fighting them, and they keep coming back. Will you help us?"],
  "replies" : [["yes", "Ok, I guess I can help. Any hint?", "1"], ["no", "No, sorry, I'm busy too.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "=60"]],
  "post" : [],
  "msg" : ["Hello. So you're the one who rescued Ben's daughters, hm?\n\nMaybe you could help me with the smugglers?"],
  "replies" : [["how", "How could I help?", "2"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Hello, how can I help you?"]
  }
]}
