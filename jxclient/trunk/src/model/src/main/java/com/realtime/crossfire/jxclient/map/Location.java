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

package com.realtime.crossfire.jxclient.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A location on the map.
 * @author Andreas Kirschbaum
 */
public class Location {

    /**
     * The x-coordinate.
     */
    private final int x;

    /**
     * The y-coordinate.
     */
    private final int y;

    /**
     * The layer.
     */
    private final int layer;

    /**
     * Creates a new location.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param layer the layer
     */
    public Location(final int x, final int y, final int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    /**
     * Returns the x-coordinate.
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate.
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the layer.
     * @return the layer
     */
    public int getLayer() {
        return layer;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != Location.class) {
            return false;
        }
        final Location loc = (Location)obj;
        return loc.x == x && loc.y == y && loc.layer == layer;
    }

    @Override
    public int hashCode() {
        return x^y*0x1000^layer*0x1000000;
    }

    @NotNull
    @Override
    public String toString() {
        return x+"/"+y+"/"+layer;
    }

}
