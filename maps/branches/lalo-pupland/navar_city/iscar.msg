{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "=50"]],
  "post" : [],
  "msg" : ["Hello $you, how are you doing?"]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "navar/Iscar-pen", "10-40"], ["item", "Hasphatois's Writing Pen"], ["npctoken", "status", "has"]],
  "post" : [["quest", "navar/Iscar-pen", "50"], ["takeitem", "Hasphatois's Writing Pen"], ["givecontents", "reward"]],
  "msg" : ["Thank you very much!\n\nHere, take this autograph. There are MANY people who'd like one, you know."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "navar/Iscar-pen", "10-40"], ["npctoken", "status", "has"]],
  "post" : [["setnpctoken", "status", "0"]],
  "msg" : ["Where is it?"]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "10-40"], ["npctoken", "status", "has"]],
  "post" : [["setnpctoken", "status", "0"]],
  "msg" : ["*sigh*"]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "10-40"]],
  "post" : [["setnpctoken", "status", "has"]],
  "msg" : ["So, did you get a pen from Hasphatois?"],
  "replies" : [["yes", "Yes, I do.", "1"], ["no", "No, not yet.", "0"]]
  }, {
  "match" : ["ok"],
  "pre" : [["npctoken", "st", "ask"]],
  "post" : [["quest", "navar/Iscar-pen", "10"]],
  "msg" : ["Thank you very much!\n\nHasphatois lives in the mining town north of Navar, you can't miss it if you follow the road."]
  }, {
  "match" : ["*"],
  "pre" : [["npctoken", "st", "ask"]],
  "post" : [["setnpctoken", "st", "0"]],
  "msg" : ["Well, ok, if you change your mind...\n\n*sigh*"]
  }, {
  "match" : ["brand"],
  "pre" : [["npctoken", "st", "br"]],
  "post" : [["setnpctoken", "st", "ask"]],
  "msg" : ["A pen made with graphite and by Hasphatois the smith.\n\nSay, you wouldn't get one for me, do you? I'd give you a great reward!"],
  "replies" : [["ok", "I guess I can help, yes.", "1"], ["no", "No, sorry, I don't have time for now.", "0"]]
  }, {
  "match" : ["why"],
  "pre" : [["npctoken", "st", "why"]],
  "post" : [["setnpctoken", "st", "br"]],
  "msg" : ["I write novels for children.\n\nBut I lost my pen, and I can't write anymore!\n\nWell, I doubt you'd have a decent writing pen anymore, I need some specific brand.\n\n*sigh*"],
  "replies" : [["brand", "What kind of pen, exactly?", "1"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [["setnpctoken", "st", "why"]],
  "msg" : ["Hello.\n\nYou wouldn't have a pen, by any chance?"],
  "replies" : [["why", "Why do you need a pen?", "1"]]
  }
]}
