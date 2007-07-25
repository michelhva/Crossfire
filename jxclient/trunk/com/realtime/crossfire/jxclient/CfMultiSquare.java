/* $Id$ */

package com.realtime.crossfire.jxclient;

import com.realtime.crossfire.jxclient.faces.Face;

/**
 * Represents a square outside the viewable area that contains multi-square
 * objects.
 *
 * @author Andreas Kirschbaum
 */
public class CfMultiSquare
{
    /**
     * The x-coordinate of this square.
     */
    private final int x;

    /**
     * The y-coordinate of this square.
     */
    private final int y;

    /**
     * The {@link CfMap} instance this square belongs to.
     */
    private final CfMap map;

    /**
     * The face values.
     */
    private final Face[] faces = new Face[CfMapSquare.LAYERS];

    /**
     * Create a new instance.
     *
     * @param x The x-coordinate of this square.
     *
     * @param y The y-coordinate of this square.
     *
     * @param map The <code>CfMap</code> instance this square belongs to.
     */
    public CfMultiSquare(final int x, final int y, final CfMap map)
    {
        this.x = x;
        this.y = y;
        this.map = map;
    }

    /**
     * Clear all faces from this square.
     */
    public void clear()
    {
        for (int layer = 0; layer < CfMapSquare.LAYERS; layer++)
        {
            if (faces[layer] != null)
            {
                map.setFace(x, y, layer, null); // XXX: do not reset fog-of-war state
                faces[layer] = null;
            }
        }
    }

    /**
     * Set a face to this square.
     *
     * @param layer The layer to update.
     *
     * @param face The face to set; <code>null</code> to clear the face.
     */
    public void setFace(final int layer, final Face face)
    {
        if (faces[layer] != null)
        {
            map.setFace(x, y, layer, null); // XXX: do not reset fog-of-war state
        }
        faces[layer] = face;
        if (faces[layer] != null)
        {
            map.setFace(x, y, layer, faces[layer]); // XXX: do not reset fog-of-war state
        }
    }
}
