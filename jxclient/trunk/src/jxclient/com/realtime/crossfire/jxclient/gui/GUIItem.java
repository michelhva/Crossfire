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

import com.realtime.crossfire.jxclient.CfItem;
import com.realtime.crossfire.jxclient.CfItemModifiedListener;
import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.CrossfireUpdateFaceListener;
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Spell;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
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
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIItem extends GUIElement implements GUIScrollable
{
    protected final BufferedImage mypiccursed;

    protected final BufferedImage mypicapplied;

    protected final BufferedImage mypicselector;

    private final BufferedImage mypicbackground;

    protected final BufferedImage mypiclocked;

    private CfItem myitem = null;

    protected final Font myfont;

    /**
     * The background color of this item.
     */
    private static final Color backgroundColor = new Color(0, 0, 0, 0.0f);

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

    /**
     * The {@link CrossfireUpdateFaceListener} registered to detect updated
     * faces.
     */
    private final CrossfireUpdateFaceListener crossfireUpdateFaceListener = new CrossfireUpdateFaceListener()
    {
        /** {@inheritDoc} */
        public void updateFace(final int faceID)
        {
            if (myitem == null)
            {
                return;
            }

            final Face face = myitem.getFace();
            if (face == null || face.getID() != faceID)
            {
                return;
            }

            render();
        }
    };

    public GUIItem(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final BufferedImage picture, final BufferedImage pic_cursed, final BufferedImage pic_applied, final BufferedImage pic_selector, final BufferedImage pic_locked, final CrossfireServerConnection msc, final Font mft)
    {
        super(jxcWindow, nn, nx, ny, nw, nh);
        mypicbackground = picture;
        mypiccursed = pic_cursed;
        mypicapplied = pic_applied;
        mypicselector = pic_selector;
        mypiclocked = pic_locked;
        active = false;
        myfont = mft;
        createBuffer();
        render();
        jxcWindow.getCrossfireServerConnection().addCrossfireUpdateFaceListener(crossfireUpdateFaceListener);
    }

    public abstract void scrollUp();

    public abstract void scrollDown();

    public void mouseClicked(final MouseEvent e)
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
        final Graphics2D g = mybuffer.createGraphics();
        g.drawImage(mypicbackground, 0, 0, null);

        g.setBackground(backgroundColor);
        g.clearRect(0, 0, w, h);
        render(g);
        g.dispose();
        setChanged();
    }

    protected abstract void render(final Graphics g);

    public void setVisible(final boolean v)
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

        setTooltipText(item == null ? null : item.getName());
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }
}
