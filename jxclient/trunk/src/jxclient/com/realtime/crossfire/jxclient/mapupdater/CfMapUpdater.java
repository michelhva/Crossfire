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
import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CrossfireMap2Command;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} to track for updated faces.
     */
    @Nullable
    private final FacesManager facesManager;

    /**
     * The {@link FaceCache} instance for looking up faces.
     */
    @NotNull
    private final FaceCache faceCache;

    /**
     * The defined animations.
     */
    @NotNull
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
    @NotNull
    private CfMap map;

    /**
     * The listeners to notify about changed map squares.
     */
    @NotNull
    private final Collection<MapListener> mapListeners = new ArrayList<MapListener>();

    /**
     * The listeners to notify about cleared maps.
     */
    @NotNull
    private final Collection<NewmapListener> newmapListeners = new ArrayList<NewmapListener>();

    /**
     * The listeners to notify about scrolled maps.
     */
    @NotNull
    private final Collection<MapscrollListener> mapscrollListeners = new ArrayList<MapscrollListener>();

    /**
     * Collects the changed map squares between calls to {@link
     * #processMapBegin()} and {@link #processMapEnd(boolean)}.
     */
    @NotNull
    private final Set<CfMapSquare> squares = new HashSet<CfMapSquare>();

    /**
     * The animations in the visible map area.
     */
    @NotNull
    private final CfMapAnimations visibleAnimations;

    /**
     * All multi-tiled faces with heads outside the visible map area.
     */
    @NotNull
    private final Collection<Location> outOfViewMultiFaces = new HashSet<Location>();

    /**
     * The map square listener attached to {@link #map}.
     */
    @NotNull
    private final CfMapSquareListener mapSquareListener = new CfMapSquareListener()
    {
        /** {@inheritDoc} */
        @Override
        public void squareModified(@NotNull final CfMapSquare mapSquare)
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
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void faceUpdated(@NotNull final Face face)
        {
            updateFace(face.getFaceNum());
        }
    };

    /**
     * The listener to detect map model changes.
     */
    @NotNull
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener()
    {
        /** {@inheritDoc} */
        @Override
        public void newMap(final int mapWidth, final int mapHeight)
        {
            processNewMap(mapWidth, mapHeight);
        }

        /** {@inheritDoc} */
        @Override
        public void mapBegin()
        {
            processMapBegin();
        }

        /** {@inheritDoc} */
        @Override
        public void mapClear(final int x, final int y)
        {
            processMapClear(x, y);
        }

        /** {@inheritDoc} */
        @Override
        public void mapDarkness(final int x, final int y, final int darkness)
        {
            processMapDarkness(x, y, darkness);
        }

        /** {@inheritDoc} */
        @Override
        public void mapFace(final int x, final int y, final int layer, final int faceNum)
        {
            processMapFace(x, y, layer, faceNum, true);
        }

        /** {@inheritDoc} */
        @Override
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
        @Override
        public void mapAnimationSpeed(final int x, final int y, final int layer, final int animSpeed)
        {
            processMapAnimationSpeed(x, y, layer, animSpeed);
        }

        /** {@inheritDoc} */
        @Override
        public void scroll(final int dx, final int dy)
        {
            processMapScroll(dx, dy);
        }

        /** {@inheritDoc} */
        @Override
        public void mapEnd()
        {
            processMapEnd(true);
        }

        /** {@inheritDoc} */
        @Override
        public void addAnimation(final int animation, final int flags, @NotNull final int[] faces)
        {
            animations.addAnimation(animation, flags, faces);
        }
    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param faceCache the instance for looking up faces
     * @param animations the defined animations
     */
    public CfMapUpdater(@NotNull final FaceCache faceCache, @NotNull final Animations animations)
    {
        facesManager = null;
        this.faceCache = faceCache;
        this.animations = animations;
        map = new CfMap(mapSquareListener);
        visibleAnimations = new CfMapAnimations(this);
    }

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager to track for updated faces
     * @param faceCache the instance for looking up faces
     * @param animations the defined animations
     * @param window the window to attach to
     */
    public CfMapUpdater(@NotNull final CrossfireServerConnection crossfireServerConnection, @Nullable final FacesManager facesManager, @NotNull final FaceCache faceCache, @NotNull final Animations animations, @NotNull final JXCWindow window)
    {
        this.facesManager = facesManager;
        this.faceCache = faceCache;
        this.animations = animations;
        crossfireServerConnection.addCrossfireUpdateMapListener(crossfireUpdateMapListener);
        if (facesManager != null)
        {
            facesManager.addFacesManagerListener(facesManagerListener);
        }
        map = new CfMap(mapSquareListener);
        visibleAnimations = new CfMapAnimations(crossfireServerConnection, this);
        window.addConnectionStateListener(guiStateListener);
    }

    /**
     * Adds a listener to notify about changed map squares.
     * @param listener the listener to add
     */
    public void addCrossfireMapListener(@NotNull final MapListener listener)
    {
        mapListeners.add(listener);
    }

    /**
     * Removes a listener to notify about changed map squares.
     * @param listener the listener to remove
     */
    public void removeCrossfireMapListener(@NotNull final MapListener listener)
    {
        mapListeners.remove(listener);
    }

    /**
     * Adds a listener to notify about cleared maps.
     * @param listener the listener to add
     */
    public void addCrossfireNewmapListener(@NotNull final NewmapListener listener)
    {
        newmapListeners.add(listener);
    }

    /**
     * Removes a listener to notify about cleared maps.
     * @param listener the listener to remove
     */
    public void removeCrossfireNewmapListener(@NotNull final NewmapListener listener)
    {
        newmapListeners.remove(listener);
    }

    /**
     * Adds a listener to notify about scrolled maps.
     * @param listener the listener to add
     */
    public void addCrossfireMapscrollListener(@NotNull final MapscrollListener listener)
    {
        mapscrollListeners.add(listener);
    }

    /**
     * Removes a listener to notify about scrolled maps.
     * @param listener the listener to remove
     */
    public void removeCrossfireMapscrollListener(@NotNull final MapscrollListener listener)
    {
        mapscrollListeners.remove(listener);
    }

    /**
     * Resets the animation state.
     */
    private void reset()
    {
        synchronized (sync)
        {
            processNewMap(1, 1);
        }
    }

    /**
     * Starts processing of a set of map square changes.
     */
    public void processMapBegin()
    {
    }

    /**
     * Updates a map square by clearing it.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void processMapClear(final int x, final int y)
    {
        synchronized (sync)
        {
            visibleAnimations.remove(x, y);
            outOfViewMultiFaces.clear();
            map.clearSquare(x, y);
        }
    }

    /**
     * Updates a map square by changing a face.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to update
     * @param faceNum the face to set. <code>0</code> clears the square
     * @param clearAnimation whether an animation should be cleared
     */
    public void processMapFace(final int x, final int y, final int layer, final int faceNum, final boolean clearAnimation)
    {
        synchronized (sync)
        {
            final Location location = new Location(x, y, layer);
            if (clearAnimation)
            {
                visibleAnimations.remove(location);
            }
            final Face face = getFace(faceNum);
            if (x >= width || y >= height)
            {
                if (face == null)
                {
                    outOfViewMultiFaces.remove(location);
                }
                else if (face.getTileWidth() > 1 || face.getTileHeight() > 1)
                {
                    outOfViewMultiFaces.add(location);
                }
            }
            map.setFace(x, y, layer, face);
        }
    }

    /**
     * Updates a map square by changing an animation.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to update
     * @param animation the animation to set
     * @param type the animation type
     */
    private void processMapAnimation(final int x, final int y, final int layer, @NotNull final Animation animation, final int type)
    {
        synchronized (sync)
        {
            map.setFace(x, y, layer, null);
            final Location location = new Location(x, y, layer);
            visibleAnimations.add(location, animation, type);
        }
    }

    /**
     * Updates a map square by changing the animation speed.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to update
     * @param animationSpeed the animation speed to set
     */
    private void processMapAnimationSpeed(final int x, final int y, final int layer, final int animationSpeed)
    {
        synchronized (sync)
        {
            final Location location = new Location(x, y, layer);
            visibleAnimations.updateSpeed(location, animationSpeed);
        }
    }

    /**
     * Updates a map square by changing the darkness value.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param darkness the darkness value to set
     */
    private void processMapDarkness(final int x, final int y, final int darkness)
    {
        synchronized (sync)
        {
            map.setDarkness(x, y, darkness);
        }
    }

    /**
     * Finishes processing of a set of map square changes. Notifies listeners
     * about changes.
     * @param alwaysProcess if set, notify listeners even if no changes are
     * present
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
     * Returns the {@link Face} instance by face ID.
     * @param faceNum the face ID
     * @return return the face instance, or <code>null</code> if
     * <code>faceNum==0</code>
     */
    @Nullable
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
     * Processes a map scroll command.
     * @param dx the distance to scroll in x-direction in squares
     * @param dy the distance to scroll in y-direction in squares
     */
    public void processMapScroll(final int dx, final int dy)
    {
        synchronized (sync)
        {
            for (final Location location : outOfViewMultiFaces)
            {
                visibleAnimations.remove(location);
                map.setFace(location.getX(), location.getY(), location.getLayer(), null);
            }
            outOfViewMultiFaces.clear();

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
                        map.clearSquare(-1, y);
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
                        map.clearSquare(width, y);
                    }
                    tx++;
                }

                int ty = dy;
                while (ty > 0)
                {
                    map.scroll(0, -1);
                    for (int x = 0; x < width; x++)
                    {
                        map.clearSquare(x, -1);
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
                        map.clearSquare(x, height);
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
     * Processes an updated face image.
     * @param faceNum the face that has changed
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
     * Processes a newmap command. This clears the map state.
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
     * Returns the current map instance.
     * @return the current map instance
     */
    @NotNull
    public CfMap getMap()
    {
        return map;
    }

    /**
     * Returns the width of the visible map area.
     * @return the width of the visible map area
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height of the visible map area.
     * @return the height of the visible map area
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Returns the {@link CfMapAnimations} instance.
     * @return the instance
     */
    @NotNull
    public CfMapAnimations getMapAnimations()
    {
        return visibleAnimations;
    }
}
