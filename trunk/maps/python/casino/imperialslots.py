#SlotMachine configuration file
#to make a new kind of slot machine, copy this file, change the settings and point the slotmachine to the new file.
#Standard type Imperial Slots
#FYI - This one uses an object for cointype and not the money code :)

import CFPython
import sys
sys.path.append('%s/%s/python' %(CFPython.GetDataDirectory(),CFPython.GetMapDirectory()))
import CFGamble
import CFItemBroker

activator=CFPython.WhoIsActivator()
activatorname=CFPython.GetName(activator)
whoami=CFPython.WhoAmI()
#gets slot name and adds map name for unique jackpot
slotname= '%s#%s' %(CFPython.GetName(whoami),CFPython.GetMapPath(CFPython.GetMap(whoami))) 
x=CFPython.GetXPosition(activator)
y=CFPython.GetYPosition(activator)
	
cointype = "imperial" #What type of coin is this slotmachine using?	
minpot = 200 #Minimum slot jackpot size	
maxpot = 10000 #Maxiumum slot jackpot size
cost = 1 #Price of usage

#Change the items on the slot spinner or the number of items.    
slotlist = ["Dread", "Dragon", "Knight", "Wizard", "Titan", "Septre", "Emperor", "JackPot"]

spinners = 4 #How many spinners on the slotmachine?

Slots=CFGamble.SlotMachine(slotname,slotlist,minpot,maxpot)

object = CFPython.CheckInventory(activator,cointype)
if (object):
    pay = CFItemBroker.Item(object).subtract(cost)
    if (pay):
       Slots.placebet(cost)
       results = Slots.spin(spinners)
       pay = 0
       pot = Slots.checkslot()
       CFPython.Write('%s' %results, activator, 7)
       for item in results:
          #match all but one - pays out by coin e.g 3 to 1 or 4 to 1
          if results.count(item) == spinners-1:
             if item == "Dread":
                pay = 1
             elif item == "Dragon":
                pay = 2
             elif item == "Knight":
                pay = 3
             elif item == "Wizard":
                pay = 4
             elif item == "Titan":
                pay = 5
             elif item == "Septre":
                pay = 6
             elif item == "Emperor":
                pay = 10
             elif item == "Jackpot":
                pay = 20
             else:
                break
             CFPython.Write("%d %ss, a minor win!" %(spinners-1,item),activator)
             payoff = cost*pay
             Slots.payoff(payoff)
             id = CFPython.CreateObject(cointype, (x, y))
             CFItemBroker.Item(id).add(payoff)
             if payoff == 1:
                message = "you win %d %s!" %(payoff,cointype)
             else:
                message = "You win %d %ss!!" %(payoff,cointype)	
             break
          elif results.count(item) == spinners:
             #all match - pays out as percent of pot
             CFPython.Write('%d %ss, a Major win!' %(spinners,item),activator)
             if item == "Dread":
                pay = .1
             elif item == "Dragon":
                pay = .15
             elif item == "Knight":
                pay = .20
             elif item == "Wizard":
                pay = .25
             elif item == "Titan":
                pay = .30
             elif item == "Septre":
                pay = .40
             elif item == "Emperor":
                pay = .50
             elif item == "JackPot":
                pay = 1
             payoff = pot*pay
             Slots.payoff(payoff)
             id = CFPython.CreateObject(cointype, (x, y))
             CFItemBroker.Item(id).add(payoff)
             if payoff == 1:
                message = "you win %d %s!" %(payoff,cointype)
             else:
                message = "You win %d %ss!!" %(payoff,cointype)	
             break
          else:
             message = "Better luck next time!"
       CFPython.Write(message,activator)
       CFPython.Write("%d in the Jackpot, Play again?" %Slots.checkslot(),activator)
    else:
       CFPython.Write("Sorry, you do not have enough %ss" %(cointype),activator)
else:
   CFPython.Write("Sorry, you do not have any %ss" %(cointype),activator)
