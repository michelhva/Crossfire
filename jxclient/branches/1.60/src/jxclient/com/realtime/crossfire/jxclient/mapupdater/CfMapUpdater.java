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

package com.realtime.crossfire.jxclient.mapupdater;

import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.CfMapAnimations;
import com.realtime.crossfire.jxclient.map.CfMapSquare;
import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.server.crossfire.MapSizeListener;
import com.realtime.crossfire.jxclient.server.crossfire.messages.Map2;
import com.realtime.crossfire.jxclient.server.socket.ClientSocket;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to update a {@link CfMap} model from protocol commands.
 * <p/>
 * The map updater is called from two threads: the {@link ClientSocket} reading
 * commands received from the Crossfire server, and {@link
 * com.realtime.crossfire.jxclient.faces.FileCacheFaceQueue} reading faces from
 * the (file) cache. Synchronization is by {@link #sync} and applies to the
 * whole map model {@link #map}.
 * @author Andreas Kirschbaum
 */
public class CfMapUpdater {

    /**
     * The object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} to track for updated faces.
     */
    @NotNull
    private final FacesManager facesManager;

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
    private final Collection<MapScrollListener> mapScrollListeners = new ArrayList<MapScrollListener>();

    /**
     * The {@link MapSizeListener}s to be notified.
     */
    @NotNull
    private final Collection<MapSizeListener> mapSizeListeners = new ArrayList<MapSizeListener>();

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
     * The listener to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void faceUpdated(@NotNull final Face face) {
            updateFace(face.getFaceNum());
        }

    };

    /**
     * The listener to detect map model changes.
     */
    @NotNull
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void newMap(final int mapWidth, final int mapHeight) {
            processNewMap(mapWidth, mapHeight);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapBegin() {
            processMapBegin();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapClear(final int x, final int y) {
            processMapClear(x, y);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapDarkness(final int x, final int y, final int darkness) {
            processMapDarkness(x, y, darkness);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapFace(final int x, final int y, final int layer, final int faceNum) {
            processMapFace(new Location(x, y, layer), faceNum, true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapAnimation(final int x, final int y, final int layer, final int animationNum, final int animationType) {
            final Animation animation = animations.get(animationNum);
            if (animation == null) {
                System.err.println("unknown animation id "+animationNum+", ignoring");
                return;
            }
            processMapAnimation(x, y, layer, animation, animationType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapAnimationSpeed(final int x, final int y, final int layer, final int animSpeed) {
            processMapAnimationSpeed(x, y, layer, animSpeed);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void scroll(final int dx, final int dy) {
            processMapScroll(dx, dy);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapMagicMap(final int x, final int y, final int color) {
            processMagicMap(x, y, color);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mapEnd() {
            processMapEnd(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addAnimation(final int animation, final int flags, @NotNull final int[] faces) {
            animations.addAnimation(animation, flags, faces);
        }

    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
            reset();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void metaserver() {
            reset();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            reset();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connected() {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param facesManager the faces manager to track for updated faces
     */
    public CfMapUpdater(@NotNull final FacesManager facesManager) {
        this.facesManager = facesManager;
        animations = new Animations(null);
        facesManager.addFacesManagerListener(facesManagerListener);
        map = new CfMap();
        visibleAnimations = new CfMapAnimations(this);
    }

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager to track for updated faces
     * @param guiStateManager the gui state manager to watch
     */
    public CfMapUpdater(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final GuiStateManager guiStateManager) {
        this.facesManager = facesManager;
        animations = new Animations(guiStateManager);
        facesManager.addFacesManagerListener(facesManagerListener);
        map = new CfMap();
        visibleAnimations = new CfMapAnimations(crossfireServerConnection, this);
        crossfireServerConnection.addCrossfireUpdateMapListener(crossfireUpdateMapListener);
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Adds a listener to notify about changed map squares.
     * @param listener the listener to add
     */
    public void addCrossfireMapListener(@NotNull final MapListener listener) {
        mapListeners.add(listener);
    }

    /**
     * Removes a listener to notify about changed map squares.
     * @param listener the listener to remove
     */
    public void removeCrossfireMapListener(@NotNull final MapListener listener) {
        mapListeners.remove(listener);
    }

    /**
     * Adds a listener to notify about cleared maps.
     * @param listener the listener to add
     */
    public void addCrossfireNewmapListener(@NotNull final NewmapListener listener) {
        newmapListeners.add(listener);
    }

    /**
     * Removes a listener to notify about cleared maps.
     * @param listener the listener to remove
     */
    public void removeCrossfireNewmapListener(@NotNull final NewmapListener listener) {
        newmapListeners.remove(listener);
    }

    /**
     * Adds a listener to notify about scrolled maps.
     * @param listener the listener to add
     */
    public void addCrossfireMapScrollListener(@NotNull final MapScrollListener listener) {
        mapScrollListeners.add(listener);
    }

    /**
     * Removes a listener to notify about scrolled maps.
     * @param listener the listener to remove
     */
    public void removeCrossfireMapScrollListener(@NotNull final MapScrollListener listener) {
        mapScrollListeners.remove(listener);
    }

    /**
     * Adds a listener to be notified about map size changes.
     * @param listener the listener to add
     */
    public void addMapSizeListener(@NotNull final MapSizeListener listener) {
        mapSizeListeners.add(listener);
    }

    /**
     * Removes a listener to be notified about map size changes.
     * @param listener the listener to remove
     */
    public void removeMapSizeListener(@NotNull final MapSizeListener listener) {
        mapSizeListeners.remove(listener);
    }

    /**
     * Resets the animation state.
     */
    private void reset() {
        synchronized (sync) {
            processNewMap(width, height);
        }
    }

    /**
     * Starts processing of a set of map square changes.
     */
    public void processMapBegin() {
    }

    /**
     * Updates a map square by clearing it.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     */
    public void processMapClear(final int x, final int y) {
        synchronized (sync) {
            visibleAnimations.remove(x, y);
            outOfViewMultiFaces.clear();
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                map.clearSquare(x, y);
            }
        }
    }

    /**
     * Updates a map square by changing a face.
     * @param location the location to update
     * @param faceNum the face to set. <code>0</code> clears the square
     * @param clearAnimation whether an animation should be cleared
     */
    public void processMapFace(@NotNull final Location location, final int faceNum, final boolean clearAnimation) {
        synchronized (sync) {
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                if (clearAnimation) {
                    visibleAnimations.remove(location);
                }
                final Face face = getFace(faceNum);
                final int x = location.getX();
                final int y = location.getY();
                if (x >= width || y >= height) {
                    if (face == null) {
                        outOfViewMultiFaces.remove(location);
                    } else if (face.getTileWidth() > 1 || face.getTileHeight() > 1) {
                        outOfViewMultiFaces.add(location);
                    }
                }
                map.setFace(x, y, location.getLayer(), face);
            }
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
    private void processMapAnimation(final int x, final int y, final int layer, @NotNull final Animation animation, final int type) {
        synchronized (sync) {
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                map.setFace(x, y, layer, null);
                final Location location = new Location(x, y, layer);
                visibleAnimations.add(location, animation, type);
            }
        }
    }

    /**
     * Updates a map square by changing the animation speed.
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param layer the layer to update
     * @param animationSpeed the animation speed to set
     */
    private void processMapAnimationSpeed(final int x, final int y, final int layer, final int animationSpeed) {
        synchronized (sync) {
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
    private void processMapDarkness(final int x, final int y, final int darkness) {
        synchronized (sync) {
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                map.setDarkness(x, y, darkness);
            }
        }
    }

    /**
     * Finishes processing of a set of map square changes. Notifies listeners
     * about changes. present
     * @param x the x-coordinate of the square
     * @param y the y-coordinate of the square
     * @param color the magic map color to set
     */
    public void processMagicMap(final int x, final int y, final int color) {
        synchronized (sync) {
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                map.setColor(x, y, color);
            }
        }
    }

    /**
     * Finishes processing of a set of map square changes. Notifies listeners
     * about changes.
     * @param alwaysProcess if set, notify listeners even if no changes are
     * present
     */
    public void processMapEnd(final boolean alwaysProcess) {
        synchronized (sync) {
            final Set<CfMapSquare> squares;
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                squares = map.getDirtyMapSquares();
                if (!alwaysProcess && squares.isEmpty()) {
                    return;
                }
            }

            for (final MapListener listener : mapListeners) {
                listener.mapChanged(map, squares);
            }
        }
    }

    /**
     * Returns the {@link Face} instance by face ID.
     * @param faceNum the face ID
     * @return return the face instance, or <code>null</code> if
     *         <code>faceNum==0</code>
     */
    @Nullable
    private Face getFace(final int faceNum) {
        return facesManager.getFace2(faceNum);
    }

    /**
     * Processes a map scroll command.
     * @param dx the distance to scroll in x-direction in squares
     * @param dy the distance to scroll in y-direction in squares
     */
    public void processMapScroll(final int dx, final int dy) {
        synchronized (sync) {
            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                for (final Location location : outOfViewMultiFaces) {
                    visibleAnimations.remove(location);
                    map.setFace(location.getX(), location.getY(), location.getLayer(), null);
                }
                outOfViewMultiFaces.clear();

                if (Math.abs(dx) >= width || Math.abs(dy) >= height) {
                    map.scroll(dx, dy);
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            map.clearSquare(x, y);
                        }
                    }
                    visibleAnimations.clear();
                } else {
                    int tx = dx;
                    while (tx > 0) {
                        map.scroll(-1, 0);
                        for (int y = 0; y < height; y++) {
                            map.clearSquare(-1, y);
                            map.clearSquare(width-1, y);
                        }
                        tx--;
                    }
                    while (tx < 0) {
                        map.scroll(+1, 0);
                        for (int y = 0; y < height; y++) {
                            map.clearSquare(0, y);
                            map.clearSquare(width, y);
                        }
                        tx++;
                    }

                    int ty = dy;
                    while (ty > 0) {
                        map.scroll(0, -1);
                        for (int x = 0; x < width; x++) {
                            map.clearSquare(x, -1);
                            map.clearSquare(x, height-1);
                        }
                        ty--;
                    }
                    while (ty < 0) {
                        map.scroll(0, +1);
                        for (int x = 0; x <= width; x++) {
                            map.clearSquare(x, 0);
                            map.clearSquare(x, height);
                        }
                        ty++;
                    }

                    visibleAnimations.scroll(dx, dy);
                }
            }

            for (final MapScrollListener mapscrollListener : mapScrollListeners) {
                mapscrollListener.mapScrolled(dx, dy);
            }
            processMapEnd(false);
        }
    }

    /**
     * Processes an updated face image.
     * @param faceNum the face that has changed
     */
    private void updateFace(final int faceNum) {
        synchronized (sync) {
            processMapBegin();

            //noinspection NestedSynchronizedStatement,SynchronizeOnNonFinalField
            synchronized (map) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        for (int layer = 0; layer < Map2.NUM_LAYERS; layer++) {
                            final Face face = map.getFace(x, y, layer);
                            if (face != null && face.getFaceNum() == faceNum) {
                                map.setFace(x, y, layer, face);
                                map.dirty(x, y);
                            }
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
    public void processNewMap(final int width, final int height) {
        synchronized (sync) {
            final boolean changed = this.width != width || this.height != height;
            this.width = width;
            this.height = height;
            final CfMap tmp = new CfMap();
            //noinspection NestedSynchronizedStatement,SynchronizationOnLocalVariableOrMethodParameter
            synchronized (tmp) {
                // force dirty flags to be set for the visible map region
                tmp.clearSquare(0, 0);
                tmp.clearSquare(width-1, height-1);
            }
            map = tmp;

            visibleAnimations.setMapSize(width, height);

            if (changed) {
                for (final MapSizeListener listener : mapSizeListeners) {
                    listener.mapSizeChanged(width, height);
                }
            }

            for (final NewmapListener listener : newmapListeners) {
                listener.commandNewmapReceived();
            }
        }
    }

    /**
     * Returns the current map instance.
     * @return the current map instance
     */
    @NotNull
    public CfMap getMap() {
        return map;
    }

    /**
     * Returns the width of the visible map area.
     * @return the width of the visible map area
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the visible map area.
     * @return the height of the visible map area
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the {@link CfMapAnimations} instance.
     * @return the instance
     */
    @NotNull
    public CfMapAnimations getMapAnimations() {
        return visibleAnimations;
    }

}
