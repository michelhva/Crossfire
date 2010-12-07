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

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Dimension;
import java.awt.Transparency;
import org.jetbrains.annotations.NotNull;

/**
 * A completely transparent {@link GUIElement}.
 * @author Andreas Kirschbaum
 */
public class GUIEmpty extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The preferred width of this component.
     */
    private final int preferredWidth;

    /**
     * The preferred height of this component.
     */
    private final int preferredHeight;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param preferredWidth the preferred width of this component
     * @param preferredHeight the preferred height of this component
     */
    public GUIEmpty(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int preferredWidth, final int preferredHeight) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Math.max(1, preferredWidth), Math.max(1, preferredHeight));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(preferredWidth == 0 ? Integer.MAX_VALUE : preferredWidth, preferredHeight == 0 ? Integer.MAX_VALUE : preferredHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(Math.max(1, preferredWidth), Math.max(1, preferredHeight));
    }

}
