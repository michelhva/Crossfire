/*
 * static char *rcsid_init_c =
 *   "$Id$";
 */
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2001 Mark Wedel & Crossfire Development Team

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

    The author can be reached via e-mail to crossfire-devel@real-time.com
*/

/* This handles the initialization of the client.  This includes making
 * the I_IMAGE and I_ARCH commands.
 */

#include <client.h>

#define TEST_FREE_AND_CLEAR(xyz) {if (xyz) { free(xyz); xyz=NULL; } }

void VersionCmd(char *data, int len)
{
    char *cp;

    csocket.cs_version = atoi(data);
    /* set sc_version in case it is an old server supplying only one version */
    csocket.sc_version = csocket.cs_version;
    if (csocket.cs_version != atoi(data)) {
	fprintf(stderr,"Differing C->S version numbers (%d,%d)\n",
	   VERSION_CS,csocket.cs_version);
/*	exit(1);*/
    }
    cp = strchr(data,' ');
    if (!cp) return;
    csocket.sc_version = atoi(cp);
    if (csocket.sc_version != VERSION_SC) {
	fprintf(stderr,"Differing S->C version numbers (%d,%d)\n",
	   VERSION_SC,csocket.sc_version);
    }
    cp = strchr(cp+1, ' ');
    if (cp)
	fprintf(stderr,"Playing on server type %s\n", cp);
}

void SendVersion(ClientSocket csock)
{
    cs_print_string(csock.fd,
		    "version %d %d %s", VERSION_CS, VERSION_SC, VERSION_INFO);
}


void SendAddMe(ClientSocket csock)
{
    cs_print_string(csock.fd, "addme");
}


void SendSetFaceMode(ClientSocket csock,int mode)
{
    cs_print_string(csock.fd, "setfacemode %d", mode);
}


void init_client_vars()
{
    int i;


    /* I think environemental variables should be more important than
     * compiled in defaults, so these probably should be reversed. 
     */
    client_libdir=getenv("CFCLIENT_LIBDIR");
#ifdef CLIENT_LIBDIR
    if (client_libdir==NULL)
	client_libdir=CLIENT_LIBDIR;
#endif

    cpl.count_left = 0;
    cpl.container = NULL;
    memset(&cpl.stats,0, sizeof(Stats));
    cpl.stats.maxsp=1;	/* avoid div by 0 errors */
    cpl.stats.maxhp=1;	/* ditto */
    cpl.stats.maxgrace=1;	/* ditto */
    /* ditto - displayed weapon speed is weapon speed/speed */
    cpl.stats.speed=1;
    cpl.input_text[0]='\0';
    cpl.title[0] = '\0';
    cpl.range[0] = '\0';
    cpl.last_command[0] = '\0';

    for (i=0; i<range_size; i++)
	cpl.ranges[i]=NULL;

    cpl.map_x=0;
    cpl.map_y=0;

    cpl.ob = player_item();
    cpl.below = map_item();
    cpl.magicmap=NULL;
    cpl.showmagic=0;
    cpl.command_window = COMMAND_WINDOW;

    csocket.command_sent=0;
    csocket.command_received=0;
    csocket.command_time=0;

    face_info.faceset = 0;
    face_info.cache_images = FALSE;
    face_info.num_images = 0;
    face_info.bmaps_checksum = 0;
    face_info.old_bmaps_checksum = 0;
    face_info.want_faceset = NULL;
    face_info.download_all_faces = 0;
    face_info.cache_hits=0;
    face_info.cache_misses=0;
    for (i=0; i<MAX_FACE_SETS; i++) {
	face_info.facesets[i].prefix = NULL;
	face_info.facesets[i].fullname = NULL;
	face_info.facesets[i].fallback = 0;
	face_info.facesets[i].size = NULL;
	face_info.facesets[i].extension = NULL;
	face_info.facesets[i].comment = NULL;
    }

}

/* This is used to clear values between connections to different
 * servers.  This needs to be called after init_client_vars has
 * been called because it does not re-allocated some values.
 */

void reset_client_vars()
{
    int i;


    cpl.count_left = 0;
    cpl.container = NULL;
    memset(&cpl.stats,0, sizeof(Stats));
    cpl.stats.maxsp=1;	/* avoid div by 0 errors */
    cpl.stats.maxhp=1;	/* ditto */
    cpl.stats.maxgrace=1;	/* ditto */
    /* ditto - displayed weapon speed is weapon speed/speed */
    cpl.stats.speed=1;
    cpl.input_text[0]='\0';
    cpl.title[0] = '\0';
    cpl.range[0] = '\0';
    cpl.last_command[0] = '\0';

    for (i=0; i<range_size; i++)
	cpl.ranges[i]=NULL;

    cpl.map_x=0;
    cpl.map_y=0;

    cpl.magicmap=NULL;
    cpl.showmagic=0;

    csocket.command_sent=0;
    csocket.command_received=0;
    csocket.command_time=0;

    face_info.faceset = 0;
    face_info.num_images = 0;
    /* Preserve the old one - this can be used to see if the next
     * server has the same name -> number mapping so that we don't
     * need to rebuild all the images.
     */
    face_info.old_bmaps_checksum = face_info.bmaps_checksum;
    face_info.bmaps_checksum = 0;
    face_info.cache_hits=0;
    face_info.cache_misses=0;
    for (i=0; i<MAX_FACE_SETS; i++) {
	TEST_FREE_AND_CLEAR(face_info.facesets[i].prefix);
	TEST_FREE_AND_CLEAR(face_info.facesets[i].fullname);
	face_info.facesets[i].fallback = 0;
	TEST_FREE_AND_CLEAR(face_info.facesets[i].size);
	TEST_FREE_AND_CLEAR(face_info.facesets[i].extension);
	TEST_FREE_AND_CLEAR(face_info.facesets[i].comment);
    }

}
