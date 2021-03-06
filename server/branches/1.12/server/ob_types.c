/*
 * static char *rcsid_ob_types =
 *   "$Id: build_map.c 5057 2006-10-29 07:50:09Z mwedel $";
 */
/*
    CrossFire, A Multiplayer game for X-windows

    Copyright (C) 2006 Mark Wedel & Crossfire Development Team
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

    The authors can be reached via e-mail to crossfire-devel@real-time.com
*/

/**
 * @file
 * Those functions deal with the object/type system.
 */

#include <global.h>
#include <ob_types.h>
#include <ob_methods.h>

#ifndef __CEXTRACT__
#include <sproto.h>
#endif

/**
 * Calls the intialization functions for all individual types.
 * @todo this should probably be moved to a file in the types/ directory, to separate types and server.
 */
void register_all_ob_types(void) {
    /* init_type_foobar() here, where foobar is for a type. In other words,
     * from here, call functions that register object methods for types.
     */
    init_type_altar();
    init_type_armour_improver();
    init_type_arrow();
    init_type_blindness();
    init_type_book();
    init_type_button();
    init_type_cf_handle();
    init_type_check_inv();
    init_type_clock();
    init_type_container();
    init_type_converter();
    init_type_creator();
    init_type_deep_swamp();
    init_type_detector();
    init_type_director();
    init_type_duplicator();
    init_type_exit();
    init_type_food();
    init_type_gate();
    init_type_hole();
    init_type_identify_altar();
    init_type_lamp();
    init_type_lighter();
    init_type_marker();
    init_type_mood_floor();
    init_type_peacemaker();
    init_type_pedestal();
    init_type_player_changer();
    init_type_player_mover();
    init_type_poison();
    init_type_poisoning();
    init_type_potion();
    init_type_power_crystal();
    init_type_rune();
    init_type_savebed();
    init_type_scroll();
    init_type_shop_inventory();
    init_type_shop_mat();
    init_type_sign();
    init_type_skillscroll();
    init_type_spell_effect();
    init_type_spellbook();
    init_type_spinner();
    init_type_teleporter();
    init_type_thrown_object();
    init_type_transport();
    init_type_trap();
    init_type_trapdoor();
    init_type_treasure();
    init_type_trigger();
    init_type_trigger_altar();
    init_type_trigger_button();
    init_type_trigger_pedestal();
    init_type_weapon_improver();
}
