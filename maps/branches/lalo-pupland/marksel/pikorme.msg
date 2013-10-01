{
  "rules": [
  {
  "match" : ["ok"],
  "pre" : [["quest", "navar/Iscar-pen", "=20"], ["npctoken", "st", "buy"], ["item", "money", "1250"]],
  "post" : [["setnpctoken", "st", "0"], ["takeitem", "money", "1250"], ["givecontents", "ore"], ["quest", "navar/Iscar-pen", "30"]],
  "msg" : ["Ok, here you go."]
  }, {
  "match" : ["ok"],
  "pre" : [["quest", "navar/Iscar-pen", "=20"], ["npctoken", "st", "buy"]],
  "post" : [["setnpctoken", "st", "0"]],
  "msg" : ["Sorry, but you give me the money first, then you get the lump."]
  }, {
  "match" : ["graphite"],
  "pre" : [["quest", "navar/Iscar-pen", "=20"], ["npctoken", "st", "hi"]],
  "post" : [["setnpctoken", "st", "buy"]],
  "msg" : ["Well yes, it'll cost you 25 platinum coins for one lump of it."],
  "replies" : [["ok", "Ok, I'll get one lump, please.", "1"], ["no", "I'll pass, thanks.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "=20"]],
  "post" : [["setnpctoken", "st", "hi"]],
  "msg" : ["*grumble*  Why'd I have to get this worthless shaft, while my brother finds gold in abundance.  How, by the beard of my father, am I going to move this blasted rock!?"],
  "replies" : [["graphite", "I've heard you can find me graphite?", "2"]]
  }
]}
