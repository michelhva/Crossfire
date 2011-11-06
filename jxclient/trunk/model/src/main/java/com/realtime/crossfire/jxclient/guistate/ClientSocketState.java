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

package com.realtime.crossfire.jxclient.guistate;

import com.realtime.crossfire.jxclient.server.socket.ClientSocket;

/**
 * Connection progress states of the {@link ClientSocket}. The state transitions
 * in the declared order.
 * @author Andreas Kirschbaum
 */
public enum ClientSocketState {

    /**
     * The socket is being established.
     */
    CONNECTING,

    /**
     * "version" protocol commands are being exchanged.
     */
    VERSION,

    /**
     * "setup" protocol commands are being exchanged.
     */
    SETUP,

    /**
     * "requestinfo" protocol commands are being exchanged.
     */
    REQUESTINFO,

    /**
     * "addme" protocol commands are being exchanged.
     */
    ADDME,

    /**
     * The client is connected, and account information is being exchanged.
     */
    ACCOUNT_INFO,

    /**
     * Connection is fully established.
     */
    CONNECTED,

    /**
     * A connection could not be established.
     */
    CONNECT_FAILED

}
