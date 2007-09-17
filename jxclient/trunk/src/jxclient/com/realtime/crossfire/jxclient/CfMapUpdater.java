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
import com.realtime.crossfire.jxclient.faces.Faces;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapAnimations;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to update a {@link CfMap} model from protocol commands.
 *
 * @author Andreas Kirschbaum
 */
public class CfMapUpdater
{
    /**
     * The current {@link CfMap} instance.
     */
    private static CfMap map = new CfMap();

    /**
     * The listeners to notify about changed map squares.
     */
    private static final List<CrossfireMapListener> mylistenersMap = new ArrayList<CrossfireMapListener>();

    /**
     * The listeners to notify about cleared maps.
     */
    private static final List<CrossfireNewmapListener> mylistenersNewmap = new ArrayList<CrossfireNewmapListener>();

    /**
     * The listeners to notify about scrolled maps.
     */
    private static final List<CrossfireMapscrollListener> mylistenersMapscroll = new ArrayList<CrossfireMapscrollListener>();

    /**
     * Collects the changed map squares between calls to {@link
     * #processMapBegin()} and {@link #processMapEnd(boolean)}.
     */
    private static final List<CfMapSquare> squares = new LinkedList<CfMapSquare>();

    /**
     * The animations in the visible map area.
     */
    private static final CfMapAnimations visibleAnimations = new CfMapAnimations(CrossfireServerConnection.MAP_WIDTH, CrossfireServerConnection.MAP_HEIGHT);

    /**
     * Private constructor to prevent instantiation.
     */
    private CfMapUpdater()
    {
    }

    /**
     * Add a listener to notify about changed map squares.
     *
     * @param listener The listener to add.
     */
    public static void addCrossfireMapListener(final CrossfireMapListener listener)
    {
        mylistenersMap.add(listener);
    }

    /**
     * Remove a listener to notify about changed map squares.
     *
     * @param listener The listener to remove.
     */
    public static void removeCrossfireMapListener(final CrossfireMapListener listener)
    {
        mylistenersMap.remove(listener);
    }

    /**
     * Add a listener to notify about cleared maps.
     *
     * @param listener The listener to add.
     */
    public static void addCrossfireNewmapListener(final CrossfireNewmapListener listener)
    {
        mylistenersNewmap.add(listener);
    }

    /**
     * Remove a listener to notify about cleared maps.
     *
     * @param listener The listener to remove.
     */
    public static void removeCrossfireNewmapListener(final CrossfireNewmapListener listener)
    {
        mylistenersNewmap.remove(listener);
    }

    /**
     * Add a listener to notify about scrolled maps.
     *
     * @param listener The listener to add.
     */
    public static void addCrossfireMapscrollListener(final CrossfireMapscrollListener listener)
    {
        mylistenersMapscroll.add(listener);
    }

    /**
     * Remove a listener to notify about scrolled maps.
     *
     * @param listener The listener to remove.
     */
    public static void removeCrossfireMapscrollListener(final CrossfireMapscrollListener listener)
    {
        mylistenersMapscroll.remove(listener);
    }

    /**
     * Start processing of a set of map square changes.
     */
    public static void processMapBegin()
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
    public static void processMapClear(final int x, final int y)
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
     */
    public static void processMapFace(final int x, final int y, final int layer, final int face)
    {
        final Face f;
        if (face == 0)
        {
            f = null;
            visibleAnimations.remove(x, y, layer);
        }
        else
        {
            f = Faces.getFace(face);
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
     */
    public static void processMapAnimation(final int x, final int y, final int layer, final Animation animation, final int type)
    {
        visibleAnimations.add(x, y, layer, animation, type);
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
    public static void processMapAnimationSpeed(final int x, final int y, final int layer, final int animationSpeed)
    {
        visibleAnimations.updateSpeed(x, y, layer, animationSpeed);
    }

    /**
     * Update all animation to the given tick number.
     *
     * @param tickno The tick number.
     */
    public static void processTick(final int tickno)
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
    public static void processMapDarkness(final int x, final int y, final int darkness)
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
    public static void processMapEnd(final boolean alwaysProcess)
    {
        if (!alwaysProcess && squares.isEmpty())
        {
            return;
        }

        final CrossfireCommandMapEvent evt = new CrossfireCommandMapEvent(new Object(), squares);
        for (final CrossfireMapListener listener : mylistenersMap)
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
     */
    private static void setMultiFace(final int x, final int y, final int layer, final int face)
    {
        if (face == -1)
        {
            return;
        }

        final Face f = face == 0 ? null : Faces.getFace(face);
        map.setMultiFace(x, y, layer, f);
    }

    /**
     * Process a map scroll command.
     *
     * @param dx The distance to scroll in x-direction in squares.
     *
     * @param dy The distance to scroll in y-direction in squares.
     */
    public static void processScroll(final int dx, final int dy)
    {
        map.clearMultiFaces();

        if (Math.abs(dx) >= CrossfireServerConnection.MAP_WIDTH || Math.abs(dy) >= CrossfireServerConnection.MAP_HEIGHT)
        {
            map.scroll(dx, dy);
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
            {
                for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH; x++)
                {
                    map.clearSquare(x, y);
                    map.dirty(x, y);
                }
            }
            visibleAnimations.clear();
            /* XXX: does not call callbacks */
            return;
        }

        int tx = dx;
        while (tx > 0)
        {
            map.scroll(-1, 0);
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
            {
                map.clearSquare(CrossfireServerConnection.MAP_WIDTH-1, y);
                map.dirty(CrossfireServerConnection.MAP_WIDTH-1, y);
            }
            tx--;
        }
        while (tx < 0)
        {
            map.scroll(+1, 0);
            for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
            {
                map.clearSquare(0, y);
                map.dirty(0, y);
            }
            tx++;
        }

        int ty = dy;
        while (ty > 0)
        {
            map.scroll(0, -1);
            for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH; x++)
            {
                map.clearSquare(x, CrossfireServerConnection.MAP_HEIGHT-1);
                map.dirty(x, CrossfireServerConnection.MAP_HEIGHT-1);
            }
            ty--;
        }
        while (ty < 0)
        {
            map.scroll(0, +1);
            for (int x = 0; x <= CrossfireServerConnection.MAP_WIDTH; x++)
            {
                map.clearSquare(x, 0);
                map.dirty(x, 0);
            }
            ty++;
        }

        visibleAnimations.scroll(dx, dy);

        final CrossfireCommandMapscrollEvent evt = new CrossfireCommandMapscrollEvent(new Object(), dx, dy);
        for (final CrossfireMapscrollListener listener : mylistenersMapscroll)
        {
            listener.commandMapscrollReceived(evt);
        }
    }

    /**
     * Process an updated face image.
     *
     * @param face The face that has changed.
     */
    public static void updateFace(final int face)
    {
        final List<CfMapSquare> l = new LinkedList<CfMapSquare>();
        for (int y = 0; y < CrossfireServerConnection.MAP_HEIGHT; y++)
        {
            for (int x = 0; x < CrossfireServerConnection.MAP_WIDTH; x++)
            {
                for (int z = 0; z < CrossfireServerConnection.NUM_LAYERS; z++)
                {
                    final Face f = map.getFace(x, y, z);
                    if (f != null && f.getID() == face)
                    {
                        map.dirty(x, y);
                        //l.add(map[x][y]);
                    }
                }
            }
        }
        final CrossfireCommandMapEvent evt = new CrossfireCommandMapEvent(new Object(), l);
        for (final CrossfireMapListener listener : mylistenersMap)
        {
            listener.commandMapReceived(evt);
        }
    }

    /**
     * Process a newmap command. This clears the map state.
     */
    public static void processNewmap()
    {
        map = new CfMap();

        // force dirty flags to be set for the visible map region
        map.clearSquare(0, 0);
        map.clearSquare(CrossfireServerConnection.MAP_WIDTH-1, CrossfireServerConnection.MAP_HEIGHT-1);

        visibleAnimations.clear();

        final CrossfireCommandNewmapEvent evt = new CrossfireCommandNewmapEvent(new Object());
        for (final CrossfireNewmapListener listener : mylistenersNewmap)
        {
            listener.commandNewmapReceived(evt);
        }
    }

    /**
     * Return the current map instance.
     *
     * @return The current map instance.
     */
    public static CfMap getMap()
    {
        return map;
    }

    /**
     * Add a modified square to the current transaction.
     *
     * @param mapSquare The map square to add.
     */
    public static void addModifiedSquare(final CfMapSquare mapSquare)
    {
        squares.add(mapSquare);
    }
}
