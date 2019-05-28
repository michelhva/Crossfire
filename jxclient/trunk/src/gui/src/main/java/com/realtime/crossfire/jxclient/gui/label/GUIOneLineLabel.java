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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link AbstractLabel} that renders the text as a plain string.
 * @author Andreas Kirschbaum
 */
public class GUIOneLineLabel extends GUILabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the window renderer to notify
     * @param name the name of this element
     * @param picture the background image; {@code null} for no background
     * @param font the font for rendering the label text
     * @param color the font color
     * @param backgroundColor the background color
     * @param alignment the text alignment
     * @param text the label text
     * @param guiFactory the global GUI factory instance
     */
    public GUIOneLineLabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final BufferedImage picture, @NotNull final Font font, @NotNull final Color color, @Nullable final Color backgroundColor, @NotNull final Alignment alignment, @NotNull final String text, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, picture, text, font, color, backgroundColor, alignment, guiFactory);
    }

    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);
        drawLine((Graphics2D)g, 0, getHeight(), getText());
    }

    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size needed to display this component.
     * @return the minimal size
     */
    @NotNull
    private Dimension getMinimumSizeInt() {
        return GuiUtils.getTextDimension(getText(), getFontMetrics(getTextFont()));
    }

    @Override
    public void notifyOpen() {
    }

}
