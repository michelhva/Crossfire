## CFgetPaths.py
## Todd Mitchell (temitchell@sympatico.ca)
## Generates the proper Crossfire directory paths
## returns the absolute paths with a nice "/" at the end.

import CFPython

def getPaths(directory):
	'''returns absolute path for crossfire directory specified.  
	Options are datadir, mapdir, uniquedir, localdir, playerdir, configdir, tempdir, all.
	"all" will return a dictionary of all paths'''

#Get the Paths

	datadir = "%s/" %(CFPython.GetDataDirectory())
	mapdir = "%s/%s/" %(datadir, CFPython.GetMapDirectory())
	uniquedir = "%s/%s/" %(datadir, CFPython.GetUniqueDirectory())
	localdir = "%s/" %(CFPython.GetLocalDirectory())
	playerdir = "%s/%s/" %(localdir, CFPython.GetPlayerDirectory())
	configdir = "%s/" %(CFPython.GetConfigurationDirectory())
	tempdir = "%s/" %(CFPython.GetTempDirectory())

#make the dictionary

	paths = {"datadir":datadir, "mapdir":mapdir, "uniquedir":uniquedir, "localdir":localdir, "playerdir":playerdir, "configdir":configdir, "tempdir":tempdir}

#return the proper 

	if directory == "all":
		return paths
	else:
		try:
			return paths[directory]
		except KeyError:
			return 0
