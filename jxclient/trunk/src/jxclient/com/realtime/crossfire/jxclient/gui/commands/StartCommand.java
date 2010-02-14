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

package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.guistate.GuiState;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which shows the start screen.
 * @author Andreas Kirschbaum
 */
public class StartCommand implements GUICommand
{
    /**
     * The {@link GuiStateManager} to affect.
     */
    @NotNull
    private final GuiStateManager guiStateManager;

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to affect
     */
    public StartCommand(@NotNull final GuiStateManager guiStateManager)
    {
        this.guiStateManager = guiStateManager;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canExecute()
    {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void execute()
    {
        guiStateManager.changeGUI(GuiState.START);
    }
}
