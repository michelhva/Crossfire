#!/usr/local/bin/perl

$MAXVAL=256;

# Number of subscripts
$SUBS=64;

$lastval=-1;

open(ITEMS,"<item_types") || die("can not open item_types file");
while(<ITEMS>) {
	next if (/^#/ || /^\s*$/);
	if (/^(\d*):/) {
		$lastval=$1;
	}
	# skip empty lines 
	else {
	    chomp;
	    die("Got item name before item number: $_\n") if ($lastval == -1);
	    push @{ $names[$lastval]} , $_;
	}
}
close(ITEMS);		

open(ITEMS, ">item_types.h") || die("Can not open item_types.h\n");

print ITEMS "/* This file is automatically generated editing by hand is strongly*/\n";
print ITEMS "/* discouraged.  Look ath the item_types file and the items.pl conversion */\n";
print ITEMS "/* script. */\n";

print ITEMS "\n#define NUM_ITEM_TYPES $MAXVAL\n";
print ITEMS "#define MAX_NAMES_PER_TYPE $SUBS\n\n";

print ITEMS "static char *item_types[$MAXVAL][$SUBS] = {\n";

for ($i=0; $i<$MAXVAL; $i++) {
    print ITEMS "{ ";
    for ($j=0; $j<= $#{ $names[$i] }; $j++) {
	print ITEMS "\"$names[$i][$j]\", ";
    }
    print ITEMS "NULL }, \n";
}
print ITEMS "}; \n";
close(ITEMS);
