{
  "rules": [
  {
  "match" : ["crystals"],
  "pre" : [["quest", "wolfsburg/Heimser", "=30"]],
  "post" : [["quest", "wolfsburg/Heimser", "40"]],
  "msg" : ["Oh, he always forgets, poor him.\n\nHe should melt them."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "wolfsburg/Heimser", "=30"]],
  "post" : [],
  "msg" : ["Hello."],
  "replies" : [["crystals", "Heimser doesn't remember whether to melt or break the mithril crystals for his experiment.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Hello."]
  }
]}
