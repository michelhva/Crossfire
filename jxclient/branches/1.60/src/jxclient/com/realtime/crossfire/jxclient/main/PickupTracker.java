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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.settings.options.Pickup;
import org.jetbrains.annotations.NotNull;

/**
 * Updates the pickup state whenever a "player" command is received from a
 * Crossfire server.
 * @author Andreas Kirschbaum
 */
public class PickupTracker {

    /**
     * The {@link CrossfireServerConnection} to track.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The {@link Pickup} to update.
     */
    @NotNull
    private final Pickup pickup;

    /**
     * The {@link CrossfireUpdateItemListener} to track "player" commands.
     */
    @NotNull
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void delinvReceived(final int tag) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void delitemReceived(@NotNull final int[] tags) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
            pickup.update(); // reset pickup mode
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
            // ignore
        }

    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {
        /** {@inheritDoc} */
        @Override
        public void start() {
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver() {
            server.removeCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            server.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected() {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to track
     * @param server the crossfire server connection track
     * @param pickup the pickup to update
     */
    public PickupTracker(@NotNull final GuiStateManager guiStateManager, @NotNull final CrossfireServerConnection server, @NotNull final Pickup pickup) {
        this.server = server;
        this.pickup = pickup;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

}
