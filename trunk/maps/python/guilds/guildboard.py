import CFPython
import CFGuilds

def mycmp(a, b):
    return cmp(a[1], b[1])

activator=CFPython.WhoIsActivator()
guilds = CFGuilds.CFGuildHouses()

CFPython.Write('Guild Standings:', activator)
CFPython.Write('Guild - Points - Status', activator)

guildlist = guilds.list_guilds()
standings = []
for guild in guildlist:
    record = guilds.info(guild)
    if record['Status'] != 'inactive':
        standings.append([record['Points'], guild, record['Status']])
standings.sort(mycmp)
for item in standings:
    CFPython.Write('%s - %s - %s' %(item[1],item[0],item[2]), activator)
