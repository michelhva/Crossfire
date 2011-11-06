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
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for labels that render text.
 * @author Andreas Kirschbaum
 */
public abstract class GUILabel extends AbstractLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The text alignment.
     */
    @NotNull
    private final Alignment textAlignment;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param picture the background image; <code>null</code> for no background
     * @param text the text
     * @param textFont the font for rendering the label text
     * @param textColor the font color
     * @param backgroundColor the background color
     * @param textAlignment the text alignment
     */
    protected GUILabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @Nullable final BufferedImage picture, @NotNull final String text, @NotNull final Font textFont, @NotNull final Color textColor, @Nullable final Color backgroundColor, @NotNull final Alignment textAlignment) {
        super(tooltipManager, elementListener, name, text, textFont, textColor, picture, backgroundColor);
        this.textAlignment = textAlignment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void textChanged() {
        setChanged();
    }

    /**
     * Draws one line of text.
     * @param g the graphics to paint to
     * @param y0 the y-coordinate to draw to
     * @param h0 the line height
     * @param text the text to draw
     */
    protected void drawLine(@NotNull final Graphics2D g, final int y0, final int h0, @NotNull final String text) {
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.setFont(getTextFont());
        g.setColor(getTextColor());
        final RectangularShape rectangle = getTextFont().getStringBounds(text, g.getFontRenderContext());
        final int y = y0+(int)(Math.round(h0-rectangle.getHeight())/2-rectangle.getY());
        switch (textAlignment) {
        case LEFT:
            g.drawString(text, 0, y);
            break;

        case CENTER:
            g.drawString(text, (int)Math.round((getWidth()-rectangle.getWidth())/2), y);
            break;

        case RIGHT:
            g.drawString(text, (int)Math.round(getWidth()-rectangle.getWidth()), y);
            break;
        }
    }

}
