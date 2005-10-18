import Crossfire
import os.path
import sys

print "Running python initialize script."
sys.path.insert(0, os.path.join(Crossfire.DataDirectory(), Crossfire.MapDirectory(), 'python'))

import CFGuilds
print "Updating Guilds"
CFGuilds.GuildUpdate()
