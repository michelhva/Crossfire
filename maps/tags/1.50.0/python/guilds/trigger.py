import Crossfire
import CFGuilds
import CFItemBroker
import random
import string
whoami=Crossfire.WhoAmI()
message = whoami.Slaying
message1 = whoami.Name
whoami.Say(message)
whoami.Say(message1)
#activator = Crossfire.WhoIsActivator()
mymap = whoami.Map
#activatorname = activator.Name
myx=10 
myy = 17
obj1=mymap.ObjectAt(myx,myy)
objects = [obj1]
players = []
names = []
marker = []
speed=.01
	

def find_trigger_marker(object):
    while (object.Type != 52) : #1 is type 'Player'
        object = object.Above
        if not object:
            return 0
    return object

def find_player(object):
    while (object.Type != 1) : #1 is type 'Player'
        object = object.Above
        if not object:
            return 0
    return object
for object in objects:
        temp = find_player(object)
        if temp:
            activator =(temp)

for object in objects:
        temp = find_trigger_marker(object)
        if temp:
            marker =(temp)
print marker
#ctrl1=marker.Slaying
foo=marker.Food
#print ctrl1
#print foo
#print activator.Name

foo2 = int (10)
foo1 = foo*foo2
foo3 = -(foo1)
foo4 = str(foo3)
marker1 = activator.CreateObject("force")
marker1.Slaying=ctrl1
marker1.Speed = 0.010000
marker1.SpeedLeft=int(- foo)

