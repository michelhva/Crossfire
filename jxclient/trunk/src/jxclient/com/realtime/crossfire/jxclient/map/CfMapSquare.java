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
 * Represents a square in a {@link CfMap}. A square comprises of {@link
 * CrossfireMap2Command#NUM_LAYERS} faces as well as a darkness value.
 *
 * <p>This class assumes that the "head" part of a face is the part the server
 * did sent. This is the bottom-right part for multi-square objects. Not that
 * this definition is inconsistent to what the server assumes as the head part
 * of an object.
 *
 * @author Andreas Kirschbaum
 */
public class CfMapSquare
{
    /**
     * The default darkness value for newly creates squares.
     */
    public static final int DEFAULT_DARKNESS = 255;

    /**
     * The default face value for newly creates squares.
     */
    public static final Face DEFAULT_FACE = null;

    /**
     * The {@link CfMapSquareListener} instance to notify.
     */
    private final CfMapSquareListener mapSquareListener;

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
     * bright
     */
    private int darkness = DEFAULT_DARKNESS;

    /**
     * The faces (of head-parts) of all layers as sent by the server.
     */
    private final Face[] faces = new Face[CrossfireMap2Command.NUM_LAYERS];

    /**
     * If this square contains a non-head part of a multi-square object this
     * points to the head square.
     */
    private final CfMapSquare[] heads = new CfMapSquare[CrossfireMap2Command.NUM_LAYERS];

    /**
     * Creates a new (empty) square.
     * @param mapSquareListener the map square listener to notify
     * @param x the absolute map x-coordinate of the top left corner of this
     * patch
     * @param y the absolute map y-coordinate of the top left corner of this
     * patch
     */
    public CfMapSquare(final CfMapSquareListener mapSquareListener, final int x, final int y)
    {
        this.mapSquareListener = mapSquareListener;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the absolute map x-coordinate of this square.
     * @return the x-coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Returns the absolute map y-coordinate of this square.
     * @return the y-coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Marks this square as dirty, i.e., needing redraw.
     */
    public void dirty()
    {
        mapSquareListener.squareModified(this);
    }

    /**
     * Marks this square as 'fog-og-war'. The values will be still returned
     * until a new value will be set.
     */
    public void clear()
    {
        if (fogOfWar)
        {
            return;
        }

        // need to check individual values because the server sometimes sends a
        // "clear" command for already cleared squares; without this check the
        // black square would be displayed as fog-of-war
        if (darkness == DEFAULT_DARKNESS)
        {
            int layer;
            for (layer = 0; layer < faces.length; layer++)
            {
                if (faces[layer] != DEFAULT_FACE || heads[layer] != null)
                {
                    break;
                }
            }
            if (layer >= faces.length)
            {
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
    public boolean setDarkness(final int darkness)
    {
        final boolean result = fogOfWar;
        final boolean markDirty = fogOfWar || this.darkness != darkness;
        fogOfWar = false;
        this.darkness = darkness;
        if (markDirty)
        {
            dirty();
        }
        return result;
    }

    /**
     * Returns the darkness value of this square.
     * @return the darkness value of the square. 0=dark, 255=full bright
     */
    public int getDarkness()
    {
        return darkness;
    }

    /**
     * Sets the face of a layer.
     * @param layer the layer for the new face between <code>0</code> and
     * <code>LAYERS-1</code>
     * @param face the face to set
     */
    public void setFace(final int layer, final Face face)
    {
        if (faces[layer] != face)
        {
            faces[layer] = face;
            dirty();
        }
    }

    /**
     * Returns the face of a layer.
     * @param layer the layer to return the face
     * @return the face value
     */
    public Face getFace(final int layer)
    {
        return faces[layer];
    }

    /**
     * Sets the map square containing the head face for a layer.
     * @param layer the layer for the new head face between <code>0</code> and
     * <code>LAYERS-1</code>
     * @param mapSquare the map square containing the head face; may be
     * <code>null</code>
     */
    public void setHeadMapSquare(final int layer, final CfMapSquare mapSquare)
    {
        if (heads[layer] != mapSquare)
        {
            heads[layer] = mapSquare;
            dirty();
        }
    }

    /**
     * Returns the map square of the head of a multi-square object.
     * @param layer the layer to return the head for
     * @return the head map square, or <code>null</code> if this square does
     * not contain a multi-tail
     */
    public CfMapSquare getHeadMapSquare(final int layer)
    {
        // suppress parts of fog-of-war objects if this square is not
        // fog-of-war
        if (heads[layer] != null && !fogOfWar && heads[layer].fogOfWar)
        {
            return null;
        }

        return heads[layer];
    }

    /**
     * Determines if the square is not up-to-date.
     * @return whether this square contains fog-of-war data
     */
    public boolean isFogOfWar()
    {
        return fogOfWar;
    }

    /**
     * Returns and resets the "fog-of-war" flag.
     * @return whether this square's fog-of-war state has been reset
     */
    public boolean resetFogOfWar()
    {
        if (!fogOfWar)
        {
            return false;
        }

        fogOfWar = false;
        dirty();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return x+"/"+y;
    }
}
