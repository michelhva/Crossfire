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

import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A {@link AbstractLabel} that renders the text as a plain string.
 *
 * @author Andreas Kirschbaum
 */
public class GUIOneLineLabel extends GUILabel
{
    /**
     * Create a new instance.
     *
     * @param window The <code>JXCWindow</code> this element belongs to.
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
     * @param backgroundColor The background color.
     *
     * @param alignment The text alignment.
     *
     * @param text The label text.
     */
    public GUIOneLineLabel(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage picture, final Font font, final Color color, final Color backgroundColor, final Alignment alignment, final String text)
    {
        super(window, name, x, y, w, h, picture, font, color, backgroundColor, alignment);
        setText(text);
    }

    /** {@inheritDoc} */
    @Override protected void paintComponent(final Graphics2D g)
    {
        super.paintComponent(g);
        drawLine(g, 0, h, getText());
    }
}
