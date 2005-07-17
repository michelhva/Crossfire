/*
 * static char *rcsid_funcpoint_h =
 *   "$Id$";
 */

/*
    CrossFire, A Multiplayer game for X-windows

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

    The author can be reached via e-mail to mark@pyramid.com.

    This file was made after an idea by vidarl@ifi.uio.no
*/

#ifndef FUNCPOINT_H
#define FUNCPOINT_H

/*
 * Some function types
 */

typedef void (*type_move_apply_func) (object *, object *, object *);
typedef void (*type_func_int)(int);
typedef void (*type_func_int_int)(int,int);
typedef void (*type_func_void)(void);
typedef void (*type_func_map)(mapstruct *);
typedef void (*type_func_map_char)(mapstruct *, char *);
typedef void (*type_func_int_map_char)(int, mapstruct *, char *);
typedef void (*type_func_ob)(object *);
typedef void (*type_func_ob_char)(object *, char *);
typedef void (*type_func_ob_cchar)(object *, const char *);
typedef void (*type_func_int_int_ob_cchar)(int, int, object *, const char *);
typedef void (*type_func_ob_ob)(object *, object *);
typedef void (*type_func_ob_int)(object *, int);
typedef int (*type_int_func_ob_ob)(object *, object *);
typedef void (*type_func_char_int)(char *, int);
typedef void (*type_func_int_ob_ob)(int, object *, object *);
typedef void (*type_func_player_int)(player *, int);
typedef void (*type_func_dragon_gain)(object *who, int atnr, int level);
typedef void (*type_func_char)(char *);
typedef object* (*type_ob_func_ob_int)(object *, int);

/*
 * These function-pointers are defined in common/glue.c
 * The functions used to set and initialise them are also there.
 *
 * Massive change. Those functions are just defined, no callback, and they should be implemented.
 * This means glue.c code & such can go away almost entirely.
 * Ryo 2005-07-15
 */

extern void	move_apply(object *, object *, object *);
extern void	draw_info(int, int, object *, const char *);
extern void	emergency_save(int);
extern void	clean_tmp_files();
extern void	fix_auto_apply(mapstruct *);
extern void	init_blocksview_players();
extern void	monster_check_apply(object *, object *);
extern void	process_active_maps();
extern void	remove_friendly_object(object *);
extern void	update_buttons(mapstruct *);
extern void	info_map(int, mapstruct *, char *);
extern void	move_teleporter(object *);
extern void	move_firewall(object *);
extern void	move_creator(object *);
extern void move_marker(object *);
extern void	move_duplicator(object *);
extern void trap_adjust(object *, int);
extern void	esrv_send_item(object *, object *);
extern void	esrv_del_item(player *, int);
extern void	esrv_update_item(int, object *, object *);
extern void	set_darkness_map(mapstruct *m);
extern void dragon_ability_gain(object *, int, int);
extern void	weather_effect(const char *);
extern object *	find_skill_by_number(object *, int);

#endif
