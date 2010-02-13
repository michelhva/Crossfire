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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.command;

import com.realtime.crossfire.jxclient.gui.commands.ActivateCommandInputCommand;
import com.realtime.crossfire.jxclient.gui.commands.ExecuteCommandCommand;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * A list of {@link GUICommand} instances.
 *
 * @author Andreas Kirschbaum
 */
public class GUICommandList
{
    /**
     * The command list type.
     */
    @NotNull
    private final CommandType commandType;

    /**
     * The list of {@link GUICommand}s in execution order.
     */
    @NotNull
    private final Collection<GUICommand> commandList = new ArrayList<GUICommand>();

    /**
     * Create a new instance as an empty command list.
     *
     * @param commandType The command list type.
     */
    public GUICommandList(@NotNull final CommandType commandType)
    {
        this.commandType = commandType;
    }

    /**
     * Add a command to the end of this command list.
     *
     * @param guiCommand The command to add.
     */
    public void add(@NotNull final GUICommand guiCommand)
    {
        commandList.add(guiCommand);
    }

    /**
     * Returns whether execution is possible.
     * @return whether execution is possible
     */
    public boolean canExecute()
    {
        switch (commandType)
        {
        case AND:
            for (final GUICommand command : commandList)
            {
                if (!command.canExecute())
                {
                    return false;
                }
            }
            break;

        case OR:
            boolean ok = false;
            for (final GUICommand command : commandList)
            {
                if (command.canExecute())
                {
                    ok = true;
                    break;
                }
            }
            if (!ok)
            {
                return false;
            }
            break;
        }

        return true;
    }

    /**
     * Execute the command list by calling {@link GUICommand#execute()} for
     * each command in order.
     */
    public void execute()
    {
        if (!canExecute())
        {
            return;
        }

        for (final GUICommand command : commandList)
        {
            command.execute();
        }
    }

    /**
     * Return the commands as a string.
     *
     * @return The commands as a string.
     */
    @NotNull
    public String getCommandString()
    {
        final StringBuilder sb = new StringBuilder();
        boolean firstCommand = true;
        for (final Object guiCommand : commandList)
        {
            final String commandString;
            if (guiCommand instanceof ExecuteCommandCommand)
            {
                commandString = ((ExecuteCommandCommand)guiCommand).getCommand();
            }
            else if (guiCommand instanceof ActivateCommandInputCommand)
            {
                final String commandText = ((ActivateCommandInputCommand)guiCommand).getCommandText();
                commandString = commandText.length() > 0 ? "-e "+commandText : "-e";
            }
            else
            {
                throw new AssertionError("Cannot encode command of type "+guiCommand.getClass().getName());
            }

            if (firstCommand)
            {
                firstCommand = false;
            }
            else
            {
                sb.append(';');
            }
            sb.append(commandString);
        }
        return sb.toString();
    }
}
