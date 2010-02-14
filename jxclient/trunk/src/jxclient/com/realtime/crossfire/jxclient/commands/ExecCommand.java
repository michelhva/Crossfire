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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.gui.commands.CommandCallback;
import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.commands.NoSuchCommandException;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "exec" command. It runs a skin command.
 * @author Andreas Kirschbaum
 */
public class ExecCommand extends AbstractCommand
{
    /**
     * The {@link CommandCallback} to lookup commands.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * Creates a new instance.
     * @param commandCallback the command callback to lookup commands
     * @param crossfireServerConnection the connection instance
     */
    public ExecCommand(@NotNull final CommandCallback commandCallback, @NotNull final CrossfireServerConnection crossfireServerConnection)
    {
        super(crossfireServerConnection);
        this.commandCallback = commandCallback;
    }

    /** {@inheritDoc} */
    @Override
    public boolean allArguments()
    {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(@NotNull final String args)
    {
        if (args.length() == 0)
        {
            drawInfoError("Which command do you want to run?");
            return;
        }

        final CommandList commandList;
        try
        {
            commandList = commandCallback.getCommandList(args);
        }
        catch (final NoSuchCommandException ex)
        {
            drawInfoError(ex.getMessage());
            return;
        }
        commandList.execute();
    }
}
