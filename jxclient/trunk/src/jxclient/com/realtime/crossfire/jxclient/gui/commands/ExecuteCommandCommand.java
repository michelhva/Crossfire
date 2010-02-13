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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.gui.command.GUICommand;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which executes a Crossfire command.
 * @author Andreas Kirschbaum
 */
public class ExecuteCommandCommand implements GUICommand
{
    /** The commands instance for executing the command. */
    @NotNull
    private final Commands commands;

    /** The command to execute. */
    @NotNull
    private final String command;

    /**
     * The {@link Macros} instance to use.
     */
    @NotNull
    private final Macros macros;

    /**
     * Creates a new instance.
     * @param commands the commands instance for executing the command
     * @param command the command to execute
     * @param macros the macros instance to use
     */
    public ExecuteCommandCommand(@NotNull final Commands commands, @NotNull final String command, @NotNull final Macros macros)
    {
        this.commands = commands;
        this.command = command;
        this.macros = macros;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canExecute()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void execute()
    {
        commands.executeCommand(macros.expandMacros(command));
    }

    /**
     * Returns the command to execute.
     * @return the command to execute
     */
    @NotNull
    public String getCommand()
    {
        return command;
    }
}
