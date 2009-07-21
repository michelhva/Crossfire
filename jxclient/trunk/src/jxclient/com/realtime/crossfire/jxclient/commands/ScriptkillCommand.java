//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.scripts.ScriptManager;
import com.realtime.crossfire.jxclient.scripts.ScriptProcess;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.util.Set;

/**
 * Implements the "scriptkill" command. It terminates running scripts.
 * @author Andreas Kirschbaum
 */
public class ScriptkillCommand extends AbstractCommand
{
    /**
     * The {@link ScriptManager} to use.
     */
    private final ScriptManager scriptManager;

    /**
     * Creates a new instance.
     * @param scriptManager the script manager to use
     * @param crossfireServerConnection the connection instance
     */
    public ScriptkillCommand(final ScriptManager scriptManager, final CrossfireServerConnection crossfireServerConnection)
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
    public void execute(final String args)
    {
        if (args.isEmpty())
        {
            drawInfoError("Which script to you want to kill?");
            return;
        }

        final Set<ScriptProcess> scriptProcesses = scriptManager.getScripts(args);
        if (scriptProcesses.isEmpty())
        {
            drawInfoError(scriptManager.hasScripts() ? "No matching scripts." : "No scripts running.");
            return;
        }
        if (scriptProcesses.size() > 1)
        {
            drawInfoError("More than one script matches: "+scriptProcesses+".");
            return;
        }
        final ScriptProcess scriptProcess = scriptProcesses.iterator().next();
        scriptProcess.killScript();
    }
}
