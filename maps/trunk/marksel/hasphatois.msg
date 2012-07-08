{
  "rules": [
  {
  "match" : ["graphite"],
  "pre" : [["quest", "navar/Iscar-pen", "=30"], ["item", "lump of graphite"]],
  "post" : [["animate", "/marksel/hasphatois.anim", "special_pen"], ["takeitem", "lump of graphite"]],
  "msg" : ["Ok, guess I'll make my special pen, then. Please wait a few seconds."]
  }, {
  "match" : ["graphite"],
  "pre" : [["quest", "navar/Iscar-pen", "=30"]],
  "post" : [],
  "msg" : ["Really? Then I'd like to see it."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "=30"]],
  "post" : [],
  "msg" : ["Sorry,I'm too busy, I can't help you now."],
  "replies" : [["graphite", "I've some graphite.", "0"]]
  }, {
  "match" : ["pen"],
  "pre" : [["quest", "navar/Iscar-pen", "=10"]],
  "post" : [["quest", "navar/Iscar-pen", "20"]],
  "msg" : ["Ha yes. Well, you need to find some graphite so I can make one.\n\nI think Pikorne the Dwarf, in the mines just west of the town, can give you some."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/Iscar-pen", "=10"]],
  "post" : [],
  "msg" : ["Sorry, I'm too busy to help you."],
  "replies" : [["pen", "I've been told you make special writing pens, I'd need one, please.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Sorry, I'm too busy to help you."]
  }
]}
