{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "=60"]],
  "post" : [],
  "msg" : ["Hello, $you."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "=50"]],
  "post" : [["quest", "navar/rescue-daughters", "60"], ["givecontents", "reward"]],
  "msg" : ["Oh, thank you for saving my daughters!\n\nHere, take that as a token of thanks.\n\n\nYou should talk to Lt. Piltro, in the High Court Building, he could be interested by information about smugglers."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "10-40"]],
  "post" : [],
  "msg" : ["Please, help find my daughters..."]
  }, {
  "match" : ["yes"],
  "pre" : [["npctoken", "status", "wait"]],
  "post" : [["quest", "navar/rescue-daughters", "10"]],
  "msg" : ["Thank you very much! I'll reward you for your courage."]
  }, {
  "match" : ["no"],
  "pre" : [["npctoken", "status", "wait"]],
  "post" : [["setnpctoken", "status", "0"]],
  "msg" : ["Oh, guess I'll have to find some one else..."]
  }, {
  "match" : ["cove"],
  "pre" : [["npctoken", "status", "cove"]],
  "post" : [["setnpctoken", "status", "wait"]],
  "msg" : ["I don't know where it is but maybe someone at the Frolicking Mermaid would?"],
  "replies" : [["ok", "Ok, I'll go try to save them.", "1"], ["no", "Sorry, I'm busy for now...", "0"]]
  }, {
  "match" : ["smugglers"],
  "pre" : [["npctoken", "status", "smugglers"]],
  "post" : [["setnpctoken", "status", "cove"]],
  "msg" : ["Yes members of the smugglers guild.  I heard them say they would take the girls to 'Smugglers Cove.' You must help me find them."],
  "replies" : [["cove", "And where is that cove?", "2"]]
  }, {
  "match" : ["what"],
  "pre" : [["npctoken", "status", "what"]],
  "post" : [["setnpctoken", "status", "smugglers"]],
  "msg" : ["My daughters Susan and Nancy.  They were captured by smugglers. We were ambushed, I got away and am trying to get help for them."],
  "replies" : [["smugglers", "Smugglers?", "2"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [["setnpctoken", "status", "what"]],
  "msg" : ["Please, can you help me find my daughters?"],
  "replies" : [["what", "What happened?", "2"]]
  }
]}
