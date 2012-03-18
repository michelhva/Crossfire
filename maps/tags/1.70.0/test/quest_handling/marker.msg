{
  "location" : "scorn",
  "rules": [
  {
  "match" : ["two", "2"],
  "pre" : [],
  "post": [["givecontents", "markers2"]],
  "msg" : ["Here you go, this set are in my inventory."]
  },{
  "match" : ["one", "1"],
  "pre" : [],
  "post": [["givecontents", "markers1"]],
  "msg" : ["Here you go, this set are in the gift box."]  
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [],
  "msg" : ["I have 2 sets of markers, which I can give you, do you want set one or two?"]
  }
]}