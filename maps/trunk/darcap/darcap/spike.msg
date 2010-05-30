{
  "location" : "darcap/darcap/spike",
  "rules": [
  {
  "match" : ["yes"],
  "pre" : [["quest", "darcap/Spike", "10"], ["item", "Molthir's famous potion for the back"]],
  "post" : [["quest", "darcap/Spike", "50"], ["givecontents", "pouch"], ["takeitem", "Molthir's famous potion for the back"]],
  "msg" : ["Ohhhhh, thanks!\n\nHere, take this as a token of gratitude."],
  "replies" : []
  },{
  "match" : ["yes"],
  "pre" : [["quest", "darcap/Spike", "10"]],
  "post" : [],
  "msg" : ["Where is it, then? Please give it to me!"],
  "replies" : []
  },{
  "match" : ["no"],
  "pre" : [["quest", "darcap/Spike", "10"]],
  "post" : [],
  "msg" : ["Please get it, my back is hurting me a lot!"],
  "replies" : []
  },{
  "match" : ["*"],
  "pre" : [["quest", "darcap/Spike", "10"]],
  "post" : [],
  "msg" : ["Please, did you get the potion from Molthir?"],
  "replies" : [["yes", "Yes, I finally got it...", "1"], ["no", "No, not yet, sorry.", "1"]]
  },{
  "match" : ["yes"],
  "pre" : [["quest", "darcap/Spike", "0"]],
  "post" : [["quest", "darcap/Spike", "10"]],
  "msg" : ["Oh, many thanks!"],
  "replies" : []
  },{
  "match" : ["no"],
  "pre" : [["quest", "darcap/Spike", "0"]],
  "post" : [],
  "msg" : ["Haaaaa, my poor back, if you can help, please tell me!"],
  "replies" : []
  },{
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Ohhhh, my poor back, it hurts!\n\nWould you be kind enough to go get me a potion from the potion shop?"],
  "replies" : [["yes", "Sure, I guess I can do that.", "0"], ["no", "No, sorry, I don't have time for now...", "0"]]
  }
]}
