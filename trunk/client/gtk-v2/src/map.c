char *rcsid_gtk2_map_c =
    "$Id$";
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2005 Mark Wedel & Crossfire Development Team

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

    The author can be reached via e-mail to crossfire@metalforge.org
*/

/* This file handles the map related code - both in terms of allocation,
 * insertion of new objects, and actual rendering (although the 
 * sdl rendering is in the sdl file
 */

#include "config.h"
#include <stdlib.h>
#include <sys/stat.h>
#ifndef WIN32
#include <unistd.h>
#endif
#include <png.h>

/* Pick up the gtk headers we need */
#include <gtk/gtk.h>
#ifndef WIN32
#include <gdk/gdkx.h>
#else
#include <time.h>
#include <gdk/gdkwin32.h>
#endif
#include <gdk/gdkkeysyms.h>

#include "client-types.h"
#include "image.h"
#include "main.h"
#include "client.h"
#include "support.h"

/* Start of map handling code.
 * For the most part, this actually is not window system specific,
 * but certainly how the client wants to store this may vary.
 */

struct Map the_map;
uint8	map_did_scroll=0, map_updated=0;

/*
 * Added for fog of war. Current size of the map structure in memory.
 * We assume a rectangular map so this is the length of one side.
 * command.c needs to know about this so not static 
 * FIX ME: Don't assume rectangle
 */

PlayerPosition pl_pos;

GtkWidget *map_drawing_area;
GdkGC *mapgc;
int map_image_size=DEFAULT_IMAGE_SIZE;
int map_image_half_size=DEFAULT_IMAGE_SIZE/2;
static GdkBitmap *dark1, *dark2, *dark3;
static GdkPixmap *dark;

/* this should really be one of the CONFIG values, or perhaps a checkbox
 * someplace that displays frame rate.
 */
int time_map_redraw=0;

/* This initializes the stuff we need for the map. */
void map_init(GtkWidget *window_root)
{
    map_drawing_area = lookup_widget(window_root,"drawingarea_map");
    mapgc = gdk_gc_new(map_drawing_area->window);
    gtk_widget_show(map_drawing_area);
    gtk_widget_add_events (map_drawing_area, GDK_BUTTON_PRESS_MASK);

    if (!use_config[CONFIG_SDL]) {
	int x,y,count;
	GdkGC	*darkgc;

	/* this is used when drawing with GdkPixmaps.  Create another surface,
         * as well as some light/dark images
         */
	dark = gdk_pixmap_new(map_drawing_area->window, map_image_size, map_image_size, -1);
	gdk_draw_rectangle(dark, map_drawing_area->style->black_gc, TRUE, 0, 0, map_image_size, map_image_size);
	dark1 = gdk_pixmap_new(map_drawing_area->window, map_image_size, map_image_size, 1);
	dark2 = gdk_pixmap_new(map_drawing_area->window, map_image_size, map_image_size, 1);
	dark3 = gdk_pixmap_new(map_drawing_area->window, map_image_size, map_image_size, 1);

	/* We need our own GC here because we are working with single bit depth images */
	darkgc = gdk_gc_new(dark1);
	gdk_gc_set_foreground(darkgc, &root_color[NDI_WHITE]);
	/* Clear any garbage values we get when we create the bitmaps */
	gdk_draw_rectangle(dark1, darkgc, TRUE, 0, 0, map_image_size, map_image_size);
	gdk_draw_rectangle(dark2, darkgc, TRUE, 0, 0, map_image_size, map_image_size);
	gdk_draw_rectangle(dark3, darkgc, TRUE, 0, 0, map_image_size, map_image_size);
	gdk_gc_set_foreground(darkgc, &root_color[NDI_BLACK]);
	count=0;
	for (x=0; x<map_image_size; x++) {
	    for (y=0; y<map_image_size; y++) {

		/* we just fill in points every X pixels - dark1 is the darkest, dark3 is the lightest.
		 * dark1 has 50% of the pixels filled in, dark2 has 33%, dark3 has 25%
		 * The formula's here are not perfect - dark2 will not match perfectly with an
		 * adjacent dark2 image.  dark3 results in diagonal stripes.  OTOH, these will
		 * change depending on the image size.
		 */
		if ((x+y) % 2) {
		    gdk_draw_point(dark1, darkgc, x, y);
		}
		if ((x+y) %3) {
		    gdk_draw_point(dark2, darkgc, x, y);
		}
		if ((x+y) % 4) {
		    gdk_draw_point(dark3, darkgc, x, y);
		}
		/* dark1 gets filled on 0x01, 0x11, 0x10, only leaving 0x00 empty */
	    }
	    /* if the row size is even, we put an extra value in count - in this
	     * way, the pixels will be even on one line, odd on the next, etc
	     * instead of vertical lines - at least for dark1 and dark3
	     */
	}
	gdk_gc_unref(darkgc);
    }
#ifdef HAVE_SDL
    else {
	init_SDL(map_drawing_area,0);
    }
#endif
}

/*
 * Takes three args, first is a return value that is a pointer
 * we should put map info into. Next two are map dimensions.
 * This function supports non rectangular maps but the client
 * pretty much doesn't. The caller is responsible for freeing
 * the data. I have the caller pass in a map struct instead of
 * returning a pointer because I didn't want to change all the
 * the_map.cells to the_map->cells...
 * The returned map memory is zero'ed.
 */
void allocate_map( struct Map* new_map, int ax, int ay)
{
    int i= 0;

    if( new_map == NULL)
	return;

    if( ax < 1 || ay < 1) {
	new_map->cells= NULL;
	return;
    }

    new_map->cells= (struct MapCell**)calloc( sizeof( struct MapCell*) * ay
		    + sizeof( struct MapCell) * ax * ay, 1);

    if( new_map->cells == NULL)
	return;

    /* Skip past the first row of pointers to rows and assign the start of
     * the actual map data
     */
    new_map->cells[0]= (struct MapCell*)((char*)new_map->cells + 
				       (sizeof( struct MapCell*) * ay));

    /* Finish assigning the beginning of each row relative to the first row
     * assigned above
     */
    for( i= 0; i < ay; i++)  {
	new_map->cells[i]= new_map->cells[0] + ( i * ax);
    }
    new_map->x= ax;
    new_map->y= ay;

    return;
}

/*
 * Clears out all the cells in the current view (which is 
 * the whole map if not using fog_of_war, and request
 * a map update from the server 
 */
void reset_map()
{
    int x= 0;
    int y= 0;

    pl_pos.x= the_map.x/2;
    pl_pos.y= the_map.y/2;
    memset( the_map.cells[0], 0, 
	   sizeof( struct MapCell) * the_map.x * the_map.y);
    for( x= pl_pos.x; x < (pl_pos.x + use_config[CONFIG_MAPWIDTH]); x++) 
    {
	for( y= pl_pos.y; y < (pl_pos.y + use_config[CONFIG_MAPHEIGHT]); y++)
	{
	    the_map.cells[x][y].need_update= 1;
	}
    }
    cs_print_string(csocket.fd, "mapredraw");
    return;
}

void print_darkness()/*too complicate to pass thru log*/
{

    int x= 0;
    int y= 0;

    for( y= 0; y < use_config[CONFIG_MAPHEIGHT]; y++)
    {
	for( x= 0; x < use_config[CONFIG_MAPWIDTH]; x++)
	{
	    fprintf( stderr, "[%3d]", the_map.cells[x][y].darkness);
	}
	fprintf( stderr, "\n");
    }
}

void print_map()/*too complicate to pass thru log*/
{
    int x= 0;
    int y= 0;
    int z= 0;

    int local_mapx = pl_pos.x + use_config[CONFIG_MAPWIDTH];
    int local_mapy = pl_pos.y + use_config[CONFIG_MAPHEIGHT];

    if( use_config[CONFIG_FOGWAR] == TRUE)
    {
	printf( " Current X pos: %d -- Current Y pos: %d\n", 
		pl_pos.x, pl_pos.y);
    }

    fprintf( stderr, "-----------------------\n");
    for( y= pl_pos.y ; y < local_mapy; y++)
    {
	for( z= 0; z < MAXLAYERS; z++)
	{
	    for( x= pl_pos.x ; x < local_mapx; x++)
	    {
		fprintf( stderr, "[%4d]", the_map.cells[x][y].heads[z].face);
	    }
	    fprintf( stderr, "\n");
	}
	fprintf( stderr, "\n");
    }
    fprintf( stderr, "-----------------------\n");
    return;
}

void set_map_darkness(int x, int y, uint8 darkness)
{
    x+= pl_pos.x;
    y+= pl_pos.y;

    the_map.cells[x][y].have_darkness = 1;
    if (darkness != (255 - the_map.cells[x][y].darkness )) {
	the_map.cells[x][y].darkness = 255 - darkness;
	the_map.cells[x][y].need_update = 1;
	/* pretty ugly - since the light code with pngximage uses
	 * neighboring spaces to adjust the darkness, we now need to
	 * let the neighbors know they should update their darkness
	 * now.
	 */
	if (use_config[CONFIG_SDL] && 
	    (use_config[CONFIG_LIGHTING] == CFG_LT_PIXEL ||
	     use_config[CONFIG_LIGHTING] == CFG_LT_PIXEL_BEST)) {
	    if (x-1>0) the_map.cells[x-1][y].need_update = 1;
	    if (y-1>0) the_map.cells[x][y-1].need_update = 1;
	    if (x+1<use_config[CONFIG_MAPWIDTH]) the_map.cells[x+1][y].need_update = 1;
	    if (y+1<use_config[CONFIG_MAPHEIGHT]) the_map.cells[x][y+1].need_update = 1;
	}
    }
}


/* 
 * Returns true if virtual view is about to butt up against 
 * the side of the virtual map on the next scroll
 * Add 2 as a fudge factor - in this way, code that checks
 * within the displayable area but +/-1 of the edges will
 * still process data within bounds.
 */
static int need_recenter_map( int dx, int dy)
{
    
    if( pl_pos.x + dx + use_config[CONFIG_MAPWIDTH] +2 >= the_map.x ||
	pl_pos.y + dy + use_config[CONFIG_MAPHEIGHT] +2 >= the_map.y ||
	pl_pos.x + dx -2 <= 0                ||
	pl_pos.y + dy -2 <= 0 )
    {
	return TRUE;
    }
    
    return FALSE;
}

/*
 * Only used in fog of war code.
 * Will recenter the virtual coordinates of the player view 
 * to the center of the map and try to keep as much current
 * state in memory as possible
 * If view is already close to center it won't move it
 */
static void recenter_virtual_map_view( struct Map *map)
{
    static struct Map tmpmap;
    struct MapCell **tmpcells;
    int y_shift= 0;
    int x_shift= 0;
    int x= 0, y= 0;

    if( map == NULL)
	return;


    if( tmpmap.cells == NULL)
    {
	allocate_map( &tmpmap, map->x, map->y);
    }

    /* 
     * If mapsize changed, allocate a new map
     */
    if( tmpmap.x != map->x || tmpmap.y != map->y)
    {
	if( tmpmap.cells)
	    free( tmpmap.cells);

	allocate_map( &tmpmap, map->x, map->y);
    }


    /*
     * If we are less then 1/4 away from either edge of the virtual map
     * or the next move would push us up against the edge (for small
     * virtual maps with large views this could happen before our 0,0 view
     * coordinate is within 1/4 of the edge) we shift to the center.
     */
    if( pl_pos.x <= (map->x/4) || pl_pos.x >= (map->x*3/4) ||
	pl_pos.x + use_config[CONFIG_MAPWIDTH] + 1 >= map->x )
    {
	x_shift= map->x/2 - pl_pos.x;
    }
    if( pl_pos.y <= (map->y/4) || pl_pos.y >= (map->y*3/4) ||
	pl_pos.y + use_config[CONFIG_MAPHEIGHT] + 1 >= map->y )
    {
	y_shift= map->y/2 - pl_pos.y;
    }


    if( x_shift == 0 && y_shift == 0)
	return;

    for( x= 0; x < map->x; x++)
    {
	if( x + x_shift >= map->x || x + x_shift < 0)
	    continue;

	for( y= 0; y < map->y; y++)
	{
	    if( y + y_shift >= map->y || y + y_shift < 0)
		continue;

	    memcpy( (char*)&tmpmap.cells[x + x_shift][y + y_shift],
		    (char*)&map->cells[x][y],
		    sizeof( struct MapCell) );
	}
    }


    pl_pos.x+= x_shift;
    pl_pos.y+= y_shift;


    /*
     * Swap cell arrays then zero out the old cells to avoid another big memcopy
     */
    tmpcells= map->cells;
    map->cells= tmpmap.cells;
    tmpmap.cells= tmpcells;

    memset( (char*)&tmpmap.cells[0][0], 0,
	    sizeof( struct MapCell) * tmpmap.x * tmpmap.y);

    return;
}

  
void display_mapscroll(int dx,int dy)
{
    int x,y;
    int local_mapx= 0, local_mapy= 0;

    /* We don't need to memcopy any of this stuff around cause 
     * we are keeping it in memory. We do need to update our
     * virtual position though
     */
	
    if( need_recenter_map( dx, dy) == TRUE)
	recenter_virtual_map_view( &the_map);

    pl_pos.x+= dx;
    pl_pos.y+= dy;
    local_mapx= pl_pos.x + use_config[CONFIG_MAPWIDTH];
    local_mapy= pl_pos.y + use_config[CONFIG_MAPHEIGHT];
	
    /*
     * For cells about to enter the view, mark them as
     * needing an update. Cells that are already in 
     * view don't need to be updated since we just memcpy
     * the image data around. This is needed for proper 
     * drawing of blank or black tiles coming into view
     */
    for( x= pl_pos.x; x < pl_pos.x + use_config[CONFIG_MAPWIDTH]; x++) {
	for( y= pl_pos.y; y < pl_pos.y + use_config[CONFIG_MAPHEIGHT]; y++) {
	    if( (x + dx) < pl_pos.x || (x + dx) >= (use_config[CONFIG_MAPWIDTH] + pl_pos.x) ||
	       (y + dy) < pl_pos.y || (y + dy) >= (use_config[CONFIG_MAPHEIGHT] + pl_pos.y) )
	    {
		if( x < 0 || y < 0 || x >= the_map.x ||	y >= the_map.y)
		    continue;


		the_map.cells[x][y].need_update= 1;
		the_map.cells[x][y].cleared= 1;
        the_map.cells[x][y].keephead = 1;/*otherwise we will also mark the multipart as cleared*/
	    }
	} /* for y */
    } /* for x */

#ifdef HAVE_SDL
    if (use_config[CONFIG_SDL])
	sdl_mapscroll(dx,dy);
    else
#endif
	map_did_scroll = 1;
/*    fprintf(stderr,"scroll command: %d %d\n", dx, dy);*/
}



/*
 * Clears all map data - this is only called when we have lost our connection
 * to a server - this way bogus data won't be around when we connect
 * to the new server
 */
void reset_map_data()
{
    int x= 0;
    int y= 0;

    pl_pos.x= the_map.x/2;
    pl_pos.y= the_map.y/2;
    memset( the_map.cells[0], 0,
	   sizeof( struct MapCell) * the_map.x * the_map.y);
    for( x= pl_pos.x; x < (pl_pos.x + use_config[CONFIG_MAPWIDTH]); x++)
    {
	for( y= pl_pos.y; y < (pl_pos.y + use_config[CONFIG_MAPHEIGHT]); y++)
	{
	    the_map.cells[x][y].need_update= 1;
	}
    }
}

         
/* Draw anything in adjacent squares that could smooth on given square
 * mx,my square to smooth on. you should not call this function to
 * smooth on a 'completly black' square.
 * layer layer to examine (we smooth only one layer at a time)
 * picx,picy place on the map_drawing_area->window to draw
 */
void drawsmooth (int mx,int my,int layer,int picx,int picy){
    static int dx[8]={0,1,1,1,0,-1,-1,-1};
    static int dy[8]={-1,-1,0,1,1,1,0,-1};
    static int bweights[8]={2,0,4,0,8,0,1,0};
    static int cweights[8]={0,2,0,4,0,8,0,1};
    static int bc_exclude[8]={
                 1+2,/*north exclude northwest (bit0) and northeast(bit1)*/
                 0,
                 2+4,/*east exclude northeast and southeast*/
                 0,
                 4+8,/*and so on*/
                 0,
                 8+1,
                 0
                };
    int partdone[8]={0,0,0,0,0,0,0,0};
    int slevels[8];
    int sfaces[8];
    int i,lowest,weight,weightC;
    int emx,emy;
    int smoothface;
    int dosmooth=0;
    if ( (the_map.cells[mx][my].heads[0].face==0)
         || !CAN_SMOOTH(the_map.cells[mx][my].smooth[layer]) )
        return;
    for (i=0;i<8;i++){
        emx=mx+dx[i];
        emy=my+dy[i];
        if ( (emx<0) || (emy<0) || (the_map.x<=emx) || (the_map.y<=emy)){
            slevels[i]=0;
            sfaces[i]=0; /*black picture*/
        }
        if (the_map.cells[emx][emy].smooth[layer]<=the_map.cells[mx][my].smooth[layer]){
            slevels[i]=0;
            sfaces[i]=0; /*black picture*/
        }else{      
            slevels[i]=the_map.cells[emx][emy].smooth[layer];
            sfaces[i]=getsmooth(the_map.cells[emx][emy].heads[layer].face);
            dosmooth=1;
        }                    
    }
    /* ok, now we have a list of smoothlevel higher than current square.
     * there are at most 8 different levels. so... let's check 8 times
     * for the lowest one (we draw from botto to top!).
     */
    lowest=-1;
    while (1){
        lowest = -1;
        for (i=0;i<8;i++){
            if ( (slevels[i]>0) && (!partdone[i]) &&
                ((lowest<0) || (slevels[i]<slevels[lowest]))
               )
                    lowest=i;    
        }
        if (lowest<0)
            break;   /*no more smooth to do on this square*/
        /*printf ("hey, must smooth something...%d\n",sfaces[lowest]);*/
        /*here we know 'what' to smooth*/
        /* we need to calculate the weight
         * for border and weight for corners.
         * then we 'markdone' 
         * the corresponding squares
         */
        /*first, the border, which may exclude some corners*/
        weight=0;
        weightC=15; /*works in backward. remove where there is nothing*/
        /*for (i=0;i<8;i++)
            cornermask[i]=1;*/
        for (i=0;i<8;i++){ /*check all nearby squares*/
            if ( (slevels[i]==slevels[lowest]) &&
                 (sfaces[i]==sfaces[lowest])){
                partdone[i]=1;
                weight=weight+bweights[i];
                weightC&=~bc_exclude[i];
            }else{
                /*must rmove the weight of a corner if not in smoothing*/
                weightC&=~cweights[i];
            }
            
        }
        /*We can't do this before since we need the partdone to be adjusted*/
        if (sfaces[lowest]<=0)
            continue;  /*Can't smooth black*/
        smoothface=sfaces[lowest];
        if (smoothface<=0){
            continue;  /*picture for smoothing not yet available*/
        }
        /* now, it's quite easy. We must draw using a 32x32 part of
         * the picture smoothface. 
         * This part is located using the 2 weights calculated: 
         * (32*weight,0) and (32*weightC,32)
         */
        if ( (!pixmaps[smoothface]->map_image) ||
             (pixmaps[smoothface] == pixmaps[0]))
            continue;   /*don't have the picture associated*/
        if (weight>0){
        gdk_gc_set_clip_mask (mapgc, pixmaps[smoothface]->map_mask);
         
        gdk_gc_set_clip_origin(mapgc, picx-map_image_size*weight,picy);
        gdk_draw_pixmap(map_drawing_area->window, mapgc,pixmaps[smoothface]->map_image,
	        map_image_size*weight, 0, picx, picy,
            map_image_size, map_image_size);
        }
        if (weightC>0){
        gdk_gc_set_clip_mask (mapgc, pixmaps[smoothface]->map_mask);
        gdk_gc_set_clip_origin(mapgc, picx-map_image_size*weightC,picy-map_image_size);
        gdk_draw_pixmap(map_drawing_area->window, mapgc,pixmaps[smoothface]->map_image,
		  map_image_size*weightC, map_image_size, picx, picy,
          map_image_size, map_image_size);          
        }
		
            
    }/*while there's some smooth to do*/
}
void gtk_draw_map(int redraw) {
    int mx,my, layer,x,y, src_x, src_y;
    struct timeval tv1, tv2,tv3;
    long elapsed1, elapsed2;
    if (time_map_redraw)
	gettimeofday(&tv1, NULL);

    for( x= 0; x<use_config[CONFIG_MAPWIDTH]; x++) {
	for(y = 0; y<use_config[CONFIG_MAPHEIGHT]; y++) {
	    /* mx,my represent the spaces on the 'virtual' map (ie, the_map structure).
	     * x and y (from the for loop) represent the visable screen.
	     */
	    mx = x + pl_pos.x;
	    my = y + pl_pos.y;

	    /* Don't need to touch this space */
	    if (!redraw && !the_map.cells[mx][my].need_update && !map_did_scroll&& !the_map.cells[mx][my].need_resmooth)
		continue;

	    /* First, we need to black out this space. */
	    gdk_draw_rectangle(map_drawing_area->window, map_drawing_area->style->black_gc, TRUE, x * map_image_size, y * map_image_size, map_image_size, map_image_size);

	    /* now draw the different layers.  Only draw if using fog of war or the
	     * space isn't clear.
	     */
	    if (use_config[CONFIG_FOGWAR] || !the_map.cells[mx][my].cleared || map_did_scroll) 
		for (layer=0; layer<MAXLAYERS; layer++) {

		    /* draw the tail first - this seems to get better results */
		    if (the_map.cells[mx][my].tails[layer].face && 
			pixmaps[the_map.cells[mx][my].tails[layer].face]->map_image) {

			/* add one to the size values to take into account the actual width of the space */
			src_x = pixmaps[the_map.cells[mx][my].tails[layer].face]->map_width - 
			    (the_map.cells[mx][my].tails[layer].size_x + 1) * map_image_size;
			src_y = pixmaps[the_map.cells[mx][my].tails[layer].face]->map_height - 
			    (the_map.cells[mx][my].tails[layer].size_y + 1) * map_image_size;

			gdk_gc_set_clip_mask (mapgc, pixmaps[the_map.cells[mx][my].tails[layer].face]->map_mask);
			gdk_gc_set_clip_origin(mapgc, 
			    (x - (pixmaps[the_map.cells[mx][my].tails[layer].face]->map_width-1)/map_image_size  + the_map.cells[mx][my].tails[layer].size_x)* map_image_size, 
			    (y - (pixmaps[the_map.cells[mx][my].tails[layer].face]->map_height-1)/map_image_size + the_map.cells[mx][my].tails[layer].size_y)* map_image_size);

			gdk_draw_pixmap(map_drawing_area->window, mapgc,
				    pixmaps[the_map.cells[mx][my].tails[layer].face]->map_image,
				    src_x, src_y, x * map_image_size, y * map_image_size,
					map_image_size, map_image_size);
		    }
		    /* Draw the head now - logic is pretty much exactly the same
		     * as that for the tail, except we know that we this is at the lower right,
		     * so we don't need to adjust the origin as much.
		     */
		    if (the_map.cells[mx][my].heads[layer].face && 
			pixmaps[the_map.cells[mx][my].heads[layer].face]->map_image){

			    /* add one to the size values to take into account the actual width of the space */
			    src_x = pixmaps[the_map.cells[mx][my].heads[layer].face]->map_width - map_image_size;
			    src_y = pixmaps[the_map.cells[mx][my].heads[layer].face]->map_height - map_image_size;

            
			    gdk_gc_set_clip_mask (mapgc, pixmaps[the_map.cells[mx][my].heads[layer].face]->map_mask);
			    gdk_gc_set_clip_origin(mapgc, 
					       (x + 1 - the_map.cells[mx][my].heads[layer].size_x) * map_image_size,
					       (y + 1 - the_map.cells[mx][my].heads[layer].size_y) * map_image_size);

			    gdk_draw_pixmap(map_drawing_area->window, mapgc,
				    pixmaps[the_map.cells[mx][my].heads[layer].face]->map_image,
				    src_x, src_y, x * map_image_size, y * map_image_size,
					map_image_size, map_image_size);
			    /*We draw the pic. Now, smooth adjacent squares on it*/
			    if ( use_config[CONFIG_SMOOTH])
				drawsmooth (mx,my,layer,x * map_image_size,y * map_image_size);
			}
		    /* Sometimes, it may happens we need to draw the smooth while there
		     * is nothing to draw at that layer (but there was something at lower
		     * layers). This is handled here. The else part is to take into account
		     * cases where the smooth as already been handled 2 code lines before
		     */

		    else if ( use_config[CONFIG_SMOOTH] && the_map.cells[mx][my].need_resmooth )
			drawsmooth (mx,my,layer,x * map_image_size,y * map_image_size);
            
		} /* else for processing the layers */
	    the_map.cells[mx][my].need_resmooth=0;

	    /* Do final logic for this map space */
	    the_map.cells[mx][my].need_update=0;

	    /* If this is a fog cell, do darknening of the space.
	     * otherwise, process light/darkness - only do those if not a 
	     * fog cell.
	     */
	    if (the_map.cells[mx][my].cleared == 1) {
		gdk_gc_set_clip_mask(mapgc, dark1);
		gdk_gc_set_clip_origin(mapgc, x * map_image_size, y*map_image_size);
		gdk_draw_pixmap(map_drawing_area->window, mapgc, dark, 0, 0,
				x * map_image_size, y*map_image_size, map_image_size, map_image_size);
	    }
	    else if (the_map.cells[mx][my].darkness > 192) { /* Full dark */
		gdk_draw_rectangle (map_drawing_area->window, map_drawing_area->style->black_gc,
				    TRUE,map_image_size*x, map_image_size *y,
				    map_image_size, map_image_size);
	    } else if (the_map.cells[mx][my].darkness> 128) {
		gdk_gc_set_clip_mask(mapgc, dark1);
		gdk_gc_set_clip_origin(mapgc, x * map_image_size, y*map_image_size);
		gdk_draw_pixmap(map_drawing_area->window, mapgc, dark, 0, 0,
				x * map_image_size, y*map_image_size, map_image_size, map_image_size);
	    } else if (the_map.cells[mx][my].darkness> 64) {
		gdk_gc_set_clip_mask(mapgc, dark2);
		gdk_gc_set_clip_origin(mapgc, x * map_image_size, y*map_image_size);
		gdk_draw_pixmap(map_drawing_area->window, mapgc, dark, 0, 0,
				x * map_image_size, y*map_image_size, map_image_size, map_image_size);
	    } else if (the_map.cells[mx][my].darkness> 1) {
		gdk_gc_set_clip_mask(mapgc, dark3);
		gdk_gc_set_clip_origin(mapgc, x * map_image_size, y*map_image_size);
		gdk_draw_pixmap(map_drawing_area->window, mapgc, dark, 0, 0,
				x * map_image_size, y*map_image_size, map_image_size, map_image_size);
	    }
	} /* For y spaces */
    } /* for x spaces */

    if (time_map_redraw)
	gettimeofday(&tv2, NULL);

    map_did_scroll = 0;

    if (time_map_redraw) {
	gettimeofday(&tv3, NULL);
	elapsed1 = (tv2.tv_sec - tv1.tv_sec)*1000000 + (tv2.tv_usec - tv1.tv_usec);
	elapsed2 = (tv3.tv_sec - tv2.tv_sec)*1000000 + (tv3.tv_usec - tv2.tv_usec);

	/* I care about performance for 'long' updates, so put the check in to make
	 * these a little more noticable */
	if ((elapsed1 + elapsed2)>10000)
	    LOG(LOG_INFO,"gtk::sdl_gen_map","gen took %7ld, flip took %7ld, total = %7ld",
		    elapsed1, elapsed2, elapsed1 + elapsed2);
    }
}

/* Basically, the player has changed maps, so any info we have 
 * (for fog of war) is bogus, so clear out all that old info
 */
void display_map_newmap()
{
    reset_map();
}



/* resize_map_window is a NOOP for the time being - not sure
 * if it will in fact need to do something, since there are scrollbars
 * for the map window now.
 */
void resize_map_window(int x, int y)
{
}



/* Simple routine to put the splash icon in the map window.
 * Only supported with non SDL right now.
 */
void draw_splash()
{
    static GdkPixmap *splash;
    static int have_init=0;
    GdkBitmap *aboutgdkmask;
    int x,y, w, h;

#include "../../pixmaps/crossfiretitle.xpm"

    if (!use_config[CONFIG_SDL]) {
	if (!have_init) {
	    splash = gdk_pixmap_create_from_xpm_d(map_drawing_area->window,
                                               &aboutgdkmask, NULL,
                                               (gchar **)crossfiretitle);
	    have_init=1;
	}
	gdk_drawable_get_size(splash, &w, &h);
	x = (map_drawing_area->allocation.width- w)/2;
	y = (map_drawing_area->allocation.height - h)/2;

	gdk_draw_pixmap(map_drawing_area->window, mapgc, splash, 0, 0,
			x, y, w, h);
    }
}

	

void draw_map(int redraw)
{
#ifdef HAVE_SDL
    if (use_config[CONFIG_SDL]) sdl_gen_map(redraw);
    else
#endif
    if (cpl.input_state == Metaserver_Select) draw_splash();
    else gtk_draw_map(redraw);
}
	

gboolean
on_drawingarea_map_expose_event        (GtkWidget       *widget,
                                        GdkEventExpose  *event,
                                        gpointer         user_data)
{
    fprintf(stderr,"got expose event\n");
    draw_map(TRUE);

    return FALSE;
}

gboolean
on_drawingarea_map_button_press_event  (GtkWidget       *widget,
                                        GdkEventButton  *event,
                                        gpointer         user_data)
{

  return FALSE;
}

/* This isn't used - it is basically a prequel - we know we got a
 * map command from the server, but have digested it all yet.
 * this can be useful if there is info we know we need to store away
 * or the like before it is destroyed, but there isn't anything like
 * that for the gtk client.
 */
void display_map_startupdate()
{
}

/* This is called after the map has been all digested.
 * this should perhaps be removed, and left to
 * being done from from the main event loop.
 */
void display_map_doneupdate(int redraw)
{
    map_updated=1;
}
