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

package com.realtime.crossfire.jxclient.guistate;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnectionListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the current {@link GuiState}. Notifies registered listeners about
 * changes.
 * @author Andreas Kirschbaum
 */
public class GuiStateManager {

    /**
     * The current GUI state.
     */
    @Nullable
    private GuiState guiState = null;

    /**
     * The synchronization object for accessing {@link #guiState}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The connection state listeners to notify.
     */
    @NotNull
    private final EventListenerList2<GuiStateListener> guiStateListeners = new EventListenerList2<GuiStateListener>(GuiStateListener.class);

    /**
     * The {@link CrossfireServerConnectionListener} used to detect connection
     * progress changes.
     */
    @NotNull
    private final CrossfireServerConnectionListener crossfireServerConnectionListener = new CrossfireServerConnectionListener() {

        @Override
        public void clientSocketStateChanged(@NotNull final ClientSocketState clientSocketState) {
            for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                listener.connecting(clientSocketState);
            }
            if (clientSocketState == ClientSocketState.CONNECTED) {
                changeGUI(GuiState.CONNECTED);
            }
        }

    };

    /**
     * The {@link ClientSocketListener} used to detect connection state
     * changes.
     */
    @NotNull
    private final ClientSocketListener clientSocketListener = new ClientSocketListener() {

        @Override
        public void connecting() {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void packetReceived(@NotNull final ByteBuffer packet) {
            // ignore
        }

        @Override
        public void packetSent(@NotNull final byte[] buf, final int len) {
            // ignore
        }

        @Override
        public void disconnecting(@NotNull final String reason, final boolean isError) {
            synchronized (sync) {
                if (guiState == GuiState.CONNECTING || (isError && guiState == GuiState.CONNECTED)) {
                    changeGUI(GuiState.CONNECT_FAILED, reason);
                }
            }
        }

        @Override
        public void disconnected(@NotNull final String reason) {
            synchronized (sync) {
                if (guiState != GuiState.CONNECT_FAILED) {
                    changeGUI(GuiState.METASERVER);
                }
            }
        }

    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the crossfire server connection to
     * monitor
     */
    public GuiStateManager(@NotNull final CrossfireServerConnection crossfireServerConnection) {
        crossfireServerConnection.addCrossfireServerConnectionListener(crossfireServerConnectionListener);
        crossfireServerConnection.addClientSocketListener(clientSocketListener);
    }

    /**
     * Sets a new {@link GuiState}.
     * @param guiState the new gui state
     */
    public void changeGUI(final GuiState guiState) {
        synchronized (sync) {
            if (this.guiState == guiState) {
                return;
            }

            this.guiState = guiState;

            switch (guiState) {
            case START:
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.start();
                }
                break;

            case METASERVER:
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.metaserver();
                }
                break;

            case CONNECTING:
                throw new IllegalArgumentException();

            case CONNECTED:
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.connected();
                }
                break;

            case CONNECT_FAILED:
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Sets a new {@link GuiState}.
     * @param guiState the new gui state
     * @param param a parameter for the new gui state
     */
    private void changeGUI(@NotNull final GuiState guiState, @NotNull final String param) {
        synchronized (sync) {
            if (this.guiState == guiState) {
                return;
            }

            this.guiState = guiState;

            switch (guiState) {
            case START:
            case METASERVER:
                throw new IllegalArgumentException();

            case CONNECTING:
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.preConnecting(param);
                }
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.connecting(param);
                }
                break;

            case CONNECTED:
                throw new IllegalArgumentException();

            case CONNECT_FAILED:
                for (final GuiStateListener listener : guiStateListeners.getListeners()) {
                    listener.connectFailed(param);
                }
                break;
            }
        }
    }

    /**
     * Returns the current {@link GuiState}.
     * @return the gui state
     */
    @Nullable
    public GuiState getGuiState() {
        synchronized (sync) {
            return guiState;
        }
    }

    /**
     * Adds a gui state listener.
     * @param listener the listener to add
     */
    public void addGuiStateListener(@NotNull final GuiStateListener listener) {
        guiStateListeners.add(listener);
    }

    /**
     * Removes a gui state listener.
     * @param listener the listener to remove
     */
    public void removeGuiStateListener(@NotNull final GuiStateListener listener) {
        guiStateListeners.remove(listener);
    }

    /**
     * Connects to a Crossfire server.
     * @param serverInfo the server to connect to
     */
    public void connect(@NotNull final String serverInfo) {
        changeGUI(GuiState.CONNECTING, serverInfo);
    }

    /**
     * Disconnects from the  Crossfire server.
     */
    public void disconnect() {
        changeGUI(GuiState.METASERVER);
    }

}
