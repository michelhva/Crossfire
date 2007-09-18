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
package com.realtime.crossfire.jxclient;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * One of the two most important classes, ServerConnection performs most of the
 * network-related work. It either decodes commands sent by the server itself,
 * or delegates their processing to other classes, like Map or Faces.
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public abstract class ServerConnection extends Thread
{
    private Socket socket;

    private DataInputStream in;

    private final List<CrossfireScriptMonitorListener> scripts_monitor = new ArrayList<CrossfireScriptMonitorListener>();

    private final String hostname;

    private final int port;

    public enum Status
    {
        /**
         * Represents the unconnected status of the client, which is the first to
         * happen during a normal gaming session.
         * @since 1.0
         */
        UNCONNECTED,

        /**
         * Represents the status of the client that is used during play.
         * @since 1.0
         */
        PLAYING,

        /**
         * Represents the status of the client that is displaying a Query dialog.
         * @since 1.0
         */
        QUERY;
    }

    private Status status = Status.UNCONNECTED;

    private final String statusSem = "mystatus_sem";

    /**
     * The Thread Main loop. ServerConnection contains its own Thread, so it
     * can monitor the socket content in parallel with the GUI handling loop.
     * @since 1.0
     */
    public void run()
    {
        setStatus(Status.PLAYING);
        try
        {
            for (;;)
            {
                readPacket();
            }
        }
        catch (final Exception e)
        {
            setStatus(Status.UNCONNECTED);
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Reads the next available packet sent by the Crossfire server on the
     * network.
     *
     * @throws IOException If an I/O error occurs.
     *
     * @throws UnknownCommandException If the command cannot be parsed.
     */
    public synchronized void readPacket() throws IOException, UnknownCommandException
    {
        final int len = in.readUnsignedShort();
        final byte[] data = new byte[len];
        in.readFully(data);
        command(data);
    }

    /**
     * Writes a Crossfire Message on the socket, so it is sent to the server.
     * @param packet the packet to be sent; it does not include the length
     * bytes but only actual payload data
     * @param length the length of <code>packet</code>; if the array is larger,
     * excess data is ignored
     * @throws IOException If an I/O error occurs.
     */
    protected void writePacket(final byte[] packet, final int length) throws IOException
    {
        assert length > 0;
        synchronized(socket)
        {
            for (final CrossfireScriptMonitorListener listener : scripts_monitor)
            {
                listener.commandSent(packet, length);
            }

            socket.getOutputStream().write(length/256);
            socket.getOutputStream().write(length);
            socket.getOutputStream().write(packet, 0, length);
        }
    }

    /**
     * Creates a new ServerConnection that will be used to communicate with the
     * server located at the given hostname:port address.
     * Note that the connection is not performed by the constructor - you need
     * to call the connect() method.
     * @param hostname The hostname (or IP address) of the server.
     * @param port The TCP port on which the server is listening.
     * @since 1.0
     */
    protected ServerConnection(final String hostname, final int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Attempts to connect the client to the server using the previously defined
     * hostname:port address.
     * @since 1.0
     */
    public void connect()
    {
        try
        {
            socket = new Socket(hostname, port);
            in = new DataInputStream(socket.getInputStream());
            start();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Sets the current status of the client to the given value. See the various
     * STATUS_ constants.
     * @param status The new status value.
     * @since 1.0
     */
    public void setStatus(final Status status)
    {
        synchronized(statusSem)
        {
            this.status = status;
        }
    }

    /**
     * Gets the current status of the client. See the STATUS_ constants.
     * @since 1.0
     * @return A value representing the current status.
     */
    public Status getStatus()
    {
        synchronized(statusSem)
        {
            return status;
        }
    }

    /**
     * This is the main command handler, in which the command received is
     * decoded, and the appropriate method called.
     * @param packet The packet payload data.
     * @throws IOException If an I/O error occurs.
     * @throws UnknownCommandException if the command cannot be parsed.
     */
    protected abstract void command(final byte[] packet) throws IOException, UnknownCommandException;

    public void addScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.add(listener);
    }

    public void removeScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.remove(listener);
    }
}
