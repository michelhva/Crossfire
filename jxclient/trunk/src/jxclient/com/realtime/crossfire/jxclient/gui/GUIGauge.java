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
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
     * The current value.
     */
    private int curValue = 0;

    /**
     * The maximum value; the gauge is displayed as full if <code>{@link
     * #curValue} &gt;= maxValue</code>.
     */
    private int maxValue = -1;

    /**
     * The minimum value; the gauge is displayed as empty if <code>{@link
     * #curValue} &gt;= minValue</code>.
     */
    private int minValue = 0;

    /**
     * The label text.
     */
    private String labelText = "";

    /**
     * The tooltip suffix. It is appended to {@link #tooltipPrefix} to form the
     * tooltip.
     */
    private String tooltipText = "";

    /**
     * The width of the "filled" area.
     */
    private int fw = 0;

    /**
     * The height of the "filled" area.
     */
    private int fh = 0;

    /**
     * The x-coordinate of the "filled" area.
     */
    private int fx = 0;

    /**
     * The y-coordinate of the "filled" area.
     */
    private int fy = 0;

    /**
     * The image for painting the "filled" area.
     */
    private BufferedImage fPicture = null;

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
     * The tooltip prefix. It is prepended to {@link #tooltipText} to form the
     * tooltip.
     */
    private final String tooltipPrefix;

    /**
     * A gauge's orientation.
     */
    public enum Orientation
    {
        /** Gauge fills west-&gt;east. */
        WE,

        /** Gauge fills east-&gt;west. */
        EW,

        /** Gauge fills north-&gt;south. */
        NS,

        /** Gauge fills south-&gt;north. */
        SN,
    }

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
        super(jxcWindow, name, x, y, w, h);
        checkSize(fullImage, "full", w, h);
        checkSize(negativeImage, "negative", w, h);
        checkSize(emptyImage, "empty", w, h);
        this.fullImage = fullImage;
        this.negativeImage = negativeImage;
        this.emptyImage = emptyImage;
        createBuffer();
        this.orientation = orientation;
        this.tooltipPrefix = tooltipPrefix;
        tooltipText = "-";      // make sure the following setValues() does not short-cut
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
    static void checkSize(final BufferedImage image, final String name, final int w, final int h)
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

    /**
     * Updates the cached graphical state ({@link #buffer}) to reflect the
     * current values.
     */
    public void updateValues()
    {
        if (maxValue <= minValue)
        {
            draw(0, 0, 0, 0, null);
            return;
        }

        int fw;
        int fh;
        int fx = 0;
        int fy = 0;

        if (curValue < 0 && negativeImage != null)
        {
            switch (orientation)
            {
            case WE:
                fw = (int)((float)(-curValue)*((float)w/(float)-minValue)+0.5);
                fh = h;
                fx = w-fw;
                draw(fx, fy, fw, fh, negativeImage);
                break;

            case EW:
                fw = (int)((float)-curValue*((float)w/(float)-minValue)+0.5);
                fh = h;
                draw(fx, fy, fw, fh, negativeImage);
                break;

            case NS:
                fh = (int)((float)-curValue*((float)h/(float)-minValue)+0.5);
                fw = w;
                fy = h-fh;
                draw(fx, fy, fw, fh, negativeImage);
                break;

            case SN:
                fh = (int)((float)-curValue*((float)h/(float)-minValue)+0.5);
                fw = w;
                draw(fx, fy, fw, fh, negativeImage);
                break;
            }
        }
        else
        {
            switch (orientation)
            {
            case WE:
                fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue)+0.5);
                fh = h;
                draw(fx, fy, fw, fh, fullImage);
                break;

            case EW:
                fw = (int)((float)Math.min(curValue, maxValue)*((float)w/(float)maxValue)+0.5);
                fh = h;
                fx = w-fw;
                draw(fx, fy, fw, fh, fullImage);
                break;

            case NS:
                fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue)+0.5);
                fw = w;
                draw(fx, fy, fw, fh, fullImage);
                break;

            case SN:
                fh = (int)((float)Math.min(curValue, maxValue)*((float)h/(float)maxValue)+0.5);
                fy = h-fh;
                fw = w;
                draw(fx, fy, fw, fh, fullImage);
                break;
            }
        }
    }

    /**
     * Draws the given part of a picture to {@link #buffer}.
     * @param fx the x-coordinate of the area to draw from
     * <code>fPicture</code>
     * @param fy the y-coordinate of the area to draw from
     * <code>fPicture</code>
     * @param fw the width of the area to draw from <code>fPicture</code>
     * @param fh the height of the area to draw from <code>fPicture</code>
     * @param fPicture the picture to draw
     */
    private void draw(int fx, int fy, int fw, int fh, final BufferedImage fPicture)
    {
        assert this.w > 0;
        if (fx > this.w)
        {
            fx = this.w;
            fw = 0;
        }
        else
        {
            if (fx < 0)
            {
                fw -= -fx;
                fx = 0;
            }

            if (fw > this.w)
            {
                fw = this.w;
            }
        }

        assert this.h > 0;
        if (fy > this.h)
        {
            fy = this.h;
            fh = 0;
        }
        else
        {
            if (fy < 0)
            {
                fh -= -fy;
                fy = 0;
            }

            if (fh > this.h)
            {
                fh = this.h;
            }
        }

        if (this.fx == fx && this.fy == fy && this.fw == fw && this.fh == fh && this.fPicture == fPicture && !mustRepaint())
        {
            return;
        }

        this.fx = fx;
        this.fy = fy;
        this.fw = fw;
        this.fh = fh;
        this.fPicture = fPicture;
        render();
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
        if (fPicture != null)
        {
            g.drawImage(fPicture, fx, fy, fw+fx, fh+fy, fx, fy, fw+fx, fh+fy, null);
        }
    }

    /**
     * Returns whether the gauge has been changed and must be repainted. May be
     * overridden in sub-classes to force repaints even if the value didn't
     * change.
     * @return whether the gauge should be repainted
     */
    protected boolean mustRepaint()
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
        if (this.curValue == curValue && this.minValue == minValue && this.maxValue == maxValue && this.labelText.equals(labelText) && this.tooltipText.equals(tooltipText))
        {
            return;
        }

        this.curValue = curValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.labelText = labelText;
        this.tooltipText = tooltipText;
        updateValues();

        setTooltipText(tooltipPrefix == null || tooltipText.length() == 0 ? null : tooltipPrefix+tooltipText);
    }

    /** {@inheritDoc} */
    protected void createBuffer()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        buffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }

    /**
     * Returns the displayed value.
     * @return the displayed value
     */
    public int getCurValue()
    {
        return curValue;
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
