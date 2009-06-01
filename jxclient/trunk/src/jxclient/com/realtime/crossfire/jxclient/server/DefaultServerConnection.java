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
package com.realtime.crossfire.jxclient.server;

import java.io.IOException;

/**
 * One of the two most important classes, ServerConnection performs most of the
 * network-related work. It either decodes commands sent by the server itself,
 * or delegates their processing to other classes, like Map or Faces.
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public abstract class DefaultServerConnection implements ServerConnection
{
    private final ClientSocket clientSocket;

    protected DefaultServerConnection() throws IOException
    {
        clientSocket = new ClientSocket();
    }

    /**
     * Starts operation.
     */
    public void start()
    {
        clientSocket.start();
    }

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    public void stop() throws InterruptedException
    {
        clientSocket.stop();
    }

    /** {@inheritDoc} */
    @Override
    public void addClientSocketListener(final ClientSocketListener clientSocketListener)
    {
        clientSocket.addClientSocketListener(clientSocketListener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeClientSocketListener(final ClientSocketListener clientSocketListener)
    {
        clientSocket.removeClientSocketListener(clientSocketListener);
    }

    /**
     * Writes a Crossfire Message on the socket, so it is sent to the server.
     * @param packet the packet to be sent; it does not include the length
     * bytes but only actual payload data
     * @param length the length of <code>packet</code>; if the array is larger,
     * excess data is ignored
     */
    protected void writePacket(final byte[] packet, final int length)
    {
        clientSocket.writePacket(packet, length);
    }

    /** {@inheritDoc} */
    @Override
    public void connect(final String hostname, final int port)
    {
        clientSocket.connect(hostname, port);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect()
    {
        clientSocket.disconnect();
    }
}
