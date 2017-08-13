/*
 * static char *rcsid_common_external_h =
 *   "$Id$";
 */
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2001,2006 Mark Wedel & Crossfire Development Team

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
 * @file common/external.h
 * Contains external calls that the common area makes callbacks to.  This was
 * really a quick hack done to allow some separation.  Really, these should be
 * set via callbacks that the client can make to the library.  Many of these
 * probably should never really be callbacks in any case, or be more general.
 */

/* Sound functions */
extern void SoundCmd(unsigned char *data, int len);
extern void Sound2Cmd(unsigned char *data, int len);
extern void MusicCmd(const char *data, int len);

/* Map window related functions */
extern void resize_map_window(int x, int y);
extern void display_map_addbelow(long x, long y, long face);
extern void display_map_doneupdate(int redraw, int notice);
extern int display_mapscroll(int dx, int dy);
extern void draw_magic_map(void);

/* Info related functions */
extern void draw_prompt(const char *str);
extern void draw_ext_info(int orig_color, int type, int subtype, const char *message);
extern void x_set_echo(void);
extern void menu_clear(void);
extern int get_info_width(void);

/* Stats related commands */
extern void draw_stats(int redraw);
extern void draw_message_window(int redraw);

/* this should really just set a field in the stats, and let the
 * client figure the new weight limit out
 */
extern void set_weight_limit(guint32 wlim);

/* Image related functions */
extern int display_willcache(void);
extern int create_and_rescale_image_from_data(Cache_Entry *ce, int pixmap_num, guint8 *rgba_data, int width, int height);
extern guint8 *png_to_data(guint8 *data, int len, guint32 *width, guint32 *height);
extern int associate_cache_entry(Cache_Entry *ce, int pixnum);
extern void image_update_download_status(int start, int end, int total);
extern void get_map_image_size(int face, guint8 *w, guint8 *h);
extern void addsmooth(guint16 face, guint16 smooth_face);

/* Item related commands */
extern void open_container(item *op);
extern void close_container(item *op);

/* Keybinding relatated commands - this probably should not be a callback */
extern void bind_key(const char *params);
extern void unbind_key(const char *params);
extern void keybindings_init(const char *character_name);

/* Misc commands */
extern void save_winpos(void);
extern void save_defaults(void);
extern void command_show(const char *params);
extern void client_tick(guint32 tick);
extern void client_pickup(guint32 pickup);

/* Account Login Functions */
extern void start_login(int method);
extern void hide_all_login_windows(void);
extern void account_login_failure(char *message);
extern void account_creation_failure(char *message);
extern void account_add_character_failure(char *message);
extern void account_change_password_failure(char *message);
extern void create_new_character_failure(char *message);
extern void choose_character_init(void);
extern void update_character_choose(const char *name, const char *class,
                             const char *race, const char *face,
                             const char *party, const char *map,
                             int level, int faceno);
extern void update_login_info(int type);

/* Character Creation Functions */
extern void new_char_window_update_info();
extern void starting_map_update_info();
