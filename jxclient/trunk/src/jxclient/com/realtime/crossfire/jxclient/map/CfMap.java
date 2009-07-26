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
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;

/**
 * Represents a map (as seen by the client). A map is a grid in which {@link
 * CfMapSquare}s can be stored.
 *
 * <p>The map will be automatically enlarged by accesses to new squares. Not
 * yet set squares are considered dark.
 * @author Andreas Kirschbaum
 */
public class CfMap
{
    /**
     * The {@link CfMapSquareListener} instance to notify.
     */
    private final CfMapSquareListener mapSquareListener;

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
     * Result values for {@link #getMapPatch(int,int)} and {@link
     * #expandTo(int,int)}.
     */
    private int ox;

    /**
     * Result values for {@link #getMapPatch(int,int)} and {@link
     * #expandTo(int,int)}.
     */
    private int oy;

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
     * Creates a new (empty) map.
     * @param mapSquareListener the map square listener instance to notify
     */
    public CfMap(final CfMapSquareListener mapSquareListener)
    {
        this.mapSquareListener = mapSquareListener;
    }

    /**
     * Sets the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param darkness the darkness value to set; 0=dark, 255=full bright
     */
    public void setDarkness(final int x, final int y, final int darkness)
    {
        if (expandTo(x, y).setDarkness(ox, oy, darkness))
        {
            for (int l = 0; l < CrossfireMap2Command.NUM_LAYERS; l++)
            {
                setFaceInternal(x, y, l, CfMapSquare.DEFAULT_FACE);
            }
        }
    }

    /**
     * Returns the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return the darkness value of the square; 0=dark, 255=full bright; not
     * yet set faces return 0
     */
    public int getDarkness(final int x, final int y)
    {
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getDarkness(ox, oy) : CfMapSquare.DEFAULT_DARKNESS;
    }

    /**
     * Sets the face of one square. This function clears fog-of-war state if
     * necessary.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to set
     * @param face the face to set; may be <code>null</code> to remove the face
     */
    public void setFace(final int x, final int y, final int layer, final Face face)
    {
        if (expandTo(x, y).resetFogOfWar(ox, oy))
        {
            setDarkness(x, y, CfMapSquare.DEFAULT_DARKNESS);
            for (int l = 0; l < CrossfireMap2Command.NUM_LAYERS; l++)
            {
                setFaceInternal(x, y, l, l == layer ? face : CfMapSquare.DEFAULT_FACE);
            }
            dirty(x, y);
        }
        else
        {
            setFaceInternal(x, y, layer, face);
        }
    }

    /**
     * Sets the face of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to set
     * @param face the face to set; may be <code>null</code> to remove the face
     */
    private void setFaceInternal(final int x, final int y, final int layer, final Face face)
    {
        final CfMapSquare headMapSquare = expandTo(x, y).getSquare(ox, oy);

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
     * Adds or removes "head" pointer to/from tail-parts of a face.
     * @param x the x-coordinate of the tail part to expand
     * @param y the y-coordinate of the tail part to expand
     * @param layer the layer to expand
     * @param face the face to expand
     * @param oldMapSquare the map square of the tail part
     * @param newMapSquare the map square of the tail part to add pointers, or
     * <code>null</code> to remove pointers
     */
    private void expandFace(final int x, final int y, final int layer, final Face face, final CfMapSquare oldMapSquare, final CfMapSquare newMapSquare)
    {
        assert oldMapSquare != null;

        final int sx = face.getTileWidth();
        final int sy = face.getTileHeight();
        for (int dx = 0; dx < sx; dx++)
        {
            for (int dy = 0; dy < sy; dy++)
            {
                if (dx > 0 || dy > 0)
                {
                    if (newMapSquare != null)
                    {
                        setHeadMapSquare(x-dx, y-dy, layer, newMapSquare, true);
                    }
                    else if (getHeadMapSquare(x-dx, y-dy, layer) == oldMapSquare)
                    {
                        setHeadMapSquare(x-dx, y-dy, layer, null, true);
                    }
                }
            }
        }
    }

    /**
     * Marks one face as "dirty". This function is called when the head part
     * becomes a fog-of-war tile. This means the face has to be redrawn.
     * @param x the x-coordinate of the tail part of the face
     * @param y the y-coordinate of the tail part of the face
     * @param layer the layer of the face
     * @param face the face to mark dirty
     */
    private void dirtyFace(final int x, final int y, final int layer, final Face face)
    {
        final int sx = face.getTileWidth();
        final int sy = face.getTileHeight();
        for (int dx = 0; dx < sx; dx++)
        {
            for (int dy = 0; dy < sy; dy++)
            {
                if (dx > 0 || dy > 0)
                {
                    if (isFogOfWar(x-dx, y-dy))
                    {
                        dirty(x-dx, y-dy);
                    }
                    else
                    {
                        setHeadMapSquare(x-dx, y-dy, layer, null, false);
                    }
                }
            }
        }
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
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getFace(ox, oy, layer) : CfMapSquare.DEFAULT_FACE;
    }

    /**
     * Sets the map square containing the head face for a layer.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer for the new head face between <code>0</code> and
     * <code>LAYERS-1</code>
     * @param mapSquare the map square containing the head face; may be
     * <code>null</code>
     * @param setAlways if set, always update the face; if unset, only update
     * when fog-of-war
     */
    private void setHeadMapSquare(final int x, final int y, final int layer, final CfMapSquare mapSquare, final boolean setAlways)
    {
        expandTo(x, y).setHeadMapSquare(ox, oy, layer, mapSquare, setAlways);
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
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getHeadMapSquare(ox, oy, layer) : null;
    }

    /**
     * Clears the content of one square. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void clearSquare(final int x, final int y)
    {
        final CfMapPatch mapPatch = expandTo(x, y);
        mapPatch.clearSquare(ox, oy);
        for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++)
        {
            final Face face = mapPatch.getFace(ox, oy, layer);
            if (face != null)
            {
                dirtyFace(x, y, layer, face);
            }
        }
    }

    /**
     * Marks a single square as dirty.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void dirty(final int x, final int y)
    {
        expandTo(x, y).dirty(ox, oy);
    }

    /**
     * Determines if the tile is not up-to-date.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return whether the tile contains fog-of-war data
     */
    public boolean isFogOfWar(final int x, final int y)
    {
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null && mapPatch.isFogOfWar(ox, oy);
    }

    /**
     * Checks if a given position is within the defined map area.
     * <p>Returns additional information in {@link #ox} and {@link #oy}.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return the map patch or <code>null</code> if the coordinates are out of
     * map bounds
     */
    private CfMapPatch getMapPatch(final int x, final int y)
    {
        if (x < minX || x > maxX || y < minY || y > maxY)
        {
            return null;
        }

        final int px = ((x-patchX)>>CfMapPatch.SIZE_LOG)-minPx;
        final int py = ((y-patchY)>>CfMapPatch.SIZE_LOG)-minPy;
        assert px >= 0;
        assert py >= 0;
        assert px <= maxPx-minPx;
        assert py <= maxPy-minPy;
        ox = (x-patchX)&(CfMapPatch.SIZE-1);
        oy = (y-patchY)&(CfMapPatch.SIZE-1);
        assert ox >= 0;
        assert oy >= 0;
        assert ox < CfMapPatch.SIZE;
        assert oy < CfMapPatch.SIZE;

        final CfMapPatch mapPatch = patch[px][py];
        if (mapPatch != null)
        {
            return mapPatch;
        }

        patch[px][py] = new CfMapPatch(mapSquareListener, x-patchX-ox, y-patchY-oy);
        return patch[px][py];
    }

    /**
     * Scrolls the map.
     * @param dx the x-difference to scroll
     * @param dy the y-difference to scroll
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
     * (Possibly) expands the defined area of the map to a given position.
     * @param x the x-coordinate to expand the defined area to
     * @param y the y-coordinate to expand the defined area to
     * @return the map patch or <code>null</code> if the coordinates are out of
     * map bounds
     */
    private CfMapPatch expandTo(final int x, final int y)
    {
        if (minX > maxX || minY > maxY)
        {
            // current map is undefined ==> start with 1x1 map
            minX = x;
            maxX = x;
            minY = y;
            maxY = y;
            minPx = (x-patchX)>>CfMapPatch.SIZE_LOG;
            maxPx = minPx;
            minPy = (y-patchY)>>CfMapPatch.SIZE_LOG;
            maxPy = minPy;
            patch = new CfMapPatch[1][1];
            patch[0][0] = null;
        }
        else
        {
            if (x < minX)
            {
                increase(x-minX, 0);
            }
            if (x > maxX)
            {
                increase(x-maxX, 0);
            }
            if (y < minY)
            {
                increase(0, y-minY);
            }
            if (y > maxY)
            {
                increase(0, y-maxY);
            }
        }

        return getMapPatch(x, y);
    }

    /**
     * Increases the defined area of the map.
     * @param dx the increase in x-direction; dx&lt;0 means "expand (-dx) tiles
     * to the left", dx&gt;0 means "expand (dx) tiles to the right"
     * @param dy the increase in y-direction; dy&lt;0 means "expand (-dy) tiles
     * to the top", dy&gt;0 means "expand (dy) tiles to the bottom"
     */
    private void increase(final int dx, final int dy)
    {
        if (dx < 0)
        {
            final int newMinX = minX+dx;
            final int newMinPx = (newMinX-patchX)>>CfMapPatch.SIZE_LOG;
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

                if (diffPw <= 0)
                {
                    throw new AssertionError();
                }

                final int newPw = size(newMinPx, maxPx);
                final int newPh = size(minPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                if (newPw <= oldPw)
                {
                    throw new AssertionError();
                }
                if (newPw != oldPw+diffPw)
                {
                    throw new AssertionError();
                }
                if (newPh != oldPh)
                {
                    throw new AssertionError();
                }

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
            final int newMaxPx = (newMaxX-patchX)>>CfMapPatch.SIZE_LOG;
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

                if (diffPw <= 0)
                {
                    throw new AssertionError();
                }

                final int newPw = size(minPx, newMaxPx);
                final int newPh = size(minPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                if (newPw <= oldPw)
                {
                    throw new AssertionError();
                }
                if (newPw != oldPw+diffPw)
                {
                    throw new AssertionError();
                }
                if (newPh != oldPh)
                {
                    throw new AssertionError();
                }

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
            final int newMinPy = (newMinY-patchY)>>CfMapPatch.SIZE_LOG;
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

                if (diffPh <= 0)
                {
                    throw new AssertionError();
                }

                final int newPw = size(minPx, maxPx);
                final int newPh = size(newMinPy, maxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                if (newPh <= oldPh)
                {
                    throw new AssertionError();
                }
                if (newPh != oldPh+diffPh)
                {
                    throw new AssertionError();
                }
                if (newPw != oldPw)
                {
                    throw new AssertionError();
                }

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
            final int newMaxPy = (newMaxY-patchY)>>CfMapPatch.SIZE_LOG;
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

                if (diffPh <= 0)
                {
                    throw new AssertionError();
                }

                final int newPw = size(minPx, maxPx);
                final int newPh = size(minPy, newMaxPy);
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                if (newPh <= oldPh)
                {
                    throw new AssertionError();
                }
                if (newPh != oldPh+diffPh)
                {
                    throw new AssertionError();
                }
                if (newPw != oldPw)
                {
                    throw new AssertionError();
                }

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
     * Calculates the number of patches needed to hold tiles between two patch
     * coordinates.
     * @param min the minimum coordinate
     * @param max the maximum coordinate
     * @return the number of patches
     */
    private static int size(final int min, final int max)
    {
        return max-min+1;
    }

    /**
     * Returns a map square.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the map square
     */
    public CfMapSquare getMapSquare(final int x, final int y)
    {
        return expandTo(x, y).getSquare(ox, oy);
    }

    /**
     * Returns the offset to convert an absolute x-coordinate of a map square
     * ({@link CfMapSquare#getX()} to a relative x-coordinate.
     * @return the x offset
     */
    public int getOffsetX()
    {
        return patchX;
    }

    /**
     * Returns the offset to convert an absolute y-coordinate of a map square
     * ({@link CfMapSquare#getY()} to a relative y-coordinate.
     * @return the y offset
     */
    public int getOffsetY()
    {
        return patchY;
    }
}
