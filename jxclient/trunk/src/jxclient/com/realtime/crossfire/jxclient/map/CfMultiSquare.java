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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.map;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a square outside the viewable area that contains multi-square
 * objects.
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
    @NotNull
    private final CfMap map;

    /**
     * The face values.
     */
    @NotNull
    private final Face[] faces = new Face[CrossfireMap2Command.NUM_LAYERS];

    /**
     * Creates a new instance.
     * @param x the x-coordinate of this square
     * @param y the y-coordinate of this square
     * @param map the <code>CfMap</code> instance this square belongs to
     */
    public CfMultiSquare(final int x, final int y, @NotNull final CfMap map)
    {
        this.x = x;
        this.y = y;
        this.map = map;
    }

    /**
     * Clears all faces from this square.
     */
    public void clear()
    {
        for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++)
        {
            if (faces[layer] != null)
            {
                map.setFace(x, y, layer, null); // XXX: do not reset fog-of-war state
                faces[layer] = null;
            }
        }
    }

    /**
     * Sets a face to this square.
     * @param layer the layer to update
     * @param face the face to set; <code>null</code> to clear the face
     */
    public void setFace(final int layer, @NotNull final Face face)
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
