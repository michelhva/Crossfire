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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIElement} that fills an area with a given color.
 * @author Andreas Kirschbaum
 */
public class GUIFill extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link Color} to paint.
     */
    @NotNull
    private final Color color;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param color the color to paint
     * @param alpha the transparency value
     */
    public GUIFill(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Color color, final float alpha) {
        super(tooltipManager, elementListener, name, alpha < 1F ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
        this.color = color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(color);
        g2.clearRect(0, 0, getWidth(), getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // ignore
    }

}
