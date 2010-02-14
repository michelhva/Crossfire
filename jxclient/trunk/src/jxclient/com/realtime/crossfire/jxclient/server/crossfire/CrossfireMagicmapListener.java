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

package com.realtime.crossfire.jxclient.server.crossfire;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public interface CrossfireMagicmapListener
{
    /**
     * Bitmask to extract the color information of a tile.
     */
    int FACE_COLOR_MASK = 0x0F;

    /**
     * Bitmask to denote a floor tile.
     */
    int FACE_FLOOR = 0x80;

    /**
     * Bitmask to denote a wall tile.
     */
    int FACE_WALL = 0x40;

    /**
     * A magicmap protocol message has been received.
     * @param width the width of <code>data</code>
     * @param height the height of <code>data</code>
     * @param px the x-coordinate of the player
     * @param py the y-coordinate of the player
     * @param data the data describing tiles
     * @param pos the index of the first valid byte in <code>data</code>
     */
    void commandMagicmapReceived(int width, int height, int px, int py, @NotNull byte[] data, int pos);
}
