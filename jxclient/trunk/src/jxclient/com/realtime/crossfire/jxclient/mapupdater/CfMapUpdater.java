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
package com.realtime.crossfire.jxclient.mapupdater;

import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapAnimations;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to update a {@link CfMap} model from protocol commands.
 *
 * @author Andreas Kirschbaum
 */
public class CfMapUpdater
{
    /**
     * The width of the visible map area.
     */
    private int width = 0;

    /**
     * The height of the visible map area.
     */
    private int height = 0;

    /**
     * The current {@link CfMap} instance.
     */
    private CfMap map = new CfMap(this);

    /**
     * The listeners to notify about changed map squares.
     */
    private final List<CrossfireMapListener> mapListeners = new ArrayList<CrossfireMapListener>();

    /**
     * The listeners to notify about cleared maps.
     */
    private final List<CrossfireNewmapListener> newmapListeners = new ArrayList<CrossfireNewmapListener>();

    /**
     * The listeners to notify about scrolled maps.
     */
    private final List<CrossfireMapscrollListener> mapscrollListeners = new ArrayList<CrossfireMapscrollListener>();

    /**
     * Collects the changed map squares between calls to {@link
     * #processMapBegin()} and {@link #processMapEnd(boolean)}.
     */
    private final Set<CfMapSquare> squares = new HashSet<CfMapSquare>();

    /**
     * The animations in the visible map area.
     */
    private CfMapAnimations visibleAnimations = new CfMapAnimations(0, 0, this);

    /**
     * Creates a new instance.
     */
    public CfMapUpdater()
    {
    }

    /**
     * Add a listener to notify about changed map squares.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireMapListener(final CrossfireMapListener listener)
    {
        mapListeners.add(listener);
    }

    /**
     * Remove a listener to notify about changed map squares.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireMapListener(final CrossfireMapListener listener)
    {
        mapListeners.remove(listener);
    }

    /**
     * Add a listener to notify about cleared maps.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireNewmapListener(final CrossfireNewmapListener listener)
    {
        newmapListeners.add(listener);
    }

    /**
     * Remove a listener to notify about cleared maps.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireNewmapListener(final CrossfireNewmapListener listener)
    {
        newmapListeners.remove(listener);
    }

    /**
     * Add a listener to notify about scrolled maps.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireMapscrollListener(final CrossfireMapscrollListener listener)
    {
        mapscrollListeners.add(listener);
    }

    /**
     * Remove a listener to notify about scrolled maps.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireMapscrollListener(final CrossfireMapscrollListener listener)
    {
        mapscrollListeners.remove(listener);
    }

    /**
     * Reset the animation state.
     */
    public void reset()
    {
        visibleAnimations.clear();
    }

    /**
     * Start processing of a set of map square changes.
     */
    public void processMapBegin()
    {
        squares.clear();
    }

    /**
     * Update a map square by clearing it.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     */
    public void processMapClear(final int x, final int y)
    {
        visibleAnimations.remove(x, y);
        map.clearSquare(x, y);
    }

    /**
     * Update a map square by changing a face.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to update.
     *
     * @param face The face to set. <code>0</code> clears the square.
     *
     * @param faces The instance for looking up faces.
     */
    public void processMapFace(final int x, final int y, final int layer, final int face, final Faces faces)
    {
        final Face f;
        if (face == 0)
        {
            f = null;
            visibleAnimations.remove(x, y, layer);
        }
        else
        {
            f = faces.getFace(face);
        }

        map.setFace(x, y, layer, f);
    }

    /**
     * Update a map square by changing an animation.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to update.
     *
     * @param animation The animation to set.
     *
     * @param type The animation type.
     *
     * @param faces The instance for looking up faces.
     */
    public void processMapAnimation(final int x, final int y, final int layer, final Animation animation, final int type, final Faces faces)
    {
        visibleAnimations.add(x, y, layer, animation, type, faces);
    }

    /**
     * Update a map square by changing the animation speed.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param layer The layer to update.
     *
     * @param animationSpeed The animation speed to set.
     */
    public void processMapAnimationSpeed(final int x, final int y, final int layer, final int animationSpeed)
    {
        visibleAnimations.updateSpeed(x, y, layer, animationSpeed);
    }

    /**
     * Update all animation to the given tick number.
     *
     * @param tickno The tick number.
     */
    public void processTick(final int tickno)
    {
        visibleAnimations.tick(tickno);
    }

    /**
     * Update a map square by changing the darkness value.
     *
     * @param x The x-coordinate of the square.
     *
     * @param y The y-coordinate of the square.
     *
     * @param darkness The darkness value to set.
     */
    public void processMapDarkness(final int x, final int y, final int darkness)
    {
        map.setDarkness(x, y, darkness);
    }

    /**
     * Finish processing of a set of map square changes. Notifies listeners
     * about changes.
     *
     * @param alwaysProcess If set, notify listeners even if no changes are
     * present.
     */
    public void processMapEnd(final boolean alwaysProcess)
    {
        if (!alwaysProcess && squares.isEmpty())
        {
            return;
        }

        final CrossfireCommandMapEvent evt = new CrossfireCommandMapEvent(new Object(), map, squares);
        for (final CrossfireMapListener listener : mapListeners)
        {
            listener.commandMapReceived(evt);
        }
        squares.clear();
    }

    /**
     * Update the face of one map square layer <em>outside</code> the view
     * area.
     *
     * @param x The x-coordinate of the tile to update. The coordinate is
     * relative to the top left corner of the view area.
     *
     * @param y The y-coordinate of the tile to update. The coordinate is
     * relative to the top left corner of the view area.
     *
     * @param layer The layer to update.
     *
     * @param face The new face to set. <code>-1</code> means "do not change face".
     *
     * @param faces The instance for looking up faces.
     */
    private void setMultiFace(final int x, final int y, final int layer, final int face, final Faces faces)
    {
        if (face == -1)
        {
            return;
        }

        final Face f = face == 0 ? null : faces.getFace(face);
        map.setMultiFace(x, y, layer, f);
    }

    /**
     * Process a map scroll command.
     *
     * @param dx The distance to scroll in x-direction in squares.
     *
     * @param dy The distance to scroll in y-direction in squares.
     */
    public void processScroll(final int dx, final int dy)
    {
        map.clearMultiFaces();

        if (Math.abs(dx) >= width || Math.abs(dy) >= height)
        {
            map.scroll(dx, dy);
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    map.clearSquare(x, y);
                }
            }
            visibleAnimations.clear();
        }
        else
        {
            int tx = dx;
            while (tx > 0)
            {
                map.scroll(-1, 0);
                for (int y = 0; y < height; y++)
                {
                    map.clearSquare(width-1, y);
                }
                tx--;
            }
            while (tx < 0)
            {
                map.scroll(+1, 0);
                for (int y = 0; y < height; y++)
                {
                    map.clearSquare(0, y);
                }
                tx++;
            }

            int ty = dy;
            while (ty > 0)
            {
                map.scroll(0, -1);
                for (int x = 0; x < width; x++)
                {
                    map.clearSquare(x, height-1);
                }
                ty--;
            }
            while (ty < 0)
            {
                map.scroll(0, +1);
                for (int x = 0; x <= width; x++)
                {
                    map.clearSquare(x, 0);
                }
                ty++;
            }

            visibleAnimations.scroll(dx, dy);
        }

        final CrossfireCommandMapscrollEvent evt = new CrossfireCommandMapscrollEvent(new Object(), dx, dy);
        for (final CrossfireMapscrollListener listener : mapscrollListeners)
        {
            listener.commandMapscrollReceived(evt);
        }
    }

    /**
     * Process an updated face image.
     *
     * @param face The face that has changed.
     */
    public void updateFace(final int face)
    {
        processMapBegin();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                for (int z = 0; z < CrossfireMap2Command.NUM_LAYERS; z++)
                {
                    final Face f = map.getFace(x, y, z);
                    if (f != null && f.getID() == face)
                    {
                        map.setFace(x, y, z, f);
                        map.dirty(x, y);
                    }
                }
            }
        }

        processMapEnd(false);
    }

    /**
     * Process a newmap command. This clears the map state.
     * @param width the width of the visible map area
     * @param height the height of the visible map area
     */
    public void processNewmap(final int width, final int height)
    {
        this.width = width;
        this.height = height;
        map = new CfMap(this);

        // force dirty flags to be set for the visible map region
        map.clearSquare(0, 0);
        map.clearSquare(width-1, height-1);

        visibleAnimations = new CfMapAnimations(width, height, this);

        for (final CrossfireNewmapListener listener : newmapListeners)
        {
            listener.commandNewmapReceived();
        }
    }

    /**
     * Return the current map instance.
     *
     * @return The current map instance.
     */
    public CfMap getMap()
    {
        return map;
    }

    /**
     * Add a modified square to the current transaction.
     *
     * @param mapSquare The map square to add.
     */
    public void addModifiedSquare(final CfMapSquare mapSquare)
    {
        squares.add(mapSquare);
    }
}
