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
import com.realtime.crossfire.jxclient.mapupdater.CfMapUpdater;
import com.realtime.crossfire.jxclient.mapupdater.MapscrollListener;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SkinEvent} that executes a {@link GUICommandList} whenever the
 * map scroll protocol command is received.
 * @author Andreas Kirschbaum
 */
public class MapscrollSkinEvent implements SkinEvent
{
    /**
     * The {@link GUICommandList} to execute.
     */
    @NotNull
    private final GUICommandList commandList;

    /**
     * The {@link CfMapUpdater} to attach to.
     */
    @NotNull
    private final CfMapUpdater mapUpdater;

    /**
     * The {@link MapscrollListener} attached to {@link #mapUpdater}.
     */
    @NotNull
    private final MapscrollListener mapscrollListener = new MapscrollListener()
    {
        /** {@inheritDoc} */
        @Override
        public void mapScrolled(final int dx, final int dy)
        {
            commandList.execute();
        }
    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param mapUpdater the map updater to attach to
     */
    public MapscrollSkinEvent(@NotNull final GUICommandList commandList, @NotNull final CfMapUpdater mapUpdater)
    {
        this.commandList = commandList;
        this.mapUpdater = mapUpdater;
        mapUpdater.addCrossfireMapscrollListener(mapscrollListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        mapUpdater.removeCrossfireMapscrollListener(mapscrollListener);
    }
}
