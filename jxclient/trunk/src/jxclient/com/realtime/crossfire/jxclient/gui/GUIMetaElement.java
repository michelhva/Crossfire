/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import com.realtime.crossfire.jxclient.metaserver.Metaserver;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntry;
import com.realtime.crossfire.jxclient.metaserver.MetaserverEntryListener;
import com.realtime.crossfire.jxclient.metaserver.MetaserverModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Display a Crossfire server entry.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIMetaElement extends ActivatableGUIElement implements GUIScrollable
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The metaserver model to monitor.
     */
    @NotNull
    private final MetaserverModel metaserverModel;

    /**
     * An image to draw before the server description. May be <code>null</code>
     * to draw no image.
     */
    @Nullable
    private final Image tcpImage;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * The format used for displaying {@link Metaserver} instances.
     */
    @NotNull
    private final String format;

    /**
     * The format used for displaying tooltips.
     */
    @NotNull
    private final String tooltip;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The metaserver index.
     */
    private int index;

    /**
     * If set, paint the element in "selected" state.
     */
    private boolean selected = false;

    /**
     * The metaserver entry listener attached for the current {@link #index}.
     */
    @NotNull
    private final MetaserverEntryListener metaserverEntryListener = new MetaserverEntryListener()
    {
        /** {@inheritDoc} */
        @Override
        public void entryChanged()
        {
            setChanged();
            updateTooltip();
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param metaserverModel the metaserver model to monitor
     * @param name the name of this element
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param tcpImage an image to draw before the server description. May be
     * <code>null</code> to draw no image
     * @param font the font to use
     * @param defaultIndex the initial metaserver index
     * @param format the format used to display metaserver instances
     * @param tooltip the format used for displaying tooltips
     */
    public GUIMetaElement(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final MetaserverModel metaserverModel, @NotNull final String name, final int w, final int h, @Nullable final Image tcpImage, @NotNull final Font font, final int defaultIndex, @NotNull final String format, @NotNull final String tooltip)
    {
        super(tooltipManager, elementListener, name, 0, 0, w, h, Transparency.TRANSLUCENT);
        this.metaserverModel = metaserverModel;
        this.tcpImage = tcpImage;
        this.font = font;
        this.defaultIndex = defaultIndex;
        index = defaultIndex;
        this.format = format;
        this.tooltip = tooltip;
        this.metaserverModel.addMetaserverEntryListener(index, metaserverEntryListener);
        setChanged();
        updateTooltip();
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        metaserverModel.removeMetaserverEntryListener(index, metaserverEntryListener);
    }

    /** {@inheritDoc} */
    @Override
    protected void render(@NotNull final Graphics g)
    {
        final MetaserverEntry metaEntry = metaserverModel.getEntry(index);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setFont(font);
        g.setColor(isActive() || selected ? Color.RED : Color.GRAY);
        if (tcpImage != null)
        {
            g.drawImage(tcpImage, 0, 0, null);
        }
        g.drawString(metaEntry == null ? "" : metaEntry.format(format), tcpImage != null ? 16 : 0, font.getSize()+1);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e)
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
    @Override
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            return index+distance < metaserverModel.size();
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void scroll(final int distance)
    {
        setIndex(index+distance);
    }

    /** {@inheritDoc} */
    @Override
    public void resetScroll()
    {
        if (index != defaultIndex)
        {
            scroll(defaultIndex-index);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void activeChanged()
    {
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
     * Sets the index of this element.
     * @param index the index
     */
    public void setIndex(final int index)
    {
        if (this.index == index)
        {
            return;
        }

        metaserverModel.removeMetaserverEntryListener(index, metaserverEntryListener);
        this.index = index;
        metaserverModel.addMetaserverEntryListener(index, metaserverEntryListener);
        setChanged();
        updateTooltip();
    }

    /**
     * Updates the tooltip text.
     */
    private void updateTooltip()
    {
        final MetaserverEntry metaEntry = metaserverModel.getEntry(index);
        setTooltipText(metaEntry == null ? null : metaEntry.format(tooltip));
    }

    /**
     * Sets the selected state.
     * @param selected whether this element should drawn as "selected"
     */
    public void setSelected(final boolean selected)
    {
        if (this.selected == selected)
        {
            return;
        }

        this.selected = selected;
        setChanged();
    }
}
