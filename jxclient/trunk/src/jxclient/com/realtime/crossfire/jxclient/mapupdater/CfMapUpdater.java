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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.map.CfMap;
import com.realtime.crossfire.jxclient.map.Location;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireTickListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateMapListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to update a {@link CfMap} model from protocol commands.
 * @author Andreas Kirschbaum
 */
public class CfMapUpdater {

    /**
     * The updated {@link MapUpdaterState} instance.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The listener to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            mapUpdaterState.updateFace(face.getFaceNum());
        }

    };

    /**
     * The listener to detect map model changes.
     */
    @NotNull
    private final CrossfireUpdateMapListener crossfireUpdateMapListener = new CrossfireUpdateMapListener() {

        @Override
        public void newMap(final int mapWidth, final int mapHeight) {
            mapUpdaterState.newMap(mapWidth, mapHeight);
        }

        @Override
        public void mapBegin() {
            mapUpdaterState.mapBegin();
        }

        @Override
        public void mapClear(final int x, final int y) {
            mapUpdaterState.mapClear(x, y);
        }

        @Override
        public void mapDarkness(final int x, final int y, final int darkness) {
            mapUpdaterState.mapDarkness(x, y, darkness);
        }

        @Override
        public void mapFace(@NotNull final Location location, final int faceNum) {
            mapUpdaterState.mapFace(location, faceNum, true);
        }

        @Override
        public void mapAnimation(@NotNull final Location location, final int animationNum, final int animationType) {
            mapUpdaterState.mapAnimation(location, animationNum, animationType);
        }

        @Override
        public void mapAnimationSpeed(@NotNull final Location location, final int animationSpeed) {
            mapUpdaterState.mapAnimationSpeed(location, animationSpeed);
        }

        @Override
        public void mapSmooth(@NotNull final Location location, final int smooth) {
            mapUpdaterState.mapSmooth(location, smooth);
        }

        @Override
        public void mapScroll(final int dx, final int dy) {
            mapUpdaterState.mapScroll(dx, dy);
        }

        @Override
        public void mapMagicMap(final int x, final int y, final byte[][] data) {
            mapUpdaterState.magicMap(x, y, data);
        }

        @Override
        public void mapEnd() {
            mapUpdaterState.mapEnd(true);
        }

        @Override
        public void addAnimation(final int animation, final int flags, @NotNull final int[] faces) {
            mapUpdaterState.addAnimation(animation, flags, faces);
        }

    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            mapUpdaterState.reset();
        }

        @Override
        public void metaserver() {
            mapUpdaterState.reset();
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            mapUpdaterState.reset();
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * The listener for receiving "tick" commands.
     */
    @NotNull
    private final CrossfireTickListener crossfireTickListener = new CrossfireTickListener() {

        @Override
        public void tick(final int tickNo) {
            mapUpdaterState.tick(tickNo);
        }

    };

    /**
     * Creates a new instance.
     * @param mapUpdaterState the map updater state to update
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager to track for updated faces
     * @param guiStateManager the gui state manager to watch
     */
    public CfMapUpdater(@NotNull final MapUpdaterState mapUpdaterState, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final GuiStateManager guiStateManager) {
        this.mapUpdaterState = mapUpdaterState;
        facesManager.addFacesManagerListener(facesManagerListener);
        crossfireServerConnection.setCrossfireUpdateMapListener(crossfireUpdateMapListener);
        guiStateManager.addGuiStateListener(guiStateListener);
        crossfireServerConnection.addCrossfireTickListener(crossfireTickListener);
    }

}
