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
 *
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class GUIGauge extends GUIElement
{
    private int curValue = 0;

    private int maxValue = -1;

    private int minValue = 0;

    /**
     * The label text.
     */
    private String labelText = "";

    /**
     * The tooltip suffix. If is appended to {@link #tooltipPrefix} to form the
     * tooltip.
     */
    private String tooltipText = "";

    private int fw = 0;

    private int fh = 0;

    private int fx = 0;

    private int fy = 0;

    private BufferedImage fPicture = null;

    private final BufferedImage fullImage;

    private final BufferedImage negativeImage;

    private final BufferedImage emptyImage;

    private final Orientation orientation;

    private final String tooltipPrefix;

    public enum Orientation
    {
        WE,
        EW,
        NS,
        SN,
    }

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
     * Compare the image size to the given values.
     *
     * @param image The image to process.
     *
     * @param name The image name for generating error messages.
     *
     * @param w The expected image width.
     *
     * @param h The expected image height.
     *
     * @throws IllegalArgumentException If <code>image</code> is not
     * <code>null</code> and it's size is not <code>w</code>x<code>h</code>.
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
     * Draw the given part of a picture to {@link #mybuffer}.
     *
     * @param fx The x-coordinate of the area to draw from
     * <code>fPicture</code>.
     *
     * @param fy The y-coordinate of the area to draw from
     * <code>fPicture</code>.
     *
     * @param fw The width of the area to draw from <code>fPicture</code>.
     *
     * @param fh The height of the area to draw from <code>fPicture</code>.
     *
     * @param fPicture The picture to draw.
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
     * May be overridden in sub-classes to force repaints even if the value
     * didn't change.
     *
     * @return Whether the gauge should be repainted.
     */
    protected boolean mustRepaint()
    {
        return false;
    }

    /**
     * Change the displayed values.
     *
     * @param curValue The values to display.
     *
     * @param minValue The minium possible value.
     *
     * @param maxValue The maximum possible value.
     *
     * @param labelText The label text.
     *
     * @param tooltipText The tooltip suffix.
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
        mybuffer = gconf.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        setChanged();
    }

    /**
     * Return the displayed value.
     *
     * @return The displayed value.
     */
    public int getCurValue()
    {
        return curValue;
    }

    /**
     * Return the label text.
     *
     * @return The label text.
     */
    public String getLabelText()
    {
        return labelText;
    }
}
