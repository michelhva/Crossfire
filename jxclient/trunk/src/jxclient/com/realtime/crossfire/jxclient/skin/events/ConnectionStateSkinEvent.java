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

import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.window.GUICommandList;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
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
     * The {@link JXCWindow} to attach to.
     */
    @NotNull
    private final JXCWindow window;

    /**
     * The {@link GuiStateListener} attached to {@link #window}.
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
        public void connecting()
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
     * @param window the window to attach to
     */
    public ConnectionStateSkinEvent(@NotNull final GUICommandList commandList, @NotNull final JXCWindow window)
    {
        this.commandList = commandList;
        this.window = window;
        window.addGuiStateListener(guiStateListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        window.removeGuiStateListener(guiStateListener);
    }
}
