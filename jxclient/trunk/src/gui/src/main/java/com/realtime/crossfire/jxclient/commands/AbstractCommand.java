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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for {@link Command} implementations.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractCommand implements Command {

    /**
     * The name of the command.
     */
    @NotNull
    private final String commandName;

    /**
     * The connection instance.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * Creates a new instance.
     * @param commandName the name of the command
     * @param crossfireServerConnection the connection instance
     */
    protected AbstractCommand(@NotNull final String commandName, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        this.commandName = commandName;
        this.crossfireServerConnection = crossfireServerConnection;
    }

    /**
     * Displays a regular output message.
     * @param message the message
     */
    protected void drawInfo(@NotNull final String message) {
        drawInfo(message, CrossfireDrawinfoListener.NDI_BLACK);
    }

    /**
     * Displays an error message.
     * @param message the error message
     */
    protected void drawInfoError(@NotNull final String message) {
        drawInfo(message, CrossfireDrawinfoListener.NDI_RED);
    }

    /**
     * Displays a message.
     * @param message the message
     * @param color the color code
     */
    protected void drawInfo(@NotNull final String message, final int color) {
        crossfireServerConnection.drawInfo(message, color);
    }

    @NotNull
    @Override
    public String getCommandName() {
        return commandName;
    }

    @NotNull
    @Override
    public String toString() {
        return commandName;
    }

}
