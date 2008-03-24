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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfItemModifiedListener;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateFaceListener;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public abstract class GUIItem extends ActivatableGUIElement implements GUIScrollable
{
    protected final BufferedImage cursedImage;

    protected final BufferedImage appliedImage;

    protected final BufferedImage selectorImage;

    protected final BufferedImage lockedImage;

    private CfItem item = null;

    protected final Font font;

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
            assert GUIItem.this.item == item;
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
            if (item == null)
            {
                return;
            }

            final Face face = item.getFace();
            if (face == null || face.getID() != faceID)
            {
                return;
            }

            render();
        }
    };

    public GUIItem(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final CrossfireServerConnection crossfireServerConnection, final Font font)
    {
        super(jxcWindow, name, x, y, w, h);
        this.cursedImage = cursedImage;
        this.appliedImage = appliedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.font = font;
        createBuffer();
        render();
        jxcWindow.getCrossfireServerConnection().addCrossfireUpdateFaceListener(crossfireUpdateFaceListener);
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final JXCWindow jxcw = (JXCWindow)e.getSource();
        switch (e.getButton())
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

    /** {@inheritDoc} */
    @Override public void activeChanged()
    {
        render();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);
        g.setBackground(backgroundColor);
        g.clearRect(0, 0, w, h);
    }

    public void setVisible(final boolean v)
    {
        super.setVisible(v);
        render();
    }

    protected CfItem getItem()
    {
        return item;
    }

    protected void setItem(final CfItem item)
    {
        if (this.item == item)
        {
            return;
        }

        if (this.item != null)
        {
            this.item.removeCfItemModifiedListener(itemModifiedListener);
        }
        this.item = item;
        if (this.item != null)
        {
            this.item.addCfItemModifiedListener(itemModifiedListener);
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
