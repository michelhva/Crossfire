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

import com.realtime.crossfire.jxclient.gui.command.CommandList;
import com.realtime.crossfire.jxclient.server.CrossfireMagicmapListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SkinEvent} that executes a {@link CommandList} whenever a
 * magicmap protocol command is received.
 * @author Andreas Kirschbaum
 */
public class CrossfireMagicmapSkinEvent implements SkinEvent
{
    /**
     * The {@link CommandList} to execute.
     */
    @NotNull
    private final CommandList commandList;

    /**
     * The {@link CrossfireServerConnection} for tracking magicmap commands.
     */
    @NotNull
    private final CrossfireServerConnection server;

    /**
     * The {@link CrossfireMagicmapListener} attached to {@link #server}.
     */
    @NotNull
    private final CrossfireMagicmapListener crossfireMagicmapListener = new CrossfireMagicmapListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandMagicmapReceived(final int width, final int height, final int px, final int py, @NotNull final byte[] data, final int pos)
        {
            commandList.execute();
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param server the connection to attach to
     */
    public CrossfireMagicmapSkinEvent(@NotNull final CommandList commandList, @NotNull final CrossfireServerConnection server)
    {
        this.commandList = commandList;
        this.server = server;
        server.addCrossfireMagicmapListener(crossfireMagicmapListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        server.removeCrossfireMagicmapListener(crossfireMagicmapListener);
    }
}
