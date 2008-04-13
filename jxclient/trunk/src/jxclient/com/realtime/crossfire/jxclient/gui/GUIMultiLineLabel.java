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
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

/**
 * A {@link AbstractLabel} that renders the text as a list of plain strings.
 * The lines are separated by newline characters.
 *
 * @author Andreas Kirschbaum
 */
public class GUIMultiLineLabel extends GUILabel
{
    /**
     * The pattern to split the text into lines.
     */
    private static final Pattern lineSeparatorPattern = Pattern.compile(" *\n");

    /**
     * The height of one line.
     */
    private int lineHeight = 0;

    /**
     * The text lines to draw.
     */
    private String[] lines = new String[0];

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
    public GUIMultiLineLabel(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Color color, final Alignment alignment, final String text)
    {
        super(jxcWindow, name, x, y, w, h, picture, font, color, alignment);
        setText(text);
    }

    /** {@inheritDoc} */
    @Override protected void textChanged()
    {
        lines = lineSeparatorPattern.split(getText(), -1);
        super.textChanged();
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);

        if (lines == null)
        {
            return;
        }

        final Font font = getFont();
        if (font == null)
        {
            return;
        }

        final Rectangle2D rect = font.getStringBounds("X", g.getFontRenderContext());
        lineHeight = (int)Math.round(rect.getMaxY()-rect.getMinY()+0.5);

        int y = 0;
        for (final String line : lines)
        {
            y += drawLine(g, y, lineHeight, line);
        }
    }
}
