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
package com.realtime.crossfire.jxclient.map;

import com.realtime.crossfire.jxclient.faces.Face;

/**
 * Represents a square area of {@link CfMapSquare}s.
 * @author Andreas Kirschbaum
 */
public class CfMapPatch
{
    /**
     * Log2 of {@link #SIZE}.
     */
    public static final int SIZE_LOG = 5;

    /**
     * Size of patches in x- and y-direction.
     */
    public static final int SIZE = 1<<SIZE_LOG;

    /**
     * The array of {@link CfMapSquare}s. Elements are never <code>null</code>.
     */
    private final CfMapSquare[][] square = new CfMapSquare[SIZE][SIZE];

    /**
     * Creates a new (empty) patch.
     * @param mapSquareListener the map square listener to notify
     * @param x0 the absolute map x-coordinate of the top left corner of this
     * patch
     * @param y0 the absolute map y-coordinate of the top left corner of this
     * patch
     */
    public CfMapPatch(final CfMapSquareListener mapSquareListener, final int x0, final int y0)
    {
        for (int y = 0; y < SIZE; y++)
        {
            for (int x = 0; x < SIZE; x++)
            {
                square[x][y] = new CfMapSquare(mapSquareListener, x0+x, y0+y);
            }
        }
    }

    /**
     * Marks a square as dirty.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void dirty(final int x, final int y)
    {
        square[x][y].dirty();
    }

    /**
     * Clears the content of one square. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void clearSquare(final int x, final int y)
    {
        square[x][y].clear();
    }

    /**
     * Sets the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param darkness the darkness value to set; 0=dark, 255=full bright
     * @return whether fog-of-war has been cleared
     */
    public boolean setDarkness(final int x, final int y, final int darkness)
    {
        return square[x][y].setDarkness(darkness);
    }

    /**
     * Determines the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return the darkness value of the square; 0=dark, 255=full bright. Not
     * yet set faces return 0
     */
    public int getDarkness(final int x, final int y)
    {
        return square[x][y].getDarkness();
    }

    /**
     * Determines the face of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer of the face
     * @return the face; dark (i.e. not yet set) faces return
     * <code>null</code>
     */
    public Face getFace(final int x, final int y, final int layer)
    {
        return square[x][y].getFace(layer);
    }

    /**
     * Sets the map square containing the head face for a layer.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer for the new head face betweem <code>0</code> and
     * <code>LAYERS-1</code>
     * @param mapSquare the map square containing the head face; may be
     * <code>null</code>
     */
    public void setHeadMapSquare(final int x, final int y, final int layer, final CfMapSquare mapSquare)
    {
        square[x][y].setHeadMapSquare(layer, mapSquare);
    }

    /**
     * Returns the map square of the head of a multi-square object.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to return the head for
     * @return the head map square, or <code>null</code> if this square does
     * not contain a multi-tail
     */
    public CfMapSquare getHeadMapSquare(final int x, final int y, final int layer)
    {
        return square[x][y].getHeadMapSquare(layer);
    }

    /**
     * Determines if a square is not up-to-date.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return whether the square is up-to-date
     */
    public boolean isFogOfWar(final int x, final int y)
    {
        return square[x][y].isFogOfWar();
    }

    /**
     * Returns and reset the "fog-of-war" flag of a square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return whether this square's fog-of-war state has been reset
     */
    public boolean resetFogOfWar(final int x, final int y)
    {
        return square[x][y].resetFogOfWar();
    }

    /**
     * Returns one map square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return the map square
     */
    public CfMapSquare getSquare(final int x, final int y)
    {
        return square[x][y];
    }
}
