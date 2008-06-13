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
package com.realtime.crossfire.jxclient.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of previously entered commands.
 * @author Andreas Kirschbaum
 */
public class CommandHistory
{
    /**
     * The maximum number of commands to store.
     */
    private static final int HISTORY_SIZE = 100;

    /**
     * The commands.
     */
    private final List<String> commands = new ArrayList<String>(HISTORY_SIZE);

    /**
     * The current command index.
     */
    private int commandIndex = 0;

    /**
     * Creates a new instance.
     * @param ident the identification string for backing up to file
     */
    public CommandHistory(final String ident)
    {
    }

    /**
     * Adds a new command.
     * @param command the command to add
     */
    public void addCommand(final String command)
    {
        final String trimmedCommand = command.trim();
        if (trimmedCommand.length() <= 0)
        {
            return;
        }
        removeCommand(trimmedCommand);
        commands.add(trimmedCommand);
        commandIndex = commands.size();
        trimToMaxSize();
    }

    /**
     * Cut off old entries if more than {@link #HISTORY_SIZE} commands are
     * present.
     */
    private void trimToMaxSize()
    {
        while (commands.size() > HISTORY_SIZE)
        {
            commands.remove(0);
            if (commandIndex > 0)
            {
                commandIndex--;
            }
        }
    }

    /**
     * Removes a command.
     * @param command the command to remove
     */
    private void removeCommand(final String command)
    {
        final Iterator<String> it = commands.iterator();
        while (it.hasNext())
        {
            final String oldCommand = it.next();
            if (oldCommand.equals(command))
            {
                it.remove();
                break;
            }
        }
    }

    /**
     * Returns the previous command.
     * @return the previous command or <code>null</code> if no previous command
     * exists
     */
    public String up()
    {
        return 1 <= commandIndex && commandIndex <= commands.size() ? commands.get(--commandIndex) : null;
    }

    /**
     * Returns the next command.
     * @return the next command or <code>null</code> if no previous command
     * exists
     */
    public String down()
    {
        if (commandIndex < commands.size())
        {
            commandIndex++;
        }
        return commandIndex < commands.size() ? commands.get(commandIndex) : null;
    }
}
