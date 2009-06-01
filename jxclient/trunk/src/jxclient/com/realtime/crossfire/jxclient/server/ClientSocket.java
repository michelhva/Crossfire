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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * A socket that processes incoming data.
 * <pre>
 * final ClientSocket clientSocket = new ClientSocket(...);
 * clientSocket.writePacket(...);
 * clientSocket.disconnect();
 * @author Andreas Kirschbaum
 */
public class ClientSocket
{
    /**
     * The maximum payload size of a Crossfire protocol packet.
     */
    private static final int MAXIMUM_PACKET_SIZE = 65536;

    /**
     * All registered {@link CrossfireScriptMonitorListener}s.
     */
    private final CrossfireScriptMonitorListener scriptMonitorListener;

    /**
     * The {@link ConnectionListener}s to notify.
     */
    private final List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();

    /**
     * The {@link Selector} used for waiting.
     */
    private final Selector selector;

    /**
     * Synchronization object for {@link #reconnect}, {@link #host}, and {@link
     * #port}.
     */
    private final Object syncConnect = new Object();

    /**
     * Set if {@link #host} or {@link #port} has changed and thus a reconnect
     * is needed.
     */
    private boolean reconnect = true;

    /**
     * The host to connect to. Set to <code>null</code> for disconnect.
     */
    private String host = null;

    /**
     * The port to connect to.
     */
    private int port = 0;

    /**
     * The packet listener which receives the read packets.
     */
    private final PacketListener packetListener;

    /**
     * A buffer for sending packets.
     */
    private final byte[] packetHeader = new byte[2];

    /**
     * The {@link SelectableChannel} of {@link #socketChannel}.
     */
    private SelectableChannel selectableChannel = null;

    /**
     * The {@link SelectionKey} registered to {@link #selectableChannel}. It's
     * interesting ops are {@link #interestOps}.
     */
    private SelectionKey selectionKey = null;

    /**
     * The currently set interest ops for {@link #selectionKey}.
     */
    private int interestOps = 0;

    /**
     * The receive buffer. It is wrapped into {@link #inputBuffer}.
     */
    private final byte[] inputBuf = new byte[2+MAXIMUM_PACKET_SIZE];

    /**
     * The receive buffer. Contains data pending to be processed.
     */
    private final ByteBuffer inputBuffer = ByteBuffer.wrap(inputBuf);

    /**
     * If set to <code>-1</code>, a two-byte packet header is read next from
     * {@link #inputBuffer}. Otherwise it is set to the packet length which
     * will be read from {@link #inputBuffer}.
     */
    private int inputLen = -1;

    /**
     * Synchronization object for {@link #outputBuffer}, {@link #selectionKey},
     * {@link #interestOps}, and {@link #socketChannel}.
     */
    private final Object syncOutput = new Object();

    /**
     * The output buffer. Contains data pending to send.
     */
    private final ByteBuffer outputBuffer = ByteBuffer.allocate(2+MAXIMUM_PACKET_SIZE);

    /**
     * The {@link SocketChannel} when connected. Set to <code>null</code> when
     * not connected.
     */
    private SocketChannel socketChannel = null;

    /**
     * Whether {@link #socketChannel} is connected.
     */
    private boolean isConnected = false;

    /**
     * The {@link Thread} used to operate the socket.
     */
    private final Thread thread = new Thread(new Runnable()
    {
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            process();
        }
    });

    /**
     * Creates a new instance.
     * @param packetListener the packet listener which receives the read
     * packets
     * @param scriptMonitorListener the listener to notify
     * @throws IOException if the socket cannot be created
     */
    public ClientSocket(final PacketListener packetListener, final CrossfireScriptMonitorListener scriptMonitorListener) throws IOException
    {
        this.scriptMonitorListener = scriptMonitorListener;
        this.packetListener = packetListener;
        selector = Selector.open();
    }

    /**
     * Starts operation.
     */
    public void start()
    {
        thread.start();
    }

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    public void stop() throws InterruptedException
    {
        thread.interrupt();
        thread.join();
    }

    /**
     * Adds a {@link ConnectionListener} to notify.
     * @param connectionListener the connection listener to add
     */
    public void addConnectionListener(final ConnectionListener connectionListener)
    {
        connectionListeners.add(connectionListener);
    }

    /**
     * Connects to a server. Disconnects an existing connection if necessary.
     * @param host the host to connect to
     * @param port the port to connect to
     */
    public void connect(final String host, final int port)
    {
        synchronized (syncConnect)
        {
            if (this.host == null || this.port == 0 || !this.host.equals(host) || this.port != port)
            {
                reconnect = true;
                this.host = host;
                this.port = port;
                selector.wakeup();
            }
        }
    }

    /**
     * Terminates the connection. Does nothing if not connected.
     */
    public void disconnect()
    {
        synchronized (syncConnect)
        {
            if (host != null || port != 0)
            {
                reconnect = true;
                host = null;
                port = 0;
                selector.wakeup();
            }
        }
    }

    /**
     * Reads/writes data from/to the socket. Returns if the {@link #thread} has
     * been interrupted.
     */
    private void process()
    {
        while (!thread.isInterrupted())
        {
            try
            {
                synchronized (syncConnect)
                {
                    if (reconnect)
                    {
                        reconnect = false;
                        processDisconnect();
                        if (host != null && port != 0)
                        {
                            processConnect(host, port);
                        }
                    }
                }

                final boolean notifyConnected;
                synchronized (syncOutput)
                {
                    if (!isConnected && socketChannel != null)
                    {
                        isConnected = socketChannel.finishConnect();
                        if (isConnected)
                        {
                            interestOps = SelectionKey.OP_READ;
                            updateInterestOps();
                            notifyConnected = true;
                        }
                        else
                        {
                            notifyConnected = false;
                        }
                    }
                    else
                    {
                        notifyConnected = false;
                    }
                }
                if (notifyConnected)
                {
                    packetListener.connected();
                }

                synchronized (syncOutput)
                {
                    if (outputBuffer.position() > 0)
                    {
                        setInterestOps(SelectionKey.OP_WRITE);
                    }
                    else
                    {
                        unsetInterestOps(SelectionKey.OP_WRITE);
                    }
                }

                selector.select();
                if (isConnected)
                {
                    processRead();
                    processWrite();
                }
            }
            catch (final IOException ex)
            {
                processDisconnect();
            }
        }
    }

    /**
     * Connects the socket. The socket must not be connected.
     * @param host the host to connect to
     * @param port the port to connect to
     * @throws IOException if an I/O error occurs
     */
    private void processConnect(final String host, final int port) throws IOException
    {
        final SocketAddress socketAddress = new InetSocketAddress(host, port);
        synchronized (syncOutput)
        {
            socketChannel = SocketChannel.open();
            selectableChannel = socketChannel.configureBlocking(false);
            isConnected = socketChannel.connect(socketAddress);
            socketChannel.socket().setTcpNoDelay(true);
            interestOps = SelectionKey.OP_CONNECT;
            selectionKey = selectableChannel.register(selector, interestOps);
            outputBuffer.clear();
        }
        inputBuffer.clear();
        connectionProgress(ClientSocketState.CONNECTING);
    }

    /**
     * Disconnects the socket. Does nothing if not currently connected.
     */
    private void processDisconnect()
    {
        synchronized (syncOutput)
        {
            if (selectionKey == null)
            {
                return;
            }

            selectionKey.cancel();
            selectionKey = null;
            outputBuffer.clear();

            try
            {
                socketChannel.socket().shutdownOutput();
            }
            catch (final IOException ex)
            {
                // ignore
            }
            try
            {
                socketChannel.close();
            }
            catch (final IOException ex)
            {
                // ignore
            }
            socketChannel = null;
        }

//XXX:        connectionProgress(ClientSocketState.DISCONNECTED);
        selectableChannel = null;

        inputBuffer.clear();

        for (final ConnectionListener connectionListener : connectionListeners)
        {
            connectionListener.connectionLost();
        }
    }

    /**
     * Reads data from the socket and parses the data into commands.
     * @throws IOException if an I/O error occurs
     */
    private void processRead() throws IOException
    {
        if (socketChannel == null)
        {
            return;
        }

        synchronized (syncOutput)
        {
            socketChannel.read(inputBuffer);
        }
        inputBuffer.flip();
        processReadCommand();
        inputBuffer.compact();
    }

    /**
     * Parses data from {@link #inputBuffer} into commands.
     */
    private void processReadCommand()
    {
        for (;;)
        {
            if (inputLen == -1)
            {
                if (inputBuffer.remaining() < 2)
                {
                    break;
                }

                inputLen = (inputBuffer.get()&0xFF)*0x100+(inputBuffer.get()&0xFF);
            }

            if (inputBuffer.remaining() < inputLen)
            {
                break;
            }

            final int start = inputBuffer.position();
            final int end = start+inputLen;
            inputBuffer.position(start+inputLen);
            inputLen = -1;
            try
            {
                packetListener.processPacket(inputBuf, start, end);
            }
            catch (final UnknownCommandException ex)
            {
                disconnect();
                break;
            }
        }
    }

    /**
     * Writes a packet. The packet contents must not change until this function
     * has returned.
     * <p>This function may be called even if the socket has been closed. In
     * this case he packet is discarded.
     * @param buf the packet to send
     * @param len the number of bytes to send
     */
    public void writePacket(final byte[] buf, final int len)
    {
        synchronized (syncOutput)
        {
            if (socketChannel == null)
            {
                return;
            }

            packetHeader[0] = (byte)(len/0x100);
            packetHeader[1] = (byte)len;
            try
            {
                try
                {
                    outputBuffer.put(packetHeader);
                    outputBuffer.put(buf, 0, len);
                }
                catch (final BufferOverflowException ex)
                {
                    throw new IOException("buffer overflow", ex);
                }
            }
            catch (final IOException ex)
            {
                try
                {
                    socketChannel.close();
                }
                catch (final IOException ex2)
                {
                    // ignore
                }
                return;
            }
        }

        selector.wakeup();
        scriptMonitorListener.commandSent(buf, len);
    }

    /**
     * Writes some pending data to the socket. Does nothing if no pending data
     * exists or if the socket does not accept data.
     * @throws IOException if an I/O error occurs
     */
    private void processWrite() throws IOException
    {
        synchronized (syncOutput)
        {
            if (socketChannel == null)
            {
                return;
            }

            if (outputBuffer.remaining() <= 0)
            {
                return;
            }

            outputBuffer.flip();
            try
            {
                socketChannel.write(outputBuffer);
            }
            finally
            {
                outputBuffer.compact();
            }
        }
    }

    /**
     * Removes interest ops to {@link #interestOps} and updates {@link
     * #selectionKey}.
     * @param interestOps the interest ops to remove
     */
    private void unsetInterestOps(final int interestOps)
    {
        assert Thread.holdsLock(syncOutput);
        if ((this.interestOps&interestOps) == 0)
        {
            return;
        }

        this.interestOps &= ~interestOps;
        updateInterestOps();
    }

    /**
     * Adds interest ops to {@link #interestOps} and updates {@link
     * #selectionKey}.
     * @param interestOps the interest ops to add
     */
    private void setInterestOps(final int interestOps)
    {
        assert Thread.holdsLock(syncOutput);
        if ((this.interestOps&interestOps) == interestOps)
        {
            return;
        }

        this.interestOps |= interestOps;
        updateInterestOps();
    }

    /**
     * Updates {@link #selectionKey}'s interest ops to match {@link
     * #interestOps}. Does nothing if <code>selectionKey</code> is <code>null</code>.
     */
    private void updateInterestOps()
    {
        assert Thread.holdsLock(syncOutput);
        if (selectionKey != null)
        {
            selectionKey.interestOps(interestOps);
        }
    }

    /**
     * Sends a connection progress notification.
     * @param clientSocketState the client socket state
     */
    public void connectionProgress(final ClientSocketState clientSocketState)
    {
        for (final ConnectionListener connectionListener : connectionListeners)
        {
            connectionListener.connected(clientSocketState);
        }
    }
}
