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

package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Command} for regression tests.
 * @author Andreas Kirschbaum
 */
public class TestCommand extends AbstractCommand {

    /**
     * Whether all remaining commands should be included as arguments.
     */
    private final boolean allArguments;

    /**
     * Creates a new instance.
     * @param commandName the name of the command
     * @param allArguments whether all remaining commands should be included as
     * arguments
     * @param crossfireServerConnection the connection instance
     */
    public TestCommand(@NotNull final String commandName, final boolean allArguments, @NotNull final CrossfireServerConnection crossfireServerConnection) {
        super(commandName, crossfireServerConnection);
        this.allArguments = allArguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allArguments() {
        return allArguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull final String args) {
        throw new AssertionError();
    }
}
