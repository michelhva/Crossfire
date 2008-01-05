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
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntry;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntryListener;
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
 * @since 1.0
 */
public class GUIMetaElement extends ActivatableGUIElement implements GUIScrollable
{
    private final BufferedImage tcpImage;

    private final Font font;

    private final GUIText text;

    private final AbstractLabel comment;

    private final String format;

    private int index;

    /**
     * The metaserver entry listener attached for the current {@link #index}.
     */
    private final MetaserverEntryListener metaserverEntryListener = new MetaserverEntryListener()
    {
        /** {@inheritDoc} */
        public void entryAdded()
        {
            render();
        }

        /** {@inheritDoc} */
        public void entryRemoved()
        {
            render();
        }

        /** {@inheritDoc} */
        public void entryChanged()
        {
            render();
        }
    };

    public GUIMetaElement(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage tcpImage, final Font font, final GUIText text, final AbstractLabel comment, final int index, final String format)
    {
        super(jxcWindow, name, x, y, w, h);
        this.tcpImage = tcpImage;
        this.font = font;
        this.text = text;
        this.comment = comment;
        this.index = index;
        this.format = format;
        createBuffer();
        jxcWindow.getMetaserver().addMetaserverEntryListener(index, metaserverEntryListener);
        render();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        final MetaserverEntry metaEntry = getJXCWindow().getMetaserver().getEntry(index);
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        g.setFont(font);
        g.setColor(isActive() ? Color.RED : Color.GRAY);
        if (tcpImage != null)
        {
            g.drawImage(tcpImage, 0, 0, null);
        }
        g.drawString(metaEntry == null ? "" : metaEntry.format(format), tcpImage != null ? 16 : 0, font.getSize()+1);
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            setActive(true);
            render();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    /** {@inheritDoc} */
    @Override public boolean setActive(final boolean active)
    {
        if (!super.setActive(active))
        {
            return false;
        }

        if (!active && comment != null)
        {
            comment.setText("");
        }

        render();
        return true;
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            return index+distance < getJXCWindow().getMetaserver().size();
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public void scroll(final int distance)
    {
        getJXCWindow().getMetaserver().removeMetaserverEntryListener(index, metaserverEntryListener);
        index += distance;
        getJXCWindow().getMetaserver().addMetaserverEntryListener(index, metaserverEntryListener);
        render();
    }

    public int getIndex()
    {
        return index;
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

    /** {@inheritDoc} */
    @Override protected void activeChanged()
    {
        super.activeChanged();

        if (!isActive())
        {
            return;
        }

        final MetaserverEntry metaEntry = getJXCWindow().getMetaserver().getEntry(index);
        if (comment != null)
        {
            comment.setText(metaEntry != null ? metaEntry.getComment() : "");
        }
        if (text != null)
        {
            text.setText(metaEntry != null ? metaEntry.getHostname() : "");
        }
    }
}
