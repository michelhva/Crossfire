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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.map.Location;
import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in map related commands.
 * @author Andreas Kirschbaum
 */
public interface CrossfireUpdateMapListener extends EventListener {

    /**
     * Bitmask in magic map information to extract the color information of a
     * tile.
     */
    int FACE_COLOR_MASK = 0x0F;

    /**
     * Bitmask in magic map information to denote a floor tile.
     */
    int FACE_FLOOR = 0x80;

    /**
     * Bitmask in magic map information to denote a wall tile.
     */
    int FACE_WALL = 0x40;

    /**
     * A "newmap" command has been received.
     * @param mapWidth the map width
     * @param mapHeight the map height
     */
    void newMap(int mapWidth, int mapHeight);

    /**
     * Parsing of a "map2" command has been started.
     * @return the synchronization object which must be <code>synchronized</code>
     *         while calling any other function (except <code>newMap()</code>)
     */
    @NotNull
    Object mapBegin();

    /**
     * Part of "map2" parsing: clear a cell.
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    void mapClear(int x, int y);

    /**
     * Part of "map2" parsing: change the darkness of a cell.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param darkness the darkness value
     */
    void mapDarkness(int x, int y, int darkness);

    /**
     * Part of "map2" parsing: set the face of a cell.
     * @param location the location
     * @param faceNum the face ID
     */
    void mapFace(@NotNull Location location, int faceNum);

    /**
     * Part of "map2" parsing: set the animation of a cell.
     * @param location the location
     * @param animationNum the animation ID
     * @param animationType the animation type
     */
    void mapAnimation(@NotNull Location location, int animationNum, int animationType);

    /**
     * Part of "map2" parsing: set the animation speed.
     * @param location the location
     * @param animationSpeed the animation speed
     */
    void mapAnimationSpeed(@NotNull Location location, int animationSpeed);

    /**
     * Part of "map2" parsing: set the smooth level.
     * @param location the location
     * @param smooth the smooth value
     */
    void mapSmooth(@NotNull Location location, int smooth);

    /**
     * Part of "map2" parsing: scroll the map view.
     * @param dx the x-distance
     * @param dy the y-distance
     */
    void mapScroll(int dx, int dy);

    /**
     * Part of "magicmap" parsing: set the magic map color.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param data the magic map data (y, x); must not be changed
     */
    void magicMap(int x, int y, byte[][] data);

    /**
     * Parsing of "map2" has been finished.
     */
    void mapEnd();

    /**
     * An "addanim" command has been received.
     * @param animation the animation ID
     * @param flags the animation flags
     * @param faces the faces list; must not be modified
     */
    void addAnimation(int animation, int flags, @NotNull int[] faces);

}
