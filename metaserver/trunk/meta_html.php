<?
//    $Id: meta_html.php 6865 2007-08-05 10:51:39Z ryo_saeba $
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

// This script reads the database and makes a simple HTML table,
// suitable for viewing in web browsers.

require_once("common.php");


if (!$db=db_connect()) {
    log_message(LOG_ERROR, "Unable to connect to database\n");
    exit;
}

$query="select * from servers where last_update>(now() - $LAST_UPDATE_TIMEOUT) order by hostname";
$qret = db_query($db, $query);

// This really shouldn't happen, but might as well log for if it does.
if (db_num_rows($qret)<1) {
    print "<html><head>No metaservers listed on server</head><body>No metaservers listed on server</body>\n";
    db_close($db);
    exit;
}
print "<table border=1>";
print "<tr><th>Hostname</th><th>Comment</th><th>arch/map/code base</th><th>flags</th>";
print "<th># players</th><th>in/out bytes</th><th>uptime (min)</th><th>version</th><th>cs/sc_version</th>";
print "<th>last update</th></tr>\n";

while ($qrow = db_fetch_assoc($qret)) {
    print "<tr><td>" . $qrow['hostname'] . ":" . $qrow['port'] . "</td>";
    print "<td>" . $qrow['html_comment'] . "</td>";
    print "<td>" . $qrow['archbase'] . "/" . $qrow['mapbase'] . "/" . $qrow['codebase'] . "</td>";
    print "<td>" . $qrow['flags'] . "</td><td>" . $qrow['num_players'] . "</td>";
    print "<td>" . $qrow['in_bytes'] . " / " . $qrow['out_bytes'] . "</td>";
    print "<td>" . (int)($qrow['uptime']/ 60) .  "</td><td>" . $qrow['version'] . "</td>";
    print "<td>" . $qrow['sc_version'] .  " / " . $qrow['cs_version'] . "</td>";
    print "<td>" . $qrow['last_update'] . "</td></tr>\n";

}
print "</table>";
db_close($db);
?>
