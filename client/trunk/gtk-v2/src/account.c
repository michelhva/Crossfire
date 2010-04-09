const char * const rcsid_gtk2_account_c =
    "$Id$";
/*
    Crossfire client, a client program for the crossfire program.

    Copyright (C) 2010 Mark Wedel & Crossfire Development Team

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

    The author can be reached via e-mail to crossfire@metalforge.org
*/

/**
 * @file gtk-v2/src/account.c
 * Handles account login, creation, and character selection.
 */

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#include <gtk/gtk.h>
#include <glade/glade.h>
#include <ctype.h>

#include "client.h"

#include "image.h"

#include "main.h"
#include "gtk2proto.h"
#include "metaserver.h"

static GtkWidget *add_character_window, *choose_char_window,
 *create_account_window, *login_window;

/* These are in the login_window */
static GtkWidget *button_login, *button_create_account,
    *button_go_metaserver, *button_exit_client,
    *entry_account_name,
    *entry_account_password, *label_account_login_status;

/* These are in the create_account_window */
static GtkWidget *button_new_create_account, *button_new_cancel,
    *entry_new_account_name,
    *entry_new_account_password, *entry_new_confirm_password,
    *label_create_account_status;

/* These are in the choose_character window */
static GtkWidget *button_play_character, *button_create_character, 
    *button_add_character, *button_return_login,
    *treeview_choose_character;

/* These are in the new_character window */
static GtkWidget *entry_new_character_name, *new_character_window,
    *label_new_char_status, *button_create_new_char,
    *button_new_char_cancel;

GtkListStore    *character_store;

static char account_password[256];

/* This enum just maps the columns in the list store to their
 * position.
 */
enum {CHAR_IMAGE, CHAR_NAME, CHAR_CLASS, CHAR_RACE, CHAR_LEVEL, CHAR_PARTY,
      CHAR_MAP, CHAR_ICON};
#define  CHAR_NUM_COLUMNS 8

/* These are in the add_character window */
static GtkWidget *button_do_add_character,
    *button_return_character_select, *entry_character_name,
    *entry_character_password, *label_add_status;

GtkTextBuffer *textbuf_motd, *textbuf_news, *textbuf_rules_account,
    *textbuf_rules_char;

/* These are used as offsets for num_text_views -
 * we share the drawing code in info.c
 * if more textviews are added, note that NUM_TEXT_VIEWS
 * in info.c needs to be increased.
 */
#define TEXTVIEW_MOTD           0
#define TEXTVIEW_NEWS           1
#define TEXTVIEW_RULES_ACCOUNT  2
#define TEXTVIEW_RULES_CHAR     3

Info_Pane login_pane[4];

extern int num_text_views;


static int has_init=0;


/**
 * As the name implies, this hides all the login related
 * windows.  This is needed in case the client loses the connection
 * to the server (either through player going to client/disconnect
 * or network failure).  get_metaserver() calls this, as well
 * as AddMeSuccess
 */
void hide_all_login_windows()
{
    extern GtkWidget *treeview_look;

    if (has_init) {
        /* If we have not initialized, nothing to hide */
        gtk_widget_hide(login_window);
        gtk_widget_hide(add_character_window);
        gtk_widget_hide(choose_char_window);
        gtk_widget_hide(create_account_window);
        gtk_widget_hide(new_character_window);

        /* if the player has started playing (this function being
         * called from AddMeSuccess), we want to make sure that
         * the extended command entry widget is not activated -
         * we want normal command entry.  Where this shows up
         * is if the player was playing before and uses a 
         * savebed - now the last thing activated is that
         * entry widget.
         */
        gtk_widget_grab_focus (GTK_WIDGET(treeview_look));
    }

}

/*****************************************************************************
 * New character window functions
 *****************************************************************************/

void create_new_character_failure(char *message)
{
        gtk_label_set_text(GTK_LABEL(label_new_char_status),
                           message);
}

static void create_new_character()
{
    const char *name;
    uint8 buf[MAX_BUF];
    SockList sl;

    SockList_Init(&sl, buf);

    name =  gtk_entry_get_text(GTK_ENTRY(entry_new_character_name));

    if (!name || *name == 0) {
        gtk_label_set_text(GTK_LABEL(label_new_char_status),
                           "You must enter a character name.");
        return;
    } else {
        gtk_label_set_text(GTK_LABEL(label_new_char_status),
                           "");

        SockList_AddString(&sl, "createplayer ");
        SockList_AddChar(&sl, strlen(name));
        SockList_AddString(&sl, name);
        SockList_AddChar(&sl, strlen(account_password));
        SockList_AddString(&sl, account_password);
        SockList_Send(&sl, csocket.fd);
    }
}


/**
 * User hit the create character button.  Get data, send to server.
 */
void
on_button_create_new_char_clicked (GtkButton *button, gpointer user_data)
{
    create_new_character();
}

/**
 * User hit return in the new character name box.  Like above,
 * get data and send to server.
 */
void on_entry_new_character_name(GtkEntry *entry, gpointer user_data)
{
    create_new_character();
}

/**
 * User his hit the cancel button in the new character window.
 * hide the new character window, show the choose character window.
 */
void
on_button_new_char_cancel_clicked(GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(new_character_window);
    gtk_widget_show(choose_char_window);
}



/**
 * Initializes the new character window.
 */
static void init_new_character_window()
{
    GladeXML *xml_tree;                                                                                      

    new_character_window = glade_xml_get_widget(dialog_xml, "new_character_window");
    gtk_window_set_transient_for(GTK_WINDOW(new_character_window), GTK_WINDOW(window_root));

    xml_tree = glade_get_widget_tree(GTK_WIDGET(new_character_window));

    button_create_new_char = glade_xml_get_widget(dialog_xml,"button_create_new_char");
    button_new_char_cancel =
        glade_xml_get_widget(dialog_xml,"button_new_char_cancel");
    entry_new_character_name = glade_xml_get_widget(dialog_xml,"entry_new_character_name");
    label_new_char_status = glade_xml_get_widget(dialog_xml,"label_new_char_status");

    g_signal_connect ((gpointer) button_create_new_char, "clicked",
                      G_CALLBACK (on_button_create_new_char_clicked), NULL);
    g_signal_connect ((gpointer) button_new_char_cancel, "clicked",
                      G_CALLBACK (on_button_new_char_cancel_clicked), NULL);
    g_signal_connect ((gpointer) entry_new_character_name, "activate",
                      G_CALLBACK (on_entry_new_character_name), NULL);
}


/******************************************************************************
 * add_character_window functions
 *****************************************************************************/

/**
 * This just sends a request to the server add add the
 * character to this account.
 */
static void add_character_to_account(const char *name, const char *password, int force)
{
    SockList sl;
    uint8 buf[MAX_BUF];

    if (!name || !password || *name == 0 || *password == 0) {
        gtk_label_set_text(GTK_LABEL(label_add_status),
                      "You must enter both a name and password!");
    } else {
        gtk_label_set_text(GTK_LABEL(label_add_status), "");

        SockList_Init(&sl, buf);
        SockList_AddString(&sl, "accountaddplayer ");
        SockList_AddChar(&sl, force);
        SockList_AddChar(&sl, strlen(name));
        SockList_AddString(&sl, name);
        SockList_AddChar(&sl, strlen(password));
        SockList_AddString(&sl, password);
        SockList_Send(&sl, csocket.fd);
    }
}


/**
 * handles a failure from the server - pretty basic - just
 * throw up the message and let the user try again.
 * This is a response to the 'failure accountaddplayer' command.
 * Calling this account_add_character_failure may be a little
 * bit of a misnomer, but all the other routines in this area refer
 * to character, not player.
 *
 * @param message
 * message to display.  Unlike other messages, the first
 * word of this message should be an integer, which denotes
 * if using the 'force' option would allow the user
 * to override this.
 */
void account_add_character_failure(char *message)
{
    char *cp;
    int retry;

    retry = atoi(message);
    cp = strchr(message,' ');
    if (cp) {
        cp++;
    } else
        cp=message;

    if (!retry) {
        gtk_label_set_text(GTK_LABEL(label_add_status), cp);
    } else {
        /* In this case, we can retry it and it should work
         * if we set force.
         * So bring up a dialog, as the user what to do - 
         * if they enter yes, we use force.  If not,
         * we clear the entry fields and just continue
         * onward.
         */
        GtkWidget *dialog;
        int result;
        const char *name, *password;

        /* Bring up a dialog window */
        dialog = 
            gtk_message_dialog_new(NULL, GTK_DIALOG_DESTROY_WITH_PARENT, GTK_MESSAGE_QUESTION,
                                   GTK_BUTTONS_YES_NO, "%s\n%s", cp, "Apply anyways?");
        result = gtk_dialog_run(GTK_DIALOG(dialog)); 
        gtk_widget_destroy(dialog);

        if (result == GTK_RESPONSE_YES ) {
            name =  gtk_entry_get_text(GTK_ENTRY(entry_character_name));
            password =  gtk_entry_get_text(GTK_ENTRY(entry_character_password));
            add_character_to_account(name, password, 1);
        } else {
            gtk_entry_set_text(GTK_ENTRY(entry_character_name), "");
            gtk_entry_set_text(GTK_ENTRY(entry_character_password), "");
            gtk_widget_grab_focus(entry_character_name);
        }
    }
}

/**
 * User has hit the add character button.  Let add_character_to_account()
 * do all the work.
 *
 * @params ignored
 */
void
on_button_do_add_character_clicked (GtkButton *button, gpointer user_data)
{
    add_character_to_account(gtk_entry_get_text(GTK_ENTRY(entry_character_name)),
                             gtk_entry_get_text(GTK_ENTRY(entry_character_password)), 0);

}


/**
 * User has hit the return to character selection button.  Pretty simple -
 * just hide this window, activate the other window.
 *
 * @params ignored
 */
void
on_button_return_character_select_clicked (GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(add_character_window);
    gtk_widget_show(choose_char_window);
}

/**
 * User has hit return in either name or password box.  If both
 * boxes have non empty data, process request.  Otherwise,
 * either stay in same box if this box is empty, or move
 * to the other box.
 *
 * @param entry
 * Entry widget which generated this callback.
 */
void on_entry_character(GtkEntry *entry, gpointer user_data) {
    const char *name, *password;

    name =  gtk_entry_get_text(GTK_ENTRY(entry_character_name));
    password =  gtk_entry_get_text(GTK_ENTRY(entry_character_password));

    if (name && name[0] && password && password[0]) {
        add_character_to_account(name, password, 0);
    } else {
        const char *cp;

        /* First case - this widget is empty - do nothing */
        cp = gtk_entry_get_text(entry);
        if (!cp || !cp[0]) return;

        /* In this case, this widget is not empty - means the
         * other one is.
         */
        if (entry == GTK_ENTRY(entry_character_name))
            gtk_widget_grab_focus(entry_character_password);
        else
            gtk_widget_grab_focus(entry_character_name);
    }
}



static void init_add_character_window() {
    GladeXML *xml_tree;                                                                                      

    add_character_window = glade_xml_get_widget(dialog_xml, "add_character_window");
    gtk_window_set_transient_for(GTK_WINDOW(add_character_window), GTK_WINDOW(window_root));

    xml_tree = glade_get_widget_tree(GTK_WIDGET(add_character_window));
    button_do_add_character = glade_xml_get_widget(dialog_xml,"button_do_add_character");
    button_return_character_select =
        glade_xml_get_widget(dialog_xml,"button_return_character_select");
    entry_character_name = glade_xml_get_widget(dialog_xml,"entry_character_name");
    entry_character_password = glade_xml_get_widget(dialog_xml,"entry_character_password");
    label_add_status = glade_xml_get_widget(dialog_xml,"label_add_status");

    g_signal_connect ((gpointer) button_do_add_character, "clicked",
                      G_CALLBACK (on_button_do_add_character_clicked), NULL);

    g_signal_connect ((gpointer) button_return_character_select, "clicked",
                      G_CALLBACK (on_button_return_character_select_clicked), NULL);
    g_signal_connect ((gpointer) entry_character_name, "activate",
                      G_CALLBACK (on_entry_character), NULL);
    g_signal_connect ((gpointer) entry_character_password, "activate",
                      G_CALLBACK (on_entry_character), NULL);
}

/*****************************************************************************
 * choose_char_window
 ****************************************************************************/

/**
 * this is called when we get the accountplayers command from
 * the server (indirectly via AccountPlayersCmd).  This tells
 * us to wipe any data from the treeview, but also hide
 * any other windows and make the choose_character_window
 * visible.
 */
void choose_character_init()
{
    gtk_widget_hide(login_window);
    gtk_widget_hide(add_character_window);
    gtk_widget_hide(create_account_window);
    gtk_widget_show(choose_char_window);

    /* Store any old/stale entries */
    gtk_list_store_clear(character_store);
}

/**
 * User has done necessary steps to play a
 * character.
 */
static void play_character(const char *name)
{
    SockList sl;
    uint8 buf[MAX_BUF];

    SockList_Init(&sl, buf);
    SockList_AddString(&sl, "accountplay ");
    SockList_AddString(&sl, name);
    SockList_Send(&sl, csocket.fd);
}

/**
 * User has hit the play character button.  Grab
 * the selected entry, if there is one. 
 */
void
on_button_play_character_clicked (GtkButton *button, gpointer user_data)
{
    GtkTreeSelection *selected;
    GtkTreeModel    *model;
    GtkTreeIter iter;
    char *name;

    selected = gtk_tree_view_get_selection(GTK_TREE_VIEW(treeview_choose_character));

    if (gtk_tree_selection_get_selected (selected, &model, &iter)) {
        gtk_tree_model_get(model, &iter, CHAR_NAME, &name, -1);

        play_character(name);
    }
}

void
on_button_create_character_clicked (GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(choose_char_window);
    gtk_widget_show(new_character_window);
    gtk_entry_set_text(GTK_ENTRY(entry_new_character_name), "");
}

/**
 * User has hit the add character button.
 * hide this window, show the add character window.
 */
void
on_button_add_character_clicked (GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(choose_char_window);
    gtk_widget_show(add_character_window);
    gtk_entry_set_text(GTK_ENTRY(entry_character_name), "");
    gtk_entry_set_text(GTK_ENTRY(entry_character_password), "");
    gtk_widget_grab_focus(entry_character_name);
}

/**
 * User has hit the return to login window.
 * hide this window, show the account login window.
 */
void
on_button_return_login_clicked(GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(choose_char_window);
    gtk_widget_show(login_window);
}

/**
 * This gets data and adds it to the list store.  This is called
 * from AccountPlayersCmd and data is from the accountplayers protocol
 * command.
 *
 * @params ALL
 * data to add to the list store.
 */
void update_character_choose(const char *name, const char *class,
                             const char *race, const char *face, 
                             const char *party, const char *map,
                             int level)
{
    GtkTreeIter iter;

    gtk_list_store_append(character_store, &iter);

    gtk_list_store_set(character_store, &iter,
                       CHAR_NAME, name,
                       CHAR_CLASS, class,
                       CHAR_RACE, race,
                       CHAR_IMAGE, face,
                       CHAR_PARTY, party,
                       CHAR_MAP, map,
                       CHAR_LEVEL, level,
                       -1);

}


/**
 * User has double clicked one of the character rows,
 * so use that character as the one to play.
 *
 * @param treeview
 * treeview which activated that (should always be treeview_choose_character)
 * @param path
 * mechanism to get to selected entry
 * @param column
 * activated column?
 * @param user_data
 * not set
 */
void on_treeview_choose_character_activated(GtkTreeView *treeview,
                                        GtkTreePath     *path,
                                        GtkTreeViewColumn *column,
                                        gpointer         user_data)
{
    GtkTreeIter iter;
    GtkTreeModel    *model;
    char *name;

    model = gtk_tree_view_get_model(treeview);
    if (gtk_tree_model_get_iter(model, &iter, path)) {
        gtk_tree_model_get(model, &iter, CHAR_NAME, &name, -1);

        if (!name) {
            LOG(LOG_ERROR,"account.c:on_treeview_choose_character_activated", "unable to get character name");
            return;
        }
        play_character(name);
    }
}



static void init_choose_char_window()
{
    GladeXML *xml_tree;                                                                                      
    GtkTextIter end;
    GtkCellRenderer *renderer;
    GtkTreeViewColumn *column;

    choose_char_window = glade_xml_get_widget(dialog_xml, "choose_character_window");
    gtk_window_set_transient_for(GTK_WINDOW(choose_char_window), GTK_WINDOW(window_root));

    xml_tree = glade_get_widget_tree(GTK_WIDGET(choose_char_window));
    button_play_character = glade_xml_get_widget(dialog_xml,"button_play_character");
    button_create_character = glade_xml_get_widget(dialog_xml,"button_create_character");
    button_add_character = glade_xml_get_widget(dialog_xml,"button_add_character");
    button_return_login = glade_xml_get_widget(dialog_xml,"button_return_login");
    login_pane[TEXTVIEW_RULES_CHAR].textview =
        glade_xml_get_widget(dialog_xml,"textview_rules_char");
    textbuf_rules_char = gtk_text_view_get_buffer(GTK_TEXT_VIEW(login_pane[TEXTVIEW_RULES_CHAR].textview));
    treeview_choose_character = glade_xml_get_widget(dialog_xml,"treeview_choose_character");

    add_tags_to_textbuffer(&login_pane[TEXTVIEW_RULES_CHAR], textbuf_rules_char);
    add_style_to_textbuffer(&login_pane[TEXTVIEW_RULES_CHAR], NULL);
    gtk_text_buffer_get_end_iter(login_pane[TEXTVIEW_RULES_CHAR].textbuffer, &end);
    login_pane[TEXTVIEW_RULES_CHAR].textmark = gtk_text_buffer_create_mark(
                        login_pane[TEXTVIEW_RULES_CHAR].textbuffer, NULL, &end, FALSE);

    g_signal_connect ((gpointer) button_play_character, "clicked",
                      G_CALLBACK (on_button_play_character_clicked), NULL);
    g_signal_connect ((gpointer) button_create_character, "clicked",
                      G_CALLBACK (on_button_create_character_clicked), NULL);
    g_signal_connect ((gpointer) button_add_character, "clicked",
                      G_CALLBACK (on_button_add_character_clicked), NULL);
    g_signal_connect ((gpointer) button_return_login, "clicked",
                      G_CALLBACK (on_button_return_login_clicked), NULL);
    g_signal_connect ((gpointer) treeview_choose_character, "row_activated",
                      G_CALLBACK (on_treeview_choose_character_activated), NULL);

    character_store = gtk_list_store_new(CHAR_NUM_COLUMNS,
                                         G_TYPE_STRING, G_TYPE_STRING,
                                         G_TYPE_STRING, G_TYPE_STRING,
                                         G_TYPE_INT, G_TYPE_STRING,
                                         G_TYPE_STRING, G_TYPE_OBJECT);
    gtk_tree_view_set_model(GTK_TREE_VIEW(treeview_choose_character),
                            GTK_TREE_MODEL(character_store));

    renderer = gtk_cell_renderer_pixbuf_new();
    column = gtk_tree_view_column_new_with_attributes ("?", renderer,
                                                       "pixbuf", CHAR_ICON,
                                                      NULL);

    gtk_tree_view_column_set_min_width(column, image_size);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Character Name", renderer,
                                                       "text", CHAR_NAME, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_NAME);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Class", renderer,
                                                       "text", CHAR_CLASS, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_CLASS);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Race", renderer,
                                                       "text", CHAR_RACE, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_RACE);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Level", renderer,
                                                       "text", CHAR_LEVEL, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_LEVEL);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Party", renderer,
                                                       "text", CHAR_PARTY, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_PARTY);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

    renderer = gtk_cell_renderer_text_new();
    column = gtk_tree_view_column_new_with_attributes ("Map", renderer,
                                                       "text", CHAR_MAP, NULL);
    gtk_tree_view_column_set_sort_column_id(column, CHAR_MAP);
    gtk_tree_view_append_column (GTK_TREE_VIEW(treeview_choose_character), column);

}

/******************************************************************************
 * create_account_window
 ******************************************************************************/

/**
 * handles a failure from the server - pretty basic - just
 * throw up the message and let the user try again.
 */
void account_creation_failure(char *message)
{
    gtk_label_set_text(GTK_LABEL(label_create_account_status), message);
}

/**
 * This does sanity checking of the passed in data, and if all
 * is good, sends the request to the server to create an account.
 * If all the data isn't good, it puts up an error message.  In
 * this routine, none of the entries should be NULL - the caller
 * should verify that before callin do_account_create();
 *
 * @param name
 * desired account name - must not be NULL.
 * @param p1
 * the first password - must not be NULL
 * @param p2
 * the second (confirmed) password.  This routine checks that p1 & p2
 * are the same, and if not, puts up an error.  p2 must not be NULL
 */
static void do_account_create(const char *name, const char *p1, const char *p2)
{
    SockList sl;
    uint8 buf[MAX_BUF];

    if (strcmp(p1, p2)) {
        gtk_label_set_text(GTK_LABEL(label_create_account_status),
                      "The passwords you entered do not match!");
        return;
    } else {
        gtk_label_set_text(GTK_LABEL(label_create_account_status),
                      "");
        SockList_Init(&sl, buf);
        SockList_AddString(&sl, "accountnew ");
        SockList_AddChar(&sl, strlen(name));
        SockList_AddString(&sl, name);
        SockList_AddChar(&sl, strlen(p1));
        SockList_AddString(&sl, p1);
        SockList_Send(&sl, csocket.fd);
        /* Store password away for new character creation */
        snprintf(account_password, 256, p1);
    }
}

/**
 * User clicked on the create account button.  In this case,
 * we just process the data and call do_account_create();
 */
void
on_button_new_create_account_clicked (GtkButton *button, gpointer user_data)
{
    const char *password1, *password2, *name;

    password1 = gtk_entry_get_text(GTK_ENTRY(entry_new_account_password));
    password2 = gtk_entry_get_text(GTK_ENTRY(entry_new_confirm_password));
    name = gtk_entry_get_text(GTK_ENTRY(entry_new_account_name));

    if (name && name[0] && password1 && password1[0] && password2 && password2[0]) {
        do_account_create(name, password1, password2);
    } else {
        gtk_label_set_text(GTK_LABEL(label_create_account_status),
                      "You must fill in all three entries!");
    }
}

void
on_button_new_cancel_clicked (GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(create_account_window);
    gtk_widget_show(login_window);
}

/**
 * This handles cases where the user hits return in
 * one of the entry boxes.  We use the same callback for
 * all 3 entry boxes, since the processing is basically
 * the same - if there is valid data in all of them,
 * we try to create an account - otherwise, we move
 * to the next box.
 *
 * @params entry
 * entry box - we use this to figure out what the next box is.
 * @params user_data
 * not used.
 */
void
on_entry_new_account (GtkEntry *entry, gpointer user_data) {

    const char *password1, *password2, *name, *cp;

    password1 = gtk_entry_get_text(GTK_ENTRY(entry_new_account_password));
    password2 = gtk_entry_get_text(GTK_ENTRY(entry_new_confirm_password));
    name = gtk_entry_get_text(GTK_ENTRY(entry_new_account_name));
    if (name && name[0] && password1 && password1[0] && password2 && password2[0]) {
        do_account_create(name, password1, password2);
    } else {
        /* In this case, one, or more, of the fields is blank.  If
         * there were more than 3 widgets, I might but them into an
         * array to make cycling easier
         */

        /* First case - if the currently active one is blank, no
         * reason to move onward.
         */
        cp = gtk_entry_get_text(entry);
        if (!cp || !cp[0]) return;

        /* I'm not sure if it would make more sense to advance
         * to the first NULL entry - but in that case, the pointer
         * may hop in non intuitive ways - in this case, the user
         * may just need to hit return a few times - MSW 2010/03/29
         */
        if (entry == GTK_ENTRY(entry_new_account_name))
            gtk_widget_grab_focus(entry_new_account_password);
        else if  (entry == GTK_ENTRY(entry_new_account_password))
            gtk_widget_grab_focus(entry_new_confirm_password);
        else if  (entry == GTK_ENTRY(entry_new_confirm_password))
            gtk_widget_grab_focus(entry_new_account_name);
    }
}

/**
 * This initializes the create account window and sets up the
 * various callbacks.
 */
static void init_create_account_window()
{
    GladeXML *xml_tree;                                                                              
    GtkTextIter end;

    create_account_window = glade_xml_get_widget(dialog_xml, "create_account_window");
    gtk_window_set_transient_for(GTK_WINDOW(create_account_window), GTK_WINDOW(window_root));

    xml_tree = glade_get_widget_tree(GTK_WIDGET(create_account_window));
    button_new_create_account = glade_xml_get_widget(dialog_xml,"button_new_create_account");
    button_new_cancel = glade_xml_get_widget(dialog_xml,"button_new_cancel");
    login_pane[TEXTVIEW_RULES_ACCOUNT].textview = glade_xml_get_widget(dialog_xml,"textview_rules_account");
    textbuf_rules_account = gtk_text_view_get_buffer(
                                   GTK_TEXT_VIEW(login_pane[TEXTVIEW_RULES_ACCOUNT].textview ));
    entry_new_account_name = glade_xml_get_widget(dialog_xml,"entry_new_account_name");
    entry_new_account_password = glade_xml_get_widget(dialog_xml,"entry_new_account_password");
    entry_new_confirm_password = glade_xml_get_widget(dialog_xml,"entry_new_confirm_password");
    label_create_account_status = glade_xml_get_widget(dialog_xml,"label_create_account_status");

    add_tags_to_textbuffer(&login_pane[TEXTVIEW_RULES_ACCOUNT], textbuf_rules_account);
    add_style_to_textbuffer(&login_pane[TEXTVIEW_RULES_ACCOUNT], NULL);
    gtk_text_buffer_get_end_iter(login_pane[TEXTVIEW_RULES_ACCOUNT].textbuffer, &end);
    login_pane[TEXTVIEW_RULES_ACCOUNT].textmark = gtk_text_buffer_create_mark(
                        login_pane[TEXTVIEW_RULES_ACCOUNT].textbuffer, NULL, &end, FALSE);

    g_signal_connect ((gpointer) button_new_create_account, "clicked",
                      G_CALLBACK (on_button_new_create_account_clicked), NULL);
    g_signal_connect ((gpointer) button_new_cancel, "clicked",
                      G_CALLBACK (on_button_new_cancel_clicked), NULL);
    g_signal_connect ((gpointer) entry_new_account_name, "activate",
                      G_CALLBACK (on_entry_new_account), NULL);
    g_signal_connect ((gpointer) entry_new_account_password, "activate",
                      G_CALLBACK (on_entry_new_account), NULL);
    g_signal_connect ((gpointer) entry_new_confirm_password, "activate",
                      G_CALLBACK (on_entry_new_account), NULL);

}


/*****************************************************************************
 * login_window
 *****************************************************************************/

/**
 * handles a failure from the server - pretty basic - just
 * throw up the message and let the user try again.
 */
void account_login_failure(char *message)
{
    gtk_label_set_text(GTK_LABEL(label_account_login_status), message);
}


/**
 * User hit the create account button.  So we need to hide the login
 * window and bring up the create login window.
 */
void
on_button_create_account_clicked (GtkButton *button, gpointer user_data)
{
    gtk_widget_hide(login_window);
    gtk_widget_show(create_account_window);
}

/**
 * User hit the go to metaserver button.  Need to disconnect from
 * The server, and by clearing the csocket_fd, the main loop routine
 * will bring up the metaserver window.
 */
void
on_button_go_metaserver_clicked (GtkButton *button, gpointer user_data)
{
    close_server_connection();

    if (csocket_fd) {
        gdk_input_remove(csocket_fd);
        csocket_fd=0;
        gtk_main_quit();
    }
}

/**
 * User hit the exit client button.  Pretty simple in this case.
 */
void
on_button_exit_client_clicked (GtkButton *button, gpointer user_data)
{
#ifdef WIN32
    script_killall();
#endif
    exit(0);
}


/**
 * This does the work of doing the login - mostly it just
 * sends the request to the server.  However, this might
 * be called from either hitting the login button or 
 * entering data in name/password and hitting return.
 */
static void do_account_login(const char *name, const char *password)
{
    SockList sl;
    uint8 buf[MAX_BUF];

    if (!name || !password || *name == 0 || *password == 0) {
        gtk_label_set_text(GTK_LABEL(label_account_login_status),
                      "You must enter both a name and password!");
    } else {
        gtk_label_set_text(GTK_LABEL(label_account_login_status), "");

        SockList_Init(&sl, buf);
        SockList_AddString(&sl, "accountlogin ");
        SockList_AddChar(&sl, strlen(name));
        SockList_AddString(&sl, name);
        SockList_AddChar(&sl, strlen(password));
        SockList_AddString(&sl, password);
        SockList_Send(&sl, csocket.fd);
        /* Store password away for new character creation */
        snprintf(account_password, 256, password);
    }
}

/**
 * User hit the login button - just call do_account_login()
 */
void
on_button_login_clicked (GtkButton *button, gpointer user_data)
{
    do_account_login(gtk_entry_get_text(GTK_ENTRY(entry_account_name)),
                     gtk_entry_get_text(GTK_ENTRY(entry_account_password)));
}

/**
 * User hit return in the name entry box.  If there is data in the password
 * box, attempt login, otherwise make the password box active.
 */
void
on_entry_account_name_activate (GtkEntry *entry, gpointer user_data) {
    const char *password;

    password = gtk_entry_get_text(GTK_ENTRY(entry_account_password));

    if (!password || *password == 0) {
        gtk_widget_grab_focus(entry_account_password);
    } else {
        do_account_login(gtk_entry_get_text(GTK_ENTRY(entry_account_name)), password);
    }
}

/**
 * user hit return in the password box.  Like above, if name data, do login,
 * otherwise make the name box active.
 */
void
on_entry_account_password_activate (GtkEntry *entry, gpointer user_data) {
    const char *name;

    name = gtk_entry_get_text(GTK_ENTRY(entry_account_name));

    if (!name || *name == 0) {
        gtk_widget_grab_focus(entry_account_name);
    } else {
        do_account_login(name, gtk_entry_get_text(GTK_ENTRY(entry_account_password)));
    }
}

/**
 * This just sets up all the widget pointers, as well as setting
 * up the callbacks for the login windows widgets.
 */
static void init_login_window()
{
    GladeXML *xml_tree;
    GtkTextIter end;

    login_window = glade_xml_get_widget(dialog_xml, "login_window");
    gtk_window_set_transient_for(GTK_WINDOW(login_window), GTK_WINDOW(window_root));

    xml_tree = glade_get_widget_tree(GTK_WIDGET(login_window));
    button_login = glade_xml_get_widget(dialog_xml,"button_login");
    button_create_account = glade_xml_get_widget(dialog_xml,"button_create_account");
    button_go_metaserver = glade_xml_get_widget(dialog_xml,"button_go_metaserver");
    button_exit_client = glade_xml_get_widget(dialog_xml,"button_exit_client");

    label_account_login_status = glade_xml_get_widget(dialog_xml,"label_account_login_status");

    login_pane[TEXTVIEW_MOTD].textview = glade_xml_get_widget(dialog_xml,"textview_motd");
    textbuf_motd = gtk_text_view_get_buffer(GTK_TEXT_VIEW(login_pane[TEXTVIEW_MOTD].textview));

    add_tags_to_textbuffer(&login_pane[TEXTVIEW_MOTD], textbuf_motd);
    add_style_to_textbuffer(&login_pane[TEXTVIEW_MOTD], NULL);
    gtk_text_buffer_get_end_iter(login_pane[TEXTVIEW_MOTD].textbuffer, &end);
    login_pane[TEXTVIEW_MOTD].textmark = gtk_text_buffer_create_mark(
                        login_pane[TEXTVIEW_MOTD].textbuffer, NULL, &end, FALSE);

    login_pane[TEXTVIEW_NEWS].textview = glade_xml_get_widget(dialog_xml,"textview_news");
    textbuf_news = gtk_text_view_get_buffer(GTK_TEXT_VIEW(login_pane[TEXTVIEW_NEWS].textview));

    add_tags_to_textbuffer(&login_pane[TEXTVIEW_NEWS], textbuf_news);
    add_style_to_textbuffer(&login_pane[TEXTVIEW_NEWS], NULL);
    gtk_text_buffer_get_end_iter(login_pane[TEXTVIEW_NEWS].textbuffer, &end);
    login_pane[TEXTVIEW_NEWS].textmark = gtk_text_buffer_create_mark(
                        login_pane[TEXTVIEW_NEWS].textbuffer, NULL, &end, FALSE);

    entry_account_name = glade_xml_get_widget(dialog_xml,"entry_account_name");
    entry_account_password = glade_xml_get_widget(dialog_xml,"entry_account_password");

    g_signal_connect ((gpointer) entry_account_name, "activate",
                      G_CALLBACK (on_entry_account_name_activate), NULL);

    g_signal_connect ((gpointer) entry_account_password, "activate",
                      G_CALLBACK (on_entry_account_password_activate), NULL);

    g_signal_connect ((gpointer) button_login, "clicked",
                      G_CALLBACK (on_button_login_clicked), NULL);
    g_signal_connect ((gpointer) button_create_account, "clicked",
                      G_CALLBACK (on_button_create_account_clicked), NULL);
    g_signal_connect ((gpointer) button_go_metaserver, "clicked",
                      G_CALLBACK (on_button_go_metaserver_clicked), NULL);
    g_signal_connect ((gpointer) button_exit_client, "clicked",
                      G_CALLBACK (on_button_exit_client_clicked), NULL);

}

/*****************************************************************************
 * Common/generic functions
 ****************************************************************************/

/**
 * This is called from ReplyInfoCmd when it gets a response from
 * news/motd/rules.  It is very possible that the window
 * will get displayed before we got a reply response, so
 * this tells the client to update it.
 *
 *@params type
 * what data just got updated - text string of motd/news/rules
 */
void update_login_info(int type)
{

    if (!has_init) return;

    /* In all cases, we clear the buffer, and if we have
     * data, then set it to that data.  This routine could
     * be smarter an
     */
    if (type == INFO_NEWS) {
        gtk_text_buffer_set_text(textbuf_news, "", 0);
        if (news) {
            /* the format of the news entry is special - there are a series
             * of %entries, and they are in reverse older (newest last)
             * we want to get rid of the %, make them more visible (convert them
             * to bold) and reverse the order.
             */
            char *mynews, *cp, *el, big_buf[BIG_BUF], *cp1;

            mynews = strdup(news);
            /* We basically work from the end of the string going towards
             * the start looking for % characters.  If we find one, we
             * have to make sure it is at the start of the line or start
             * of the buffer
             */
            for (cp = mynews + strlen(mynews); cp > mynews; cp--) {
                if (*cp == '%' && (*(cp-1) == '\n' || cp == mynews)) {
                    /* Find the end of the line */
                    el = strchr(cp, '\n');
                    /* null out the newline, put el one char beyond it */
                    if (el) {
                        *el=0;
                        el++;
                    }
                    /* There isn't a clear standard - % news may be valid,
                     * as might be %news.  If % news is used, it looks better
                     * to get rid of that leading space.
                     */
                    cp1 = cp+1;
                    while (isspace(*cp1)) cp1++;

                    /* since we've null out the newline, this snprintf will
                     * only get the % line and that is it.  Mark it up
                     */
                    snprintf(big_buf, BIG_BUF, "[b]%s[/b]", cp1);
                    add_marked_text_to_pane(&login_pane[TEXTVIEW_NEWS], big_buf, 0, 0, 0);
                    /* Now we draw the text that goes with it, if it exists */
                    if (el)
                        add_marked_text_to_pane(&login_pane[TEXTVIEW_NEWS], el, 0, 0, 0);

                    /* Now we wipe the % out.  In this way, the news buffer is shorter,
                     * so when it draws the ext, there will just be that between the %
                     * and the one we just wiped out.
                     */
                    *cp = 0;
                }
            }
            /* If there are remnants left over, or perhaps
             * the news file isn't formatted with % headers, display
             * what we have got.
             */
            if (*mynews != 0)
                add_marked_text_to_pane(&login_pane[TEXTVIEW_NEWS], mynews, 0, 0, 0);

        }
    }
    else if (type == INFO_MOTD) {
        gtk_text_buffer_set_text(textbuf_motd, "", 0);
        if (motd) 
            add_marked_text_to_pane(&login_pane[TEXTVIEW_MOTD], motd, 0, 0, 0);
    }
    else if (type == INFO_RULES) {
        gtk_text_buffer_set_text(textbuf_rules_account, "", 0);
        gtk_text_buffer_set_text(textbuf_rules_char, "", 0);

        if (rules) {
            add_marked_text_to_pane(&login_pane[TEXTVIEW_RULES_ACCOUNT], rules, 0, 0, 0);
            add_marked_text_to_pane(&login_pane[TEXTVIEW_RULES_CHAR], rules, 0, 0, 0);
        }
    }

}


/**
 * Starts the login process.  If not already done, gets widgets,
 * sets up callboacks, etc.  This is at the end of the file
 * so all the callbacks are defined before this function - in
 * that way, we do not need forward declarations.
 * This is called from SetupCmd in common/commands.c
 *
 * @param method
 * login method that the server suppots.
 */
void start_login(int method)
{

    /* Store this away - if method is only 1, we can not
     * do smart character creation.
     */
    serverloginmethod = method;

    if (!has_init) {
        /* Since there are 4 windows associated with account and character
         * login, to make life a little easier, each section here does
         * all the work for one window, so it is easier to see
         * that everything for a window is done - don't need to hunt
         * through what would otherwise be a long routine looking
         * for entries.
         */
        init_add_character_window();

        init_choose_char_window();

        init_login_window();

        init_create_account_window();

        init_new_character_window();

        has_init=1;

        /* In case we have gotten news/motd/rules before getting here, 
         * update it now.
         */
        update_login_info(INFO_NEWS);
        update_login_info(INFO_RULES);
        update_login_info(INFO_MOTD);
    }

    gtk_entry_set_text(GTK_ENTRY(entry_account_name), "");
    gtk_entry_set_text(GTK_ENTRY(entry_account_password), "");
    /* We set focus to account name - this makes the most sense if user is
     * logging in again - it is possible that the password is active,
     * but both fields are blank, which is not what is expected.
     */
    gtk_widget_grab_focus(entry_account_name);
    gtk_widget_show(login_window);

}

