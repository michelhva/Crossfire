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
import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapAnimations;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.CfMapSquareListener;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateMapListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to update a {@link CfMap} model from protocol commands.
 * <p>
 * The map updater is called from two threads: the {@link 
 * com.realtime.crossfire.jxclient.server.ClientSocket} reading commands
 * received from the Crossfire server, and {@link
 * com.realtime.crossfire.jxclient.faces.FileCacheFaceQueue} reading faces from
 * the (file) cache. Synchronization is by {@link #sync} and applies to the
 * whole map model {@link #map}.
 * @author Andreas Kirschbaum
 */
public class CfMapUpdater
{
    /**
     * The object used for synchronization.
     */
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} to track for updated faces.
     */
    private final FacesManager facesManager;

    /**
     * The {@link FaceCache} instance for looking up faces.
     */
    private final FaceCache faceCache;

    /**
     * The defined animations.
     */
    private final Animations animations;

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
    private CfMap map;

    /**
     * The listeners to notify about changed map squares.
     */
    private final List<MapListener> mapListeners = new ArrayList<MapListener>();

    /**
     * The listeners to notify about cleared maps.
     */
    private final List<NewmapListener> newmapListeners = new ArrayList<NewmapListener>();

    /**
     * The listeners to notify about scrolled maps.
     */
    private final List<MapscrollListener> mapscrollListeners = new ArrayList<MapscrollListener>();

    /**
     * Collects the changed map squares between calls to {@link
     * #processMapBegin()} and {@link #processMapEnd(boolean)}.
     */
    private final Set<CfMapSquare> squares = new HashSet<CfMapSquare>();

    /**
     * The animations in the visible map area.
     */
    private final CfMapAnimations visibleAnimations;

    /**
     * The map square listener attached to {@link #map}.
     */
    private final CfMapSquareListener mapSquareListener = new CfMapSquareListener()
    {
        /** {@inheritDoc} */
        public void squareModified(final CfMapSquare mapSquare)
        {
            synchronized (squares)
            {
                squares.add(mapSquare);
            }
        }
    };

    /**
     * The listener to detect updated faces.
     */
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        public void faceUpdated(final Face face)
        {
            updateFace(face.getFaceNum());
        }
    };

    /**
     * The listener to detect map model changes.
     */
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener()
    {
        /** {@inheritDoc} */
        public void newMap(final int mapWidth, final int mapHeight)
        {
            processNewMap(mapWidth, mapHeight);
        }

        /** {@inheritDoc} */
        public void mapBegin()
        {
            processMapBegin();
        }

        /** {@inheritDoc} */
        public void mapClear(final int x, final int y)
        {
            processMapClear(x, y);
        }

        /** {@inheritDoc} */
        public void mapDarkness(final int x, final int y, final int darkness)
        {
            processMapDarkness(x, y, darkness);
        }

        /** {@inheritDoc} */
        public void mapFace(final int x, final int y, final int layer, final int faceNum)
        {
            processMapFace(x, y, layer, faceNum);
        }

        /** {@inheritDoc} */
        public void mapAnimation(final int x, final int y, final int layer, final int animationNum, final int animationType)
        {
            final Animation animation = animations.get(animationNum);
            if (animation == null)
            {
                System.err.println("unknown animation id "+animationNum+", ignoring");
                return;
            }
            processMapAnimation(x, y, layer, animation, animationType);
        }

        /** {@inheritDoc} */
        public void mapAnimationSpeed(final int x, final int y, final int layer, final int animSpeed)
        {
            processMapAnimationSpeed(x, y, layer, animSpeed);
        }

        /** {@inheritDoc} */
        public void scroll(final int dx, final int dy)
        {
            processScroll(dx, dy);
        }

        /** {@inheritDoc} */
        public void mapEnd()
        {
            processMapEnd(true);
        }

        /** {@inheritDoc} */
        public void addAnimation(final int animation, final int flags, final int[] faces)
        {
            animations.addAnimation(animation, flags, faces);
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager to track for updated faces
     * @param faceCache the instance for looking up faces
     * @param animations the defined animations
     */
    public CfMapUpdater(final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final FaceCache faceCache, final Animations animations)
    {
        this.facesManager = facesManager;
        this.faceCache = faceCache;
        this.animations = animations;
        if (crossfireServerConnection != null)
        {
            crossfireServerConnection.addCrossfireUpdateMapListener(crossfireUpdateMapListener);
        }
        if (facesManager != null)
        {
            facesManager.addFacesManagerListener(facesManagerListener);
        }
        map = new CfMap(mapSquareListener);
        visibleAnimations = new CfMapAnimations(crossfireServerConnection, this);
    }

    /**
     * Add a listener to notify about changed map squares.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireMapListener(final MapListener listener)
    {
        mapListeners.add(listener);
    }

    /**
     * Remove a listener to notify about changed map squares.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireMapListener(final MapListener listener)
    {
        mapListeners.remove(listener);
    }

    /**
     * Add a listener to notify about cleared maps.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireNewmapListener(final NewmapListener listener)
    {
        newmapListeners.add(listener);
    }

    /**
     * Remove a listener to notify about cleared maps.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireNewmapListener(final NewmapListener listener)
    {
        newmapListeners.remove(listener);
    }

    /**
     * Add a listener to notify about scrolled maps.
     *
     * @param listener The listener to add.
     */
    public void addCrossfireMapscrollListener(final MapscrollListener listener)
    {
        mapscrollListeners.add(listener);
    }

    /**
     * Remove a listener to notify about scrolled maps.
     *
     * @param listener The listener to remove.
     */
    public void removeCrossfireMapscrollListener(final MapscrollListener listener)
    {
        mapscrollListeners.remove(listener);
    }

    /**
     * Reset the animation state.
     */
    public void reset()
    {
        synchronized (sync)
        {
            visibleAnimations.clear();
        }
    }

    /**
     * Start processing of a set of map square changes.
     */
    public void processMapBegin()
    {
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
        synchronized (sync)
        {
            visibleAnimations.remove(x, y);
            map.clearSquare(x, y);
        }
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
     * @param faceNum The face to set. <code>0</code> clears the square.
     */
    public void processMapFace(final int x, final int y, final int layer, final int faceNum)
    {
        synchronized (sync)
        {
            if (faceNum == 0)
            {
                visibleAnimations.remove(x, y, layer);
            }
            map.setFace(x, y, layer, getFace(faceNum));
        }
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
    private void processMapAnimation(final int x, final int y, final int layer, final Animation animation, final int type)
    {
        synchronized (sync)
        {
            visibleAnimations.add(x, y, layer, animation, type);
        }
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
    private void processMapAnimationSpeed(final int x, final int y, final int layer, final int animationSpeed)
    {
        synchronized (sync)
        {
            visibleAnimations.updateSpeed(x, y, layer, animationSpeed);
        }
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
    private void processMapDarkness(final int x, final int y, final int darkness)
    {
        synchronized (sync)
        {
            map.setDarkness(x, y, darkness);
        }
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
        synchronized (sync)
        {
            if (!alwaysProcess && squares.isEmpty())
            {
                return;
            }

            for (final MapListener listener : mapListeners)
            {
                listener.mapChanged(map, squares);
            }
            squares.clear();
        }
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
     * @param faceNum The new face to set. <code>-1</code> means "do not change face".
     */
    private void setMultiFace(final int x, final int y, final int layer, final int faceNum)
    {
        if (faceNum == -1)
        {
            return;
        }

        final Face face = getFace(faceNum);
        synchronized (sync)
        {
            map.setMultiFace(x, y, layer, face);
        }
    }

    /**
     * Returns the {@link Face} instance by face ID.
     * @param faceNum the face ID
     * @return return the face instance, or <code>null</code> if
     * <code>faceNum==0</code>
     */
    private Face getFace(final int faceNum)
    {
        if (facesManager != null)
        {
            // request face information for later use
            facesManager.getFaceImages(faceNum); 
        }
        return faceNum == 0 ? null : faceCache.getFace(faceNum);
    }

    /**
     * Process a map scroll command.
     *
     * @param dx The distance to scroll in x-direction in squares.
     *
     * @param dy The distance to scroll in y-direction in squares.
     */
    private void processScroll(final int dx, final int dy)
    {
        synchronized (sync)
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

            for (final MapscrollListener mapscrollListener : mapscrollListeners)
            {
                mapscrollListener.mapScrolled(dx, dy);
            }
        }
    }

    /**
     * Process an updated face image.
     *
     * @param faceNum The face that has changed.
     */
    private void updateFace(final int faceNum)
    {
        synchronized (sync)
        {
            processMapBegin();

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    for (int layer = 0; layer < CrossfireMap2Command.NUM_LAYERS; layer++)
                    {
                        final Face face = map.getFace(x, y, layer);
                        if (face != null && face.getFaceNum() == faceNum)
                        {
                            map.setFace(x, y, layer, face);
                            map.dirty(x, y);
                        }
                    }
                }
            }

            processMapEnd(false);
        }
    }

    /**
     * Process a newmap command. This clears the map state.
     * @param width the width of the visible map area
     * @param height the height of the visible map area
     */
    public void processNewMap(final int width, final int height)
    {
        synchronized (sync)
        {
            this.width = width;
            this.height = height;
            map = new CfMap(mapSquareListener);

            // force dirty flags to be set for the visible map region
            map.clearSquare(0, 0);
            map.clearSquare(width-1, height-1);

            visibleAnimations.setMapSize(width, height);

            for (final NewmapListener listener : newmapListeners)
            {
                listener.commandNewmapReceived();
            }
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
     * Returns the {@link CfMapAnimations} instance.
     * @return the instance
     */
    public CfMapAnimations getMapAnimations()
    {
        return visibleAnimations;
    }
}
