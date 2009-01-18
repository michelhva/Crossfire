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
package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Maintains currently running script processes.
 * @author Andreas Kirschbaum
 */
public class ScriptManager
{
    /**
     * The {@link JXCWindow} to execute in.
     */
    private final JXCWindow window;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The {@link CrossfireServerConnection} instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link Stats} instance to watch.
     */
    private final Stats stats;

    /**
     * All running {@link ScriptProcess}es.
     */
    private final Set<ScriptProcess> scriptProcesses = new HashSet<ScriptProcess>();

    /**
     * Creates a new instance.
     * @param window the window to execute in
     * @param commandQueue the command queue for sending commands
     * @param crossfireServerConnection the connection instance
     * @param stats the stats instance to watch
     */
    public ScriptManager(final JXCWindow window, final CommandQueue commandQueue, final CrossfireServerConnection crossfireServerConnection, final Stats stats)
    {
        this.window = window;
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.stats = stats;
    }

    /**
     * Creates a new script instance.
     * @param command the script command including arguments
     */
    public void newScript(final String command)
    {
        final ScriptProcess scriptProcess;
        try
        {
            scriptProcess = new ScriptProcess(command, window, commandQueue, crossfireServerConnection, stats);
        }
        catch (final IOException ex)
        {
            crossfireServerConnection.drawInfo("Unable to run script: "+ex.getMessage(), CrossfireCommandDrawinfoEvent.NDI_RED);
            return;
        }
        scriptProcesses.add(scriptProcess);
        scriptProcess.addScriptProcessListener(new ScriptProcessListener()
        {
            /** {@inheritDoc} */
            @Override
            public void scriptTerminated(final String result)
            {
                scriptProcesses.remove(scriptProcess);
                if(result == null)
                {
                    crossfireServerConnection.drawInfo("Script '"+scriptProcess.getName()+"' finished.", CrossfireCommandDrawinfoEvent.NDI_BLACK);
                }
                else
                {
                    crossfireServerConnection.drawInfo("Script '"+scriptProcess.getName()+"' failed: "+result, CrossfireCommandDrawinfoEvent.NDI_RED);
                }
            }
        });
        crossfireServerConnection.drawInfo("Script '"+scriptProcess.getName()+"' started.", CrossfireCommandDrawinfoEvent.NDI_BLACK);
        scriptProcess.start();
    }
}
