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
from CFGuildClearance import CheckClearance
log = CFLog.CFLog()
priceMailScroll = 5
priceFactor = 50

CoinTypes={"SILVER":1,"GOLD":10,"PLATINUM":50,"JADE":5000,"AMBERIUM":500000, "IMPERIAL NOTE":10000,"TEN IMPERIAL NOTE":100000,"ONE HUNDRED IMPERIAL NOTE":1000000}
ArchType={"SILVER":"silvercoin","GOLD":"goldcoin","PLATINUM":"platinacoin","JADE":"jadecoin","AMBERIUM":"ambercoin", "IMPERIAL NOTE":"imperial","TEN IMPERIAL NOTE":"imperial10","ONE HUNDRED IMPERIAL NOTE":"imperial100"}
bankdatabase = "ImperialBank_DB"
Items={'BBQ':200*30000, 'Stove':200*30000, 'AlchemyLab':400*30000, 'Cauldron':200*30000, "CrystalRoom":100,"Tannery":200*30000,"ThaumaturgyRoom":200*30000,"JewelrsRoom":400*30000,"ThaumaturgyDesk":200*30000,"TanningDesk":200*30000,"JewelrsBench":400*30000,"Bowyer":200*30000,"BowyerBench":200*25000,"Smithy":200*30000,"Forge":200*25000}
Cards=["Stove","Cauldron","TanningDesk","ThaymaturgyDesk","JewelrsBench","BowyerBench","Forge"]
remarklist = ['Excellent','Thank You','Thank You','Thank You', 'Thank You', 'Great', 'OK', 'Wonderful', 'Swell', 'Dude', 'Big Spender']
exclaimlist = ['Hey','Hey','Hey','Hey', 'Now just a minute', 'AHEM', 'OK...Wait a minute', 'Look chowderhead']
buddylist =   ['buddy','buddy','buddy','buddy','pal','friend','friend','friend','friend','dude','chum', 'sweetie']
whoami=Crossfire.WhoAmI()
mymap=whoami.Map
path=mymap.Path
path=path.replace("mainfloor","")
SecondFloor=Crossfire.ReadyMap(path+'secondfloor')
ToolShed=Crossfire.ReadyMap(path+"guild_toolshed")
Rooms={"BBQ":(mymap,40,25),"AlchemyLab":(SecondFloor,22,12),"CrystalRoom":(SecondFloor,22,13),"Tannery":(SecondFloor,22,14),"ThaumaturgyRoom":(SecondFloor,21,13),"JewelrsRoom":(SecondFloor,22,14),"Bowyer":(ToolShed,22,16),"Smithy":(ToolShed,23,16)}

def find_mailbox(object):
    
    while (object.Name != 'mailbox'):
    
        object = object.Above
        if not object:
                
                return 0
    return object
def FindCoin(object):
    
    while (object.Name.find('silver coin')==-1):
        object = object.Above
        if not object:
                
                return 0
    return object
guildname=Crossfire.ScriptParameters()
bank = CFBank.CFBank(bankdatabase)
accountname=guildname+str(guildname.__hash__())
if (guildname):
    if whoami.Name=='Jack':
        guild = CFGuilds.CFGuild(guildname)
        activator=Crossfire.WhoIsActivator()
        X=activator.X
        Y=activator.Y
        bank = CFBank.CFBank(bankdatabase)
        in_guild=CFGuilds.SearchGuilds(activator.Name)
        
        text = Crossfire.WhatIsMessage().split()
        if text[0].upper() == 'HELP' or text[0].upper() == 'YES':
                message=('Let me know how much you want to pay.  Say pay <amount> <cointype>\n\tValid coin types are '+', '.join(CoinTypes)+'.\nYou can also check the balance by saying "balance".\nGuild Masters can also purchase items with the "buy" command.\nI also provide mailscrolls at 10 plat each.')
        elif text[0].upper()=="BUY":
                try:
                        Item=text[1]
                except:
                        Item=None
                        message="Syntax: Buy <item>\n Valid items are: "+', '.join([i +' - '+ str( Items[i]) for i in Items])+'.'
                if Item!=None:
                        if ( not CheckClearance([guildname, "GuildMaster"],activator)):
                                
                                message="Only guild masters and GMs can buy expansions for the guild."
                        else:
                                try:
                                        Price=Items.get(Item)
                                except:
                                        Item=None	
                                        Price=None
                                        message="Syntax: Buy <item>\n Valid items are: "+', '.join(Items)+'.'
                                if Price!=None:
                                        balance=bank.getbalance(accountname)
                                        if Price<=balance:
                                                Card=Cards.__contains__(Item)
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
                                                        coin=Loc[0].ObjectAt(Loc[1],Loc[2])
                                                        coin=FindCoin(coin)
                                                        message="The guild already has this expansion!"
                                                        if (coin==0):
                                                                coin=mymap.CreateObject('silvercoin',40,29)
                                                                coin.Teleport(Loc[0],Loc[1],Loc[2])
                                                                #activator.Teleport(mymap,X,Y)
                                                                bank.withdraw(accountname, Price)
                                                                message="The new room has been unlocked.\n"
                                                                message+="The guild now has a balance of "
                                                                message+=str(bank.getbalance(accountname))+" silver coins."
                                        else:
                                                message="The guild does not have sufficient funds."
        elif text[0] == 'mailscroll':
                if len(text) == 2:
                        if log.info(text[1]):
                                if activator.PayAmount(priceMailScroll*priceFactor*2):
                                        whoami.Say('Here is your mailscroll')
                                        id = activator.Map.CreateObject('scroll', X, Y)
                                        id.Name = 'mailscroll T: '+text[1]+' F: '+activator.Name
                                        id.NamePl = 'mailscrolls T: '+text[1]+' F: '+activator.Name
                                        id.Value = 0
                                else:
                                        whoami.Say('You need %s platinum for a mailscroll'%priceMailScroll)
                        else:
                                whoami.Say('I don\'t know %s'%text[1])
                else:
                        whoami.Say('Usage "mailscroll <friend>"')
        elif text[0].upper()=='WITHDRAW':
                if (not activator.DungeonMaster==1 and not CheckClearance([guildname,"Master"],activator)):
		
                        message="Only guild masters, masters, and DMs can withdraw funds from the guild."
                else:   
                        try:
                                Amount=int(text[1])
			
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
		
                        except:
                                message = "Syntax:  Withdraw <quantity> {coin type=Silver}"
                                Amount=None
		
                        if Amount!=None:
                                balance=bank.getbalance(accountname)
                                try:
                                        Type=' '.join(text[2:])
                                except:
                                        Type="Silver"
                                Value=CoinTypes.get(Type.upper(),1)
                                if Value==1:
                                        Type="SILVER"
                                if Amount*Value <= balance:
				
                                        message=(str(Amount))
				
                                        message+=" "+Type+" withdrawn.\nYour new present balance is "
				
                                        id = activator.Map.CreateObject(ArchType.get(Type.upper()), activator.X, activator.Y)
                                        bank.withdraw(accountname, Amount*Value)
                                        CFItemBroker.Item(id).add(Amount)
                                        activator.Take(id)
                                        message+=str(bank.getbalance(accountname))+"."
                                else:
                                        message="You only have "+str(bank.getbalance(accountname))+" silver coins on account."
				
	
        elif text[0].upper()=='BALANCE':
                balance=bank.getbalance(accountname)
                message="The guild currently has %s silver coins on deposit" %(str(balance))
        elif text[0].upper() == 'PAY':
                if len(text)>2:
                        cost,conversionfactor=text[1],CoinTypes.get(' '.join(text[2:]).upper())
                        total=int(cost)*conversionfactor
                        if activator.PayAmount(total):
                                guild.pay_dues(activator.Name,total)
                                message = "%s, %s %s paid to the guild." %(random.choice(remarklist),cost, ' '.join(text[2:]))
                                bank.deposit(accountname, total)
                        else:
                                if cost > 1:
                                        message ="%s, you don't have %s %ss." %(random.choice(exclaimlist),cost,' '.join(text[2:]))
                                else:
                                        message ="You don't have any %s, %s." %(' '.join(text[2:]),random.choice(buddylist))
                else:
                        message = "How much ya wanna pay %s?" %(random.choice(buddylist))
        else:
                message = "Howdy %s, paying some guild dues today?" %(random.choice(buddylist))
        whoami.Say(message)
    else:
            bank.deposit(accountname, 5000)
else:
     activator.Write('dues Error, please notify a DM')