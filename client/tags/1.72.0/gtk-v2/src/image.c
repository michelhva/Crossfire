/*
 * Crossfire -- cooperative multi-player graphical RPG and adventure game
 *
 * Copyright (c) 1999-2013 Mark Wedel and the Crossfire Development Team
 * Copyright (c) 1992 Frank Tore Johansen
 *
 * Crossfire is free software and comes with ABSOLUTELY NO WARRANTY. You are
 * welcome to redistribute it under certain conditions. For details, see the
 * 'LICENSE' and 'COPYING' files.
 *
 * The authors can be reached via e-mail to crossfire-devel@real-time.com
 */

/**
 * @file
 * Contains highlevel image related functions and mostly deals with the image
 * caching, processing the image commands from the server, etc.  It is
 * gtk-specific as it returns gtk pixmaps.
 */

#include "client.h"

#include <gtk/gtk.h>

#ifdef HAVE_SDL
#include <SDL.h>
#include <SDL_image.h>
#endif

#include "image.h"
#include "main.h"
#include "mapdata.h"
#include "gtk2proto.h"

extern GtkWidget *window_root; /**< In main.c */
int image_size=DEFAULT_IMAGE_SIZE;

struct {
    char    *name;
    guint32  checksum;
    guint8   *png_data;
    guint32  width, height;
} private_cache[MAXPIXMAPNUM];

#define BPP 4

PixmapInfo *pixmaps[MAXPIXMAPNUM];

int last_face_num=0;

/* Do we have new images to display? */
int have_new_image=0;

/*
 * this is used to rescale big images that will be drawn in the inventory/look
 * lists.  What the code further below basically does is figure out how big the
 * object is (in squares), and this looks at the icon_rescale_factor to figure
 * what scale factor it gives.  Not that the icon_rescale_factor values are
 * passed directly to the rescale routines.  These represent percentages - so
 * even taking into account that the values diminish as the table grows, they
 * will still appear larger if the location in the table times the factor is
 * greater than 100.  We find the largest dimension that the image has.  The
 * values in the comment is the effective scaling compared to the base image
 * size that this big image will appear as.  Using a table makes it easier to
 * adjust the values so things look right.
 */

#define MAX_ICON_SPACES     10
static const int icon_rescale_factor[MAX_ICON_SPACES] = {
    100, 100,           80 /* 2 = 160 */,   60 /* 3 = 180 */,
    50 /* 4 = 200 */,   45 /* 5 = 225 */,   40 /* 6 = 240 */,
    35 /* 7 = 259 */,   35 /* 8 = 280 */,   33 /* 9 = 300 */
};

/******************************************************************************
 *
 * Code related to face caching.
 *
 *****************************************************************************/

/* Does not appear to be used anywhere
typedef struct Keys {
    uint8       flags;
    sint8       direction;
    KeySym      keysym;
    char        *command;
    struct Keys *next;
} Key_Entry;
*/

/* Rotate right from bsd sum. */
#define ROTATE_RIGHT(c) if ((c) & 01) (c) = ((c) >>1) + 0x80000000; else (c) >>= 1;

/*#define CHECKSUM_DEBUG*/

/**
 * Helper function to make the code more readable
 */
static void create_icon_image(guint8 *data, PixmapInfo *pi) {
    pi->icon_mask = NULL;
    pi->icon_image = rgba_to_gdkpixbuf(data, pi->icon_width, pi->icon_height);
}

/**
 * Helper function to make the code more readable
 *
 * @param data
 * @param pi
 */
static void create_map_image(guint8 *data, PixmapInfo *pi) {
    pi->map_image = NULL;
    pi->map_mask = NULL;

    if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_SDL) {
#if defined(HAVE_SDL)
        int i;
        SDL_Surface *fog;
        guint32 g,*p;
        guint8 *l;

#if SDL_BYTEORDER == SDL_LIL_ENDIAN
        pi->map_image = SDL_CreateRGBSurfaceFrom(data, pi->map_width,
                        pi->map_height, 32, pi->map_width * 4,  0xff,
                        0xff00, 0xff0000, 0xff000000);

        fog = SDL_CreateRGBSurface(SDL_SRCALPHA | SDL_HWSURFACE,
                                   pi->map_width,  pi->map_height, 32, 0xff,
                                   0xff00, 0xff0000, 0xff000000);
        SDL_LockSurface(fog);

        for (i=0; i < pi->map_width * pi->map_height; i++) {
            l = (guint8 *) (data + i*4);
#if 1
            g = MAX(*l, *(l+1));
            g = MAX(g, *(l+2));
#else
            g = ( *l +  *(l+1) + *(l+2)) / 3;
#endif
            p = (guint32*) fog->pixels + i;
            *p = g | (g << 8) | (g << 16) | (*(l + 3) << 24);
        }

        SDL_UnlockSurface(fog);
        pi->fog_image = fog;
#else
        /* Big endian */
        pi->map_image = SDL_CreateRGBSurfaceFrom(data, pi->map_width,
                        pi->map_height, 32, pi->map_width * 4,  0xff000000,
                        0xff0000, 0xff00, 0xff);

        fog = SDL_CreateRGBSurface(SDL_SRCALPHA | SDL_HWSURFACE,
                                   pi->map_width,  pi->map_height, 32, 0xff000000,
                                   0xff0000, 0xff00, 0xff);
        SDL_LockSurface(fog);

        /*
         * I think this works out, but haven't tried it on a big endian machine
         * as my recollection is that the png data would be in the same order,
         * just the bytes for it to go on the screen are reversed.
         */
        for (i=0; i < pi->map_width * pi->map_height; i++) {
            l = (guint8 *) (data + i*4);
#if 1
            g = MAX(*l, *(l+1));
            g = MAX(g, *(l+2));
#else
            g = ( *l +  *(l+1) + *(l+2)) / 3;
#endif
            p = (guint32*) fog->pixels + i;
            *p = (g << 8) | (g << 16) | (g << 24) | *(l + 3);
        }

        for (i=0; i < pi->map_width * pi->map_height; i+= 4) {
            guint32 *tmp;

            /*
             * The pointer arithemtic below looks suspicious, but it is a patch
             * that is submitted, so just putting it in as submitted.  MSW
             * 2004-05-11
             */
            p = (guint32*) (fog->pixels + i);
            g = ( ((*p >> 24) & 0xff)  + ((*p >> 16) & 0xff) + ((*p >> 8) & 0xff)) / 3;
            tmp = (guint32*) fog->pixels + i;
            *tmp = (g << 24) | (g << 16) | (g << 8) | (*p & 0xff);
        }

        SDL_UnlockSurface(fog);
        pi->fog_image = fog;
#endif

#endif
    } else if (use_config[CONFIG_DISPLAYMODE] == CFG_DM_OPENGL) {
#ifdef HAVE_OPENGL
        create_opengl_map_image(data, pi);
#endif
    } else if (use_config[CONFIG_DISPLAYMODE] == CFG_DM_PIXMAP) {
        pi->map_image = rgba_to_cairo_surface(data, pi->map_width, pi->map_height);
    }
}

/**
 * Memory management.
 *
 * @param pi
 */
static void free_pixmap(PixmapInfo *pi)
{
    if (pi->icon_image) {
        g_object_unref(pi->icon_image);
    }
    if (pi->icon_mask) {
        g_object_unref(pi->icon_mask);
    }
    if (pi->map_mask) {
        g_object_unref(pi->map_mask);
    }
    if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_SDL) {
#ifdef HAVE_SDL
        if (pi->map_image) {
            SDL_FreeSurface(pi->map_image);
            free(((SDL_Surface*)pi->map_image)->pixels);
            SDL_FreeSurface(pi->fog_image);
            /*
             * Minor memory leak here - SDL_FreeSurface() frees the pixel
             * data _unless_ SDL_CreateRGBSurfaceFrom() was used to create
             * the surface.  SDL_CreateRGBSurfaceFrom() is used to create
             * the map data, which is why we need the free there.  The
             * reason this is a minor memory leak is because
             * SDL_CreateRGBSurfaceFrom() is used to create the question
             * mark image, and without this free, that data is not freed.
             * However, with this, client crashes after disconnecting from
             * server with double free.
             */
            /*          free(((SDL_Surface*)pi->fog_image)->pixels);*/
        }
#endif
    } else if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_OPENGL) {
#ifdef HAVE_OPENGL
        opengl_free_pixmap(pi);
#endif
    } else if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_PIXMAP) {
        if (pi->map_image) {
            cairo_surface_destroy(pi->map_image);
        }
    }
}

/**
 * Takes the pixmap to put the data into, as well as the rgba data (ie, already
 * loaded with png_to_data).  Scales and stores the relevant data into the
 * pixmap structure.
 *
 * @param ce can be NULL
 * @param pixmap_num
 * @param rgba_data
 * @param width
 * @param height
 *
 * @return 1 on failure.
 */
int create_and_rescale_image_from_data(Cache_Entry *ce, int pixmap_num,
        guint8 *rgba_data, int width, int height) {
    int nx, ny, iscale, factor;
    PixmapInfo  *pi;

    if (pixmap_num <= 0 || pixmap_num >= MAXPIXMAPNUM) {
        return 1;
    }

    if (pixmaps[pixmap_num] != pixmaps[0]) {
        /* As per bug 2938906, one can see image corruption when switching between
         * servers.  The cause is that the cache table stores away
         * a pointer to the pixmap[] entry - if we go and free it,
         * the cache table can point to garbage, so don't free it.
         * This causes some memory leak, but if/when there is good
         * cache support for multiple servers, eventually the amount
         * of memory consumed will reach a limit (it has every image of
         * every server in memory
         *
         * The cause of image corruption requires a few different things:
         * 1) images of the same name have different numbers on the 2 serves.
         * 2) the image number is higher on the first than second server
         * 3) the image using the high number does not exist/is different
         *    on the second server, causing this routine to be called.
         */

        if (!use_config[CONFIG_CACHE]) {
            free_pixmap(pixmaps[pixmap_num]);
            free(pixmaps[pixmap_num]);
        }
        pixmaps[pixmap_num] = pixmaps[0];
    }

    pi = calloc(1, sizeof(PixmapInfo));

    iscale = use_config[CONFIG_ICONSCALE];

    /*
     * If the image is big, figure out what we should scale it to so it fits
     * better display
     */
    if (width > DEFAULT_IMAGE_SIZE || height>DEFAULT_IMAGE_SIZE) {
        int ts = 100;

        factor = width / DEFAULT_IMAGE_SIZE;
        if (factor >= MAX_ICON_SPACES) {
            factor = MAX_ICON_SPACES - 1;
        }
        if (icon_rescale_factor[factor] < ts) {
            ts = icon_rescale_factor[factor];
        }

        factor = height / DEFAULT_IMAGE_SIZE;
        if (factor >= MAX_ICON_SPACES) {
            factor = MAX_ICON_SPACES - 1;
        }
        if (icon_rescale_factor[factor] < ts) {
            ts = icon_rescale_factor[factor];
        }

        iscale = ts * use_config[CONFIG_ICONSCALE] / 100;
    }

    /* In all cases, the icon images are in native form. */
    if (iscale != 100) {
        nx=width;
        ny=height;
        guint8 *png_tmp = rescale_rgba_data(rgba_data, &nx, &ny, iscale);
        pi->icon_width = nx;
        pi->icon_height = ny;
        create_icon_image(png_tmp, pi);
        free(png_tmp);
    } else {
        pi->icon_width = width;
        pi->icon_height = height;
        create_icon_image(rgba_data, pi);
    }

    /*
     * If icon_scale matched use_config[CONFIG_MAPSCALE], we could try to be
     * more intelligent, but this should not be called too often, and this
     * keeps the code simpler.
     */
    guint8 *png_tmp;

    if (use_config[CONFIG_MAPSCALE] != 100) {
        nx=width;
        ny=height;
        png_tmp = rescale_rgba_data(rgba_data, &nx, &ny, use_config[CONFIG_MAPSCALE]);
        pi->map_width = nx;
        pi->map_height = ny;
        create_map_image(png_tmp, pi);
        /*
         * pixmap mode and opengl don't need the rgba data after they have
         * created the image, so we can free it.  SDL uses the raw rgba data,
         * so it can't be freed.
         */
        if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_PIXMAP ||
                use_config[CONFIG_DISPLAYMODE]==CFG_DM_OPENGL) {
            free(png_tmp);
        }
    } else {
        pi->map_width = width;
        pi->map_height = height;
        /*
         * If using SDL mode, a copy of the rgba data needs to be stored away.
         */
        if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_SDL) {
            png_tmp = g_malloc(width * height * BPP);
            memcpy(png_tmp, rgba_data, width * height * BPP);
        } else {
            png_tmp = rgba_data;
        }
        create_map_image(png_tmp, pi);
    }
    /*
     * Not ideal, but if it is missing the map or icon image, presume something
     * failed.  However, opengl doesn't set the map_image, so if using that
     * display mode, don't make this check.
     */
    if (!pi->icon_image || (!pi->map_image && use_config[CONFIG_DISPLAYMODE]!=CFG_DM_OPENGL)) {
        free_pixmap(pi);
        free(pi);
        return 1;
    }
    if (ce) {
        ce->image_data = pi;
    }
    pixmaps[pixmap_num] = pi;
    if (use_config[CONFIG_CACHE]) {
        have_new_image++;
    }

    return 0;
}

/**
 * Referenced from common/commands.c
 *
 * @param face
 * @param smooth_face
 */
void addsmooth(guint16 face, guint16 smooth_face)
{
    pixmaps[face]->smooth_face = smooth_face;
}

/**
 * This functions associates image_data in the cache entry with the specific
 * pixmap number.  Currently, there is no failure condition, but there is the
 * potential that in the future, we want to more closely look at the data and
 * if it isn't valid, return the failure code.
 *
 * @return 0 on success, -1 on failure.
 */
int associate_cache_entry(Cache_Entry *ce, int pixnum)
{
    pixmaps[pixnum] = ce->image_data;
    return 0;
}

/**
 * Connecting to different servers, try to clear out any old images.  Try to
 * free the data to prevent memory leaks.  This could be more clever, ie, if
 * we're caching images and go to a new server and get a name, we should try to
 * re-arrange our cache or the like.
 */
void reset_image_data(void)
{
    int i;

    reset_image_cache_data();
    /*
     * The entries in the pixmaps array are also tracked in the image cache in
     * the common area.  We will try to recycle those images that we can.
     * Thus, if we connect to a new server, we can just re-use the images we
     * have already rendered.
     */
    for (i=1; i<MAXPIXMAPNUM; i++) {
        if (!want_config[CONFIG_CACHE] && pixmaps[i] != pixmaps[0]) {
            free_pixmap(pixmaps[i]);
            free(pixmaps[i]);
            pixmaps[i] = pixmaps[0];
        }
    }
}

static GtkWidget *pbar, *pbar_window;

/**
 * Draws a status bar showing where we our in terms of downloading all the
 * image data. A few hacks:
 * If start is 1, this is the first batch, so it means we need to create the
 * appropriate status window.
 * If start = end = total, it means were finished, so destroy the gui element.
 *
 * @param start The start value just sent to the server.
 * @param end
 * @param total The total number of images.
 */
void image_update_download_status(int start, int end, int total) {
    int x, y, wx, wy, w, h;

    if (start == 1) {
        pbar = gtk_progress_bar_new();
        get_window_coord(window_root, &x,&y, &wx,&wy,&w,&h);

        pbar_window = gtk_window_new(GTK_WINDOW_POPUP);
        gtk_window_set_transient_for(GTK_WINDOW(pbar_window), GTK_WINDOW (window_root));

        gtk_container_add(GTK_CONTAINER(pbar_window), pbar);
        gtk_widget_show(pbar);
        gtk_widget_show(pbar_window);
    } else if (start == total) {
        gtk_widget_destroy(pbar_window);
        pbar = NULL;
        pbar_window = NULL;
        return;
    }

    gtk_progress_bar_set_fraction(GTK_PROGRESS_BAR(pbar), (float)start / end);
    while (gtk_events_pending()) {
        gtk_main_iteration();
    }
}

/**
 *
 * @param face
 * @param w
 * @param h
 */
void get_map_image_size(int face, guint8 *w, guint8 *h)
{
    /* We want to calculate the number of spaces this image
     * uses it.  By adding the image size but substracting one,
     * we cover the cases where the image size is not an even
     * increment.  EG, if the map_image_size is 32, and an image
     * is 33 wide, we want that to register as two spaces.  By
     * adding 31, that works out.
     */
    if ( face < 0 || face >= MAXPIXMAPNUM) {
        *w = 1;
        *h = 1;
    } else {
        *w = (pixmaps[face]->map_width + map_image_size - 1)/ map_image_size;
        *h = (pixmaps[face]->map_height + map_image_size - 1)/ map_image_size;
    }
}

/******************************************************************************
 *
 * Code related to face caching.
 *
 *****************************************************************************/

/**
 * Initializes the data for image caching
 * Create question mark to display in each supported rendering mode when an
 * image is not cached.  When image caching is enabled, if a needed image is
 * not yet in the cache, a question mark image is displayed instead.  The
 * image displayed is unique to the display mode.  This function creates
 * the image to use when OpenGL mode is in effect.
 *
 */
void init_image_cache_data(void)
{
    int i;
    GtkStyle *style;
#include "../../pixmaps/question.xpm"


    LOG(LOG_DEBUG, "gtk-v2::init_image_cache_data", "Init Image Cache");

    style = gtk_widget_get_style(window_root);
    pixmaps[0] = g_new(PixmapInfo, 1);
    pixmaps[0]->icon_image =
        gdk_pixbuf_new_from_xpm_data((const gchar **)question_xpm);
#ifdef HAVE_SDL
    if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_SDL) {
        /*
         * Make a semi-transparent question mark symbol to use for the cached
         * images.
         */
#include "../../pixmaps/question.sdl"
        pixmaps[0]->map_image = SDL_CreateRGBSurfaceFrom(question_sdl,
                                32, 32, 1, 4, 1, 1, 1, 1);
        SDL_SetAlpha(pixmaps[0]->map_image, SDL_SRCALPHA, 70);
        pixmaps[0]->fog_image = SDL_CreateRGBSurfaceFrom(question_sdl,
                                32, 32, 1, 4, 1, 1, 1, 1);
        SDL_SetAlpha(pixmaps[0]->fog_image, SDL_SRCALPHA, 70);
    } else
#endif
        if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_PIXMAP) {
            pixmaps[0]->map_image =  pixmaps[0]->icon_image;
            pixmaps[0]->fog_image =  pixmaps[0]->icon_image;
            pixmaps[0]->map_mask =  pixmaps[0]->icon_mask;
        }
#ifdef HAVE_OPENGL
        else if (use_config[CONFIG_DISPLAYMODE]==CFG_DM_OPENGL) {
            create_opengl_question_mark();
        }
#endif

    pixmaps[0]->icon_width = pixmaps[0]->icon_height = pixmaps[0]->map_width = pixmaps[0]->map_height = map_image_size;
    pixmaps[0]->smooth_face = 0;

    /* Don't do anything special for SDL image - rather, that drawing
     * code will check to see if there is no data
     */

    /* Initialize all the images to be of the same value. */
    for (i=1; i<MAXPIXMAPNUM; i++)  {
        pixmaps[i] = pixmaps[0];
    }

    init_common_cache_data();
}
