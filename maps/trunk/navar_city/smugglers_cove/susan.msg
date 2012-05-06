{
  "rules": [
  {
  "match" : ["killed"],
  "pre" : [["npctoken", "status", "killed"], ["item", "giant rat's skull", "4"], ["quest", "navar/rescue-daughters", "=40"]],
  "post" : [["quest", "navar/rescue-daughters", "50"]],
  "msg" : ["Thank you for saving me!"]
  }, {
  "match" : ["killed"],
  "pre" : [["npctoken", "status", "killed"], ["quest", "navar/rescue-daughters", "=40"]],
  "post" : [],
  "msg" : ["No, I'm sure you didn't kill them yet!"]
  }, {
  "match" : ["saved"],
  "pre" : [["npctoken", "status", "saved"], ["quest", "navar/rescue-daughters", "=40"]],
  "post" : [["setnpctoken", "status", "killed"]],
  "msg" : ["I'm afraid of those evil rats!"],
  "replies" : [["killed", "I killed them, it's safe now.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "navar/rescue-daughters", "=40"]],
  "post" : [["setnpctoken", "status", "saved"]],
  "msg" : ["Help!"],
  "replies" : [["saved", "You're saved, now.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [["setnpctoken", "status", "saved"]],
  "msg" : ["Help!"]
  }
]}
