#!/usr/bin/perl
# $Id$

# Copyright 2000 by Mark Wedel.
# This script follows the same license as crossfire (GPL).

use Socket;
use English;

# We periodically generate a nice HTML file that people can't put their
# web browser at.  This is the location of that file.
$HTML_FILE="/var/apache/htdocs/metaserver.html";

# Cache file to keep data we ahve collected.  This is used so that if
# the metaserver program crashes/dies, it still has some old data.
# You may want to set this to a location that will survive across system
# reboots.
$CACHE_FILE="/var/tmp/meta_xfire.cache";

# We remove a server after $REMOVE_SERVER number of seconds of no updates.
# 86400 is 1 day - maybe a bit long, but at current time I would like to get
# a more accurate of number of servers out there.  Also, I can see an
# established server being down for a while, but I think it is still nice
# to have that in the listing.
$REMOVE_SERVER=86400;

# UPDATE_SYNC determines how often we update the HTML_FILE and CACHE_FILE.
$UPDATE_SYNC=300;

socket(SOCKET, PF_INET, SOCK_DGRAM, getprotobyname("udp")) || 
	die("$0: can not open socket: $OS_ERROR\n");
bind(SOCKET, sockaddr_in(13326, INADDR_ANY)) ||
	die("$0: Can not bind to socket: $OS_ERROR\n");

vec($rin, fileno(SOCKET), 1)=1;

if (open(CACHE,"<$CACHE_FILE")) {
    while (<CACHE>) {
	chomp;
	($ip, $rest) = split /\|/, $_, 2;
	$data{$ip} = $_;
    }
}
close(CACHE);

$last_sync=time;

while (1) {
    $nfound=select($rout=$rin, undef, undef, 60);
    $cur_time=time;
    if ($nfound) {
	$ipaddr = recv(SOCKET, $data, 256, 0) ||
	    print STDERR "$0: error on recv call: $OS_ERROR\n";
	($port, $ip) = sockaddr_in($ipaddr);
	$host = inet_ntoa $ip;
	$data{$host} = "$host|$cur_time|$data";
    }
    # Need to generate some files.  This is also where we remove outdated
    # hosts.
    if ($last_sync+$UPDATE_SYNC < $cur_time) {
	open(CACHE,">$CACHE_FILE");
	open(HTML,">$HTML_FILE");

	print HTML 
'<title>Crossfire Server List</title>
<h1 align=center>Crossfire Server List</h1><p>
<table border=1 align=center cellpadding=5>
<tr>
<th>IP Address</th><th>Last Update Date/Time</th><th>Last Update Minutes Elapsed</th>
<th>Hostname</th><th>Number of Players</th><th>Version</th><th>Comment</th>
</tr>
';

	foreach $i (keys %data) {
	    ($ip, $time, @rest) = split /\|/, $data{$i};
	    if ($time+$REMOVE_SERVER<$cur_time) { 
		delete $data{$i}; 
	    } else {
		print CACHE "$data{$i}\n";
		$elapsed = int(($cur_time - $time)/60);
		$gmtime = gmtime($time);
		print HTML "<tr><td>$i</td><td>$gmtime</td><td>$elapsed</td>";
		print HTML "<td>$rest[0]</td><td>$rest[1]</td><td>$rest[2]</td><td>$rest[3]</td></tr>\n";
	    }
	}
	$gmtime = gmtime($cur_time);
	print HTML "
</table><p>
The server name is reported by the server, while the ip address is determined by
the incoming data packet.  These values may not resolve to the same thing in the
case of multi homed hosts or multi ip hosts.<p>

All times are in GMT.<p>

<font size=-2>Last Updated: $gmtime<font size=+2><p>";
	close(HTML);
	close(CACHE);
    }
}
