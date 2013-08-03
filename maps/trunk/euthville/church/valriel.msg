{
  "location" : "euthville",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "euthville/valriel", "40"]],
  "post" : [],
  "msg" : ["Hello, thanks again for bringing back the chalice."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "euthville/valriel", "30"], ["item", "Valriel's Chalice"]],
  "post" : [["takeitem", "Valriel's Chalice"], ["quest", "euthville/valriel", "40"], ["givecontents", "reward"]],
  "msg" : ["Thank you very much! May Valriel bless you!\n\nPlease take this Holy Symbol as a token of my gratitude."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "euthville/valriel", "10"]],
  "post" : [["setnpctoken", "", ""]],
  "msg" : ["Where is it, then?"]
  }, {
  "match" : ["no"],
  "pre" : [["quest", "euthville/valriel", "10-30"]],
  "post" : [],
  "msg" : ["Please bring it back..."]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "euthville/valriel", "10-30"]],
  "post" : [],
  "msg" : ["So, do you have the chalice?"],
  "replies" : [["yes", "Yes, I got it.", "1"], ["no", "No, not yet", "1"]]
  }, {
  "match" : ["recover"],
  "pre" : [["npctoken", "state", "recover"]],
  "post" : [["quest", "euthville/valriel", "10"], ["giveitem", "key to the catacombs"]],
  "msg" : ["Really? That'd be very nice. Here is the key to the catacombs, but take care, those creatures are evil!"]
  }, {
  "match" : ["why"],
  "pre" : [["npctoken", "state", "why"]],
  "post" : [["setnpctoken", "state", "recover"]],
  "msg" : ["An evil creature stole Valriel's Holy Chalice.\n\nMt friend Maxus went down the catacombs to follow it, but he's not back, and I'm afraid for him..."],
  "replies" : [["recover", "Should I try to recover it?", "2"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [["setnpctoken", "state", "why"]],
  "msg" : ["Hello. I'm sorry, but today's Mass is cancelled."],
  "replies" : [["why", "Why?", "2"]]
  }
]}
