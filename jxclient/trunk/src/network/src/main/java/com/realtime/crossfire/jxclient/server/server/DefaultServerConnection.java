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

package com.realtime.crossfire.jxclient.server.server;

import com.realtime.crossfire.jxclient.server.crossfire.Model;
import com.realtime.crossfire.jxclient.server.socket.ClientSocket;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketMonitorCommand;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * One of the two most important classes, ServerConnection performs most of the
 * network-related work. It either decodes commands sent by the server itself,
 * or delegates their processing to other classes, like Map or Faces.
 * @author Lauwenmark
 */
public class DefaultServerConnection implements ServerConnection {

    /**
     * The {@link ClientSocket} instance used to connect to Crossfire servers.
     */
    @NotNull
    private final ClientSocket clientSocket;

    /**
     * Creates a new instance.
     * @param model the model to update
     * @param debugProtocol tf non-{@code null}, write all protocol commands to
     * this writer
     * @throws IOException if an internal error occurs
     */
    public DefaultServerConnection(@NotNull final Model model, @Nullable final DebugWriter debugProtocol) throws IOException {
        clientSocket = new ClientSocket(model, debugProtocol);
    }

    /**
     * Starts operation.
     */
    public void start() {
        clientSocket.start();
    }

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    public void stop() throws InterruptedException {
        clientSocket.stop();
    }

    @Override
    public void addClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        clientSocket.addClientSocketListener(clientSocketListener);
    }

    @Override
    public void removeClientSocketListener(@NotNull final ClientSocketListener clientSocketListener) {
        clientSocket.removeClientSocketListener(clientSocketListener);
    }

    /**
     * Writes a Crossfire Message on the socket, so it is sent to the server.
     * @param packet the packet to be sent; it does not include the length bytes
     * but only actual payload data
     * @param length the length of {@code packet}; if the array is larger,
     * excess data is ignored
     * @param monitor the monitor command to send to script processes
     */
    public void writePacket(@NotNull final byte[] packet, final int length, @NotNull final ClientSocketMonitorCommand monitor) {
        clientSocket.writePacket(packet, length, monitor);
    }

    @Override
    public void connect(@NotNull final String hostname, final int port) {
        clientSocket.connect(hostname, port);
    }

    @Override
    public void disconnect(@NotNull final String reason) {
        clientSocket.disconnect(reason, false);
    }

}
