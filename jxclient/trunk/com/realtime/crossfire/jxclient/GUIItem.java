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
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public abstract class GUIItem extends GUIElement implements GUIScrollable, CrossfireItem1Listener,
                                        CrossfireUpditemListener, CrossfireDelitemListener,
                                        CrossfireItem2Listener, CrossfireSpellAddedListener,
                                        CrossfireSpellRemovedListener,
                                        CrossfireSpellUpdatedListener
{
    protected BufferedImage mypiccursed;
    protected BufferedImage mypicapplied;
    protected BufferedImage mypicselector;
    protected BufferedImage mypicbackground;
    protected BufferedImage mypiclocked;

    private CfItem myitem = null;

    protected Font myfont;

    /**
     * If set, some attibutes have been modified since last call to {@link #render()}.
     */
    private boolean modified = true;

    public GUIItem
            (String nn, int nx, int ny, int nw, int nh, String picture,
             String pic_cursed, String pic_applied, String pic_selector,
             String pic_locked, ServerConnection msc, Font mft)
            throws IOException
    {
        mypicbackground =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(picture));
        mypiccursed   =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(pic_cursed));
        mypicapplied  =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(pic_applied));
        mypicselector =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(pic_selector));
        mypiclocked   =
            javax.imageio.ImageIO.read(this.getClass().getClassLoader().getResource(pic_locked));
        x = nx;
        y = ny;
        w = nw;
        h = nh;
        myname = nn;
        active = false;
        myfont = mft;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice      gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(nw, nh, Transparency.TRANSLUCENT);
    }
    public abstract void scrollUp();
    public abstract void scrollDown();
    public void mouseClicked(MouseEvent e)
    {
        final JXCWindow jxcw = (JXCWindow)e.getSource();
        switch(e.getButton())
        {
            case MouseEvent.BUTTON1:
                setActive(true);
                button1Clicked(jxcw);
                render();
                break;
            case MouseEvent.BUTTON2:
                button2Clicked(jxcw);
                break;
            case MouseEvent.BUTTON3:
                button3Clicked(jxcw);
                break;
        }
    }
    protected abstract void button1Clicked(final JXCWindow jxcw);
    protected abstract void button2Clicked(final JXCWindow jxcw);
    protected abstract void button3Clicked(final JXCWindow jxcw);
    public void setActive(final boolean act)
    {
        if (active != act)
        {
            active = act;
            setModified();
            render();
        }
    }
    protected void setModified()
    {
        modified = true;
    }
    protected void render()
    {
        if (!modified)
        {
            return;
        }
        modified = false;

        Graphics2D g = mybuffer.createGraphics();
        g.drawImage(mypicbackground, 0, 0, null);

        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        render(g);
        g.dispose();
    }
    protected abstract void render(final Graphics g);
    public abstract void CommandUpditemReceived(final CrossfireCommandUpditemEvent evt);
    public abstract void CommandItem1Received(final CrossfireCommandItem1Event evt);
    public abstract void CommandItem2Received(final CrossfireCommandItem2Event evt);
    public void CommandDelitemReceived(final CrossfireCommandDelitemEvent evt)
    {
        final CfItem item = evt.getItem();
        if (myitem == item)
        {
            setItem(null);
        }
        render();
    }
    public void CommandAddSpellReceived(final CrossfireCommandAddSpellEvent evt)
    {
    }
    public void CommandUpdSpellReceived(final CrossfireCommandUpdSpellEvent evt)
    {
    }
    public void CommandDelSpellReceived(final CrossfireCommandDelSpellEvent evt)
    {
    }
    public void setVisible(boolean v)
    {
        super.setVisible(v);
        render();
    }

    protected CfItem getItem()
    {
        return myitem;
    }

    protected void setItem(final CfItem item)
    {
        if (myitem != item)
        {
            myitem = item;
            setModified();
        }
    }
}
