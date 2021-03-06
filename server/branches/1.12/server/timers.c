/*
    CrossFire, A Multiplayer game for X-windows

    Copyright (C) 2001-2007 Yann Chachkoff & Crossfire Development Team

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

    The authors can be reached via e-mail at crossfire-devel@real-time.com
*/

/**
 * @file
 * This handles custom timers. See @ref page_timers.
 *
 * @page page_timers Custom timers
 *
 * It is possible, through the functions in the @ref timers.c file, to create
 * custom timers that get activated after some specified time.
 *
 * A timer is associated to a specific ::object, and is given a delay, either
 * in server ticks or in seconds. When the delay expires, and if the item still
 * is valid, an ::EVENT_TIMER is generated for the specific object. Actual handling
 * is thus delegated to plugins.
 *
 * Note that timers are one shot only, they reset after they activate.
 */

#include <timers.h>
#ifndef __CEXTRACT__
#include <sproto.h>
#endif

/* Extern in header. */
cftimer timers_table[MAX_TIMERS];

static void cftimer_process_event(tag_t ob_tag);

/**
 * Processes all timers.
 */
void cftimer_process_timers(void) {
    int i;

    for (i = 0; i < MAX_TIMERS; i++) {
        if (timers_table[i].mode == TIMER_MODE_CYCLES) {
            timers_table[i].delay--;
            if (timers_table[i].delay == 0) {
                /* Call object timer event */
                timers_table[i].mode = TIMER_MODE_DEAD;
                cftimer_process_event(timers_table[i].ob_tag);
            }
        } else if (timers_table[i].mode == TIMER_MODE_SECONDS) {
            if (timers_table[i].delay <= seconds()) {
                /* Call object timer event */
                timers_table[i].mode = TIMER_MODE_DEAD;
                cftimer_process_event(timers_table[i].ob_tag);
            }
        }
    }
}

/**
 * Triggers the ::EVENT_TIMER of the given object.
 *
 * @param ob_tag
 * object tag to use.
 */
static void cftimer_process_event(tag_t ob_tag) {
    object *ob = find_object(ob_tag);

    if (ob)
        execute_event(ob, EVENT_TIMER, NULL, NULL, NULL, SCRIPT_FIX_ALL);
}

/**
 * Creates a new timer.
 * @param id
 * desired timer identifier.
 * @param delay
 * desired timer delay.
 * @param ob
 * object that will be linked to this timer. Should have an ::EVENT_TIMER handler.
 * @param mode
 * unit for delay, should be ::TIMER_MODE_SECONDS or ::TIMER_MODE_CYCLES. See timers.h.
 * @retval ::TIMER_ERR_NONE
 * timer was successfully created.
 * @retval ::TIMER_ERR_ID
 * invalid ID.
 * @retval ::TIMER_ERR_MODE
 * invalid mode.
 * @retval ::TIMER_ERR_OBJ
 * ob is NULL or has no ::EVENT_TIMER handler.
 */
int cftimer_create(int id, long delay, object *ob, int mode) {
    if (id >= MAX_TIMERS)
        return TIMER_ERR_ID;
    if (id < 0)
        return TIMER_ERR_ID;
    if (timers_table[id].mode != TIMER_MODE_DEAD)
        return TIMER_ERR_ID;
    if ((mode != TIMER_MODE_SECONDS) && (mode != TIMER_MODE_CYCLES))
        return TIMER_ERR_MODE;
    if (ob == NULL)
        return TIMER_ERR_OBJ;
    if (find_obj_by_type_subtype(ob, EVENT_CONNECTOR, EVENT_TIMER) == NULL)
        return TIMER_ERR_OBJ;
    timers_table[id].mode = mode;
    timers_table[id].ob_tag = ob->count;
    if (mode == TIMER_MODE_CYCLES)
        timers_table[id].delay = delay;
    else
        timers_table[id].delay = seconds()+delay;
    return TIMER_ERR_NONE;
}

/**
 * Destroys an existing timer.
 * @param id
 * identifier of the timer to destroy.
 * @retval ::TIMER_ERR_NONE
 * no problem encountered.
 * @retval ::TIMER_ERR_ID
 * unknown id - timer not found or invalid.
 */
int cftimer_destroy(int id) {
    if (id >= MAX_TIMERS)
        return TIMER_ERR_ID;
    if (id < 0)
        return TIMER_ERR_ID;
    timers_table[id].mode = TIMER_MODE_DEAD;
    return TIMER_ERR_NONE;
}

/**
 * Finds a free ID for a new timer.
 * @retval ::TIMER_ERR_ID
 * no free ID available.
 * @retval >0
 * an available ID.
 */
int cftimer_find_free_id(void) {
    int i;

    for (i = 0; i < MAX_TIMERS; i++) {
        if (timers_table[i].mode == TIMER_MODE_DEAD)
            return i;
    }
    return TIMER_ERR_ID;
}

/**
 * Initialize timers.
 */
void cftimer_init(void) {
    memset(&timers_table[0], 0, sizeof(cftimer)*MAX_TIMERS);
}
