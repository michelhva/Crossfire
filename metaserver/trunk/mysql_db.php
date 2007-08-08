<?php
//    $Id: mysql_db.php 6865 2007-08-05 10:51:39Z ryo_saeba $
//    CrossFire Metaserver
//
//    Copyright (C) 2007 Mark Wedel & Crossfire Development Team
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//    The authors can be reached via e-mail at crossfire-devel@real-time.com

// The point of this file is to abstract the
// database operations so that instead of the meta_..
// scripts making mysql calls directly, they make
// generic db_.. calls.  In this way, to support
// a different database only requires including a different
// file here, and not rewriting the scripts to use postgres,
// oracle, whatever calls.  Note - the meta_.. scripts
// do presume that some form of SQL backend will be used -
// it would have been possible to move the calls into
// this file here, but that would basically amount to
// each query being its own function here - I'm not
// sure that would be worthwhile.

define ("DB_HOST", "localhost");
define ("DB_USER", "metaserver");
define ("DB_PWD", "metaserver");
define ("DB_NAME", "metaserver");

	
// Connect to a host and select the specified table
function db_connect()
{	
    $link = mysql_connect(DB_HOST, DB_USER, DB_PWD)
	    or die("Could not connect to " . DB_HOST . " as " . DB_USER . " with password\n");

    mysql_select_db(DB_NAME)
	    or die("Could not select database " . DB_NAME);
				
    return $link;
}
	
// Close a connection to a host
function db_close($link)
{	
    // Closing connection
    mysql_close($link);
}
	
// Make a query
function db_query($link, $query)
{
    return(mysql_query($query, $link));
}

// Returns an assoc list of data from the database.
// Passed in parameter is return from db_query
function db_fetch_assoc($qlist)
{
    return(mysql_fetch_assoc($qlist));
}

// Returns the number of rows matched with a query.
// Passed in parameter is return from db_query
function db_num_rows($qlist)
{
	return(mysql_num_rows($qlist));
}


?>
