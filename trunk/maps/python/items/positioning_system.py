import CFPython

world_prefix = '/world/world_'
world_len = len( world_prefix ) + len( 'xxx_xxx' )
world_sep = '_'
world_map_size = 50

CFPython.SetReturnValue( 1 )

player = CFPython.WhoIsActivator()
gps = CFPython.WhoAmI()
map = CFPython.GetMap( player )

if ( map == 0 ):
	CFPython.CFWrite( 'You\'re lost in a vacuum!', player )
else:
	path = CFPython.GetMapPath( map )
	if ( path.find( world_prefix ) != 0 ) or ( len( path ) != world_len ):
		CFPython.Write( 'You can\'t position yourself here.', player )
	else:
		marked = CFPython.GetMarkedItem( player )

		if ( marked != gps ) and ( CFPython.GetFood( gps ) == 0 ):
			CFPython.Write( 'You must fix the origin of the positioning system first!', player )
		else:
			coord = path.split( world_sep )
			if ( len( coord ) != 3 ):
				CFPython.Write( 'Strange place, you can\'t position yourself...', player )
			else:
				map_x = int( coord[ 1 ] ) - 99
				map_y = int( coord[ 2 ] ) - 99
				x = map_x * world_map_size + CFPython.GetXPosition( player )
				y = map_y * world_map_size + CFPython.GetYPosition( player )

				if ( marked == gps ):
					CFPython.SetHP( gps, x )
					CFPython.SetSP( gps, y )
					CFPython.SetFood( gps, 1 )
					CFPython.Write( 'You reset the origin of the system.', player )
				else:
					x = x - CFPython.GetHP( gps )
					y = y - CFPython.GetSP( gps )
					CFPython.Write( 'You are at %s:%s.'%( x, y ), player )
