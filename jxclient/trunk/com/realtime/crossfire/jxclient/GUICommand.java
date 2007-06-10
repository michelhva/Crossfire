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

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommand
{
    private GUIElement mytarget;
    private int myorder;
    private Object myparams; //Often a String, but not always - see CMD_QUIT or CMD_CONNECT

    public static final int CMD_SHOW       = 0;
    public static final int CMD_HIDE       = 1;
    public static final int CMD_TOGGLE     = 2;
    public static final int CMD_PRINT      = 3;
    public static final int CMD_QUIT       = 4;
    public static final int CMD_SCROLLUP   = 5;
    public static final int CMD_SCROLLDOWN = 6;
    public static final int CMD_CONNECT    = 7;
    public static final int CMD_GUI_META   = 8;
    public static final int CMD_GUI_START  = 9;
    public static final int CMD_GUI_LEAVE_DIALOG  = 10;
    public static final int CMD_GUI_SEND_COMMAND = 11;
    public static final int CMD_GUI_SPELLBELT = 12;

    public GUICommand(GUIElement element, int order, Object params)
    {
        mytarget = element;
        myorder = order;
        myparams = params;
    }
    public void execute()
    {
        //System.out.println("Executing command "+myorder+ " on "+mytarget.getName());
        switch (myorder)
        {
            case CMD_SHOW:
                if (mytarget.isVisible()==false)
                    mytarget.setVisible(true);
                break;
            case CMD_HIDE:
                if (mytarget.isVisible()==true)
                    mytarget.setVisible(false);
                break;
            case CMD_TOGGLE:
                if (mytarget.isVisible()==true)
                    mytarget.setVisible(false);
                else
                    mytarget.setVisible(true);
                break;
            case CMD_PRINT:
                break;
            case CMD_QUIT:
                ((JXCWindow)myparams).endRendering();
                break;
            case CMD_SCROLLUP:
                if (mytarget instanceof GUIScrollable)
                    ((GUIScrollable)mytarget).scrollUp();
                break;
            case CMD_SCROLLDOWN:
                if (mytarget instanceof GUIScrollable)
                    ((GUIScrollable)mytarget).scrollDown();
                break;
            case CMD_CONNECT:
                ((JXCWindow)myparams).connect(((GUIText)mytarget).getText(), 13327);
                break;
            case CMD_GUI_META:
                ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_METASERVER);
                break;
            case CMD_GUI_START:
                ((JXCWindow)myparams).changeGUI(JXCWindow.GUI_START);
                break;
            case CMD_GUI_LEAVE_DIALOG:
                ((JXCWindow)myparams).setDialogStatus(JXCWindow.DLG_NONE);
                break;
            case CMD_GUI_SEND_COMMAND:
            {
                java.util.List lp = (java.util.List)myparams;
                JXCWindow jxcw = (JXCWindow)lp.get(0);
                String cmd = (String)lp.get(1);
                jxcw.send("command 0 "+cmd);
            }
                break;
            case CMD_GUI_SPELLBELT:
            {
                java.util.List lp = (java.util.List)myparams;
                JXCWindow jxcw = (JXCWindow)lp.get(0);
                SpellBeltItem myspellbelt = (SpellBeltItem)lp.get(1);

                if ((myspellbelt != null)&&(myspellbelt.getSpell()!=null))
                {
                    int status = myspellbelt.getStatus();
                    try
                    {
                        if (status==SpellBeltItem.STATUS_CAST)
                            jxcw.getCrossfireServerConnection().writePacket("command 0 cast "+
                                    myspellbelt.getSpell().getInternalName());
                        else
                            jxcw.getCrossfireServerConnection().writePacket("command 0 invoke "+
                                    myspellbelt.getSpell().getInternalName());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        System.exit(0);
                    }
                }
            }
                break;
        }
    }
    public Object getParams()
    {
        return myparams;
    }
}
