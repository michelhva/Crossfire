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
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a square in a {@link CfMap}. A square comprises of {@link
 * Map2#NUM_LAYERS} faces as well as a darkness value.
 * <p/>
 * This class assumes that the "head" part of a face is the part the server did
 * sent. This is the bottom-right part for multi-square objects. Not that this
 * definition is inconsistent to what the server assumes as the head part of an
 * object.
 * @author Andreas Kirschbaum
 */
public class CfMapSquare {

    /**
     * The default darkness value for newly created squares.
     */
    public static final int DEFAULT_DARKNESS = 255;

    /**
     * The default smooth value for newly created squares.
     */
    public static final int DEFAULT_SMOOTH = 0;

    /**
     * The default magic map color for newly created squares.
     */
    public static final int DEFAULT_COLOR = -1;

    /**
     * The darkness value for a full bright square.
     */
    public static final int DARKNESS_FULL_BRIGHT = 255;

    /**
     * The default face value for newly creates squares.
     */
    @Nullable
    public static final Face DEFAULT_FACE = null;

    /**
     * The {@link CfMap} this map square is part of.
     */
    @NotNull
    private final CfMap map;

    /**
     * The absolute x-coordinate of this square in its {@link CfMap}.
     */
    private final int x;

    /**
     * The absolute y-coordinate of this square in its {@link CfMap}.
     */
    private final int y;

    /**
     * Flag used to defer clearing the values: when a <code>CfMapSquare</code>
     * is cleared, the old values remain valid until at least one field is
     * re-set.
     */
    private boolean fogOfWar = false;

    /**
     * The darkness value of the square in the range [0..255]. 0=dark, 255=full
     * bright={@link #DARKNESS_FULL_BRIGHT}.
     */
    private int darkness = DEFAULT_DARKNESS;

    /**
     * The magic map color of the square. Set to {@link #DEFAULT_COLOR} if none
     * is known.
     */
    private int color = DEFAULT_COLOR;

    /**
     * The faces (of head-parts) of all layers as sent by the server.
     */
    @NotNull
    private final Face[] faces = new Face[Map2.NUM_LAYERS];

    /**
     * If this square contains a non-head part of a multi-square object this
     * points to the head square.
     */
    @NotNull
    private final CfMapSquare[] heads = new CfMapSquare[Map2.NUM_LAYERS];

    /**
     * The smooth values of all layers as sent by the server.
     */
    @NotNull
    private final int[] smooths = new int[Map2.NUM_LAYERS];

    /**
     * Creates a new (empty) square.
     * @param map the map this map square is part of
     * @param x the absolute map x-coordinate of the top left corner of this
     * patch
     * @param y the absolute map y-coordinate of the top left corner of this
     * patch
     */
    public CfMapSquare(@NotNull final CfMap map, final int x, final int y) {
        this.map = map;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the absolute map x-coordinate of this square.
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the absolute map y-coordinate of this square.
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Marks this square as dirty, i.e., needing redraw.
     */
    public void dirty() {
        map.squareModified(this);
    }

    /**
     * Marks this square as 'fog-og-war'. The values will be still returned
     * until a new value will be set.
     */
    public void clear() {
        if (fogOfWar) {
            return;
        }

        // need to check individual values because the server sometimes sends a
        // "clear" command for already cleared squares; without this check the
        // black square would be displayed as fog-of-war
        if (darkness == DEFAULT_DARKNESS) {
            int layer;
            for (layer = 0; layer < faces.length; layer++) {
                if (faces[layer] != DEFAULT_FACE || heads[layer] != null || smooths[layer] != 0) {
                    break;
                }
            }
            if (layer >= faces.length) {
                return;
            }
        }

        fogOfWar = true;
        dirty();
    }

    /**
     * Sets the darkness value of this square.
     * @param darkness the new darkness value between <code>0</code> and
     * <code>255</code>; 0=dark, 255=full bright
     * @return whether fog-of-war has been cleared
     */
    public boolean setDarkness(final int darkness) {
        final boolean result = fogOfWar;
        final boolean markDirty = fogOfWar || this.darkness != darkness;
        fogOfWar = false;
        this.darkness = darkness;
        if (markDirty) {
            dirty();
        }
        return result;
    }

    /**
     * Returns the darkness value of this square.
     * @return the darkness value of the square; 0=dark, 255=full bright
     */
    public int getDarkness() {
        return darkness;
    }

    /**
     * Sets the smooth value of this square.
     * @param layer the layer between <code>0</code> and <code>LAYERS-1</code>
     * @param smooth the new smooth value
     * @return whether fog-of-war has been cleared (1) or whether the smooth
     *         value has changed (2)
     */
    public int setSmooth(final int layer, final int smooth) {
        final boolean fogOfWarCleared = fogOfWar;
        final boolean smoothChanged = smooths[layer] != smooth;
        smooths[layer] = smooth;
        final boolean markDirty = fogOfWar || smoothChanged;
        fogOfWar = false;
        if (markDirty) {
            dirty();
        }
        return (fogOfWarCleared ? 1 : 0)|(smoothChanged ? 2 : 0);
    }

    /**
     * Returns the smooth value of this square.
     * @param layer the layer between <code>0</code> and <code>LAYERS-1</code>
     * @return the smooth value of the square
     */
    public int getSmooth(final int layer) {
        return smooths[layer];
    }

    /**
     * Sets the magic map color of this square.
     * @param color the new color
     * @return whether fog-of-war has been cleared
     */
    public boolean setColor(final int color) {
        final boolean result = fogOfWar;
        final boolean markDirty = fogOfWar || this.color != color;
        fogOfWar = false;
        this.color = color;
        if (markDirty) {
            dirty();
        }
        return result;
    }

    /**
     * Returns the magic map color of this square.
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the face of a layer.
     * @param layer the layer for the new face between <code>0</code> and
     * <code>LAYERS-1</code>
     * @param face the face to set
     */
    public void setFace(final int layer, @Nullable final Face face) {
        if (faces[layer] != face) {
            faces[layer] = face;
            dirty();
        }
    }

    /**
     * Returns the face of a layer.
     * @param layer the layer to return the face
     * @return the face value
     */
    @Nullable
    public Face getFace(final int layer) {
        return faces[layer];
    }

    /**
     * Sets the map square containing the head face for a layer.
     * @param layer the layer for the new head face between <code>0</code> and
     * <code>LAYERS-1</code>
     * @param mapSquare the map square containing the head face; may be
     * <code>null</code>
     * @param setAlways if set, always update the face; if unset, only update
     * when fog-of-war
     */
    public void setHeadMapSquare(final int layer, @Nullable final CfMapSquare mapSquare, final boolean setAlways) {
        if (heads[layer] != mapSquare && (setAlways || heads[layer] == null || heads[layer].isFogOfWar())) {
            heads[layer] = mapSquare;
            dirty();
        }
    }

    /**
     * Returns the map square of the head of a multi-square object.
     * @param layer the layer to return the head for
     * @return the head map square, or <code>null</code> if this square does not
     *         contain a multi-tail
     */
    @Nullable
    public CfMapSquare getHeadMapSquare(final int layer) {
        // suppress parts of fog-of-war objects if this square is not
        // fog-of-war
        if (heads[layer] != null && !fogOfWar && heads[layer].fogOfWar) {
            return null;
        }

        return heads[layer];
    }

    /**
     * Determines if the square is not up-to-date.
     * @return whether this square contains fog-of-war data
     */
    public boolean isFogOfWar() {
        return fogOfWar;
    }

    /**
     * Returns and resets the "fog-of-war" flag.
     * @return whether this square's fog-of-war state has been reset
     */
    public boolean resetFogOfWar() {
        if (!fogOfWar) {
            return false;
        }

        fogOfWar = false;
        dirty();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return x+"/"+y;
    }

}
