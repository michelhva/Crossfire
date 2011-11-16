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

import com.realtime.crossfire.jxclient.gui.textinput.CommandExecutor;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.settings.CommandHistory;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "again" command. It repeats the previously executed command.
 * @author Andreas Kirschbaum
 */
public class AgainCommand extends AbstractCommand {

    /**
     * The {@link CommandExecutor} for executing the commands.
     */
    @NotNull
    private final CommandExecutor commandExecutor;

    /**
     * The {@link CommandHistory} for determining the command to execute.
     */
    @NotNull
    private final CommandHistory commandHistory;

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance
     * @param commandExecutor the command executor for executing the commands
     * @param commandHistory the command history for determining the command to
     * execute
     */
    public AgainCommand(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final CommandExecutor commandExecutor, @NotNull final CommandHistory commandHistory) {
        super("again", crossfireServerConnection);
        this.commandExecutor = commandExecutor;
        this.commandHistory = commandHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allArguments() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull final String args) {
        if (!args.isEmpty()) {
            drawInfoError("The 'again' command does not take any arguments.");
            return;
        }

        int index = 0;
        while (true) {
            final CharSequence command = commandHistory.last(index);
            if (command == null) {
                drawInfoError("There is no command to repeat.");
                return;
            }

            if (!command.equals("again")) {
                commandExecutor.executeCommand(command);
                return;
            }

            index++;
        }
    }

}
