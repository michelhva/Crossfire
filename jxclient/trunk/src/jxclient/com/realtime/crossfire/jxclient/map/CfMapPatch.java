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
 *
 * @author Andreas Kirschbaum
 */
public class CfMapPatch
{
    /**
     * Size of patchs in x- and y-direction.
     */
    public static int SIZE = 25;

    /**
     * The array of {@link CfMapSquare}s. Elements are never <code>null</code>.
     */
    private final CfMapSquare[][] square = new CfMapSquare[SIZE][SIZE];

    /**
     * Create a new (empty) patch.
     *
     * @param map The <code>CfMap</code> instance this patch belongs to.
     *
     * @param x0 The absolute map x-coordinate of the top left corner of this
     * patch.
     *
     * @param y0 The absolute map y-coordinate of the top left corner of this
     * patch.
     */
    public CfMapPatch(final CfMap map, final int x0, final int y0)
    {
        for (int y = 0; y < SIZE; y++)
        {
            for (int x = 0; x < SIZE; x++)
            {
                square[x][y] = new CfMapSquare(map, x0+x, y0+y);
            }
        }
    }

    /**
     * Determine if a given square has been modified.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return <code>true</code> iff at least one square value (face or
     * darkness) has been set.
     */
    public boolean isModified(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].isModified();
    }

    /**
     * Mark a square as dirty.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     */
    public void dirty(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        square[x][y].dirty();
    }

    /**
     * Return whether a square is dirty and clear the dirty flag.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return Whether the square is dirty.
     */
    public boolean resetDirty(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].resetDirty();
    }

    /**
     * Clear the content of one square. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     */
    public void clearSquare(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        square[x][y].clear();
    }

    /**
     * Clear the content of all squares. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     */
    public void clearAllSquares()
    {
        for (int x = 0; x < SIZE; x++)
        {
            for (int y = 0; y < SIZE; y++)
            {
                square[x][y].clear();
            }
        }
    }

    /**
     * Set the darkness value of one square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param darkness The darkness value to set. 0=dark, 255=full bright.
     */
    public void setDarkness(final int x, final int y, final int darkness)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        square[x][y].setDarkness(darkness);
    }

    /**
     * Determine the darkness value of one square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return The darkness value of the square. 0=dark, 255=full bright. Not
     * yet set faces return 0.
     */
    public int getDarkness(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].getDarkness();
    }

    /**
     * Set the face of one square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to set.
     *
     * @param face The face to set.
     */
    public void setFace(final int x, final int y, final int layer, final Face face)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        square[x][y].setFace(layer, face);
    }

    /**
     * Determine the face of one square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer of the face.
     *
     * @return The face; dark (i.e. not yet set) faces return
     * <code>null</code>.
     */
    public Face getFace(final int x, final int y, final int layer)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].getFace(layer);
    }

    /**
     * Sets the map square containing the head face for a layer.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer for the new head face betweem <code>0</code> and
     * <code>LAYERS-1</code>.
     *
     * @param mapSquare The map square containing the head face; may be
     * <code>null</code>.
     */
    public void setHeadMapSquare(final int x, final int y, final int layer, final CfMapSquare mapSquare)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        square[x][y].setHeadMapSquare(layer, mapSquare);
    }

    /**
     * Returns the map square of the head of a multi-square object.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to return the head for.
     *
     * @return The head map square, or <code>null</code> if this square does
     * not contain a multi-tail.
     */
    public CfMapSquare getHeadMapSquare(final int x, final int y, final int layer)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        return square[x][y].getHeadMapSquare(layer);
    }

    /**
     * Determine if a square is not up-to-date.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return Whether the square is up-to-date.
     */
    public boolean isFogOfWar(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].isFogOfWar();
    }

    /**
     * Return and reset the "fog-of-war" flag of a square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return Whether this square's fog-of-war state has been reset.
     */
    public boolean resetFogOfWar(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y].resetFogOfWar();
    }

    /**
     * Return one map square.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return The map square.
     */
    public CfMapSquare getSquare(final int x, final int y)
    {
        if (x < 0 || x >= SIZE) throw new IllegalArgumentException();
        if (y < 0 || y >= SIZE) throw new IllegalArgumentException();

        return square[x][y];
    }
}
