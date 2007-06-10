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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
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

    private Socket                  mysocket;
    private DataInputStream         in;
    private byte[]                  buf = null;

    private List<CrossfireScriptMonitorListener> scripts_monitor =
            new ArrayList<CrossfireScriptMonitorListener>();

    private String                  myhost = "localhost";
    private int                     myport = 13327;

    /**
     * Represents the unconnected status of the client, which is the first to
     * happen during a normal gaming session.
     * @since 1.0
     */
    public static final int         STATUS_UNCONNECTED = 0;

    /**
     * Represents the status of the client that is used during play.
     * @since 1.0
     */
    public static final int         STATUS_PLAYING     = 1;

    /**
     * Represents the status of the client that is displaying a Query dialog.
     * @since 1.0
     */
    public static final int         STATUS_QUERY       = 2;

    /**
     * The width of the map displayed, in squares.
     * @since 1.0
     */
    public static final int         MAP_WIDTH  =17;

    /**
     * The height of the map displayed, in squares.
     * @since 1.0
     */
    public static final int         MAP_HEIGHT =13;

    /**
     * The total number of map layers to display.
     * @since 1.0
     */
    public static final int         NUM_LAYERS =3;

    /**
     * The pixel size of the gaming squares. Notice that they are supposed to
     * be *squares*, so only a single value is needed :)
     * @since 1.0
     */
    public static final int         SQUARE_SIZE = 64;

    private int                     mystatus = STATUS_UNCONNECTED;
    private String mystatus_sem = "mystatus_sem";

    /**
     * The Thread Main loop. ServerConnection contains its own Thread, so it
     * can monitor the socket content in parallel with the GUI handling loop.
     * @since 1.0
     */
    public void run()
    {
        setStatus(STATUS_PLAYING);
        try
        {
            for(;;)
            {
                readPacket();
            }
        }
        catch (Exception e)
        {
            setStatus(STATUS_UNCONNECTED);
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Tests if there are some data waiting on the socket. Just a simple wrapper
     * for an InputStream.available() check.
     * @return True if there is some data waiting, else false.
     * @since 1.0
     */
    public synchronized boolean waitForData() throws IOException
    {
        return (in.available() > 0) ? true : false;
    }

    /**
     * Reads the next available packet sent by the Crossfire server on the
     * network.
     * @since 1.0
     */
    public synchronized void readPacket() throws IOException, UnknownCommandException
    {
        int len = 0;
        int i = 0;

        len = in.readUnsignedShort();
        byte[] data = new byte[len];
        byte[] cmd = null;

        in.readFully(data);
        for (i=0;i<len;i++)
        {
            if (data[i]==0x20)
            {
                cmd = new byte[i+1];
                for(int j=0;j<i;j++)
                    cmd[j]=data[j];
                break;
            }
        }
        String cmdstr = (cmd != null) ? new String(cmd) : new String(data);
        DataInputStream dis = null;
        if (i<len)
        {
            dis = new DataInputStream(new ByteArrayInputStream(data));
            dis.skipBytes(i+1);
        }
        command(cmdstr, dis);
    }

    public void writePacket(String str) throws IOException
    {
        writePacket(str.getBytes("ISO-8859-1"));
    }

    /**
     * Writes a Crossfire Message on the socket, so it is sent to the server.
     * @param packet the packet to be sent; it does not include the length
     * bytes but only actual payload data
     * @since 1.0
     */
    private void writePacket(final byte[] packet) throws IOException
    {
        synchronized(mysocket)
        {
            for (final CrossfireScriptMonitorListener listener : scripts_monitor)
            {
                listener.commandSent(packet);
            }

            mysocket.getOutputStream().write(packet.length/256);
            mysocket.getOutputStream().write(packet.length);
            mysocket.getOutputStream().write(packet);
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
    protected ServerConnection(String hostname, int port)
    {
        buf = new byte[2];
        myhost = hostname;
        myport = port;
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
            mysocket = new Socket(myhost, myport);
            in = new DataInputStream(mysocket.getInputStream());
            start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Sets the current status of the client to the given value. See the various
     * STATUS_ constants.
     * @param nstatus The new status value.
     * @since 1.0
     */
    public void setStatus(int nstatus)
    {
        synchronized(mystatus_sem)
        {
            mystatus = nstatus;
        }
    }

    /**
     * Gets the current status of the client. See the STATUS_ constants.
     * @since 1.0
     * @return A value representing the current status.
     */
    public int getStatus()
    {
        synchronized(mystatus_sem)
        {
            return mystatus;
        }
    }

    /**
     * This is the main command handler, in which the command received is
     * decoded, and the appropriate method called.
     * @param cmd The S->C command received.
     * @param dis The DataInputStream holding the content of the message.
     * @since 1.0
     */
    protected abstract void command(String cmd, DataInputStream dis) throws IOException, UnknownCommandException;

    public void addScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.add(listener);
    }
    public void removeScriptMonitor(CrossfireScriptMonitorListener listener)
    {
        scripts_monitor.remove(listener);
    }
}
