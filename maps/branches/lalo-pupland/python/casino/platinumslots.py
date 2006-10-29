#SlotMachine configuration file
#to make a new kind of slot machine, copy this file, change the settings and point the slotmachine to the new file.
# Standard type Platinum Slots

import Crossfire
import CFGamble
import CFItemBroker

activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
whoami=Crossfire.WhoAmI()
#gets slot name and adds map name for unique jackpot
slotname= '%s#%s' %(whoami.Name,whoami.Map.Path)
x=activator.X
y=activator.Y

cointype = "platinacoin" #What type of coin is this slotmachine using?
minpot = 200 #Minimum slot jackpot size
maxpot = 100000 #Maxiumum slot jackpot size
cost = 1 #Price of usage

#Change the items on the slot spinner or the number of items.
slotlist = ["Jester", "Lord", "Lady", "Prince", "Princess", "King", "Queen", "JackPot"]

spinners = 4 #How many spinners on the slotmachine?


Slots=CFGamble.SlotMachine(slotname,slotlist,minpot,maxpot)

if (activator.PayAmount(cost*50)):#platinumcoin
   Slots.placebet(cost)
   results = Slots.spin(spinners)
   pay = 0
   pot = Slots.checkslot()
   activator.Write('%s' %results, 7)
   for item in results:
      #match all but one - pays out by coin e.g 3 to 1 or 4 to 1
      if results.count(item) == spinners-1:
         if item == "Jester":
            pay = 1
         elif item == "Lord":
            pay = 2
         elif item == "Lady":
            pay = 3
         elif item == "Prince":
            pay = 4
         elif item == "Princess":
            pay = 5
         elif item == "Queen":
            pay = 10
         elif item == "King":
            pay = 20
	 elif item == "Jackpot":
            pay = 25
         else:
	    break
	 activator.Write("%d %ss, a minor win!" %(spinners-1,item))
         payoff = cost*pay
         Slots.payoff(payoff)
         id = activator.Map.CreateObject(cointype, x, y)
         CFItemBroker.Item(id).add(payoff)
         if payoff == 1:
            message = "you win %d %s!" %(payoff,cointype)
	 else:
	    message = "You win %d %ss!!" %(payoff,cointype)
         break
      elif results.count(item) == spinners:
         #all match - pays out as percent of pot
         activator.Write('%d %ss, a Major win!' %(spinners,item))
         if item == "Jester":
            pay = .1
         elif item == "Lord":
            pay = .15
         elif item == "Lady":
            pay = .25
         elif item == "Prince":
            pay = .3
         elif item == "Princess":
            pay = .4
         elif item == "Queen":
            pay = .5
         elif item == "King":
            pay = .6
	 elif item == "JackPot":
            pay = 1
         payoff = pot*pay
         Slots.payoff(payoff)
         id = activator.Map.CreateObject(cointype, x, y)
         CFItemBroker.Item(id).add(payoff)
         if payoff == 1:
            message = "you win %d %s!" %(payoff,cointype)
	 else:
	    message = "You win %d %ss!!" %(payoff,cointype)
         break
      else:
         message = "Better luck next time!"
   activator.Write(message)
   activator.Write("%d in the Jackpot, Play again?" %Slots.checkslot())
else:
   activator.Write("Sorry, you do not have enough money")
