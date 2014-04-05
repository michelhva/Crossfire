{
  "rules": [
  {
  "match" : ["ok"],
  "pre" : [["npctoken", "status", "gold"], ["item", "money", "1000"], ["quest", "navar/rescue-daughters", "=10"]],
  "post" : [["quest", "navar/rescue-daughters", "20"], ["takeitem", "money", "1000"]],
  "msg" : ["Ah, thank ye.  The Smuggler's Cove is a tiny inlet just southwest of Navar where the top smugglers like to work.  The Smugglers have been conducting a lot of business these past days but a honest bloke like me wouldn't know what that was about."]
  }, {
  "match" : ["ok"],
  "pre" : [["npctoken", "status", "gold"], ["quest", "navar/rescue-daughters", "=10"]],
  "post" : [["setnpctoken", "status", "0"]],
  "msg" : ["So where is yar money?"]
  }, {
  "match" : ["no"],
  "pre" : [["npctoken", "status", "gold"], ["quest", "navar/rescue-daughters", "=10"]],
  "post" : [["setnpctoken", "status", "0"]],
  "msg" : ["As you wish, mate."]
  }, {
  "match" : ["cove"],
  "pre" : [["quest", "navar/rescue-daughters", "=10"]],
  "post" : [["setnpctoken", "status", "gold"]],
  "msg" : ["Well maybe if you give a hundred gold on I'd tell ya about that..."],
  "replies" : [["ok", "Ok, here's the money.", "0"], ["no", "Too expensive, I'll manage myself, thanks.", "0"]]
  }, {
  "match" : ["bradley"],
  "pre" : [],
  "post" : [],
  "msg" : ["You try being a pirate named Bradley..."]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["This chick's seen it all.  She knows everyone."]
  }
]}
