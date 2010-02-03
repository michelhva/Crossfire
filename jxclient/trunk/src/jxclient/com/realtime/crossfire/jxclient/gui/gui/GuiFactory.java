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

package com.realtime.crossfire.jxclient.gui.gui;

import com.realtime.crossfire.jxclient.commands.Commands;
import com.realtime.crossfire.jxclient.commands.Macros;
import com.realtime.crossfire.jxclient.window.GuiManager;
import com.realtime.crossfire.jxclient.window.MouseTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for creating {@link Gui} instances.
 * @author Andreas Kirschbaum
 */
public class GuiFactory
{
    /**
     * The mouse tracker when in debug GUI mode or <code>null</code> otherwise.
     */
    @Nullable
    private final MouseTracker mouseTracker;

    /**
     * The commands instance for executing commands.
     */
    @NotNull
    private final Commands commands;

    /**
     * The gui manager to use.
     */
    @NotNull
    private final GuiManager guiManager;

    /**
     * The {@link Macros} instance to use.
     */
    @NotNull
    private final Macros macros;

    /**
     * Creates a new instance.
     * @param mouseTracker the mouse tracker when in debug GUI mode or
     * <code>null</code> otherwise
     * @param commands the commands instance for executing commands
     * @param guiManager the gui manager to use
     * @param macros the macros instance to use
     */
    public GuiFactory(@Nullable final MouseTracker mouseTracker, @NotNull final Commands commands, @NotNull final GuiManager guiManager, @NotNull final Macros macros)
    {
        this.mouseTracker = mouseTracker;
        this.commands = commands;
        this.guiManager = guiManager;
        this.macros = macros;
    }

    /**
     * Creates a new {@link Gui} instance.
     * @return the new gui instance
     */
    @NotNull
    public Gui newGui()
    {
        return new Gui(mouseTracker, commands, guiManager, macros);
    }
}
