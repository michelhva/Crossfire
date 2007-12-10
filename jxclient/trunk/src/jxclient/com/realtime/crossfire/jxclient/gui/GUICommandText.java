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

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.ServerConnection;
import java.awt.Color;
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
    public GUICommandText(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage activeImage, final BufferedImage inactiveImage, final Font font, final Color inactiveColor, final Color activeColor, final int margin, final String text)
    {
        super(jxcWindow, name, x, y, w, h, activeImage, inactiveImage, font, inactiveColor, activeColor, margin, text);
    }

    /** {@inheritDoc} */
    protected void execute(final JXCWindow jxcWindow, final String command)
    {
        switch (jxcWindow.getCrossfireServerConnection().getStatus())
        {
        case PLAYING:
            jxcWindow.executeCommand(command);
            break;

        case QUERY:
            jxcWindow.getCrossfireServerConnection().setStatus(ServerConnection.Status.PLAYING);
            try
            {
                jxcWindow.getCrossfireServerConnection().sendReply(command);
            }
            catch (final Exception ex)
            {
                ex.printStackTrace();
            }
            jxcWindow.closeQueryDialog();
            break;

        default:
            break;
        }

        setText("");
    }
}
