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
package com.realtime.crossfire.jxclient.server;

import java.util.EventObject;

/**
 * Stores information of the "magicmap" Crossfire protocol command.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class CrossfireCommandMagicmapEvent extends EventObject
{
    /** The serial version UID. */
    private static final long serialVersionUID = 1;

    /**
     * Bitmask to extract the color information of a tile in {@link #data}.
     */
    public static final int FACE_COLOR_MASK = 0x0F;

    /**
     * Bitmask to denote a floor tile in {@link #data}.
     */
    public static final int FACE_FLOOR = 0x80;

    /**
     * Bitmask to denote a wall tile in {@link #data}.
     */
    public static final int FACE_WALL = 0x40;

    /**
     * The width of {@link #data} in tiles.
     */
    private final int width;

    /**
     * The height of {@link #data} in tiles.
     */
    private final int height;

    /**
     * The x-coordinate of the player.
     */
    private final int px;

    /**
     * The y-coordinate of the player.
     */
    private final int py;

    /**
     * The data describing the tiles. It is 1 byte per tile, with the low
     * nibble containing the color information, and the high nibble containing
     * extra flags, like the existance of walls and floors. See the {@link
     * #FACE_FLOOR} and {@link #FACE_WALL} values. The string of data
     * represents the tiles from left to right, then up to down.
     */
    private final byte[] data;

    /**
     * The index of the first valid byte in {@link #data}.
     */
    private final int pos;

    /**
     * Creates a new instance.
     * @param src the source object; currently unused
     * @param width the width of <code>data</code>
     * @param height the height of <code>data</code>
     * @param px the x-coordinate of the player
     * @param py the y-coordinate of the player
     * @param data the data describing tiles
     * @param pos the index of the first valid byte in <code>data</code>
     */
    public CrossfireCommandMagicmapEvent(final Object src, final int width, final int height, final int px, final int py, final byte[] data, final int pos)
    {
        super(src);
        this.width = width;
        this.height = height;
        this.px = px;
        this.py = py;
        this.data = data;
        this.pos = pos;
    }

    /**
     * Returns the width of the tile data.
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height of the tile data.
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Returns the x-coordinate of the player.
     * @return the x-coodinate
     */
    public int getPX()
    {
        return px;
    }

    /**
     * Returns the y-coordiante of the player.
     * @return the y-coordinate
     */
    public int getPY()
    {
        return py;
    }

    /**
     * Returns the tile data. See {@link #data} for details.
     * @return the tile data
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Return the index of the first valid byte in {@link #data}.
     * @return the first index
     */
    public int getPos()
    {
        return pos;
    }
}
