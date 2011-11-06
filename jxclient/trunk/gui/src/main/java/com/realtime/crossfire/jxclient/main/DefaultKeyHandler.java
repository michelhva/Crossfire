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

import com.realtime.crossfire.jxclient.guistate.GuiState;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.server.ServerConnection;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.KeyHandlerListener;
import java.nio.ByteBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link KeyHandlerListener} which updates the state of a {@link
 * GuiManager}.
 * @author Andreas Kirschbaum
 */
public class DefaultKeyHandler implements KeyHandlerListener {

    /**
     * The {@link Exiter} instance.
     */
    @NotNull
    private final Exiter exiter;

    /**
     * The {@link GuiManager} instance.
     */
    @NotNull
    private final GuiManager guiManager;

    /**
     * The {@link GuiStateManager} instance.
     */
    @NotNull
    private final GuiStateManager guiStateManager;

    /**
     * The synchronization object for accesses to {@link #connected}.
     */
    @NotNull
    private final Object semaphoreConnected = new Object();

    /**
     * Whether a server connection is active.
     */
    private boolean connected = false;

    /**
     * The {@link ClientSocketListener} used to detect connection state
     * changes.
     */
    @NotNull
    private final ClientSocketListener clientSocketListener = new ClientSocketListener() {

        @Override
        public void connecting() {
            setConnected(true);
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
            // ignore
        }

        @Override
        public void disconnected(@NotNull final String reason) {
            setConnected(false);
        }

    };

    /**
     * Creates a new instance.
     * @param exiter the exiter instance
     * @param guiManager the gui manager instance
     * @param server the server connection to track
     * @param guiStateManager the gui state manager instance
     */
    public DefaultKeyHandler(@NotNull final Exiter exiter, @NotNull final GuiManager guiManager, @NotNull final ServerConnection server, @NotNull final GuiStateManager guiStateManager) {
        this.exiter = exiter;
        this.guiManager = guiManager;
        this.guiStateManager = guiStateManager;
        server.addClientSocketListener(clientSocketListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void escPressed() {
        if (guiStateManager.getGuiState() == GuiState.CONNECT_FAILED) {
            guiStateManager.disconnect();
            return;
        }

        switch (guiManager.escPressed(isConnected())) {
        case IGNORE:
            break;

        case DISCONNECT:
            guiStateManager.disconnect();
            break;

        case QUIT:
            exiter.terminate();
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased() {
        guiManager.closeKeybindDialog();
    }

    /**
     * Records whether a server connection is active.
     * @param connected whether a server connection is active
     */
    private void setConnected(final boolean connected) {
        synchronized (semaphoreConnected) {
            this.connected = connected;
        }
    }

    /**
     * Returns whether a server connection is active.
     * @return whether a server connection is active
     */
    private boolean isConnected() {
        synchronized (semaphoreConnected) {
            return connected;
        }
    }

}
