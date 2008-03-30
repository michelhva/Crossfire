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

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;

/**
 * Abstract base class of commands.
 *
 * @author Andreas Kirschbaum
 */
public abstract class AbstractCommand implements Command
{
    /**
     * The window to execute in.
     */
    private final JXCWindow window;

    /**
     * Create a new instance.
     *
     * @param window The window to execute in.
     */
    protected AbstractCommand(final JXCWindow window)
    {
        this.window = window;
    }

    /**
     * Display an error message.
     *
     * @param message The error message.
     */
    protected void drawInfoError(final String message)
    {
        drawInfo(message, CrossfireCommandDrawinfoEvent.NDI_RED);
    }

    /**
     * Display a message.
     *
     * @param message The message.
     *
     * @param color The color code.
     */
    protected void drawInfo(final String message, final int color)
    {
        window.getCrossfireServerConnection().drawInfo(message, color);
    }

    /**
     * Return the associated window.
     *
     * @return The window.
     */
    protected JXCWindow getWindow()
    {
        return window;
    }
}
