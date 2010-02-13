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

import com.realtime.crossfire.jxclient.gui.command.GUICommand;
import com.realtime.crossfire.jxclient.gui.list.GUIList;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUICommand} which scrolls a {@link GUIList} instance.
 * @author Andreas Kirschbaum
 */
public class ScrollListCommand implements GUICommand
{
    /**
     * The list to scroll.
     */
    @NotNull
    private final GUIList list;

    /**
     * The distance to scroll.
     */
    private final int distance;

    /**
     * Creates a new instance.
     * @param list the list to scroll
     * @param distance the distance to scroll
     */
    public ScrollListCommand(@NotNull final GUIList list, final int distance)
    {
        this.list = list;
        this.distance = distance;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canExecute()
    {
        return list.canScroll(distance);
    }

    /** {@inheritDoc} */
    @Override
    public void execute()
    {
        list.scroll(distance);
    }
}
