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
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommandText extends GUIText implements KeyListener
{
    public GUICommandText
            (String nn, int nx, int ny, int nw, int nh, String picactive,
             String picinactive, Font nf, String txt)  throws IOException
    {
        super(nn,nx,ny,nw,nh,picactive,picinactive,nf,txt);
    }
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_BACK_SPACE:
                if (mytext.length()>0)
                {
                    mytext = mytext.substring(0,mytext.length()-1);
                    render();
                }
                break;
            case KeyEvent.VK_DELETE:
                if (mytext.length()>0)
                {
                    mytext = "";
                    render();
                }
                break;
            case KeyEvent.VK_ENTER:
                switch(((JXCWindow)e.getSource()).getCrossfireServerConnection().getStatus())
                {
                    case CrossfireServerConnection.STATUS_PLAYING:
                        System.out.println("Command:"+mytext);
                        if (mytext.startsWith("bind "))
                        {
                            String cmdl = mytext.substring(5);
                            String[] cmds = cmdl.split(";");
                            java.util.List list_parms;
                            GUICommand guicmd;
                            java.util.List<GUICommand> list_commands =
                                    new ArrayList<GUICommand>();

                            for(int i=0; i<cmds.length;i++)
                            {
                                list_parms = new ArrayList();
                                list_parms.add(e.getSource());
                                list_parms.add(cmds[i]);
                                guicmd = new GUICommand(null, GUICommand.CMD_GUI_SEND_COMMAND,
                                        list_parms);
                                list_commands.add(guicmd);
                            }
                            ((JXCWindow)e.getSource()).createKeyBinding(list_commands);
                        }
                        else if (mytext.startsWith("unbind"))
                        {
                            ((JXCWindow)e.getSource()).removeKeyBinding();
                        }
                        else if (mytext.startsWith("script "))
                        {
                            ((JXCWindow)e.getSource()).runScript(mytext.substring(7));
                        }
                        else
                        {
                            ((JXCWindow)e.getSource()).sendNcom(0, mytext);
                        }
                        mytext="";
                        setActive(false);
                        break;
                    case CrossfireServerConnection.STATUS_QUERY:
                        ((JXCWindow)e.getSource()).getCrossfireServerConnection().setStatus(
                                CrossfireServerConnection.STATUS_PLAYING);
                        ((JXCWindow)e.getSource()).send("reply "+mytext);
                        ((JXCWindow)e.getSource()).setDialogStatus(JXCWindow.DLG_NONE);
                        mytext="";
                        setActive(false);
                        break;
                    default:
                        mytext="";
                        setActive(false);
                        break;
                }
                break;
            case KeyEvent.VK_SHIFT:
                break;
            default:
                char chr = e.getKeyChar();
                mytext = mytext+chr;
                render();
                break;
        }
    }
}
