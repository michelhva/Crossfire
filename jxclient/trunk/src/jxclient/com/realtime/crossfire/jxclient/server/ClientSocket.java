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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A socket that processes incoming data.
 *
 * <pre>
 * final ClientSocket clientSocket = new ClientSocket(...);
 * clientSocket.writePacket(...);
 * clientSocket.disconnect();
 *
 * @author Andreas Kirschbaum
 */
public class ClientSocket extends Thread
{
    /**
     * All registered {@link CrossfireScriptMonitorListener}s.
     */
    private final CrossfireScriptMonitorListener scriptMonitorListener;

    /**
     * The host to connect to.
     */
    private final String host;

    /**
     * The port to connect to.
     */
    private final int port;

    /**
     * The packet listener which receives the read packets.
     */
    private final PacketListener packetListener;

    /**
     * The connection listener which is notified about connection related
     * events.
     */
    private final ConnectionListener connectionListener;

    /**
     * Whether this socket is connected.
     */
    private boolean connected = true;

    /**
     * A buffer for sending packets. It is used for synchronization.
     */
    private final byte[] tmp = new byte[2];

    /**
     * The socket. Set to <code>null</code> if not connected.
     */
    private Socket socket = null;

    /**
     * The output stream of the socket. Set to <code>null</code> if not
     * connected.
     */
    private OutputStream outputStream = null;

    /**
     * Create a new instance.
     *
     * @param host The host to connect to.
     *
     * @param port The port to connect to.
     *
     * @param packetListener The packet listener which receives the read
     * packets.
     *
     * @param connectionListener The connection listener to notify.
     */
    public ClientSocket(final String host, final int port, final PacketListener packetListener, final CrossfireScriptMonitorListener scriptMonitorListener, final ConnectionListener connectionListener)
    {
        this.scriptMonitorListener = scriptMonitorListener;
        this.connectionListener = connectionListener;
        this.host = host;
        this.port = port;
        this.packetListener = packetListener;
        start();
    }

    /**
     * Terminate the connection. Does nothing if not connected.
     */
    public void disconnect()
    {
        synchronized (tmp)
        {
            if (!connected)
            {
                return;
            }

            connected = false;
            try
            {
                if (socket != null)
                {
                    socket.close();
                }
            }
            catch (final IOException ex)
            {
                // ignore
            }
        }
    }

    /** {@inheritDoc} */
    public void run()
    {
        try
        {
            socket = new Socket(host, port);
            try
            {
                final InputStream inputStream = socket.getInputStream();
                synchronized (tmp)
                {
                    outputStream = socket.getOutputStream();
                }
                final byte[] buf = new byte[2+0xFFFF];
                int pos = 0;
LOOP:
                for (;;)
                {
                    if (isInterrupted())
                    {
                        throw new IOException("thread has been interrupted");
                    }

                    while (pos < 2)
                    {
                        final int len = inputStream.read(buf, pos, buf.length-pos);
                        if (len <= 0)
                        {
                            break LOOP;
                        }

                        pos += len;
                    }
                    final int packetLen = 2+(buf[0]&0xFF)*0x100+(buf[1]&0xFF);
                    while (pos < packetLen)
                    {
                        final int len = inputStream.read(buf, pos, buf.length-pos);
                        if (len <= 0)
                        {
                            break LOOP;
                        }

                        pos += len;
                    }

                    int thisStart = 0;
                    int thisLen = packetLen-2;
                    for (;;)
                    {
                        packetListener.processPacket(buf, thisStart+2, thisStart+2+thisLen);
                        thisStart += 2+thisLen;
                        if (thisStart+2 > pos)
                        {
                            break;
                        }
                        thisLen = (buf[thisStart]&0xFF)*0x100+(buf[thisStart+1]&0xFF);
                        if (thisStart+2+thisLen > pos)
                        {
                            break;
                        }
                    }

                    System.arraycopy(buf, thisStart, buf, 0, pos-thisStart);
                    pos -= thisStart;
                }
            }
            finally
            {
                final Socket s = socket;
                synchronized (tmp)
                {
                    connected = false;
                    socket = null;
                    outputStream = null;
                }
                s.close();
            }
        }
        catch (final IOException ex)
        {
            System.err.println("Warning: connection to server lost: "+ex.getMessage());
        }
        catch (final UnknownCommandException ex)
        {
            System.err.println("Warning: received unknown command: "+ex.getMessage());
        }

        connectionListener.connectionLost();
    }

    /**
     * Write a packet. The packet contents must not change until this function
     * has returned.
     *
     * <p>This function may be called even if the socket has been closed. In
     * this case he packet is discarded.
     *
     * @param buf The packet to send.
     *
     * @param len The number of bytes to send.
     */
    public void writePacket(final byte[] buf, final int len)
    {
        synchronized (tmp)
        {
            if (!connected)
            {
                return;
            }

            tmp[0] = (byte)(len/0x100);
            tmp[1] = (byte)len;
            try
            {
                outputStream.write(tmp);
                outputStream.write(buf, 0, len);
            }
            catch (final IOException ex)
            {
                connected = false;
                try
                {
                    socket.close();
                }
                catch (final IOException ex2)
                {
                    // ignore
                }
                return;
            }
        }

        scriptMonitorListener.commandSent(buf, len);
    }
}
