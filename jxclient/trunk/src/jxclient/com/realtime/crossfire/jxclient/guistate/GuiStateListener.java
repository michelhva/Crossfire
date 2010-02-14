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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.guistate;

import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import org.jetbrains.annotations.NotNull;

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
     * Connection establishment is in progress. This function will be called
     * before {@link #connecting(String)}.
     * @param serverInfo the server connecting to
     */
    void preConnecting(@NotNull String serverInfo);

    /**
     * Connection establishment is in progress.
     * @param serverInfo the server connecting to
     */
    void connecting(@NotNull String serverInfo);

    /**
     * Connection establishment is in progress.
     * @param clientSocketState the client socket state
     */
    void connecting(@NotNull ClientSocketState clientSocketState);

    /**
     * The connection has been established.
     */
    void connected();

    /**
     * An connection attempt failed.
     * @param reason the failure reason
     */
    void connectFailed(@NotNull String reason);
}
