//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for item managers.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractManager
{
    /**
     * Modified items for which no events have been delivered.
     */
    @NotNull
    private final Set<Integer> modifiedItems = new HashSet<Integer>();

    /**
     * Maps slot index to list of {@link LocationListener}s to be notified
     * about added or removed items.
     */
    @NotNull
    private final Map<Integer, EventListenerList> allListeners = new HashMap<Integer, EventListenerList>();

    /**
     * Listeners interested in ranged changes.
     */
    @NotNull
    private final Collection<LocationsListener> locationsListeners = new ArrayList<LocationsListener>();

    /**
     * Resets the manager's state.
     */
    public void reset()
    {
        synchronized (modifiedItems)
        {
            modifiedItems.clear();
        }
    }

    /**
     * Adds a {@link LocationsListener}s to be notified about changes.
     * @param listener the listener to add
     */
    public void addLocationsListener(@NotNull final LocationsListener listener)
    {
        locationsListeners.add(listener);
    }

    /**
     * Removes a {@link LocationsListener}s to be notified about changes.
     * @param listener the listener to remove
     */
    public void removeLocationsListener(@NotNull final LocationsListener listener)
    {
        locationsListeners.remove(listener);
    }

    /**
     * Adds a {@link LocationListener}s to be notified about changes in a slot.
     * @param index the slot index
     * @param listener the listener
     */
    public void addLocationListener(final int index, @NotNull final LocationListener listener)
    {
        EventListenerList listeners = allListeners.get(index);
        if (listeners == null)
        {
            listeners = new EventListenerList();
            allListeners.put(index, listeners);
        }
        listeners.add(LocationListener.class, listener);
    }

    /**
     * Removes a {@link LocationListener}s to be notified about changes in a
     * slot.
     * @param index the slot index
     * @param listener the listener
     */
    public void removeLocationListener(final int index, @NotNull final LocationListener listener)
    {
        final EventListenerList listeners = allListeners.get(index);
        assert listeners != null;
        listeners.remove(LocationListener.class, listener);
        if (listeners.getListenerCount() <= 0)
        {
            allListeners.remove(index);
        }
    }

    /**
     * Delivers pending events.
     * @param items The item information.
     */
    public void fireEvents(@NotNull final List<CfItem> items)
    {
        final Collection<Integer> tmp;
        synchronized (modifiedItems)
        {
            tmp = new HashSet<Integer>(modifiedItems);
            modifiedItems.clear();
        }

        for (final LocationsListener listener : locationsListeners)
        {
            listener.locationsModified(tmp);
        }

        for (final int index : tmp)
        {
            final EventListenerList tileListeners = allListeners.get(index);
            if (tileListeners != null)
            {
                @Nullable CfItem item;
                try
                {
                    item = items.get(index);
                }
                catch (final IndexOutOfBoundsException ex)
                {
                    item = null;
                }
                for (final LocationListener listener : tileListeners.getListeners(LocationListener.class))
                {
                    listener.locationModified(index, item);
                }
            }
        }
    }

    /**
     * Adds pending modified events.
     * @param items the pending events to add; may be <code>null</code>
     */
    public void addModified(@Nullable final Collection<CfItem> items)
    {
        if (items != null)
        {
            addModified(0, items.size());
        }
    }

    /**
     * Adds a pending modified event.
     * @param index the index having changed
     */
    public void addModified(final int index)
    {
        synchronized (modifiedItems)
        {
            modifiedItems.add(index);
        }
    }

    /**
     * Adds pending modified events.
     * @param start the first index to add
     * @param end the first index not to add
     */
    public void addModified(final int start, final int end)
    {
        synchronized (modifiedItems)
        {
            for (int i = start; i < end; i++)
            {
                modifiedItems.add(i);
            }
        }
    }
}
