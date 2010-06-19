{
  "location" : "darcap/cameron",
  "rules": [
  {
  "match" : ["wine"],
  "pre" : [["item", "money", "18"], ["npctoken", "step", "drink"]],
  "post" : [["takeitem", "money", "18"], ["giveitem", "bottle of wine"], ["setnpctoken", "step", "0"]],
  "msg" : ["Here you go."],
  "replies" : []
  },{
  "match" : ["wine"],
  "pre" : [["npctoken", "step", "drink"]],
  "post" : [["setnpctoken", "step", "0"]],
  "msg" : ["Sorry, friend, but you definitely need to pay for the wine."],
  "replies" : []
  },{
  "match" : ["drink"],
  "pre" : [],
  "post" : [["setnpctoken", "step", "drink"]],
  "msg" : ["I have some fine wine, for 18 silver coins."],
  "replies" : [["wine", "I'll have some wine, please.", "0"], ["food", "And what about food?", "2"]]
  },{
  "match" : ["food"],
  "pre" : [],
  "post" : [],
  "msg" : ["Sorry, no. But you can try Bob's Fish & Chips, south of Darcap."],
  "replies" : [["drink", "Ok, what about drinks, then?", "2"]]
  },{
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["What can I get you?"],
  "replies" : [["drinks", "What drinks do you have?", "2"], ["food", "Do you have anything to eat?", "2"]]
  }
]}
