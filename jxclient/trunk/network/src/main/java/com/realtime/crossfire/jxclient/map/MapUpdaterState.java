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

import com.realtime.crossfire.jxclient.animations.Animation;
import com.realtime.crossfire.jxclient.animations.Animations;
import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireTickListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Update a {@link CfMap} model from protocol commands.
 * <p/>
 * The map updater is called from two threads: the {@link
 * com.realtime.crossfire.jxclient.server.socket.ClientSocket} reading commands
 * received from the Crossfire server, and {@link com.realtime.crossfire.jxclient.faces.FileCacheFaceQueue}
 * reading faces from the (file) cache. Synchronization is by {@link #sync} and
 * applies to the whole map model {@link #map}.
 * @author Andreas Kirschbaum
 */
public class MapUpdaterState implements CrossfireTickListener, CrossfireUpdateMapListener, FacesManagerListener {

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
    private int mapWidth = 0;

    /**
     * The height of the visible map area.
     */
    private int mapHeight = 0;

    /**
     * The current {@link CfMap} instance.
     */
    @NotNull
    private final CfMap map = new CfMap();

    /**
     * The listeners to notify about changed map squares.
     */
    @NotNull
    private final EventListenerList2<MapListener> mapListeners = new EventListenerList2<MapListener>(MapListener.class);

    /**
     * The listeners to notify about cleared maps.
     */
    @NotNull
    private final EventListenerList2<NewmapListener> newmapListeners = new EventListenerList2<NewmapListener>(NewmapListener.class);

    /**
     * The listeners to notify about scrolled maps.
     */
    @NotNull
    private final EventListenerList2<MapScrollListener> mapScrollListeners = new EventListenerList2<MapScrollListener>(MapScrollListener.class);

    /**
     * The {@link MapSizeListener MapSizeListeners} to be notified.
     */
    @NotNull
    private final EventListenerList2<MapSizeListener> mapSizeListeners = new EventListenerList2<MapSizeListener>(MapSizeListener.class);

    /**
     * The animations in the visible map area.
     */
    @NotNull
    private final CfMapAnimations visibleAnimations = new CfMapAnimations();

    /**
     * All multi-tiled faces with heads outside the visible map area.
     */
    @NotNull
    private final Collection<Location> outOfViewMultiFaces = new HashSet<Location>();

    /**
     * Creates a new instance.
     * @param facesManager the faces manager to track for updated faces
     * @param guiStateManager the gui state manager to watch or
     * <code>null</code>
     */
    public MapUpdaterState(@NotNull final FacesManager facesManager, @Nullable final GuiStateManager guiStateManager) {
        this.facesManager = facesManager;
        animations = new Animations(guiStateManager);
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
    public void reset() {
        synchronized (sync) {
            newMap(mapWidth, mapHeight);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Object mapBegin() {
        return sync;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapClear(final int x, final int y) {
        assert Thread.holdsLock(sync);
        visibleAnimations.remove(x, y);
        outOfViewMultiFaces.clear();
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.clearSquare(x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFace(@NotNull final Location location, final int faceNum) {
        mapFace(location, faceNum, true);
    }

    /**
     * Updates a map square by changing a face.
     * @param location the location to update
     * @param faceNum the face to set; <code>0</code> clears the square
     * @param clearAnimation whether an animation should be cleared
     */
    public void mapFace(@NotNull final Location location, final int faceNum, final boolean clearAnimation) {
        assert Thread.holdsLock(sync);
        if (clearAnimation) {
            visibleAnimations.remove(location);
        }
        final Face face = facesManager.getFace2(faceNum);
        final int x = location.getX();
        final int y = location.getY();
        if (x >= mapWidth || y >= mapHeight) {
            if (face == null) {
                outOfViewMultiFaces.remove(location);
            } else if (face.getTileWidth() > 1 || face.getTileHeight() > 1) {
                outOfViewMultiFaces.add(location);
            }
        }
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.setFace(x, y, location.getLayer(), face);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapAnimation(@NotNull final Location location, final int animationNum, final int animationType) {
        assert Thread.holdsLock(sync);
        final Animation animation = animations.get(animationNum);
        if (animation == null) {
            System.err.println("unknown animation id "+animationNum+", ignoring");
            return;
        }

        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.setFace(location.getX(), location.getY(), location.getLayer(), null);
        }
        visibleAnimations.add(this, location, animation, animationType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapAnimationSpeed(@NotNull final Location location, final int animationSpeed) {
        assert Thread.holdsLock(sync);
        visibleAnimations.updateSpeed(this, location, animationSpeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapSmooth(@NotNull final Location location, final int smooth) {
        assert Thread.holdsLock(sync);
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.setSmooth(location.getX(), location.getY(), location.getLayer(), smooth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapDarkness(final int x, final int y, final int darkness) {
        assert Thread.holdsLock(sync);
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.setDarkness(x, y, darkness);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void magicMap(final int x, final int y, final byte[][] data) {
        assert Thread.holdsLock(sync);
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            map.setMagicMap(x, y, data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapEnd() {
        mapEnd(true);
    }

    /**
     * Finishes processing of a set of map square changes. Notifies listeners
     * about changes.
     * @param alwaysProcess if set, notify listeners even if no changes are
     * present
     */
    public void mapEnd(final boolean alwaysProcess) {
        assert Thread.holdsLock(sync);
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            final Set<CfMapSquare> squares = map.getDirtyMapSquares();
            if (!alwaysProcess && squares.isEmpty()) {
                return;
            }

            for (final MapListener listener : mapListeners.getListeners()) {
                listener.mapChanged(map, squares);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapScroll(final int dx, final int dy) {
        assert Thread.holdsLock(sync);
        //noinspection SynchronizeOnNonFinalField
        synchronized (map) {
            for (final Location location : outOfViewMultiFaces) {
                visibleAnimations.remove(location);
                map.setFace(location.getX(), location.getY(), location.getLayer(), null);
            }
            outOfViewMultiFaces.clear();

            if (map.processMapScroll(dx, dy, mapWidth, mapHeight)) {
                visibleAnimations.clear();
            } else {
                visibleAnimations.scroll(dx, dy);
            }
        }

        for (final MapScrollListener mapscrollListener : mapScrollListeners.getListeners()) {
            mapscrollListener.mapScrolled(dx, dy);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void faceUpdated(@NotNull final Face face) {
        synchronized (mapBegin()) {
            //noinspection SynchronizeOnNonFinalField,NestedSynchronizedStatement
            synchronized (map) {
                map.updateFace(face.getFaceNum(), mapWidth, mapHeight);
            }
            mapEnd();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newMap(final int mapWidth, final int mapHeight) {
        synchronized (sync) {
            final boolean changed = this.mapWidth != mapWidth || this.mapHeight != mapHeight;
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
            //noinspection NestedSynchronizedStatement
            synchronized (map) {
                map.reset(mapWidth, mapHeight);
            }

            visibleAnimations.setMapSize(mapWidth, mapHeight);

            if (changed) {
                for (final MapSizeListener listener : mapSizeListeners.getListeners()) {
                    listener.mapSizeChanged(mapWidth, mapHeight);
                }
            }

            for (final NewmapListener listener : newmapListeners.getListeners()) {
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
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * Returns the height of the visible map area.
     * @return the height of the visible map area
     */
    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnimation(final int animation, final int flags, @NotNull final int[] faces) {
        animations.addAnimation(animation, flags, faces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick(final int tickNo) {
        synchronized (sync) {
            visibleAnimations.tick(this, tickNo);
        }
    }

}
