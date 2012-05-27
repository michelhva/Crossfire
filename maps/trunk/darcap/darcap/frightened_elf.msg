{
  "rules": [
  {
  "match" : ["pygmy"],
  "pre" : [["quest", "darcap/pygmy-forest", "=10"]],
  "post" : [],
  "msg" : ["The Pygmys are these evil little creatures that live in a forest on an island north and slightly east of this town...I had to use dimension door to get there...they are really dangerous and I'm sure they are guarding something in that forest..."]
  }, {
  "match" : ["pygmy"],
  "pre" : [],
  "post" : [["quest", "darcap/pygmy-forest", "10"]],
  "msg" : ["The Pygmys are these evil little creatures that live in a forest on an island north and slightly east of this town...I had to use dimension door to get there...they are really dangerous and I'm sure they are guarding something in that forest..."]
  }, {
  "match" : ["nightmares"],
  "pre" : [],
  "post" : [],
  "msg" : ["Yes, I keep having a nightmare where the Pygmys slaughter me and then eat me..like what I saw them doing to their prisoners when I stumbled upon that forest..."],
  "replies" : [["pygmy", "Who are those pygmys?", "2"]]
  }, {
  "match" : ["*"],
  "pre" : [],
  "post" : [],
  "msg" : ["I still have nightmares about my visit to the Pygmy Forest."],
  "replies" : [["nightmares", "What kind of nightmares?", "2"], ["pygmy", "Who are those pygmys?", "2"]]
  }
]}
