{
  "location" : "navar",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "quests/TrollCanyon", "30"]],
  "post" : [],
  "msg" : ["Thanks for clearing those pesky trolls, now I have more free time."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "quests/TrollCanyon", "10-20"]],
  "post" : [],
  "msg" : ["Hello. So, did you clean those pesky trolls?"]
  }, {
  "match" : ["sure"],
  "pre" : [["npctoken", "dialog", "sure"]],
  "post" : [["quest", "quests/TrollCanyon", "10"]],
  "msg" : ["Thanks you very much!\n\nMy spies tell me this den is situated south-west of Navar, close to the mountains.\n\nFrom the Navar-Scorn road, take the dirt road leading to the Training Center, and search the valley south of it."]
  }, {
  "match" : ["*"],
  "pre" : [["npctoken", "dialog", "sure"]],
  "post" : [["setnpctoken", "dialog", "(empty)"]],
  "msg" : ["Ah well, I knew it was too much to hope for..."]
  }, {
  "match" : ["go"],
  "pre" : [["npctoken", "dialog", "go"]],
  "post" : [["setnpctoken", "dialog", "sure"]],
  "msg" : ["Really? That would be very nice. Are you sure?"],
  "replies" : [["sure", "Yes, I feel like having some fun with trolls.", "1"], ["no", "Well, no.", "1"]]
  }, {
  "match" : ["busy"],
  "pre" : [["npctoken", "dialog", "trolls"]],
  "post" : [["setnpctoken", "dialog", "go"]],
  "msg" : ["Well, trolls are said to be building some fortress in the mountains, and planning some evil.\n\nUnfortunately I don't have enough troops to go teach them a lesson."],
  "replies" : [["go", "So should I go do that for you?", "2"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [["setnpctoken", "dialog", "trolls"]],
  "msg" : ["Hello.\n\nSorry, but I'm kind of busy."],
  "replies" : [["busy", "Why are you busy?", "2"]]
  }
]}
