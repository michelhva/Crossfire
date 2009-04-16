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

import com.realtime.crossfire.jxclient.gui.log.Buffer;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;

/**
 * Implements the command "clear". It clears all text windows.
 * @author Andreas Kirschbaum
 */
public class ClearCommand extends AbstractCommand
{
    /**
     * The {@link JXCWindowRenderer} to affect.
     */
    private final JXCWindowRenderer windowRenderer;

    /**
     * The {@link Buffer} instances to clear.
     */
    private final Buffer[] buffers;

    /**
     * Creates a new instance.
     * @param windowRenderer the window renderer to affect
     * @param crossfireServerConnection the connection instance
     * @param buffers the buffers to clear
     */
    protected ClearCommand(final JXCWindowRenderer windowRenderer, final CrossfireServerConnection crossfireServerConnection, final Buffer... buffers)
    {
        super(crossfireServerConnection);
        this.windowRenderer = windowRenderer;
        this.buffers = buffers.clone();
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
        if (args.length() != 0)
        {
            drawInfoError("The clear command does not take arguments.");
            return;
        }

        final Buffer buffer = windowRenderer.getActiveMessageBuffer();
        if (buffer == null)
        {
            drawInfoError("No active text window.");
            return;
        }

        buffer.clear();
    }
}
