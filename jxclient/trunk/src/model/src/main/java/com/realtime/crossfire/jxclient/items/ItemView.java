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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A list of {@link CfItem CfItems}.
 * @author Andreas Kirschbaum
 */
public interface ItemView {

    /**
     * Returns the number of items.
     * @return the number of items
     */
    int getSize();

    /**
     * Returns the {@link CfItem} in a given slot.
     * @param index the slot index
     * @return the item or <code>null</code> if the slot is empty
     */
    @Nullable
    CfItem getItem(int index);

    /**
     * Adds a {@link LocationsListener} to be notified when any displayed item
     * has changed.
     * @param locationsListener the locations listener to add
     */
    void addLocationsListener(@NotNull LocationsListener locationsListener);

    /**
     * Removes a {@link LocationsListener} to be notified when any displayed
     * item has changed.
     * @param locationsListener the locations listener to remove
     */
    void removeLocationsListener(@NotNull LocationsListener locationsListener);

    /**
     * Adds a {@link LocationListener} to be notified when the item displayed in
     * a floor slot has changed.
     * @param index the floor slot
     * @param locationListener the location listener to add
     */
    void addLocationListener(int index, @NotNull LocationListener locationListener);

    /**
     * Removes a {@link LocationListener} to be notified when the item displayed
     * in a floor slot has changed.
     * @param index the floor slot
     * @param locationListener the location listener to remove
     */
    void removeLocationListener(int index, @NotNull LocationListener locationListener);

}
