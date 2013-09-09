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

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.server.socket.UnknownCommandException;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Negotiates the size of the ground view in items with the Crossfire server.
 * @author Andreas Kirschbaum
 */
public class NumLookObjects {

    /**
     * The default number of ground objects when no "setup num_look_objects"
     * command has been sent.
     */
    private static final int DEFAULT_NUM_LOOK_OBJECTS = 50;

    /**
     * The {@link CrossfireServerConnection} for sending "setup" commands.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The appender to write protocol commands to. May be <code>null</code> to
     * not write anything.
     */
    @Nullable
    private final DebugWriter debugProtocol;

    /**
     * The synchronization objects for accessing mutable fields.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * Whether the current client socket state is {@link
     * ClientSocketState#CONNECTED}.
     */
    private boolean connected;

    /**
     * The number of ground view objects to be negotiated with the server.
     */
    private int preferredNumLookObjects = DEFAULT_NUM_LOOK_OBJECTS;

    /**
     * The number of ground view objects being negotiated with the server. Set
     * to <code>0</code> when not negotiating.
     */
    private int pendingNumLookObjects;

    /**
     * The currently active number of ground view objects.
     */
    private int currentNumLookObjects;

    /**
     * Whether negotiation may be pending.
     */
    private boolean pending;

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the crossfire server connection for
     * sending setup commands
     * @param debugProtocol the appender for writing debug messages to or {@code
     * null} to not write debug messages
     */
    NumLookObjects(@NotNull final CrossfireServerConnection crossfireServerConnection, @Nullable final DebugWriter debugProtocol) {
        this.crossfireServerConnection = crossfireServerConnection;
        this.debugProtocol = debugProtocol;
    }

    /**
     * Called after the server connection has been established.
     */
    public void connected() {
        synchronized (sync) {
            connected = false;
            pendingNumLookObjects = 0;
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("connected: defaulting to pending_num_look_objects="+pendingNumLookObjects);
            }
            currentNumLookObjects = DEFAULT_NUM_LOOK_OBJECTS;
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("connected: defaulting to num_look_objects="+currentNumLookObjects);
            }
            sync.notifyAll();
        }
    }

    /**
     * Requests a change of the number of ground objects from the server.
     */
    private void negotiateNumLookObjects() {
        final int numLookObjects;
        synchronized (sync) {
            pending = false;
            numLookObjects = preferredNumLookObjects;
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("negotiateNumLookObjects: "+numLookObjects);
            }

            if (!connected) {
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("negotiateNumLookObjects: not connected, ignoring");
                }
                return;
            }
            if (pendingNumLookObjects != 0) {
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("negotiateNumLookObjects: already negotiating pending_num_look_objects="+pendingNumLookObjects+", ignoring");
                }
                return;
            }
            if (currentNumLookObjects == numLookObjects) {
                if (debugProtocol != null) {
                    debugProtocol.debugProtocolWrite("negotiateNumLookObjects: unchanged from num_look_objects="+currentNumLookObjects+", ignoring");
                }
                return;
            }
            pendingNumLookObjects = numLookObjects;
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("negotateNumLookObjects: pending_num_look_objects="+pendingNumLookObjects+", sending setup command");
            }
            sync.notifyAll();
        }
        crossfireServerConnection.sendSetup("num_look_objects "+numLookObjects);
    }

    /**
     * Called when a "setup num_look_objects" response has been received from
     * the server.
     * @param value the value of the message
     * @throws UnknownCommandException if the value cannot be parsed
     */
    public void processSetupNumLookObjects(@NotNull final String value) throws UnknownCommandException {
        if (value.equals("FALSE")) {
            System.err.println("Warning: the server is too old for this client since it does not support the num_look_objects setup option.");
            System.err.println("Expect issues with the ground view display.");
            synchronized (sync) {
                pendingNumLookObjects = 0;
                sync.notifyAll();
            }
            if (debugProtocol != null) {
                debugProtocol.debugProtocolWrite("processSetup: pending_num_look_objects=0 [server didn't understand setup command]");
            }
        } else {
            final int thisNumLookObjects;
            try {
                thisNumLookObjects = Integer.parseInt(value);
            } catch (final NumberFormatException ignored) {
                throw new UnknownCommandException("the server returned 'setup num_look_objects "+value+"'.");
            }
            final boolean negotiate;
            synchronized (sync) {
                if (pendingNumLookObjects == 0) {
                    System.err.println("the server sent an unexpected 'setup num_look_objects "+value+"'.");
                    negotiate = false;
                } else {
                    if (pendingNumLookObjects != thisNumLookObjects) {
                        System.err.println("Warning: the server didn't accept the num_look_objects setup option: requested "+pendingNumLookObjects+", returned "+thisNumLookObjects+".");
                        System.err.println("Expect issues with the ground view display.");
                    }
                    pendingNumLookObjects = 0;
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("processSetup: pending_num_look_objects="+pendingNumLookObjects+" [ok]");
                    }
                    currentNumLookObjects = thisNumLookObjects;
                    if (debugProtocol != null) {
                        debugProtocol.debugProtocolWrite("processSetup: num_look_objects="+currentNumLookObjects);
                    }
                    negotiate = currentNumLookObjects != preferredNumLookObjects;
                    if (negotiate) {
                        pending = true;
                    }
                    sync.notifyAll();
                }
            }
            if (negotiate) {
                negotiateNumLookObjects();
            }
        }
    }

    /**
     * Sets the preferred number of ground items.
     * @param preferredNumLookObjects the number of ground items
     */
    public void setPreferredNumLookObjects(final int preferredNumLookObjects) {
        synchronized (sync) {
            final int preferredNumLookObjects2 = Math.max(3, preferredNumLookObjects);
            if (this.preferredNumLookObjects == preferredNumLookObjects2) {
                return;
            }

            this.preferredNumLookObjects = preferredNumLookObjects2;
            pending = true;
        }
        negotiateNumLookObjects();
    }

    /**
     * Returns the current number of ground items.
     * @return the number of ground items
     */
    public int getCurrentNumLookObjects() {
        synchronized (sync) {
            return currentNumLookObjects;
        }
    }

    /**
     * Waits until {@link #getCurrentNumLookObjects()} is stable. This function
     * returns as soon as the negotiation with the Crossfire server is
     * complete.
     * @throws InterruptedException if the current thread was interrupted
     */
    public void waitForCurrentNumLookObjectsValid() throws InterruptedException {
        synchronized (sync) {
            while (!connected || pendingNumLookObjects != 0 || pending) {
                sync.wait();
            }
        }
    }

    /**
     * Called whenever the client socket state has changed.
     * @param clientSocketState the new client socket state
     */
    public void setClientSocketState(@NotNull final ClientSocketState clientSocketState) {
        synchronized (sync) {
            connected = clientSocketState == ClientSocketState.CONNECTED;
            sync.notifyAll();
            if (!connected) {
                return;
            }
            pending = true;
        }
        negotiateNumLookObjects();
    }

}
