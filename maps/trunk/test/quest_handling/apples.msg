{
  "location" : "testquest",
  "rules": [
  {
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "30"]],
  "post": [],
  "msg" : ["Thank you for getting me the apples $you"]
  },{
  "match" : ["yes"],
  "pre" : [["quest", "testquest2", "10"], ["item", "apple", "7"], ["token", "askapples", "yes"]],
  "post": [["settoken", "askapples", "no"], ["takeitem", "apples", "0"], ["quest", "testquest2", "40"], ["giveitem", "money", "20"]],
  "msg" : ["Wow, you got even more apples than I wanted, thank you so much, here's a couple of gold coins for you"]  
  },{
  "match" : ["yes"],
  "pre" : [["quest", "testquest2", "10"], ["item", "apple", "6"], ["token", "askapples", "yes"]],
  "post": [["settoken", "askapples", "no"], ["takeitem", "apples", "6"], ["quest", "testquest2", "30"]],
  "msg" : ["Thank You for giving me the apples."]
  },{
  "match" : ["no"],
  "pre" : [["token", "askapples", "yes"]],
  "post": [["settoken", "askapples", "no"]],
  "msg" : ["Ok, speak to me again if you change your mind"]
  },{
  "match" : ["no","yes"],
  "pre" : [],
  "post": [],
  "msg" : ["I didn't ask a question"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "10"], ["item", "apple", "6"]],
  "post": [["settoken", "askapples", "yes"]],
  "msg" : ["You have the apples? will you give them to me?"]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "20"]],
  "post": [],
  "msg" : ["What's the hold up? I already gave you my mcguffin."]
  },{
  "match" : ["*"],
  "pre" : [["quest", "testquest2", "10"]],
  "post": [["giveitem", "mcguffin"], ["quest", "testquest2", "20"]],
  "msg" : ["Struggling to get the apples? here, take my mcguffin to help you."]
  },{
  "match" : ["*"],
  "pre" : [],
  "post": [["quest", "testquest2", "10"]],
  "msg" : ["Give me apples, I want 6 apples"]
  }
]}