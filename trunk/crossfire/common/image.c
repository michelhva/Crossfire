/*
 * static char *rcsid_image_c =
 *   "$Id$";
 */

/*
    CrossFire, A Multiplayer game for X-windows

    Copyright (C) 2000 Mark Wedel
    Copyright (C) 1992 Frank Tore Johansen

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

    The maintainer of this code can be reached at mwedel@scruz.net
*/



#include <global.h>
#include <stdio.h>

/* bmappair and xbm are used when looking for the image id numbe
 * of a face by name.  xbm is sorted alphabetically so that bsearch
 * can be used to quickly find the entry for a name.  the number is
 * then an index into the new_faces array.
 */
New_Face *new_faces;

struct bmappair {
    char *name;
    unsigned int number;
};

static struct bmappair *xbm=NULL;

/* only used in loader.c, to go from the numeric image id (which is
 * used throughout the program) backto the standard name.
 */

MapLook blank_look;

/* Following can just as easily be pointers, but 
 * it is easier to keep them like this.
 */
New_Face *blank_face, *dark_faces[3], *potion_face, *empty_face;


/* nroffiles is the actual number of bitmaps defined.
 * nrofpixmaps is the higest numbers bitmap that is loaded.  With
 * the automatic generation of the bmaps file, this is now equal
 * to nroffiles.
 *
 * The xbm array (which contains name and number information, and
 * is then sorted) contains nroffiles+1 entries.  the xbm_names
 * array (which is used for converting the numeric face to
 * a name) contains nrofpixmaps+1 entries.
 */
int nroffiles = 0, nrofpixmaps=0;

/* This now merges the color alias and color name table together.
 * The first entry is the color as it is referred to in objects,
 * the second is the actual screen/X11 color to use.  NUM_COLORS is now
 * defined in global.h - if adding colors, increase that value, and everything
 * else should work appropriately.
 */
char *colorname[NUM_COLORS][2] = {
{"black",	"Black"},		/* 0  */
{"white",	"White"},		/* 1  */
{"blue",	"Navy"},		/* 2  */
{"red",		"Red"},			/* 3  */
{"orange",	"Orange"},		/* 4  */
{"light_blue",	"DodgerBlue"},		/* 5  */
{"dark_orange",	"DarkOrange2"},		/* 6  */
{"green",	"SeaGreen"},		/* 7  */
{"light_green",	"DarkSeaGreen"},	/* 8  - Used for window background color */
{"grey",	"Grey50"},		/* 9  */
{"brown",	"Sienna"},		/* 10 */
{"yellow",	"Gold"},		/* 11 */
{"khaki",	"Khaki"}		/* 12 */ 
};

static int compar (struct bmappair *a, struct bmappair *b) {
    return strcmp (a->name, b->name);
}


/*
 * Returns the matching color in the coloralias if found,
 * 0 otherwise.  Note that 0 will actually be black, so there is no
 * way the calling function can tell if an error occurred or not
 *
 * CF 0.91.7: relocated from loader.c file - this perhaps can be
 * declared static.
 */

char find_color(char *name) {
  int i;
  for(i=0;i<NUM_COLORS;i++)
    if(!strcmp(name,colorname[i][0]))
      return i;
  LOG(llevError,"Unknown color: %s\n",name);
  return 0;
}

/* This reads the lib/faces file, getting color and visibility information.
 * it is called by ReadBmapNames.
 */


static void ReadFaceData()
{
    char buf[MAX_BUF], *cp;
    New_Face *on_face=NULL;
    FILE *fp;

    sprintf(buf,"%s/faces", settings.datadir);
    LOG(llevDebug,"Reading faces from %s...",buf);
    if ((fp=fopen(buf,"r"))==NULL) {
	perror("Can't open faces file");
	printf("buf = %s\n", buf);
	exit(-1);
    }
    while (fgets(buf, MAX_BUF, fp)!=NULL) {
	if (*buf=='#') continue;
	if (!strncmp(buf,"end",3)) {
	    on_face = NULL;
	}
	else if (!strncmp(buf,"face",4)) {
	     int tmp;

	     cp = buf + 5;
	     cp[strlen(cp)-1] = '\0';	/* remove newline */

	     if ((tmp=FindFace(cp,-1))==-1) {
		LOG(llevError,"Could not find face %s\n", cp);
		continue;
	     }
	     on_face = &new_faces[tmp];
	    on_face->visibility=0;
	}
	else if (on_face==NULL) {
	    LOG(llevError,"Got line with no face set: %s\n", buf);
	}
	else if (!strncmp(buf,"color_fg",8)) {
	    cp = buf + 9;
	    cp[strlen(cp)-1] = '\0';
	    on_face->fg = find_color(cp);
	    if (on_face->magicmap==255) on_face->magicmap=on_face->fg;
	}
	else if (!strncmp(buf,"color_bg",8)) {
	    cp = buf + 9;
	    cp[strlen(cp)-1] = '\0';
	    on_face->bg = find_color(cp);
	}
	else if (!strncmp(buf,"visibility",10)) {
	    on_face->visibility = atoi(buf + 11);
	}
	else if (!strncmp(buf,"magicmap",8)) {
	    cp=buf+9;
	    cp[strlen(cp)-1] = '\0';
	    on_face->magicmap=find_color(cp);
	}
	else if (!strncmp(buf,"is_floor",8)) {
	    int value = atoi(buf+9);
	    if (value) on_face->magicmap |= FACE_FLOOR;
	}
	else LOG(llevDebug,"Got unknown line in faces file: %s\n", buf);
    }
    LOG(llevDebug,"done\n");
    fclose(fp);
}

/* This reads the bmaps.paths file to get all the bitmap names and
 * stuff.  It only needs to be done once, because it is player
 * independent (ie, what display the person is on will not make a
 * difference.)
 */

int ReadBmapNames () {
    char buf[MAX_BUF], *p, *q;
    FILE *fp;
    int value, nrofbmaps = 0, i, fg,bg;

    sprintf (buf,"%s/bmaps", settings.datadir);
    LOG(llevDebug,"Reading bmaps from %s...",buf);
    if ((fp=fopen(buf,"r"))==NULL) {
	perror("Can't open bmaps file");
	printf("buf = %s\n", buf);
	exit(-1);
    }
    
    /* First count how many bitmaps we have, so we can allocate correctly */
    while (fgets (buf, MAX_BUF, fp)!=NULL)
	if(buf[0] != '#' && buf[0] != '\n' )
	    nrofbmaps++;
    rewind(fp);
    
    xbm = (struct bmappair *) malloc(sizeof(struct bmappair) * (nrofbmaps + 1));
    memset (xbm, 0, sizeof (struct bmappair) * (nrofbmaps + 1));
    
    while(fgets (buf, MAX_BUF, fp)!=NULL) {
	if (*buf == '#')
	    continue;
	
	p = (*buf == '\\') ? (buf + 1): buf;
	if (!(p = strtok (p , " \t")) || !(q = strtok (NULL , " \t\n"))) {
	    LOG(llevDebug,"Warning, syntax error: %s\n", buf);
	    continue;
	}
	value = atoi (p);
	xbm[nroffiles].name = strdup_local(q);
	xbm[nroffiles].number = value;
	nroffiles++;
	if(value > nrofpixmaps)
	    nrofpixmaps = value;
    }
    fclose(fp);

    LOG(llevDebug,"done (got %d/%d/%d)\n",nrofpixmaps,nrofbmaps,nroffiles);

    new_faces = (New_Face *)malloc(sizeof(New_Face) * (nrofpixmaps+1));
    fg = find_color ("black");
    bg = find_color ("khaki");
    for (i = 0; i <= nrofpixmaps; i++) {
	new_faces[i].name = "";
	new_faces[i].number = i;
	new_faces[i].fg = fg;
	new_faces[i].bg = bg;
	new_faces[i].visibility=0;
	new_faces[i].magicmap=255;
    }
    for (i = 0; i < nroffiles; i++) {
	new_faces[xbm[i].number].name = xbm[i].name;
    }

    nrofpixmaps++;

    qsort (xbm, nrofbmaps, sizeof(struct bmappair), (int (*)())compar);

    ReadFaceData();

    for (i = 0; i < nrofpixmaps; i++) {
	if (new_faces[i].magicmap==255) {
#if 0	/* Useful for initial debugging, not needed now */
	    LOG(llevDebug,"Face %s still had default magicmap, resetting to black\n",
		new_faces[i].name);
#endif
	    new_faces[i].magicmap=0;
	}
    }
    /* Actually forcefully setting the colors here probably should not
     * be done - it could easily create confusion.
     */
    blank_face = &new_faces[FindFace(BLANK_FACE_NAME, 0)];
    blank_look.face = blank_face;
    blank_look.flags = 0;
    blank_face->fg = find_color ("black");
    blank_face->bg = find_color ("khaki");
    blank_face->magicmap = find_color ("khaki") | FACE_FLOOR;

    empty_face = &new_faces[FindFace(EMPTY_FACE_NAME, 0)];

    dark_faces[0] = &new_faces[FindFace (DARK_FACE1_NAME,0)];
    dark_faces[1] = &new_faces[FindFace (DARK_FACE2_NAME,0)];
    dark_faces[2] = &new_faces[FindFace (DARK_FACE3_NAME,0)];

    potion_face  = &new_faces[FindFace(POTION_FACE_NAME,0)];
    potion_face->fg = find_color("light_blue");
    potion_face->bg = find_color("khaki");

    return nrofpixmaps;
}


/* This returns an the face number of face 'name'.  Number is constant
 * during an invocation, but not necessarily between versions (this
 * is because the faces are arranged in alphabetical order, so
 * if a face is removed or added, all faces after that will now
 * have a different number.
 *
 * the parameter error determines behaviour.  If a face is
 * not found, then error is returned.  This can be useful if
 * you want some default face used, or can be set to negative
 * so that it will be known that the face could not be found
 * (needed in client, so that it will know to request that image
 * from the server)
 */
int FindFace (char *name, int error) {
    int i;
    struct bmappair *bp, tmp;
    char *p;


    /* Using actual numbers for faces is a very bad idea.  This is because
     * each time the archetype file is rebuilt, all the face numbers
     * change.
     */
    if ((i = atoi(name))) {
	LOG(llevError,"Warning: Integer face name used: %s\n", name);
	return i;
    }

    if ((p = strchr (name, '\n')))
	*p = '\0';

    tmp.name = name;
    bp = (struct bmappair *)bsearch 
	(&tmp, xbm, nroffiles, sizeof(struct bmappair), (int (*)())compar);

    return bp ? bp->number : error;
}

void free_all_images()
{
    int i;

    for (i=0; i<nroffiles; i++)
	free(xbm[i].name);
    free(xbm);
    free(new_faces);
}
