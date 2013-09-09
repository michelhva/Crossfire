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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.settings;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating {@link CommandHistory} instances by name.
 * @author Andreas Kirschbaum
 */
public class CommandHistoryFactory {

    /**
     * All defined {@link CommandHistory} instances. Maps identification string
     * to instance.
     */
    @NotNull
    private final Map<String, CommandHistory> commandHistories = new HashMap<String, CommandHistory>();

    /**
     * Returns a {@link CommandHistory} instance by name. Calling this function
     * twice for the same name returns the same instance.
     * @param ident the name
     * @return the command history instance
     */
    @NotNull
    public CommandHistory getCommandHistory(@NotNull final String ident) {
        final CommandHistory existingCommandHistory = commandHistories.get(ident);
        if (existingCommandHistory != null) {
            return existingCommandHistory;
        }

        final CommandHistory commandHistory = new CommandHistory(ident);
        commandHistories.put(ident, commandHistory);
        return commandHistory;
    }

}
