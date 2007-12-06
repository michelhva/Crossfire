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

/**
 * A {@link GUIGauge} which displays the current value on top of the gauge.
 *
 * @author Andreas Kirschbaum
 */
public class GUITextGauge extends GUIGauge
{
    /**
     * The text color.
     */
    private final Color color;

    /**
     * The text font.
     */
    private final Font font;

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
     * @param pictureFull The image for positive values.
     *
     * @param pictureNegative The image for negative values.
     *
     * @param pictureFull The image for an empty gauge.
     *
     * @param orientation The gauge's orientation.
     *
     * @param tooltipPrefix The prefix for generating a tooltip.
     *
     * @param color The text color.
     *
     * @param font The text font.
     */
    public GUITextGauge(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage pictureFull, final BufferedImage pictureNegative, final BufferedImage pictureEmpty, final Orientation orientation, final String tooltipPrefix, final Color color, final Font font)
    {
        super(jxcWindow, name, x, y, w, h, pictureFull, pictureNegative, pictureEmpty, orientation, tooltipPrefix);
        if (color == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        this.color = color;
        this.font = font;
    }

    /** {@inheritDoc} */
    @Override public void render()
    {
        super.render();
        if (font == null)
        {
            return;
        }

        final Graphics2D g = mybuffer.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.setColor(color);
        g.setFont(font);
        final String text = getLabelText();
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        g.drawString(text, (int)Math.round((w-rect.getWidth())/2), (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2);
        g.dispose();
        setChanged();
    }

    /** {@inheritDoc} */
    @Override protected boolean mustRepaint()
    {
        return true;
    }
}
