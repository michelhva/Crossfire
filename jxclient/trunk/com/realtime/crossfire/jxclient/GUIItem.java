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
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIItem extends GUIElement implements GUIScrollable,
                                        CrossfireSpellAddedListener,
                                        CrossfireSpellRemovedListener,
                                        CrossfireSpellUpdatedListener
{
    protected final BufferedImage mypiccursed;
    protected final BufferedImage mypicapplied;
    protected final BufferedImage mypicselector;
    private final BufferedImage mypicbackground;
    protected final BufferedImage mypiclocked;

    private CfItem myitem = null;

    protected final Font myfont;

    /**
     * The {@link CfItemModifiedListener} used to detect attribute changes of
     * the displayed item.
     */
    private final CfItemModifiedListener itemModifiedListener = new CfItemModifiedListener()
    {
        /** {@inheritDoc} */
        public void itemModified(final CfItem item)
        {
            assert myitem == item;
            render();
        }
    };

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
        render();
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
            render();
        }
    }
    protected void render()
    {
        Graphics2D g = mybuffer.createGraphics();
        g.drawImage(mypicbackground, 0, 0, null);

        g.setBackground(new Color(0,0,0,0.0f));
        g.clearRect(0,0,w,h);
        render(g);
        g.dispose();
        setChanged();
    }
    protected abstract void render(final Graphics g);
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
        if (myitem == item)
        {
            return;
        }

        if (myitem != null)
        {
            myitem.removeCfItemModifiedListener(itemModifiedListener);
        }
        myitem = item;
        if (myitem != null)
        {
            myitem.addCfItemModifiedListener(itemModifiedListener);
        }

        render();
    }
}
