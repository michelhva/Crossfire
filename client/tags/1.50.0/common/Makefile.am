#
# Makefile for crossfire-client
#
#   Copyright (C) 2005,2007 Mark Wedel & Crossfire Development Team
#
#   This program is free software; you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation; either version 2 of the License, or
#   (at your option) any later version.
#
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#   along with this program; if not, write to the Free Software
#   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
#   The author can be reached via e-mail to crossfire-devel@real-time.com

noinst_LIBRARIES = libcfclient.a

libcfclient_a_SOURCES = \
	client.c \
	commands.c \
	image.c \
	init.c \
	item.c \
	mapdata.c \
	metaserver.c \
	misc.c \
	newsocket.c \
	p_cmd.c \
	player.c \
	script.c \
	script_lua.c

noinst_HEADERS = \
	cconfig.h \
	client-types.h \
	client.h \
	config.h \
	def-keys.h \
	external.h \
	item-types.h \
	item.h \
	mapdata.h \
	metaserver.h \
	msgtypes.h \
	p_cmd.h \
	proto.h \
	script.h \
	script_lua.h \
        version.h \
        svnversion.h \
	shared/newclient.h

BUILT_SOURCES = svnversion.h
CLEANFILES = svnversion.h

EXTRA_DIST = \
	def-keys \
	item-types \
	items.pl

AM_CPPFLAGS = $(PTHREAD_CFLAGS)

proto:
	cproto -DCPROTO -DCFCLIENT $(DEFAULT_INCLUDES) $(AM_CPPFLAGS) \
		$(libcfclient_a_SOURCES) > proto.h
	chmod 644 proto.h

item-types.h: item-types
	$(PERL) items.pl

def-keys.h: def-keys
	../utils/mdk.sh

$(srcdir)/msgtypes.h: $(srcdir)/shared/newclient.h
	$(PERL) $(srcdir)/msgtypes.pl $(srcdir)

svnversion.h: FORCE
	OUTPUT_DATA='/* Auto-generated at build time. */'; \
	if [ "X`echo -n`" = "X-n" ]; then \
		echo_n() { echo $${1+"$$@"}"\c"; } \
	else \
		echo_n() { echo -n $${1+"$$@"}; } \
	fi; \
	if test -n "$(SVNVERSION)" -a -d "$(top_srcdir)/.svn"; \
	then OUTPUT_DATA=`echo "$$OUTPUT_DATA"; echo_n '#define SVN_REV "'; $(SVNVERSION) -n "$(top_srcdir)"`'"'; \
	fi; \
	if test ! -e svnversion.h; then \
		echo "$$OUTPUT_DATA" > svnversion.h; \
	elif test "$$OUTPUT_DATA" != "`cat svnversion.h`"; then \
		echo "$$OUTPUT_DATA" > svnversion.h; \
	fi

FORCE:
