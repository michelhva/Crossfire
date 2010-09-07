# -*- coding: utf-8 -*-
# This Python script installs all guilds to their location.
import os
from optparse import OptionParser
parser = OptionParser()
parser.add_option("--local-copy",dest="local_copy",help="puts a copy of generated files in a templates/guild/<guildname>",default=False,action="store_true")
parser.add_option("--no-install-copy",dest="install_copy",help="installs a remote copy to the destination directory.  If false, only configures the files for installation.",default=False,action="store_true")

(options, args) = parser.parse_args()
local_copy = " --local-copy" if options.local_copy else ""
local_copy+=' --no-install-copy' if options.local_copy else ''
t=open('GuildLocations')
a=t.read()
t.close()
b=a.split('\n')
for c in b:
        print c + local_copy
	os.system('./convert.py '+c + local_copy)