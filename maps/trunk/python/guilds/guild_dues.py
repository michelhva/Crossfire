#Script for paying Guild Dues
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# author:Avion temitchell@sourceforge.net

import Crossfire,CFGuilds,CFItemBroker,random,string,sys,CFBank,CFMail,CFLog
log = CFLog.CFLog()
priceMailScroll = 5
priceFactor = 50

guildname=Crossfire.ScriptParameters() # 6 is say event
if (guildname):
     guild = CFGuilds.CFGuild(guildname)
activator=Crossfire.WhoIsActivator()
in_guild=CFGuilds.SearchGuilds(activator.Name)
bankdatabase = "ImperialBank_DB"
bank = CFBank.CFBank(bankdatabase)
def find_mailbox(object):
    
    while (object.Name != 'mailbox') : #1 is type 'Player'
    
        object = object.Above
        if not object:
		
		return 0
    return object



global whoami

X=activator.X
Y=activator.Y
x=6
y=7
x1=31
x2=32
x3=33
x4=34
x5=35
y1=9
y2=10
y3=11
y4=12
y5=13
activator=Crossfire.WhoIsActivator()
activatorname=activator.Name
mymap = activator.Map
whoami=Crossfire.WhoAmI()

if whoami.Name=='Jack':
 Items={'BBQ':200*30000/5000, 'Stove':200*30000/5000, 'AlchemyLab':400*30000/5000, 'Cauldron':200*30000/5000, "CrystalRoom":100,"Tannery":200*30000/5000,"ThaumaturgyRoom":200*30000/5000,"JewelrsRoom":400*30000/5000,"ThaumaturgyDesk":200*30000/5000,"TanningDesk":200*30000/5000,"JewelrsBench":400*30000/5000,\
 "Bowyer":200*30000/5000,"BowyerBench":200*25000/5000,"Smithy":200*30000/5000,"Forge":200*25000/5000}
 Cards=["Stove","Cauldron","TanningDesk","ThaymaturgyDesk","JewelrsBench","BowyerBench","Forge"]
 remarklist = ['Excellent','Thank You','Thank You','Thank You', 'Thank You', 'Great', 'OK', 'Wonderful', 'Swell', 'Dude', 'Big Spender']
 exclaimlist = ['Hey','Hey','Hey','Hey', 'Now just a minute', 'AHEM', 'OK...Wait a minute', 'Look chowderhead']
 buddylist =   ['buddy','buddy','buddy','buddy','pal','friend','friend','friend','friend','dude','chum', 'sweetie']
 
 guildname=Crossfire.ScriptParameters() # 6 is say event
 accountname=guildname+str(guildname.__hash__())
 text = Crossfire.WhatIsMessage().split()
 path=mymap.Path
 path=path.replace("mainfloor","")
 SecondFloor=Crossfire.ReadyMap(path+'secondfloor')
 ToolShed=Crossfire.ReadyMap(path+"guild_toolshed")
 Rooms={"BBQ":(mymap,40,25),"AlchemyLab":(SecondFloor,22,12),"CrystalRoom":(SecondFloor,22,13),"Tannery":(SecondFloor,22,14),"ThaumaturgyRoom":(SecondFloor,21,13),"JewelrsRoom":(SecondFloor,22,14),"Bowyer":(ToolShed,22,16),"Smithy":(ToolShed,23,16)}

 
 if (guildname):
     guild = CFGuilds.CFGuild(guildname)
     cointype = "jadecoin" #What type of token are we using for guild dues?
     object = activator.CheckInventory(cointype)
     if text[0].upper() == 'HELP' or text[0].upper() == 'YES':
         message='Let me know how many jade coins you want to pay.  Say pay <amount>\nYou can also check the balance by saying "balance".\nGuild Masters can also purchase items with the "buy" command.\nI also provide mailscrolls at 10 plat each.'
     elif text[0].upper()=="BUY":
	     
	try:
		     
		     Item=text[1]
		     
	except:
		     
		     Item=None
		     message="Syntax: Buy <item>\n Valid items are: "
		     for i in Items:
			     message+=i+", "
		     message=message[:len(message)-2]
		     message+='.'
		     
	if Item!=None:
	 if activator.DungeonMaster==1:
		ClearanceApproved=5
	 else:
		if guild.info(activatorname)!=0:
			text1=string.split(str(guild.info(activatorname)))

			
			ClearanceApproved = (text1[5])
			if ClearanceApproved=="GuildMaster":
				ClearanceAproved=5
		else:
			ClearanceApproved=0
	
	 if ClearanceApproved<5:
		
		message="Only guild masters and GMs can buy expansions for the guild."
	 else:
		
	  try:
			Price=Items.get(Item)
	  except:
			Item=None	
			Price=None
			message="Syntax: Buy <item>\n Valid items are: "
			for i in Items:
				message+=i+", "
			message=message[:len(message)-2]
			message+='.'
			
	  if Price!=None:
		balance=bank.getbalance(accountname)
		if Price<=balance:
			Card=False
			for i in Cards:
				if i == Item:
					Card=True
			if Card==True:

				pack=activator.Map.CreateObject('package', 40, 29)
				pack.Name = 'IPO-package F: ACME T: ' + guildname
				card=mymap.CreateObject('diploma',40,29)
				card.Name=Item
				
				
				message = "Your card has been ordered and should arrive in the mail shortly."
				
				mailbox=mymap.ObjectAt(12,21)
				
				mailbox=find_mailbox(mailbox)
				if mailbox==0:
					mailbox=mymap.ObjectAt(30,5)
					mailbox=find_mailbox(mailbox)
				if mailbox==0:
					message+=" The postman couldn't find the mailbox.  Please contact a DM to install your mailbox."
				else:
					cardnew=card.InsertInto(pack)
					packnew=pack.InsertInto(mailbox)
				bank.withdraw(accountname, Price)
			else:
				Loc=Rooms.get(Item)
				
				activator.Teleport(Loc[0],Loc[1],Loc[2])
				
				activator.Teleport(mymap,X,Y)
				bank.withdraw(accountname, Price)
		else:
			message="The guild does not have sufficient funds.  " +str(Price)+" jade needed."
     elif text[0] == 'mailscroll':
	if len(text) == 2:
		if log.info(text[1]):
			if activator.PayAmount(priceMailScroll*priceFactor*2):
				whoami.Say('Here is your mailscroll')
				id = activator.Map.CreateObject('scroll', X, Y)
				id.Name = 'mailscroll T: '+text[1]+' F: '+activatorname
				id.NamePl = 'mailscrolls T: '+text[1]+' F: '+activatorname
				id.Value = 0
			else:
				whoami.Say('You need %s platinum for a mailscroll'%priceMailScroll)
		else:
			whoami.Say('I don\'t know %s'%text[1])

	else:
		whoami.Say('Usage "mailscroll <friend>"')
     elif text[0].upper()=='WITHDRAW':
	if activator.DungeonMaster==1:
		ClearanceApproved=5
	else:
		if guild.info(activatorname)!=0:
			text1=string.split(str(guild.info(activatorname)))

			
			ClearanceApproved = (text1[5])
			whoami.Say(repr(ClearanceApproved))
			if ClearanceApproved=="'GuildMaster',":
				ClearanceApproved=5
				whoami.Say("Welcome Guild Master")
			elif ClearanceApproved=="'Master',":
				ClearanceApproved=4
			else:
				ClearanceApproved=0
		else:
			ClearanceApproved=0
	if ClearanceApproved<4:
		
		message="Only guild masters, masters, and GMs can withdraw funds from the guild."
	else:
		try:
			Amount=int(text[1])
			
		
		except:
			message = "Syntax:  Withdraw <quantity>"
			Amount=None
		
		if Amount!=None:
			balance=bank.getbalance(accountname)
			
			if Amount <= balance:
				
				message=(str(Amount))
				
				message+=" Jade coins withdrawn.\nYour new present balance is "
				
				id = activator.Map.CreateObject('jadecoin', activator.X, activator.Y)
				bank.withdraw(accountname, Amount)
				CFItemBroker.Item(id).add(Amount)
				activator.Take(id)
				message+=str(bank.getbalance(accountname))+"."
			else:
				message="You only have "+str(bank.getbalance(accountname))+" Jade coins on account."
				
	
     elif text[0].upper()=='BALANCE':
	balance=bank.getbalance(accountname)
	message="The guild currently has %s Jade coins on deposit" %(str(balance))
     elif text[0].upper() == 'PAY':
         if len(text)==2:
             cost = int(text[1])
	     if cost <0:
		cost=-1*cost
             if (object):
                 pay = CFItemBroker.Item(object).subtract(cost)
                 if (pay):
                     guild.pay_dues(activatorname,cost)
                     message = "%s, %d %s paid to the guild." %(random.choice(remarklist),cost, cointype)
		     bank.deposit(accountname, cost)
                     
                     
                     
                     
                     
                     
                     

                 else:
                     if cost > 1:
                        message ="%s, you don't have %d %ss." %(random.choice(exclaimlist),cost,cointype)
                     else:
                         message ="You don't have any %s %s." %(cointype,random.choice(buddylist))
             else:
                message = "Come back when you got the %ss %s." %(cointype,random.choice(buddylist))
         else:
             message = "How much ya wanna pay %s?" %(random.choice(buddylist))
     else:
         message = "Howdy %s, paying some guild dues today?" %(random.choice(buddylist))
     whoami.Say(message)
 else:
     activator.Write('Guildname Error, please notify a DM')
else:
  accountname=guildname+str(guildname.__hash__())
  
  bank.deposit(accountname,1)
  
  
