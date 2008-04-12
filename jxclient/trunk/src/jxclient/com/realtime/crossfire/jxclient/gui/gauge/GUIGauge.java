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

import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Transparency;

/**
 * Displays a value as a graphical gauge that's filling state depends on the
 * value.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUIGauge extends GUIElement
{
    /**
     * The label text.
     */
    private String labelText = "";

    /**
     * The tooltip prefix. It is prepended to {@link #tooltipText} to form the
     * tooltip.
     */
    private final String tooltipPrefix;

    /**
     * The tooltip suffix. It is appended to {@link #tooltipPrefix} to form the
     * tooltip.
     */
    private String tooltipText = "";

    /**
     * The image representing a full gauge.
     */
    private final BufferedImage fullImage;

    /**
     * The image representing a more-than-empty gauge.
     */
    private final BufferedImage negativeImage;

    /**
     * The image representing an empty gauge.
     */
    private final BufferedImage emptyImage;

    /**
     * The gauge's orientation.
     */
    private final Orientation orientation;

    /**
     * The gauge state.
     */
    private final GaugeState gaugeState = new GaugeState(this);

    /**
     * Creates a new instance.
     * @param jxcWindow the <code>JXCWindow</code> this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param fullImage the image representing a full gauge
     * @param negativeImage the image representing a more-than-empty gauge; if
     * set to <code>null</code> the gauge remains in empty state
     * @param emptyImage the image representing an empty gauge; if set to
     * <code>null</code> an empty background is used instead
     * @param orientation the gauge's orientation
     * @param tooltipPrefix the prefix for displaying tooltips; if set to
     * <code>null</code> no tooltips are shown
     */
    public GUIGauge(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage fullImage, final BufferedImage negativeImage, final BufferedImage emptyImage, final Orientation orientation, final String tooltipPrefix)
    {
        super(jxcWindow, name, x, y, w, h, Transparency.TRANSLUCENT);
        checkSize(fullImage, "full", w, h);
        checkSize(negativeImage, "negative", w, h);
        checkSize(emptyImage, "empty", w, h);
        this.fullImage = fullImage;
        this.negativeImage = negativeImage;
        this.emptyImage = emptyImage;
        this.orientation = orientation;
        this.tooltipPrefix = tooltipPrefix;
        tooltipText = "-";      // make sure the following setValues() does not short-cut
        orientation.setExtends(w, h);
        orientation.setHasNegativeImage(negativeImage != null);
        setValues(0, 0, 0, "", "");
    }

    /**
     * Validates an images's size: checks if the given image has the given size
     * (in pixels).
     * @param image the image to validate; if set to <code>null</code> no
     * exception is thrown
     * @param name the image's name for generating error messages
     * @param w the expected image width
     * @param h the expected image height
     * @throws IllegalArgumentException if <code>image</code> is not
     * <code>null</code> and it's size is not <code>w</code>x<code>h</code>
     */
    private static void checkSize(final BufferedImage image, final String name, final int w, final int h)
    {
        if (image == null)
        {
            return;
        }

        if (image.getWidth() != w)
        {
            throw new IllegalArgumentException("width of '"+name+"' does not match element width");
        }

        if (image.getHeight() != h)
        {
            throw new IllegalArgumentException("height of '"+name+"' does not match element height");
        }
    }

    /** {@inheritDoc} */
    @Override protected void render(final Graphics2D g)
    {
        super.render(g);
        g.setBackground(new Color(0, 0, 0, 0.0f));
        g.clearRect(0, 0, w, h);
        if (emptyImage != null)
        {
            g.drawImage(emptyImage, 0, 0, null);
        }
        gaugeState.draw(g);
    }

    /**
     * Returns whether the gauge has been changed and must be repainted. May be
     * overridden in sub-classes to force repaints even if the value didn't
     * change.
     * @return whether the gauge should be repainted
     */
    public boolean mustRepaint()
    {
        return false;
    }

    /**
     * Sets the values to display.
     * @param curValue the values to display
     * @param minValue the minium possible value
     * @param maxValue the maximum possible value
     * @param labelText the label text
     * @param tooltipText the tooltip suffix
     */
    public void setValues(final int curValue, final int minValue, final int maxValue, final String labelText, final String tooltipText)
    {
        if (!orientation.setValues(curValue, minValue, maxValue) && this.labelText.equals(labelText) && this.tooltipText.equals(tooltipText))
        {
            return;
        }

        this.labelText = labelText;
        this.tooltipText = tooltipText;

        gaugeState.draw(orientation.getX(), orientation.getY(), orientation.getW(), orientation.getH(), !orientation.isValid() ? null : orientation.isNegativeImage() ? negativeImage : fullImage);

        setTooltipText(tooltipPrefix == null || tooltipText.length() == 0 ? null : tooltipPrefix+tooltipText);
    }

    /**
     * Returns the label text.
     * @return the label text
     */
    public String getLabelText()
    {
        return labelText;
    }
}
