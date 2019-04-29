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

package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Shortcut} that executes a Crossfire command.
 * @author Andreas Kirschbaum
 */
public class ShortcutCommand extends Shortcut {

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The command to execute.
     */
    @NotNull
    private final String command;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for sending commands
     * @param command the command to execute
     */
    public ShortcutCommand(@NotNull final CommandQueue commandQueue, @NotNull final String command) {
        this.commandQueue = commandQueue;
        this.command = command;
    }

    /**
     * Returns the command to execute.
     * @return the command
     */
    @NotNull
    public String getCommand() {
        return command;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void execute() {
        commandQueue.sendNcom(false, command);
    }

    @NotNull
    @Override
    public String getTooltipText() {
        return command;
    }

    @Override
    public void visit(@NotNull final ShortcutVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean displaysFace(final Face face) {
        return false;
    }

}
