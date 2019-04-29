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

import com.realtime.crossfire.jxclient.gui.log.Buffer;
import com.realtime.crossfire.jxclient.gui.misc.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the command "clear". It clears the active message window.
 * @author Andreas Kirschbaum
 */
public class ClearCommand extends AbstractCommand {

    /**
     * The {@link JXCWindowRenderer} to affect.
     */
    @NotNull
    private final JXCWindowRenderer windowRenderer;

    /**
     * Creates a new instance.
     * @param windowRenderer the window renderer to affect
     * @param crossfireServerConnection the connection instance
     */
    public ClearCommand(@NotNull final JXCWindowRenderer windowRenderer, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        super("clear", crossfireServerConnection);
        this.windowRenderer = windowRenderer;
    }

    @Override
    public boolean allArguments() {
        return false;
    }

    @Override
    public void execute(@NotNull final String args) {
        if (!args.isEmpty()) {
            drawInfoError("The clear command does not take arguments.");
            return;
        }

        final Buffer buffer = windowRenderer.getActiveMessageBuffer();
        if (buffer == null) {
            drawInfoError("No active text window.");
            return;
        }

        buffer.clear();
    }

}
