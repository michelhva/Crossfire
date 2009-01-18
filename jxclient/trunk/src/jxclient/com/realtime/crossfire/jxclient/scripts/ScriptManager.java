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
import java.util.concurrent.CopyOnWriteArraySet;

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
    private final Set<ScriptProcess> scriptProcesses = new CopyOnWriteArraySet<ScriptProcess>();

    /**
     * The script ID for the next created script.
     */
    private int nextScriptId = 1;

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
            scriptProcess = new ScriptProcess(nextScriptId, command, window, commandQueue, crossfireServerConnection, stats);
        }
        catch (final IOException ex)
        {
            crossfireServerConnection.drawInfo("Unable to run script: "+ex.getMessage(), CrossfireCommandDrawinfoEvent.NDI_RED);
            return;
        }
        nextScriptId++;
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
                    crossfireServerConnection.drawInfo("Script '"+scriptProcess+"' finished.", CrossfireCommandDrawinfoEvent.NDI_BLACK);
                }
                else
                {
                    crossfireServerConnection.drawInfo("Script '"+scriptProcess+"' failed: "+result, CrossfireCommandDrawinfoEvent.NDI_RED);
                }
            }
        });
        crossfireServerConnection.drawInfo("Script '"+scriptProcess+"' started.", CrossfireCommandDrawinfoEvent.NDI_BLACK);
        scriptProcess.start();
    }

    /**
     * Returns all running scripts matching a given (partial) name or a script
     * ID.
     * @param partialScriptName the partial name or a script ID to match
     * against; an empty string matches all scripts
     * @return the matching scripts, possibly empty
     */
    public Set<ScriptProcess> getScripts(final String partialScriptName)
    {
        try
        {
            return getScriptByScriptId(Integer.parseInt(partialScriptName));
        }
        catch(final NumberFormatException ex)
        {
            return getScriptsByName(partialScriptName);
        }
    }

    /**
     * Returns all running scripts matching a given script ID.
     * @param scriptId the script ID
     * @return the matching scripts, possibly empty
     */
    private Set<ScriptProcess> getScriptByScriptId(final int scriptId)
    {
        final Set<ScriptProcess> result = new HashSet<ScriptProcess>();
        for(final ScriptProcess scriptProcess : scriptProcesses)
        {
            if(scriptProcess.getScriptId() == scriptId)
            {
                result.add(scriptProcess);
                break;
            }
        }
        return result;
    }

    /**
     * Returns all running scripts matching a given (partial) name.
     * @param partialScriptName the partial script name; an empty string
     * matches all scripts
     * @return the matching scripts, possibly empty
     */
    private Set<ScriptProcess> getScriptsByName(final String partialScriptName)
    {
        final Set<ScriptProcess> result = new HashSet<ScriptProcess>();
        for(final ScriptProcess scriptProcess : scriptProcesses)
        {
            if(scriptProcess.getFilename().contains(partialScriptName))
            {
                result.add(scriptProcess);
            }
        }
        return result;
    }

    /**
     * Returns whether at least one script is running.
     * @return whether at least one script is running
     */
    public boolean hasScripts()
    {
        return !scriptProcesses.isEmpty();
    }
}
