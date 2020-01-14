package com.realtime.crossfire.jxclient.server.crossfire;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

/**
 * A dummy Crossfire server for tests.
 */
public class TestCrossfireServer {

    /**
     * The {@link Charset} for converting bytes to characters.
     */
    @NotNull
    private final Charset charset = Charset.forName("ISO-8859-1");

    /**
     * The {@link Semaphore} for waiting until character login.
     */
    @NotNull
    private final Semaphore sem = new Semaphore(0);

    /**
     * The server socket.
     */
    @NotNull
    @SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed", "SocketOpenedButNotSafelyClosed"})
    private final ServerSocket server = new ServerSocket(0);

    /**
     * The worker thread for processing data received from the socket.
     */
    @NotNull
    private final Thread thread = new Thread(this::run);

    /**
     * The synchronization object for accesses to {@link #client}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The client socket. Set to {@code null} if not open.
     */
    @Nullable
    private Socket client;

    /**
     * Starts a dummy Crossfire server.
     * @throws IOException if an I/O error occurs
     */
    public TestCrossfireServer() throws IOException {
    }

    /**
     * Starts the server.
     */
    public void start() {
        thread.start();
    }

    /**
     * Stops the server.
     * @throws InterruptedException if the current thread was interrupted
     * @throws IOException if closing a socket fails
     */
    public void stop() throws InterruptedException, IOException {
        thread.interrupt();
        synchronized (sync) {
            server.close();
            if (client != null) {
                client.close();
            }
        }
        thread.join();
    }

    /**
     * Returns the port the server is listening on.
     * @return the port
     */
    public int getLocalPort() {
        return server.getLocalPort();
    }

    /**
     * Blocks until the character login has finished.
     * @throws InterruptedException if the current thread was interrupted
     */
    public void waitForCharacterLogin() throws InterruptedException {
        sem.acquire();
    }

    /**
     * Processes data bytes received from the socket.
     */
    private void run() {
        try {
            final Socket tmp = acceptClient(server);
            synchronized (sync) {
                client = tmp;
            }
            try {
                final InputStream in = getInputStream(tmp);
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
                    switch (cmd) {
                    case "version":
                        writeString("version 1 1 info");
                        break;

                    case "setup":
                        processSetup(new String(data, paramsIndex, data.length-paramsIndex, charset));
                        break;

                    case "requestinfo":
                        processRequestinfo(new String(data, paramsIndex, data.length-paramsIndex, charset));
                        break;

                    case "toggleextendedtext":
                        // ignore
                        break;

                    case "addme":
                        processAddme();
                        break;

                    default:
                        Assert.fail("received unexpected command: "+cmd);
                        break;
                    }
                }
            } finally {
                synchronized (sync) {
                    client = null;
                }
                //noinspection ThrowFromFinallyBlock
                tmp.close();
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
    private static byte[] readPacket(@NotNull final InputStream in) throws EOFException {
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
     * @param params the message's parameters
     * @throws IOException if an I/O error occurs
     */
    private void processSetup(@NotNull final String params) throws IOException {
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
        writeString(sb.toString());
    }

    /**
     * Processes a "requestinfo" message.
     * @param params the message's parameters
     * @throws IOException if an I/O error occurs
     */
    private void processRequestinfo(@NotNull final String params) throws IOException {
        switch (params) {
        case "exp_table":
            writeBytes("replyinfo exp_table \0\1".getBytes(StandardCharsets.US_ASCII));
            break;

        case "skill_info 1":
            writeBytes("replyinfo skill_info ".getBytes(StandardCharsets.US_ASCII));
            break;

        case "knowledge_info":
            writeBytes("replyinfo knowledge_info ".getBytes(StandardCharsets.US_ASCII));
            break;

        case "image_info":
            writeBytes("replyinfo image_info 0\n0\n".getBytes(StandardCharsets.US_ASCII));
            break;

        case "startingmap":
            writeBytes("replyinfo startingmap ".getBytes(StandardCharsets.US_ASCII));
            break;

        case "race_list":
            writeBytes("replyinfo race_list ".getBytes(StandardCharsets.US_ASCII));
            break;

        case "class_list":
            writeBytes("replyinfo class_list ".getBytes(StandardCharsets.US_ASCII));
            break;

        case "newcharinfo":
            writeBytes("replyinfo newcharinfo ".getBytes(StandardCharsets.US_ASCII));
            break;

        default:
            Assert.fail("requestinfo "+params+" not implemented");
            break;
        }
    }

    /**
     * Processes an "addme" message.
     * @throws IOException if an I/O error occurs
     */
    private void processAddme() throws IOException {
        writeString("query 0 What is your name?");
        writeString("addme_success");
        assert sem != null;
        sem.release();
    }

    /**
     * Reads a single byte from the client.
     * @param in the input stream to read from
     * @return the byte
     * @throws EOFException if an I/O error occurs
     */
    private static int readByte(@NotNull final InputStream in) throws EOFException {
        final int ch;
        try {
            ch = in.read();
        } catch (final SocketException ex) {
            final String message = ex.getMessage();
            if (!message.equals("Socket closed")) {
                Assert.fail(message);
                throw new AssertionError(ex);
            }
            final EOFException ex2 = new EOFException("EOF");
            ex2.initCause(ex);
            throw ex2;
        } catch (final IOException ex) {
            Assert.fail(ex.getMessage());
            throw new AssertionError(ex);
        }
        if (ch == -1) {
            throw new EOFException("EOF");
        }
        return ch;
    }

    /**
     * Write a Crossfire message to the client.
     * @param s the message's payload
     * @throws IOException if an I/O error occurs
     */
    private void writeString(@NotNull final String s) throws IOException {
        writeBytes(s.getBytes(charset));
    }

    /**
     * Write a Crossfire message to the client.
     * @param b the message's payload
     * @throws IOException if an I/O error occurs
     */
    private void writeBytes(@NotNull final byte[] b) throws IOException {
        final OutputStream out = getOutputStream();
        final int len = b.length;
        out.write(len/0x100);
        out.write(len);
        out.write(b);
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
            //noinspection SocketOpenedButNotSafelyClosed
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
     * @return the input stream
     */
    @NotNull
    private OutputStream getOutputStream() {
        final Socket tmp;
        synchronized (sync) {
            tmp = client;
        }
        if (tmp == null) {
            Assert.fail("no client connection");
        }

        final OutputStream out;
        try {
            out = tmp.getOutputStream();
        } catch (final IOException ex) {
            Assert.fail(ex.getMessage());
            throw new AssertionError(ex);
        }
        return out;
    }

}
