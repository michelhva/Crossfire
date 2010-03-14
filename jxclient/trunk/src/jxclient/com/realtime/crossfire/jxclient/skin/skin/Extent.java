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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.skin.skin;

/**
 * Encapsulates the extent of a GUI element. It consists of a location and a
 * size.
 * @author Andreas Kirschbaum
 */
public class Extent {

    /**
     * The x coordinate.
     */
    private final int x;

    /**
     * The y coordinate.
     */
    private final int y;

    /**
     * The width.
     */
    private final int w;

    /**
     * The height.
     */
    private final int h;

    /**
     * Creates a new instance.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h thr height
     */
    public Extent(final int x, final int y, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the x coordinate.
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the width.
     * @return the width
     */
    public int getConstantW() {
        return w;
    }

    /**
     * Returns the height.
     * @return the height
     */
    public int getConstantH() {
        return h;
    }

}
