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
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

/**
 * Represents a square in a {@link CfMap}. A square comprises of {@link
 * #LAYERS} faces as well as a darkness value.
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
     * Number of supported layers.
     */
    public static final int LAYERS = CrossfireServerConnection.NUM_LAYERS;

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
    private int darkness = 255;

    /**
     * The faces (of head-parts) of all layers as sent by the server.
     */
    private final Face[] faces = new Face[LAYERS];

    /**
     * If this square contains a non-head part of a multi-square object this
     * points to the head square.
     */
    private final CfMapSquare[] heads = new CfMapSquare[LAYERS];

    /**
     * Create a new (empty) square.
     *
     * @param x The absolute map x-coordinate of the top left corner of this
     * patch.
     *
     * @param y The absolute map y-coordinate of the top left corner of this
     * patch.
     */
    public CfMapSquare(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Return the absolute map x-coordinate of this square.
     *
     * @return The x-coordinate.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Return the absolute map y-coordinate of this square.
     *
     * @return The y-coordinate.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Mark this square as dirty, i.e., needing redraw.
     */
    public void dirty()
    {
        CfMapUpdater.addModifiedSquare(this);
    }

    /**
     * Mark this square as 'fog-og-war'. The values will be still returned
     * until a new value will be set.
     */
    public void clear()
    {
        // need to check individual values because the server sometimes sends a
        // "clear" command for already cleared squares; without this check the
        // black square would be displayed as fog-of-war
        if (fogOfWar)
        {
            return;
        }
        if (darkness == 255)
        {
            int layer;
            for (layer = 0; layer < faces.length; layer++)
            {
                if (faces[layer] != null || heads[layer] != null)
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
     * Set the darkness value of this square.
     *
     * @param darkness The new darkness value between <code>0</code> and
     * <code>255</code>. 0=dark, 255=full bright.
     *
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
     * Determine the darkness value of this square.
     *
     * @return The darkness value of the square. 0=dark, 255=full bright.
     */
    public int getDarkness()
    {
        return darkness;
    }

    /**
     * Set the face of a layer.
     *
     * @param layer The layer for the new face between <code>0</code> and
     * <code>LAYERS-1</code>.
     *
     * @param face The face to set.
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
     * Return the face of a layer.
     *
     * @param layer The layer to return the face.
     *
     * @return The face value.
     */
    public Face getFace(final int layer)
    {
        return faces[layer];
    }

    /**
     * Set the map square containing the head face for a layer.
     *
     * @param layer The layer for the new head face between <code>0</code> and
     * <code>LAYERS-1</code>.
     *
     * @param mapSquare The map square containing the head face; may be
     * <code>null</code>.
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
     * Return the map square of the head of a multi-square object.
     *
     * @param layer The layer to return the head for.
     *
     * @return The head map square, or <code>null</code> if this square does
     * not contain a multi-tail.
     */
    public CfMapSquare getHeadMapSquare(final int layer)
    {
        // suppress parts of fog-of-war objects if this square is not
        // fog-of-war
        if (heads[layer] != null && fogOfWar && heads[layer].isFogOfWar())
        {
            return null;
        }

        return heads[layer];
    }

    /**
     * Determine if the square is not up-to-date.
     *
     * @return Whether this square contains fog-of-war data.
     */
    public boolean isFogOfWar()
    {
        return fogOfWar;
    }

    /**
     * Return and reset the "fog-of-war" flag.
     *
     * @return Whether this square's fog-of-war state has been reset.
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
}
