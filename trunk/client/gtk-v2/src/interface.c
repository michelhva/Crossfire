/*
 * DO NOT EDIT THIS FILE - it is generated by Glade.
 */

#ifdef HAVE_CONFIG_H
#  include <config.h>
#endif

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>

#include <gdk/gdkkeysyms.h>
#include <gtk/gtk.h>

#include "callbacks.h"
#include "interface.h"
#include "support.h"

#define GLADE_HOOKUP_OBJECT(component,widget,name) \
  g_object_set_data_full (G_OBJECT (component), name, \
    gtk_widget_ref (widget), (GDestroyNotify) gtk_widget_unref)

#define GLADE_HOOKUP_OBJECT_NO_REF(component,widget,name) \
  g_object_set_data (G_OBJECT (component), name, widget)

GtkWidget*
create_window_root (void)
{
  GtkWidget *window_root;
  GtkWidget *vbox2;
  GtkWidget *hbox6;
  GtkWidget *menubar1;
  GtkWidget *menuitem1;
  GtkWidget *menuitem1_menu;
  GtkWidget *quit_character1;
  GtkWidget *quit2;
  GtkWidget *menuitem4;
  GtkWidget *menuitem4_menu;
  GtkWidget *about1;
  GtkWidget *fire_on;
  GtkWidget *run_on;
  GtkWidget *filler;
  GtkWidget *hpaned_map_other;
  GtkWidget *vpaned_map_stats;
  GtkWidget *notebook4;
  GtkWidget *table_map;
  GtkWidget *drawingarea_map;
  GtkWidget *hscrollbar_map;
  GtkWidget *vscrollbar_map;
  GtkWidget *button_map_recenter;
  GtkWidget *label46;
  GtkWidget *drawingarea_magic_map;
  GtkWidget *label47;
  GtkWidget *hbox2;
  GtkWidget *table4;
  GtkWidget *label_stats_hp;
  GtkWidget *label_stats_sp;
  GtkWidget *label_stats_grace;
  GtkWidget *label_stats_food;
  GtkWidget *progressbar_hp;
  GtkWidget *progressbar_sp;
  GtkWidget *progressbar_grace;
  GtkWidget *progressbar_food;
  GtkWidget *fire_label;
  GtkWidget *run_label;
  GtkWidget *notebook3;
  GtkWidget *vbox6;
  GtkWidget *label_playername;
  GtkWidget *hbox8;
  GtkWidget *label49;
  GtkWidget *label_str;
  GtkWidget *label51;
  GtkWidget *label_dex;
  GtkWidget *label53;
  GtkWidget *label_con;
  GtkWidget *label55;
  GtkWidget *label_int;
  GtkWidget *label57;
  GtkWidget *label_wis;
  GtkWidget *label59;
  GtkWidget *label_pow;
  GtkWidget *label61;
  GtkWidget *label_cha;
  GtkWidget *hbox4;
  GtkWidget *label32;
  GtkWidget *label_wc;
  GtkWidget *label34;
  GtkWidget *label_dam;
  GtkWidget *label36;
  GtkWidget *label_ac;
  GtkWidget *label38;
  GtkWidget *label_armor;
  GtkWidget *hbox9;
  GtkWidget *label64;
  GtkWidget *label_speed;
  GtkWidget *label66;
  GtkWidget *label_weapon_speed;
  GtkWidget *label_range;
  GtkWidget *hbox7;
  GtkWidget *label_exp;
  GtkWidget *label_level;
  GtkWidget *label14;
  GtkWidget *table_skills_exp;
  GtkWidget *label15;
  GtkWidget *table_protections;
  GtkWidget *label16;
  GtkWidget *vpaned_info_inventory;
  GtkWidget *vbox_info_entry;
  GtkWidget *notebook_info;
  GtkWidget *scrolledwindow1;
  GtkWidget *textview_info1;
  GtkWidget *label1;
  GtkWidget *scrolledwindow2;
  GtkWidget *textview_info2;
  GtkWidget *label2;
  GtkWidget *entry_commands;
  GtkWidget *vpaned3;
  GtkWidget *vbox3;
  GtkWidget *hbox1;
  GtkWidget *label3;
  GtkWidget *label_inv_weight;
  GtkWidget *label5;
  GtkObject *spinbutton_count_adj;
  GtkWidget *spinbutton_count;
  GtkWidget *notebook_inv;
  GtkWidget *scrolledwindow6;
  GtkWidget *viewport1;
  GtkWidget *inv_table;
  GtkWidget *label80;
  GtkWidget *vbox4;
  GtkWidget *label9;
  GtkWidget *scrolledwindow5;
  GtkWidget *treeview_look;

  window_root = gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_widget_set_size_request (window_root, 1200, 1010);
  gtk_window_set_title (GTK_WINDOW (window_root), _("Crossfire Client - GTK v2"));

  vbox2 = gtk_vbox_new (FALSE, 0);
  gtk_widget_show (vbox2);
  gtk_container_add (GTK_CONTAINER (window_root), vbox2);

  hbox6 = gtk_hbox_new (FALSE, 0);
  gtk_widget_show (hbox6);
  gtk_box_pack_start (GTK_BOX (vbox2), hbox6, FALSE, FALSE, 0);

  menubar1 = gtk_menu_bar_new ();
  gtk_widget_show (menubar1);
  gtk_box_pack_start (GTK_BOX (hbox6), menubar1, FALSE, FALSE, 0);

  menuitem1 = gtk_menu_item_new_with_mnemonic (_("_File"));
  gtk_widget_show (menuitem1);
  gtk_container_add (GTK_CONTAINER (menubar1), menuitem1);

  menuitem1_menu = gtk_menu_new ();
  gtk_menu_item_set_submenu (GTK_MENU_ITEM (menuitem1), menuitem1_menu);

  quit_character1 = gtk_menu_item_new_with_mnemonic (_("Quit Character"));
  gtk_widget_show (quit_character1);
  gtk_container_add (GTK_CONTAINER (menuitem1_menu), quit_character1);

  quit2 = gtk_menu_item_new_with_mnemonic (_("_Quit"));
  gtk_widget_show (quit2);
  gtk_container_add (GTK_CONTAINER (menuitem1_menu), quit2);

  menuitem4 = gtk_menu_item_new_with_mnemonic (_("_Help"));
  gtk_widget_show (menuitem4);
  gtk_container_add (GTK_CONTAINER (menubar1), menuitem4);

  menuitem4_menu = gtk_menu_new ();
  gtk_menu_item_set_submenu (GTK_MENU_ITEM (menuitem4), menuitem4_menu);

  about1 = gtk_menu_item_new_with_mnemonic (_("_About"));
  gtk_widget_show (about1);
  gtk_container_add (GTK_CONTAINER (menuitem4_menu), about1);

  fire_on = gtk_label_new ("");
  gtk_widget_show (fire_on);
  gtk_box_pack_start (GTK_BOX (hbox6), fire_on, TRUE, FALSE, 0);

  run_on = gtk_label_new ("");
  gtk_widget_show (run_on);
  gtk_box_pack_start (GTK_BOX (hbox6), run_on, FALSE, TRUE, 0);

  filler = gtk_label_new ("");
  gtk_widget_show (filler);
  gtk_box_pack_start (GTK_BOX (hbox6), filler, FALSE, FALSE, 0);

  hpaned_map_other = gtk_hpaned_new ();
  gtk_widget_show (hpaned_map_other);
  gtk_box_pack_start (GTK_BOX (vbox2), hpaned_map_other, TRUE, TRUE, 0);
  gtk_paned_set_position (GTK_PANED (hpaned_map_other), 820);

  vpaned_map_stats = gtk_vpaned_new ();
  gtk_widget_show (vpaned_map_stats);
  gtk_paned_pack1 (GTK_PANED (hpaned_map_other), vpaned_map_stats, FALSE, TRUE);
  gtk_paned_set_position (GTK_PANED (vpaned_map_stats), 847);

  notebook4 = gtk_notebook_new ();
  gtk_widget_show (notebook4);
  gtk_paned_pack1 (GTK_PANED (vpaned_map_stats), notebook4, FALSE, TRUE);
  gtk_notebook_set_show_border (GTK_NOTEBOOK (notebook4), FALSE);

  table_map = gtk_table_new (2, 2, FALSE);
  gtk_widget_show (table_map);
  gtk_container_add (GTK_CONTAINER (notebook4), table_map);

  drawingarea_map = gtk_drawing_area_new ();
  gtk_widget_show (drawingarea_map);
  gtk_table_attach (GTK_TABLE (table_map), drawingarea_map, 0, 1, 0, 1,
                    (GtkAttachOptions) (GTK_EXPAND | GTK_FILL),
                    (GtkAttachOptions) (GTK_EXPAND | GTK_FILL), 0, 0);
  gtk_widget_set_size_request (drawingarea_map, 800, 800);

  hscrollbar_map = gtk_hscrollbar_new (GTK_ADJUSTMENT (gtk_adjustment_new (50, 0, 100, 1, 0, 0)));
  gtk_widget_show (hscrollbar_map);
  gtk_table_attach (GTK_TABLE (table_map), hscrollbar_map, 0, 1, 1, 2,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (GTK_EXPAND | GTK_FILL), 0, 0);

  vscrollbar_map = gtk_vscrollbar_new (GTK_ADJUSTMENT (gtk_adjustment_new (50, 0, 100, 0, 0, 0)));
  gtk_widget_show (vscrollbar_map);
  gtk_table_attach (GTK_TABLE (table_map), vscrollbar_map, 1, 2, 0, 1,
                    (GtkAttachOptions) (GTK_EXPAND | GTK_FILL),
                    (GtkAttachOptions) (GTK_FILL), 0, 0);

  button_map_recenter = gtk_button_new_with_mnemonic ("");
  gtk_widget_show (button_map_recenter);
  gtk_table_attach (GTK_TABLE (table_map), button_map_recenter, 1, 2, 1, 2,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  label46 = gtk_label_new (_("Map"));
  gtk_widget_show (label46);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook4), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook4), 0), label46);

  drawingarea_magic_map = gtk_drawing_area_new ();
  gtk_widget_show (drawingarea_magic_map);
  gtk_container_add (GTK_CONTAINER (notebook4), drawingarea_magic_map);

  label47 = gtk_label_new (_("Magic Map"));
  gtk_widget_show (label47);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook4), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook4), 1), label47);

  hbox2 = gtk_hbox_new (FALSE, 0);
  gtk_widget_show (hbox2);
  gtk_paned_pack2 (GTK_PANED (vpaned_map_stats), hbox2, TRUE, TRUE);

  table4 = gtk_table_new (5, 2, TRUE);
  gtk_widget_show (table4);
  gtk_box_pack_start (GTK_BOX (hbox2), table4, FALSE, TRUE, 0);
  gtk_table_set_row_spacings (GTK_TABLE (table4), 4);

  label_stats_hp = gtk_label_new (_("HP: 0/0"));
  gtk_widget_show (label_stats_hp);
  gtk_table_attach (GTK_TABLE (table4), label_stats_hp, 0, 1, 0, 1,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  label_stats_sp = gtk_label_new (_("Spell Points: 0/0"));
  gtk_widget_show (label_stats_sp);
  gtk_table_attach (GTK_TABLE (table4), label_stats_sp, 0, 1, 1, 2,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  label_stats_grace = gtk_label_new (_("Grace: 0/0"));
  gtk_widget_show (label_stats_grace);
  gtk_table_attach (GTK_TABLE (table4), label_stats_grace, 0, 1, 2, 3,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  label_stats_food = gtk_label_new (_("Food: 0/0"));
  gtk_widget_show (label_stats_food);
  gtk_table_attach (GTK_TABLE (table4), label_stats_food, 0, 1, 3, 4,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  progressbar_hp = gtk_progress_bar_new ();
  gtk_widget_show (progressbar_hp);
  gtk_table_attach (GTK_TABLE (table4), progressbar_hp, 1, 2, 0, 1,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  progressbar_sp = gtk_progress_bar_new ();
  gtk_widget_show (progressbar_sp);
  gtk_table_attach (GTK_TABLE (table4), progressbar_sp, 1, 2, 1, 2,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  progressbar_grace = gtk_progress_bar_new ();
  gtk_widget_show (progressbar_grace);
  gtk_table_attach (GTK_TABLE (table4), progressbar_grace, 1, 2, 2, 3,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  progressbar_food = gtk_progress_bar_new ();
  gtk_widget_show (progressbar_food);
  gtk_table_attach (GTK_TABLE (table4), progressbar_food, 1, 2, 3, 4,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);

  fire_label = gtk_label_new ("");
  gtk_widget_show (fire_label);
  gtk_table_attach (GTK_TABLE (table4), fire_label, 0, 1, 4, 5,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);
  gtk_misc_set_alignment (GTK_MISC (fire_label), 0, 0.5);

  run_label = gtk_label_new ("");
  gtk_widget_show (run_label);
  gtk_table_attach (GTK_TABLE (table4), run_label, 1, 2, 4, 5,
                    (GtkAttachOptions) (GTK_FILL),
                    (GtkAttachOptions) (0), 0, 0);
  gtk_misc_set_alignment (GTK_MISC (run_label), 0, 0.5);

  notebook3 = gtk_notebook_new ();
  gtk_widget_show (notebook3);
  gtk_box_pack_start (GTK_BOX (hbox2), notebook3, TRUE, TRUE, 0);

  vbox6 = gtk_vbox_new (TRUE, 0);
  gtk_widget_show (vbox6);
  gtk_container_add (GTK_CONTAINER (notebook3), vbox6);

  label_playername = gtk_label_new (_("Player:"));
  gtk_widget_show (label_playername);
  gtk_box_pack_start (GTK_BOX (vbox6), label_playername, FALSE, FALSE, 0);

  hbox8 = gtk_hbox_new (TRUE, 0);
  gtk_widget_show (hbox8);
  gtk_box_pack_start (GTK_BOX (vbox6), hbox8, TRUE, TRUE, 0);

  label49 = gtk_label_new (_("Str"));
  gtk_widget_show (label49);
  gtk_box_pack_start (GTK_BOX (hbox8), label49, FALSE, FALSE, 0);

  label_str = gtk_label_new ("");
  gtk_widget_show (label_str);
  gtk_box_pack_start (GTK_BOX (hbox8), label_str, FALSE, FALSE, 0);

  label51 = gtk_label_new (_("Dex"));
  gtk_widget_show (label51);
  gtk_box_pack_start (GTK_BOX (hbox8), label51, FALSE, FALSE, 0);

  label_dex = gtk_label_new ("");
  gtk_widget_show (label_dex);
  gtk_box_pack_start (GTK_BOX (hbox8), label_dex, FALSE, FALSE, 0);

  label53 = gtk_label_new (_("Con"));
  gtk_widget_show (label53);
  gtk_box_pack_start (GTK_BOX (hbox8), label53, FALSE, FALSE, 0);

  label_con = gtk_label_new ("");
  gtk_widget_show (label_con);
  gtk_box_pack_start (GTK_BOX (hbox8), label_con, FALSE, FALSE, 0);

  label55 = gtk_label_new (_("Int"));
  gtk_widget_show (label55);
  gtk_box_pack_start (GTK_BOX (hbox8), label55, FALSE, FALSE, 0);

  label_int = gtk_label_new ("");
  gtk_widget_show (label_int);
  gtk_box_pack_start (GTK_BOX (hbox8), label_int, FALSE, FALSE, 0);

  label57 = gtk_label_new (_("Wis"));
  gtk_widget_show (label57);
  gtk_box_pack_start (GTK_BOX (hbox8), label57, FALSE, FALSE, 0);

  label_wis = gtk_label_new ("");
  gtk_widget_show (label_wis);
  gtk_box_pack_start (GTK_BOX (hbox8), label_wis, FALSE, FALSE, 0);

  label59 = gtk_label_new (_("Pow"));
  gtk_widget_show (label59);
  gtk_box_pack_start (GTK_BOX (hbox8), label59, FALSE, FALSE, 0);

  label_pow = gtk_label_new ("");
  gtk_widget_show (label_pow);
  gtk_box_pack_start (GTK_BOX (hbox8), label_pow, FALSE, FALSE, 0);

  label61 = gtk_label_new (_("Cha"));
  gtk_widget_show (label61);
  gtk_box_pack_start (GTK_BOX (hbox8), label61, FALSE, FALSE, 0);

  label_cha = gtk_label_new ("");
  gtk_widget_show (label_cha);
  gtk_box_pack_start (GTK_BOX (hbox8), label_cha, FALSE, FALSE, 0);

  hbox4 = gtk_hbox_new (TRUE, 0);
  gtk_widget_show (hbox4);
  gtk_box_pack_start (GTK_BOX (vbox6), hbox4, TRUE, TRUE, 0);

  label32 = gtk_label_new (_("WC"));
  gtk_widget_show (label32);
  gtk_box_pack_start (GTK_BOX (hbox4), label32, FALSE, FALSE, 0);

  label_wc = gtk_label_new ("");
  gtk_widget_show (label_wc);
  gtk_box_pack_start (GTK_BOX (hbox4), label_wc, FALSE, FALSE, 0);

  label34 = gtk_label_new (_("Dam"));
  gtk_widget_show (label34);
  gtk_box_pack_start (GTK_BOX (hbox4), label34, FALSE, FALSE, 0);

  label_dam = gtk_label_new ("");
  gtk_widget_show (label_dam);
  gtk_box_pack_start (GTK_BOX (hbox4), label_dam, FALSE, FALSE, 0);

  label36 = gtk_label_new (_("AC"));
  gtk_widget_show (label36);
  gtk_box_pack_start (GTK_BOX (hbox4), label36, FALSE, FALSE, 0);

  label_ac = gtk_label_new ("");
  gtk_widget_show (label_ac);
  gtk_box_pack_start (GTK_BOX (hbox4), label_ac, FALSE, FALSE, 0);

  label38 = gtk_label_new (_("Armor"));
  gtk_widget_show (label38);
  gtk_box_pack_start (GTK_BOX (hbox4), label38, FALSE, FALSE, 0);

  label_armor = gtk_label_new ("");
  gtk_widget_show (label_armor);
  gtk_box_pack_start (GTK_BOX (hbox4), label_armor, FALSE, FALSE, 0);

  hbox9 = gtk_hbox_new (TRUE, 0);
  gtk_widget_show (hbox9);
  gtk_box_pack_start (GTK_BOX (vbox6), hbox9, FALSE, FALSE, 0);

  label64 = gtk_label_new (_("Speed"));
  gtk_widget_show (label64);
  gtk_box_pack_start (GTK_BOX (hbox9), label64, FALSE, FALSE, 0);

  label_speed = gtk_label_new ("");
  gtk_widget_show (label_speed);
  gtk_box_pack_start (GTK_BOX (hbox9), label_speed, FALSE, FALSE, 0);

  label66 = gtk_label_new (_("Weapon Speed"));
  gtk_widget_show (label66);
  gtk_box_pack_start (GTK_BOX (hbox9), label66, FALSE, FALSE, 0);

  label_weapon_speed = gtk_label_new ("");
  gtk_widget_show (label_weapon_speed);
  gtk_box_pack_start (GTK_BOX (hbox9), label_weapon_speed, FALSE, FALSE, 0);

  label_range = gtk_label_new (_("Range:"));
  gtk_widget_show (label_range);
  gtk_box_pack_start (GTK_BOX (vbox6), label_range, FALSE, FALSE, 0);

  hbox7 = gtk_hbox_new (TRUE, 0);
  gtk_widget_show (hbox7);
  gtk_box_pack_start (GTK_BOX (vbox6), hbox7, TRUE, TRUE, 0);

  label_exp = gtk_label_new (_("Experience:"));
  gtk_widget_show (label_exp);
  gtk_box_pack_start (GTK_BOX (hbox7), label_exp, FALSE, FALSE, 0);

  label_level = gtk_label_new (_("Level:"));
  gtk_widget_show (label_level);
  gtk_box_pack_start (GTK_BOX (hbox7), label_level, FALSE, FALSE, 0);

  label14 = gtk_label_new (_("Core Stats"));
  gtk_widget_show (label14);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook3), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook3), 0), label14);

  table_skills_exp = gtk_table_new (6, 6, TRUE);
  gtk_widget_show (table_skills_exp);
  gtk_container_add (GTK_CONTAINER (notebook3), table_skills_exp);

  label15 = gtk_label_new (_("Skills & Experience"));
  gtk_widget_show (label15);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook3), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook3), 1), label15);

  table_protections = gtk_table_new (6, 6, TRUE);
  gtk_widget_show (table_protections);
  gtk_container_add (GTK_CONTAINER (notebook3), table_protections);

  label16 = gtk_label_new (_("Protections"));
  gtk_widget_show (label16);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook3), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook3), 2), label16);

  vpaned_info_inventory = gtk_vpaned_new ();
  gtk_widget_show (vpaned_info_inventory);
  gtk_paned_pack2 (GTK_PANED (hpaned_map_other), vpaned_info_inventory, TRUE, TRUE);
  gtk_paned_set_position (GTK_PANED (vpaned_info_inventory), 300);

  vbox_info_entry = gtk_vbox_new (FALSE, 0);
  gtk_widget_show (vbox_info_entry);
  gtk_paned_pack1 (GTK_PANED (vpaned_info_inventory), vbox_info_entry, FALSE, TRUE);

  notebook_info = gtk_notebook_new ();
  gtk_widget_show (notebook_info);
  gtk_box_pack_start (GTK_BOX (vbox_info_entry), notebook_info, TRUE, TRUE, 0);

  scrolledwindow1 = gtk_scrolled_window_new (NULL, NULL);
  gtk_widget_show (scrolledwindow1);
  gtk_container_add (GTK_CONTAINER (notebook_info), scrolledwindow1);
  gtk_scrolled_window_set_policy (GTK_SCROLLED_WINDOW (scrolledwindow1), GTK_POLICY_NEVER, GTK_POLICY_ALWAYS);

  textview_info1 = gtk_text_view_new ();
  gtk_widget_show (textview_info1);
  gtk_container_add (GTK_CONTAINER (scrolledwindow1), textview_info1);
  gtk_text_view_set_editable (GTK_TEXT_VIEW (textview_info1), FALSE);
  gtk_text_view_set_accepts_tab (GTK_TEXT_VIEW (textview_info1), FALSE);
  gtk_text_view_set_cursor_visible (GTK_TEXT_VIEW (textview_info1), FALSE);

  label1 = gtk_label_new (_("Messages"));
  gtk_widget_show (label1);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook_info), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook_info), 0), label1);

  scrolledwindow2 = gtk_scrolled_window_new (NULL, NULL);
  gtk_widget_show (scrolledwindow2);
  gtk_container_add (GTK_CONTAINER (notebook_info), scrolledwindow2);
  gtk_scrolled_window_set_policy (GTK_SCROLLED_WINDOW (scrolledwindow2), GTK_POLICY_NEVER, GTK_POLICY_ALWAYS);

  textview_info2 = gtk_text_view_new ();
  gtk_widget_show (textview_info2);
  gtk_container_add (GTK_CONTAINER (scrolledwindow2), textview_info2);
  gtk_text_view_set_editable (GTK_TEXT_VIEW (textview_info2), FALSE);
  gtk_text_view_set_accepts_tab (GTK_TEXT_VIEW (textview_info2), FALSE);
  gtk_text_view_set_cursor_visible (GTK_TEXT_VIEW (textview_info2), FALSE);

  label2 = gtk_label_new (_("Critical messages"));
  gtk_widget_show (label2);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook_info), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook_info), 1), label2);

  entry_commands = gtk_entry_new ();
  gtk_widget_show (entry_commands);
  gtk_box_pack_start (GTK_BOX (vbox_info_entry), entry_commands, FALSE, FALSE, 0);

  vpaned3 = gtk_vpaned_new ();
  gtk_widget_show (vpaned3);
  gtk_paned_pack2 (GTK_PANED (vpaned_info_inventory), vpaned3, TRUE, TRUE);
  gtk_paned_set_position (GTK_PANED (vpaned3), 400);

  vbox3 = gtk_vbox_new (FALSE, 0);
  gtk_widget_show (vbox3);
  gtk_paned_pack1 (GTK_PANED (vpaned3), vbox3, FALSE, TRUE);

  hbox1 = gtk_hbox_new (TRUE, 0);
  gtk_widget_show (hbox1);
  gtk_box_pack_start (GTK_BOX (vbox3), hbox1, FALSE, TRUE, 0);

  label3 = gtk_label_new (_("Inventory:"));
  gtk_widget_show (label3);
  gtk_box_pack_start (GTK_BOX (hbox1), label3, FALSE, FALSE, 10);

  label_inv_weight = gtk_label_new (_("0/0"));
  gtk_widget_show (label_inv_weight);
  gtk_box_pack_start (GTK_BOX (hbox1), label_inv_weight, FALSE, FALSE, 15);

  label5 = gtk_label_new (_("Count"));
  gtk_widget_show (label5);
  gtk_box_pack_start (GTK_BOX (hbox1), label5, FALSE, FALSE, 5);

  spinbutton_count_adj = gtk_adjustment_new (0, 0, 1e+06, 1, 10, 10);
  spinbutton_count = gtk_spin_button_new (GTK_ADJUSTMENT (spinbutton_count_adj), 1, 0);
  gtk_widget_show (spinbutton_count);
  gtk_box_pack_start (GTK_BOX (hbox1), spinbutton_count, TRUE, TRUE, 0);
  gtk_spin_button_set_numeric (GTK_SPIN_BUTTON (spinbutton_count), TRUE);

  notebook_inv = gtk_notebook_new ();
  gtk_widget_show (notebook_inv);
  gtk_box_pack_start (GTK_BOX (vbox3), notebook_inv, TRUE, TRUE, 0);
  GTK_WIDGET_UNSET_FLAGS (notebook_inv, GTK_CAN_FOCUS);

  scrolledwindow6 = gtk_scrolled_window_new (NULL, NULL);
  gtk_widget_show (scrolledwindow6);
  gtk_container_add (GTK_CONTAINER (notebook_inv), scrolledwindow6);
  gtk_scrolled_window_set_policy (GTK_SCROLLED_WINDOW (scrolledwindow6), GTK_POLICY_NEVER, GTK_POLICY_ALWAYS);

  viewport1 = gtk_viewport_new (NULL, NULL);
  gtk_widget_show (viewport1);
  gtk_container_add (GTK_CONTAINER (scrolledwindow6), viewport1);
  GTK_WIDGET_SET_FLAGS (viewport1, GTK_CAN_FOCUS);

  inv_table = gtk_table_new (11, 10, FALSE);
  gtk_widget_show (inv_table);
  gtk_container_add (GTK_CONTAINER (viewport1), inv_table);
  GTK_WIDGET_SET_FLAGS (inv_table, GTK_CAN_FOCUS);

  label80 = gtk_label_new (_("Icons"));
  gtk_widget_show (label80);
  gtk_notebook_set_tab_label (GTK_NOTEBOOK (notebook_inv), gtk_notebook_get_nth_page (GTK_NOTEBOOK (notebook_inv), 0), label80);

  vbox4 = gtk_vbox_new (FALSE, 0);
  gtk_widget_show (vbox4);
  gtk_paned_pack2 (GTK_PANED (vpaned3), vbox4, TRUE, TRUE);

  label9 = gtk_label_new (_("You see:"));
  gtk_widget_show (label9);
  gtk_box_pack_start (GTK_BOX (vbox4), label9, FALSE, FALSE, 0);

  scrolledwindow5 = gtk_scrolled_window_new (NULL, NULL);
  gtk_widget_show (scrolledwindow5);
  gtk_box_pack_start (GTK_BOX (vbox4), scrolledwindow5, TRUE, TRUE, 0);
  gtk_scrolled_window_set_policy (GTK_SCROLLED_WINDOW (scrolledwindow5), GTK_POLICY_NEVER, GTK_POLICY_ALWAYS);

  treeview_look = gtk_tree_view_new ();
  gtk_widget_show (treeview_look);
  gtk_container_add (GTK_CONTAINER (scrolledwindow5), treeview_look);

  g_signal_connect_swapped ((gpointer) window_root, "key_press_event",
                            G_CALLBACK (keyfunc),
                            GTK_OBJECT (window_root));
  g_signal_connect_swapped ((gpointer) window_root, "key_release_event",
                            G_CALLBACK (keyrelfunc),
                            GTK_OBJECT (window_root));
  g_signal_connect ((gpointer) quit_character1, "activate",
                    G_CALLBACK (menu_quit_character),
                    NULL);
  g_signal_connect ((gpointer) quit2, "activate",
                    G_CALLBACK (menu_quit_program),
                    NULL);
  g_signal_connect ((gpointer) about1, "activate",
                    G_CALLBACK (menu_about),
                    NULL);
  g_signal_connect ((gpointer) drawingarea_map, "expose_event",
                    G_CALLBACK (on_drawingarea_map_expose_event),
                    NULL);
  g_signal_connect ((gpointer) drawingarea_map, "button_press_event",
                    G_CALLBACK (on_drawingarea_map_button_press_event),
                    NULL);
  g_signal_connect ((gpointer) entry_commands, "activate",
                    G_CALLBACK (on_entry_commands_activate),
                    NULL);
  g_signal_connect ((gpointer) notebook_inv, "switch_page",
                    G_CALLBACK (on_notebook_switch_page),
                    NULL);
  g_signal_connect ((gpointer) inv_table, "expose_event",
                    G_CALLBACK (on_inv_table_expose_event),
                    NULL);
  g_signal_connect ((gpointer) treeview_look, "row_collapsed",
                    G_CALLBACK (list_row_collapse),
                    NULL);

  /* Store pointers to all widgets, for use by lookup_widget(). */
  GLADE_HOOKUP_OBJECT_NO_REF (window_root, window_root, "window_root");
  GLADE_HOOKUP_OBJECT (window_root, vbox2, "vbox2");
  GLADE_HOOKUP_OBJECT (window_root, hbox6, "hbox6");
  GLADE_HOOKUP_OBJECT (window_root, menubar1, "menubar1");
  GLADE_HOOKUP_OBJECT (window_root, menuitem1, "menuitem1");
  GLADE_HOOKUP_OBJECT (window_root, menuitem1_menu, "menuitem1_menu");
  GLADE_HOOKUP_OBJECT (window_root, quit_character1, "quit_character1");
  GLADE_HOOKUP_OBJECT (window_root, quit2, "quit2");
  GLADE_HOOKUP_OBJECT (window_root, menuitem4, "menuitem4");
  GLADE_HOOKUP_OBJECT (window_root, menuitem4_menu, "menuitem4_menu");
  GLADE_HOOKUP_OBJECT (window_root, about1, "about1");
  GLADE_HOOKUP_OBJECT (window_root, fire_on, "fire_on");
  GLADE_HOOKUP_OBJECT (window_root, run_on, "run_on");
  GLADE_HOOKUP_OBJECT (window_root, filler, "filler");
  GLADE_HOOKUP_OBJECT (window_root, hpaned_map_other, "hpaned_map_other");
  GLADE_HOOKUP_OBJECT (window_root, vpaned_map_stats, "vpaned_map_stats");
  GLADE_HOOKUP_OBJECT (window_root, notebook4, "notebook4");
  GLADE_HOOKUP_OBJECT (window_root, table_map, "table_map");
  GLADE_HOOKUP_OBJECT (window_root, drawingarea_map, "drawingarea_map");
  GLADE_HOOKUP_OBJECT (window_root, hscrollbar_map, "hscrollbar_map");
  GLADE_HOOKUP_OBJECT (window_root, vscrollbar_map, "vscrollbar_map");
  GLADE_HOOKUP_OBJECT (window_root, button_map_recenter, "button_map_recenter");
  GLADE_HOOKUP_OBJECT (window_root, label46, "label46");
  GLADE_HOOKUP_OBJECT (window_root, drawingarea_magic_map, "drawingarea_magic_map");
  GLADE_HOOKUP_OBJECT (window_root, label47, "label47");
  GLADE_HOOKUP_OBJECT (window_root, hbox2, "hbox2");
  GLADE_HOOKUP_OBJECT (window_root, table4, "table4");
  GLADE_HOOKUP_OBJECT (window_root, label_stats_hp, "label_stats_hp");
  GLADE_HOOKUP_OBJECT (window_root, label_stats_sp, "label_stats_sp");
  GLADE_HOOKUP_OBJECT (window_root, label_stats_grace, "label_stats_grace");
  GLADE_HOOKUP_OBJECT (window_root, label_stats_food, "label_stats_food");
  GLADE_HOOKUP_OBJECT (window_root, progressbar_hp, "progressbar_hp");
  GLADE_HOOKUP_OBJECT (window_root, progressbar_sp, "progressbar_sp");
  GLADE_HOOKUP_OBJECT (window_root, progressbar_grace, "progressbar_grace");
  GLADE_HOOKUP_OBJECT (window_root, progressbar_food, "progressbar_food");
  GLADE_HOOKUP_OBJECT (window_root, fire_label, "fire_label");
  GLADE_HOOKUP_OBJECT (window_root, run_label, "run_label");
  GLADE_HOOKUP_OBJECT (window_root, notebook3, "notebook3");
  GLADE_HOOKUP_OBJECT (window_root, vbox6, "vbox6");
  GLADE_HOOKUP_OBJECT (window_root, label_playername, "label_playername");
  GLADE_HOOKUP_OBJECT (window_root, hbox8, "hbox8");
  GLADE_HOOKUP_OBJECT (window_root, label49, "label49");
  GLADE_HOOKUP_OBJECT (window_root, label_str, "label_str");
  GLADE_HOOKUP_OBJECT (window_root, label51, "label51");
  GLADE_HOOKUP_OBJECT (window_root, label_dex, "label_dex");
  GLADE_HOOKUP_OBJECT (window_root, label53, "label53");
  GLADE_HOOKUP_OBJECT (window_root, label_con, "label_con");
  GLADE_HOOKUP_OBJECT (window_root, label55, "label55");
  GLADE_HOOKUP_OBJECT (window_root, label_int, "label_int");
  GLADE_HOOKUP_OBJECT (window_root, label57, "label57");
  GLADE_HOOKUP_OBJECT (window_root, label_wis, "label_wis");
  GLADE_HOOKUP_OBJECT (window_root, label59, "label59");
  GLADE_HOOKUP_OBJECT (window_root, label_pow, "label_pow");
  GLADE_HOOKUP_OBJECT (window_root, label61, "label61");
  GLADE_HOOKUP_OBJECT (window_root, label_cha, "label_cha");
  GLADE_HOOKUP_OBJECT (window_root, hbox4, "hbox4");
  GLADE_HOOKUP_OBJECT (window_root, label32, "label32");
  GLADE_HOOKUP_OBJECT (window_root, label_wc, "label_wc");
  GLADE_HOOKUP_OBJECT (window_root, label34, "label34");
  GLADE_HOOKUP_OBJECT (window_root, label_dam, "label_dam");
  GLADE_HOOKUP_OBJECT (window_root, label36, "label36");
  GLADE_HOOKUP_OBJECT (window_root, label_ac, "label_ac");
  GLADE_HOOKUP_OBJECT (window_root, label38, "label38");
  GLADE_HOOKUP_OBJECT (window_root, label_armor, "label_armor");
  GLADE_HOOKUP_OBJECT (window_root, hbox9, "hbox9");
  GLADE_HOOKUP_OBJECT (window_root, label64, "label64");
  GLADE_HOOKUP_OBJECT (window_root, label_speed, "label_speed");
  GLADE_HOOKUP_OBJECT (window_root, label66, "label66");
  GLADE_HOOKUP_OBJECT (window_root, label_weapon_speed, "label_weapon_speed");
  GLADE_HOOKUP_OBJECT (window_root, label_range, "label_range");
  GLADE_HOOKUP_OBJECT (window_root, hbox7, "hbox7");
  GLADE_HOOKUP_OBJECT (window_root, label_exp, "label_exp");
  GLADE_HOOKUP_OBJECT (window_root, label_level, "label_level");
  GLADE_HOOKUP_OBJECT (window_root, label14, "label14");
  GLADE_HOOKUP_OBJECT (window_root, table_skills_exp, "table_skills_exp");
  GLADE_HOOKUP_OBJECT (window_root, label15, "label15");
  GLADE_HOOKUP_OBJECT (window_root, table_protections, "table_protections");
  GLADE_HOOKUP_OBJECT (window_root, label16, "label16");
  GLADE_HOOKUP_OBJECT (window_root, vpaned_info_inventory, "vpaned_info_inventory");
  GLADE_HOOKUP_OBJECT (window_root, vbox_info_entry, "vbox_info_entry");
  GLADE_HOOKUP_OBJECT (window_root, notebook_info, "notebook_info");
  GLADE_HOOKUP_OBJECT (window_root, scrolledwindow1, "scrolledwindow1");
  GLADE_HOOKUP_OBJECT (window_root, textview_info1, "textview_info1");
  GLADE_HOOKUP_OBJECT (window_root, label1, "label1");
  GLADE_HOOKUP_OBJECT (window_root, scrolledwindow2, "scrolledwindow2");
  GLADE_HOOKUP_OBJECT (window_root, textview_info2, "textview_info2");
  GLADE_HOOKUP_OBJECT (window_root, label2, "label2");
  GLADE_HOOKUP_OBJECT (window_root, entry_commands, "entry_commands");
  GLADE_HOOKUP_OBJECT (window_root, vpaned3, "vpaned3");
  GLADE_HOOKUP_OBJECT (window_root, vbox3, "vbox3");
  GLADE_HOOKUP_OBJECT (window_root, hbox1, "hbox1");
  GLADE_HOOKUP_OBJECT (window_root, label3, "label3");
  GLADE_HOOKUP_OBJECT (window_root, label_inv_weight, "label_inv_weight");
  GLADE_HOOKUP_OBJECT (window_root, label5, "label5");
  GLADE_HOOKUP_OBJECT (window_root, spinbutton_count, "spinbutton_count");
  GLADE_HOOKUP_OBJECT (window_root, notebook_inv, "notebook_inv");
  GLADE_HOOKUP_OBJECT (window_root, scrolledwindow6, "scrolledwindow6");
  GLADE_HOOKUP_OBJECT (window_root, viewport1, "viewport1");
  GLADE_HOOKUP_OBJECT (window_root, inv_table, "inv_table");
  GLADE_HOOKUP_OBJECT (window_root, label80, "label80");
  GLADE_HOOKUP_OBJECT (window_root, vbox4, "vbox4");
  GLADE_HOOKUP_OBJECT (window_root, label9, "label9");
  GLADE_HOOKUP_OBJECT (window_root, scrolledwindow5, "scrolledwindow5");
  GLADE_HOOKUP_OBJECT (window_root, treeview_look, "treeview_look");

  return window_root;
}

