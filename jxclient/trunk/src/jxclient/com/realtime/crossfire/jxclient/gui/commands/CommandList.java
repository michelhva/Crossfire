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

package com.realtime.crossfire.jxclient.gui.commands;

import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * A list of {@link GUICommand} instances.
 * @author Andreas Kirschbaum
 */
public class CommandList {

    /**
     * The command list type.
     */
    @NotNull
    private final CommandListType commandListType;

    /**
     * The list of {@link GUICommand GUICommands} in execution order.
     */
    @NotNull
    private final Collection<GUICommand> commandList = new ArrayList<GUICommand>();

    /**
     * Creates a new instance as an empty command list.
     * @param commandListType the command list type
     */
    public CommandList(@NotNull final CommandListType commandListType) {
        this.commandListType = commandListType;
    }

    /**
     * Adds a command to the end of this command list.
     * @param guiCommand the command to add
     */
    public void add(@NotNull final GUICommand guiCommand) {
        commandList.add(guiCommand);
    }

    /**
     * Returns whether execution is possible.
     * @return whether execution is possible
     */
    private boolean canExecute() {
        switch (commandListType) {
        case AND:
            for (final GUICommand command : commandList) {
                if (!command.canExecute()) {
                    return false;
                }
            }
            break;

        case OR:
            boolean ok = false;
            for (final GUICommand command : commandList) {
                if (command.canExecute()) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                return false;
            }
            break;
        }

        return true;
    }

    /**
     * Execute the command list by calling {@link GUICommand#execute()} for each
     * command in order.
     */
    public void execute() {
        if (!canExecute()) {
            return;
        }

        for (final GUICommand command : commandList) {
            command.execute();
        }
    }

    /**
     * Returns the commands as a string.
     * @return the commands as a string
     */
    @NotNull
    public String getCommandString() {
        final StringBuilder sb = new StringBuilder();
        boolean firstCommand = true;
        for (final GUICommand guiCommand : commandList) {
            if (!(guiCommand instanceof GUICommand2)) {
                throw new AssertionError("Cannot encode command of type "+guiCommand.getClass().getName());
            }
            final String commandString = ((GUICommand2)guiCommand).getCommandString();
            if (firstCommand) {
                firstCommand = false;
            } else {
                sb.append(';');
            }
            sb.append(commandString);
        }
        return sb.toString();
    }

}
