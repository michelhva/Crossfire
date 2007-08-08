<?
//    $Id: meta_client.php 6865 2007-08-05 10:51:39Z ryo_saeba $
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

// meta_client.php
// This script produces data for the client in an easy
// to parse form
require_once("common.php");

if (!$db=db_connect()) {
    log_message(LOG_ERROR, "Unable to connect to database\n");
    exit;
}

// Could be simpler to do a select * here, but 
// selecting only the fields we want, as unix format
// for the data as last_update makes the code
// below simpler (don't have to special handle
// unused fields, or special handle
// last_update.
$query="select hostname,port,html_comment,text_comment,archbase,mapbase," .
    "codebase,flags,num_players,in_bytes,out_bytes,uptime,version,sc_version," .
    "cs_version,unix_timestamp(last_update) as last_update " .
    "from servers where last_update>(now() - $LAST_UPDATE_TIMEOUT)";

$qret = db_query($db, $query);

// This really shouldn't happen, but might as well log for if it does.
if (db_num_rows($qret)<1) {
    exit;
}

while ($qrow = db_fetch_assoc($qret)) {
    print "START_SERVER_DATA\n";
    foreach (array_keys($qrow) as $key) {
	if ($qrow[$key] != "")
	    print "$key=".$qrow[$key] . "\n";
    }

    print "END_SERVER_DATA\n";
}
db_close($db);
?>
