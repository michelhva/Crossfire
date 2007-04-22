//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class JXCSkinPrelude implements JXCSkin
{
    private java.util.List<GUIElement> mygui            = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_query   = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_book    = new ArrayList<GUIElement>();
    private java.util.List<GUIElement> mydialog_keybind = new ArrayList<GUIElement>();

    public java.util.List<GUIElement> getDialogKeyBind(ServerConnection myserver, JXCWindow p)
            throws JXCSkinException
    {
        mydialog_keybind.clear();

        Font font_default_medium = null;
        Font font_stats = null;
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_default_medium = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_stats  = font_default_medium.deriveFont(12f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            //Query-Reply Dialog
            GUIPicture dlg_kb_back = new GUIPicture("dlg_kb_back", 23+350, 38+200,
                    200, 100, "default.theme/pictures/dialog_background.png");
            GUILabel dlg_kb_message = new GUILabel("dlg_kb_message", 35+350, 60+200,
                    180, 32, null, font_stats, "Type the key to (un)bind:");
            GUIPicture dlg_kb_a = new GUIPicture("dlg_kb_a", 0+350, 0+200,
                    23, 38, "default.theme/pictures/dialog_border_A.png");
            GUIPicture dlg_kb_b = new GUIPicture("dlg_kb_b", 23+350, 0+200,
                    200, 38, "default.theme/pictures/dialog_border_B.png");
            GUIPicture dlg_kb_c = new GUIPicture("dlg_kb_c", 223+350, 3+200,
                    21, 35, "default.theme/pictures/dialog_border_C.png");
            GUIPicture dlg_kb_d = new GUIPicture("dlg_kb_d", 0+350, 38+200,
                    23, 100, "default.theme/pictures/dialog_border_D.png");
            GUIPicture dlg_kb_e = new GUIPicture("dlg_kb_e", 223+350, 38+200,
                    21, 100, "default.theme/pictures/dialog_border_E.png");
            GUIPicture dlg_kb_f = new GUIPicture("dlg_kb_f", 0+350, 138+200,
                    23, 16, "default.theme/pictures/dialog_border_F.png");
            GUIPicture dlg_kb_g = new GUIPicture("dlg_kb_g", 23+350, 138+200,
                    200, 12, "default.theme/pictures/dialog_border_G.png");
            GUIPicture dlg_kb_h = new GUIPicture("dlg_kb_h", 223+350, 138+200,
                    21, 16, "default.theme/pictures/dialog_border_H.png");

            mydialog_keybind.add(dlg_kb_back);
            mydialog_keybind.add(dlg_kb_message);
            mydialog_keybind.add(dlg_kb_a);
            mydialog_keybind.add(dlg_kb_b);
            mydialog_keybind.add(dlg_kb_c);
            mydialog_keybind.add(dlg_kb_d);
            mydialog_keybind.add(dlg_kb_e);
            mydialog_keybind.add(dlg_kb_f);
            mydialog_keybind.add(dlg_kb_g);
            mydialog_keybind.add(dlg_kb_h);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the key binding dialog");
        }
        return mydialog_keybind;
    }
    public java.util.List<GUIElement> getDialogQuery(ServerConnection myserver, JXCWindow p)
            throws JXCSkinException
    {
        mydialog_query.clear();

        Font font_default_small  = null;
        Font font_default_medium = null;
        Font font_default_large  = null;
        Font font_log            = null;
        Font font_menu           = null;
        Font font_stats          = null;
        Font font_types          = null;
        Font font_cmdline        = null;
        java.util.List<GUICommand> command_log_upper_up = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_log_upper_down = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rinv_down = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rinv_up = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_magicmap_display = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_magicmap_undisplay = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_menu_display = new ArrayList<GUICommand>();
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_default_medium = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_default_large  = font_default_medium.deriveFont(16f);
            font_menu   = font_default_medium.deriveFont(14f);
            font_stats  = font_default_medium.deriveFont(12f);
            font_types  = font_default_medium.deriveFont(12f);
            font_default_medium = font_default_medium.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/regular.ttf");
            font_default_small  = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_default_small  = font_default_small.deriveFont(16f);
            font_cmdline = font_default_small.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/courbd.ttf");
            font_log  = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_log  = font_log.deriveFont(8f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }

        try
        {
            //Query-Reply Dialog
            GUIPicture dlg_query_back = new GUIPicture("dlg_query_back", 23+350, 38+200,
                    200, 100, "default.theme/pictures/dialog_background.png");
            GUILabel dlg_query_message = new GUILabel("dlg_query_message", 35+350, 60+200,
                    180, 32, null, font_stats, "X");
            GUICommandText dlg_query_command = new GUICommandText("dlg_query_command",
                    50+350, 100+200, 132, 20,
                    "default.theme/pictures/textarea_medium_active.png",
                    "default.theme/pictures/textarea_medium_inactive.png",
                    font_cmdline, "");
            GUIPicture dlg_query_a = new GUIPicture("dlg_query_a", 0+350, 0+200,
                    23, 38, "default.theme/pictures/dialog_border_A.png");
            GUIPicture dlg_query_b = new GUIPicture("dlg_query_b", 23+350, 0+200,
                    200, 38, "default.theme/pictures/dialog_border_B.png");
            GUIPicture dlg_query_c = new GUIPicture("dlg_query_c", 223+350, 3+200,
                    21, 35, "default.theme/pictures/dialog_border_C.png");
            GUIPicture dlg_query_d = new GUIPicture("dlg_query_d", 0+350, 38+200,
                    23, 100, "default.theme/pictures/dialog_border_D.png");
            GUIPicture dlg_query_e = new GUIPicture("dlg_query_e", 223+350, 38+200,
                    21, 100, "default.theme/pictures/dialog_border_E.png");
            GUIPicture dlg_query_f = new GUIPicture("dlg_query_f", 0+350, 138+200,
                    23, 16, "default.theme/pictures/dialog_border_F.png");
            GUIPicture dlg_query_g = new GUIPicture("dlg_query_g", 23+350, 138+200,
                    200, 12, "default.theme/pictures/dialog_border_G.png");
            GUIPicture dlg_query_h = new GUIPicture("dlg_query_h", 223+350, 138+200,
                    21, 16, "default.theme/pictures/dialog_border_H.png");

            myserver.addCrossfireQueryListener(dlg_query_message);

            mydialog_query.add(dlg_query_back);
            mydialog_query.add(dlg_query_message);
            mydialog_query.add(dlg_query_a);
            mydialog_query.add(dlg_query_b);
            mydialog_query.add(dlg_query_c);
            mydialog_query.add(dlg_query_d);
            mydialog_query.add(dlg_query_e);
            mydialog_query.add(dlg_query_f);
            mydialog_query.add(dlg_query_g);
            mydialog_query.add(dlg_query_h);
            mydialog_query.add(dlg_query_command);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the query dialog");
        }
        return mydialog_query;
    }
    public java.util.List<GUIElement> getMainInterface(ServerConnection myserver, JXCWindow p)
            throws JXCSkinException
    {
        mygui.clear();

        System.out.println("Free Memory before getMainInterface GC 1:"+
                Runtime.getRuntime().freeMemory()/1024+" KB");
        System.gc();
        System.out.println("Free Memory after getMainInterface GC 1:"+
                Runtime.getRuntime().freeMemory()/1024 + " KB");

        Font font_default_small  = null;
        Font font_default_medium = null;
        Font font_default_large  = null;
        Font font_log            = null;
        Font font_menu           = null;
        Font font_stats          = null;
        Font font_types          = null;
        Font font_cmdline        = null;
        java.util.List<GUICommand> command_log_upper_up = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_log_upper_down = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rinv_down = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rinv_up = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rsp_down = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rsp_up = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rsp_display = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_rsp_undisplay = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_magicmap_display = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_magicmap_undisplay = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_menu_display = new ArrayList<GUICommand>();
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_default_medium = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_default_large  = font_default_medium.deriveFont(16f);
            font_menu   = font_default_medium.deriveFont(14f);
            font_stats  = font_default_medium.deriveFont(12f);
            font_types  = font_default_medium.deriveFont(12f);
            font_default_medium = font_default_medium.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/regular.ttf");
            font_default_small  = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_default_small  = font_default_small.deriveFont(8f);
            font_cmdline = font_default_small.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/courbd.ttf");
            font_log  = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_log  = font_log.deriveFont(8f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }

        try
        {
            //Map Group (100,100)
            GUIMap gui_map = new GUIMap("playield", 0+100, 0+100, 800,800);

            //LGauges Group (80,574)

            GUIPicture gui_sword_back = new GUIPicture("sword_back", 16+80, 0+574,
                    64, 256, "default.theme/pictures/background_sword.png");
            GUIPicture gui_gauge_hp_back = new GUIPicture("gauge_hp_back", 0+80, 14+574,
                    19, 144, "default.theme/pictures/life_gauge_empty.png");
            GUIPicture gui_gauge_fp_back = new GUIPicture("gauge_fp_back", 75+80, 15+574,
                    21, 143, "default.theme/pictures/food_gauge_empty.png");

            GUIGauge gui_gauge_hp = new GUIGauge("gauge_hp", 0+80, 14+574,
                    19, 144, "default.theme/pictures/life_gauge_full.png",null,
                    "default.theme/pictures/life_gauge_empty.png",
                    Stats.CS_STAT_HP,
                    GUIGauge.ORIENTATION_SN);
            GUIGauge gui_gauge_fp = new GUIGauge("gauge_hp", 75+80, 15+574,
                    21, 143, "default.theme/pictures/food_gauge_full.png",null,
                    "default.theme/pictures/food_gauge_empty.png",
                    Stats.CS_STAT_FOOD,
                    GUIGauge.ORIENTATION_SN);

            //RGauges Group (820,552)

            GUIPicture gui_staff_back = new GUIPicture("staff_back", 40+820, 0+552,
                    64, 256, "default.theme/pictures/background_staff.png");
            GUIPicture gui_gauge_gp_back = new GUIPicture("gauge_gp_back", 0+820, 30+552,
                    50, 150, "default.theme/pictures/grace_gauge_empty.png");
            GUIPicture gui_gauge_sp_back = new GUIPicture("gauge_sp_back", 80+820, 30+552,
                    50, 150, "default.theme/pictures/magic_gauge_empty.png");

            GUIGauge gui_gauge_gp = new GUIGauge("gauge_gp", 0+820, 30+552,
                    50, 150, "default.theme/pictures/grace_gauge_full.png",null,
                    "default.theme/pictures/grace_gauge_empty.png",
                    Stats.CS_STAT_GRACE,
                    GUIGauge.ORIENTATION_SN);
            GUIGauge gui_gauge_sp = new GUIGauge("gauge_sp", 80+820, 30+552,
                    50, 150, "default.theme/pictures/magic_gauge_full.png",null,
                    "default.theme/pictures/magic_gauge_empty.png",
                    Stats.CS_STAT_SP,
                    GUIGauge.ORIENTATION_SN);

            //UpperPane (207,27)
            GUIPicture gui_title_back = new GUIPicture("title_back", 0+207, 0+27,
                    1024, 128, "default.theme/pictures/title.png");
            GUILabel gui_label_hp = new GUILabel("label_hp", 236+207, 64+27,
                    20, 16, null, font_types, Stats.CS_STAT_HP);
            GUILabel gui_label_fp = new GUILabel("label_fp", 284+207, 64+27,
                    36, 16, null, font_types, Stats.CS_STAT_FOOD);
            GUILabel gui_label_sp = new GUILabel("label_sp", 333+207, 64+27,
                    36, 16, null, font_types, Stats.CS_STAT_SP);
            GUILabel gui_label_gp = new GUILabel("label_gp", 381+207, 64+27,
                    36, 16, null, font_types, Stats.CS_STAT_GRACE);
            GUILabel gui_label_title = new GUILabel("label_title", 32+207, 24+27,
                    512, 24, null, font_default_large, Stats.CS_STAT_TITLE);

            //UpperBar (113,0)
            GUIPicture gui_up_back = new GUIPicture("up_back", 0+113, 0,
                    1024, 32, "default.theme/pictures/background_up.png");
            GUILabel gui_label_stat_str = new GUILabel("label_stat_str", 318+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_STR);
            GUILabel gui_label_stat_dex = new GUILabel("label_stat_dex", 403+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_DEX);
            GUILabel gui_label_stat_con = new GUILabel("label_stat_con", 488+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_CON);
            GUILabel gui_label_stat_int = new GUILabel("label_stat_int", 573+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_INT);
            GUILabel gui_label_stat_pow = new GUILabel("label_stat_pow", 658+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_POW);
            GUILabel gui_label_stat_wis = new GUILabel("label_stat_wis", 743+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_WIS);
            GUILabel gui_label_stat_cha = new GUILabel("label_stat_cha", 828+113, 3,
                    32, 16, null, font_types, Stats.CS_STAT_CHA);

            GUIPicture gui_magicmap_content = new GUIPicture("magicmap_content", 0+113, 20+3,
                    232, 232, "default.theme/pictures/magicmap_content.png");
            GUIMagicMap gui_magicmap = new GUIMagicMap("magicmap", 18+113, 38+3,
                    196, 196);

            gui_magicmap_content.setVisible(false);
            gui_magicmap.setVisible(false);

            command_magicmap_display.add(new GUICommand(gui_magicmap_content,
                                         GUICommand.CMD_SHOW, ""));
            command_magicmap_display.add(new GUICommand(gui_magicmap,
                                         GUICommand.CMD_SHOW, ""));

            command_magicmap_undisplay.add(new GUICommand(gui_magicmap_content,
                                           GUICommand.CMD_HIDE, ""));
            command_magicmap_undisplay.add(new GUICommand(gui_magicmap,
                                           GUICommand.CMD_HIDE, ""));

            GUIButton gui_btn_magicmap_hide = new GUIButton("btn_magicmap_hide",
                    0+63, 0+3, 232, 20,
                    "default.theme/pictures/magicmap_extended_inactive.png",
                    "default.theme/pictures/magicmap_extended_active.png",
                    command_magicmap_undisplay);
            GUIButton gui_btn_magicmap_show = new GUIButton("btn_magicmap_show",
                    0+63, 0+3, 232, 20,
                    "default.theme/pictures/magicmap_shrinked_inactive.png",
                    "default.theme/pictures/magicmap_shrinked_active.png",
                    command_magicmap_display);

            GUIButton gui_btn_menu = new GUIButton("btn_menu",
                    260+63, 0+3, 50, 20,
                    "default.theme/pictures/button_menu_inactive.png",
                    "default.theme/pictures/button_menu_active.png",
                    command_menu_display);

            gui_btn_magicmap_hide.setVisible(false);

            command_magicmap_undisplay.add(new GUICommand(gui_btn_magicmap_show,
                                           GUICommand.CMD_SHOW, ""));
            command_magicmap_undisplay.add(new GUICommand(gui_btn_magicmap_hide,
                                           GUICommand.CMD_HIDE, ""));
            command_magicmap_display.add(new GUICommand(gui_btn_magicmap_show,
                                         GUICommand.CMD_HIDE, ""));
            command_magicmap_display.add(new GUICommand(gui_btn_magicmap_hide,
                                         GUICommand.CMD_SHOW, ""));

            //LeftBar (0,0)
            GUIPicture gui_left_back = new GUIPicture("left_back", 0, 0,
                    128, 768, "default.theme/pictures/background_left.png");

            //RightBar (926,0);
            GUIPicture gui_right_back = new GUIPicture("right_back", 0+926, 0,
                    98, 768, "default.theme/pictures/background_right.png");

            //LInventory (26,171);
            GUIPicture gui_spellbelt_left_back = new GUIPicture("spellbelt_left_back",
                    0+26, 171,
                    45, 597, "default.theme/pictures/spellbelt_left.png");
            GUIItem gui_spellbelt0 = new GUIItemSpellbelt("spellbelt0", 0+5+26, 0+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    0, myserver, font_default_small);
            GUIItem gui_spellbelt1 = new GUIItemSpellbelt("spellbelt1", 0+5+26, 46+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    1, myserver, font_default_small);
            GUIItem gui_spellbelt2 = new GUIItemSpellbelt("spellbelt2", 0+5+26, 92+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    2, myserver, font_default_small);
            GUIItem gui_spellbelt3 = new GUIItemSpellbelt("spellbelt3", 0+5+26, 138+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    3, myserver, font_default_small);
            GUIItem gui_spellbelt4 = new GUIItemSpellbelt("spellbelt4", 0+5+26, 184+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    4, myserver, font_default_small);
            GUIItem gui_spellbelt5 = new GUIItemSpellbelt("spellbelt5", 0+5+26, 230+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    5, myserver, font_default_small);
            GUIItem gui_spellbelt6 = new GUIItemSpellbelt("spellbelt6", 0+5+26, 276+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    6, myserver, font_default_small);
            GUIItem gui_spellbelt7 = new GUIItemSpellbelt("spellbelt7", 0+5+26, 322+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    7, myserver, font_default_small);
            GUIItem gui_spellbelt8 = new GUIItemSpellbelt("spellbelt8", 0+5+26, 368+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    8, myserver, font_default_small);
            GUIItem gui_spellbelt9 = new GUIItemSpellbelt("spellbelt9", 0+5+26, 414+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    9, myserver, font_default_small);
            GUIItem gui_spellbeltA = new GUIItemSpellbelt("spellbeltA", 0+5+26, 460+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    10, myserver, font_default_small);
            GUIItem gui_spellbeltB = new GUIItemSpellbelt("spellbeltB", 0+5+26, 506+34+171, 32, 32,
                    "default.theme/pictures/empty.png",
                    "default.theme/pictures/spell_cast.png",
                    "default.theme/pictures/spell_invoke.png",
                    "default.theme/pictures/inventory_selector.png",
                    "default.theme/pictures/marker_locked.png",
                    11, myserver, font_default_small);

            //RInventory (973,53)
            GUIPicture gui_inv_right_back = new GUIPicture("inv_right_back", 8+973, 0+53,
                    43, 715, "default.theme/pictures/inventory.png");
            GUIItem gui_rinv0 = new GUIItemInventory("rinv0", 0+973+14, 0+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            0, myserver, font_default_small);
            GUIItem gui_rinv1 = new GUIItemInventory("rinv1", 0+973+14, 35+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            1, myserver, font_default_small);
            GUIItem gui_rinv2 = new GUIItemInventory("rinv2", 0+973+14, 70+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            2, myserver, font_default_small);
            GUIItem gui_rinv3 = new GUIItemInventory("rinv3", 0+973+14, 105+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            3, myserver, font_default_small);
            GUIItem gui_rinv4 = new GUIItemInventory("rinv4", 0+973+14, 140+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            4, myserver, font_default_small);
            GUIItem gui_rinv5 = new GUIItemInventory("rinv5", 0+973+14, 175+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            5, myserver, font_default_small);
            GUIItem gui_rinv6 = new GUIItemInventory("rinv6", 0+973+14, 210+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            6, myserver, font_default_small);
            GUIItem gui_rinv7 = new GUIItemInventory("rinv7", 0+973+14, 245+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            7, myserver, font_default_small);
            GUIItem gui_rinv8 = new GUIItemInventory("rinv8", 0+973+14, 280+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            8, myserver, font_default_small);
            GUIItem gui_rinv9 = new GUIItemInventory("rinv9", 0+973+14, 315+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            9, myserver, font_default_small);
            GUIItem gui_rinvA = new GUIItemInventory("rinvA", 0+973+14, 350+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            10, myserver, font_default_small);
            GUIItem gui_rinvB = new GUIItemInventory("rinvB", 0+973+14, 385+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            11, myserver, font_default_small);
            GUIItem gui_rinvC = new GUIItemInventory("rinvC", 0+973+14, 420+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            12, myserver, font_default_small);
            GUIItem gui_rinvD = new GUIItemInventory("rinvD", 0+973+14, 455+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            13, myserver, font_default_small);
            GUIItem gui_rinvE = new GUIItemInventory("rinvE", 0+973+14, 490+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            14, myserver, font_default_small);
            GUIItem gui_rinvF = new GUIItemInventory("rinvF", 0+973+14, 525+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            15, myserver, font_default_small);
            GUIItem gui_rinvG = new GUIItemInventory("rinvG", 0+973+14, 560+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            16, myserver, font_default_small);
            GUIItem gui_rinvH = new GUIItemInventory("rinvH", 0+973+14, 595+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            17, myserver, font_default_small);
            GUIItem gui_rinvI = new GUIItemInventory("rinvI", 0+973+14, 630+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            18, myserver, font_default_small);
            GUIItem gui_rinvJ = new GUIItemInventory("rinvJ", 0+973+14, 665+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            19, myserver, font_default_small);

            GUIItem gui_rsp0 = new GUIItemSpelllist("rsp0", 0+973+14, 0+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            0, myserver, font_default_small);
            GUIItem gui_rsp1 = new GUIItemSpelllist("rsp1", 0+973+14, 35+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            1, myserver, font_default_small);
            GUIItem gui_rsp2 = new GUIItemSpelllist("rsp2", 0+973+14, 70+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            2, myserver,
                                            font_default_small);
            GUIItem gui_rsp3 = new GUIItemSpelllist("rsp3", 0+973+14, 105+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            3, myserver, font_default_small);
            GUIItem gui_rsp4 = new GUIItemSpelllist("rsp4", 0+973+14, 140+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            4, myserver, font_default_small);
            GUIItem gui_rsp5 = new GUIItemSpelllist("rsp5", 0+973+14, 175+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            5, myserver, font_default_small);
            GUIItem gui_rsp6 = new GUIItemSpelllist("rsp6", 0+973+14, 210+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            6, myserver, font_default_small);
            GUIItem gui_rsp7 = new GUIItemSpelllist("rsp7", 0+973+14, 245+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            7, myserver, font_default_small);
            GUIItem gui_rsp8 = new GUIItemSpelllist("rsp8", 0+973+14, 280+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            8, myserver, font_default_small);
            GUIItem gui_rsp9 = new GUIItemSpelllist("rsp9", 0+973+14, 315+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            9, myserver, font_default_small);
            GUIItem gui_rspA = new GUIItemSpelllist("rspA", 0+973+14, 350+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            10, myserver, font_default_small);
            GUIItem gui_rspB = new GUIItemSpelllist("rspB", 0+973+14, 385+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            11, myserver, font_default_small);
            GUIItem gui_rspC = new GUIItemSpelllist("rspC", 0+973+14, 420+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            12, myserver, font_default_small);
            GUIItem gui_rspD = new GUIItemSpelllist("rspD", 0+973+14, 455+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            13, myserver, font_default_small);
            GUIItem gui_rspE = new GUIItemSpelllist("rspE", 0+973+14, 490+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            14, myserver, font_default_small);
            GUIItem gui_rspF = new GUIItemSpelllist("rspF", 0+973+14, 525+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            15, myserver, font_default_small);
            GUIItem gui_rspG = new GUIItemSpelllist("rspG", 0+973+14, 560+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            16, myserver, font_default_small);
            GUIItem gui_rspH = new GUIItemSpelllist("rspH", 0+973+14, 595+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            17, myserver, font_default_small);
            GUIItem gui_rspI = new GUIItemSpelllist("rspI", 0+973+14, 630+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            18, myserver, font_default_small);
            GUIItem gui_rspJ = new GUIItemSpelllist("rspJ", 0+973+14, 665+53+9, 32, 32,
                                            "default.theme/pictures/empty.png",
                                            "default.theme/pictures/marker_cursed.png",
                                            "default.theme/pictures/marker_applied.png",
                                            "default.theme/pictures/inventory_selector.png",
                                            "default.theme/pictures/marker_locked.png",
                                            19, myserver, font_default_small);

            command_rinv_up.add(new GUICommand(gui_rinv0, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv1, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv2, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv3, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv4, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv5, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv6, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv7, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv8, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinv9, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvA, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvB, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvC, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvD, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvE, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvF, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvG, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvH, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvI, GUICommand.CMD_SCROLLUP, ""));
            command_rinv_up.add(new GUICommand(gui_rinvJ, GUICommand.CMD_SCROLLUP, ""));

            command_rinv_down.add(new GUICommand(gui_rinv0, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv1, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv2, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv3, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv4, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv5, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv6, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv7, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv8, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinv9, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvA, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvB, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvC, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvD, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvE, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvF, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvG, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvH, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvI, GUICommand.CMD_SCROLLDOWN, ""));
            command_rinv_down.add(new GUICommand(gui_rinvJ, GUICommand.CMD_SCROLLDOWN, ""));

            command_rsp_up.add(new GUICommand(gui_rsp0, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp1, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp2, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp3, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp4, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp5, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp6, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp7, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp8, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rsp9, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspA, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspB, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspC, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspD, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspE, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspF, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspG, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspH, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspI, GUICommand.CMD_SCROLLUP, ""));
            command_rsp_up.add(new GUICommand(gui_rspJ, GUICommand.CMD_SCROLLUP, ""));

            command_rsp_down.add(new GUICommand(gui_rsp0, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp1, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp2, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp3, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp4, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp5, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp6, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp7, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp8, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rsp9, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspA, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspB, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspC, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspD, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspE, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspF, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspG, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspH, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspI, GUICommand.CMD_SCROLLDOWN, ""));
            command_rsp_down.add(new GUICommand(gui_rspJ, GUICommand.CMD_SCROLLDOWN, ""));

            GUIButton gui_btn_rinv_up = new GUIButton("btn_rinv_up", 0+973, 13+53, 10, 31,
                    "default.theme/pictures/inv_scrollup_inactive.png",
                    "default.theme/pictures/inv_scrollup_pushed.png",
                    command_rinv_up);
            GUIButton gui_btn_rinv_down = new GUIButton("btn_rinv_down", 0+973, 656+53, 10, 31,
                    "default.theme/pictures/inv_scrolldown_inactive.png",
                    "default.theme/pictures/inv_scrolldown_pushed.png",
                    command_rinv_down);

            GUIButton gui_btn_rsp_up = new GUIButton("btn_rsp_up", 0+973, 13+53, 10, 31,
                    "default.theme/pictures/inv_scrollup_inactive.png",
                    "default.theme/pictures/inv_scrollup_pushed.png",
                    command_rsp_up);
            GUIButton gui_btn_rsp_down = new GUIButton("btn_rsp_down", 0+973, 656+53, 10, 31,
                    "default.theme/pictures/inv_scrolldown_inactive.png",
                    "default.theme/pictures/inv_scrolldown_pushed.png",
                    command_rsp_down);

            gui_btn_rsp_up.setVisible(false);
            gui_btn_rsp_down.setVisible(false);
            gui_rsp0.setVisible(false);
            gui_rsp1.setVisible(false);
            gui_rsp2.setVisible(false);
            gui_rsp3.setVisible(false);
            gui_rsp4.setVisible(false);
            gui_rsp5.setVisible(false);
            gui_rsp6.setVisible(false);
            gui_rsp7.setVisible(false);
            gui_rsp8.setVisible(false);
            gui_rsp9.setVisible(false);
            gui_rspA.setVisible(false);
            gui_rspB.setVisible(false);
            gui_rspC.setVisible(false);
            gui_rspD.setVisible(false);
            gui_rspE.setVisible(false);
            gui_rspF.setVisible(false);
            gui_rspG.setVisible(false);
            gui_rspH.setVisible(false);
            gui_rspI.setVisible(false);
            gui_rspJ.setVisible(false);

            //SecondaryStats (319,621)
            GUILabel gui_label_sstat_level = new GUILabel("label_sstat_level", 0+319, 4+621,
                    64, 20, null, font_stats, Stats.CS_STAT_LEVEL);
            GUILabel gui_label_sstat_exp = new GUILabel("label_sstat_exp", 0+319, 27+621,
                    64, 20, null, font_stats, Stats.CS_STAT_EXP64);
            GUILabel gui_label_sstat_range = new GUILabel("label_sstat_range", 0+319, 49+621,
                    85, 20, null, font_stats, Stats.CS_STAT_RANGE);
            GUILabel gui_label_sstat_wc = new GUILabel("label_sstat_wc", 200+319, 4+621,
                    56, 20, null, font_stats, Stats.CS_STAT_WC);
            GUILabel gui_label_sstat_ac = new GUILabel("label_sstat_ac", 200+319, 27+621,
                    56, 20, null, font_stats, Stats.CS_STAT_AC);
            GUILabel gui_label_sstat_dam = new GUILabel("label_sstat_dam", 200+319, 49+621,
                    56, 20, null, font_stats, Stats.CS_STAT_DAM);
            GUILabel gui_label_sstat_speed = new GUILabel("label_sstat_speed", 200+319, 71+621,
                    56, 20, null, font_stats, Stats.CS_STAT_SPEED);

            //Panel (199,551)
            GUIPicture gui_panel_back = new GUIPicture("panel_back", 0+199, 0+551,
                    700, 230, "default.theme/pictures/background_panel.png");
            GUIPicture gui_panel_spells = new GUIPicture("panel_spells", 392+199, 74+551,
                    224, 123, "default.theme/pictures/spell_panel.png");
            GUILabel gui_panel_spells_icon = new GUILabel("label_panel_spells_icon",
                    400+199, 85+551, 100, 20, null, font_stats, GUILabel.LABEL_SPELL_ICON);
            GUILabel gui_panel_spells_name = new GUILabel("label_panel_spells_name",
                    450+199, 85+551, 100, 20, null, font_stats, GUILabel.LABEL_SPELL_NAME);
            GUILabel gui_panel_spells_description = new GUILabel("label_panel_spells_description",
                    450+199, 105+551, 100, 20, null, font_stats,
                    GUILabel.LABEL_SPELL_DESCRIPTION);

            gui_panel_spells.setVisible(false);
            gui_panel_spells_icon.setVisible(false);
            gui_panel_spells_name.setVisible(false);
            gui_panel_spells_description.setVisible(false);

            GUIItem gui_floor0 = new GUIItemFloor("floor0", 0+199+9, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             0, myserver,
                                             font_default_small);
            GUIItem gui_floor1 = new GUIItemFloor("floor1", 0+199+9+34, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             1, myserver,
                                             font_default_small);
            GUIItem gui_floor2 = new GUIItemFloor("floor2", 0+199+9+68, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             2, myserver,
                                             font_default_small);
            GUIItem gui_floor3 = new GUIItemFloor("floor3", 0+199+9+102, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             3, myserver,
                                             font_default_small);
            GUIItem gui_floor4 = new GUIItemFloor("floor4", 0+199+9+136, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             4, myserver,
                                             font_default_small);
            GUIItem gui_floor5 = new GUIItemFloor("floor5", 0+199+9+170, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             5, myserver,
                                             font_default_small);
            GUIItem gui_floor6 = new GUIItemFloor("floor6", 0+199+9+204, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             6, myserver,
                                             font_default_small);
            GUIItem gui_floor7 = new GUIItemFloor("floor7", 0+199+9+238, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             7, myserver,
                                             font_default_small);
            GUIItem gui_floor8 = new GUIItemFloor("floor8", 0+199+9+272, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             8, myserver,
                                             font_default_small);
            GUIItem gui_floor9 = new GUIItemFloor("floor9", 0+199+9+306, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             9, myserver,
                                             font_default_small);
            GUIItem gui_floorA = new GUIItemFloor("floorA", 0+199+9+340, 0+551+165, 32, 32,
                                             "default.theme/pictures/empty.png",
                                             "default.theme/pictures/marker_cursed.png",
                                             "default.theme/pictures/marker_applied.png",
                                             "default.theme/pictures/inventory_selector.png",
                                             "default.theme/pictures/marker_locked.png",
                                             10, myserver,
                                            font_default_small);

            //Resistances (630,628)
            GUIPicture gui_gr0_back = new GUIPicture("gr0_back", 0+630, 0+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr1_back = new GUIPicture("gr1_back", 76+630, 0+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr2_back = new GUIPicture("gr2_back", 152+630, 0+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr3_back = new GUIPicture("gr3_back", 0+630, 21+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr4_back = new GUIPicture("gr4_back", 76+630, 21+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr5_back = new GUIPicture("gr5_back", 152+630, 21+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr6_back = new GUIPicture("gr6_back", 0+630, 42+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr7_back = new GUIPicture("gr7_back", 76+630, 42+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr8_back = new GUIPicture("gr8_back", 152+630, 42+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_gr9_back = new GUIPicture("gr9_back", 0+630, 63+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grA_back = new GUIPicture("grA_back", 76+630, 63+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grB_back = new GUIPicture("grB_back", 152+630, 63+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grC_back = new GUIPicture("grC_back", 0+630, 84+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grD_back = new GUIPicture("grD_back", 76+630, 84+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grE_back = new GUIPicture("grE_back", 152+630, 84+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grF_back = new GUIPicture("grF_back", 0+630, 105+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grG_back = new GUIPicture("grG_back", 76+630, 105+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");
            GUIPicture gui_grH_back = new GUIPicture("grH_back", 152+630, 105+628,
                    32, 11, "default.theme/pictures/res_gauge_empty.png");


            GUIGauge gui_gr0 = new GUIGauge("gr0", 0+630, 0+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_PHYS, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr1 = new GUIGauge("gr1", 76+630, 0+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_MAG, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr2 = new GUIGauge("gr2", 152+630, 0+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_FIRE, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr3 = new GUIGauge("gr3", 0+630, 21+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_ELEC, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr4 = new GUIGauge("gr4", 76+630, 21+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_COLD, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr5 = new GUIGauge("gr5", 152+630, 21+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_CONF, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr6 = new GUIGauge("gr6", 0+630, 42+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_ACID, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr7 = new GUIGauge("gr7", 76+630, 42+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_DRAIN, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr8 = new GUIGauge("gr8", 152+630, 42+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_GHOSTHIT,
                                            GUIGauge.ORIENTATION_WE);
            GUIGauge gui_gr9 = new GUIGauge("gr9", 0+630, 63+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_POISON, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grA = new GUIGauge("grA", 76+630, 63+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_SLOW, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grB = new GUIGauge("grB", 152+630, 63+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_PARA, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grC = new GUIGauge("grC", 0+630, 84+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_TURN_UNDEAD, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grD = new GUIGauge("grD", 76+630, 84+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_FEAR, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grE = new GUIGauge("grE", 152+630, 84+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_DEPLETE,
                                            GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grF = new GUIGauge("grF", 0+630, 105+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_DEATH, GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grG = new GUIGauge("grG", 76+630, 105+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_HOLYWORD,
                                            GUIGauge.ORIENTATION_WE);
            GUIGauge gui_grH = new GUIGauge("grH", 152+630, 105+628,
                                            29, 11,
                                            "default.theme/pictures/res_gauge_positive.png",
                                            "default.theme/pictures/res_gauge_negative.png",
                                            "default.theme/pictures/res_gauge_empty.png",
                                            Stats.CS_STAT_RES_BLIND, GUIGauge.ORIENTATION_WE);

            //LowerBar (72,732)
            GUIPicture gui_lower_back = new GUIPicture("lower_back", 0+72, 0+732,
                    880, 36, "default.theme/pictures/lower_bar.png");
            GUICommandText gui_command = new GUICommandText("command", 127+72, 17+732,
                    512, 32, "default.theme/pictures/empty.png",
                    "default.theme/pictures/empty.png",
                    font_cmdline, "");

            command_rsp_display.add(new GUICommand(gui_rinv0, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv1, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv2, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv3, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv4, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv5, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv6, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv7, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv8, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinv9, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvA, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvB, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvC, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvD, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvE, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvF, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvG, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvH, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvI, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rinvJ, GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_rsp0, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp1, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp2, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp3, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp4, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp5, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp6, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp7, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp8, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rsp9, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspA, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspB, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspC, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspD, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspE, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspF, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspG, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspH, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspI, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_rspJ, GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_btn_rsp_up,
                                      GUICommand.CMD_SHOW,""));
            command_rsp_display.add(new GUICommand(gui_btn_rsp_down,
                                      GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_btn_rinv_up,
                                      GUICommand.CMD_HIDE,""));
            command_rsp_display.add(new GUICommand(gui_btn_rinv_down,
                                      GUICommand.CMD_HIDE, ""));
            command_rsp_display.add(new GUICommand(gui_panel_spells,
                                      GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_panel_spells_icon,
                                    GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_panel_spells_name,
                                    GUICommand.CMD_SHOW, ""));
            command_rsp_display.add(new GUICommand(gui_panel_spells_description,
                                    GUICommand.CMD_SHOW, ""));

            command_rsp_undisplay.add(new GUICommand(gui_rsp0, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp1, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp2, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp3, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp4, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp5, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp6, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp7, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp8, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rsp9, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspA, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspB, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspC, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspD, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspE, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspF, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspG, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspH, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspI, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rspJ, GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv0, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv1, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv2, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv3, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv4, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv5, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv6, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv7, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv8, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinv9, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvA, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvB, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvC, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvD, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvE, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvF, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvG, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvH, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvI, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_rinvJ, GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_btn_rsp_up,
                                      GUICommand.CMD_HIDE,""));
            command_rsp_undisplay.add(new GUICommand(gui_btn_rsp_down,
                                      GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_btn_rinv_up,
                                      GUICommand.CMD_SHOW,""));
            command_rsp_undisplay.add(new GUICommand(gui_btn_rinv_down,
                                      GUICommand.CMD_SHOW, ""));
            command_rsp_undisplay.add(new GUICommand(gui_panel_spells,
                                      GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_panel_spells_icon,
                                      GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_panel_spells_name,
                                      GUICommand.CMD_HIDE, ""));
            command_rsp_undisplay.add(new GUICommand(gui_panel_spells_description,
                                    GUICommand.CMD_HIDE, ""));

            GUIButton gui_btn_rsp_display = new GUIButton("btn_rsp_display",
                    690+72, 22+732, 25, 14,
                    "default.theme/pictures/button_chat_inactive.png",
                    "default.theme/pictures/button_chat_active.png",
                    command_rsp_display);
            GUIButton gui_btn_rsp_undisplay = new GUIButton("btn_rsp_undisplay",
                    690+72, 22+732, 25, 14,
                    "default.theme/pictures/button_chat_pushed.png",
                    "default.theme/pictures/button_chat_active.png",
                    command_rsp_undisplay);
            gui_btn_rsp_undisplay.setVisible(false);
            command_rsp_undisplay.add(new GUICommand(gui_btn_rsp_undisplay,
                                      GUICommand.CMD_HIDE,""));
            command_rsp_undisplay.add(new GUICommand(gui_btn_rsp_display,
                                      GUICommand.CMD_SHOW,""));
            command_rsp_display.add(new GUICommand(gui_btn_rsp_display,
                                    GUICommand.CMD_HIDE,""));
            command_rsp_display.add(new GUICommand(gui_btn_rsp_undisplay,
                                    GUICommand.CMD_SHOW,""));

            GUIButton gui_btn_stats = new GUIButton("btn_stats",
                    720+72, 22+732, 25, 14,
                    "default.theme/pictures/button_stat_inactive.png",
                    "default.theme/pictures/button_stat_active.png",
                    command_menu_display);

            //TextPaneUp (679,100)
            GUIPicture gui_log_upper_back = new GUIPicture("lower_back", 0+679, 0+100,
                    301, 204, "default.theme/pictures/log1_expanded_inactive.png");
            GUILog gui_log_upper = new GUILog("log_upper", 10+679,10+100,200,180,
                                              "default.theme/pictures/empty.png",
                                              font_log, 15,511);
            command_log_upper_up.add(new GUICommand(gui_log_upper,
                                     GUICommand.CMD_SCROLLUP, ""));
            command_log_upper_down.add(new GUICommand(gui_log_upper,
                                       GUICommand.CMD_SCROLLDOWN, ""));

            GUIButton gui_log_upper_up = new GUIButton("log_upper_up", 0+679, 0+100, 10, 31,
                    "default.theme/pictures/inv_scrollup_inactive.png",
                    "default.theme/pictures/inv_scrollup_inactive.png",
                    command_log_upper_up);
            GUIButton gui_log_upper_down = new GUIButton("log_upper_down", 0+679, 180+100, 10, 31,
                    "default.theme/pictures/inv_scrolldown_inactive.png",
                    "default.theme/pictures/inv_scrolldown_inactive.png",
                    command_log_upper_down);

            myserver.addCrossfireMap1Listener(gui_map);
            myserver.addCrossfireNewmapListener(gui_map);
            myserver.addCrossfireNewmapListener(gui_magicmap);
            myserver.addCrossfireMapscrollListener(gui_map);
            myserver.addCrossfireStatsListener(gui_gauge_hp);
            myserver.addCrossfireStatsListener(gui_gauge_fp);
            myserver.addCrossfireStatsListener(gui_gauge_gp);
            myserver.addCrossfireStatsListener(gui_gauge_sp);
            myserver.addCrossfireStatsListener(gui_label_hp);
            myserver.addCrossfireStatsListener(gui_label_fp);
            myserver.addCrossfireStatsListener(gui_label_sp);
            myserver.addCrossfireStatsListener(gui_label_gp);
            myserver.addCrossfireStatsListener(gui_label_title);
            myserver.addCrossfireStatsListener(gui_label_stat_str);
            myserver.addCrossfireStatsListener(gui_label_stat_dex);
            myserver.addCrossfireStatsListener(gui_label_stat_con);
            myserver.addCrossfireStatsListener(gui_label_stat_int);
            myserver.addCrossfireStatsListener(gui_label_stat_pow);
            myserver.addCrossfireStatsListener(gui_label_stat_wis);
            myserver.addCrossfireStatsListener(gui_label_stat_cha);
            myserver.addCrossfireStatsListener(gui_label_sstat_level);
            myserver.addCrossfireStatsListener(gui_label_sstat_exp);
            myserver.addCrossfireStatsListener(gui_label_sstat_wc);
            myserver.addCrossfireStatsListener(gui_label_sstat_ac);
            myserver.addCrossfireStatsListener(gui_label_sstat_dam);
            myserver.addCrossfireStatsListener(gui_label_sstat_speed);
            myserver.addCrossfireStatsListener(gui_label_sstat_range);
            myserver.addCrossfireStatsListener(gui_gr0);
            myserver.addCrossfireStatsListener(gui_gr1);
            myserver.addCrossfireStatsListener(gui_gr2);
            myserver.addCrossfireStatsListener(gui_gr3);
            myserver.addCrossfireStatsListener(gui_gr4);
            myserver.addCrossfireStatsListener(gui_gr5);
            myserver.addCrossfireStatsListener(gui_gr6);
            myserver.addCrossfireStatsListener(gui_gr7);
            myserver.addCrossfireStatsListener(gui_gr8);
            myserver.addCrossfireStatsListener(gui_gr9);
            myserver.addCrossfireStatsListener(gui_grA);
            myserver.addCrossfireStatsListener(gui_grB);
            myserver.addCrossfireStatsListener(gui_grC);
            myserver.addCrossfireStatsListener(gui_grD);
            myserver.addCrossfireStatsListener(gui_grE);
            myserver.addCrossfireStatsListener(gui_grF);
            myserver.addCrossfireStatsListener(gui_grG);
            myserver.addCrossfireStatsListener(gui_grH);
            myserver.addCrossfireQueryListener(gui_log_upper);
            myserver.addCrossfireDrawinfoListener(gui_log_upper);

            myserver.addCrossfireMagicmapListener(gui_magicmap);

            p.addSpellListener(gui_panel_spells_icon);
            p.addSpellListener(gui_panel_spells_name);
            p.addSpellListener(gui_panel_spells_description);

            myserver.addCrossfireSpellAddedListener(gui_rsp0);
            myserver.addCrossfireSpellAddedListener(gui_rsp1);
            myserver.addCrossfireSpellAddedListener(gui_rsp2);
            myserver.addCrossfireSpellAddedListener(gui_rsp3);
            myserver.addCrossfireSpellAddedListener(gui_rsp4);
            myserver.addCrossfireSpellAddedListener(gui_rsp5);
            myserver.addCrossfireSpellAddedListener(gui_rsp6);
            myserver.addCrossfireSpellAddedListener(gui_rsp7);
            myserver.addCrossfireSpellAddedListener(gui_rsp8);
            myserver.addCrossfireSpellAddedListener(gui_rsp9);
            myserver.addCrossfireSpellAddedListener(gui_rspA);
            myserver.addCrossfireSpellAddedListener(gui_rspB);
            myserver.addCrossfireSpellAddedListener(gui_rspC);
            myserver.addCrossfireSpellAddedListener(gui_rspD);
            myserver.addCrossfireSpellAddedListener(gui_rspE);
            myserver.addCrossfireSpellAddedListener(gui_rspF);
            myserver.addCrossfireSpellAddedListener(gui_rspG);
            myserver.addCrossfireSpellAddedListener(gui_rspH);
            myserver.addCrossfireSpellAddedListener(gui_rspI);
            myserver.addCrossfireSpellAddedListener(gui_rspJ);

            myserver.addCrossfireSpellRemovedListener(gui_rsp0);
            myserver.addCrossfireSpellRemovedListener(gui_rsp1);
            myserver.addCrossfireSpellRemovedListener(gui_rsp2);
            myserver.addCrossfireSpellRemovedListener(gui_rsp3);
            myserver.addCrossfireSpellRemovedListener(gui_rsp4);
            myserver.addCrossfireSpellRemovedListener(gui_rsp5);
            myserver.addCrossfireSpellRemovedListener(gui_rsp6);
            myserver.addCrossfireSpellRemovedListener(gui_rsp7);
            myserver.addCrossfireSpellRemovedListener(gui_rsp8);
            myserver.addCrossfireSpellRemovedListener(gui_rsp9);
            myserver.addCrossfireSpellRemovedListener(gui_rspA);
            myserver.addCrossfireSpellRemovedListener(gui_rspB);
            myserver.addCrossfireSpellRemovedListener(gui_rspC);
            myserver.addCrossfireSpellRemovedListener(gui_rspD);
            myserver.addCrossfireSpellRemovedListener(gui_rspE);
            myserver.addCrossfireSpellRemovedListener(gui_rspF);
            myserver.addCrossfireSpellRemovedListener(gui_rspG);
            myserver.addCrossfireSpellRemovedListener(gui_rspH);
            myserver.addCrossfireSpellRemovedListener(gui_rspI);
            myserver.addCrossfireSpellRemovedListener(gui_rspJ);

            myserver.addCrossfireSpellUpdatedListener(gui_rsp0);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp1);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp2);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp3);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp4);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp5);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp6);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp7);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp8);
            myserver.addCrossfireSpellUpdatedListener(gui_rsp9);
            myserver.addCrossfireSpellUpdatedListener(gui_rspA);
            myserver.addCrossfireSpellUpdatedListener(gui_rspB);
            myserver.addCrossfireSpellUpdatedListener(gui_rspC);
            myserver.addCrossfireSpellUpdatedListener(gui_rspD);
            myserver.addCrossfireSpellUpdatedListener(gui_rspE);
            myserver.addCrossfireSpellUpdatedListener(gui_rspF);
            myserver.addCrossfireSpellUpdatedListener(gui_rspG);
            myserver.addCrossfireSpellUpdatedListener(gui_rspH);
            myserver.addCrossfireSpellUpdatedListener(gui_rspI);
            myserver.addCrossfireSpellUpdatedListener(gui_rspJ);

            mygui.add(gui_map);
            mygui.add(gui_sword_back);
            mygui.add(gui_gauge_hp_back);
            mygui.add(gui_gauge_fp_back);
            mygui.add(gui_gauge_hp);
            mygui.add(gui_gauge_fp);
            mygui.add(gui_staff_back);
            mygui.add(gui_gauge_gp_back);
            mygui.add(gui_gauge_sp_back);
            mygui.add(gui_gauge_gp);
            mygui.add(gui_gauge_sp);
            mygui.add(gui_up_back);
            mygui.add(gui_title_back);
            mygui.add(gui_left_back);
            mygui.add(gui_right_back);
            mygui.add(gui_spellbelt_left_back);
            mygui.add(gui_inv_right_back);
            mygui.add(gui_panel_back);
            mygui.add(gui_gr0_back);
            mygui.add(gui_gr1_back);
            mygui.add(gui_gr2_back);
            mygui.add(gui_gr3_back);
            mygui.add(gui_gr4_back);
            mygui.add(gui_gr5_back);
            mygui.add(gui_gr6_back);
            mygui.add(gui_gr7_back);
            mygui.add(gui_gr8_back);
            mygui.add(gui_gr9_back);
            mygui.add(gui_grA_back);
            mygui.add(gui_grB_back);
            mygui.add(gui_grC_back);
            mygui.add(gui_grD_back);
            mygui.add(gui_grE_back);
            mygui.add(gui_grF_back);
            mygui.add(gui_grG_back);
            mygui.add(gui_grH_back);
            mygui.add(gui_gr0);
            mygui.add(gui_gr1);
            mygui.add(gui_gr2);
            mygui.add(gui_gr3);
            mygui.add(gui_gr4);
            mygui.add(gui_gr5);
            mygui.add(gui_gr6);
            mygui.add(gui_gr7);
            mygui.add(gui_gr8);
            mygui.add(gui_gr9);
            mygui.add(gui_grA);
            mygui.add(gui_grB);
            mygui.add(gui_grC);
            mygui.add(gui_grD);
            mygui.add(gui_grE);
            mygui.add(gui_grF);
            mygui.add(gui_grG);
            mygui.add(gui_grH);
            mygui.add(gui_lower_back);
            mygui.add(gui_label_hp);
            mygui.add(gui_label_fp);
            mygui.add(gui_label_sp);
            mygui.add(gui_label_gp);
            mygui.add(gui_label_title);
            mygui.add(gui_label_stat_str);
            mygui.add(gui_label_stat_dex);
            mygui.add(gui_label_stat_con);
            mygui.add(gui_label_stat_int);
            mygui.add(gui_label_stat_pow);
            mygui.add(gui_label_stat_wis);
            mygui.add(gui_label_stat_cha);
            mygui.add(gui_label_sstat_level);
            mygui.add(gui_label_sstat_exp);
            mygui.add(gui_label_sstat_wc);
            mygui.add(gui_label_sstat_ac);
            mygui.add(gui_label_sstat_dam);
            mygui.add(gui_label_sstat_speed);
            mygui.add(gui_label_sstat_range);
            mygui.add(gui_command);
            mygui.add(gui_btn_rsp_display);
            mygui.add(gui_btn_rsp_undisplay);
            mygui.add(gui_btn_stats);
            mygui.add(gui_log_upper_back);
            mygui.add(gui_log_upper);
            mygui.add(gui_btn_rinv_up);
            mygui.add(gui_btn_rinv_down);
            mygui.add(gui_btn_rsp_up);
            mygui.add(gui_btn_rsp_down);
            mygui.add(gui_spellbelt0);
            mygui.add(gui_spellbelt1);
            mygui.add(gui_spellbelt2);
            mygui.add(gui_spellbelt3);
            mygui.add(gui_spellbelt4);
            mygui.add(gui_spellbelt5);
            mygui.add(gui_spellbelt6);
            mygui.add(gui_spellbelt7);
            mygui.add(gui_spellbelt8);
            mygui.add(gui_spellbelt9);
            mygui.add(gui_spellbeltA);
            mygui.add(gui_spellbeltB);
            mygui.add(gui_rinv0);
            mygui.add(gui_rinv1);
            mygui.add(gui_rinv2);
            mygui.add(gui_rinv3);
            mygui.add(gui_rinv4);
            mygui.add(gui_rinv5);
            mygui.add(gui_rinv6);
            mygui.add(gui_rinv7);
            mygui.add(gui_rinv8);
            mygui.add(gui_rinv9);
            mygui.add(gui_rinvA);
            mygui.add(gui_rinvB);
            mygui.add(gui_rinvC);
            mygui.add(gui_rinvD);
            mygui.add(gui_rinvE);
            mygui.add(gui_rinvF);
            mygui.add(gui_rinvG);
            mygui.add(gui_rinvH);
            mygui.add(gui_rinvI);
            mygui.add(gui_rinvJ);
            mygui.add(gui_rsp0);
            mygui.add(gui_rsp1);
            mygui.add(gui_rsp2);
            mygui.add(gui_rsp3);
            mygui.add(gui_rsp4);
            mygui.add(gui_rsp5);
            mygui.add(gui_rsp6);
            mygui.add(gui_rsp7);
            mygui.add(gui_rsp8);
            mygui.add(gui_rsp9);
            mygui.add(gui_rspA);
            mygui.add(gui_rspB);
            mygui.add(gui_rspC);
            mygui.add(gui_rspD);
            mygui.add(gui_rspE);
            mygui.add(gui_rspF);
            mygui.add(gui_rspG);
            mygui.add(gui_rspH);
            mygui.add(gui_rspI);
            mygui.add(gui_rspJ);
            mygui.add(gui_floor0);
            mygui.add(gui_floor1);
            mygui.add(gui_floor2);
            mygui.add(gui_floor3);
            mygui.add(gui_floor4);
            mygui.add(gui_floor5);
            mygui.add(gui_floor6);
            mygui.add(gui_floor7);
            mygui.add(gui_floor8);
            mygui.add(gui_floor9);
            mygui.add(gui_floorA);
            mygui.add(gui_log_upper_down);
            mygui.add(gui_log_upper_up);
            mygui.add(gui_btn_magicmap_hide);
            mygui.add(gui_btn_magicmap_show);
            mygui.add(gui_btn_menu);
            mygui.add(gui_magicmap_content);
            mygui.add(gui_magicmap);
            mygui.add(gui_panel_spells);
            mygui.add(gui_panel_spells_icon);
            mygui.add(gui_panel_spells_name);
            mygui.add(gui_panel_spells_description);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the main interface");
        }
        System.out.println("Free Memory before getMainInterface GC 1:"+
                Runtime.getRuntime().freeMemory()/1024+" KB");
        System.gc();
        System.out.println("Free Memory after getMainInterface GC 1:"+
                Runtime.getRuntime().freeMemory()/1024 + " KB");
        return mygui;
    }

    public java.util.List<GUIElement> getMetaInterface(ServerConnection myserver, JXCWindow p)
            throws JXCSkinException
    {
        mygui.clear();
        java.util.List<GUICommand> command_metaconnect = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_metacancel  = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_metaup      = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_metadown    = new ArrayList<GUICommand>();

        Font font_metaurl=null;
        Font font_description=null;
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_metaurl = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_metaurl = font_metaurl.deriveFont(16f);
            font_description = font_metaurl.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            GUIPicture gui_background = new GUIPicture("background", 0, 0, 1024, 768,
                    "default.theme/pictures/metaserver.png");
            GUIText gui_metaurl = new GUIText("metaurl", 300, 620, 260, 20,
                                              "default.theme/pictures/textarea_big_active.png",
                                              "default.theme/pictures/textarea_big_inactive.png",
                                              font_metaurl,
                                              "");
            GUILabel gui_metacomment = new GUILabel("metacomment", 200, 650, 600, 118,
                    "default.theme/pictures/empty.png",
                    font_description,
                    "");
            GUIMetaElement gui_metaselect1 = new GUIMetaElement("metaselect1",
                    300, 250, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 0);
            GUIMetaElement gui_metaselect2 = new GUIMetaElement(
                    "metaselect2", 300, 280, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 1);
            GUIMetaElement gui_metaselect3 = new GUIMetaElement(
                    "metaselect3", 300, 310, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 2);
            GUIMetaElement gui_metaselect4 = new GUIMetaElement(
                    "metaselect4", 300, 340, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 3);
            GUIMetaElement gui_metaselect5 = new GUIMetaElement(
                    "metaselect5", 300, 370, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl,
                    gui_metaurl,
                    gui_metacomment, 4);
            GUIMetaElement gui_metaselect6 = new GUIMetaElement(
                    "metaselect6", 300, 400, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 5);
            GUIMetaElement gui_metaselect7 = new GUIMetaElement(
                    "metaselect7", 300, 430, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 6);
            GUIMetaElement gui_metaselect8 = new GUIMetaElement(
                    "metaselect8", 300, 460, 400, 20,
                    "default.theme/pictures/metaentry_tcp.png",
                    "default.theme/pictures/metaentry_udp.png",
                    font_metaurl, gui_metaurl, gui_metacomment, 7);

            command_metaup.add(new GUICommand(gui_metaselect1,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect2,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect3,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect4,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect5,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect6,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect7,
                               GUICommand.CMD_SCROLLUP, ""));
            command_metaup.add(new GUICommand(gui_metaselect8,
                               GUICommand.CMD_SCROLLUP, ""));

            command_metadown.add(new GUICommand(gui_metaselect1,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect2,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect3,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect4,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect5,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect6,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect7,
                                 GUICommand.CMD_SCROLLDOWN, ""));
            command_metadown.add(new GUICommand(gui_metaselect8,
                                 GUICommand.CMD_SCROLLDOWN, ""));

            command_metaconnect.add(new GUICommand(gui_metaurl,
                                    GUICommand.CMD_CONNECT, p));
            command_metacancel.add(new GUICommand(null,
                                   GUICommand.CMD_GUI_START, p));

            GUIButton gui_metaconnect = new GUIButton("metaconnect", 620, 600, 135, 55,
                    "default.theme/pictures/button_small_active.png",
                    "default.theme/pictures/button_small_pushed.png",
                    "Connect", font_metaurl,Color.DARK_GRAY,42,34,
                    command_metaconnect);
            GUIButton gui_metacancel = new GUIButton("metacancel", 620, 670, 135, 55,
                    "default.theme/pictures/button_small_active.png",
                    "default.theme/pictures/button_small_pushed.png",
                    "Cancel", font_metaurl,Color.DARK_GRAY,42,34,
                    command_metacancel);
            GUIButton gui_metaup = new GUIButton("metaup", 270, 250, 20, 25,
                    "default.theme/pictures/metaup.png",
                    "default.theme/pictures/metaup.png",
                    command_metaup);
            GUIButton gui_metadown = new GUIButton("metadown", 270, 470, 20, 25,
                    "default.theme/pictures/metadown.png",
                    "default.theme/pictures/metadown.png",
                    command_metadown);
            mygui.add(gui_background);
            mygui.add(gui_metaurl);
            mygui.add(gui_metaconnect);
            mygui.add(gui_metaup);
            mygui.add(gui_metadown);
            mygui.add(gui_metacomment);
            mygui.add(gui_metaselect1);
            mygui.add(gui_metaselect2);
            mygui.add(gui_metaselect3);
            mygui.add(gui_metaselect4);
            mygui.add(gui_metaselect5);
            mygui.add(gui_metaselect6);
            mygui.add(gui_metaselect7);
            mygui.add(gui_metaselect8);
            mygui.add(gui_metacancel);
            //myactive_element = gui_metaselect1;
            //gui_metaselect1.setActive(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the metaserver interface");
        }
        return mygui;
    }

    public java.util.List<GUIElement> getStartInterface(ServerConnection myserver, JXCWindow p)
            throws JXCSkinException
    {
        mygui.clear();
        java.util.List<GUICommand> command_startmenu_meta = new ArrayList<GUICommand>();
        java.util.List<GUICommand> command_startmenu_quit = new ArrayList<GUICommand>();

        Font font_metaurl=null;
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_metaurl = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_metaurl = font_metaurl.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {
            GUIPicture gui_background = new GUIPicture("background", 160, 0, 714, 112,
                    "default.theme/pictures/crossfire_logo.png");

            command_startmenu_meta.add(new GUICommand(null, GUICommand.CMD_GUI_META, p));
            command_startmenu_quit.add(new GUICommand(null, GUICommand.CMD_QUIT, p));

            GUIButton gui_startmenu_meta = new GUIButton("startmenu_meta",
                    210, 200, 600, 55,
                    "default.theme/pictures/button_large_active.png",
                    "default.theme/pictures/button_large_pushed.png",
                    "Connect to a game server", font_metaurl,Color.DARK_GRAY,
                    220, 34, command_startmenu_meta);
            GUIButton gui_startmenu_new = new GUIButton("startmenu_new",
                    210, 300, 600, 55,
                    "default.theme/pictures/button_large_active.png",
                    "default.theme/pictures/button_large_pushed.png",
                    "Create a new character (not implemented)", font_metaurl,Color.DARK_GRAY,
                    180, 34, command_startmenu_meta);
            GUIButton gui_startmenu_load = new GUIButton("startmenu_load",
                    210, 400, 600, 55,
                    "default.theme/pictures/button_large_active.png",
                    "default.theme/pictures/button_large_pushed.png",
                    "Load a saved character (not implemented)", font_metaurl,Color.DARK_GRAY,
                    180, 34, command_startmenu_meta);
            GUIButton gui_startmenu_options = new GUIButton("startmenu_options",
                    210, 500, 600, 55,
                    "default.theme/pictures/button_large_active.png",
                    "default.theme/pictures/button_large_pushed.png",
                    "Options (not implemented)", font_metaurl,Color.DARK_GRAY,
                    230, 34, command_startmenu_meta);
            GUIButton gui_startmenu_quit = new GUIButton("startmenu_quit",
                    210, 600, 600, 55,
                    "default.theme/pictures/button_large_active.png",
                    "default.theme/pictures/button_large_pushed.png",
                    "Leave Crossfire", font_metaurl,Color.DARK_GRAY,
                    250, 34, command_startmenu_quit);
            mygui.add(gui_background);
            mygui.add(gui_startmenu_meta);
            mygui.add(gui_startmenu_new);
            mygui.add(gui_startmenu_load);
            mygui.add(gui_startmenu_options);
            mygui.add(gui_startmenu_quit);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the starting interface");
        }
        return mygui;
    }

    public java.util.List<GUIElement> getDialogBook(ServerConnection myserver, JXCWindow p,
                                                       int booknr)
            throws JXCSkinException
    {
        mydialog_book.clear();
        java.util.List<GUICommand> command_book_close = new ArrayList<GUICommand>();

        Font font_metaurl=null;
        InputStream ttf=null;
        try
        {
            ttf = this.getClass().getClassLoader().getResourceAsStream("default.theme/fonts/default.ttf");
            font_metaurl = Font.createFont(Font.TRUETYPE_FONT, ttf);
            font_metaurl = font_metaurl.deriveFont(14f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot load the fonts");
        }
        finally
        {
            try
            {
                ttf.close();
            }
            catch (Exception e)
            {}
        }
        try
        {

            switch(booknr)
            {
                case ServerConnection.MSG_TYPE_BOOK_CLASP_1:
                case ServerConnection.MSG_TYPE_BOOK_CLASP_2:
                case ServerConnection.MSG_TYPE_BOOK_ELEGANT_1:
                case ServerConnection.MSG_TYPE_BOOK_ELEGANT_2:
                case ServerConnection.MSG_TYPE_BOOK_QUARTO_1:
                case ServerConnection.MSG_TYPE_BOOK_QUARTO_2:
                case ServerConnection.MSG_TYPE_BOOK_SPELL_EVOKER:
                case ServerConnection.MSG_TYPE_BOOK_SPELL_PRAYER:
                case ServerConnection.MSG_TYPE_BOOK_SPELL_PYRO:
                case ServerConnection.MSG_TYPE_BOOK_SPELL_SORCERER:
                case ServerConnection.MSG_TYPE_BOOK_SPELL_SUMMONER:
                default:
                    GUIPicture gui_background = new GUIPicture("book_background",
                            50, 50, 900, 600,
                            "default.theme/pictures/bouquin.png");
                    GUILabel gui_book_text = new GUILabel("book_text",
                            190, 100, 850, 550,
                            null, font_metaurl, Color.DARK_GRAY, "");
                    GUIButton gui_book_close = new GUIButton("book_close",
                            690, 510, 135, 55,
                            "default.theme/pictures/button_small_active.png",
                            "default.theme/pictures/button_small_pushed.png",
                            "Close", font_metaurl,Color.DARK_GRAY,
                            20, 34, command_book_close);
                    command_book_close.add(new GUICommand(null,
                                               GUICommand.CMD_GUI_LEAVE_DIALOG, p));

                    myserver.addCrossfireDrawextinfoListener(gui_book_text);

                    mydialog_book.add(gui_background);
                    mydialog_book.add(gui_book_text);
                    mydialog_book.add(gui_book_close);
                    break;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JXCSkinException("Cannot create the starting interface");
        }
        return mydialog_book;
    }
}
