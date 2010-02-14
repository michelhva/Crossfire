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
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "unbind" command. It removes a key binding.
 * @author Andreas Kirschbaum
 */
public class UnbindCommand extends AbstractCommand
{
    /**
     * The {@link CommandCallback} to use.
     */
    @NotNull
    private final CommandCallback commandCallback;

    /**
     * Creates a new instance.
     * @param commandCallback the command callback to use
     * @param crossfireServerConnection the connection instance
     */
    public UnbindCommand(@NotNull final CommandCallback commandCallback, @NotNull final CrossfireServerConnection crossfireServerConnection)
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
        final CharSequence commands;
        final boolean perCharacterBinding;
        if (args.equals("-c"))
        {
            perCharacterBinding = true;
            commands = "";
        }
        else if (args.startsWith("-c "))
        {
            perCharacterBinding = true;
            commands = args.substring(3).trim();
        }
        else
        {
            perCharacterBinding = false;
            commands = args;
        }

        if (commands.length() != 0)
        {
            drawInfoError("No arguments allowed.");
            return;
        }

        if (!commandCallback.removeKeyBinding(perCharacterBinding))
        {
            drawInfoError("Cannot use unbind -c since no character is logged in.");
            return;
        }
    }
}
