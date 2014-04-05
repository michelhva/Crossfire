{
  "rules": [
  {
  "match" : ["treasure"],
  "pre" : [["quest", "wolfsburg/treasure-island", "=10"]],
  "post" : [["quest", "wolfsburg/treasure-island", "20"]],
  "msg" : ["Yarh, I do happen.\n\nYou need a magic shovel.\n\nTalk to my friend Long John Silver in this tavern, he knows more than I do."]
  }, {
  "match" : ["pirate", "job"],
  "pre" : [["quest", "wolfsburg/treasure-island", "=10"]],
  "post" : [],
  "msg" : ["I search the seven seas looking for treasure."],
  "replies" : [["treasure", "Talking of treasure, you don't happen to know anything about the so-called treasure island?", "2"]]
  }, {
  "match" : ["hi", "hello"],
  "pre" : [],
  "post" : [],
  "msg" : ["ARRRRRRR MATEY!!!!!"]
  }, {
  "match" : ["name"],
  "pre" : [],
  "post" : [],
  "msg" : ["I am Black Beard the pirate."]
  }, {
  "match" : ["pirate", "job"],
  "pre" : [],
  "post" : [],
  "msg" : ["I search the seven seas looking for treasure."]
  }
]}
