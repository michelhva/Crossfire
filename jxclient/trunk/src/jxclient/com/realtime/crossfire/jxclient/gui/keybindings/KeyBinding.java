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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for key bindings. A key binding consists of information
 * about the bound key and an associated {@link CommandList}.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public abstract class KeyBinding
{
    /**
     * The associated {@link CommandList}.
     */
    @NotNull
    private final CommandList commands;

    /**
     * Whether the key binding is a "default" binding which should not be
     * saved.
     */
    private final boolean isDefault;

    /**
     * Returns the associated {@link CommandList}.
     * @return the associated command list
     */
    @NotNull
    public CommandList getCommands()
    {
        return commands;
    }

    /**
     * Creates a new instance.
     * @param commands the commands to execute
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     */
    protected KeyBinding(@NotNull final CommandList commands, final boolean isDefault)
    {
        this.commands = commands;
        this.isDefault = isDefault;
    }

    /** {@inheritDoc} */
    @Override
    public abstract boolean equals(@Nullable final Object obj);

    /** {@inheritDoc} */
    @Override
    public abstract int hashCode();

    /**
     * Checks whether this key binding matches a key code/modifiers pair.
     * @param keyCode the key code to check
     * @param modifiers the modifiers to check
     * @return whether the key binding matches the parameters
     */
    public abstract boolean matchesKeyCode(final int keyCode, final int modifiers);

    /**
     * Check whether this key binding matches a key character.
     * @param keyChar the key character to check
     * @return whether the key binding matches the parameters
     */
    public abstract boolean matchesKeyChar(final char keyChar);

    /**
     * Returns the commands as a string.
     * @return the commands as a string
     */
    @NotNull
    public String getCommandString()
    {
        return commands.getCommandString();
    }

    /**
     * Returns whether the key binding is a "default" binding which should not
     * be saved.
     * @return <code>true</code>=do not save
     */
    public boolean isDefault()
    {
        return isDefault;
    }
}
