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

import com.realtime.crossfire.jxclient.gui.log.MessageBufferUpdater;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

/**
 * Implements the "debug_colors" command. It prints text to the log window
 * using different colors.
 * @author Andreas Kirschbaum
 */
public class DebugColorsCommand extends AbstractCommand
{
    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance
     */
    public DebugColorsCommand(final CrossfireServerConnection crossfireServerConnection)
    {
        super(crossfireServerConnection);
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
            drawInfoError("The debug_colors commands does not take arguments.");
            return;
        }

        for (int color = 0; color < 16; color++)
        {
            drawInfo("This line is color #"+color+" ("+MessageBufferUpdater.getColorName(color)+").", color);
        }
    }
}
