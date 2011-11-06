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

package com.realtime.crossfire.jxclient.items;

import java.util.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for listeners in {@link ItemSet} related events.
 * @author Andreas Kirschbaum
 */
public interface ItemSetListener extends EventListener {

    /**
     * A {@link CfItem} has been added.
     * @param item the added item
     */
    void itemAdded(@NotNull CfItem item);

    /**
     * A {@link CfItem}'s location has been changed.
     * @param item the moved item
     */
    void itemMoved(@NotNull CfItem item);

    /**
     * A {@link CfItem}'s attributes have been changed.
     * @param item the changed item
     */
    void itemChanged(@NotNull CfItem item);

    /**
     * A {@link CfItem} has been removed.
     * @param item the removed item
     */
    void itemRemoved(@NotNull CfItem item);

    /**
     * The player {@link CfItem} has changed.
     * @param player the new player item or <code>null</code>
     */
    void playerChanged(@Nullable CfItem player);

    /**
     * The currently opened container has changed.
     * @param tag the opened container's tag or <code>0</code>
     */
    void openContainerChanged(int tag);

}
