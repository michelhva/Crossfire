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

package com.realtime.crossfire.jxclient.gui.log;

/**
 * Abstract base class for {@link Segment} implementations.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractSegment implements Segment {

    /**
     * The x-coordinate to display the segment. Set to <code>-1</code> if
     * unknown.
     */
    private int x = -1;

    /**
     * The y-coordinate to display the segment. Set to <code>-1</code> if
     * unknown.
     */
    private int y = -1;

    /**
     * The width of the segment if displayed. Set to <code>-1</code> if
     * unknown.
     */
    private int width = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Returns the x-coordinate to display the segment.
     * @return the x-coordinate
     */
    protected int getX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Returns the y-coordinate to display the segment.
     * @return the y-coordinate
     */
    protected int getY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Returns the width to display the segment.
     * @return the width
     */
    protected int getWidth() {
        return width;
    }

}
