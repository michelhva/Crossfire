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
package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.JXCWindow;

/**
 * A {@link Shortcut} that executes a Crossfire command.
 *
 * @author Andreas Kirschbaum
 */
public class ShortcutCommand extends Shortcut
{
     /**
     * The window to cast the spell in.
     */
    private final JXCWindow jxcWindow;

    /**
     * The command to execute.
     */
    private String command;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window to execute in.
     *
     * @param command The command to execute.
     */
    public ShortcutCommand(final JXCWindow jxcWindow, final String command)
    {
        this.jxcWindow = jxcWindow;
        this.command = command;
    }

    /**
     * Return the command to execute.
     *
     * @return The command.
     */
    public String getCommand()
    {
        return command;
    }

    /** {@inheritDoc} */
    public void execute()
    {
        jxcWindow.getCommandQueue().sendNcom(command);
    }
}
