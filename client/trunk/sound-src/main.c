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
 * @file sound-src/main.c
 */

#include "client.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <SDL.h>

#include "common.h"
#include "version.h"

/** Print detailed version information. */
static void print_version() {
    printf("Crossfire Sound Server %s\n", FULL_VERSION);
}

/** Print a message stating how to get help. */
static void print_quickhelp() {
    fprintf(stderr, "Type 'cfsndserv -h' for usage.\n");
}

/** Print out usage information. */
static void print_usage() {
    printf(
        "Usage: cfsndserv [options]\n"
        "\n"
        "Options:\n"
        "  -h   display this help message\n"
        "  -v   display version information\n"
    );
}

/**
 * Preliminary handler for Crossfire server sound2 and music commands that are
 * received from the client via stdin.
 *
 * The sound player differentiates sound2 and music commands by looking at the
 * first parameter that comes in.  Music commands consist of a single, quoted
 * string that identifies the music to play, while a sound effect command has
 * various numeric parameters followed by strings that identify what to play.
 *
 * Sound2 data consists of whitespace delimited values:  x, y, dir, vol, type,
 * sound, and source.  Type, sound, and source define what to play, while the
 * other parameters may be used to figure out how to play it.  x and y are
 * offsets from the player to identify where the sound originated. dir can be
 * set to indicate a direction that the source is travelling in. vol is an
 * attenuation factor (0-100) that may be applied to the sound volume to make
 * it possible, for example, to give map-designers the ability to suggest
 * relative loudness of sounds in the environment.
 *
 * FIXME: This is a work-in-progress.  The sound2 was put into the server
 * without a plan to fix the clients.  cfsndserv is basically made obsolete by
 * sound2.  The basic fix resurrects some sound support but does not fully
 * implement the features sound2 is supposed to provide.
 *
 * @param data A text buffer that (hopefully) has a sound or music command.
 * @param len  The length of the text data in the command buffer.
 * @return     0 if the buffer contains a well-formed command, otherwise -1.
 */
static int parse_input(char *data, int len) {
    char* dptr;                         /* Pointer used when parsing data */
    char* sound = NULL;                 /* Points to a sound or music name */
    char* source = NULL;
    int   soundlen;
    int   spacelen;
    int   type = 0;
    int   dir = 0;
    int   vol = 0;
    int   x = 0;
    int   y = 0;
    int   i = 0;

    dptr = strtok(data, "\"");
    /*
     * Is data a blank line (ending with LF) or is it a quoted, empty string?
     */
    if (dptr == NULL) {
        fprintf(stderr, "Sound/music command does not contain any data.\n");
        return -1;
    }
    /*
     * If the first character is not a quote character, a sound command is
     * expected.
     */
    if (data[0] != '\"') {
        /*
         * There are 5 numeric values expected and required.  Technically, if
         * cfsndserv was new, and the client old, 4 might be present, but the
         * player does not attempt to support old clients.
         */
        i = sscanf(dptr, "%d %d %d %d %d", &x, &y, &dir, &vol, &type);

        if ((i != 5)
                ||  (dir < 0)
                ||  (dir > 8)
                ||  (vol < 0)
                ||  (vol > 100)
                ||  (type < 1)) {
            /*
             * There is not much point in trying to work with data that does
             * not fit some basic rules known at the time of development.
             */
            fprintf(stderr, "Unrecognized sound command data format.\n");
#ifdef SOUND_DEBUG
            fprintf(stderr,
                    "(%d valid items read) x=%d y=%d dir=%d vol=%d type=%d\n",
                    i, x, y, dir, vol, type);
#endif
            return -1;
        }
    }
    /*
     * Below this point, when type == 0, a music command is expected, and when
     * type != 0, a sound command is required.
     */
    if (type) {
        /*
         * dptr points to the numerics already read, so advance to the string
         * following the first quote delimiter.  A sound source name is
         * expected.
         */
        dptr = strtok(NULL, "\"");
        if (dptr == NULL) {
            fprintf(stderr, "Sound command is missing sound/source names.\n");
            return -1;
        }
        source = dptr;

        /*
         * Verify there is whitespace between source and sound names.
         */
        dptr = strtok(NULL, "\"");
        if (dptr == NULL) {
            fprintf(stderr, "Sound command is missing the sound name.\n");
            return -1;
        }
        spacelen = strlen(dptr);
        for (i = 0; i < spacelen; i++) {
            if (dptr[i] != ' ' && dptr[i] != '\t') {
                fprintf(stderr, "Invalid characters after source name.\n");
                return -1;
            }
        }
        /*
         * Advance the data pointer to the following sound name.
         */
        dptr = strtok(NULL, "\"");
        if (dptr == NULL) {
            fprintf(stderr, "Sound command is missing the sound name.\n");
            return -1;
        }
    }
    /*
     * Record the sound or music name here (type determines which it is).
     */
    sound = dptr;
    soundlen = strlen(dptr);
    /*
     * If there was a trailing quote after the sound or music name, there will
     * be a null there now, and sound[soundlen] should point to the character
     * just before another null at data[len-1] (that terminates the command).
     */
    i = sound - data + soundlen + 1 + 1;
    if (i - 1 == len) {
        fprintf(stderr, "Sound or music name does not end with a quote.\n");
        return -1;
    }
    if (i > len) {
        fprintf(stderr,
                "Invalid data after sound/music name (a quoted string needed)\n");
        return -1;
    }

    if (type) {
        /* Play sound effect. */
        fprintf(stderr, "Playing sound "
                "%d,%d dir=%d vol=%d type=%d source=\"%s\" sound=\"%s\"\n",
                x, y, dir, vol, type, source, sound);
        cf_play_sound(x, y, dir, vol, type, sound, source);
        return 0;
    } else {
        /* Play music. */
#ifdef SOUND_DEBUG
        fprintf(stderr, "Playing music \"%s\"\n", sound);
#endif
        cf_play_music(sound);
    }

    return 0;
}

int main(int argc, char *argv[]) {
    int flag;
    while ((flag = getopt(argc, argv, "hv")) != -1) {
        switch (flag) {
        case 'h':
            print_usage();
            exit(EXIT_SUCCESS);
            break;
        case 'v':
            print_version();
            exit(EXIT_SUCCESS);
            break;
        case '?':
            print_quickhelp();
            exit(EXIT_FAILURE);
            break;
        }
    }

    if (cf_snd_init() != 0) {
        exit(EXIT_FAILURE);
    }
    atexit(cf_snd_exit);

    char inbuf[1024];
    while (fgets(inbuf, sizeof(inbuf), stdin) != NULL) {
        parse_input(inbuf, strlen(inbuf));
    }
}
