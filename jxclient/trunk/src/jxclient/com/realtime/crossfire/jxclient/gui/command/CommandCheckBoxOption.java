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

package com.realtime.crossfire.jxclient.gui.command;

import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link CheckBoxOption} that executes {@link CommandList}s when
 * checked/unchecked.
 *
 * @author Andreas Kirschbaum
 */
public class CommandCheckBoxOption extends CheckBoxOption
{
    /**
     * The command list to execute when checked.
     */
    @NotNull
    private final CommandList commandOn;

    /**
     * The command list to execute when unchecked.
     */
    @NotNull
    private final CommandList commandOff;

    /**
     * Create a new instance.
     *
     * @param commandOn The command list to execute when checked.
     *
     * @param commandOff The command list to execute when unchecked.
     */
    public CommandCheckBoxOption(@NotNull final CommandList commandOn, @NotNull final CommandList commandOff)
    {
        this.commandOn = commandOn;
        this.commandOff = commandOff;
    }

    /** {@inheritDoc} */
    @Override
    protected void execute(final boolean checked)
    {
        if (checked)
        {
            commandOn.execute();
        }
        else
        {
            commandOff.execute();
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDefaultChecked()
    {
        return true;
    }
}
