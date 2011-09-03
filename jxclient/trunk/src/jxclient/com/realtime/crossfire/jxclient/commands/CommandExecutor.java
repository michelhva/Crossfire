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
import org.jetbrains.annotations.NotNull;

/**
 * Executes {@link Command Commands}.
 * @author Andreas Kirschbaum
 */
public class CommandExecutor {

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link Commands} to consider.
     */
    @NotNull
    private final Commands commands;

    /**
     * Creates a new instance.
     * @param commandQueue the command queue for sending commands
     * @param commands the commands to consider
     */
    public CommandExecutor(@NotNull final CommandQueue commandQueue, @NotNull final Commands commands) {
        this.commandQueue = commandQueue;
        this.commands = commands;
    }

    /**
     * Executes a command or a list of commands. The commands may be a client-
     * or a server-sided command.
     * @param commandLine the commands to execute
     */
    public void executeCommand(@NotNull final CharSequence commandLine) {
        final Iterable<CommandExec> commandList = CommandExpander.expand(commandLine, commands);
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
