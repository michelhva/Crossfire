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

/**
 * Interface for listeners interested in map related commands.
 * @author Andreas Kirschbaum
 */
public interface CrossfireUpdateMapListener
{
    /**
     * A "newmap" command has been received.
     * @param mapWidth the map width
     * @param mapHeight the map height
     */
    void newMap(int mapWidth, int mapHeight);

    /**
     * Parsing of a "map2" command has been started.
     */
    void mapBegin();

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
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param layer the layer
     * @param faceNum the face ID
     */
    void mapFace(int x, int y, int layer, int faceNum);

    /**
     * Part of "map2" parsing: set the animation of a cell.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param layer the layer
     * @param animationNum the animation ID
     * @param animationType the animation type
     */
    void mapAnimation(int x, int y, int layer, int animationNum, int animationType);

    /**
     * Part of "map2" parsing: set the animation speed.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param layer the layer
     * @param animSpeed the animation speed
     */
    void mapAnimationSpeed(int x, int y, int layer, int animSpeed);

    /**
     * Part of "map2" parsing: scroll the map view.
     * @param dx the x-distance
     * @param dy the y-distance
     */
    void scroll(int dx, int dy);

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
    void addAnimation(int animation, int flags, int[] faces);
}
