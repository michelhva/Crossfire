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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.gauge;

/**
 * Abstract base class for implementing {@link Orientation} instances.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractOrientation implements Orientation {

    /**
     * The total width of the image.
     */
    private int width;

    /**
     * The total height of the image.
     */
    private int height;

    /**
     * The current value.
     */
    private int cur;

    /**
     * The minimum value; the gauge is displayed as empty if {@code cur >=
     * min}.
     */
    private int min;

    /**
     * The maximum value; the gauge is displayed as full if {@code cur >= max}.
     */
    private int max;

    /**
     * The x-coordinate of the highlighted part of the image.
     */
    private int x;

    /**
     * The y-coordinate of the highlighted part of the image.
     */
    private int y;

    /**
     * The width of the highlighted part of the image.
     */
    private int w;

    /**
     * The height of the highlighted part of the image.
     */
    private int h;

    /**
     * Whether the gauge can display negative images.
     */
    private boolean hasNegativeImage;

    /**
     * Creates a new instance.
     */
    protected AbstractOrientation() {
    }

    @Override
    public void setHasNegativeImage(final boolean hasNegativeImage) {
        if (this.hasNegativeImage == hasNegativeImage) {
            return;
        }

        this.hasNegativeImage = hasNegativeImage;
        reCalculate();
    }

    @Override
    public boolean setValues(final int cur, final int min, final int max) {
        if (this.cur == cur && this.min == min && this.max == max) {
            return false;
        }

        this.cur = cur;
        this.min = min;
        this.max = max;
        reCalculate();
        return true;
    }

    @Override
    public boolean setExtends(final int width, final int height) {
        if (this.width == width && this.height == height) {
            return false;
        }

        this.width = width;
        this.height = height;
        reCalculate();
        return true;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public int getH() {
        return h;
    }

    @Override
    public boolean isNegativeImage() {
        return cur < min && hasNegativeImage;
    }

    @Override
    public boolean isValid() {
        return min < max;
    }

    /**
     * Recalculate the extents of the highlighted image part.
     */
    protected abstract void reCalculate();

    /**
     * Returns the fraction {@code val/max} rounded to [{@code 0..size}].
     * @param val the value
     * @param max the range size
     * @param size the size of the result
     * @return the fraction in pixels
     */
    protected static int calculate(final int val, final int max, final int size) {
        if (val <= 0 || max <= 0) {
            return 0;
        }
        if (val >= max) {
            return size;
        }
        return (size*val+max/2)/max;
    }

    /**
     * Returns the total width of the image.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the total height of the image.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the current value.
     * @return the current value
     */
    public int getCur() {
        return cur;
    }

    /**
     * Returns the minimum value.
     * @return the minimum value
     */
    public int getMin() {
        return min;
    }

    /**
     * Returns the maximum value.
     * @return the maximum value
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the extent of the highlighted part of the image.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param w the width
     * @param h the height
     */
    protected void setExtent(final int x, final int y, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

}
