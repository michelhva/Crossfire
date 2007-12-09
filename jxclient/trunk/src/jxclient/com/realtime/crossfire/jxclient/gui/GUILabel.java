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
import java.awt.Color;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Abstrct base class for labels that render text.
 *
 * @author Andreas Kirschbaum
 */
public abstract class GUILabel extends AbstractLabel
{
    /**
     * The font for rendering the label text.
     */
    private final Font font;

    /**
     * The text color.
     */
    private final Color color;

    /**
     * The text alignment.
     */
    private final Alignment alignment;

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
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
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
     * @param font The font for rendering the label text.
     *
     * @param color The font color.
     *
     * @param alignment The text alignment.
     *
     * @param text The label text.
     */
    public GUILabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Color color, final Alignment alignment, final String text)
    {
        super(jxcWindow, name, x, y, w, h, picture, text);
        this.font = font;
        this.color = color;
        this.alignment = alignment;
        render();
    }

    /** {@inheritDoc} */
    @Override protected void textChanged()
    {
        render();
    }

    /**
     * Draw one line of text.
     *
     * @param y0 The y-ccordinate to draw to.
     *
     * @param h0 The line height.
     *
     * @param text The text to draw.
     *
     * @return The line height.
     */
    protected int drawLine(final int y0, final int h0, final String text)
    {
        if (font == null)
        {
            return 0;
        }

        final Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.setFont(font);
        g.setColor(color);
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        final int y = y0+(int)Math.round((h0-rect.getMaxY()-rect.getMinY()))/2;
        switch (alignment)
        {
        case LEFT:
            g.drawString(text, 0, y);
            break;

        case CENTER:
            g.drawString(text, (int)Math.round((w-rect.getWidth())/2), y);
            break;

        case RIGHT:
            g.drawString(text, (int)Math.round(w-rect.getWidth()), y);
            break;
        }
        g.dispose();

        return (int)(rect.getHeight()+0.5);
    }

    /**
     * Return the font.
     *
     * @return The font.
     */
    protected Font getFont()
    {
        return font;
    }
}
