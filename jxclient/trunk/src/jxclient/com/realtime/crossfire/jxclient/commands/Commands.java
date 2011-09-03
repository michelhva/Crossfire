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

import com.realtime.crossfire.jxclient.queue.CommandQueue;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parses and executes client-side commands.
 * @author Andreas Kirschbaum
 */
public class Commands {

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * Maps command name to {@link Command} instance.
     */
    @NotNull
    private final Map<String, Command> commands = new HashMap<String, Command>();

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for sending commands
     */
    public Commands(@NotNull final CommandQueue commandQueue) {
        this.commandQueue = commandQueue;
    }

    /**
     * Adds an executable {@link Command}.
     * @param command the command to add
     */
    public void addCommand(@NotNull final Command command) {
        if (commands.put(command.getCommandName(), command) != null) {
            throw new IllegalArgumentException("duplicate command: "+command.getCommandName());
        }
    }

    /**
     * Returns a {@link Command} by name.
     * @param commandName the command name to search
     * @return the command or <code>null</code> if <code>commandName</code> is
     *         undefined
     */
    @Nullable
    public Command findCommand(@NotNull final String commandName) {
        return commands.get(commandName.toLowerCase());
    }

    /**
     * Executes a command or a list of commands. The commands may be a client-
     * or a server-sided command.
     * @param commandLine the commands to execute
     */
    public void executeCommand(@NotNull final CharSequence commandLine) {
        final Iterable<CommandExec> commandList = CommandExpander.expand(commandLine, this);
        for (final CommandExec commandExec : commandList) {
            final Command command = commandExec.getCommand();
            if (command == null) {
                commandQueue.sendNcom(false, commandExec.getArgs());
            } else {
                command.execute(commandExec.getArgs());
            }
        }
    }

}
