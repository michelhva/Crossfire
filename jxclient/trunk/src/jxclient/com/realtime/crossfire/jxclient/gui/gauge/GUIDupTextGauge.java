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
package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A {@link GUIDupGauge} which displays the current value as a text string on top
 * of the gauge.
 * @author Andreas Kirschbaum
 */
public class GUIDupTextGauge extends GUIDupGauge
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
     * The label text.
     */
    private String labelText = "";

    /**
     * Creates a new instance.
     * @param window the <code>JXCWindow</code> this element belongs to.
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param pictureFullDiv the top image
     * @param pictureFullMod the bottom image
     * @param pictureEmpty the image for an empty gauge
     * @param orientationDiv the gauge's orientation
     * @param orientationMod the gauge's orientation
     * @param tooltipPrefix the prefix for generating a tooltip
     * @param color the text color
     * @param font the text font
     */
    public GUIDupTextGauge(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final BufferedImage pictureFullDiv, final BufferedImage pictureFullMod, final BufferedImage pictureEmpty, final Orientation orientationDiv, final Orientation orientationMod, final String tooltipPrefix, final Color color, final Font font)
    {
        super(window, name, x, y, w, h, pictureFullDiv, pictureFullMod, pictureEmpty, orientationDiv, orientationMod, tooltipPrefix);
        if (color == null) throw new IllegalArgumentException();
        if (font == null) throw new IllegalArgumentException();
        this.color = color;
        this.font = font;
    }

    /** {@inheritDoc} */
    @Override protected void paintComponent(final Graphics2D g)
    {
        super.paintComponent(g);

        if (font == null)
        {
            return;
        }

        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.setColor(color);
        g.setFont(font);
        final String text = labelText;
        final Rectangle2D rect = font.getStringBounds(text, g.getFontRenderContext());
        g.drawString(text, (int)Math.round((w-rect.getWidth())/2), (int)Math.round((h-rect.getMaxY()-rect.getMinY()))/2);
    }

    /** {@inheritDoc} */
    @Override public void setValues(final int curValue, final int minValue, final int maxValue, final String labelText, final String tooltipText)
    {
        super.setValues(curValue, minValue, maxValue, labelText, tooltipText);
        this.labelText = labelText;
        setChanged();
    }
}
