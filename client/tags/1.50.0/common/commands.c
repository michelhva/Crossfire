const char * const rcsid_common_commands_c =
    "$Id$";
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2001,2010 Mark Wedel & Crossfire Development Team

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

/**
 * @file common/commands.c
 * Handles server->client commands; See player.c for client->server commands.
 *
 * Not necessarily all commands are handled - some might be in other files
 * (like init.c)
 *
 * This file contains most of the commands for the dispatch loop. Most of
 * the functions are self-explanatory.
 *
 * pixmap/bitmap : receive the picture, and display it.
 * drawinfo      : draws a string in the info window.
 * stats         : updates the local copy of the stats and displays it.
 * handle_query  : prompts the user for input.
 * send_reply    : sends off the reply for the input.
 * player        : gets the player information.
 * MapScroll     : scrolls the map on the client by some amount.
 * MapCmd        : displays the map with layer packing or stack packing.
 *   packing/unpacking is best understood by looking at the server code
 *   (server/ericserver.c)
 *   stack packing: for every map entry that changed, we pack 1 byte for the
 *   x/y location, 1 byte for the count, and 2 bytes per face in the stack.
 *   layer packing is harder, but I seem to remember more efficient: first we
 *   pack in a list of all map cells that changed and are now empty.  The end
 *   of this list is a 255, which is bigger that 121, the maximum packed map
 *   location.
 *   For each changed location we also pack in a list of all the faces and X/Y
 *   coordinates by layer, where the layer is the depth in the map.  This
 *   essentially takes slices through the map rather than stacks.
 *   Then for each layer, (max is MAXMAPCELLFACES, a bad name) we start
 *   packing the layer into the message.  First we pack in a face, then for
 *   each place on the layer with the same face, we pack in the x/y location.
 *   We mark the last x/y location with the high bit on (11*11 = 121 < 128).
 *   We then continue on with the next face, which is why the code marks the
 *   faces as -1 if they are finished.  Finally we mark the last face in the
 *   layer again with the high bit, clearly limiting the total number of faces
 *   to 32767, the code comments it's 16384, I'm not clear why, but the second
 *   bit may be used somewhere else as well.
 *   The unpacking routines basically perform the opposite operations.
 */

int mapupdatesent = 0;

#include <client.h>
#include <external.h>
#include <assert.h>

#include "mapdata.h"

char *news=NULL, *motd=NULL, *rules=NULL;

int spellmon_level = 0;                 /**< Keeps track of what spellmon
                                         *   command is supported by the
                                         *   server. */

/**
 *
 * @param data
 * @param len
 */
static void get_exp_info(const unsigned char *data, int len) {
    int pos, level;

    if (len < 2) {
        LOG(LOG_ERROR, "common::get_exp_info", "no max level info from server provided");
        return;
    }

    exp_table_max = GetShort_String(data);
    pos = 2;
    exp_table = calloc(exp_table_max, sizeof(uint64));
    for (level = 1; level <= exp_table_max && pos < len; level++) {
        exp_table[level] = GetInt64_String(data+pos);
        pos += 8;
    }
    if (level != exp_table_max) {
        LOG(LOG_ERROR, "common::get_exp_info",
             "Incomplete table sent - got %d entries, wanted %d", level, exp_table_max);
    }
}

/**
 *
 * @param data
 * @param len
 */
static void get_skill_info(char *data, int len) {
    char *cp, *nl, *sn;
    int val;

    cp = data;
    do {
        nl = strchr(cp, '\n');
        if (nl) {
            *nl = 0;
            nl++;
        }
        sn = strchr(cp, ':');
        if (!sn) {
            LOG(LOG_WARNING, "common::get_skill_info", "corrupt line: /%s/", cp);
            return;
        }

        *sn = 0;
        sn++;
        val = atoi(cp);
        val -= CS_STAT_SKILLINFO;
        if (val < 0 || val> CS_NUM_SKILLS) {
            LOG(LOG_WARNING, "common::get_skill_info", "invalid skill number %d", val);
            return;
        }

        free(skill_names[val]);
        skill_names[val] = strdup_local(sn);
        cp = nl;
    } while (cp < data+len);
}

/**
 * Handles the response from a 'requestinfo' command.  This function doesn't
 * do much itself other than dispatch to other functions.
 *
 * @param buf
 * @param len
 */
void ReplyInfoCmd(uint8 *buf, int len) {
    uint8 *cp;
    int i;

    /* Covers a bug in the server in that it could send a replyinfo with no parameters */
    if (!buf) {
        return;
    }

    for (i = 0; i < len; i++) {
        /* Either a space or newline represents a break */
        if (*(buf+i) == ' ' || *(buf+i) == '\n') {
            break;
        }
    }
    if (i >= len) {
        /* Don't print buf, as it may contain binary data */
        /* Downgrade this to DEBUG - if the client issued an unsupported requestinfo
         * info to the server, we'll end up here - this could be normal behaviour
         */
        LOG(LOG_DEBUG, "common::ReplyInfoCmd", "Never found a space in the replyinfo");
        return;
    }

    /* Null out the space and put cp beyond it */
    cp = buf+i;
    *cp++ = '\0';
    if (!strcmp((char*)buf, "image_info")) {
        get_image_info(cp, len-i-1);   /* located in common/image.c */
    } else if (!strcmp((char*)buf, "image_sums")) {
        get_image_sums((char*)cp, len-i-1);   /* located in common/image.c */
    } else if (!strcmp((char*)buf, "skill_info")) {
        get_skill_info((char*)cp, len-i-1);   /* located in common/commands.c */
    } else if (!strcmp((char*)buf, "exp_table")) {
        get_exp_info(cp, len-i-1);   /* located in common/commands.c */
    } else if (!strcmp((char*)buf, "motd")) {
        if (motd) free((char*)motd);
        motd = strdup(cp);
        update_login_info(INFO_MOTD);
    } else if (!strcmp((char*)buf, "news")) {
        if (news) free((char*)news);
        news = strdup(cp);
        update_login_info(INFO_NEWS);
    } else if (!strcmp((char*)buf, "rules")) {
        if (rules) free((char*)rules);
        rules = strdup(cp);
        update_login_info(INFO_RULES);
    }
}

/**
 * Received a response to a setup from the server.
 * This function is basically the same as the server side function - we just
 * do some different processing on the data.
 *
 * @param buf
 * @param len
 */
void SetupCmd(char *buf, int len) {
    int s;
    char *cmd, *param;

    /* Process the setup commands.
     * Syntax is setup <cmdname1> <parameter> <cmdname2> <parameter> ...
     *
     * The server sends the status of the cmd back, or a FALSE if the cmd is
     * unknown.  The client then must sort this out.
     */

    LOG(LOG_DEBUG, "common::SetupCmd", "%s", buf);
    for (s = 0; ; ) {
        if (s >= len) { /* ugly, but for secure...*/
            break;
        }

        cmd = &buf[s];

        /* find the next space, and put a null there */
        for (; buf[s] && buf[s] != ' '; s++)
            ;
        buf[s++] = 0;
        while (buf[s] == ' ') {
            s++;
        }
        if (s >= len) {
            break;
        }

        param = &buf[s];

        for (; buf[s] && buf[s] != ' '; s++)
            ;
        buf[s++] = 0;
        while (s < len && buf[s] == ' ') {
            s++;
        }

        /* What is done with the returned data depends on what the server
         * returns.  In some cases the client may fall back to other methods,
         * report an error, or try another setup command.
         */
        if (!strcmp(cmd, "sound2")) {
            /* No parsing needed, but we don't want a warning about unknown
             * setup option below.
             */
        } else if (!strcmp(cmd, "sound")) {
            /* No, this should not be !strcmp()... */
        } else if (!strcmp(cmd, "mapsize")) {
            int x, y = 0;
            char *cp, tmpbuf[MAX_BUF];

            if (!strcasecmp(param, "false")) {
                draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER,
                  "Server only supports standard sized maps (11x11)");
                /* Do this because we may have been playing on a big server
                 * before */
                use_config[CONFIG_MAPWIDTH] = 11;
                use_config[CONFIG_MAPHEIGHT] = 11;
                mapdata_set_size(use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                resize_map_window(use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                continue;
            }
            x = atoi(param);
            for (cp = param; *cp != 0; cp++) {
                if (*cp == 'x' || *cp == 'X') {
                    y = atoi(cp+1);
                    break;
                }
            }
            /* A size larger than what the server supports was requested.
             * Reduce the size to server maximum, and re-send the setup
             * command.  Update our want sizes, and tell the player what is
             * going on.
             */
            if (use_config[CONFIG_MAPWIDTH] > x || use_config[CONFIG_MAPHEIGHT] > y) {
                if (use_config[CONFIG_MAPWIDTH] > x) use_config[CONFIG_MAPWIDTH] = x;
                if (use_config[CONFIG_MAPHEIGHT] > y) use_config[CONFIG_MAPHEIGHT] = y;
                mapdata_set_size(use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                cs_print_string(csocket.fd,
                                "setup mapsize %dx%d", use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                snprintf(tmpbuf, sizeof(tmpbuf), "Server supports a max mapsize of %d x %d - requesting a %d x %d mapsize",
                    x, y, use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER,
                    tmpbuf);
            } else if (use_config[CONFIG_MAPWIDTH] == x && use_config[CONFIG_MAPHEIGHT] == y) {
                mapdata_set_size(use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
                resize_map_window(use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT]);
            } else {
                /* The request was not bigger than what server supports, and
                 * not the same size, so what is the problem?  Tell the user
                 * that something is wrong.
                 */
                snprintf(tmpbuf, sizeof(tmpbuf), "Unable to set mapsize on server - we wanted %d x %d, server returned %d x %d",
                    use_config[CONFIG_MAPWIDTH], use_config[CONFIG_MAPHEIGHT], x, y);
                draw_ext_info(
                    NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER, tmpbuf);
            }
        } else if (!strcmp(cmd, "darkness")) {
            /* Older servers might not support this setup command.
             */
            if (!strcmp(param, "FALSE")) {
                LOG(LOG_WARNING, "common::SetupCmd", "Server returned FALSE for setup command %s", cmd);
            }
        } else if (!strcmp(cmd, "spellmon")) {

            /* Older servers might not support this setup command or all of
             * the extensions.
             *
             * Spellmon 2 was added to the protocol in January 2010 to send an
             * additional spell information string with casting requirements
             * including required items, if the spell needs arguments passed
             * (like text for rune of marking), etc.
             *
             * To use the new feature, "setup spellmon 1 spellmon 2" is sent,
             * and if "spellmon 1 spellmon FALSE" is returned then the server
             * doesn't accept 2 - sending spellmon 2 to a server that does not
             * support it is not problematic, so the spellmon 1 command will
             * still be handled correctly by the server.  If the server sends
             * "spellmon 1 spellmon 2" then the extended mode is in effect.
             *
             * It is not particularly important for the player to know what
             * level of command is accepted by the server.  The extra features
             * will simply not be functionally available.
             */
            if (!strcmp(param, "FALSE")) {
                LOG(LOG_INFO, "common::SetupCmd", "Server returned FALSE for a %s setup command", cmd);
            } else {
                spellmon_level = atoi(param);
            }
        } else if (!strcmp(cmd, "facecache")) {
                use_config[CONFIG_CACHE] = atoi(param);
        } else if (!strcmp(cmd, "faceset")) {
            if (!strcmp(param, "FALSE")) {
                draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER,
                    "Server does not support other image sets, will use default");
                face_info.faceset = 0;
            }
        } else if (!strcmp(cmd, "map2cmd")) {
            if (!strcmp(param, "FALSE")) {
                draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER,
                    "Server does not support map2cmd!");
                draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_SERVER,
                    "This server is too old to support this client!");
                close_server_connection();
            }
        } else if (!strcmp(cmd, "want_pickup")) {
            /* Nothing special to do as this is info pushed from server and
             * not having it isn't that bad.
             */
        } else if (!strcmp(cmd, "loginmethod")) {
            int method = atoi(param);

            /* If the server supports new login, start the process.  Pass what
             * version the server supports so client can do appropriate
             * work
             */
            if (method) {
                start_login(method);
            }
        } else {
            LOG(LOG_INFO, "common::SetupCmd", "Got setup for a command we don't understand: %s %s",
                cmd, param);
        }
    }
}

/**
 * Handles when the server says we can't be added.  In reality, we need to
 * close the connection and quit out, because the client is going to close us
 * down anyways.
 *
 * @param data
 * @param len
 */
void AddMeFail(char *data, int len) {
    (void)data; /* __UNUSED__ */
    (void)len; /* __UNUSED__ */

    LOG(LOG_INFO, "common::AddMeFail", "addme_failed received.");
    return;
}

/**
 * This is really a throwaway command - there really isn't any reason to send
 * addme_success commands.
 *
 * @param data
 * @param len
 */
void AddMeSuccess(char *data, int len) {
    (void)data; /* __UNUSED__ */
    (void)len; /* __UNUSED__ */

    hide_all_login_windows();
    LOG(LOG_INFO, "common::AddMeSuccess", "addme_success received.");
    return;
}

/**
 *
 * @param data
 * @param len
 */
void GoodbyeCmd(char *data, int len) {
    (void)data; /* __UNUSED__ */
    (void)len; /* __UNUSED__ */

    /* This could probably be greatly improved - I am not sure if anything
     * needs to be saved here, but certainly it should be possible to
     * reconnect to the server or a different server without having to
     * rerun the client.
     */
    LOG(LOG_WARNING, "common::GoodbyeCmd", "Received goodbye command from server - exiting");
    exit(0);
}

Animations animations[MAXANIM];

/**
 *
 * @param data
 * @param len
 */
void AnimCmd(unsigned char *data, int len) {
    short anum;
    int i, j;

    anum = GetShort_String(data);
    if (anum < 0 || anum > MAXANIM) {
        LOG(LOG_WARNING, "common::AnimCmd", "animation number invalid: %d", anum);
        return;
    }

    animations[anum].flags = GetShort_String(data+2);
    animations[anum].num_animations = (len-4)/2;
    if (animations[anum].num_animations < 1) {
        LOG(LOG_WARNING, "common::AnimCmd", "num animations invalid: %d",
            animations[anum].num_animations);
        return;
    }
    animations[anum].faces = malloc(sizeof(uint16)*animations[anum].num_animations);
    for (i = 4, j = 0; i < len; i += 2, j++) {
        animations[anum].faces[j] = GetShort_String(data+i);
    }

    if (j != animations[anum].num_animations) {
        LOG(LOG_WARNING, "common::AnimCmd",
            "Calculated animations does not equal stored animations? (%d!=%d)",
            j, animations[anum].num_animations);
    }

    animations[anum].speed = 0;
    animations[anum].speed_left = 0;
    animations[anum].phase = 0;

    LOG(LOG_DEBUG, "common::AnimCmd", "Received animation %d, %d faces", anum, animations[anum].num_animations);
}

/**
 * Receives the smooth mapping from the server.  Because this information is
 * reference a lot, the smoothing face is stored in the pixmap data - this
 * makes access much faster than searching an array of data for the face to
 * use.
 *
 * @param data
 * @param len
 */
void SmoothCmd(unsigned char *data, int len) {
    uint16 faceid;
    uint16 smoothing;

    /* len is unused.
     * We should check that we don't have an invalid short command.
     * Hence, the compiler warning is valid.
     */

    faceid = GetShort_String(data);
    smoothing = GetShort_String(data+2);
    addsmooth(faceid, smoothing);
}

/**
 * Draws a string in the info window.
 *
 * @param data
 * @param len
 */
void DrawInfoCmd(char *data, int len) {
    int color = atoi(data);
    char *buf;

    (void)len; /* __UNUSED__ */

    buf = strchr(data, ' ');
    if (!buf) {
        LOG(LOG_WARNING, "common::DrawInfoCmd", "got no data");
        buf = "";
    } else {
        buf++;
    }
    draw_ext_info(color, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_COMMAND, buf);
}

TextManager *firstTextManager = NULL;

/**
 *
 * @param type
 * @param callback
 */
void setTextManager(int type, ExtTextManager callback) {
    TextManager *current = firstTextManager;

    while (current != NULL) {
        if (current->type == type) {
            current->callback = callback;
            return;
        }
        current = current->next;
    }
    current = malloc(sizeof(TextManager));
    current->type = type;
    current->callback = callback;
    current->next = firstTextManager;
    firstTextManager = current;
}

/**
 *
 * @param type
 */
static ExtTextManager getTextManager(int type) {
    TextManager *current = firstTextManager;
    while (current != NULL) {
        if (current->type == type) {
            return current->callback;
        }
        current = current->next;
    }
    return NULL;
}

/**
 * We must extract color, type, subtype and dispatch to callback
 *
 * @param data
 * @param len
 */
void DrawExtInfoCmd(char *data, int len) {
    int color;
    int type, subtype;
    char *buf = data;
    int wordCount = 3;
    ExtTextManager fnct;

    while (wordCount > 0) {
        while (buf[0] == ' ') {
            buf++;
        }
        wordCount--;
        while (buf[0] != ' ') {
            if (buf[0] == '\0') {
                LOG(LOG_WARNING,
                    "common::DrawExtInfoCmd", "Data is missing %d parameters %s",
                    wordCount,
                    data);
                return;
            } else {
                buf++;
            }
        }
        if (buf[0] == ' ') {
            buf++; /*remove trailing space to send clean data to callback */
        }
    }
    wordCount = sscanf(data, "%d %d %d", &color, &type, &subtype);
    if (wordCount != 3) {
        LOG(LOG_WARNING,
            "common::DrawExtInfoCmd", "Wrong parameters received. Could only parse %d out of 3 int in %s",
            wordCount,
            data);
        return;
    }
    fnct = getTextManager(type);
    if (fnct == NULL) {
        LOG(LOG_WARNING,
            "common::DrawExtInfoCmd", "Server send us a type %d but i can't find any callback for it",
            type);
        return;
    }
    fnct(color, type, subtype, buf);
}

/**
 * Maintain the last_used_skills LRU list for displaying the recently used
 * skills first.
 *
 * @param skill_id
 */
void use_skill(int skill_id)
{
   int i = 0;
   int next;
   int prev = last_used_skills[0];

   if(last_used_skills[0] == skill_id) return;

   do
   {
	  next = last_used_skills[i+1];
	  last_used_skills[i+1] = prev;
	  prev = next;
	  ++i;
   } while(next != skill_id && next >= 0);
   last_used_skills[0] = skill_id;
}

/**
 * Updates the local copy of the stats and displays it.
 *
 * @param data
 * @param len
 */
void StatsCmd(unsigned char *data, int len) {
    int i = 0, c, redraw = 0;
    sint64 last_exp;

    while (i < len) {
        c = data[i++];
        if (c >= CS_STAT_RESIST_START && c <= CS_STAT_RESIST_END) {
            cpl.stats.resists[c-CS_STAT_RESIST_START] = GetShort_String(data+i);
            i += 2;
            cpl.stats.resist_change = 1;
        } else if (c >= CS_STAT_SKILLINFO && c < (CS_STAT_SKILLINFO+CS_NUM_SKILLS)) {
            /* We track to see if the exp has gone from 0 to some total value -
             * we do this because the draw logic currently only draws skills where
             * the player has exp.  We need to communicate to the draw function
             * that it should draw all the players skills.  Using redraw is
             * a little overkill, because a lot of the data may not be changing.
             * OTOH, such a transition should only happen rarely, not not be a very
             * big deal.
             */
            cpl.stats.skill_level[c-CS_STAT_SKILLINFO] = data[i++];
            last_exp = cpl.stats.skill_exp[c-CS_STAT_SKILLINFO];
            cpl.stats.skill_exp[c-CS_STAT_SKILLINFO] = GetInt64_String(data+i);
			use_skill(c-CS_STAT_SKILLINFO);
            if (last_exp == 0 && cpl.stats.skill_exp[c-CS_STAT_SKILLINFO]) {
                redraw = 1;
            }
            i += 8;
        } else {
            switch (c) {
            case CS_STAT_HP:      cpl.stats.hp = GetShort_String(data+i); i += 2; break;
            case CS_STAT_MAXHP:   cpl.stats.maxhp = GetShort_String(data+i); i += 2; break;
            case CS_STAT_SP:      cpl.stats.sp = GetShort_String(data+i); i += 2; break;
            case CS_STAT_MAXSP:   cpl.stats.maxsp = GetShort_String(data+i); i += 2; break;
            case CS_STAT_GRACE:   cpl.stats.grace = GetShort_String(data+i); i += 2; break;
            case CS_STAT_MAXGRACE:cpl.stats.maxgrace = GetShort_String(data+i); i += 2; break;
            case CS_STAT_STR:     cpl.stats.Str = GetShort_String(data+i); i += 2; break;
            case CS_STAT_INT:     cpl.stats.Int = GetShort_String(data+i); i += 2; break;
            case CS_STAT_POW:     cpl.stats.Pow = GetShort_String(data+i); i += 2; break;
            case CS_STAT_WIS:     cpl.stats.Wis = GetShort_String(data+i); i += 2; break;
            case CS_STAT_DEX:     cpl.stats.Dex = GetShort_String(data+i); i += 2; break;
            case CS_STAT_CON:     cpl.stats.Con = GetShort_String(data+i); i += 2; break;
            case CS_STAT_CHA:     cpl.stats.Cha = GetShort_String(data+i); i += 2; break;
            case CS_STAT_EXP:     cpl.stats.exp = GetInt_String(data+i); i += 4; break;
            case CS_STAT_EXP64:   cpl.stats.exp = GetInt64_String(data+i); i += 8; break;
            case CS_STAT_LEVEL:   cpl.stats.level = GetShort_String(data+i); i += 2; break;
            case CS_STAT_WC:      cpl.stats.wc = GetShort_String(data+i); i += 2; break;
            case CS_STAT_AC:      cpl.stats.ac = GetShort_String(data+i); i += 2; break;
            case CS_STAT_DAM:     cpl.stats.dam = GetShort_String(data+i); i += 2; break;
            case CS_STAT_ARMOUR:  cpl.stats.resists[0] = GetShort_String(data+i); i += 2; break;
            case CS_STAT_SPEED:   cpl.stats.speed = GetInt_String(data+i); i += 4; break;
            case CS_STAT_FOOD:    cpl.stats.food = GetShort_String(data+i); i += 2; break;
            case CS_STAT_WEAP_SP: cpl.stats.weapon_sp = GetInt_String(data+i); i += 4; break;
            case CS_STAT_SPELL_ATTUNE:cpl.stats.attuned = GetInt_String(data+i); i += 4; cpl.spells_updated = 1; break;
            case CS_STAT_SPELL_REPEL:cpl.stats.repelled = GetInt_String(data+i); i += 4; cpl.spells_updated = 1; break;
            case CS_STAT_SPELL_DENY:cpl.stats.denied = GetInt_String(data+i); i += 4; cpl.spells_updated = 1; break;

            case CS_STAT_FLAGS: cpl.stats.flags = GetShort_String(data+i); i += 2; break;
            case CS_STAT_WEIGHT_LIM:set_weight_limit(cpl.stats.weight_limit = GetInt_String(data+i)); i += 4; break;

                /* Skill experience handling */
                /* We make the assumption based on current bindings in the protocol
                 * that these skip 2 values and are otherwise in order.
                 */
            case CS_STAT_SKILLEXP_AGILITY:
            case CS_STAT_SKILLEXP_PERSONAL:
            case CS_STAT_SKILLEXP_MENTAL:
            case CS_STAT_SKILLEXP_PHYSIQUE:
            case CS_STAT_SKILLEXP_MAGIC:
            case CS_STAT_SKILLEXP_WISDOM:
				  {
					 int skill_id = (c-CS_STAT_SKILLEXP_START)/2;
				   cpl.stats.skill_exp[skill_id] = GetInt_String(data+i);
				   use_skill(skill_id);
                i += 4;
				  }
                break;

            case CS_STAT_SKILLEXP_AGLEVEL:
            case CS_STAT_SKILLEXP_PELEVEL:
            case CS_STAT_SKILLEXP_MELEVEL:
            case CS_STAT_SKILLEXP_PHLEVEL:
            case CS_STAT_SKILLEXP_MALEVEL:
            case CS_STAT_SKILLEXP_WILEVEL:
                cpl.stats.skill_level[(c-CS_STAT_SKILLEXP_START-1)/2] = GetShort_String(data+i);
                i += 2;
                break;

            case CS_STAT_RANGE: {
                int rlen = data[i++];
                strncpy(cpl.range, (const char*)data+i, rlen);
                cpl.range[rlen] = '\0';
                i += rlen;
                break;
            }

            case CS_STAT_TITLE: {
                int rlen = data[i++];
                strncpy(cpl.title, (const char*)data+i, rlen);
                cpl.title[rlen] = '\0';
                i += rlen;
                break;
            }

            default:
                LOG(LOG_WARNING, "common::StatsCmd", "Unknown stat number %d", c);
/*              abort();*/
                break;
            }
        }
    }

    if (i > len) {
        LOG(LOG_WARNING, "common::StatsCmd", "got stats overflow, processed %d bytes out of %d", i, len);
    }
    draw_stats(redraw);
    draw_message_window(0);
#ifdef HAVE_LUA
    script_lua_stats();
#endif
}

/**
 * Prompts the user for input.
 *
 * @param data
 * @param len
 */
void handle_query(char *data, int len) {
    char *buf, *cp;
    uint8 flags = atoi(data);

    (void)len; /* __UNUSED__ */

    if (flags&CS_QUERY_HIDEINPUT) { /* no echo */
        cpl.no_echo = 1;
    } else {
        cpl.no_echo = 0;
    }

    /* Let the window system know this may have changed */
    x_set_echo();

    /* The actual text is optional */
    buf = strchr(data, ' ');
    if (buf) {
        buf++;
    }

    /* If we just get passed an empty string, why draw this? */
    if (buf) {
        cp = buf;
        while ((buf = strchr(buf, '\n')) != NULL) {
            *buf++ = '\0';
            draw_ext_info(
                NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_QUERY, cp);
            cp = buf;
        }
        /* Yes/no - don't do anything with it now */
        if (flags&CS_QUERY_YESNO) {
        }

        /* one character response expected */
        if (flags&CS_QUERY_SINGLECHAR) {
            cpl.input_state = Reply_One;
        } else {
            cpl.input_state = Reply_Many;
        }

        if (cp) {
            draw_prompt(cp);
        }
    }

    LOG(LOG_DEBUG, "common::handle_query", "Received query.  Input state now %d", cpl.input_state);
}

/**
 * Sends a reply to the server.
 * This function basically just packs the stuff up.
 *
 * @param text contains the null terminated string of text to send.
 */
void send_reply(const char *text) {
    cs_print_string(csocket.fd, "reply %s", text);

    /* Let the window system know that the (possibly hidden) query is over. */
    cpl.no_echo = 0;
    x_set_echo();
}

/**
 * Gets the player information.
 * This function copies relevant data from the archetype to the object.  Only
 * copies data that was not set in the object structure.
 *
 * @param data
 * @param len
 */
void PlayerCmd(unsigned char *data, int len) {
    char name[MAX_BUF];
    int tag, weight, face, i = 0, nlen;

    reset_player_data();
    tag = GetInt_String(data); i += 4;
    weight = GetInt_String(data+i); i += 4;
    face = GetInt_String(data+i); i += 4;
    nlen = data[i++];
    memcpy(name, (const char*)data+i, nlen);
    name[nlen] = '\0';
    i += nlen;

    if (i != len) {
        LOG(LOG_WARNING, "common::PlayerCmd", "lengths do not match (%d!=%d)", len, i);
    }
    new_player(tag, name, weight, face);
}

/**
 *
 * @param op
 */
void item_actions(item *op) {
    if (!op) {
        return;
    }

    if (op->open) {
        open_container(op);
        cpl.container = op;
    } else if (op->was_open) {
        close_container(op);
        cpl.container = NULL;
    }
}

/**
 * Parses the data sent to us from the server.
 * revision is what item command the data came from - newer ones have addition
 * fields.
 *
 * @param data
 * @param len
 */
static void common_item_command(uint8 *data, int len) {

    int weight, loc, tag, face, flags, pos = 0, nlen, anim, nrof, type;
    uint8 animspeed;
    char name[MAX_BUF];

    loc = GetInt_String(data);
    pos += 4;

    if (pos == len) {
        LOG(LOG_WARNING, "common::common_item_command", "Got location with no other data");
        return;
    } else if (loc < 0) { /* delete following items */
        LOG(LOG_WARNING, "common::common_item_command", "Got location with negative value (%d)", loc);
        return;
    } else {
        while (pos < len) {
            tag = GetInt_String(data+pos); pos += 4;
            flags = GetInt_String(data+pos); pos += 4;
            weight = GetInt_String(data+pos); pos += 4;
            face = GetInt_String(data+pos); pos += 4;
            nlen = data[pos++];
            memcpy(name, (char*)data+pos, nlen);
            pos += nlen;
            name[nlen] = '\0';
            anim = GetShort_String(data+pos); pos += 2;
            animspeed = data[pos++];
            nrof = GetInt_String(data+pos); pos += 4;
            type = GetShort_String(data+pos); pos += 2;
            update_item(tag, loc, name, weight, face, flags, anim, animspeed, nrof, type);
            item_actions(locate_item(tag));
        }
        if (pos > len) {
            LOG(LOG_WARNING, "common::common_item_cmd", "Overread buffer: %d > %d", pos, len);
        }
    }
}

/**
 *
 * @param data
 * @param len
 */
void Item2Cmd(unsigned char *data, int len) {
    common_item_command(data, len);
}

/**
 * Updates some attributes of an item
 *
 * @param data
 * @param len
 */
void UpdateItemCmd(unsigned char *data, int len) {
    int weight, loc, tag, face, sendflags, flags, pos = 0, nlen, anim;
    uint32 nrof;
    char name[MAX_BUF];
    item *ip, *env = NULL;
    uint8 animspeed;

    sendflags = data[0];
    pos += 1;
    tag = GetInt_String(data+pos);
    pos += 4;
    ip = locate_item(tag);
    if (!ip) {
/*
        fprintf(stderr, "Got update_item command for item we don't have (%d)\n", tag);
*/
        return;
    }

    /* Copy all of these so we can pass the values to update_item and
     * don't need to figure out which ones were modified by this function.
     */
    *name = '\0';
    loc = ip->env ? ip->env->tag : 0;
    weight = ip->weight*1000;
    face = ip->face;
    flags = ip->flagsval;
    anim = ip->animation_id;
    animspeed = ip->anim_speed;
    nrof = ip->nrof;

    if (sendflags&UPD_LOCATION) {
        loc = GetInt_String(data+pos);
        env = locate_item(loc);
        LOG(LOG_WARNING, "common::UpdateItemCmd", "Got tag of unknown object (%d) for new location", loc);
        pos += 4;
    }
    if (sendflags&UPD_FLAGS) {
        flags = GetInt_String(data+pos);
        pos += 4;
    }
    if (sendflags&UPD_WEIGHT) {
        weight = GetInt_String(data+pos);
        pos += 4;
    }
    if (sendflags&UPD_FACE) {
        face = GetInt_String(data+pos);
        pos += 4;
    }
    if (sendflags&UPD_NAME) {
        nlen = data[pos++];
        memcpy(name, (char*)data+pos, nlen);
        pos += nlen;
        name[nlen] = '\0';
    }
    if (pos > len) {
        LOG(LOG_WARNING, "common::UpdateItemCmd", "Overread buffer: %d > %d", pos, len);
        return; /* we have bad data, probably don't want to store it then */
    }
    if (sendflags&UPD_ANIM) {
        anim = GetShort_String(data+pos);
        pos += 2;
    }
    if (sendflags&UPD_ANIMSPEED) {
        animspeed = data[pos++];
    }
    if (sendflags&UPD_NROF) {
        nrof = (uint32)GetInt_String(data+pos);
        pos += 4;
    }
    /* update_item calls set_item_values which will then set the list
     * redraw flag, so we don't need to do an explicit redraw here.  Actually,
     * calling update_item is a little bit of overkill, since we
     * already determined some of the values in this function.
     */
    update_item(tag, loc, name, weight, face, flags, anim, animspeed, nrof, ip->type);
    item_actions(locate_item(tag));
}

/**
 *
 * @param data
 * @param len
 */
void DeleteItem(unsigned char *data, int len) {
    int pos = 0, tag;

    while (pos < len) {
        item *op;

        tag = GetInt_String(data+pos); pos += 4;
        op = locate_item(tag);
        if (op != NULL) {
            remove_item(op);
        } else {
            LOG(LOG_WARNING, "common::DeleteItem", "Cannot find tag %d", tag);
        }
    }
    if (pos > len) {
        LOG(LOG_WARNING, "common::DeleteItem", "Overread buffer: %d > %d", pos, len);
    }
}

/**
 *
 * @param data
 * @param len
 */
void DeleteInventory(unsigned char *data, int len) {
    int tag;
    item *op;

    (void)len; /* __UNUSED__ */

    tag = atoi((const char*)data);
    op = locate_item(tag);
    if (op != NULL) {
        remove_item_inventory(op);
    } else {
        LOG(LOG_WARNING, "common::DeleteInventory", "Invalid tag: %d", tag);
    }
}

/****************************************************************************/

/**
 * @defgroup SCSpellCommands Server->Client spell command functions.
 * @{
 */

/**
 *
 * @param data
 * @param len
 */
void AddspellCmd(unsigned char *data, int len) {
    uint8 nlen;
    uint16 mlen, pos = 0;
    Spell *newspell, *tmp;

    while (pos < len) {
        newspell = calloc(1, sizeof(Spell));

        /* Get standard spell information (spellmon 1)
         */
        newspell->tag = GetInt_String(data+pos); pos += 4;
        newspell->level = GetShort_String(data+pos); pos += 2;
        newspell->time = GetShort_String(data+pos); pos += 2;
        newspell->sp = GetShort_String(data+pos); pos += 2;
        newspell->grace = GetShort_String(data+pos); pos += 2;
        newspell->dam = GetShort_String(data+pos); pos += 2;
        newspell->skill_number = GetChar_String(data+pos); pos += 1;
        newspell->path = GetInt_String(data+pos); pos += 4;
        newspell->face = GetInt_String(data+pos); pos += 4;
        nlen = GetChar_String(data+pos); pos += 1;
        strncpy(newspell->name, (char*)data+pos, nlen); pos += nlen;
        newspell->name[nlen] = '\0'; /* to ensure we are null terminated */
        mlen = GetShort_String(data+pos); pos += 2;
        strncpy(newspell->message, (char*)data+pos, mlen); pos += mlen;
        newspell->message[mlen] = '\0'; /* to ensure we are null terminated */

        if (spellmon_level < 2) {

          /* The server is not sending spellmon 2 extended information, so
           * initialize the spell data fields as unused/empty.
           */
          newspell->usage = 0;
          newspell->requirements[0] = '\0';

        } else if (pos < len) {

          /* The server is sending extended spell information (spellmon 2) so
           * process it.
           */
          newspell->usage = GetChar_String(data+pos); pos += 1;
          nlen = GetChar_String(data+pos); pos += 1;
          strncpy(newspell->requirements, (char*) data+pos, nlen); pos += nlen;
          newspell->requirements[nlen] = '\0'; /* Ensure null-termination */
        }

        /* Compute the derived spell information.
         */
        newspell->skill = skill_names[newspell->skill_number-CS_STAT_SKILLINFO];

        /* Add the spell to the player struct.
         */
        if (!cpl.spelldata) {
            cpl.spelldata = newspell;
        } else {
            for (tmp = cpl.spelldata; tmp->next; tmp = tmp->next)
                ;
            tmp->next = newspell;
        }
        /* Check to see if there are more spells to add.
         */
    }
    if (pos > len) {
        LOG(LOG_WARNING, "common::AddspellCmd", "Overread buffer: %d > %d", pos, len);
    }
    cpl.spells_updated = 1;
}

/**
 *
 * @param data
 * @param len
 */
void UpdspellCmd(unsigned char *data, int len) {
    int flags, tag, pos = 0;
    Spell *tmp;

    if (!cpl.spelldata) {
        LOG(LOG_WARNING, "common::UpdspellCmd", "I know no spells to update");
        return;
    }

    flags = GetChar_String(data+pos); pos += 1;
    tag = GetInt_String(data+pos); pos += 4;
    for (tmp = cpl.spelldata; tmp && tmp->tag != tag; tmp = tmp->next)
        ;
    if (!tmp) {
        LOG(LOG_WARNING, "common::UpdspellCmd", "Invalid tag: %d", tag);
        return;
    }
    if (flags&UPD_SP_MANA) {
        tmp->sp = GetShort_String(data+pos); pos += 2;
    }
    if (flags&UPD_SP_GRACE) {
        tmp->grace = GetShort_String(data+pos); pos += 2;
    }
    if (flags&UPD_SP_DAMAGE) {
        tmp->dam = GetShort_String(data+pos); pos += 2;
    }
    if (pos > len) {
        LOG(LOG_WARNING, "common::UpdspellCmd", "Overread buffer: %d > %d", pos, len);
    }
    cpl.spells_updated = 1;
}

/**
 *
 * @param data
 * @param len
 */
void DeleteSpell(unsigned char *data, int len) {
    int tag;
    Spell *tmp, *target;

    if (!cpl.spelldata) {
        LOG(LOG_WARNING, "common::DeleteSpell", "I know no spells to delete");
        return;
    }

    tag = GetInt_String(data);
    /* special case, the first spell is the one removed */
    if (cpl.spelldata->tag == tag) {
        target = cpl.spelldata;
        if (target->next) {
            cpl.spelldata = target->next;
        } else {
            cpl.spelldata = NULL;
        }
        free(target);
        return;
    }

    for (tmp = cpl.spelldata; tmp->next && tmp->next->tag != tag; tmp = tmp->next)
        ;
    if (!tmp->next) {
        LOG(LOG_WARNING, "common::DeleteSpell", "Invalid tag: %d", tag);
        return;
    }
    target = tmp->next;
    if (target->next) {
        tmp->next = target->next;
    } else {
        tmp->next = NULL;
    }
    free(target);
    cpl.spells_updated = 1;
}

/****************************************************************************/

/**
 * @} */ /* EndOf SCSpellCommands
 */

/**
 * @defgroup SCMapCommands Server->Client map command functions.
 * @{
 */

/**
 *
 * @param data
 * @param len
 */
void NewmapCmd(unsigned char *data, int len) {
    (void)data; /* __UNUSED__ */
    (void)len; /* __UNUSED__ */

    mapdata_newmap();
}

/* This is the common processing block for the map1 and
 * map1a protocol commands.  The map1a mieks minor extensions
 * and are easy to deal with inline (in fact, this code
 * doesn't even care what rev is - just certain bits will
 * only bet set when using the map1a command.
 * rev is 0 for map1,
 * 1 for map1a.  It conceivable that there could be future
 * revisions.
 */

/* NUM_LAYERS should only be used for the map1{a} which only
 * has a few layers.  Map2 has 10 layers.  However, some of the
 * map1 logic requires this to be set right.
 */
#define NUM_LAYERS (MAP1_LAYERS-1)

/**
 *
 * @param data
 * @param len
 */
void Map2Cmd(unsigned char *data, int len) {
    int mask, x, y, pos = 0, space_len, value;
    uint8 type;

    display_map_startupdate();
    /* Not really using map1 protocol, but some draw logic differs from
     * the original draw logic, and map2 is closest.
     */
    while (pos < len) {
        mask = GetShort_String(data+pos); pos += 2;
        x = ((mask>>10)&0x3f)-MAP2_COORD_OFFSET;
        y = ((mask>>4)&0x3f)-MAP2_COORD_OFFSET;

        /* This is a scroll then.  Go back and fetch another coordinate */
        if (mask&0x1) {
            mapdata_scroll(x, y);
            continue;
        }

        if (x<0) {
            LOG(LOG_WARNING, "commands.c::Map2Cmd", "got negative x!");
            x = 0;
        } else if (x >= MAX_VIEW) {
            LOG(LOG_WARNING, "commands.c::Map2Cmd", "got x >= MAX_VIEW!");
            x = MAX_VIEW - 1;
        }

        if (y<0) {
            LOG(LOG_WARNING, "commands.c::Map2Cmd", "got negative y!");
            y = 0;
        } else if (y >= MAX_VIEW) {
            LOG(LOG_WARNING, "commands.c::Map2Cmd", "got y >= MAX_VIEW!");
            y = MAX_VIEW - 1;
        }

        assert(0 <= x && x < MAX_VIEW);
        assert(0 <= y && y < MAX_VIEW);
        /* Clear the old cell data if needed. Used to be done in
         * mapdata_set_face_layer() however that caused darkness to only
         * work if sent after the layers.
         */
        mapdata_clear_old(x, y);

        /* Inner loop is for the data on the space itself */
        while (pos < len) {
            type = data[pos++];
            /* type == 255 means nothing more for this space */
            if (type == 255) {
                mapdata_set_check_space(x, y);
                break;
            }
            space_len = type>>5;
            type &= 0x1f;
            /* Clear the space */
            if (type == MAP2_TYPE_CLEAR) {
                mapdata_clear_space(x, y);
                continue;
            } else if (type == MAP2_TYPE_DARKNESS) {
                value = data[pos++];
                mapdata_set_darkness(x, y, value);
                continue;
            } else if (type >= MAP2_LAYER_START && type < MAP2_LAYER_START+MAXLAYERS) {
                int layer, opt;

                /* This is face information for a layer. */
                layer = type&0xf;

                if (layer < 0) {
                    LOG(LOG_WARNING, "commands.c::Map2Cmd", "got negative layer!");
                    layer = 0;
                } else if (layer >= MAXLAYERS) {
                    LOG(LOG_WARNING, "commands.c::Map2Cmd", "got layer >= MAXLAYERS!");
                    layer = MAXLAYERS - 1;
                }
                assert(0 <= layer && layer < MAXLAYERS);

                /* This is the face */
                value = GetShort_String(data+pos); pos += 2;
                if (!(value&FACE_IS_ANIM)) {
                    mapdata_set_face_layer(x, y, value, layer);
                }

                if (space_len > 2) {
                    opt = data[pos++];
                    if (value&FACE_IS_ANIM) {
                        /* Animation speed */
                        mapdata_set_anim_layer(x, y, value, opt, layer);
                    } else {
                        /* Smooth info */
                        mapdata_set_smooth(x, y, opt, layer);
                    }
                }
                /* Currently, if 4 bytes, must be a smooth byte */
                if (space_len > 3) {
                    opt = data[pos++];
                    mapdata_set_smooth(x, y, opt, layer);
                }
                continue;
            } /* if image layer */
        } /* while pos<len inner loop for space */
    } /* While pos<len outer loop */
    mapupdatesent = 0;
    display_map_doneupdate(FALSE, FALSE);
}

/**
 * Scrolls the map on the client by some amount.
 *
 * @param data
 * @param len
 */
void map_scrollCmd(char *data, int len) {
    int dx, dy;
    char *buf;

    (void)len; /* __UNUSED__ */

    dx = atoi(data);
    buf = strchr(data, ' ');
    if (!buf) {
        LOG(LOG_WARNING, "common::map_scrollCmd", "Got short packet.");
        return;
    }
    buf++;
    dy = atoi(buf);

    display_map_startupdate();
    mapdata_scroll(dx, dy);
    display_map_doneupdate(FALSE, TRUE);
}

/**
 * Extract smoothing infos from an extendedmapinfo packet part data is located
 * at the beginning of the smooth datas
 *
 * @param data
 * @param len
 * @param x
 * @param y
 * @param layer
 */
int ExtSmooth(unsigned char *data, int len, int x, int y, int layer) {
    static int dx[8] = { 0, 1, 1, 1, 0, -1, -1, -1, };
    static int dy[8] = { -1, -1, 0, 1, 1, 1, 0, -1, };
    int i, rx, ry;
    int newsm;

    if (len < 1) {
        return 0;
    }

    x += pl_pos.x;
    y += pl_pos.y;
    newsm = GetChar_String(data);

    if (the_map.cells[x][y].smooth[layer] != newsm) {
        for (i = 0; i < 8; i++) {
            rx = x+dx[i];
            ry = y+dy[i];
            if (rx < 0 || ry < 0 || the_map.x <= rx || the_map.y <= ry) {
                continue;
            }
            the_map.cells[x][y].need_resmooth = 1;
        }
    }
    the_map.cells[x][y].smooth[layer] = newsm;
    return 1;/*Cause smooth infos only use 1 byte*/
}

/**
 * Handle MapExtended command
 * Warning! if you add commands to extended, take care that the 'layer'
 * argument of main loop is the opposite of the layer of the map so if you
 * reference a layer, use NUM_LAYERS-layer.
 *
 * @param data
 * @param len
 */
void MapExtendedCmd(unsigned char *data, int len) {
    int mask, x, y, pos = 0, layer;
    int noredraw = 0;
    int hassmooth = 0;
    int entrysize;
    int startpackentry;

    if (!mapupdatesent) {
        display_map_startupdate();
    }
    mapupdatesent = 1;
    mask = GetChar_String(data+pos); pos += 1;
    if (mask&EMI_NOREDRAW) {
        noredraw = 1;
    }
    if (mask&EMI_SMOOTH) {
        hassmooth = 1;
    }
    while (mask&EMI_HASMOREBITS) {
        /*There may be bits we ignore about*/
        mask = GetChar_String(data+pos);
        pos += 1;
    }
    entrysize = GetChar_String(data+pos);
    pos = pos+1;

    while (pos+entrysize+2 <= len) {
        mask = GetShort_String(data+pos); pos += 2;
        x = (mask>>10)&0x3f;
        y = (mask>>4)&0x3f;
        for (layer = NUM_LAYERS; layer >= 0; layer--) {
            if (mask&(1<<layer)) {
                /*handle an entry*/
                if (pos+entrysize > len) { /*erroneous packet*/
                    break;
                }
                startpackentry = pos;
                /* If you had extended infos to the server, this
                 * is where, in the client, you may add your code
                 */
                if (hassmooth) {
                    pos = pos+ExtSmooth(data+pos, len-pos, x, y, NUM_LAYERS-layer);
                }
                /* continue with other if you add new extended
                 * infos to server
                 */

                /* Now point to the next data */
                pos = startpackentry+entrysize;
            }
        }
    }
    if (!noredraw) {
        display_map_doneupdate(FALSE, FALSE);
        mapupdatesent = 0;
    }
}

/**
 *
 * @param data
 * @param len
 */
void MagicMapCmd(unsigned char *data, int len) {
    unsigned char *cp;
    int i;

    /* First, extract the size/position information. */
    if (sscanf((const char*)data, "%hd %hd %hd %hd", &cpl.mmapx, &cpl.mmapy, &cpl.pmapx, &cpl.pmapy) != 4) {
        LOG(LOG_WARNING, "common::MagicMapCmd", "Was not able to properly extract magic map size, pos");
        return;
    }

    /* Now we need to find the start of the actual data.  There are 4
     * space characters we need to skip over.
     */
    for (cp = data, i = 0; i < 4 && cp < data+len; cp++) {
        if (*cp == ' ') {
            i++;
        }
    }
    if (i != 4) {
        LOG(LOG_WARNING, "common::MagicMapCmd", "Was unable to find start of magic map data");
        return;
    }
    i = len-(cp-data); /* This should be the number of bytes left */
    if (i != cpl.mmapx*cpl.mmapy) {
        LOG(LOG_WARNING, "common::MagicMapCmd", "Magic map size mismatch.  Have %d bytes, should have %d",
            i, cpl.mmapx*cpl.mmapy);
        return;
    }
    free(cpl.magicmap);
    cpl.magicmap = malloc(cpl.mmapx*cpl.mmapy);
    /* Order the server puts it in should be just fine.  Note that
     * the only requirement that this works is that magicmap by 8 bits,
     * being that is the size specified in the protocol and what the
     * server sends us.
     */
    memcpy(cpl.magicmap, cp, cpl.mmapx*cpl.mmapy);
    cpl.showmagic = 1;
    draw_magic_map();
}

/**
 * @} */ /* EndOf SCMapCommands
 */

/**
 *
 * @param data
 * @param len
 */
void SinkCmd(unsigned char *data, int len) {
}

/**
 * Got a tick from the server.  We currently don't care what tick number it
 * is, but just have the code in case at some time we do.
 *
 * @param data
 * @param len
 */
void TickCmd(uint8 *data, int len) {

    tick = GetInt_String(data);

    /* Up to the specific client to decide what to do */
    client_tick(tick);
}

/**
 * Server gives us current player's pickup.
 *
 * @param data
 * buffer sent by server.
 * @param len
 * length of data.
 */
void PickupCmd(uint8 *data, int len) {
    uint32 pickup = GetInt_String(data);
    client_pickup(pickup);
}

/**
 * Handles a failure return from the server.
 *
 * @param data
 * buffer sent by server.
 * @param len
 * length of data.
 */
void FailureCmd(char *buf, int len) {
    char *cp;

    /* The format of the buffer is 'command error message'.  We need to
     * extract the failed command, and then pass in the error message
     * to the appropriate handler.  So find the space, set it to null.
     * in that way, buf is now just the failure command, and cp is the
     * message.
     */
    cp = strchr(buf,' ');
    if (!cp) return;

    *cp = 0;
    cp++;

    if (!strcmp(buf,"accountlogin")) {
        account_login_failure(cp);
    }
    else if (!strcmp(buf,"accountnew")) {
        account_creation_failure(cp);
    }
    else if (!strcmp(buf,"accountaddplayer")) {
        account_add_character_failure(cp);
    }
    else if (!strcmp(buf,"createplayer")) {
        create_new_character_failure(cp);
    }
    else
        /* This really is an error - if this happens it menas the server failed
         * to process a request that the client made - the client should be able
         * to handle failures for all request types it makes.  But this is also a problem
         * in that it means that the server is waiting for a correct response, and
         * if we do not display anything, the player is unlikely to know this.
         */
        LOG(LOG_ERROR, "common::FailureCmd", "Got a failure response we can not handle: %s:%s",
            buf, cp);
}

/**
 * This handles the accountplayers command
 */
void AccountPlayersCmd(char *buf, int len) {

    int num_characters, level, pos, flen, faceno;
    char name[MAX_BUF], class[MAX_BUF], race[MAX_BUF], 
        face[MAX_BUF], party[MAX_BUF], map[MAX_BUF];


    /* This is called first so it can clear out the existing
     * data store.
     */
    choose_character_init();

    level=0;
    name[0]=0;
    class[0]=0;
    race[0]=0;
    face[0]=0;
    party[0]=0;
    map[0]=0;
    faceno=0;

    /* We don't do anything with this right now */
    num_characters=buf[0];
    pos=1;
    while (pos < len) {
        flen = buf[pos];
        /* flen == 0 is to note that we got end of character data */
        if (flen == 0) {
            update_character_choose(name, class, race, face, party, map, level, faceno);
            /* blank all the values - it is no sure thing that the
             * next character will fill all these in.
             */
            level=0;
            name[0]=0;
            class[0]=0;
            race[0]=0;
            face[0]=0;
            party[0]=0;
            map[0]=0;
            faceno=0;
            pos++;
            continue;
        }
        pos++;
        if ((pos +flen) > len || flen>=MAX_BUF) {
            LOG(LOG_ERROR,"commands.c:AccountPlayerCmd", "data overran buffer");
            return;
        }
        switch (buf[pos]) {
        case ACL_NAME:
            strncpy(name, buf + pos +1, flen-1);
            name[flen-1] = 0;
            break;

        case ACL_CLASS:
            strncpy(class, buf + pos +1, flen-1);
            class[flen-1] = 0;
            break;

        case ACL_RACE:
            strncpy(race, buf + pos +1, flen-1);
            race[flen-1] = 0;
            break;
        
        case ACL_FACE:
            strncpy(face, buf + pos +1, flen-1);
            face[flen-1] = 0;
            break;

        case ACL_PARTY:
            strncpy(party, buf + pos +1, flen-1);
            party[flen-1] = 0;
            break;

        case ACL_MAP:
            strncpy(map, buf + pos +1, flen-1);
            map[flen-1] = 0;
            break;

        case ACL_LEVEL:
            level = GetShort_String(buf+pos+1);
            break;
        case ACL_FACE_NUM:
            faceno = GetShort_String(buf+pos+1);
            break;
        }
        pos += flen;
    }
}
