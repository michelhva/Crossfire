#!/usr/bin/perl
#
# This script goes through and updates the exits for maps.
# First pass, so a lot of the options need to be set in this
# script.  It will search all directories in and below your current
# working directory, so run from the directory you want to update.

# Written by Mark Wedel (mwedel@sonic.net) 
# This borrows some amount of code from the map_info script written
# by Tero Haatanen <Tero.Haatanen@lut.fi>

# Name of the old map that we update exits on
# Note that this can be a regexp.
$OLD_MAP_NAME="(/santo_dominion/town|../town|../../town)";

# OLD_MAP_STARTX/Y and OLD_MAP_ENDX/Y determine the range for the 
# updates.  For example, scorn/city was broken up on two of the
# map tiles, so this gets used to correspond that properly.
# you can use very large END values just to make sure the entire
# map is covered
$OLD_MAP_STARTX=3;
$OLD_MAP_STARTY=12;
$OLD_MAP_ENDX=27;
$OLD_MAP_ENDY=100;

# New map names.  OFFX/Y is the offset compared to the old values - these
# can be negative provided that STARTX above is positive (eg, the
# map is being shifted.)

$NEW_MAP_NAME="/world/world_102_108";
$NEW_MAP_OFFX=0;
$NEW_MAP_OFFY=-9;

$VERBOSE=0;


if ((($OLD_MAP_STARTX + $NEW_MAP_OFFX) < 0) || 
    (($OLD_MAP_STARTY + $NEW_MAP_OFFY) < 0 )) {
	print "Current settings will result in negative destination coordinates.\n";
	exit(1);
}


&maplist(".");


while ($file = shift (@maps)) {
    &updatemap;
}


exit;

# return table containing all objects in the map
sub updatemap {
    local ($m, $made_change=0);
    $last = "";
    $parent = "";
    
    # Note that $/ is the input record seperator.  By changing
    # this to \nend\n, it means that when we read from the file,
    # we basically read an entire arch at the same time.  Note that
    # given this, $ in regexps matches this value below, and not
    # a newline.  \n should generally be used instead of $ in
    # regexps if you really want the end of line.
    # Similary, ^ matches start of record, which means the arch line.

    $/ = "\nend\n";
    if (! open (IN, $file)) {
	print "Can't open map file $file\n";
	return;
    }
    $_ = <IN>;
    if (! /^arch map\n/) {
	print "Error: file $file isn't mapfile. ($_)\n";
	return;
    }
    if (! open(OUT, ">$file.new")) {
	print "Can't open output file $file.new\n";
	return;
    }
    print OUT $_;
    if ($VERBOSE) {
	    print "Testing $file, ";
	    print /^name (.+)$/ ? $1 : "No mapname";
	    print ", size [", /^x (\d+)$/ ? $1 : 16;
	    print ",", /^y (\d+)/ ? $1 : 16, "]";
    
	    if (! /^msg$/) {
		print ", No message\n";
	    } elsif (/(\w+@\S+)/) {
		print ", $1\n";
	    } else {
		print ", Unknown\n";
	    }
	    $printmap=0;
    }
    else {
	$name=  /^name (.+)$/ ? $1 : "No mapname";
	$x=  /^x (\d+)$/ ? $1 : 16;
	$y= /^y (\d+)/ ? $1 : 16;
	$mapname="Map $file, $name, size [$x, $y]\n" ;
	$printmap=1;
    }

    while (<IN>) {
	if (($m = (@_ = /^arch \S+\s*$/g)) > 1) {
	    $parent = /^arch (\S+)\s*$/;
	    print OUT $_;

	    # Object has an inventory.  Just read through until we get
	    # an end
	    while (<IN>) {
		last if (/((.|\n)*end\n)(arch (.|\n)*\nend\n)/);
		print OUT $_;
	    }
	    $parent="";
	    # Objects with inventory should not contain exits, so
	    # do not need to try and process them.  Likewise, the objects
	    # in the inventory should not contain exits.
	} else { 
	    if (m#\nslaying $OLD_MAP_NAME\n#) {
		$destx = /\nhp (\d+)\n/ ? $1 : 0;
		$desty = /\nsp (\d+)\n/ ? $1 : 0;
		if ($destx >= $OLD_MAP_STARTX && $destx <= $OLD_MAP_ENDX  &&
		    $desty >= $OLD_MAP_STARTY && $desty <= $OLD_MAP_ENDY) {
		    # Ok.  This exit matches our criteria.  Substitute in
		    # the new values
		    s/slaying $OLD_MAP_NAME\n/slaying $NEW_MAP_NAME\n/;
		    $destx += $NEW_MAP_OFFX;
		    $desty += $NEW_MAP_OFFY;
		    s/\nhp \d+\n/\nhp $destx\n/;
		    s/\nsp \d+\n/\nsp $desty\n/;
		    $made_change=1;
		}
	    }
	    print OUT $_;
	} # else not an object with inventory
    } # while <IN> LOOP
    close (IN);
    close(OUT);
    if ($made_change) {
	print "$file has changed\n";
	unlink($file);
	rename("$file.new", $file);
    }
    else {
	unlink("$file.new");
    }
}

# @maps contains all filenames
sub maplist {
    local ($dir, $file, @dirs) = shift;

    opendir (DIR , $dir) || die "Can't open directory : $dir\n";
    while ($file = readdir (DIR)) {
	next if ($file eq "." || $file eq ".." || $file eq "CVS");

	$file = "$dir/$file";
	push (@dirs, $file) if (-d $file);
	push (@maps, $file) if (-f $file);
    }
    closedir (DIR);

    # recursive handle sub-dirs too
    while ($_ = shift @dirs) {
	&maplist ($_);
    }
}

