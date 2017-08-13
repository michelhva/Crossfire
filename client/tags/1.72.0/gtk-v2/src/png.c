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
 * Functions for manipulating graphics in the GTK-V2 client.
 */

#include "client.h"

#include <errno.h>
#include <gtk/gtk.h>
#include <png.h>

/* Defines for PNG return values */
/* These should be in a header file, but currently our calling functions
 * routines just check for nonzero return status and don't really care
 * why the load failed.
 */
#define PNGX_NOFILE     1
#define PNGX_OUTOFMEM   2
#define PNGX_DATA       3

static guint8 *data_cp;
static int data_len, data_start;

/**
 *
 * @param png_ptr
 * @param data
 * @param length
 */
static void user_read_data(png_structp png_ptr, png_bytep data, png_size_t length)
{
    memcpy(data, data_cp + data_start, length);
    data_start += length;
}

guint8 *png_to_data(guint8 *data, int len, guint32 *width, guint32 *height) {
    guint8 *pixels = NULL;
    static png_bytepp rows = NULL;
    static guint32 rows_byte = 0;

    png_structp png_ptr;
    png_infop info_ptr;
    int bit_depth, color_type, interlace_type;

    data_len = len;
    data_cp = data;
    data_start = 0;

    png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (!png_ptr) {
        return NULL;
    }

    info_ptr = png_create_info_struct(png_ptr);
    if (!info_ptr) {
        png_destroy_read_struct(&png_ptr, NULL, NULL);
        return NULL;
    }

    if (setjmp(png_jmpbuf(png_ptr))) {
        png_destroy_read_struct(&png_ptr, &info_ptr, NULL);
        return NULL;
    }

    png_set_read_fn(png_ptr, NULL, user_read_data);
    png_read_info(png_ptr, info_ptr);

    /*
     * This seems to bug on at least one system (other than mine)
     * http://www.metalforge.net/cfmb/viewtopic.php?t=1085
     *
     * I think its actually a bug in libpng. This function dies with an
     * error based on image width. However I've produced a work around
     * using the indivial functions. Repeated below.
     *
    png_get_IHDR(png_ptr, info_ptr, (png_uint_32*)width, (png_unit_32*)height, &bit_depth,
             &color_type, &interlace_type, NULL, &filter_type);
     */
    *width = png_get_image_width(png_ptr, info_ptr);
    *height = png_get_image_height(png_ptr, info_ptr);
    bit_depth = png_get_bit_depth(png_ptr, info_ptr);
    color_type = png_get_color_type(png_ptr, info_ptr);
    interlace_type = png_get_interlace_type(png_ptr, info_ptr);

    if (color_type == PNG_COLOR_TYPE_PALETTE &&
            bit_depth <= 8) {

        /* Convert indexed images to RGB */
        png_set_expand (png_ptr);

    } else if (color_type == PNG_COLOR_TYPE_GRAY &&
               bit_depth < 8) {

        /* Convert grayscale to RGB */
        png_set_expand (png_ptr);

    } else if (png_get_valid (png_ptr,
                              info_ptr, PNG_INFO_tRNS)) {

        /* If we have transparency header, convert it to alpha
           channel */
        png_set_expand(png_ptr);

    } else if (bit_depth < 8) {

        /* If we have < 8 scale it up to 8 */
        png_set_expand(png_ptr);


        /* Conceivably, png_set_packing() is a better idea;
         * God only knows how libpng works
         */
    }
    /* If we are 16-bit, convert to 8-bit */
    if (bit_depth == 16) {
        png_set_strip_16(png_ptr);
    }

    /* If gray scale, convert to RGB */
    if (color_type == PNG_COLOR_TYPE_GRAY ||
            color_type == PNG_COLOR_TYPE_GRAY_ALPHA) {
        png_set_gray_to_rgb(png_ptr);
    }

    /* If interlaced, handle that */
    if (interlace_type != PNG_INTERLACE_NONE) {
        png_set_interlace_handling(png_ptr);
    }

    /* pad it to 4 bytes to make processing easier */
    if (!(color_type & PNG_COLOR_MASK_ALPHA)) {
        png_set_filler(png_ptr, 255, PNG_FILLER_AFTER);
    }

    /* Update the info the reflect our transformations */
    png_read_update_info(png_ptr, info_ptr);
    /* re-read due to transformations just made */
    /*
     * See above for error description
    png_get_IHDR(png_ptr, info_ptr, (png_uint_32*)width, (png_uint_32*)height, &bit_depth,
             &color_type, &interlace_type, NULL, &filter_type);
    */
    *width = png_get_image_width(png_ptr, info_ptr);
    *height = png_get_image_height(png_ptr, info_ptr);

    pixels = (guint8*)g_malloc(*width **height * 4);

    if (!pixels) {
        png_destroy_read_struct (&png_ptr, &info_ptr, NULL);
        LOG(LOG_CRITICAL,"gtk-v2::png_to_data","Out of memory - exiting");
        exit(1);
    }

    /* the png library needs the rows, but we will just return the raw data */
    if (rows_byte == 0) {
        rows = (png_bytepp)g_malloc(sizeof(png_byte *) **height);
        rows_byte = *height;
    } else if (*height > rows_byte) {
        rows = (png_bytepp)g_realloc(rows, sizeof(png_byte *) **height);

        if (rows == NULL) {
            LOG(LOG_ERROR, "png_to_data",
                    "Could not allocate memory: %s", strerror(errno));
            exit(EXIT_FAILURE);
        }

        rows_byte = *height;
    }
    if (!rows) {
        png_destroy_read_struct (&png_ptr, &info_ptr, NULL);
        free(pixels);
        return NULL;
    }

    for (guint32 y = 0; y < *height; y++) {
        rows[y] = pixels + y * *width * 4;
    }

    png_read_image(png_ptr, rows);
    png_destroy_read_struct (&png_ptr, &info_ptr, NULL);

    return pixels;
}

/* RATIO is used to know what units scale is - in this case, a percentage, so
 * it is set to 100
 */
#define RATIO   100

#define MAX_IMAGE_WIDTH         1024
#define MAX_IMAGE_HEIGHT        1024
#define BPP 4

/**
 * Takes png data and scales it accordingly.  This function is based on
 * pnmscale, but has been modified to support alpha channel - instead of
 * blending the alpha channel, it takes the most opaque value - blending it is
 * not likely to give sane results IMO - for any image that has transparent
 * information, if we blended the alpha, the result would be the edges of that
 * region being partially transparent.
 * This function has also been re-written to use more static data - in the case
 * of the client, it will be called thousands of times, so it doesn't make
 * sense to free the data and then re-allocate it.
 *
 * For pixels that are fully transparent, the end result after scaling is they
 * will be tranparent black.  This is a needed effect for blending to work
 * properly.
 *
 * This function returns a new pointer to the scaled image data.  This is
 * g_malloc'd data, so should be freed at some point to prevent leaks.  This
 * function does not modify the data passed to it - the caller is responsible
 * for freeing it if it is no longer needed.
 *
 * function arguments:
 * @param *data PNG data - this is any 4 byte per pixel data, in RGBA format.
 * @param *width Source width modified to contain the new image size.
 * @param *height Source height modified to contain the new image size.
 * @param scale Percentage size that new image should be.  100 is a same size
 *              image - values larger than 100 will result in zoom, values less
 *              than 100 will result in a shrinkage.
 */
guint8 *rescale_rgba_data(guint8 *data, int *width, int *height, int scale)
{
    static int xrow[BPP * MAX_IMAGE_WIDTH], yrow[BPP*MAX_IMAGE_HEIGHT];
    static guint8 *nrows[MAX_IMAGE_HEIGHT];

    /* Figure out new height/width */
    int new_width = *width  * scale / RATIO, new_height = *height * scale / RATIO;

    int sourcerow=0, ytoleft, ytofill, xtoleft, xtofill, dest_column=0, source_column=0, needcol,
        destrow=0;
    int x,y;
    guint8 *ndata;
    guint8 r,g,b,a;

    if (*width > MAX_IMAGE_WIDTH || new_width > MAX_IMAGE_WIDTH
            || *height > MAX_IMAGE_HEIGHT || new_height > MAX_IMAGE_HEIGHT) {
        LOG(LOG_CRITICAL,"gtk-v2::rescale_rgba_data","Image too big");
        exit(0);
    }

    /* clear old values these may have */
    memset(yrow, 0, sizeof(int) **height * BPP);

    ndata = (guint8*)g_malloc(new_width * new_height * BPP);

    for (y=0; y<new_height; y++) {
        nrows[y] = (png_bytep) (ndata + y * new_width * BPP);
    }

    ytoleft = scale;
    ytofill = RATIO;

    for (y=0,sourcerow=0; y < new_height; y++) {
        memset(xrow, 0, sizeof(int) **width * BPP);
        while (ytoleft < ytofill) {
            for (x=0; x< *width; ++x) {
                /* Only want to copy the data if this is not a transperent pixel.
                 * If it is transparent, the color information is has is probably
                 * bogus, and blending that makes the results look worse.
                 */
                if (data[(sourcerow **width + x)*BPP+3] > 0 ) {
                    yrow[x*BPP] += ytoleft * data[(sourcerow **width + x)*BPP]/RATIO;
                    yrow[x*BPP+1] += ytoleft * data[(sourcerow **width + x)*BPP+1]/RATIO;
                    yrow[x*BPP+2] += ytoleft * data[(sourcerow **width + x)*BPP+2]/RATIO;
                }
                /* Alpha is a bit special - we don't want to blend it -
                 * we want to take whatever is the more opaque value.
                 */
                if (data[(sourcerow **width + x)*BPP+3] > yrow[x*BPP+3]) {
                    yrow[x*BPP+3] = data[(sourcerow **width + x)*BPP+3];
                }
            }
            ytofill -= ytoleft;
            ytoleft = scale;
            if (sourcerow < *height) {
                sourcerow++;
            }
        }

        for (x=0; x < *width; ++x) {
            if (data[(sourcerow **width + x)*BPP+3] > 0 ) {
                xrow[x*BPP] = yrow[x*BPP] + ytofill * data[(sourcerow **width + x)*BPP] / RATIO;
                xrow[x*BPP+1] = yrow[x*BPP+1] + ytofill * data[(sourcerow **width + x)*BPP+1] / RATIO;
                xrow[x*BPP+2] = yrow[x*BPP+2] + ytofill * data[(sourcerow **width + x)*BPP+2] / RATIO;
            }
            if (data[(sourcerow **width + x)*BPP+3] > xrow[x*BPP+3]) {
                xrow[x*BPP+3] = data[(sourcerow **width + x)*BPP+3];
            }
            yrow[x*BPP]=0;
            yrow[x*BPP+1]=0;
            yrow[x*BPP+2]=0;
            yrow[x*BPP+3]=0;
        }

        ytoleft -= ytofill;
        if (ytoleft <= 0) {
            ytoleft = scale;
            if (sourcerow < *height) {
                sourcerow++;
            }
        }

        ytofill = RATIO;
        xtofill = RATIO;
        dest_column = 0;
        source_column=0;
        needcol=0;
        r=0;
        g=0;
        b=0;
        a=0;

        for (x=0; x< *width; x++) {
            xtoleft = scale;

            while (xtoleft >= xtofill) {
                if (needcol) {
                    dest_column++;
                    r=0;
                    g=0;
                    b=0;
                    a=0;
                }

                if (xrow[source_column*BPP+3] > 0) {
                    r += xtofill * xrow[source_column*BPP] / RATIO;
                    g += xtofill * xrow[1+source_column*BPP] / RATIO;
                    b += xtofill * xrow[2+source_column*BPP] / RATIO;
                }
                if (xrow[3+source_column*BPP] > a) {
                    a = xrow[3+source_column*BPP];
                }

                nrows[destrow][dest_column * BPP] = r;
                nrows[destrow][1+dest_column * BPP] = g;
                nrows[destrow][2+dest_column * BPP] = b;
                nrows[destrow][3+dest_column * BPP] = a;
                xtoleft -= xtofill;
                xtofill = RATIO;
                needcol=1;
            }

            if (xtoleft > 0 ) {
                if (needcol) {
                    dest_column++;
                    r=0;
                    g=0;
                    b=0;
                    a=0;
                    needcol=0;
                }

                if (xrow[3+source_column*BPP] > 0) {
                    r += xtoleft * xrow[source_column*BPP] / RATIO;
                    g += xtoleft * xrow[1+source_column*BPP] / RATIO;
                    b += xtoleft * xrow[2+source_column*BPP] / RATIO;
                }
                if (xrow[3+source_column*BPP] > a) {
                    a = xrow[3+source_column*BPP];
                }

                xtofill -= xtoleft;
            }
            source_column++;
        }

        if (xtofill > 0 ) {
            source_column--;
            if (xrow[3+source_column*BPP] > 0) {
                r += xtofill * xrow[source_column*BPP] / RATIO;
                g += xtofill * xrow[1+source_column*BPP] / RATIO;
                b += xtofill * xrow[2+source_column*BPP] / RATIO;
            }
            if (xrow[3+source_column*BPP] > a) {
                a = xrow[3+source_column*BPP];
            }
        }

        /* Not positve, but without the bound checking for dest_column,
         * we were overrunning the buffer.  My guess is this only really
         * showed up if when the images are being scaled - there is probably
         * something like half a pixel left over.
         */
        if (!needcol && (dest_column < new_width)) {
            nrows[destrow][dest_column * BPP] = r;
            nrows[destrow][1+dest_column * BPP] = g;
            nrows[destrow][2+dest_column * BPP] = b;
            nrows[destrow][3+dest_column * BPP] = a;
        }
        destrow++;
    }
    *width = new_width;
    *height = new_height;
    return ndata;
}

guchar rgb[512*512*3];  /**< Make this especially big to support larger images
                         *   in the future */

/**
 * Create a GdkPixbuf for the given RGBA data.
 */
GdkPixbuf *rgba_to_gdkpixbuf(guint8 *data, int width, int height) {
    /* Our data doesn't have correct stride values, so we can't just create it
     * from raw data using gdk_pixbuf_new_from_data(). */

    GdkPixbuf *pix;
    pix = gdk_pixbuf_new(GDK_COLORSPACE_RGB, TRUE, 8, width, height);

    int rowstride = gdk_pixbuf_get_rowstride(pix);
    unsigned char *pixels = gdk_pixbuf_get_pixels(pix);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            unsigned char *p = pixels + y * rowstride + x * 4;
            p[0] = data[4*(x + y * width)];
            p[1] = data[4*(x + y * width) + 1 ];
            p[2] = data[4*(x + y * width) + 2 ];
            p[3] = data[4*(x + y * width) + 3 ];
        }
    }

    return pix;
}

/**
 * Create a Cairo surface for the given RGBA data.
 */
cairo_surface_t *rgba_to_cairo_surface(guint8 *data, int width, int height) {
    cairo_surface_t *surface;
    surface = cairo_image_surface_create(CAIRO_FORMAT_ARGB32, width, height);
    cairo_surface_flush(surface);

    unsigned char *pixels = cairo_image_surface_get_data(surface);
    int stride = cairo_image_surface_get_stride(surface);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            guint32 *p = (guint32 *)(pixels + y * stride + x * 4);

            // Cairo format is native-endian ARGB, but our format is RGBA.
            guint32 a = data[4 * (x + y * width) + 3];
            guint32 r = data[4 * (x + y * width) + 0] * a / 255;
            guint32 g = data[4 * (x + y * width) + 1] * a / 255;
            guint32 b = data[4 * (x + y * width) + 2] * a / 255;

            *p = a << (3 * 8) | r << (2 * 8) | g << (1 * 8) | b << (0 * 8);
        }
    }

    cairo_surface_mark_dirty(surface);
    return surface;
}
