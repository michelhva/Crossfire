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

package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.map.MapScrollListener;
import com.realtime.crossfire.jxclient.map.MapUpdaterState;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SkinEvent} that executes a {@link CommandList} whenever the map
 * scroll protocol command is received.
 * @author Andreas Kirschbaum
 */
public class MapScrollSkinEvent implements SkinEvent {

    /**
     * The {@link CommandList} to execute.
     */
    @NotNull
    private final CommandList commandList;

    /**
     * The {@link MapUpdaterState} to attach to.
     */
    @NotNull
    private final MapUpdaterState mapUpdaterState;

    /**
     * The {@link MapScrollListener} attached to {@link #mapUpdaterState}.
     */
    @NotNull
    private final MapScrollListener mapscrollListener = new MapScrollListener() {

        @Override
        public void mapScrolled(final int dx, final int dy) {
            commandList.execute();
        }

    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param mapUpdaterState the map updater state to attach to
     */
    public MapScrollSkinEvent(@NotNull final CommandList commandList, @NotNull final MapUpdaterState mapUpdaterState) {
        this.commandList = commandList;
        this.mapUpdaterState = mapUpdaterState;
        this.mapUpdaterState.addCrossfireMapScrollListener(mapscrollListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        mapUpdaterState.removeCrossfireMapScrollListener(mapscrollListener);
    }

}
