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

/**
 * Abstract base class for implementing {@link Orientation} instances.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractOrientation implements Orientation
{
    /**
     * The total width of the image.
     */
    protected int width = 0;

    /**
     * The total height of the image.
     */
    protected int height = 0;

    /**
     * The current value.
     */
    protected int cur = 0;

    /**
     * The minimum value; the gauge is displayed as empty if <code>{@link #cur}
     * &gt;= min</code>.
     */
    protected int min = 0;

    /**
     * The maximum value; the gauge is displayed as full if <code>{@link #cur}
     * &gt;= max</code>.
     */
    protected int max = 0;

    /**
     * The x-coordinate of the highlighted part of the image.
     */
    protected int x = 0;

    /**
     * The y-coordinate of the highlighted part of the image.
     */
    protected int y = 0;

    /**
     * The width of the highlighted part of the image.
     */
    protected int w = 0;

    /**
     * The height of the highlighted part of the image.
     */
    protected int h = 0;

    /**
     * Whether the gauge can display negative images.
     */
    private boolean hasNegativeImage = false;

    /**
     * Creates a new instance.
     */
    protected AbstractOrientation()
    {
    }

    /** {@inheritDoc} */
    public void setHasNegativeImage(final boolean hasNegativeImage)
    {
        if (this.hasNegativeImage == hasNegativeImage)
        {
            return;
        }

        this.hasNegativeImage = hasNegativeImage;
        recalc();
    }

    /** {@inheritDoc} */
    public boolean setValues(final int cur, final int min, final int max)
    {
        if (this.cur == cur && this.min == min && this.max == max)
        {
            return false;
        }

        this.cur = cur;
        this.min = min;
        this.max = max;
        recalc();
        return true;
    }

    /** {@inheritDoc} */
    public void setExtends(final int width, final int height)
    {
        if (this.width == width && this.height == height)
        {
            return;
        }

        this.width = width;
        this.height = height;
        recalc();
    }

    /** {@inheritDoc} */
    public int getX()
    {
        return x;
    }

    /** {@inheritDoc} */
    public int getY()
    {
        return y;
    }

    /** {@inheritDoc} */
    public int getW()
    {
        return w;
    }

    /** {@inheritDoc} */
    public int getH()
    {
        return h;
    }

    /** {@inheritDoc} */
    public boolean isNegativeImage()
    {
        return cur < min && hasNegativeImage;
    }

    /** {@inheritDoc} */
    public boolean isValid()
    {
        return min < max;
    }

    /**
     * Recalculate the extends of the highlighted image part.
     */
    protected abstract void recalc();

    /**
     * Returns the fraction <code>val/max</code> rounded to
     * [<code>0..width</code>].
     * @param val the value
     * @param max the range size
     * @param width the size of the result
     * @return the fraction in pixels
     */
    protected static int calc(final int val, final int max, final int width)
    {
        if (val <= 0 || max <= 0)
        {
            return 0;
        }
        else if (val >= max)
        {
            return width;
        }
        else
        {
            return (width*val+max/2)/max;
        }
    }
}
