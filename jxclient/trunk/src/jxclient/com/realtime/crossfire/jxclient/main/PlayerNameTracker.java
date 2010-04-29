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

package com.realtime.crossfire.jxclient.main;

import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemSetListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.window.JXCConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tracks a {@link GuiStateManager} and updates a {@link JXCConnection}'s
 * character name.
 * @author Andreas Kirschbaum
 */
public class PlayerNameTracker {

    /**
     * The {@link JXCConnection} to track.
     */
    @NotNull
    private final JXCConnection connection;

    /**
     * The {@link ItemSet} to track.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The listener to detect a changed player name.
     */
    @NotNull
    private final ItemSetListener itemSetListener = new ItemSetListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemAdded(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemMoved(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemChanged(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void itemRemoved(@NotNull final CfItem item) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void playerChanged(@Nullable final CfItem player) {
            connection.setCharacter(player == null ? null : player.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void openContainerChanged(final int tag) {
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
            itemSet.removeItemSetListener(itemSetListener);
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver() {
            itemSet.removeItemSetListener(itemSetListener);
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            connection.setHost(serverInfo);
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            itemSet.addItemSetListener(itemSetListener);
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
     * @param connection the connection to track
     * @param itemSet the item set to track
     */
    public PlayerNameTracker(@NotNull final GuiStateManager guiStateManager, @NotNull final JXCConnection connection, @NotNull final ItemSet itemSet) {
        this.connection = connection;
        this.itemSet = itemSet;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

}
