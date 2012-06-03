{
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "santo_dominion/Gordon", "20"]],
  "post" : [],
  "msg" : ["Thank you for helping us."]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "santo_dominion/Gordon", "=10"], ["item", "beholder's tongue"], ["item", "scorpion's stinger"], ["item", "shadow viper's skin"]],
  "post" : [["animate", "/santo_dominion/warloc_druid.anim", "MakePotion"], ["quest", "santo_dominion/Gordon", "20"], ["takeitem", "beholder's tongue"], ["takeitem", "scorpion's stinger"], ["takeitem", "shadow viper's skin"]],
  "msg" : ["Thank you, now I can make the cure!"]
  }, {
  "match" : ["yes"],
  "pre" : [["quest", "santo_dominion/Gordon", "=10"]],
  "post" : [],
  "msg" : ["Where are they?"]
  }, {
  "match" : ["no"],
  "pre" : [["quest", "santo_dominion/Gordon", "=10"]],
  "post" : [],
  "msg" : ["Please hurry up..."]
  }, {
  "match" : ["viper"],
  "pre" : [["quest", "santo_dominion/Gordon", "=10"]],
  "post" : [],
  "msg" : ["A brood of shadow vipers exists somewhere down the well of Santo Dominion.  We've got only one well here in town, so you can't miss it.  Anyways, be careful down there!"]
  }, {
  "match" : ["*"],
  "pre" : [["quest", "santo_dominion/Gordon", "=10"]],
  "post" : [],
  "msg" : ["Did you find the ingredients?"],
  "replies" : [["yes", "Yes.", "1"], ["no", "No, not yet.", "1"]]
  }, {
  "match" : ["ok"],
  "pre" : [],
  "post" : [["quest", "santo_dominion/Gordon", "10"]],
  "msg" : ["Thank you very much!"],
  "replies" : [["viper", "Where can I find shadow vipers?", "2"]]
  }, {
  "match" : ["no"],
  "pre" : [],
  "post" : [],
  "msg" : ["Poor Gordon, I hope he'll survive till I can find the ingredients!"]
  }, {
  "match" : ["how"],
  "pre" : [],
  "post" : [],
  "msg" : ["I need the following ingredients to complete the cure:\n- a beholder's tongue\n- a scorpion's stinger\n- a skin of the shadow viper\n"],
  "replies" : [["ok", "Ok, I'll find them.", "0"], ["no", "Sorry, I'm busy for now.", "0"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["Do you see my friend Gorgon in the other room? He got poisoned.  We need some help!"],
  "replies" : [["how", "How can I help?", "2"]]
  }
]}
