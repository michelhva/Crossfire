/*
 * static char *rcsid_image_c =
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

/*
 * This file contains image related functions - this is a higher level up -
 * it mostly deals with the caching of the images, processing the image commands
 * from the server, etc.  This file is gtk specific - at least it returns
 * gtk pixmaps.
 */

#include "config.h"
#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>
#include <png.h>

/* Pick up the gtk headers we need */
#include <gtk/gtk.h>
#include <gdk/gdkx.h>
#include <gdk/gdkkeysyms.h>

#ifdef HAVE_SDL
#include <SDL.h>
#include <SDL_image.h>
#endif

#include "client-types.h"
#include "gx11.h"
#include "client.h"

#include "gtkproto.h"

/* size for icons and map images, represented as a percentage */
int icon_scale=100, map_scale=100;

struct {
    char    *name;
    uint32  checksum;
    uint8   *png_data;
    uint32  width, height;
} private_cache[MAXPIXMAPNUM];

/* This holds the name we recieve with the 'face' command so we know what
 * to save it as when we actually get the face.
 */
char *facetoname[MAXPIXMAPNUM];


int last_face_num=0;


/******************************************************************************
 *
 * Code related to face caching.
 *
 *****************************************************************************/

char facecachedir[MAX_BUF];

typedef struct Keys {
    uint8	flags;
    sint8	direction;
    KeySym	keysym;
    char	*command;
    struct Keys	*next;
} Key_Entry;


/* Rotate right from bsd sum. */
#define ROTATE_RIGHT(c) if ((c) & 01) (c) = ((c) >>1) + 0x80000000; else (c) >>= 1;

/*#define CHECKSUM_DEBUG*/

/* These little helper functions just make the code below much more readable */
static void create_icon_image(uint8 *data, PixmapInfo *pi, int pixmap_num)
{
    if (rgba_to_gdkpixmap(gtkwin_root->window, data, pi->icon_width, pi->icon_height,
		(GdkPixmap**)&pi->icon_image, (GdkBitmap**)&pi->icon_mask,
		gtk_widget_get_colormap(gtkwin_root)))
		    fprintf(stderr,"Unable to create scaled image, dest num = %d\n", pixmap_num);
}

/* These little helper functions just make the code below much more readable */
static void create_map_image(uint8 *data, PixmapInfo *pi)
{
    pi->map_image = NULL;
    pi->map_mask = NULL;

    if (sdlimage) {
#if defined(HAVE_SDL)
    #if SDL_BYTEORDER == SDL_LIL_ENDIAN
	pi->map_image = SDL_CreateRGBSurfaceFrom(data, pi->map_width,
	        pi->map_height, 32, pi->map_width * 4,  0xff,
			0xff00, 0xff0000, 0xff000000);
    #else
	/* Big endian */
	pi->map_image = SDL_CreateRGBSurfaceFrom(data, pi->map_width,
	        pi->map_height, 32, pi->map_width * 4,  0xff000000,
			0xff0000, 0xff00, 0xff);

    #endif

#endif
    }
    else {
	rgba_to_gdkpixmap(gtkwin_root->window, data, pi->map_width, pi->map_height,
		(GdkPixmap**)&pi->map_image, (GdkBitmap**)&pi->map_mask,
		gtk_widget_get_colormap(gtkwin_root));
    }
}

static void free_pixmap(PixmapInfo *pi)
{
    if (pi->icon_image) gdk_pixmap_unref(pi->icon_image);
    if (pi->icon_mask) gdk_pixmap_unref(pi->icon_mask);
    if (pi->map_mask) gdk_pixmap_unref(pi->map_mask);
    if (pi->map_image) {
#ifdef HAVE_SDL
	if (sdlimage) {
	    SDL_FreeSurface(pi->map_image);
	    free(((SDL_Surface*)pi->map_image)->pixels);
	}
	else
#endif
	{
	    gdk_pixmap_unref(pi->map_image);
	}
    }
}

/* Takes the pixmap to put the data into, as well as the rgba
 * data (ie, already loaded with png_to_data).  Scales and
 * stores the relevant data into the pixmap structure.
 * returns 1 on failure.
 * ce can be NULL
 */

int create_and_rescale_image_from_data(Cache_Entry *ce, int pixmap_num, uint8 *rgba_data, int width, int height)
{
    int nx, ny;
    uint8 *png_tmp;
    PixmapInfo	*pi;

    pi = malloc(sizeof(PixmapInfo));

    /* In all cases, the icon images are in native form. */
    if (icon_scale != 100) {
	nx=width;
	ny=height;
	png_tmp = rescale_rgba_data(rgba_data, &nx, &ny, icon_scale);
	pi->icon_width = nx;
	pi->icon_height = ny;
	create_icon_image(png_tmp, pi, pixmap_num);
	free(png_tmp);
    }
    else {
	pi->icon_width = width;
	pi->icon_height = height;
	create_icon_image(rgba_data, pi, pixmap_num);
    }

    /* We could try to be more intelligent if icon_scale matched map_scale,
     * but this shouldn't be called too often, and this keeps the code
     * simpler.
     */
    if (map_scale != 100) {
	nx=width;
	ny=height;
	png_tmp = rescale_rgba_data(rgba_data, &nx, &ny, map_scale);
	pi->map_width = nx;
	pi->map_height = ny;
	create_map_image(png_tmp, pi);
	if (!sdlimage) free(png_tmp);
    } else {
	pi->map_width = width;
	pi->map_height = height;
	create_map_image(rgba_data, pi);
    }
    /* Not ideal, but basically, if it is missing the map or icon image, presume
     * something failed.
     */
    if (!pi->icon_image || !pi->map_image) {
	free_pixmap(pi);
	free(pi);
	return 1;
    }
    if (ce) {
	ce->image_data = pi;
	redraw_needed=1;
    }
    pixmaps[pixmap_num] = pi;
    return 0;
}

/* This functions associates the image_data in the cache entry
 * with the specific pixmap number.  Returns 0 on success, -1
 * on failure.  Currently, there is no failure condition, but
 * there is the potential that in the future, we want to more
 * closely look at the data and if it isn't valid, return
 * the failure code.
 */
int associate_cache_entry(Cache_Entry *ce, int pixnum)
{

    pixmaps[pixnum] = ce->image_data;
    return 0;
}

/* We can now connect to different servers, so we need to clear out
 * any old images.  We try to free the data also to prevent memory
 * leaks.
 * This could be more clever, ie, if we're caching images and go to
 * a new server and get a name, we should try to re-arrange our cache
 * or the like.
 */

void reset_image_data()
{
    int i;
    reset_image_cache_data();

    /* The entries in the pixmaps array are also tracked in the image cache in
     * the common area.  We will try to recyle those images that we can - thus, if
     * we connect to a new server, we can just re-use the images we have already
     * rendered.
     */
    for (i=1; i<MAXPIXMAPNUM; i++) {
	if (!face_info.cache_images && pixmaps[i] != pixmaps[0]) {
	    free_pixmap(pixmaps[i]);
	    free(pixmaps[i]);
	}
	pixmaps[i] = pixmaps[0];
    }
    memset( the_map.cells[0], 0, sizeof( sizeof( struct MapCell)*
					 the_map.x * the_map.y ));
}


/* This function draws a little status bar showing where we our
 * in terms of downloading all the image data.
 * start is the start value just sent to the server, end is the end
 * value.  total is the total number of images.
 * A few hacks:
 * If start is 1, this is the first batch, so it means we need to
 * create the appropriate status window.
 * if start = end = total, it means were finished, so destroy
 * the gui element.
 */
static GtkWidget	*pbar=NULL, *pbar_window=NULL;
static GtkAdjustment *padj=NULL;
void image_update_download_status(int start, int end, int total)
{
    int x, y, wx, wy, w, h;

    if (start == 1) {
	padj = (GtkAdjustment*) gtk_adjustment_new (0, 1, total, 0, 0, 0);

	pbar = gtk_progress_bar_new_with_adjustment(padj);
	gtk_progress_set_format_string(GTK_PROGRESS(pbar), "Downloading image %v of %u (%p%% complete)");
	gtk_progress_bar_set_bar_style(GTK_PROGRESS_BAR(pbar), GTK_PROGRESS_CONTINUOUS);
	gtk_progress_set_show_text(GTK_PROGRESS(pbar), TRUE);
	get_window_coord(gtkwin_root, &x,&y, &wx,&wy,&w,&h);

	pbar_window = gtk_window_new(GTK_WINDOW_POPUP);
	gtk_window_set_policy(GTK_WINDOW(pbar_window), TRUE, TRUE, FALSE);
	gtk_window_set_transient_for(GTK_WINDOW(pbar_window), GTK_WINDOW (gtkwin_root));
	/* we more or less want this window centered on the main crossfire window,
	 * and not necessarily centered on the screen or in the upper left corner.
	 */
	gtk_widget_set_uposition(pbar_window, (wx + w)/2, (wy + h) / 2);

	gtk_container_add(GTK_CONTAINER(pbar_window), pbar);
	gtk_widget_show(pbar);
	gtk_widget_show(pbar_window);
    }
    if (start == total) {
	gtk_widget_destroy(pbar_window);
	pbar = NULL;
	pbar_window = NULL;
	padj = NULL;
	return;
    }

    gtk_progress_set_value(GTK_PROGRESS(pbar), start);
    while ( gtk_events_pending() ) {
        gtk_main_iteration();
    }

}
