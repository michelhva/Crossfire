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
 * Abstrct base class for labels that render text.
 *
 * @author Andreas Kirschbaum
 */
public abstract class GUILabel extends AbstractLabel
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The font for rendering the label text.
     */
    @Nullable
    private final Font textFont;

    /**
     * The text color.
     */
    @NotNull
    private final Color textColor;

    /**
     * The text alignment.
     */
    @NotNull
    private final Alignment textAlignment;

    /**
     * The text alignment.
     */
    public enum Alignment
    {
        /** Left-aligned. */
        LEFT,

        /** Center-aligned. */
        CENTER,

        /** Right-aligned. */
        RIGHT,
    }

    /**
     * Create a new instance.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param elementListener the element listener to notify
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param picture The background image; <code>null</code> for no
     * background.
     *
     * @param textFont The font for rendering the label text.
     *
     * @param textColor The font color.
     *
     * @param backgroundColor The background color.
     *
     * @param textAlignment The text alignment.
     */
    protected GUILabel(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int x, final int y, final int w, final int h, @Nullable final BufferedImage picture, @Nullable final Font textFont, @NotNull final Color textColor, @NotNull final Color backgroundColor, @NotNull final Alignment textAlignment)
    {
        super(tooltipManager, elementListener, name, x, y, w, h, picture, backgroundColor);
        this.textFont = textFont;
        this.textColor = textColor;
        this.textAlignment = textAlignment;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /** {@inheritDoc} */
    @Override
    protected void textChanged()
    {
        setChanged();
    }

    /**
     * Draw one line of text.
     *
     * @param g The graphics to paint to.
     *
     * @param y0 The y-ccordinate to draw to.
     *
     * @param h0 The line height.
     *
     * @param text The text to draw.
     *
     * @return The line height.
     */
    protected int drawLine(@NotNull final Graphics2D g, final int y0, final int h0, @NotNull final String text)
    {
        if (textFont == null)
        {
            return 0;
        }

        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.setFont(textFont);
        g.setColor(textColor);
        final RectangularShape rect = textFont.getStringBounds(text, g.getFontRenderContext());
        final int y = y0+(int)Math.round((h0-rect.getMaxY()-rect.getMinY()))/2;
        switch (textAlignment)
        {
        case LEFT:
            g.drawString(text, 0, y);
            break;

        case CENTER:
            g.drawString(text, (int)Math.round((getWidth()-rect.getWidth())/2), y);
            break;

        case RIGHT:
            g.drawString(text, (int)Math.round(getWidth()-rect.getWidth()), y);
            break;
        }

        return (int)(rect.getHeight()+0.5);
    }

    /**
     * Return the font.
     *
     * @return The font.
     */
    @Nullable
    protected Font getTextFont()
    {
        return textFont;
    }
}
