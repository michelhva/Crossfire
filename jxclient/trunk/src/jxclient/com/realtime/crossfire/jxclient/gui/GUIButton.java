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

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.io.IOException;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIButton extends GUIElement
{
    private BufferedImage mypicture_up;

    private BufferedImage mypicture_down;

    private GUICommandList mylist;

    private String mytext = null;

    private Font myfont = null;

    private int mytx = 0;

    private int myty = 0;

    private Color myfontcolor = new Color(255, 255, 255);

    public GUIButton(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh, BufferedImage picup, BufferedImage picdown, GUICommandList cmd)
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        if (picup == null) throw new IllegalArgumentException();
        if (picdown == null) throw new IllegalArgumentException();
        if (picup.getWidth() != picdown.getWidth()) throw new IllegalArgumentException();
        if (picup.getHeight() != picdown.getHeight()) throw new IllegalArgumentException();
        mypicture_up = picup;
        mypicture_down = picdown;
        mylist = cmd;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(picup.getWidth(), picup.getHeight(), Transparency.TRANSLUCENT);
        render();
    }

    public GUIButton(final JXCWindow jxcWindow, String nn, int nx, int ny, int nw, int nh, BufferedImage picup, BufferedImage picdown, String txt, Font f, Color mfc, int tx, int ty, GUICommandList cmd)
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        if (picup == null) throw new IllegalArgumentException();
        if (picdown == null) throw new IllegalArgumentException();
        if (picup.getWidth() != picdown.getWidth()) throw new IllegalArgumentException();
        if (picup.getHeight() != picdown.getHeight()) throw new IllegalArgumentException();
        mypicture_up = picup;
        mypicture_down = picdown;
        mylist = cmd;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(picup.getWidth(), picup.getHeight(), Transparency.TRANSLUCENT);
        mytext = txt;
        myfont = f;
        myfontcolor = mfc;
        mytx = tx;
        myty = ty;
        render();
    }

    public void mouseReleased(MouseEvent e)
    {
        int b = e.getButton();
        switch(b)
        {
        case MouseEvent.BUTTON1:
            mylist.execute();
            active = false;
            JXCWindow jxc = (JXCWindow)(e.getSource());
            jxc.deactivateCurrentElement();
            render();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    public void mousePressed(MouseEvent e)
    {
        active = true;
        render();
    }

    public void render()
    {
        Graphics2D g = mybuffer.createGraphics();
        g.setFont(myfont);
        g.setColor(myfontcolor);
        if (active)
            g.drawImage(mypicture_down, 0, 0, null);
        else
            g.drawImage(mypicture_up, 0, 0, null);
        if (mytext != null)
        {
            g.drawString(mytext, mytx, myty);
        }
        g.dispose();
        setChanged();
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        throw new AssertionError();
    }
}
