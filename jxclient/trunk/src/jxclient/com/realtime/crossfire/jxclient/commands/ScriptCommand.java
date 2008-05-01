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

import com.realtime.crossfire.jxclient.scripts.ScriptProcess;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.io.IOException;

/**
 * Implements a "script" command. It runs a new script.
 *
 * @author Andreas Kirschbaum
 */
public class ScriptCommand extends AbstractCommand
{
    /**
     * The connection instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * Create a new instance.
     *
     * @param window The window to execute in.
     *
     * @param crossfireServerConnection the connection instance
     */
    protected ScriptCommand(final JXCWindow window, final CrossfireServerConnection crossfireServerConnection)
    {
        super(window, crossfireServerConnection);
        this.crossfireServerConnection = crossfireServerConnection;
    }

    /** {@inheritDoc} */
    public boolean allArguments()
    {
        return false;
    }

    /** {@inheritDoc} */
    public void execute(final String args)
    {
        if (args.length() == 0)
        {
            drawInfoError("Which script to you want to run?");
            return;
        }

        try
        {
            final ScriptProcess scriptProcess = new ScriptProcess(args, getWindow(), crossfireServerConnection);
            // XXX: store scriptProcess
        }
        catch (final IOException ex)
        {
            drawInfoError("Unable to run script: "+ex.getMessage());
        }
    }
}
