<?
//    $Id: meta_update.php 6865 2007-08-05 10:51:39Z ryo_saeba $
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

// meta_update takes the HTTP_POST requests from the server, does sanity
// checking, and updates the database files.

require_once("common.php");



// Check that user has set up there config file.  Note that the server will
// see these messages - whether it logs them or not will depend on settings,
// but that should let the server admin fix things up.
if ($_POST['hostname'] == "put.your.hostname.here") {
    echo "You have not properly set up your metaserver2 configuration file - hostname is set to default\n";
    exit;
}

$hostname = gethostbyaddr($_SERVER['REMOTE_ADDR']);
$ip = gethostbyname($_POST['hostname']);

// Basically, either forward or reverse addressing must work -
// if the ip that the user specified hostname resolves to does
// not match that of the incoming connection, or the hostname based
// on the ip of the incoming connection does not match that specified
// by the server, we reject this user - no spoofing of other servers
// allowed.
if ($ip != $_SERVER['REMOTE_ADDR'] && $hostname != $_POST[hostname]) {
    echo "neither forward nor reverse DNS look corresponds to incoming ip address.\n";
    echo "incoming ip: " . $_SERVER['REMOTE_ADDR'] . ", DNS of that: $hostname\n";
    echo "User specified hostname: " . $_POST[hostname] . " IP of that hostname: $ip\n";
    log_message(LOG_WARN, $_SERVER['REMOTE_ADDR'] . " does not have correct hostname set\n");
    exit;
}

if (!$db=db_connect()) {
    log_message(LOG_ERROR, "Unable to connect to database\n");
    exit;
}

// We use the values from the real IP/hostname here - most likely
// people are being blacklisted because of malicious use of the server -
// as such, blacklisting on the hostname they tell us to use isn't
// likely to be very useful.
$query="select * from blacklist where ('" . $_SERVER['REMOTE_ADDR'] ."' regexp hostname) or ('" .
    $hostname . "' regexp hostname)";

$qret = db_query($db, $query);

// If we get a match, provide some feedback to the matching server -
// in that way, if it is in error, they can try and get it corrected.

if (db_num_rows($qret)) {
    while ($qrow = db_fetch_assoc($qret)) {
	print "Your system has been blacklisted.  Matching entry: " . $qrow['hostname'] . "\n";
	log_message(LOG_WARN, "Attempt to connect from blacklisted host: " . $_SERVER['REMOTE_ADDR'] .
		    "/$hostname\n");
    }
    db_close($db);
    exit;
}

// If we get here, the server updating us has passed all its checks, so we
// consider it valid.  Now to update the database.

// First, we need to check to see if this is an update of an existing entry,
// or a new entry.  doing a select * is probably overkill, but may
// prove useful if we need to extra analysis of data in the table.

foreach (array_keys($_POST) as $key) {
    // Replace any newlines with spaces - makes future processing easier
    $our_post[$key] = str_replace("\n", " ", $_POST[$key]);

    // Don't double escape the strings.
    if (!get_magic_quotes_gpc()) $our_post[$key] = mysql_real_escape_string($our_post[$key]);
}

$query="select * from servers where port=" . mysql_real_escape_string($our_post['port'])
    ." and hostname='" .mysql_real_escape_string($our_post['hostname']) . "'";

$qret = db_query($db, $query);

// This really shouldn't happen, but might as well log for if it does.
if (db_num_rows($qret)>1) {
    log_message(LOG_ERROR, "Multiple matches for " . $our_post['hostname'] . ":" .
		$our_post['port'] . "\n");
}

// Got match rows - this must be an update.
// The use of mysql_real_escape_string() may seem excessive, since some of
// these values are supposed to be integers.  But it is potentially
// untrusted severs sending us all this data, and it is sent as strings,
// so better to be safe than sorry.

if (db_num_rows($qret)) {
    $qrow = db_fetch_assoc($qret);
    $update="update servers set hostname='". $our_post['hostname'] .
	"', port='". $our_post['port'] . 
	"', html_comment='". $our_post['html_comment'] . 
	"', text_comment='". $our_post['text_comment'] . 
	"', archbase='". $our_post['archbase'] . 
	"', mapbase='". $our_post['mapbase'] . 
	"', codebase='". $our_post['codebase'] . 
	"', flags='". $our_post['flags'] . 
	"', num_players='". $our_post['num_players'] . 
	"', in_bytes='". $our_post['in_bytes'] . 
	"', out_bytes='". $our_post['out_bytes'] . 
	"', uptime='". $our_post['uptime'] . 
	"', version='". $our_post['version'] . 
	"', sc_version='". $our_post['sc_version'] . 
	"', cs_version='". $our_post['cs_version'] . 
	"', last_update=now() where entry=" . $qrow['entry'] . ";";
} else {
    $update="insert into servers (hostname, port, html_comment, text_comment, " .
	"archbase, mapbase, codebase, flags, num_players, in_bytes, out_bytes, " .
	"uptime, version, sc_version, cs_version, last_update) values (" .
	"'" . $our_post['hostname'] . "', " .
	"'" . $our_post['port'] . "', " .
	"'" . $our_post['html_comment'] . "', " .
	"'" . $our_post['text_comment'] . "', " .
	"'" . $our_post['archbase'] . "', " .
	"'" . $our_post['mapbase'] . "', " .
	"'" . $our_post['codebase'] . "', " .
	"'" . $our_post['flags'] . "', " .
	"'" . $our_post['num_players'] . "', " .
	"'" . $our_post['in_bytes'] . "', " .
	"'" . $our_post['out_bytes'] . "', " .
	"'" . $our_post['uptime'] . "', " .
	"'" . $our_post['version'] . "', " .
	"'" . $our_post['sc_version'] . "', " .
	"'" . $our_post['cs_version'] . "', " .
	"now() );";
}

// Not really a query, but updat
$result = db_query($db, $update);
if (!$result) {
    LOG(LOG_ERROR, "Update/insert failed: $query\n");
}

db_close($db);
?>
