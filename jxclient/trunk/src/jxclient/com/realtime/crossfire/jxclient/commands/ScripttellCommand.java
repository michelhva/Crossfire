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

import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.scripts.ScriptProcess;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "scripttell" command. It sends a "scripttell" command to a
 * script.
 * @author Andreas Kirschbaum
 */
public class ScripttellCommand extends AbstractCommand
{
    /**
     * The {@link ScriptManager} to use.
     */
    @NotNull
    private final ScriptManager scriptManager;

    /**
     * Creates a new instance.
     * @param scriptManager the script manager to use
     * @param crossfireServerConnection the connection instance
     */
    public ScripttellCommand(@NotNull final ScriptManager scriptManager, @NotNull final CrossfireServerConnection crossfireServerConnection)
    {
        super(crossfireServerConnection);
        this.scriptManager = scriptManager;
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
        if (args.isEmpty())
        {
            drawInfoError("Which script do you want to talk to?");
            return;
        }

        final String[] tmp = args.split(" +", 2);
        final Collection<ScriptProcess> scriptProcesses = scriptManager.getScripts(tmp[0]);
        if (scriptProcesses.isEmpty())
        {
            drawInfoError(scriptManager.hasScripts() ? "No matching scripts." : "No scripts running.");
            return;
        }

        if (tmp.length < 2)
        {
            drawInfoError("What do you want to tell the script?");
            return;
        }

        final String cmd = "scripttell "+tmp[1];
        for (final ScriptProcess scriptProcess : scriptProcesses)
        {
            scriptProcess.commandSent(cmd);
        }
    }
}
