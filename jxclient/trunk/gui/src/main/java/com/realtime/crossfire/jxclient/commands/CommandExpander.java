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

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Expands a command (or list of commands) into a sequence of {@link CommandExec
 * Commands} to execute.
 * @author Andreas Kirschbaum
 */
public class CommandExpander {

    /**
     * The {@link Pattern} for splitting command sequences.
     */
    @NotNull
    private static final Pattern PATTERN_SEPARATOR = Pattern.compile(" *; *");

    /**
     * The {@link Pattern} for splitting commands from command arguments.
     */
    @NotNull
    private static final Pattern PATTERN_SPACES = Pattern.compile(" +");

    /**
     * Private constructor to prevent instantiation.
     */
    private CommandExpander() {
    }

    /**
     * Expands a command list into a sequence of {@link CommandExec Commands} to
     * execute.
     * @param commandList the commands to split
     * @param commands the commands to use
     * @return the list of commands
     */
    public static Collection<CommandExec> expand(@NotNull final CharSequence commandList, @NotNull final Commands commands) {
        final Collection<CommandExec> list = new ArrayList<CommandExec>();
        CharSequence remainingCommandList = commandList;
        while (true) {
            final String[] tmp = PATTERN_SEPARATOR.split(remainingCommandList, 2);
            final String commandSpec = tmp[0];
            if (!commandSpec.isEmpty()) {
                final String[] tmp2 = PATTERN_SPACES.split(commandSpec, 2);
                final String commandName = tmp2[0];
                final String commandArgs = tmp2.length == 2 ? tmp2[1] : "";
                final Command command = commands.findCommand(commandName);
                if (command == null) {
                    list.add(new CommandExec(null, commandSpec));
                } else if (command.allArguments()) {
                    final String[] tmp3 = PATTERN_SPACES.split(remainingCommandList, 2);
                    list.add(new CommandExec(command, tmp3.length == 2 ? tmp3[1] : ""));
                    break;
                } else {
                    list.add(new CommandExec(command, commandArgs));
                }
            }
            if (tmp.length < 2) {
                break;
            }
            remainingCommandList = tmp[1];
        }
        return list;
    }

}
