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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map (as seen by the client). A map is a grid in which {@link
 * CfMapSquare CfMapSquares} can be stored.
 * <p/>
 * The map will be automatically enlarged by accesses to new squares. Not yet
 * set squares are considered dark.
 * <p/>
 * All accesses must be synchronized on the map instance.
 * @author Andreas Kirschbaum
 */
public class CfMap {

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
     * Result values for {@link #getMapPatch(int, int)} and {@link
     * #expandTo(int, int)}.
     */
    private int ox;

    /**
     * Result values for {@link #getMapPatch(int, int)} and {@link
     * #expandTo(int, int)}.
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
    @Nullable
    private CfMapPatch[][] patch = null;

    /**
     * The "dirty" map squares that have been modified.
     */
    @NotNull
    private final Set<CfMapSquare> dirtyMapSquares = new HashSet<CfMapSquare>();

    /**
     * The map squares containing pending faces. Maps face number to map squares
     * that need to be repainted when the face becomes available.
     */
    @NotNull
    private final Map<Integer, Collection<CfMapSquare>> pendingFaceSquares = new HashMap<Integer, Collection<CfMapSquare>>();

    /**
     * Clears the map contents.
     * @param mapWidth the width of the visible map area
     * @param mapHeight the height of the visible map area
     */
    public void reset(final int mapWidth, final int mapHeight) {
        minX = 0;
        maxX = -1;
        minY = 0;
        maxY = -1;
        minPx = 0;
        maxPx = -1;
        minPy = 0;
        maxPy = -1;
        patchX = 0;
        patchY = 0;
        patch = null;
        dirtyMapSquares.clear();
        pendingFaceSquares.clear();

        // force dirty flags to be set for the visible map region
        clearSquare(0, 0);
        clearSquare(mapWidth-1, mapHeight-1);
    }

    /**
     * Sets the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param darkness the darkness value to set; 0=dark, 255=full bright
     */
    public void setDarkness(final int x, final int y, final int darkness) {
        assert Thread.holdsLock(this);
        if (expandTo(x, y).setDarkness(ox, oy, darkness)) {
            for (int l = 0; l < Map2.NUM_LAYERS; l++) {
                setFaceInternal(x, y, l, CfMapSquare.DEFAULT_FACE);
            }
        }
    }

    /**
     * Returns the darkness value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return the darkness value of the square; 0=dark, 255=full bright; not
     *         yet set faces return 0
     */
    public int getDarkness(final int x, final int y) {
        assert Thread.holdsLock(this);
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getDarkness(ox, oy) : CfMapSquare.DEFAULT_DARKNESS;
    }

    /**
     * Sets the smooth value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to set
     * @param smooth the smooth value to set
     */
    public void setSmooth(final int x, final int y, final int layer, final int smooth) {
        final int result = expandTo(x, y).setSmooth(ox, oy, layer, smooth);
        if ((result&1) != 0) {
            for (int l = 0; l < Map2.NUM_LAYERS; l++) {
                setFaceInternal(x, y, l, CfMapSquare.DEFAULT_FACE);
            }
        }
        if ((result&2) != 0) {
            for (int dx = -1; dx <= +1; dx++) {
                for (int dy = -1; dy <= +1; dy++) {
                    squareModified(getMapSquare(x+dx, y+dy));
                }
            }
        }
    }

    /**
     * Returns the smooth value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer of the square
     * @return the smooth value of the square
     */
    public int getSmooth(final int x, final int y, final int layer) {
        final CfMapPatch mapPatch = getMapPatch(x, y);
        final int result = mapPatch != null ? mapPatch.getSmooth(ox, oy, layer) : CfMapSquare.DEFAULT_SMOOTH;
        return result;
    }

    /**
     * Sets the magic map color of one square.
     * @param x0 the x-coordinate of the square
     * @param y0 the y-coordinate of the square
     * @param data the magic map data (y, x); will not be changed
     */
    public void setMagicMap(final int x0, final int y0, final byte[][] data) {
        assert Thread.holdsLock(this);
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                final int color = data[y][x]&CrossfireUpdateMapListener.FACE_COLOR_MASK;
                if (expandTo(x0+x, y0+y).setColor(ox, oy, color)) {
                    for (int l = 0; l < Map2.NUM_LAYERS; l++) {
                        setFaceInternal(x, y, l, CfMapSquare.DEFAULT_FACE);
                    }
                }
            }
        }
    }

    /**
     * Returns the magic map color value of one square.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return the color
     */
    public int getColor(final int x, final int y) {
        assert Thread.holdsLock(this);
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getColor(ox, oy) : CfMapSquare.DEFAULT_COLOR;
    }

    /**
     * Sets the face of one square. This function clears fog-of-war state if
     * necessary.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to set
     * @param face the face to set; may be <code>null</code> to remove the face
     */
    public void setFace(final int x, final int y, final int layer, @Nullable final Face face) {
        assert Thread.holdsLock(this);
        if (expandTo(x, y).resetFogOfWar(ox, oy)) {
            setDarkness(x, y, CfMapSquare.DEFAULT_DARKNESS);
            for (int l = 0; l < Map2.NUM_LAYERS; l++) {
                setFaceInternal(x, y, l, l == layer ? face : CfMapSquare.DEFAULT_FACE);
            }
            dirty(x, y);
        } else {
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
    private void setFaceInternal(final int x, final int y, final int layer, @Nullable final Face face) {
        final CfMapSquare headMapSquare = expandTo(x, y).getSquare(ox, oy);

        final Face oldFace = headMapSquare.getFace(layer);
        if (oldFace != null) {
            expandFace(x, y, layer, oldFace, headMapSquare, null);
        }
        headMapSquare.setFace(layer, face);
        if (face != null) {
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
    private void expandFace(final int x, final int y, final int layer, @NotNull final Face face, @NotNull final CfMapSquare oldMapSquare, @Nullable final CfMapSquare newMapSquare) {
        final int sx = face.getTileWidth();
        final int sy = face.getTileHeight();
        for (int dx = 0; dx < sx; dx++) {
            for (int dy = 0; dy < sy; dy++) {
                if (dx > 0 || dy > 0) {
                    if (newMapSquare != null) {
                        setHeadMapSquare(x-dx, y-dy, layer, newMapSquare, true);
                    } else if (getHeadMapSquare(x-dx, y-dy, layer) == oldMapSquare) {
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
    private void dirtyFace(final int x, final int y, final int layer, @NotNull final Face face) {
        final int sx = face.getTileWidth();
        final int sy = face.getTileHeight();
        for (int dx = 0; dx < sx; dx++) {
            for (int dy = 0; dy < sy; dy++) {
                if (dx > 0 || dy > 0) {
                    if (isFogOfWar(x-dx, y-dy)) {
                        dirty(x-dx, y-dy);
                    } else {
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
     * @return the face; dark (i.e. not yet set) faces return <code>null</code>
     */
    @Nullable
    public Face getFace(final int x, final int y, final int layer) {
        assert Thread.holdsLock(this);
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
    private void setHeadMapSquare(final int x, final int y, final int layer, @Nullable final CfMapSquare mapSquare, final boolean setAlways) {
        expandTo(x, y).setHeadMapSquare(ox, oy, layer, mapSquare, setAlways);
    }

    /**
     * Returns the map square of the head of a multi-square object.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to return the head for
     * @return the head map square, or <code>null</code> if this square does not
     *         contain a multi-tail
     */
    @Nullable
    public CfMapSquare getHeadMapSquare(final int x, final int y, final int layer) {
        assert Thread.holdsLock(this);
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null ? mapPatch.getHeadMapSquare(ox, oy, layer) : null;
    }

    /**
     * Clears the content of one square. Note: the old square content remains
     * available until at least one value will be changed ("fog of war").
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void clearSquare(final int x, final int y) {
        assert Thread.holdsLock(this);
        final CfMapPatch mapPatch = expandTo(x, y);
        mapPatch.clearSquare(ox, oy);
        for (int layer = 0; layer < Map2.NUM_LAYERS; layer++) {
            final Face face = mapPatch.getFace(ox, oy, layer);
            if (face != null) {
                dirtyFace(x, y, layer, face);
            }
        }
    }

    /**
     * Marks a single square as dirty.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void dirty(final int x, final int y) {
        assert Thread.holdsLock(this);
        expandTo(x, y).dirty(ox, oy);
    }

    /**
     * Determines if the tile is not up-to-date.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @return whether the tile contains fog-of-war data
     */
    public boolean isFogOfWar(final int x, final int y) {
        assert Thread.holdsLock(this);
        final CfMapPatch mapPatch = getMapPatch(x, y);
        return mapPatch != null && mapPatch.isFogOfWar(ox, oy);
    }

    /**
     * Checks if a given position is within the defined map area. <p>Returns
     * additional information in {@link #ox} and {@link #oy}.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return the map patch or <code>null</code> if the coordinates are out of
     *         map bounds
     */
    @Nullable
    private CfMapPatch getMapPatch(final int x, final int y) {
        if (x < minX || x > maxX || y < minY || y > maxY) {
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

        assert patch != null;
        assert patch[px] != null;
        final CfMapPatch mapPatch = patch[px][py];
        if (mapPatch != null) {
            return mapPatch;
        }

        patch[px][py] = new CfMapPatch(this, x-patchX-ox, y-patchY-oy);
        assert patch != null;
        assert patch[px] != null;
        return patch[px][py];
    }

    /**
     * Scrolls the map.
     * @param dx the x-difference to scroll
     * @param dy the y-difference to scroll
     */
    private void scroll(final int dx, final int dy) {
        assert Thread.holdsLock(this);
        if (dx == 0 && dy == 0) {
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
     *         map bounds
     */
    @NotNull
    private CfMapPatch expandTo(final int x, final int y) {
        if (minX > maxX || minY > maxY) {
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
            //noinspection AssignmentToNull
            patch[0][0] = null;
        } else {
            if (x < minX) {
                increase(x-minX, 0);
            }
            if (x > maxX) {
                increase(x-maxX, 0);
            }
            if (y < minY) {
                increase(0, y-minY);
            }
            if (y > maxY) {
                increase(0, y-maxY);
            }
        }

        final CfMapPatch result = getMapPatch(x, y);
        assert result != null;
        return result;
    }

    /**
     * Increases the defined area of the map.
     * @param dx the increase in x-direction; dx&lt;0 means "expand (-dx) tiles
     * to the left", dx&gt;0 means "expand (dx) tiles to the right"
     * @param dy the increase in y-direction; dy&lt;0 means "expand (-dy) tiles
     * to the top", dy&gt;0 means "expand (dy) tiles to the bottom"
     */
    private void increase(final int dx, final int dy) {
        if (dx < 0) {
            final int newMinX = minX+dx;
            final int newMinPx = (newMinX-patchX)>>CfMapPatch.SIZE_LOG;
            final int diffPw = minPx-newMinPx;
            if (diffPw == 0) {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                minX = newMinX;
            } else {
                // need to add (diffPw) patches to the left

                assert diffPw > 0;

                final int newPw = size(newMinPx, maxPx);
                final int newPh = size(minPy, maxPy);
                assert patch != null;
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                assert newPw > oldPw;
                assert newPw == oldPw+diffPw;
                assert newPh == oldPh;

                minX = newMinX;
                minPx = newMinPx;
                patch = copyPatches(newPw, newPh, diffPw, 0, oldPw, oldPh);
            }
        } else if (dx > 0) {
            final int newMaxX = maxX+dx;
            final int newMaxPx = (newMaxX-patchX)>>CfMapPatch.SIZE_LOG;
            final int diffPw = newMaxPx-maxPx;
            if (diffPw == 0) {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                maxX = newMaxX;
            } else {
                // need to add (diffPw) patches to the right

                assert diffPw > 0;

                final int newPw = size(minPx, newMaxPx);
                final int newPh = size(minPy, maxPy);
                assert patch != null;
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new width must be more than old size
                assert newPw > oldPw;
                assert newPw == oldPw+diffPw;
                assert newPh == oldPh;

                maxX = newMaxX;
                maxPx = newMaxPx;
                patch = copyPatches(newPw, newPh, 0, 0, oldPw, oldPh);
            }
        }

        if (dy < 0) {
            final int newMinY = minY+dy;
            final int newMinPy = (newMinY-patchY)>>CfMapPatch.SIZE_LOG;
            final int diffPh = minPy-newMinPy;
            if (diffPh == 0) {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                minY = newMinY;
            } else {
                // need to add (diffPh) patches to the top

                assert diffPh > 0;

                final int newPw = size(minPx, maxPx);
                final int newPh = size(newMinPy, maxPy);
                assert patch != null;
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                assert newPh > oldPh;
                assert newPh == oldPh+diffPh;
                assert newPw == oldPw;

                minY = newMinY;
                minPy = newMinPy;
                patch = copyPatches(newPw, newPh, 0, diffPh, oldPw, oldPh);
            }
        } else if (dy > 0) {
            final int newMaxY = maxY+dy;
            final int newMaxPy = (newMaxY-patchY)>>CfMapPatch.SIZE_LOG;
            final int diffPh = newMaxPy-maxPy;
            if (diffPh == 0) {
                // new size fits within current patch ==> no change to
                // <code>patch</code>
                maxY = newMaxY;
            } else {
                // need to add (diffPh) patches to the bottom

                assert diffPh > 0;

                final int newPw = size(minPx, maxPx);
                final int newPh = size(minPy, newMaxPy);
                assert patch != null;
                final int oldPw = patch.length;
                final int oldPh = patch[0].length;

                // new height must be more than old size
                assert newPh > oldPh;
                assert newPh == oldPh+diffPh;
                assert newPw == oldPw;

                maxY = newMaxY;
                maxPy = newMaxPy;
                patch = copyPatches(newPw, newPh, 0, 0, oldPw, oldPh);
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
    private static int size(final int min, final int max) {
        return max-min+1;
    }

    /**
     * Returns a map square.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the map square
     */
    @NotNull
    public CfMapSquare getMapSquare(final int x, final int y) {
        assert Thread.holdsLock(this);
        return expandTo(x, y).getSquare(ox, oy);
    }

    /**
     * Returns a map square.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the map square or <code>null</code> if it would be dirty
     */
    @Nullable
    public CfMapSquare getMapSquareUnlessDirty(final int x, final int y) {
        assert Thread.holdsLock(this);
        final CfMapSquare mapSquare = getMapSquare(x, y);
        return dirtyMapSquares.contains(mapSquare) ? null : mapSquare;
    }

    /**
     * Returns the offset to convert an absolute x-coordinate of a map square
     * ({@link CfMapSquare#getX()} to a relative x-coordinate.
     * @return the x offset
     */
    public int getOffsetX() {
        assert Thread.holdsLock(this);
        return patchX;
    }

    /**
     * Returns the offset to convert an absolute y-coordinate of a map square
     * ({@link CfMapSquare#getY()} to a relative y-coordinate.
     * @return the y offset
     */
    public int getOffsetY() {
        assert Thread.holdsLock(this);
        return patchY;
    }

    /**
     * Returns a copy of a rectangular area of {@link #patch}.
     * @param newWidth the width of the new area
     * @param newHeight the height of the new area
     * @param offsetX the x-offset into the new area
     * @param offsetY the y-offset into the new area
     * @param height the height of the area to copy
     * @param width the width of the area to copy
     * @return the copy
     */
    @NotNull
    private CfMapPatch[][] copyPatches(final int newWidth, final int newHeight, final int offsetX, final int offsetY, final int height, final int width) {
        assert patch != null;
        final CfMapPatch[][] newPatch = new CfMapPatch[newWidth][newHeight];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                newPatch[offsetX+x][offsetY+y] = patch[x][y];
            }
        }
        return newPatch;
    }

    /**
     * Marks a {@link CfMapSquare} as dirty.
     * @param mapSquare the map square
     */
    public void squareModified(@NotNull final CfMapSquare mapSquare) {
        assert Thread.holdsLock(this);
        dirtyMapSquares.add(mapSquare);
    }

    /**
     * Marks a {@link CfMapSquare} as containing a pending face.
     * @param x the x coordinate of the map square
     * @param y the y coordinate of the map square
     * @param faceNum the pending face
     */
    public void squarePendingFace(final int x, final int y, final int faceNum) {
        assert Thread.holdsLock(this);
        final Integer tmpFaceNum = faceNum;
        Collection<CfMapSquare> mapSquares = pendingFaceSquares.get(tmpFaceNum);
        if (mapSquares == null) {
            mapSquares = new HashSet<CfMapSquare>();
            pendingFaceSquares.put(tmpFaceNum, mapSquares);
        }
        mapSquares.add(getMapSquare(x, y));
    }

    /**
     * Returns the dirty map squares. The result may be modified by the caller.
     * @return the dirty map squares
     */
    @NotNull
    public Set<CfMapSquare> getDirtyMapSquares() {
        assert Thread.holdsLock(this);
        final Set<CfMapSquare> result = new HashSet<CfMapSquare>(dirtyMapSquares);
        dirtyMapSquares.clear();
        return result;
    }

    /**
     * Processes an updated face image.
     * @param faceNum the face that has changed
     * @param width the width of the visible map area
     * @param height the height of the visible map area
     */
    public void updateFace(final int faceNum, final int width, final int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int layer = 0; layer < Map2.NUM_LAYERS; layer++) {
                    final Face face = getFace(x, y, layer);
                    if (face != null && face.getFaceNum() == faceNum) {
                        setFace(x, y, layer, face);
                        dirty(x, y);
                    }
                }
            }
        }

        final Collection<CfMapSquare> mapSquares = pendingFaceSquares.remove(faceNum);
        if (mapSquares != null) {
            dirtyMapSquares.addAll(mapSquares);
        }
    }

    /**
     * Processes a map scroll command.
     * @param dx the distance to scroll in x-direction in squares
     * @param dy the distance to scroll in y-direction in squares
     * @param width the width of the visible map area in map squares
     * @param height the height of the visible map area in map squares
     * @return whether scrolling did clear the whole visible map area
     */
    public boolean processMapScroll(final int dx, final int dy, final int width, final int height) {
        if (Math.abs(dx) >= width || Math.abs(dy) >= height) {
            scroll(dx, dy);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    clearSquare(x, y);
                }
            }

            return true;
        }

        int tx = dx;
        while (tx > 0) {
            scroll(-1, 0);
            for (int y = 0; y < height; y++) {
                clearSquare(-1, y);
                clearSquare(width-1, y);
            }
            tx--;
        }
        while (tx < 0) {
            scroll(+1, 0);
            for (int y = 0; y < height; y++) {
                clearSquare(0, y);
                clearSquare(width, y);
            }
            tx++;
        }

        int ty = dy;
        while (ty > 0) {
            scroll(0, -1);
            for (int x = 0; x < width; x++) {
                clearSquare(x, -1);
                clearSquare(x, height-1);
            }
            ty--;
        }
        while (ty < 0) {
            scroll(0, +1);
            for (int x = 0; x <= width; x++) {
                clearSquare(x, 0);
                clearSquare(x, height);
            }
            ty++;
        }

        return false;
    }

}
