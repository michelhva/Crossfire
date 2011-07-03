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

package com.realtime.crossfire.jxclient.server.crossfire;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Semaphore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression tests for {@link DefaultCrossfireServerConnection}.
 * @author Andreas Kirschbaum
 */
public class DefaultCrossfireServerConnectionTest {

    /**
     * The {@link Semaphore} for waiting until character login.
     */
    @Nullable
    private Semaphore sem;

    /**
     * Checks that {@link DefaultCrossfireServerConnection#setPreferredNumLookObjects(int)}
     * queues multiple updates.
     * @throws InterruptedException if the test fails
     * @throws IOException if the test fails
     */
    @Test
    public void testNegotiateNumLookObjects1() throws InterruptedException, IOException {
        sem = new Semaphore(0);
        final DefaultCrossfireServerConnection connection = new DefaultCrossfireServerConnection(null, "version");
        final int port = startServer();
        connection.start();
        try {
            connection.connect("localhost", port);
            connection.setPreferredNumLookObjects(10);
            assert sem != null;
            sem.acquire();
            Thread.sleep(200);
            Assert.assertEquals(10, connection.getCurrentNumLookObjects());
            connection.setPreferredNumLookObjects(11);
            connection.setPreferredNumLookObjects(12);
            connection.setPreferredNumLookObjects(13);
            connection.setPreferredNumLookObjects(14);
            Thread.sleep(200);
            Assert.assertEquals(14, connection.getCurrentNumLookObjects());
        } finally {
            connection.stop();
        }
    }

    /**
     * Starts a dummy Crossfire server.
     * @return the port the Crossfire server is listening on
     * @throws IOException if an I/O error occurs
     */
    private int startServer() throws IOException {
        //noinspection SocketOpenedButNotSafelyClosed
        final ServerSocket server = new ServerSocket(0);
        final Thread thread = new Thread(new Runnable() {

            /**
             * The {@link Charset} for converting bytes to characters.
             */
            @NotNull
            private final Charset charset = Charset.forName("ISO-8859-1");

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                try {
                    final Socket client = acceptClient(server);
                    try {
                        final InputStream in = getInputStream(client);
                        final OutputStream out = getOutputStream(client);
                        while (true) {
                            final byte[] data = readPacket(in);
                            if (data == null) {
                                break;
                            }
                            int paramsIndex;
                            for (paramsIndex = 0; paramsIndex < data.length; paramsIndex++) {
                                if (data[paramsIndex] == (byte)' ') {
                                    break;
                                }
                            }
                            final String cmd = new String(data, 0, paramsIndex, charset);
                            if (paramsIndex < data.length && data[paramsIndex] == (byte)' ') {
                                paramsIndex++;
                            }
                            if (cmd.equals("version")) {
                                writeString(out, "version 1 1 info");
                            } else if (cmd.equals("setup")) {
                                processSetup(out, new String(data, paramsIndex, data.length-paramsIndex, charset));
                            } else if (cmd.equals("requestinfo")) {
                                processRequestinfo(out, new String(data, paramsIndex, data.length-paramsIndex, charset));
                            } else if (cmd.equals("toggleextendedtext")) {
                                // ignore
                            } else if (cmd.equals("addme")) {
                                processAddme(out);
                            } else {
                                Assert.fail("received unexpected command: "+cmd);
                            }
                        }
                    } finally {
                        client.close();
                    }
                } catch (final IOException ex) {
                    Assert.fail(ex.getMessage());
                    throw new AssertionError(ex);
                }
            }

            /**
             * Reads a Crossfire message from an {@link InputStream}.
             * @param in the input stream
             * @return the message
             * @throws EOFException if the socket has been closed unexpectedly
             */
            @Nullable
            private byte[] readPacket(@NotNull final InputStream in) throws EOFException {
                final int tmp;
                try {
                    tmp = readByte(in);
                } catch (final EOFException ignored) {
                    return null;
                }
                final int packetLen = tmp*0x100+readByte(in);
                final byte[] data = new byte[packetLen];
                try {
                    int pos = 0;
                    while (pos < data.length) {
                        final int len = in.read(data, pos, data.length-pos);
                        if (len == -1) {
                            throw new EOFException("unexpected end of file reached");
                        }
                        pos += len;
                    }
                } catch (final IOException ex) {
                    Assert.fail(ex.getMessage());
                    throw new AssertionError(ex);
                }
                return data;
            }

            /**
             * Processes a "setup" message.
             * @param out the output stream for responses
             * @param params the message´s parameters
             * @throws IOException if an I/O error occurs
             */
            private void processSetup(@NotNull final OutputStream out, @NotNull final String params) throws IOException {
                final String[] params2 = params.split(" ", -1);
                Assert.assertEquals(0, params2.length%2);
                final StringBuilder sb = new StringBuilder("setup");
                for (int i = 0; i < params2.length; i += 2) {
                    final String key = params2[i];
                    final String value = params2[i+1];
                    if (key.equals("map2cmd") || key.equals("newmapcmd") || key.equals("facecache") || key.equals("extendedTextInfos") || key.equals("itemcmd") || key.equals("spellmon") || key.equals("tick") || key.equals("num_look_objects") || key.equals("mapsize")) {
                        sb.append(" ").append(key).append(" ").append(value);
                    } else {
                        sb.append(" ").append(key).append(" FALSE");
                    }
                }
                writeString(out, sb.toString());
            }

            /**
             * Processes a "requestinfo" message.
             * @param out the output stream for responses
             * @param params the message´s parameters
             * @throws IOException if an I/O error occurs
             */
            private void processRequestinfo(@NotNull final OutputStream out, @NotNull final String params) throws IOException {
                if (params.equals("exp_table")) {
                    writeBytes(out, new byte[] {
                        'r',
                        'e',
                        'p',
                        'l',
                        'y',
                        'i',
                        'n',
                        'f',
                        'o',
                        ' ',
                        'e',
                        'x',
                        'p',
                        '_',
                        't',
                        'a',
                        'b',
                        'l',
                        'e',
                        ' ',
                        0,
                        1,
                    });
                } else {
                    // ignore
                }
            }

            /**
             * Processes an "addme" message.
             * @param out the output stream for responses
             * @throws IOException if an I/O error occurs
             */
            private void processAddme(@NotNull final OutputStream out) throws IOException {
                writeString(out, "query 0 What is your name?");
                writeString(out, "addme_success");
                assert sem != null;
                sem.release();
            }

            /**
             * Reads a single byte from the client.
             * @param in the input stream to read from
             * @return the byte
             * @throws EOFException if an I/O error occurs
             */
            private int readByte(@NotNull final InputStream in) throws EOFException {
                final int ch;
                try {
                    ch = in.read();
                } catch (final IOException ex) {
                    Assert.fail(ex.getMessage());
                    throw new AssertionError(ex);
                }
                if (ch == -1) {
                    throw new EOFException();
                }
                return ch;
            }

            /**
             * Write a Crossfire message to the client.
             * @param out the output stream to write to
             * @param s the message´s payload
             * @throws IOException if an I/O error occurs
             */
            private void writeString(@NotNull final OutputStream out, @NotNull final String s) throws IOException {
                writeBytes(out, s.getBytes(charset));
            }

            /**
             * Write a Crossfire message to the client.
             * @param out the output stream to write to
             * @param b the message´s payload
             * @throws IOException if an I/O error occurs
             */
            private void writeBytes(@NotNull final OutputStream out, @NotNull final byte[] b) throws IOException {
                final int len = b.length;
                out.write(len/0x100);
                out.write(len);
                out.write(b);
            }

        });
        thread.start();
        return server.getLocalPort();
    }

    /**
     * Accepts a single client from a {@link ServerSocket}.
     * @param server the server socket
     * @return the client
     */
    @NotNull
    private static Socket acceptClient(@NotNull final ServerSocket server) {
        final Socket client;
        try {
            client = server.accept();
        } catch (final IOException ex) {
            Assert.fail(ex.getMessage());
            throw new AssertionError(ex);
        }
        return client;
    }

    /**
     * Returns the {@link InputStream} of a {@link Socket}.
     * @param socket the socket
     * @return the input stream
     */
    @NotNull
    private static InputStream getInputStream(@NotNull final Socket socket) {
        final InputStream in;
        try {
            in = socket.getInputStream();
        } catch (final IOException ex) {
            Assert.fail(ex.getMessage());
            throw new AssertionError(ex);
        }
        return in;
    }

    /**
     * Returns the {@link OutputStream} of a {@link Socket}.
     * @param socket the socket
     * @return the input stream
     */
    @NotNull
    private static OutputStream getOutputStream(@NotNull final Socket socket) {
        final OutputStream out;
        try {
            out = socket.getOutputStream();
        } catch (final IOException ex) {
            Assert.fail(ex.getMessage());
            throw new AssertionError(ex);
        }
        return out;
    }

}
