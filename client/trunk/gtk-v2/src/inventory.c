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
 * Draw inventory and 'look' windows
 */

#include "client.h"

#include <gtk/gtk.h>

#include "image.h"
#include "main.h"
#include "gtk2proto.h"

#include "../../pixmaps/all.xpm"
#include "../../pixmaps/coin.xpm"
#include "../../pixmaps/hand.xpm"
#include "../../pixmaps/hand2.xpm"
#include "../../pixmaps/lock.xpm"
#include "../../pixmaps/mag.xpm"
#include "../../pixmaps/nonmag.xpm"
#include "../../pixmaps/skull.xpm"
#include "../../pixmaps/unlock.xpm"
#include "../../pixmaps/unidentified.xpm"

GtkWidget *treeview_look;

/** Color to use to indicate that an item is applied. */
static const GdkColor applied_color = {0, 50000, 50000, 50000};

static GtkTreeStore *store_look;
static GtkWidget *encumbrance_current;
static GtkWidget *encumbrance_max;
static GtkWidget *inv_notebook;
static GtkWidget *inv_table;

static double weight_limit;

/*
 * Hopefully, large enough.  Trying to do this with g_malloc gets more
 * complicated because position of elements within the array are important, so
 * a simple g_realloc won't work.
 */
#define MAX_INV_COLUMNS 20
#define MAX_INV_ROWS    100
static GtkWidget *inv_table_children[MAX_INV_ROWS][MAX_INV_COLUMNS];

/* Different styles we recognize */
enum Styles {
    Style_Magical = 0, Style_Cursed, Style_Unpaid, Style_Locked, Style_Applied, Style_Last
};

/* The name of these styles in the rc file */
static const char *Style_Names[Style_Last] = {
    "inv_magical", "inv_cursed", "inv_unpaid", "inv_locked", "inv_applied"
};

/* Actual styles as loaded.  May be null if no style found. */
static GtkStyle *inv_styles[Style_Last];

/*
 * The basic idea of the NoteBook_Info structure is to hold everything we need
 * to know about the different inventory notebooks in a module fashion -
 * instead of hardcoding values, they can be held in the array.
 */
#define NUM_INV_LISTS   11
#define INV_SHOW_ITEM   0x1
#define INV_SHOW_COLOR  0x2

/**
 * @enum display_type
 * Indicate how an inventory tab should be drawn
 */
enum display_type {
    INV_TREE, INV_TABLE
};

static int num_inv_notebook_pages = 0;

typedef struct {
    const char *name; /**< Name of this page, for the show command */
    const char *tooltip; /**< Tooltip for menu */
    const char *const *xpm; /**< Icon to draw for the notebook selector */
    int(*show_func) (item *it); /**< Function that takes an item and returns
                                 * INV_SHOW_* above on whether to show this
                                 * item and if it should be shown in color
                                 */
    enum display_type type; /**< Type of widget */
    GtkWidget *treeview; /**< treeview widget for this tab */
} Notebook_Info;

static GtkTreeStore *treestore; /**< store of data for treeview */

/* Prototypes for static functions */
static void on_switch_page(GtkNotebook *notebook, gpointer *page,
                           guint page_num, gpointer user_data);

static int show_all(item *it) {
    return INV_SHOW_ITEM | INV_SHOW_COLOR;
}

static int show_applied(item *it) {
    return (it->applied ? INV_SHOW_ITEM : 0);
}

static int show_unapplied(item *it) {
    return (it->applied ? 0 : INV_SHOW_ITEM);
}

static int show_unpaid(item *it) {
    return (it->unpaid ? INV_SHOW_ITEM : 0);
}

static int show_cursed(item *it) {
    return ((it->cursed | it->damned) ? INV_SHOW_ITEM : 0);
}

static int show_magical(item *it) {
    return (it->magical ? INV_SHOW_ITEM : 0);
}

static int show_nonmagical(item *it) {
    return (it->magical ? 0 : INV_SHOW_ITEM);
}

static int show_locked(item *it) {
    return (it->locked ? (INV_SHOW_ITEM | INV_SHOW_COLOR) : 0);
}

static int show_unlocked(item *it) {
    // Show open containers, even if locked, to make moving items easier.
    return ((it->locked && !it->open) ? 0 : (INV_SHOW_ITEM | INV_SHOW_COLOR));
}

static int show_unidentified(item *it) {
    return ((it->flagsval & F_UNIDENTIFIED) ? INV_SHOW_ITEM : 0);
}

static Notebook_Info inv_notebooks[NUM_INV_LISTS] = {
    {"all", "All", all_xpm, show_all, INV_TREE},
    {"applied", "Applied", hand_xpm, show_applied, INV_TREE},
    {"unapplied", "Unapplied", hand2_xpm, show_unapplied, INV_TREE},
    {"unpaid", "Unpaid", coin_xpm, show_unpaid, INV_TREE},
    {"cursed", "Cursed", skull_xpm, show_cursed, INV_TREE},
    {"magical", "Magical", mag_xpm, show_magical, INV_TREE},
    {"nonmagical", "Non-magical", nonmag_xpm, show_nonmagical, INV_TREE},
    {"locked", "Locked", lock_xpm, show_locked, INV_TREE},
    {"unlocked", "Unlocked", unlock_xpm, show_unlocked, INV_TREE},
    {"unidentified", "Unidentified", unidentified_xpm, show_unidentified, INV_TREE},
    {"icons", "Icon View", NULL, show_all, INV_TABLE}
};

/**
 * @enum list_property
 * Constants used to refer to columns in the inventory list view
 */
enum list_property {
    LIST_NONE, LIST_ICON, LIST_NAME, LIST_WEIGHT, LIST_OBJECT, LIST_BACKGROUND, LIST_TYPE,
    LIST_BASENAME, LIST_FOREGROUND, LIST_FONT, LIST_NUM_COLUMNS
};

/**
 * @enum item_env
 * Describe where an item is. These constants must be kept as-is for use in
 * a few bitwise operations.
 */
enum item_env {
    ITEM_INVENTORY = 0x1, ITEM_GROUND = 0x2, ITEM_IN_CONTAINER = 0x4
};

/**
 * Returns information on the environment of the item, using the return values
 * below.  Note that there should never be a case where both ITEM_GROUND and
 * ITEM_INVENTORY are returned, but I prefer a more active approach in
 * returning actual values and not presuming that lack of value means it is in
 * the other location.
 *
 * @param it
 * @return
 */
static int get_item_env(item *it) {
    if (it->env == cpl.ob) {
        return ITEM_INVENTORY;
    }
    if (it->env == cpl.below) {
        return ITEM_GROUND;
    }
    if (it->env == NULL) {
        return 0;
    }
    return (ITEM_IN_CONTAINER | get_item_env(it->env));
}

/**
 *
 * @param event
 * @param tmp
 */
static void list_item_action(GdkEventButton *event, item *tmp) {
    int env;

    /* We need to know where this item is in fact is */
    env = get_item_env(tmp);

    /* It'd sure be nice if these weren't hardcoded values for button and
     * shift presses.
     */
    if (event->button == 1) {
        if (event->state & GDK_SHIFT_MASK) {
            toggle_locked(tmp);
        } else {
            client_send_examine(tmp->tag);
        }
    } else if (event->button == 2) {
        if (event->state & GDK_SHIFT_MASK) {
            send_mark_obj(tmp);
        } else {
            client_send_apply(tmp->tag);
        }
    } else if (event->button == 3) {
        if (tmp->locked) {
            draw_ext_info(NDI_BLACK, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_NOTICE,
                    "This item is locked. To drop it, first unlock by shift+leftclicking on it.");
        } else {
            guint32 dest;

            cpl.count = gtk_spin_button_get_value_as_int(GTK_SPIN_BUTTON(spinbutton_count));
            /*
             * Figure out where to move the item to.  If it is on the ground,
             * it is moving to the players inventory.  If it is in a container,
             * it is also moving to players inventory.  If it is in the players
             * inventory (not a container) and the player has an open container
             * in his inventory, move the object to the container (not ground).
             * Otherwise, it is moving to the ground (dest=0).  Have to look at
             * the item environment, because what list is no longer accurate.
             */
            if (env & (ITEM_GROUND | ITEM_IN_CONTAINER)) {
                dest = cpl.ob->tag;
            } else if (env == ITEM_INVENTORY && cpl.container &&
                    (get_item_env(cpl.container) == ITEM_INVENTORY ||
                    get_item_env(cpl.container) == ITEM_GROUND)) {
                dest = cpl.container->tag;
            } else {
                dest = 0;
            }

            client_send_move(dest, tmp->tag, cpl.count);
            gtk_spin_button_set_value(GTK_SPIN_BUTTON(spinbutton_count), 0.0);
            cpl.count = 0;
        }
    }
}

/**
 * Used when a button is pressed on the inventory or look list.  The parameters
 * are those determined by the callback.  Note that this function isn't 100%
 * ideal - some of the events/handling is only relevant for objects in the
 * inventory and not the look window (eg, locking items).  OTOH, maybe it is
 * just as well that the behaviour is always consistent.
 *
 * @param selection
 * @param model
 * @param path
 * @param path_currently_selected
 * @param userdata
 * @return FALSE
 */
static gboolean list_selection_func(GtkTreeSelection *selection,
        GtkTreeModel *model, GtkTreePath *path,
        gboolean path_currently_selected, gpointer userdata) {
    GtkTreeIter iter;
    GdkEventButton *event;

    /* Get the current event so we can know if shift is pressed */
    event = (GdkEventButton*)gtk_get_current_event();
    if (!event) {
        LOG(LOG_ERROR, "inventory.c::list_selection_func", "Unable to get event structure\n");
        return FALSE;
    }

    if (gtk_tree_model_get_iter(model, &iter, path)) {
        item *tmp;

        gtk_tree_model_get(model, &iter, LIST_OBJECT, &tmp, -1);

        if (!tmp) {
            LOG(LOG_ERROR, "inventory.c::list_selection_func", "Unable to get item structure\n");
            return FALSE;
        }
        list_item_action(event, tmp);
    }
    /*
     * Don't want the row toggled - our code above handles what we need to do,
     * so return false.
     */
    return FALSE;
}

/**
 * If the player collapses the row with the little icon, we have to unapply the
 * object for things to work 'sanely' (eg, items not go into the container
 *
 * @param treeview
 * @param iter
 * @param path
 * @param user_data
 */
static void list_row_collapse(GtkTreeView *treeview, GtkTreeIter *iter,
        GtkTreePath *path, gpointer user_data) {
    GtkTreeModel *model;
    item *tmp;

    model = gtk_tree_view_get_model(treeview);

    gtk_tree_model_get(GTK_TREE_MODEL(model), iter, LIST_OBJECT, &tmp, -1);
    client_send_apply(tmp->tag);
}

/**
 *
 * @param treeview
 */
static void setup_list_columns(GtkWidget *treeview) {
    GtkCellRenderer *renderer;
    GtkTreeViewColumn *column;
    GtkTreeSelection *selection;

#if 0
    /*
     * This is a hack to hide the expander column.  We do this because access
     * via containers need to be handled by the apply/unapply mechanism -
     * otherwise, I think it will be confusing - people 'closing' the container
     * with the expander arrow and still having things go into/out of the
     * container.  Unfortunat
     */
    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes("", renderer,
            "text", LIST_NONE,
            NULL);
    gtk_tree_view_append_column(GTK_TREE_VIEW(treeview), column);
    gtk_tree_view_column_set_visible(column, FALSE);
    gtk_tree_view_set_expander_column(GTK_TREE_VIEW(treeview), column);
#endif

    renderer = gtk_cell_renderer_pixbuf_new();
    /*
     * Setting the xalign to 0.0 IMO makes the display better.  Gtk
     * automatically resizes the column to make space based on image size,
     * however, it isn't really aggressive on shrinking it.  IMO, it looks
     * better for the image to always be at the far left - without this
     * alignment, the image is centered which IMO doesn't always look good.
     */
    g_object_set(G_OBJECT(renderer), "xalign", 0.0, NULL);
    column = gtk_tree_view_column_new_with_attributes("?", renderer,
            "pixbuf", LIST_ICON, NULL);

    /*  gtk_tree_view_column_set_sizing(column, GTK_TREE_VIEW_COLUMN_FIXED);*/
    gtk_tree_view_column_set_min_width(column, image_size);
    gtk_tree_view_column_set_sort_column_id(column, LIST_TYPE);
    gtk_tree_view_append_column(GTK_TREE_VIEW(treeview), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes("Name", renderer,
            "text", LIST_NAME, NULL);
    gtk_tree_view_column_set_expand(column, TRUE);
    gtk_tree_view_column_set_sort_column_id(column, LIST_BASENAME);

    gtk_tree_view_append_column(GTK_TREE_VIEW(treeview), column);
    gtk_tree_view_column_add_attribute(column, renderer, "background-gdk", LIST_BACKGROUND);
    gtk_tree_view_column_add_attribute(column, renderer, "foreground-gdk", LIST_FOREGROUND);
    gtk_tree_view_column_add_attribute(column, renderer, "font-desc", LIST_FONT);
    gtk_tree_view_set_expander_column(GTK_TREE_VIEW(treeview), column);
    gtk_tree_view_column_set_sizing(column, GTK_TREE_VIEW_COLUMN_FIXED);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes("Weight", renderer,
            "text", LIST_WEIGHT, NULL);
    /*
     * At 50, the title was always truncated on some systems.  64 is the
     * minimum on those systems for it to be possible to avoid truncation at
     * all.  Truncating the title looks cheesy, especially since heavy items
     * (100+) need the width of the field anyway.  If weight pushed off the
     * edge is a problem, it would be just better to let the user resize or
     * find a way to allow rendering with a smaller font.
     */
    gtk_tree_view_column_set_min_width(column, 64);
    gtk_tree_view_column_set_sizing(column, GTK_TREE_VIEW_COLUMN_FIXED);

    gtk_tree_view_column_set_sort_column_id(column, LIST_WEIGHT);
    gtk_tree_view_append_column(GTK_TREE_VIEW(treeview), column);
    gtk_tree_view_column_add_attribute(column, renderer, "background-gdk", LIST_BACKGROUND);
    gtk_tree_view_column_add_attribute(column, renderer, "foreground-gdk", LIST_FOREGROUND);
    gtk_tree_view_column_add_attribute(column, renderer, "font-desc", LIST_FONT);
    /*
     * Really, we never really do selections - clicking on an object causes a
     * reaction right then.  So grab press before the selection and just negate
     * the selection - that's more efficient than unselection the item after it
     * was selected.
     */
    selection = gtk_tree_view_get_selection(GTK_TREE_VIEW(treeview));

    gtk_tree_selection_set_select_function(selection, list_selection_func, NULL, NULL);
}

/**
 * Gets the style information for the inventory windows.  This is a separate
 * function because if the user changes styles, it can be nice to re-load the
 * configuration.  The style for the inventory/look is a bit special.  That is
 * because with gtk, styles are widget wide - all rows in the widget would use
 * the same style.  We want to adjust the styles based on other attributes.
 */
void inventory_get_styles() {
    int i;
    GtkStyle *tmp_style;
    static int has_init = 0;

    for (i = 0; i < Style_Last; i++) {
        if (has_init && inv_styles[i]) {
            g_object_unref(inv_styles[i]);
        }
        tmp_style = gtk_rc_get_style_by_paths(gtk_settings_get_default(), NULL, Style_Names[i],
                G_TYPE_NONE);
        if (tmp_style) {
            inv_styles[i] = g_object_ref(tmp_style);
        } else {
            LOG(LOG_INFO, "inventory.c::inventory_get_styles", "Unable to find style for %s",
                    Style_Names[i]);
            inv_styles[i] = NULL;
        }
    }
    has_init = 1;
}

/**
 * Set up the inventory viewer.
 *
 * @param window_root The client main window.
 */
void inventory_init(GtkWidget *window_root) {
    int i;

    /*inventory_get_styles();*/

    inv_notebook = GTK_WIDGET(gtk_builder_get_object(window_xml,
            "notebook_inv"));
    treeview_look = GTK_WIDGET(gtk_builder_get_object(window_xml,
            "treeview_look"));
    encumbrance_current = GTK_WIDGET(gtk_builder_get_object(window_xml,
            "label_stat_encumbrance_current"));
    encumbrance_max = GTK_WIDGET(gtk_builder_get_object(window_xml,
            "label_stat_encumbrance_max"));
    inv_table = GTK_WIDGET(gtk_builder_get_object(window_xml,
            "inv_table"));

    g_signal_connect((gpointer)inv_notebook, "switch_page",
                     (GCallback)on_switch_page, NULL);
    g_signal_connect((gpointer) treeview_look, "row_collapsed",
            (GCallback) list_row_collapse, NULL);

    memset(inv_table_children, 0, sizeof (GtkWidget *) * MAX_INV_ROWS * MAX_INV_COLUMNS);

    treestore = gtk_tree_store_new(LIST_NUM_COLUMNS,
            G_TYPE_STRING,
            G_TYPE_OBJECT,
            G_TYPE_STRING,
            G_TYPE_STRING,
            G_TYPE_POINTER,
            GDK_TYPE_COLOR,
            G_TYPE_INT,
            G_TYPE_STRING,
            GDK_TYPE_COLOR,
            PANGO_TYPE_FONT_DESCRIPTION);

    store_look = gtk_tree_store_new(LIST_NUM_COLUMNS,
            G_TYPE_STRING,
            G_TYPE_OBJECT,
            G_TYPE_STRING,
            G_TYPE_STRING,
            G_TYPE_POINTER,
            GDK_TYPE_COLOR,
            G_TYPE_INT,
            G_TYPE_STRING,
            GDK_TYPE_COLOR,
            PANGO_TYPE_FONT_DESCRIPTION);

    gtk_tree_view_set_model(GTK_TREE_VIEW(treeview_look), GTK_TREE_MODEL(store_look));
    setup_list_columns(treeview_look);
    /*
     * Glade doesn't let us fully realize a treeview widget - we still need to
     * to do a bunch of customization just like we do for the look window
     * above.  If we have to do all that work, might as well just put it in the
     * for loop below vs setting up half realized widgets within layout that we
     * then need to finish setting up.  However, that said, we want to be able
     * to set up other notebooks within layout for perhaps a true list of just
     * icons.  So we presume that any tabs that exist must already be all set
     * up.  We prepend our tabs to the existing tab - this makes the position
     * of the array of noteboks correspond to actual data in the tabs.
     */
    for (i = 0; i < NUM_INV_LISTS; i++) {
        GtkWidget *swindow, *image;

        if (inv_notebooks[i].type == INV_TREE) {
            swindow = gtk_scrolled_window_new(NULL, NULL);
            gtk_widget_show(swindow);
            gtk_scrolled_window_set_policy(GTK_SCROLLED_WINDOW(swindow),
                    GTK_POLICY_NEVER, GTK_POLICY_ALWAYS);
            image = gtk_image_new_from_pixbuf(
                    gdk_pixbuf_new_from_xpm_data((const char**) inv_notebooks[i].xpm));

            if (inv_notebooks[i].tooltip) {
                GtkWidget *eb;

                eb = gtk_event_box_new();
                gtk_widget_show(eb);

                gtk_container_add(GTK_CONTAINER(eb), image);
                gtk_widget_show(image);

                image = eb;
                gtk_widget_set_tooltip_text(image, inv_notebooks[i].tooltip);
            }

            gtk_notebook_insert_page(GTK_NOTEBOOK(inv_notebook), swindow, image, i);

            inv_notebooks[i].treeview = gtk_tree_view_new_with_model(GTK_TREE_MODEL(
                    treestore));

            g_signal_connect((gpointer) inv_notebooks[i].treeview, "row_collapsed",
                    G_CALLBACK(list_row_collapse), NULL);

            setup_list_columns(inv_notebooks[i].treeview);
            gtk_widget_show(inv_notebooks[i].treeview);
            gtk_container_add(GTK_CONTAINER(swindow), inv_notebooks[i].treeview);
        }
    }
    num_inv_notebook_pages = gtk_notebook_get_n_pages(GTK_NOTEBOOK(inv_notebook));

    /* Make sure we are on the first page */
    gtk_notebook_set_current_page(GTK_NOTEBOOK(inv_notebook), 0);

    /* If all the data is set up properly, these should match */
    if (num_inv_notebook_pages != NUM_INV_LISTS) {
        LOG(LOG_ERROR, "inventory.c::inventory_init",
                "num_inv_notebook_pages (%d) does not match NUM_INV_LISTS(%d)\n",
                num_inv_notebook_pages, NUM_INV_LISTS);
    }

}

/**
 * Open and close_container are now no-ops - since these are now drawn inline
 * as treestores, we don't need to update what we are drawing were.  and since
 * the activation of a container will cause the list to be redrawn, don't need
 * to worry about making an explicit call here.
 *
 * @param op
 */
void close_container(item *op) {
}

/**
 *
 * @param op
 */
void open_container(item *op) {
}

/**
 *
 * @param params
 */
void command_show(const char *params) {
    if (!params) {
        /*
         * Shouldn't need to get current page, but next_page call is not
         * wrapping like the docs claim it should.
         */
        if (gtk_notebook_get_current_page(GTK_NOTEBOOK(inv_notebook)) ==
            num_inv_notebook_pages) {
            gtk_notebook_set_current_page(GTK_NOTEBOOK(inv_notebook), 0);
        } else {
            gtk_notebook_next_page(GTK_NOTEBOOK(inv_notebook));
        }
    } else {
        char buf[MAX_BUF];

        for (int i = 0; i < NUM_INV_LISTS; i++) {
            if (!strncmp(params, inv_notebooks[i].name, strlen(params))) {
                gtk_notebook_set_current_page(GTK_NOTEBOOK(inv_notebook), i);
                return;
            }
        }
        snprintf(buf, sizeof (buf), "Unknown notebook page %s\n", params);
        draw_ext_info(NDI_RED, MSG_TYPE_CLIENT, MSG_TYPE_CLIENT_ERROR, buf);
    }
}

/**
 * No reason to divide by 1000 everytime we do the display, so do it once and
 * store it here.
 *
 * @param wlim
 */
void set_weight_limit(guint32 wlim) {
    weight_limit = wlim / 1000.0;
}

/**
 *
 * @param it
 * @return a style based on values in it
 */
static GtkStyle *get_row_style(item *it) {
    int style;

    /* Note that this ordering is documented in the sample rc file.
     * it would be nice if this precedence could be more easily
     * setable by the end user.
     */
    if (it->unpaid) {
        style = Style_Unpaid;
    } else if (it->cursed || it->damned) {
        style = Style_Cursed;
    } else if (it->magical) {
        style = Style_Magical;
    } else if (it->applied) {
        style = Style_Applied;
    } else if (it->locked) {
        style = Style_Locked;
    } else {
        return NULL; /* No matching style */
    }

    return inv_styles[style];
}

/***************************************************************************
 * Below are the actual guts for drawing the inventory and look windows.
 * Some quick notes:
 * 1) The gtk2 widgets (treeview/treemodel) seem noticably slower than the
 *    older clist widgets in gtk1.  This is beyond the code below - just
 *    scrolling the window, which is all done internally by gtk, is
 *    quite slow.  Seems especially bad when using the scrollwheel.
 * 2) documentation suggests the detaching the treemodel and re-attaching
 *    it after insertions would be faster.  The problem is that this causes
 *    loss of positioning for the scrollbar. Eg, you eat a food in the middle
 *    of your inventory, and then inventory resets to the top of the inventory.
 * 3) it'd probably be much more efficient if the code could know what changes
 *    are taking place, instead of rebuilding the tree model each time.  For
 *    example, if the only thing that changed is the number of of the object,
 *    we can just update the name and weight, and not rebuild the entire list.
 *    This may be doable in the code below by getting data from the tree store
 *    and comparing it against what we want to show - however, figuring out
 *    insertions and removals are more difficult.
 */

void item_event_item_deleting(item * it) {
}

void item_event_container_clearing(item * container) {
}

void item_event_item_changed(item * it) {
}

/**
 * Adds a row to the treestore.
 *
 * @param it the object to add
 * @param store The TreeStore object.
 * @param new Returns the iter used/updated for the store
 * @param parent The parent iter (can be null).  If non null, then this creates
 * a real tree, for things like containers.
 * @param color If true, do foreground/background colors, otherwise, just black
 * & white Normally it is set.  However, when showing the cursed inv tab, it
 * doesn't make a lot of sense to show them all in the special color, since
 * they all meet that special criteria
 */
static void add_object_to_store(item *it, GtkTreeStore *store,
        GtkTreeIter *new, GtkTreeIter *parent, int color) {
    char buf[256], buf1[256];
    GdkColor *foreground = NULL, *background = NULL;
    PangoFontDescription *font = NULL;
    GtkStyle *row_style;

    if (it->weight < 0) {
        strcpy(buf, " ");
    } else {
        snprintf(buf, sizeof (buf), "%6.1f", it->nrof * it->weight);
    }
    snprintf(buf1, 255, "%s %s", it->d_name, it->flags);
    if (color) {
        row_style = get_row_style(it);
        if (row_style) {
            /*
             * Even if the user doesn't define these, we should still get get
             * defaults from the system.
             */
            foreground = &row_style->text[GTK_STATE_NORMAL];
            background = &row_style->base[GTK_STATE_NORMAL];
            font = row_style->font_desc;
        }
    }

    gtk_tree_store_append(store, new, parent); /* Acquire an iterator */
    gtk_tree_store_set(store, new,
            LIST_ICON, (GdkPixbuf*) pixmaps[it->face]->icon_image,
            LIST_NAME, buf1,
            LIST_WEIGHT, buf,
            LIST_BACKGROUND, background,
            LIST_FOREGROUND, foreground,
            LIST_FONT, font,
            LIST_OBJECT, it,
            LIST_TYPE, it->type,
            LIST_BASENAME, it->s_name,
            -1);
}

/**
 * Draws the objects beneath the player.
 */
void draw_look_list() {
    item *tmp;
    GtkTreeIter iter;
    /*
     * List drawing is actually fairly inefficient - we only know globally if
     * the objects has changed, but have no idea what specific object has
     * changed.  As such, we are forced to basicly redraw the entire list each
     * time this is called.
     */
    gtk_tree_store_clear(store_look);

    for (tmp = cpl.below->inv; tmp; tmp = tmp->next) {
        add_object_to_store(tmp, store_look, &iter, NULL, 1);

        if ((cpl.container == tmp) && tmp->open) {
            item *tmp2;
            GtkTreeIter iter1;
            GtkTreePath *path;

            for (tmp2 = tmp->inv; tmp2; tmp2 = tmp2->next) {
                add_object_to_store(tmp2, store_look, &iter1, &iter, 1);
            }
            path = gtk_tree_model_get_path(GTK_TREE_MODEL(store_look), &iter);
            gtk_tree_view_expand_row(GTK_TREE_VIEW(treeview_look), path, FALSE);
            gtk_tree_path_free(path);
        }
    }
}

/**
 * Draws the inventory window.  tab is the notebook tab we are drawing.  Has to
 * be passed in because the callback sets this before the notebook is updated.
 *
 * @param tab
 */
static void draw_inv_list(int tab) {
    item *tmp;
    GtkTreeIter iter;
    int rowflag;

    /*
     * List drawing is actually fairly inefficient - we only know globally if
     * the objects has changed, but have no idea what specific object has
     * changed.  As such, we are forced to basicly redraw the entire list each
     * time this is called.
     */
    gtk_tree_store_clear(treestore);

    for (tmp = cpl.ob->inv; tmp; tmp = tmp->next) {
        rowflag = inv_notebooks[tab].show_func(tmp);
        if (!(rowflag & INV_SHOW_ITEM)) {
            continue;
        }

        add_object_to_store(tmp, treestore, &iter, NULL, rowflag & INV_SHOW_COLOR);

        if ((cpl.container == tmp) && tmp->open) {
            item *tmp2;
            GtkTreeIter iter1;
            GtkTreePath *path;

            for (tmp2 = tmp->inv; tmp2; tmp2 = tmp2->next) {
                /*
                 * Wonder if we really want this logic for objects in
                 * containers?  my thought is yes - being able to see all
                 * cursed objects in the container could be quite useful.
                 * Unfortunately, that doesn't quite work as intended, because
                 * we will only get here if the container object is being
                 * displayed.  Since container objects can't be cursed, can't
                 * use that as a filter.
                 */
                /*
                rowflag = inv_notebooks[tab].show_func(tmp2);
                 */
                if (!(rowflag & INV_SHOW_ITEM)) {
                    continue;
                }
                add_object_to_store(tmp2, treestore, &iter1, &iter,
                        rowflag & INV_SHOW_COLOR);
            }
            path = gtk_tree_model_get_path(GTK_TREE_MODEL(treestore), &iter);
            gtk_tree_view_expand_row(GTK_TREE_VIEW(inv_notebooks[tab].treeview), path, FALSE);
            gtk_tree_path_free(path);
        }
    }
}

/**
 *
 * @param widget
 * @param event
 * @param user_data
 * @return TRUE
 */
static gboolean drawingarea_inventory_table_button_press_event(
    GtkWidget *widget, GdkEventButton *event, gpointer user_data) {
    list_item_action(event, (item*) user_data);
    return TRUE;
}

static void draw_inv_table_icon(GdkWindow *dst, const void *image) {
    cairo_t *cr = gdk_cairo_create(dst);

    gdk_window_clear(dst);
    gdk_cairo_set_source_pixbuf(cr, (GdkPixbuf *) image, 0, 0);
    cairo_paint(cr);
    cairo_destroy(cr);
}

/**
 *
 * @param widget
 * @param event
 * @param user_data
 * @return TRUE
 */
static gboolean drawingarea_inventory_table_expose_event(GtkWidget *widget,
        GdkEventExpose *event, gpointer user_data) {
    item *tmp;

    tmp = (item*) user_data;

    /*
     * Can get cases when switching tabs that we get an expose event before the
     * list is updated - if so, don't draw stuff we don't have faces for.
     */
    if (tmp->face) {
        draw_inv_table_icon(gtk_widget_get_window(widget), pixmaps[tmp->face]->icon_image);
    }

    return TRUE;
}

/**
 * Draws the table of image icons.
 *
 * @param animate If non-zero, then this is an animation run - flip the
 * animation state of the objects, and only draw those that need to be drawn.
 */
static void draw_inv_table(int animate) {
    int x, y, rows, columns, num_items, i;
    static int max_drawn = 0;
    item *tmp;
    char buf[256];
    gulong handler;

    num_items = 0;
    for (tmp = cpl.ob->inv; tmp; tmp = tmp->next) {
        num_items++;
    }

    GtkAllocation size;
    gtk_widget_get_allocation(inv_table, &size);

    columns = size.width / image_size;
    if (columns > MAX_INV_COLUMNS) {
        columns = MAX_INV_COLUMNS;
    }
    rows = size.height / image_size;

    if (num_items > columns * rows) {
        rows = num_items / columns;
        if (num_items % columns) {
            rows++;
        }
    }
    if (rows > MAX_INV_ROWS) {
        rows = MAX_INV_ROWS;
    }

    gtk_table_resize(GTK_TABLE(inv_table), rows, columns);

    x = 0;
    y = 0;
    for (tmp = cpl.ob->inv; tmp; tmp = tmp->next) {
        if (inv_table_children[x][y] == NULL) {
            inv_table_children[x][y] = gtk_drawing_area_new();
            gtk_widget_set_size_request(inv_table_children[x][y], image_size,
                                        image_size);

            gtk_table_attach(GTK_TABLE(inv_table), inv_table_children[x][y],
                    x, x + 1, y, y + 1, GTK_FILL, GTK_FILL, 0, 0);
        }
        if (animate) {
            /* This is an object with animations */
            if (tmp->animation_id > 0 && tmp->anim_speed) {
                tmp->last_anim++;

                /* Time to change the face for this one */
                if (tmp->last_anim >= tmp->anim_speed) {
                    tmp->anim_state++;
                    if (tmp->anim_state >= animations[tmp->animation_id].num_animations) {
                        tmp->anim_state = 0;
                    }
                    tmp->face = animations[tmp->animation_id].faces[tmp->anim_state];
                    tmp->last_anim = 0;

                    draw_inv_table_icon(
                        gtk_widget_get_window(inv_table_children[x][y]),
                        pixmaps[tmp->face]->icon_image);
                }
            }
            /* On animation run, so don't do any of the remaining logic */
        } else {
            /*
             * Need to clear out the old signals, since the signals are
             * effectively stacked - you can have 6 signal handlers tied to the
             * same function.
             */
            handler = g_signal_handler_find((gpointer) inv_table_children[x][y],
                    G_SIGNAL_MATCH_FUNC, 0, 0, NULL,
                    G_CALLBACK(drawingarea_inventory_table_button_press_event),
                    NULL);

            if (handler) {
                g_signal_handler_disconnect((gpointer) inv_table_children[x][y], handler);
            }

            handler = g_signal_handler_find((gpointer) inv_table_children[x][y],
                    G_SIGNAL_MATCH_FUNC, 0, 0, NULL,
                    G_CALLBACK(drawingarea_inventory_table_expose_event),
                    NULL);
            if (handler) {
                g_signal_handler_disconnect((gpointer) inv_table_children[x][y], handler);
            }
            /*
             * Not positive precisely what events are needed, but some events
             * beyond just the button press are necessary for the tooltips to
             * work.
             */
            gtk_widget_add_events(inv_table_children[x][y], GDK_ALL_EVENTS_MASK);

            g_signal_connect((gpointer) inv_table_children[x][y], "button_press_event",
                    G_CALLBACK(drawingarea_inventory_table_button_press_event),
                    tmp);

            g_signal_connect((gpointer) inv_table_children[x][y], "expose_event",
                    G_CALLBACK(drawingarea_inventory_table_expose_event),
                    tmp);

            /* Draw the inventory icon image to the table. */
            draw_inv_table_icon(gtk_widget_get_window(inv_table_children[x][y]),
                                pixmaps[tmp->face]->icon_image);

            // Draw an extra indicator if the item is applied.
            if (tmp->applied) {
                gtk_widget_modify_bg(inv_table_children[x][y],
                        GTK_STATE_NORMAL, &applied_color);
            } else {
                gtk_widget_modify_bg(inv_table_children[x][y],
                        GTK_STATE_NORMAL, NULL);
            }

            gtk_widget_show(inv_table_children[x][y]);
            /*
             * Use tooltips to provide additional detail about the icons.
             * Looking at the code, the tooltip widget will take care of
             * removing the old tooltip, freeing strings, etc.
             */
            snprintf(buf, 255, "%s %s", tmp->d_name, tmp->flags);
            gtk_widget_set_tooltip_text(inv_table_children[x][y], buf);
        }
        x++;
        if (x == columns) {
            x = 0;
            y++;
        }

    }
    /* Don't need to do the logic below if only doing animation run */
    if (animate) {
        return;
    }
    /*
     * Need to disconnect the callback functions cells we did not draw.
     * otherwise, we get errors on objects that are drawn.
     */
    for (i = num_items; i <= max_drawn; i++) {
        if (inv_table_children[x][y]) {
            gtk_widget_destroy(inv_table_children[x][y]);
            inv_table_children[x][y] = NULL;
        }
        x++;
        if (x == columns) {
            x = 0;
            y++;
        }
    }
    max_drawn = num_items;

    gtk_widget_show(inv_table);
}

/**
 * Draws the inventory and updates the encumbrance statistics display in the
 * client.  Have to determine how to draw it.
 *
 * @param tab
 */
static void draw_inv(int tab) {
    char buf[256];

    snprintf(buf, sizeof (buf), "%6.1f", cpl.ob->weight);
    gtk_label_set_text(GTK_LABEL(encumbrance_current), buf);
    snprintf(buf, sizeof (buf), "%6.1f", weight_limit);
    gtk_label_set_text(GTK_LABEL(encumbrance_max), buf);

    if (inv_notebooks[tab].type == INV_TREE) {
        draw_inv_list(tab);
    } else if (inv_notebooks[tab].type == INV_TABLE) {
        draw_inv_table(0);
    }
}

/**
 * Redraws inventory and look windows when necessary
 */
void draw_lists() {
    /*
     * There are some extra complications with container handling and timing.
     * For example, we draw the window before we get a list of the container,
     * and then the container contents are not drawn - this can be handled by
     * looking at container->inv_updated.
     */
    if (cpl.container && cpl.container->inv_updated) {
        cpl.container->env->inv_updated = 1;
        cpl.container->inv_updated = 0;
    }
    if (cpl.ob->inv_updated) {
        draw_inv(gtk_notebook_get_current_page(GTK_NOTEBOOK(inv_notebook)));
        cpl.ob->inv_updated = 0;
    }
    if (cpl.below->inv_updated) {
        draw_look_list();
        cpl.below->inv_updated = 0;
    }
}

/**
 * People are likely go to the different tabs much less often than their
 * inventory changes.  So rather than update all the tabs whenever the players
 * inventory changes, only update the tab the player is viewing, and if they
 * change tabs, draw the new tab and get rid of the old info.  Ideally, I'd
 * like to call draw_inv() from this function, but there is some oddity
 *
 * @param notebook
 * @param page
 * @param page_num
 * @param user_data
 */
static void on_switch_page(GtkNotebook *notebook, gpointer *page,
                           guint page_num, gpointer user_data) {
    guint oldpage;

    oldpage = gtk_notebook_get_current_page(GTK_NOTEBOOK(notebook));
    if (oldpage != page_num && inv_notebooks[oldpage].type == INV_TREE) {
        gtk_tree_store_clear(treestore);
    }
    cpl.ob->inv_updated = 1;
}

/**
 *
 */
static void animate_inventory() {
    gboolean valid;
    GtkTreeIter iter;
    item *tmp;
    int page;
    GtkTreeStore *store;

    page = gtk_notebook_get_current_page(GTK_NOTEBOOK(inv_notebook));

    /* Still need to do logic for the table view. */
    if (inv_notebooks[page].type == INV_TABLE) {
        draw_inv_table(1);
        return;
    }

    store = treestore;

    /* Get the first iter in the list */
    valid = gtk_tree_model_get_iter_first(GTK_TREE_MODEL(store), &iter);

    while (valid) {
        gtk_tree_model_get(GTK_TREE_MODEL(store), &iter,
                LIST_OBJECT, &tmp,
                -1);

        /* This is an object with animations */
        if (tmp->animation_id > 0 && tmp->anim_speed &&
                animations[tmp->animation_id].faces != NULL) {
            tmp->last_anim++;

            /* Time to change the face for this one */
            if (tmp->last_anim >= tmp->anim_speed) {
                tmp->anim_state++;
                if (tmp->anim_state >= animations[tmp->animation_id].num_animations) {
                    tmp->anim_state = 0;
                }
                tmp->face = animations[tmp->animation_id].faces[tmp->anim_state];
                tmp->last_anim = 0;

                /* Update image in the tree store */
                gtk_tree_store_set(store, &iter,
                        LIST_ICON, (GdkPixbuf*) pixmaps[tmp->face]->icon_image,
                        -1);
            }
        }
        valid = gtk_tree_model_iter_next(GTK_TREE_MODEL(store), &iter);
    }
}

/**
 *
 */
static void animate_look() {
    gboolean valid;
    GtkTreeIter iter;
    item *tmp;

    /* Get the first iter in the list */
    valid = gtk_tree_model_get_iter_first(GTK_TREE_MODEL(store_look), &iter);

    while (valid) {
        gtk_tree_model_get(GTK_TREE_MODEL(store_look), &iter,
                LIST_OBJECT, &tmp,
                -1);

        /* This is an object with animations */
        if (tmp->animation_id > 0 && tmp->anim_speed) {
            tmp->last_anim++;

            /* Time to change the face for this one */
            if (tmp->last_anim >= tmp->anim_speed) {
                tmp->anim_state++;
                if (tmp->anim_state >= animations[tmp->animation_id].num_animations) {
                    tmp->anim_state = 0;
                }
                tmp->face = animations[tmp->animation_id].faces[tmp->anim_state];
                tmp->last_anim = 0;

                /* Update image in the tree store */
                gtk_tree_store_set(store_look, &iter,
                        LIST_ICON, (GdkPixbuf*) pixmaps[tmp->face]->icon_image,
                        -1);
            }
        }
        valid = gtk_tree_model_iter_next(GTK_TREE_MODEL(store_look), &iter);
    }
}

/**
 * This is called periodically from main.c - basically a timeout, used to
 * animate the inventory.
 */
void inventory_tick() {
    animate_inventory();
    animate_look();
}
