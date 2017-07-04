/*
 * Crossfire -- cooperative multi-player graphical RPG and adventure game
 *
 * Copyright (c) 1999-2013 Mark Wedel and the Crossfire Development Team
 * Copyright (c) 1992 Frank Tore Johansen
 *
 * Crossfire is free software and comes with ABSOLUTELY NO WARRANTY. You are
 * welcome to redistribute it under certain conditions. For details, please
 * see COPYING and LICENSE.
 *
 * The authors can be reached via e-mail at <crossfire@metalforge.org>.
 */

/**
 * @file
 * Contains image related functions at a high level.  It mostly deals with the
 * caching of the images, processing the image commands from the server, etc.
 */

#include "client.h"

#include <ctype.h>
#include <glib/gstdio.h>

#ifdef WIN32
#include <io.h>
#include <direct.h>
#endif

#include "external.h"

/* Rotate right from bsd sum. */
#define ROTATE_RIGHT(c) if ((c) & 01) (c) = ((c) >>1) + 0x80000000; else (c) >>= 1;

/*#define CHECKSUM_DEBUG*/

struct FD_Cache {
    char    name[MAX_BUF];
    int	    fd;
} fd_cache[MAX_FACE_SETS];

/**
 * Given a filename, this tries to load the data.  It returns 0 success, -1 on
 * failure.  It returns the data and len, the passed options.  This function
 * is called only if the client caching feature is enabled.
 *
 * @param filename File name of an image to try to load.
 * @param data Caller-allocated pointer to a buffer to load image into.
 * @param len Amount of buffer used by the loaded image.
 * @param csum Returns 0/unset (caller already knows if checksum matches?).
 *             Changes have made such that the caller knows whether or not
 *             the checksum matches, so there is little point to re-do it.
 * @return 0 on success, -1 on failure.
 */
static int load_image(char *filename, guint8 *data, int *len, guint32 *csum) {
    int fd, i;
    char *cp;

    /* If the name includes an @, then that is a combined image file, so we
     * need to load the image a bit specially. By using these combined image
     * files, it reduces number of opens needed. In fact, we keep track of
     * which ones we have opened to improve performance. Note that while not
     * currently done, this combined image scheme could be done when storing
     * images in the player's image cache. */
    if ((cp = strchr(filename, '@')) != NULL) {
        char *lp;
        int offset, last = -1;

#ifdef WIN32
        int length;
#endif

        offset = atoi(cp + 1);
        lp = strchr(cp, ':');
        if (!lp) {
            LOG(LOG_ERROR, "common::load_image",
                "Corrupt filename - has '@' but no ':' ?(%s)", filename);
            return -1;
        }
#ifdef WIN32
        length = atoi(lp + 1);
#endif
        *cp = 0;
        for (i = 0; i < MAX_FACE_SETS; i++) {
            if (!strcmp(fd_cache[i].name, filename)) {
                break;
            }
            if (last == -1 && fd_cache[i].fd == -1) {
                last = i;
            }
        }
        /* Didn't find a matching entry yet, so make one */
        if (i == MAX_FACE_SETS) {
            if (last == -1) {
                LOG(LOG_WARNING, "common::load_image",
                    "fd_cache filled up?  unable to load matching cache entry");
                *cp = '@';	/* put @ back in string */
                return -1;
            }
#ifdef WIN32
            if ((fd_cache[last].fd = open(filename, O_RDONLY | O_BINARY)) == -1)
#else
            if ((fd_cache[last].fd = open(filename, O_RDONLY)) == -1)
#endif
            {
                LOG(LOG_WARNING, "common::load_image", "unable to load listed cache file %s",
                    filename);
                *cp = '@';	/* put @ back in string */
                return -1;
            }
            strcpy(fd_cache[last].name, filename);
            i = last;
        }
        lseek(fd_cache[i].fd, offset, SEEK_SET);
#ifdef WIN32
        *len = read(fd_cache[i].fd, data, length);
#else
        *len = read(fd_cache[i].fd, data, 65535);
#endif
        *cp = '@';
    } else {
#ifdef WIN32
        int length = 0;
        if ((fd = open(filename, O_RDONLY | O_BINARY)) == -1) {
            return -1;
        }
        length = lseek(fd, 0, SEEK_END);
        lseek(fd, 0, SEEK_SET);
        *len = read(fd, data, length);
#else
        if ((fd = open(filename, O_RDONLY)) == -1) {
            return -1;
        }
        *len = read(fd, data, 65535);
#endif
        close(fd);
    }

    face_info.cache_hits++;
    *csum = 0;
    return 0;

#if 0
    /* Shouldn't be needed anymore */
    *csum = 0;
    for (i = 0; i < *len; i++) {
        ROTATE_RIGHT(*csum);
        *csum += data[i];
        *csum &= 0xffffffff;
    }
#endif

}

/****************************************************************************
 * This is our image caching logic.  We use a hash to make the name lookups
 * happen quickly - this is done for speed, but also because we don't really
 * have a good idea on how many images may used.  It also means that as the
 * cache gets filled up with images in a random order, the lookup is still
 * pretty quick.
 *
 * If a bucket is filled with an entry that is not of the right name,
 * we store/look for the correct one in the next bucket.
 */

/* This should be a power of 2 */
#define IMAGE_HASH  8192

Face_Information face_info;

/** This holds the name we recieve with the 'face' command so we know what
 * to save it as when we actually get the face.
 */
static char *facetoname[MAXPIXMAPNUM];


struct Image_Cache {
    char    *image_name;
    struct Cache_Entry	*cache_entry;
} image_cache[IMAGE_HASH];

/**
 * This function is basically hasharch from the server, common/arch.c a few
 * changes - first, we stop processing when we reach the first . - this is
 * because I'm not sure if hashing .111 at the end of all the image names will
 * be very useful.
 */
static guint32 image_hash_name(char *str, int tablesize) {
    guint32 hash = 0;
    char *p;

    /* use the same one-at-a-time hash function the server now uses */
    for (p = str; *p != '\0' && *p != '.'; p++) {
        hash += *p;
        hash += hash << 10;
        hash ^= hash >>  6;
    }
    hash += hash <<  3;
    hash ^= hash >> 11;
    hash += hash << 15;
    return hash % tablesize;
}

/**
 * This function returns an index into the image_cache for a matching entry,
 * -1 if no match is found.
 */
static gint32 image_find_hash(char *str) {
    guint32  hash = image_hash_name(str, IMAGE_HASH), newhash;

    newhash = hash;
    do {
        /* No entry - return immediately */
        if (image_cache[newhash].image_name == NULL) {
            return -1;
        }
        if (!strcmp(image_cache[newhash].image_name, str)) {
            return newhash;
        }
        newhash ++;
        if (newhash == IMAGE_HASH) {
            newhash = 0;
        }
    } while (newhash != hash);

    /* If the hash table is full, this is bad because we won't be able to
     * add any new entries.
     */
    LOG(LOG_WARNING, "common::image_find_hash",
        "Hash table is full, increase IMAGE_CACHE size");
    return -1;
}

/**
 *
 */
static void image_remove_hash(char *imagename, Cache_Entry *ce) {
    int	hash_entry;
    Cache_Entry	*last;

    hash_entry = image_find_hash(imagename);
    if (hash_entry == -1) {
        LOG(LOG_ERROR, "common::image_remove_hash",
            "Unable to find cache entry for %s, %s", imagename, ce->filename);
        return;
    }
    if (image_cache[hash_entry].cache_entry == ce) {
        image_cache[hash_entry].cache_entry = ce->next;
        free(ce->filename);
        free(ce);
        return;
    }
    last = image_cache[hash_entry].cache_entry;
    while (last->next && last->next != ce) {
        last = last->next;
    }
    if (!last->next) {
        LOG(LOG_ERROR, "common::image_rmove_hash",
            "Unable to find cache entry for %s, %s", imagename, ce->filename);
        return;
    }
    last->next = ce->next;
    free(ce->filename);
    free(ce);
}

/**
 * This finds and returns the Cache_Entry of the image that matches name
 * and checksum if has_sum is set.  If has_sum is not set, we can't
 * do a checksum comparison.
 */
static Cache_Entry *image_find_cache_entry(char *imagename, guint32 checksum,
        int has_sum) {
    int	hash_entry;
    Cache_Entry	*entry;

    hash_entry = image_find_hash(imagename);
    if (hash_entry == -1) {
        return NULL;
    }
    entry = image_cache[hash_entry].cache_entry;
    if (has_sum) {
        while (entry) {
            if (entry->checksum == checksum) {
                break;
            }
            entry = entry->next;
        }
    }
    return entry;   /* This could be NULL */
}

/**
 * Add a hash entry.  Returns the entry we added, NULL on failure.
 */
static Cache_Entry *image_add_hash(char *imagename, char *filename,
                                   guint32 checksum, guint32 ispublic) {
    Cache_Entry *new_entry;
    guint32  hash = image_hash_name(imagename, IMAGE_HASH), newhash;

    newhash = hash;
    while (image_cache[newhash].image_name != NULL &&
            strcmp(image_cache[newhash].image_name, imagename)) {
        newhash ++;
        if (newhash == IMAGE_HASH) {
            newhash = 0;
        }
        /* If the hash table is full, can't do anything */
        if (newhash == hash) {
            LOG(LOG_WARNING, "common::image_find_hash",
                "Hash table is full, increase IMAGE_CACHE size");
            return NULL;
        }
    }
    if (!image_cache[newhash].image_name) {
        image_cache[newhash].image_name = g_strdup(imagename);
    }

    /* We insert the new entry at the start of the list of the buckets
     * for this entry.  In the case of the players entries, this probably
     * improves performance, presuming ones later in the file are more likely
     * to be used compared to those at the start of the file.
     */
    new_entry = g_malloc(sizeof(struct Cache_Entry));
    new_entry->filename = g_strdup(filename);
    new_entry->checksum = checksum;
    new_entry->ispublic = ispublic;
    new_entry->image_data = NULL;
    new_entry->next = image_cache[newhash].cache_entry;
    image_cache[newhash].cache_entry = new_entry;
    return new_entry;
}

/**
 * Process a line from the bmaps.client file.  In theory, the format should be
 * quite strict, as it is computer generated, but we try to be lenient/follow
 * some conventions.  Note that this is destructive to the data passed in
 * line.
 */
static void image_process_line(char *line, guint32 ispublic) {
    char imagename[MAX_BUF], filename[MAX_BUF];
    guint32 checksum;

    if (line[0] == '#') {
        return;    /* Ignore comments */
    }

    if (sscanf(line, "%s %u %s", imagename, &checksum, filename) == 3) {
        image_add_hash(imagename, filename, checksum, ispublic);
    } else {
        LOG(LOG_WARNING, "common::image_process_line",
            "Did not parse line %s properly?", line);
    }
}

/**
 *
 */
void init_common_cache_data(void) {
    FILE *fp;
    char    bmaps[MAX_BUF], inbuf[MAX_BUF];
    int i;

    if (!want_config[CONFIG_CACHE]) {
        return;
    }

    for (i = 0; i < MAXPIXMAPNUM; i++) {
        facetoname[i] = NULL;
    }

    /* First, make sure that image_cache is nulled out */
    memset(image_cache, 0, IMAGE_HASH * sizeof(struct Image_Cache));

    snprintf(bmaps, sizeof(bmaps), "%s/bmaps.client", CF_DATADIR);
    if ((fp = fopen(bmaps, "r")) != NULL) {
        while (fgets(inbuf, MAX_BUF - 1, fp) != NULL) {
            image_process_line(inbuf, 1);
        }
        fclose(fp);
    } else {
        snprintf(inbuf, sizeof(inbuf),
                 "Unable to open %s.  You may wish to download and install the image file to improve performance.\n",
                 bmaps);
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_NOTICE, inbuf);
    }

    snprintf(bmaps, sizeof(bmaps), "%s/image-cache/bmaps.client", cache_dir);
    if ((fp = fopen(bmaps, "r")) != NULL) {
        while (fgets(inbuf, MAX_BUF - 1, fp) != NULL) {
            image_process_line(inbuf, 0);
        }
        fclose(fp);
    } /* User may not have a cache, so no error if not found */
    for (i = 0; i < MAX_FACE_SETS; i++) {
        fd_cache[i].fd = -1;
        fd_cache[i].name[0] = '\0';
    }
}

/******************************************************************************
 *
 * Code related to face caching.
 *
 *****************************************************************************/

char facecachedir[MAX_BUF];

/**
 *
 */
void requestface(int pnum, char *facename) {
    face_info.cache_misses++;
    facetoname[pnum] = g_strdup(facename);
    cs_print_string(csocket.fd, "askface %d", pnum);
}

/**
 * This is common for all the face commands (face2, face1, face).
 * For face1 and face commands, faceset should always be zero.
 * for face commands, has_sum and checksum will be zero.
 * pnum is the face number, while face is the name.
 * We actually don't care what the set it - it could be useful right now,
 * but in the current caching scheme, we look through all the facesets for
 * the image and if the checksum matches, we assume we have match.
 * This approach makes sure that we don't have to store the same image multiple
 * times simply because the set number may be different.
 */
void finish_face_cmd(int pnum, guint32 checksum, int has_sum, char *face,
                     int faceset) {
    int len;
    guint32 nx, ny;
    guint8 data[65536], *png_tmp;
    char filename[1024];
    guint32 newsum = 0;
    Cache_Entry *ce = NULL;

#if 0
    fprintf(stderr, "finish_face_cmd, pnum=%d, checksum=%d, face=%s\n",
            pnum, checksum, face);
#endif

    /* In the case of gfx, we don't care about checksum.  For public and
     * private areas, if we care about checksum, and the image doesn't match,
     * we go onto the next step.  If nothing found, we request it
     * from the server.
     */
    snprintf(filename, sizeof(filename), "%s/gfx/%s.png", cache_dir, face);
    if (load_image(filename, data, &len, &newsum) == -1) {
        ce = image_find_cache_entry(face, checksum, has_sum);
        if (!ce) {
            /* Not in our cache, so request it from the server */
            requestface(pnum, face);
            return;
        } else if (ce->image_data) {
            /* If this has image_data, then it has already been rendered */
            if (!associate_cache_entry(ce, pnum)) {
                return;
            }
        }
        if (ce->ispublic)
            snprintf(filename, sizeof(filename), "%s/%s",
                     CF_DATADIR, ce->filename);
        else
            snprintf(filename, sizeof(filename), "%s/image-cache/%s",
                    cache_dir, ce->filename);
        if (load_image(filename, data, &len, &newsum) == -1) {
            LOG(LOG_WARNING, "common::finish_face_cmd",
                "file %s listed in cache file, but unable to load", filename);
            requestface(pnum, face);
            return;
        }
    }

    /* If we got here, we found an image and the checksum is OK. */

    if (!(png_tmp = png_to_data(data, len, &nx, &ny))) {
        /* If the data is bad, remove it if it is in the players private cache */
        LOG(LOG_WARNING, "common::finish_face_cmd",
            "Got error on png_to_data, image=%s", face);
        if (ce) {
            if (!ce->ispublic) {
                unlink(filename);
            }
            image_remove_hash(face, ce);
        }

        requestface(pnum, face);
    }

    /* create_and_rescale_image_from data is an external reference to a piece in
     * the gui section of the code.
     */
    if (create_and_rescale_image_from_data(ce, pnum, png_tmp, nx, ny)) {
        LOG(LOG_WARNING, "common::finish_face_cmd",
            "Got error on create_and_rescale_image_from_data, file=%s", filename);
        requestface(pnum, face);
    }
    free(png_tmp);
}


/**
 * We can now connect to different servers, so we need to clear out any old
 * images.  We try to free the data also to prevent memory leaks.
 * Note that we don't touch our hashed entries - so that when we connect to a
 * new server, we still have all that information.
 */
void reset_image_cache_data(void) {
    int i;

    if (want_config[CONFIG_CACHE]) {
        for (i = 1; i < MAXPIXMAPNUM; i++) {
            free(facetoname[i]);
            facetoname[i] = NULL;
        }
    }
}

/**
 * We only get here if the server believes we are caching images.  We rely on
 * the fact that the server will only send a face command for a particular
 * number once - at current time, we have no way of knowing if we have already
 * received a face for a particular number.
 */
void Face2Cmd(guint8 *data,  int len) {
    int pnum;
    guint8 setnum;
    guint32  checksum;
    char *face;

    /* A quick sanity check, since if client isn't caching, all the data
     * structures may not be initialized.
     */
    if (!use_config[CONFIG_CACHE]) {
        LOG(LOG_WARNING, "common::Face2Cmd",
            "Received a 'face' command when we are not caching");
        return;
    }
    pnum = GetShort_String(data);
    setnum = data[2];
    checksum = GetInt_String(data + 3);
    face = (char *)data + 7;
    data[len] = '\0';

    finish_face_cmd(pnum, checksum, 1, face, setnum);
}

/**
 *
 */
void Image2Cmd(guint8 *data,  int len) {
    int pnum, plen;
    guint8 setnum;

    pnum = GetInt_String(data);
    setnum = data[4];
    plen = GetInt_String(data + 5);
    if (len < 9 || (len - 9) != plen) {
        LOG(LOG_WARNING, "common::Image2Cmd", "Lengths don't compare (%d,%d)",
            (len - 9), plen);
        return;
    }
    display_newpng(pnum, data + 9, plen, setnum);
}

/**
 * Helper for display_newpng, implements the caching of the image to disk.
 */
static void cache_newpng(int face, guint8 *buf, int buflen, int setnum,
                         Cache_Entry **ce) {
    char filename[MAX_BUF], basename[MAX_BUF];
    FILE *tmpfile;
    guint32 i, csum;

    if (facetoname[face] == NULL) {
        LOG(LOG_WARNING, "common::display_newpng",
            "Caching images, but name for %ld not set", face);
        /* Return to avoid null dereference. */
        return;
    }
    /* Make necessary leading directories */
    snprintf(filename, sizeof(filename), "%s/image-cache", cache_dir);
    if (g_access(filename, R_OK | W_OK | X_OK) == -1) {
        g_mkdir(filename, 0755);
    }

    snprintf(filename, sizeof(filename), "%s/image-cache/%c%c",
        cache_dir, facetoname[face][0], facetoname[face][1]);
    if (access(filename, R_OK | W_OK | X_OK) == -1) {
        g_mkdir(filename, 0755);
    }

    /* If setnum is valid, and we have faceset information for it,
     * put that prefix in.  This will make it easier later on to
     * allow the client to switch image sets on the fly, as it can
     * determine what set the image belongs to.
     * We also append the number to it - there could be several versions
     * of 'face.base.111.x' if different servers have different image
     * values.
     */
    if (setnum >= 0 && setnum < MAX_FACE_SETS &&
            face_info.facesets[setnum].prefix) {
        snprintf(basename, sizeof(basename), "%s.%s", facetoname[face],
                 face_info.facesets[setnum].prefix);
    } else {
        strcpy(basename, facetoname[face]);
    }

    /* Decrease it by one since it will immediately get increased
     * in the loop below.
     */
    setnum--;
    do {
        setnum++;
        snprintf(filename, sizeof(filename), "%s/image-cache/%c%c/%s.%d",
                cache_dir, facetoname[face][0], facetoname[face][1], basename, setnum);
    } while (g_access(filename, F_OK) == -0);

#ifdef WIN32
    if ((tmpfile = fopen(filename, "wb")) == NULL)
#else
    if ((tmpfile = fopen(filename, "w")) == NULL)
#endif
    {
        LOG(LOG_WARNING, "common::display_newpng", "Can not open %s for writing",
            filename);
    } else {
        /* found a file we can write to */

        fwrite(buf, buflen, 1, tmpfile);
        fclose(tmpfile);
        csum = 0;
        for (i = 0; (int)i < buflen; i++) {
            ROTATE_RIGHT(csum);
            csum += buf[i];
            csum &= 0xffffffff;
        }
        snprintf(filename, sizeof(filename), "%c%c/%s.%d", facetoname[face][0],
                 facetoname[face][1],
                 basename, setnum);
        *ce = image_add_hash(facetoname[face], filename,  csum, 0);

        /* It may very well be more efficient to try to store these up
         * and then write them as a bunch instead of constantly opening the
         * file for appending.  OTOH, hopefully people will be using the
         * built image archives, so only a few faces actually need to get
         * downloaded.
         */
        snprintf(filename, sizeof(filename), "%s/image-cache/bmaps.client", cache_dir);
        if ((tmpfile = fopen(filename, "a")) == NULL) {
            LOG(LOG_WARNING, "common::display_newpng", "Can not open %s for appending",
                filename);
        } else {
            fprintf(tmpfile, "%s %u %c%c/%s.%d\n",
                    facetoname[face], csum, facetoname[face][0],
                    facetoname[face][1], basename, setnum);
            fclose(tmpfile);
        }
    }
}


/**
 * This function is called when the server has sent us the actual png data for
 * an image.  If caching, we need to write this data to disk (this is handled
 * in the function cache_newpng).
 */
void display_newpng(int face, guint8 *buf, int buflen, int setnum) {
    guint8   *pngtmp;
    guint32 width, height;
    Cache_Entry *ce = NULL;

    if (use_config[CONFIG_CACHE]) {
        cache_newpng(face, buf, buflen, setnum, &ce);
    }

    pngtmp = png_to_data(buf, buflen, &width, &height);
    if (create_and_rescale_image_from_data(ce, face, pngtmp, width, height)) {
        LOG(LOG_WARNING, "common::display_newpng",
            "create_and_rescale_image_from_data failed for face %ld", face);
    }

    if (use_config[CONFIG_CACHE]) {
        free(facetoname[face]);
        facetoname[face] = NULL;
    }
    free(pngtmp);
}

/**
 * Takes the data from a replyinfo image_info and breaks it down.  The info
 * contained is the checkums, number of images, and faceset information.  It
 * stores this data into the face_info structure.
 * Since we know data is null terminated, we can use the strchr operations
 * with safety.
 * In each block, we find the newline - if we find one, we presume the data is
 * good, and update the face_info accordingly.  if we don't find a newline, we
 * return.
 */
void get_image_info(guint8 *data, int len) {
    char *cp, *lp, *cps[7], buf[MAX_BUF];
    int onset = 0, badline = 0, i;

    replyinfo_status |= RI_IMAGE_INFO;

    lp = (char *)data;
    cp = strchr(lp, '\n');
    if (!cp || (cp - lp) > len) {
        return;
    }
    face_info.num_images = atoi(lp);

    lp = cp + 1;
    cp = strchr(lp, '\n');
    if (!cp || (cp - lp) > len) {
        return;
    }
    face_info.bmaps_checksum = strtoul(lp, NULL,
                                       10);	/* need unsigned, so no atoi */

    lp = cp + 1;
    cp = strchr(lp, '\n');
    while (cp && (cp - lp) <= len) {
        *cp++ = '\0';

        /* The code below is pretty much the same as the code from the server
         * which loads the original faceset file.
         */
        if (!(cps[0] = strtok(lp, ":"))) {
            badline = 1;
        }
        for (i = 1; i < 7; i++) {
            if (!(cps[i] = strtok(NULL, ":"))) {
                badline = 1;
            }
        }
        if (badline) {
            LOG(LOG_WARNING, "common::get_image_info", "bad data, ignoring line:/%s/", lp);
        } else {
            onset = atoi(cps[0]);
            if (onset >= MAX_FACE_SETS) {
                LOG(LOG_WARNING, "common::get_image_info", "setnum is too high: %d > %d",
                    onset, MAX_FACE_SETS);
            }
            face_info.facesets[onset].prefix = g_strdup(cps[1]);
            face_info.facesets[onset].fullname = g_strdup(cps[2]);
            face_info.facesets[onset].fallback = atoi(cps[3]);
            face_info.facesets[onset].size = g_strdup(cps[4]);
            face_info.facesets[onset].extension = g_strdup(cps[5]);
            face_info.facesets[onset].comment = g_strdup(cps[6]);
        }
        lp = cp;
        cp = strchr(lp, '\n');
    }
    face_info.have_faceset_info = 1;
    /* if the user has requested a specific face set and that set
     * is not numeric, try to find a matching set and send the
     * relevent setup command.
     */
    if (face_info.want_faceset && atoi(face_info.want_faceset) == 0) {
        for (onset = 0; onset < MAX_FACE_SETS; onset++) {
            if (face_info.facesets[onset].prefix &&
                    !g_ascii_strcasecmp(face_info.facesets[onset].prefix, face_info.want_faceset)) {
                break;
            }
            if (face_info.facesets[onset].fullname &&
                    !g_ascii_strcasecmp(face_info.facesets[onset].fullname, face_info.want_faceset)) {
                break;
            }
        }
        if (onset < MAX_FACE_SETS) { /* We found a match */
            face_info.faceset = onset;
            cs_print_string(csocket.fd, "setup faceset %d", onset);
        } else {
            snprintf(buf, sizeof(buf), "Unable to find match for faceset %s on the server",
                     face_info.want_faceset);
            draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_CONFIG, buf);
        }
    }

}

/**
 * This gets a block of checksums from the server.  This lets it prebuild the
 * images or what not.  It would probably be nice to add a gui callback
 * someplace that gives a little status display (18% done or whatever) - that
 * probably needs to be done further up.
 *
 * The start and stop values are not meaningful - they are here because the
 * semantics of the requestinfo/replyinfo is that replyinfo includes the same
 * request data as the requestinfo (thus, if the request failed for some
 * reason, the client would know which one failed and then try again).
 * Currently, we don't have any logic in the function below to deal with
 * failures.
 */
void get_image_sums(char *data, int len) {
    int stop, imagenum, slen, faceset;
    guint32  checksum;
    char *cp, *lp;

    cp = strchr((char *)data, ' ');
    if (!cp || (cp - data) > len) {
        return;
    }

    while (isspace(*cp)) {
        cp++;
    }
    lp = cp;
    cp = strchr(lp, ' ');
    if (!cp || (cp - data) > len) {
        return;
    }
    stop = atoi(lp);

    replyinfo_last_face = stop;

    /* Can't use isspace here, because it matches with tab, ascii code
     * 9 - this results in advancing too many spaces because
     * starting at image 2304, the MSB of the image number will be
     * 9.  Using a check against space will work until we get up to
     * 8192 images.
     */
    while (*cp == ' ') {
        cp++;
    }
    while ((cp - data) < len) {
        imagenum = GetShort_String((guint8 *)cp);
        cp += 2;
        checksum = GetInt_String((guint8 *)cp);
        cp += 4;
        faceset = *cp;
        cp++;
        slen = *cp;
        cp++;
        /* Note that as is, this can break horribly if the client is missing a large number
         * of images - that is because it will request a whole bunch which will overflow
         * the servers output buffer, causing it to close the connection.
         * What probably should be done is for the client to just request this checksum
         * information in small batches so that even if the client has no local
         * images, requesting the entire batch won't overflow the sockets buffer - this
         * probably amounts to about 100 images at a time
         */
        finish_face_cmd(imagenum, checksum, 1, (char *)cp, faceset);
        if (imagenum > stop) {
            LOG(LOG_WARNING, "common::get_image_sums",
                "Received an image beyond our range? %d > %d", imagenum, stop);
        }
        cp += slen;
    }
}

