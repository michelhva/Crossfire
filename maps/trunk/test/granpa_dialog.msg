{
  "location" : "test_grandpa_01",
  "rules": [
  {
  "match" : "hello|hi",
  "pre" : [["token", "hello","0"]],
  "post" : [["settoken", "hello","1"]],
  "msg" : ["Hello, lad!","Hi, young fellow!","Howdy!"]
  }, {
  "match": "hello|hi",
  "pre" :[["token", "hello","1"]],
  "post" :[["settoken", "hello", "*"]],
  "msg" : ["I've heard, you know, I'm not deaf *grmbl*"]
  } , {
  "match" : "*",
  "pre" : [["token", "hello","*"]],
  "post" : [["settoken", "hello", "*"]],
  "msg" : ["What ?", "Huh ?", "What do you want ?"]
  }
]}