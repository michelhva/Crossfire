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

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in drawextinfo messages received from the
 * Crossfire server.
 * @author Lauwenmark
 */
public interface CrossfireDrawextinfoListener extends EventListener {

    /**
     * A drawextinfo message has been received.
     * @param color the message color
     * @param type the message type
     * @param subtype the message subtype
     * @param message the message
     */
    void commandDrawextinfoReceived(int color, int type, int subtype, @NotNull String message);

    /**
     * Enables or disables printing of message types.
     * @param printMessageTypes whether to enable or disable message types
     */
    void setDebugMode(boolean printMessageTypes);

}
