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
 * This file covers the pickup menu items.  We only implement the new pickup
 * code - it seems to me that it should be able to cover everything the old
 * pickup mode does.
 */

#include "client.h"

#include <gtk/gtk.h>

#include "main.h"
#include "image.h"
#include "gtk2proto.h"

typedef struct {
    GtkWidget   *menuitem;
    guint32      pickup_mode;
} PickupMapping;

#define MAX_PICKUPS 50

PickupMapping pickup_mapping[MAX_PICKUPS];
static int num_pickups=0;

/*
 * Definitions for detailed pickup descriptions.  The objective is to define
 * intelligent groups of items that the user can pick up or leave as he likes.
 */

/* High bit as flag for new pickup options */
#define PU_NOTHING              0x00000000

#define PU_DEBUG                0x10000000
#define PU_INHIBIT              0x20000000
#define PU_STOP                 0x40000000
#define PU_NEWMODE              0x80000000

#define PU_RATIO                0x0000000F

#define PU_FOOD                 0x00000010
#define PU_DRINK                0x00000020
#define PU_VALUABLES            0x00000040
#define PU_BOW                  0x00000080

#define PU_ARROW                0x00000100
#define PU_HELMET               0x00000200
#define PU_SHIELD               0x00000400
#define PU_ARMOUR               0x00000800

#define PU_BOOTS                0x00001000
#define PU_GLOVES               0x00002000
#define PU_CLOAK                0x00004000
#define PU_KEY                  0x00008000

#define PU_MISSILEWEAPON        0x00010000
#define PU_ALLWEAPON            0x00020000
#define PU_MAGICAL              0x00040000
#define PU_POTION               0x00080000

#define PU_SPELLBOOK            0x00100000
#define PU_SKILLSCROLL          0x00200000
#define PU_READABLES            0x00400000
#define PU_MAGIC_DEVICE         0x00800000

#define PU_NOT_CURSED           0x01000000
#define PU_JEWELS               0x02000000
#define PU_FLESH                0x04000000
#define PU_CONTAINERS           0x08000000

static unsigned int pmode=0, no_recurse=0;

/**
 * Handles the pickup operations.  Unfortunately, it isn't easy (possible?)
 * in layout to attach values to the menu items.
 *
 * @param on is TRUE if the button is activated, 0 if it is off.
 * @param val is the PU_ bitmasks to set/clear.
 */
static void new_menu_pickup(int on, int val)
{
    char modestr[128];

    if (no_recurse) {
        return;
    }

    if (on) {
        pmode |= val | PU_NEWMODE;
    } else {
        pmode &= ~val;
    }

    draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_NOTICE,
                  "To set this pickup mode to a key, use:");

    snprintf(modestr, sizeof(modestr), "bind pickup %u",pmode);
    draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_NOTICE, modestr);
    snprintf(modestr, sizeof(modestr), "pickup %u",pmode);
    send_command(modestr, -1, 0);
}

void
on_menu_dont_pickup_activate           (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_INHIBIT);
}

void
on_menu_stop_before_pickup_activate    (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_STOP);
}

/***************************************************************************
 * armor pickup options
 **************************************************************************/

void
on_menu_body_armor_activate            (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_ARMOUR);
}

void
on_menu_boots_activate                 (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_BOOTS);
}

void
on_menu_cloaks_activate                (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_CLOAK);
}

void
on_menu_gloves_activate                (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_GLOVES);
}

void
on_menu_helmets_activate               (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_HELMET);
}

void
on_menu_shields_activate               (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_SHIELD);
}

/***************************************************************************
 * Books submenu
 ****************************************************************************/

void
on_menu_skillscrolls_activate          (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_SKILLSCROLL);
}


void
on_menu_normal_book_scrolls_activate   (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_READABLES);
}


void
on_menu_spellbooks_activate            (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_SPELLBOOK);
}

/***************************************************************************/

void
on_menu_drinks_activate                (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_DRINK);
}

void
on_menu_food_activate                  (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_FOOD);
}

void
on_menu_keys_activate                  (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_KEY);
}

void
on_menu_magical_items_activate         (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_MAGICAL);
}

void
on_menu_potions_activate                (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_POTION);
}

void
on_menu_valuables_activate             (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_VALUABLES);
}

void
on_menu_wands_rods_horns_activate      (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_MAGIC_DEVICE);
}

void
on_menu_not_cursed_activate            (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_NOT_CURSED);
}

void
on_menu_jewels_activate                  (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_JEWELS);
}

void
on_menu_containers_activate                  (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_CONTAINERS);
}

void
on_menu_flesh_activate                  (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_FLESH);
}

/***************************************************************************
 * Weapons submenu
 ***************************************************************************/

void
on_menu_all_weapons_activate           (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_ALLWEAPON);
}

void
on_menu_missile_weapons_activate       (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_MISSILEWEAPON);
}

void
on_menu_bows_activate                  (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_BOW);
}

void
on_menu_arrows_activate                (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), PU_ARROW);
}

/***************************************************************************
 * Weight/value submenu
 ***************************************************************************/

void
on_menu_ratio_pickup_off_activate       (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 0);
}

void
on_menu_ratio_5_activate               (GtkMenuItem     *menuitem,
                                        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 1);
}

void
on_menu_ratio_10_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 2);
}

void
on_menu_ratio_15_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 3);
}

void
on_menu_ratio_20_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 4);
}

void
on_menu_ratio_25_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 5);
}

void
on_menu_ratio_30_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 6);
}

void
on_menu_ratio_35_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 7);
}

void
on_menu_ratio_40_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 8);
}

void
on_menu_ratio_45_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 9);
}

void
on_menu_ratio_50_activate               (GtkMenuItem     *menuitem,
        gpointer         user_data)
{
    new_menu_pickup(gtk_check_menu_item_get_active(GTK_CHECK_MENU_ITEM(menuitem)), 10);
}

/**
 * Maps the menuitem lists into pickup values.  In this way, client_pickup
 * knows what values to change.
 *
 * @param window_root
 */
void pickup_init(GtkWidget *window_root)
{
    static int has_init=0;

    /*
     * There isn't really any harm doing this multiple times, but isn't any
     * point either.
     */
    if (has_init) {
        return;
    }
    has_init=1;

    /*
     * The order here really doesn't make much difference.  I suppose order
     * could either be in pickup modes (PU_...) or the list of items in the
     * menu tree.  I chose the later, as easier to make sure all the items are
     * accounted for.
     *
     * In practice, with these values now set up, we could use a single
     * function to hande all the events from the menubar instead of the values
     * above - that function basically takes the structure that was clicked,
     * and finds the value in this array that corresponds to it.  But that code
     * currently works fine and isn't really outdated, so isn't a big reason to
     * change it.
     */

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "do_not_pickup"));
    pickup_mapping[num_pickups].pickup_mode = PU_INHIBIT;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "stop_before_pickup"));
    pickup_mapping[num_pickups].pickup_mode = PU_STOP;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "body_armor"));
    pickup_mapping[num_pickups].pickup_mode = PU_ARMOUR;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "boots"));
    pickup_mapping[num_pickups].pickup_mode = PU_BOOTS;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "cloaks"));
    pickup_mapping[num_pickups].pickup_mode = PU_CLOAK;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "gloves"));
    pickup_mapping[num_pickups].pickup_mode = PU_GLOVES;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "helmets"));
    pickup_mapping[num_pickups].pickup_mode = PU_HELMET;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "shields"));
    pickup_mapping[num_pickups].pickup_mode = PU_SHIELD;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "skillscrolls"));
    pickup_mapping[num_pickups].pickup_mode = PU_SKILLSCROLL;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "normal_book_scrolls"));
    pickup_mapping[num_pickups].pickup_mode = PU_READABLES;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "spellbooks"));
    pickup_mapping[num_pickups].pickup_mode = PU_SPELLBOOK;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "drinks"));
    pickup_mapping[num_pickups].pickup_mode = PU_DRINK;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "food"));
    pickup_mapping[num_pickups].pickup_mode = PU_FOOD;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "flesh"));
    pickup_mapping[num_pickups].pickup_mode = PU_FLESH;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "keys"));
    pickup_mapping[num_pickups].pickup_mode = PU_KEY;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "magical_items"));
    pickup_mapping[num_pickups].pickup_mode = PU_MAGICAL;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "potions"));
    pickup_mapping[num_pickups].pickup_mode = PU_POTION;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "valuables"));
    pickup_mapping[num_pickups].pickup_mode = PU_VALUABLES;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "wands_rods_horns"));
    pickup_mapping[num_pickups].pickup_mode = PU_MAGIC_DEVICE;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "jewels"));
    pickup_mapping[num_pickups].pickup_mode = PU_JEWELS;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "containers"));
    pickup_mapping[num_pickups].pickup_mode = PU_CONTAINERS;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "all_weapons"));
    pickup_mapping[num_pickups].pickup_mode = PU_ALLWEAPON;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "missile_weapons"));
    pickup_mapping[num_pickups].pickup_mode = PU_MISSILEWEAPON;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "bows"));
    pickup_mapping[num_pickups].pickup_mode = PU_BOW;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "arrows"));
    pickup_mapping[num_pickups].pickup_mode = PU_ARROW;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_pickup_off"));
    pickup_mapping[num_pickups].pickup_mode = ~PU_RATIO;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_5"));
    pickup_mapping[num_pickups].pickup_mode = 1;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_10"));
    pickup_mapping[num_pickups].pickup_mode = 2;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_15"));
    pickup_mapping[num_pickups].pickup_mode = 3;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_20"));
    pickup_mapping[num_pickups].pickup_mode = 4;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_25"));
    pickup_mapping[num_pickups].pickup_mode = 5;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_30"));
    pickup_mapping[num_pickups].pickup_mode = 6;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_35"));
    pickup_mapping[num_pickups].pickup_mode = 7;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_40"));
    pickup_mapping[num_pickups].pickup_mode = 8;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_45"));
    pickup_mapping[num_pickups].pickup_mode = 9;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "ratio_50"));
    pickup_mapping[num_pickups].pickup_mode = 10;
    num_pickups++;

    pickup_mapping[num_pickups].menuitem =
        GTK_WIDGET(gtk_builder_get_object(window_xml, "not_cursed"));
    pickup_mapping[num_pickups].pickup_mode = PU_NOT_CURSED;
    num_pickups++;

    /*
     * Do some bounds checking.  We could actually set this exactly right,
     * since additional menu entries are not likely to be added often.  We exit
     * because if we overrun that structure, we've screwed up memory and will
     * likely crash or otherwise have odd behaviour.
     */
    if (num_pickups>=MAX_PICKUPS) {
        LOG(LOG_ERROR, "pickup.c::pickup_init", "num_pickups (%d) >= MAX_PICKUPS (%d)\n",
            num_pickups, MAX_PICKUPS);
        exit(1);
    }

}

/**
 * We get pickup information from server, update our status.
 */
void client_pickup(guint32 pickup)
{
    int i;

    /*
     * no_recurse is used to limit callbacks - otherwise what happens is when
     * we call set_active below, it emits the appropriate signal, which results
     * in new_menu_pickup() getting called, which then sends a new pickup
     * command to the server, which then results in server sending data to
     * client, etc.
     */
    no_recurse=1;
    pmode=pickup;

    for (i=0; i < num_pickups; i++) {
        if ((pickup & ~PU_RATIO) & pickup_mapping[i].pickup_mode ||
                (pickup & PU_RATIO) == pickup_mapping[i].pickup_mode) {
            gtk_check_menu_item_set_active(GTK_CHECK_MENU_ITEM(pickup_mapping[i].menuitem), 1);
        } else {
            gtk_check_menu_item_set_active(GTK_CHECK_MENU_ITEM(pickup_mapping[i].menuitem), 0);
        }
    }
    no_recurse=0;
}
