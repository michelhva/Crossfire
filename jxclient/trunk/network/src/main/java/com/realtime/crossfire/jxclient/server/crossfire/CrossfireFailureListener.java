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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.server.crossfire;

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for listeners interested in the "failure" messages received from
 * the Crossfire server.
 * @author Nicolas Weeger
 */
public interface CrossfireFailureListener extends EventListener {

    /**
     * Command failure received.
     * @param command command which generated the failure, only the command
     * itself without any parameter.
     * @param arguments human-readable message, though the format depends on the
     * command and the context.
     */
    void failure(@NotNull String command, @NotNull String arguments);

    /**
     * Failure is not relevant anymore, clean it.
     */
    void clearFailure();
}
