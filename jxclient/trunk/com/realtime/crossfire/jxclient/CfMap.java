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
package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.faces.Face;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Represents a map (as seen by the client). A map is a grid in which {@link
 * CfMapSquare}s can be stored.
 *
 * <p>The map will be automatically enlarged by accesses to new squares. Not
 * yet set squares are considered dark.
 *
 * @author Andreas Kirschbaum
 */
public class CfMap
{
    /**
     * The left edge of the defined tiles.
     */
    private int minX = 0;

    /**
     * The right edge of the defined tiles.
     */
    private int maxX = -1;

    /**
     * The top edge of the defined tiles.
     */
    private int minY = 0;

    /**
     * The bottom edge of the defined tiles.
     */
    private int maxY = -1;

    /**
     * The left edge of the defined patches in {@link #patch}.
     */
    private int minPx = 0;

    /**
     * The right edge of the defined patches in {@link #patch}.
     */
    private int maxPx = -1;

    /**
     * The top edge of the defined patches in {@link #patch}.
     */
    private int minPy = 0;

    /**
     * The bottom edge of the defined patches in {@link #patch}.
     */
    private int maxPy = -1;

    /**
     * Result values for {@link #isWithinMap(int,int)} and {@link
     * #expandTo(int,int)}.
     */
    private int px, py, ox, oy;

    /**
     * Left edge of viewable area.
     */
    private int patchX = 0;

    /**
     * Top edge of viewable area.
     */
    private int patchY = 0;

    /**
     * Array of (possibly) defined squares.
     */
    private CfMapPatch[][] patch = null;

    /**
     * Multi-face objects with bottom-right parts outside the viewable map area
     * but with at least one part inside the viewable area.
     */
    private Map<Point, CfMultiSquare> outOfMapMultiFaces = new HashMap<Point, CfMultiSquare>();

    /**
     * Create a new (empty) map.
     */
    public CfMap()
    {
    }

    /**
     * Forget about all multi-square faces in out-of-map bounds squares.
     */
    public void clearMultiFaces()
    {
        for (final CfMultiSquare multiSquare : outOfMapMultiFaces.values())
        {
            multiSquare.clear();
        }
        outOfMapMultiFaces.clear();
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
        if (darkness < 0 || darkness > 255) throw new IllegalArgumentException();

        expandTo(x, y);
        if (patch[px][py].resetFogOfWar(ox, oy))
        {
            patch[px][py].setDarkness(ox, oy, darkness);
            for (int l = 0; l < CfMapSquare.LAYERS; l++)
            {
                setFaceInternal(x, y, l, null);
            }
        }
        else
        {
            patch[px][py].setDarkness(ox, oy, darkness);
        }
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
        if (!isWithinMap(x, y))
        {
            return 0;
        }

        return patch[px][py].getDarkness(ox, oy);
    }

    /**
     * Set the face of one square. This function clears fog-of-war state if
     * necessary.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to set.
     *
     * @param face The face to set; may be <code>null</code> to remove the
     * face.
     */
    public void setFace(final int x, final int y, final int layer, final Face face)
    {
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        expandTo(x, y);
        if (patch[px][py].resetFogOfWar(ox, oy))
        {
            setDarkness(x, y, 255);
            for (int l = 0; l < CfMapSquare.LAYERS; l++)
            {
                setFaceInternal(x, y, l, l == layer ? face : null);
            }
            dirty(x, y);
        }
        else
        {
            setFaceInternal(x, y, layer, face);
        }
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
     * @param face The face to set; may be <code>null</code> to remove the
     * face.
     */
    private void setFaceInternal(final int x, final int y, final int layer, final Face face)
    {
        expandTo(x, y);
        final CfMapSquare headMapSquare = patch[px][py].getSquare(ox, oy);

        final Face oldFace = headMapSquare.getFace(layer);
        if (oldFace != null)
        {
            expandFace(x, y, layer, oldFace, headMapSquare, null);
        }
        headMapSquare.setFace(layer, face);
        if (face != null)
        {
            expandFace(x, y, layer, face, headMapSquare, headMapSquare);
        }
    }

    /**
     * Add or remove "head" pointer to/from tail-parts of a face.
     *
     * @param x The x-coordinate of the tail part to expand.
     *
     * @param y The y-coordinate of the tail part to expand.
     *
     * @param layer The layer to expand.
     *
     * @param face The face to expand.
     *
     * @param oldMapSquare The map square of the tail part.
     *
     * @param newMapSquare The map square of the tail part to add pointers, or
     * <code>null</code> to remove pointers.
     */
    private void expandFace(final int x, final int y, final int layer, final Face face, final CfMapSquare oldMapSquare, final CfMapSquare newMapSquare)
    {
        assert oldMapSquare != null;

        final ImageIcon img = face.getImageIcon(false);
        final int sx = (img.getIconWidth()+31)/32;
        final int sy = (img.getIconHeight()+31)/32;
        for (int dx = 0; dx < sx; dx++)
        {
            for (int dy = 0; dy < sy; dy++)
            {
                if (dx > 0 || dy > 0)
                {
                    if (newMapSquare != null)
                    {
                        setHeadMapSquare(x-dx, y-dy, layer, newMapSquare);
                    }
                    else if (getHeadMapSquare(x-dx, y-dy, layer) == oldMapSquare)
                    {
                        setHeadMapSquare(x-dx, y-dy, layer, null);
                    }
                }
            }
        }
    }

    /**
     * Mark one face as "dirty". This means the face has to be redrawn.
     *
     * @param x The x-coordinate of the tail part of the face.
     *
     * @param y The y-coordinate of the tail part of the face.
     *
     * @param face The face to mark dirty.
     */
    private void dirtyFace(final int x, final int y, final Face face)
    {
        final ImageIcon img = face.getImageIcon(false);
        final int sx = (img.getIconWidth()+31)/32;
        final int sy = (img.getIconHeight()+31)/32;
        for (int dx = 0; dx < sx; dx++)
        {
            for (int dy = 0; dy < sy; dy++)
            {
                if (dx > 0 || dy > 0)
                {
                    dirty(x-dx, y-dy);
                }
            }
        }
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
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        if (!isWithinMap(x, y))
        {
            return null;
        }

        return patch[px][py].getFace(ox, oy, layer);
    }

    /**
     * Set the map square containing the head face for a layer.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer for the new head face between <code>0</code> and
     * <code>LAYERS-1</code>.
     *
     * @param mapSquare The map square containing the head face; may be
     * <code>null</code>.
     */
    private void setHeadMapSquare(final int x, final int y, final int layer, final CfMapSquare mapSquare)
    {
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        expandTo(x, y);
        patch[px][py].setHeadMapSquare(ox, oy, layer, mapSquare);
    }

    /**
     * Return the map square of the head of a multi-square object.
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
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        if (!isWithinMap(x, y))
        {
            return null;
        }

        return patch[px][py].getHeadMapSquare(ox, oy, layer);
    }

    /**
     * Set the face of an out-of-map bounds multi-face object.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to set.
     *
     * @param face The face to set.
     */
    public void setMultiFace(final int x, final int y, final int layer, final Face face)
    {
        if (layer < 0 || layer >= CfMapSquare.LAYERS) throw new IllegalArgumentException();

        getOrCreateMultiSquare(x, y).setFace(layer, face);
    }

    /**
     * Return (or create if it doesn't yet exist) a {@link CfMultiSquare}
     * object for a given coordinate.
     *
     * @param x The x-coordinate.
     *
     * @param y The y-coordinate.
     *
     * @return The multi square object for the coordinate.
     */
    private CfMultiSquare getOrCreateMultiSquare(final int x, final int y)
    {
        final Point point = new Point(x, y);
        final CfMultiSquare multiSquare = outOfMapMultiFaces.get(point);
        if (multiSquare != null)
        {
            return multiSquare;
        }

        final CfMultiSquare newMultiSquare = new CfMultiSquare(x, y, this);
        outOfMapMultiFaces.put(point, newMultiSquare);
        return newMultiSquare;
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
        expandTo(x, y);
        patch[px][py].clearSquare(ox, oy);
        for (int layer = 0; layer < CfMapSquare.LAYERS; layer++)
        {
            final Face face = patch[px][py].getFace(ox, oy, layer);
            if (face != null)
            {
                dirtyFace(x, y, face);
            }
        }
    }

    /**
     * Clear the content of all squares. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     */
    public void clearAllSquares()
    {
        if (patch == null)
        {
            return;
        }

        if (patch.length == 0)
        {
            return;
        }

        for (int x = 0; x < patch.length; x++)
        {
            for (int y = 0; y < patch[0].length; y++)
            {
                if (patch[x][y] != null)
                {
                    patch[x][y].clearAllSquares();
                }
            }
        }
    }

    /**
     * Determine if a given square has been defined.
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
        return isWithinMap(x, y) && patch[px][py].isModified(ox, oy);
    }

    /**
     * Mark a single square as dirty.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     */
    public void dirty(final int x, final int y)
    {
        if (isWithinMap(x, y))
        {
            patch[px][py].dirty(ox, oy);
        }
    }

    /**
     * Return whether a given square is dirty and clear the dirty flag.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return Whether the square is dirty.
     */
    public boolean resetDirty(final int x, final int y)
    {
        return isWithinMap(x, y) && patch[px][py].resetDirty(ox, oy);
    }

    /**
     * Determine if the tile is not up-to-date.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @return Whether the tile contains fog-of-war data.
     */
    public boolean isFogOfWar(final int x, final int y)
    {
        return isWithinMap(x, y) && patch[px][py].isFogOfWar(ox, oy);
    }

    /**
     * Check if a given position is within the defined map area.
     *
     * <p>Returns additional information in {@link #px}, {@link #py}, {@link
     * #ox} and {@link #py}.
     *
     * @param x The x-coordinate to check.
     *
     * @param y The y-coordinate to check.
     *
     * @return <code>true</code> iff the given position is within the defined
     * map area.
     */
    public boolean isWithinMap(int x, int y)
    {
        if (x < minX || x > maxX || y < minY || y > maxY)
        {
            return false;
        }

        px = div(x-patchX, CfMapPatch.SIZE)-minPx;
        py = div(y-patchY, CfMapPatch.SIZE)-minPy;
        assert px >= 0;
        assert py >= 0;
        assert px <= maxPx-minPx;
        assert py <= maxPy-minPy;
        ox = mod(x-patchX, CfMapPatch.SIZE);
        oy = mod(y-patchY, CfMapPatch.SIZE);
        assert ox >= 0;
        assert oy >= 0;
        assert ox < CfMapPatch.SIZE;
        assert oy < CfMapPatch.SIZE;

        if (patch[px][py] == null)
        {
            patch[px][py] = new CfMapPatch(this, x-patchX-ox, y-patchY-oy);
        }

        return true;
    }

    /**
     * Scroll the map.
     *
     * @param dx The x-difference to scroll.
     *
     * @param dy The y-difference to scroll.
     */
    public void scroll(final int dx, final int dy)
    {
        if (dx == 0 && dy == 0)
        {
            return;
        }

        minX += dx;
        maxX += dx;
        minY += dy;
        maxY += dy;
        patchX += dx;
        patchY += dy;
    }

    /**
     * (Possibly) expand the defined area of the map to a given position.
     *
     * @param x The x-coordinate to expand the defined area to.
     *
     * @param y The y-coordinate to expand the defined area to.
     */
    private void expandTo(final int x, final int y)
    {
        if (minX > maxX || minY > maxY)
        {
            // current map is undefined ==> start with 1x1 map
            minX = maxX = x;
            minY = maxY = y;
            minPx = maxPx = div(x-patchX, CfMapPatch.SIZE);
            minPy = maxPy = div(y-patchY, CfMapPatch.SIZE);
            patch = new CfMapPatch[1][1];
            patch[0][0] = null;
        }
        else
        {
            if (x < minX) increase(x-minX, 0);
            if (x > maxX) increase(x-maxX, 0);
            if (y < minY) increase(0, y-minY);
            if (y > maxY) increase(0, y-maxY);
        }

        if (!isWithinMap(x, y))
        {
            throw new AssertionError();
        }
    }

    /**
     * Increase the defined area of the map.
     *
     * @param dx The increase in x-direction. dx<0 means "expand (-dx) tiles to
     * the left", dx>0 means "expand (dx) tiles to the right".
     *
     * @param dy The increase in y-direction. dy<0 means "expand (-dy) tiles to
     * the top", dy>0 means "expand (dy) tiles to the bottom".
     */
    private void increase(final int dx, final int dy)
    {
        if (patch == null) throw new IllegalArgumentException();

        if (dx < 0)
        {
            final int newMinX = minX+dx;
            final int newMinPx = div(newMinX-patchX, CfMapPatch.SIZE);
            final int diffPw = minPx-newMinPx;
            if (diffPw == 0)
            {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                minX = newMinX;
            }
            else
            {
                // need to add (diffPw) patches to the left

                if (diffPw <= 0) throw new AssertionError();

                final int newPw = size(newMinPx, maxPx);
                final int newPh = size(minPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                if (newPw <= oldPw) throw new AssertionError();
                if (newPw != oldPw+diffPw) throw new AssertionError();
                if (newPh != oldPh) throw new AssertionError();

                final CfMapPatch[][] newPatch = new CfMapPatch[newPw][newPh];
                for (int y = 0; y < oldPh; y++)
                {
                    for (int x = 0; x < diffPw; x++)
                    {
                        newPatch[x][y] = null;
                    }

                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x+diffPw][y] = patch[x][y];
                    }
                }

                minX = newMinX;
                minPx = newMinPx;
                patch = newPatch;
            }
        }
        else if (dx > 0)
        {
            final int newMaxX = maxX+dx;
            final int newMaxPx = div(newMaxX-patchX, CfMapPatch.SIZE);
            final int diffPw = newMaxPx-maxPx;
            if (diffPw == 0)
            {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                maxX = newMaxX;
            }
            else
            {
                // need to add (diffPw) patches to the right

                if (diffPw <= 0) throw new AssertionError();

                final int newPw = size(minPx, newMaxPx);
                final int newPh = size(minPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                if (newPw <= oldPw) throw new AssertionError();
                if (newPw != oldPw+diffPw) throw new AssertionError();
                if (newPh != oldPh) throw new AssertionError();

                final CfMapPatch[][] newPatch = new CfMapPatch[newPw][newPh];
                for (int y = 0; y < oldPh; y++)
                {
                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x][y] = patch[x][y];
                    }

                    for (int x = 0; x < diffPw; x++)
                    {
                        newPatch[x+oldPw][y] = null;
                    }
                }

                maxX = newMaxX;
                maxPx = newMaxPx;
                patch = newPatch;
            }
        }

        if (dy < 0)
        {
            final int newMinY = minY+dy;
            final int newMinPy = div(newMinY-patchY, CfMapPatch.SIZE);
            final int diffPh = minPy-newMinPy;
            if (diffPh == 0)
            {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                minY = newMinY;
            }
            else
            {
                // need to add (diffPh) patches to the top

                if (diffPh <= 0) throw new AssertionError();

                final int newPw = size(minPx, maxPx);
                final int newPh = size(newMinPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                if (newPh <= oldPh) throw new AssertionError();
                if (newPh != oldPh+diffPh) throw new AssertionError();
                if (newPw != oldPw) throw new AssertionError();

                final CfMapPatch[][] newPatch = new CfMapPatch[newPw][newPh];
                for (int y = 0; y < diffPh; y++)
                {
                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x][y] = null;
                    }
                }
                for (int y = 0; y < oldPh; y++)
                {
                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x][y+diffPh] = patch[x][y];
                    }
                }

                minY = newMinY;
                minPy = newMinPy;
                patch = newPatch;
            }
        }
        else if (dy > 0)
        {
            final int newMaxY = maxY+dy;
            final int newMaxPy = div(newMaxY-patchY, CfMapPatch.SIZE);
            final int diffPh = newMaxPy-maxPy;
            if (diffPh == 0)
            {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                maxY = newMaxY;
            }
            else
            {
                // need to add (diffPh) patches to the bottom

                if (diffPh <= 0) throw new AssertionError();

                final int newPw = size(minPx, maxPx);
                final int newPh = size(minPy, newMaxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                if (newPh <= oldPh) throw new AssertionError();
                if (newPh != oldPh+diffPh) throw new AssertionError();
                if (newPw != oldPw) throw new AssertionError();

                final CfMapPatch[][] newPatch = new CfMapPatch[newPw][newPh];
                for (int y = 0; y < oldPh; y++)
                {
                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x][y] = patch[x][y];
                    }
                }
                for (int y = 0; y < diffPh; y++)
                {
                    for (int x = 0; x < oldPw; x++)
                    {
                        newPatch[x][y+oldPh] = null;
                    }
                }

                maxY = newMaxY;
                maxPy = newMaxPy;
                patch = newPatch;
            }
        }
    }

    /**
     * Calculate the number of patches needed to hold tiles between two patch
     * coordinates.
     *
     * @param min The minimum coordinate.
     *
     * @param max The maximum coordinate.
     */
    private static int size(final int min, final int max)
    {
        return max-min+1;
    }

    /**
     * Calculate <code>x/y</code> but always round towards negative infinitiy.
     *
     * @param x The numerator.
     *
     * @param y the denominator.
     */
    private static int div(final int x, final int y)
    {
        if (y <= 0) throw new IllegalArgumentException();

        if (x >= 0)
        {
            return x/y;
        }

        return -((-x+y-1)/y);
    }

    /**
     * Calculate <code>x%y</code> but alway round towards negative infinity.
     *
     * @param x The numerator.
     *
     * @param y the denominator.
     */
    private static int mod(final int x, final int y)
    {
        if (y <= 0) throw new IllegalArgumentException();

        if (x >= 0)
        {
            return x%y;
        }

        return x-div(x, y)*y;
    }

    /**
     * Return a map square.
     *
     * @param x The x-coordinate.
     *
     * @param y The y-coordinate.
     *
     * @return The map square.
     */
    public CfMapSquare getMapSquare(final int x, final int y)
    {
        expandTo(x, y);
        return patch[px][py].getSquare(ox, oy);
    }
}
