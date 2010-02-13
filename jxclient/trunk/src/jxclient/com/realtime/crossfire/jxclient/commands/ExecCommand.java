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

import com.realtime.crossfire.jxclient.gui.command.GUICommandList;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.skin.skin.JXCSkinException;
import com.realtime.crossfire.jxclient.window.GuiManager;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "exec" command. It runs a skin command.
 * @author Andreas Kirschbaum
 */
public class ExecCommand extends AbstractCommand
{
    /**
     * The {@link GuiManager} for looking up commands.
     */
    @NotNull
    private final GuiManager guiManager;

    /**
     * Creates a new instance.
     * @param guiManager the gui manager for looking up commands
     * @param crossfireServerConnection the connection instance
     */
    public ExecCommand(@NotNull final GuiManager guiManager, @NotNull final CrossfireServerConnection crossfireServerConnection)
    {
        super(crossfireServerConnection);
        this.guiManager = guiManager;
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

        final GUICommandList commandList;
        try
        {
            commandList = guiManager.getSkin().getCommandList(args);
        }
        catch (final JXCSkinException ex)
        {
            drawInfoError(ex.getMessage());
            return;
        }
        commandList.execute();
    }
}
