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

package com.realtime.crossfire.jxclient.gui.commandlist;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating {@link GUICommand} instances from string
 * representation.
 * @author Andreas Kirschbaum
 */
public interface GUICommandFactory {

    /**
     * Creates a new {@link GUICommand} instance from string representation.
     * @param encodedCommandString the command string representation
     * @return the new command instance
     */
    GUICommand createCommandDecode(@NotNull String encodedCommandString);

    /**
     * Creates a new {@link GUICommand} instance from string representation.
     * @param commandString the command string representation
     * @return the new command instance
     */
    @NotNull
    GUICommand createCommand(@NotNull final String commandString);

    /**
     * Encodes a key binding if necessary.
     * @param command the key binding
     * @return the encoded key binding
     */
    @NotNull
    String encode(@NotNull final String command);

    /**
     * Decodes a key binding if necessary.
     * @param command the key binding
     * @return the decoded key binding
     */
    @NotNull
    String decode(@NotNull final String command);

}
