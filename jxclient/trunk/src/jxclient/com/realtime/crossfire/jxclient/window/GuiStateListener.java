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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.server.ClientSocketState;

/**
 * Interface for listeners interested gui state changes.
 * @author Andreas Kirschbaum
 */
public interface GuiStateListener
{
    /**
     * The start screen is displayed.
     */
    void start();

    /**
     * The server selection screen is displayed.
     */
    void metaserver();

    /**
     * Connection establishment is in progress.
     */
    void connecting();

    /**
     * Connection establishment is in progress.
     * @param clientSocketState the client socket state
     */
    void connecting(final ClientSocketState clientSocketState);

    /**
     * The connection has been established.
     */
    void connected();

    /**
     * An connection attempt failed.
     * @param reason the failure reason
     */
    void connectFailed(String reason);
}
