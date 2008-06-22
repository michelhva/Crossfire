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

import com.realtime.crossfire.jxclient.gui.textinput.GUIText;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntry;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntryListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class GUIMetaElement extends ActivatableGUIElement implements GUIScrollable
{
    private final Metaserver metaserver;

    private final BufferedImage tcpImage;

    private final Font font;

    private final GUIText text;

    private final AbstractLabel comment;

    private final String format;

    private final String tooltip;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    private int index;

    /**
     * The metaserver entry listener attached for the current {@link #index}.
     */
    private final MetaserverEntryListener metaserverEntryListener = new MetaserverEntryListener()
    {
        /** {@inheritDoc} */
        public void entryAdded()
        {
            setChanged();
            updateTooltip();
        }

        /** {@inheritDoc} */
        public void entryRemoved()
        {
            setChanged();
            updateTooltip();
        }

        /** {@inheritDoc} */
        public void entryChanged()
        {
            setChanged();
            updateTooltip();
        }
    };

    public GUIMetaElement(final JXCWindow window, final Metaserver metaserver, final String name, final int x, final int y, final int w, final int h, final BufferedImage tcpImage, final Font font, final GUIText text, final AbstractLabel comment, final int defaultIndex, final String format, final String tooltip)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);
        this.metaserver = metaserver;
        this.tcpImage = tcpImage;
        this.font = font;
        this.text = text;
        this.comment = comment;
        this.defaultIndex = defaultIndex;
        index = defaultIndex;
        this.format = format;
        this.tooltip = tooltip;
        metaserver.addMetaserverEntryListener(defaultIndex, metaserverEntryListener);
        setChanged();
        updateTooltip();
    }

    /** {@inheritDoc} */
    @Override public void paintComponent(final Graphics2D g)
    {
        super.paintComponent(g);

        final MetaserverEntry metaEntry = metaserver.getEntry(index);
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
            setChanged();
            break;

        case MouseEvent.BUTTON2:
            break;

        case MouseEvent.BUTTON3:
            break;
        }
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
            return index+distance < metaserver.size();
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public void scroll(final int distance)
    {
        metaserver.removeMetaserverEntryListener(index, metaserverEntryListener);
        index += distance;
        metaserver.addMetaserverEntryListener(index, metaserverEntryListener);
        setChanged();
        updateTooltip();
    }

    /** {@inheritDoc} */
    public void resetScroll()
    {
        if (index != defaultIndex)
        {
            scroll(defaultIndex-index);
        }
    }

    /** {@inheritDoc} */
    @Override protected void activeChanged()
    {
        super.activeChanged();

        if (!isActive())
        {
            if (comment != null)
            {
                comment.setText("");
            }
        }
        else
        {
            final MetaserverEntry metaEntry = metaserver.getEntry(index);
            if (comment != null)
            {
                comment.setText(metaEntry != null ? metaEntry.getComment() : "");
            }
            if (text != null)
            {
                text.setText(metaEntry != null ? metaEntry.getHostname() : "");
            }
        }

        setChanged();
    }

    /**
     * Returns the index of this element.
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Updates the tooltip text.
     */
    private void updateTooltip()
    {
        final MetaserverEntry metaEntry = metaserver.getEntry(index);
        setTooltipText(metaEntry == null ? null : metaEntry.format(tooltip));
    }
}
