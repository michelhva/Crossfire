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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Command} instance and its arguments.
 * @author Andreas Kirschbaum
 */
public class CommandExec {

    /**
     * The command to execute or <code>null</code> to send as "ncom".
     */
    @Nullable
    private final Command command;

    /**
     * The command arguments.
     */
    @NotNull
    private final String args;

    /**
     * Creates a new instance.
     * @param command the command to execute or <code>null</code> to send as
     * "ncom"
     * @param args the command arguments
     */
    public CommandExec(@Nullable final Command command, @NotNull final String args) {
        this.command = command;
        this.args = args;
    }

    /**
     * Returns the command to execute or <code>null</code> to execute as
     * "ncom".
     * @return the command or <code>null</code>
     */
    @Nullable
    public Command getCommand() {
        return command;
    }

    /**
     * Returns the command arguments.
     * @return the command arguments
     */
    @NotNull
    public String getArgs() {
        return args;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (command == null ? 0 : command.hashCode())+args.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        final CommandExec o = (CommandExec)obj;
        return command == o.command && args.equals(o.args);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return command+"/"+args;
    }

}
