import Crossfire
import os.path
import sys

print "Running python initialize script."
sys.path.insert(0, os.path.join(Crossfire.DataDirectory(), Crossfire.MapDirectory(), 'python'))

path = os.path.join(Crossfire.DataDirectory(), Crossfire.MapDirectory(), 'python/events/init')

scripts = os.listdir(path)

for script in scripts:
	if (script.endswith('.py')):
		execfile(os.path.join(path, script))
