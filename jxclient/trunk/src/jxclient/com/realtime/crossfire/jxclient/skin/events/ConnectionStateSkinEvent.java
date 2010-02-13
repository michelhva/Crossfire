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

package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.gui.command.GUICommandList;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.GuiStateManager;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} at connection
 * setup.
 * @author Andreas Kirschbaum
 */
public class ConnectionStateSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    @NotNull
    private final GUICommandList commandList;

    /**
     * The {@link GuiStateManager} to watch.
     */
    @NotNull
    private final GuiStateManager guiStateManager;

    /**
     * The {@link GuiStateListener} attached to {@link #guiStateManager}.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            commandList.execute();
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param guiStateManager the gui state manager to watch
     */
    public ConnectionStateSkinEvent(@NotNull final GUICommandList commandList, @NotNull final GuiStateManager guiStateManager)
    {
        this.commandList = commandList;
        this.guiStateManager = guiStateManager;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        guiStateManager.removeGuiStateListener(guiStateListener);
    }
}
