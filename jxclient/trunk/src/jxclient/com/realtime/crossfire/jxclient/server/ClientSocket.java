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

import com.realtime.crossfire.jxclient.util.DebugWriter;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     * The appender to write state changes to. May be <code>null</code> to not
     * write anything.
     */
    private final DebugWriter debugProtocol;

    /**
     * The {@link ClientSocketListener}s to notify.
     */
    private final List<ClientSocketListener> clientSocketListeners = new ArrayList<ClientSocketListener>();

    /**
     * The {@link Selector} used for waiting.
     */
    private final Selector selector;

    /**
     * Synchronization object for {@link #reconnect}, {@link #host}, {@link
     * #port}, and {@link #disconnectPending}.
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
     * If set, notify listeners.
     */
    private boolean disconnectPending = false;

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
     * @param debugProtocol tf non-<code>null</code>, write all protocol
     * commands to this writer
     * @throws IOException if the socket cannot be created
     */
    public ClientSocket(final DebugWriter debugProtocol) throws IOException
    {
        this.debugProtocol = debugProtocol;
        selector = Selector.open();
    }

    /**
     * Starts operation.
     */
    public void start()
    {
        debugProtocol.debugProtocolWrite("socket:start");
        thread.start();
    }

    /**
     * Stops operation.
     * @throws InterruptedException if stopping was interrupted
     */
    public void stop() throws InterruptedException
    {
        debugProtocol.debugProtocolWrite("socket:stop");
        thread.interrupt();
        thread.join();
        debugProtocol.debugProtocolWrite("socket:stopped");
    }

    /**
     * Adds a {@link ClientSocketListener} to be notified.
     * @param clientSocketListener the client socket listener to add
     */
    public void addClientSocketListener(final ClientSocketListener clientSocketListener)
    {
        clientSocketListeners.add(clientSocketListener);
    }

    /**
     * Removes a {@link ClientSocketListener} to be notified.
     * @param clientSocketListener the client socket listener to remove
     */
    public void removeClientSocketListener(final ClientSocketListener clientSocketListener)
    {
        clientSocketListeners.remove(clientSocketListener);
    }

    /**
     * Connects to a server. Disconnects an existing connection if necessary.
     * @param host the host to connect to
     * @param port the port to connect to
     */
    public void connect(final String host, final int port)
    {
        debugProtocol.debugProtocolWrite("socket:connect "+host+":"+port);
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
        debugProtocol.debugProtocolWrite("socket:disconnect");
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
                        processDisconnect("reconnect");
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
                    for (final ClientSocketListener clientSocketListener : clientSocketListeners)
                    {
                        clientSocketListener.connected();
                    }
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
                final Set<SelectionKey> selectedKeys = selector.selectedKeys();
                if (selectedKeys.remove(selectionKey))
                {
                    if (isConnected)
                    {
                        processRead();
                        processWrite();
                    }
                }
                assert selectedKeys.isEmpty();
            }
            catch (final IOException ex)
            {
                debugProtocol.debugProtocolWrite("socket:exception "+ex.getMessage(), ex);
                processDisconnect(ex.getMessage());
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
        debugProtocol.debugProtocolWrite("socket:connecting to "+host+":"+port);
        disconnectPending = true;
        for (final ClientSocketListener clientSocketListener : clientSocketListeners)
        {
            clientSocketListener.connecting();
        }

        final SocketAddress socketAddress = new InetSocketAddress(host, port);
        synchronized (syncOutput)
        {
            outputBuffer.clear();
            inputBuffer.clear();
            selectionKey = null;
            try
            {
                socketChannel = SocketChannel.open();
                selectableChannel = socketChannel.configureBlocking(false);
                try
                {
                    isConnected = socketChannel.connect(socketAddress);
                }
                catch (final UnresolvedAddressException ex)
                {
                    throw new IOException("Cannot resolve address: "+socketAddress, ex);
                }
                catch (final IllegalArgumentException ex)
                {
                    throw new IOException(ex.getMessage(), ex);
                }
                socketChannel.socket().setTcpNoDelay(true);
                interestOps = SelectionKey.OP_CONNECT;
                selectionKey = selectableChannel.register(selector, interestOps);
            }
            finally
            {
                if (selectionKey == null)
                {
                    socketChannel = null;
                    selectableChannel = null;
                    isConnected = false;
                    interestOps = 0;
                }
            }
        }
    }

    /**
     * Disconnects the socket. Does nothing if not currently connected.
     * @param reason the reason for disconnection
     */
    private void processDisconnect(final String reason)
    {
        debugProtocol.debugProtocolWrite("socket:disconnecting");
        final boolean notifyListeners;
        synchronized (syncOutput)
        {
            notifyListeners = disconnectPending;
            disconnectPending = false;
        }
        if (notifyListeners)
        {
            for (final ClientSocketListener clientSocketListener : clientSocketListeners)
            {
                clientSocketListener.disconnecting(reason);
            }
        }

        try
        {
            synchronized (syncOutput)
            {
                if (selectionKey != null)
                {
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
                    selectableChannel = null;
                    inputBuffer.clear();
                }
            }
        }
        finally
        {
            if (notifyListeners)
            {
                for (final ClientSocketListener clientSocketListener : clientSocketListeners)
                {
                    clientSocketListener.disconnected(reason);
                }
            }
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
            if (socketChannel.read(inputBuffer) == -1)
            {
                throw new EOFException();
            }
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
                for (final ClientSocketListener clientSocketListener : clientSocketListeners)
                {
                    clientSocketListener.packetReceived(inputBuf, start, end);
                }
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
        for (final ClientSocketListener clientSocketListener : clientSocketListeners)
        {
            clientSocketListener.packetSent(buf, len);
        }
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
        debugProtocol.debugProtocolWrite("socket:set interest ops to "+interestOps);
        assert Thread.holdsLock(syncOutput);
        if (selectionKey != null)
        {
            selectionKey.interestOps(interestOps);
        }
    }
}
