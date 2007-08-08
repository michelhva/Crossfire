<?
//    $Id: common.php 6865 2007-08-05 10:51:39Z ryo_saeba $
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

// some useful/common functions

$LOGFILE="/tmp/metaserver2.log";

// If the last update is more than this number of seconds,
// we don't include it in any data we output.  Some form/script
// to clean up old entries from the database is perhaps needed,
// but maybe having old entries for historical reasons is nice.
// 3600 = 1 hour, which seems reasonable - if not listed
// for that long, server is probably down, so shy list it.
$LAST_UPDATE_TIMEOUT=3600;


require_once("mysql_db.php");

// Have to be careful on a lot of these names,
// since syslog uses similar names.
define("LOG_ERROR", 3);		// Script gets unexpected run data - can't open database for exampl;e
define("LOG_WARN", 2);		// Some servers/clients are doing odd things
define("LOG_DEBUGGING", 1);     // Debug type messages used for debugging

// LOG_LEVEL is what leve messages to log - things of higher
// importance are also logged.
define("LOG_LEVEL", LOG_WARN);

function log_message($level, $message) {
    global $LOGFILE;

    if (!isset($LOGFILE)) return;

    if (!$fp = fopen($LOGFILE,"a")) {
	return;
    }
    if (fwrite($fp, "$level: $message") === FALSE) {
        return;
    }
    fclose($fp);
}
