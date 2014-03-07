{
  "rules": [
  {
  "match" : ["yes"],
  "pre" : [["quest", "wolfsburg/treasure-island", "30-40"], ["item", "Long John Silver's treasure map"]],
  "post" : [["quest", "wolfsburg/treasure-island", "50"], ["takeitem", "Long John Silver's treasure map"], ["givecontents", "map_box"]],
  "msg" : ["Yarrr, here is the map to the magic shovel.\n\nTry talking to the cap'tains of gallions here, one should be able to take you there."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "wolfsburg/treasure-island", "30-40"]],
  "post" : [],
  "msg" : ["Where is it?"]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "wolfsburg/treasure-island", "30-40"]],
  "post" : [],
  "msg" : ["Yar mate, got my map already?"],
  "replies" : [["yes", "Yes, I do.", "1"]]
  }, {
  "match" : ["treasure"],
  "pre" : [["quest", "wolfsburg/treasure-island", "=20"]],
  "post" : [["quest", "wolfsburg/treasure-island", "30"], ["givecontents", "key_box"]],
  "msg" : ["Yaeahhh!\n\nYou need the magic shovel which is on a remote island.\n\nHere is the key to my den, reachable from Wolfsburg by boat, it contains a map, please bring it to me."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "wolfsburg/treasure-island", "=20"]],
  "post" : [],
  "msg" : ["ARRRRRRR!!!!!"],
  "replies" : [["treasure", "Black Beard says you know things about the so-called treasure island?", "0"]]
  }
]}
