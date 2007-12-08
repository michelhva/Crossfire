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
import com.realtime.crossfire.jxclient.Metaserver;
import com.realtime.crossfire.jxclient.MetaserverEntry;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMetaElement extends GUIElement implements GUIScrollable
{
    private final BufferedImage tcpImage;

    private final BufferedImage udpImage;

    private final Font font;

    private final GUIText text;

    private final AbstractLabel comment;

    private final String format;

    private int index;

    public GUIMetaElement(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage tcpImage, final BufferedImage udpImage, final Font font, final GUIText text, final AbstractLabel comment, final int index, final String format)
    {
        super(jxcWindow, name, x, y, w, h);
        this.tcpImage = tcpImage;
        this.udpImage = udpImage;
        this.font = font;
        this.text = text;
        this.comment = comment;
        this.index = index;
        this.format = format;
        createBuffer();
        render();
    }

    protected void render()
    {
        final List<MetaserverEntry> metaEntries = Metaserver.query();
        if (index < 0 || index >= metaEntries.size())
        {
            return;
        }

        final MetaserverEntry metaEntry = metaEntries.get(index);
        final Graphics2D g = mybuffer.createGraphics();
        g.setFont(font);
        g.setColor(active ? Color.RED : Color.GRAY);
        if (tcpImage != null)
        {
            g.drawImage(tcpImage, 0, 0, null);
        }
        g.drawString(metaEntry.format(format), 16, font.getSize()+1);
        g.dispose();
        if (comment != null && active)
        {
            comment.setText(metaEntry.getComment());
        }
        if (text != null && active)
        {
            text.setText(metaEntry.getHost());
        }
        setChanged();
    }

    /** {@inheritDoc} */
    @Override public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        final int b = e.getButton();
        switch (b)
        {
        case MouseEvent.BUTTON1:
            active = true;
            render();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
    }

    public void setActive(final boolean active)
    {
        if (this.active && !active)
        {
            if (comment != null)
            {
                comment.setText("");
            }
        }
        this.active = active;
        render();
    }

    /** {@inheritDoc} */
    public boolean canScrollUp()
    {
        return index > 0;
    }

    public void scrollUp()
    {
        index--;
        render();
    }

    /** {@inheritDoc} */
    public boolean canScrollDown()
    {
        return index+1 < Metaserver.query().size();
    }

    public void scrollDown()
    {
        index++;
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
}
