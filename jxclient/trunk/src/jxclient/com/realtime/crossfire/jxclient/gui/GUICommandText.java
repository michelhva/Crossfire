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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUICommandText extends GUIText implements KeyListener
{
    public GUICommandText(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh, BufferedImage picactive, BufferedImage picinactive, Font nf, String txt)
    {
        super(jxcWindow, nn, nx, ny, nw, nh, picactive, picinactive, nf, txt);
    }

    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_BACK_SPACE:
            if (mytext.length()>0)
            {
                mytext = mytext.substring(0, mytext.length()-1);
                render();
            }
            break;

        case KeyEvent.VK_DELETE:
            if (mytext.length() > 0)
            {
                mytext = "";
                render();
            }
            break;

        case KeyEvent.VK_ENTER:
            switch(((JXCWindow)e.getSource()).getCrossfireServerConnection().getStatus())
            {
            case CrossfireServerConnection.STATUS_PLAYING:
                if (mytext.startsWith("bind "))
                {
                    String cmdl = mytext.substring(5);
                    final GUICommandList commands = new GUICommandList(cmdl, (JXCWindow)e.getSource());
                    ((JXCWindow)e.getSource()).createKeyBinding(commands);
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
                    ((JXCWindow)e.getSource()).sendNcom(mytext);
                }
                mytext = "";
                setActive(false);
                break;

            case CrossfireServerConnection.STATUS_QUERY:
                ((JXCWindow)e.getSource()).getCrossfireServerConnection().setStatus(CrossfireServerConnection.STATUS_PLAYING);
                try
                {
                    ((JXCWindow)e.getSource()).getCrossfireServerConnection().sendReply(mytext);
                }
                catch (final Exception ex)
                {
                    ex.printStackTrace();
                }
                ((JXCWindow)e.getSource()).setDialogStatus(JXCWindow.DLG_NONE);
                mytext = "";
                setActive(false);
                break;

            default:
                mytext = "";
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
