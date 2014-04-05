{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "=30"]],
  "post" : [["quest", "navar/rescue-daughters", "40"]],
  "msg" : ["Oh please go rescue Susan, she was thrown to the monsters in the cavern behind the east gates!"]
  }, {
  "match" : ["monsters"],
  "pre" : [],
  "post" : [],
  "msg" : ["Yes huge horrible monsters - they were going to feed us to them!"]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Thank you for saving us from the monsters."]
  }
]}
