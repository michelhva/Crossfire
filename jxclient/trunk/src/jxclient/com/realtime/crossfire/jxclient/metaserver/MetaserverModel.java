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
package com.realtime.crossfire.jxclient.metaserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains the metaserver information.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class MetaserverModel
{
    /**
     * The current entries.
     */
    private final List<MetaserverEntry> metalist = new ArrayList<MetaserverEntry>();

    /**
     * The pending entries. Only valid between {@link #begin()} and {@link
     * #commit()}.
     */
    private final List<MetaserverEntry> metalistPending = new ArrayList<MetaserverEntry>();

    /**
     * Object used for synchronization.
     */
    private final Object sync = new Object();

    /**
     * All registered metaserver listeners.
     */
    private final List<MetaserverListener> metaserverListeners = new ArrayList<MetaserverListener>();

    /**
     * All registered metaserver entry listeners. Maps entry index to list of listeners.
     */
    private final Map<Integer, List<MetaserverEntryListener>> metaserverEntryListeners = new HashMap<Integer, List<MetaserverEntryListener>>();

    /**
     * Returns a metaserver entry by index.
     * @param index the index
     * @return the metaserver entry, or <code>null</code> if the index is
     * invalid
     */
    public MetaserverEntry getEntry(final int index)
    {
        try
        {
            synchronized (sync)
            {
                return metalist.get(index);
            }
        }
        catch (final IndexOutOfBoundsException ex)
        {
            return null;
        }
    }

    /**
     * Returns the index of an entry by server name.
     * @param serverName the server name
     * @return the index, or <code>-1</code> if not found
     */
    public int getServerIndex(final String serverName)
    {
        synchronized (sync)
        {
            int index = 0;
            for (final MetaserverEntry metaserverEntry : metalist)
            {
                if (metaserverEntry.getHostname().equals(serverName))
                {
                    return index;
                }

                index++;
            }
        }

        return -1;
    }

    /**
     * Returns the number of metaserver entries.
     * @return the number of metaserver entries
     */
    public int size()
    {
        synchronized (sync)
        {
            return metalist.size();
        }
    }

    /**
     * Adds an entry.
     * @param metaserverEntry the entry to add
     */
    public void add(final MetaserverEntry metaserverEntry)
    {
        synchronized (sync)
        {
            metalistPending.add(metaserverEntry);
        }
    }

    /**
     * Starts an update transaction.
     */
    public void begin()
    {
        metalistPending.clear();
    }

    /**
     * Finishes an update transaction.
     */
    public void commit()
    {
        final int oldMetalistSize;
        final int newMetalistSize;
        synchronized (sync)
        {
            oldMetalistSize = metalist.size();
            metalist.clear();
            metalist.addAll(metalistPending);
            Collections.sort(metalist);
            newMetalistSize = metalist.size();
        }
        metalistPending.clear();

        for (final MetaserverListener metaserverListener : metaserverListeners)
        {
            metaserverListener.numberOfEntriesChanged();
        }

        for (int i = 0, imax = Math.max(oldMetalistSize, newMetalistSize); i < imax; i++)
        {
            for (final MetaserverEntryListener metaserverEntryListener : getMetaserverEntryListeners(i))
            {
                metaserverEntryListener.entryChanged();
            }
        }
    }

    /**
     * Adds a metaserver listener.
     * @param listener the listener to add
     */
    public void addMetaserverListener(final MetaserverListener listener)
    {
        metaserverListeners.add(listener);
    }

    /**
     * Removes a metaserver listener.
     * @param listener the listener to remove
     */
    public void removeMetaserverListener(final MetaserverListener listener)
    {
        metaserverListeners.remove(listener);
    }

    /**
     * Adds a metaserver entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to add
     */
    public void addMetaserverEntryListener(final int index, final MetaserverEntryListener listener)
    {
        getMetaserverEntryListeners(index).add(listener);
    }

    /**
     * Removes a metaserver entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to remove
     */
    public void removeMetaserverEntryListener(final int index, final MetaserverEntryListener listener)
    {
        getMetaserverEntryListeners(index).remove(listener);
    }

    /**
     * Returns the metaserver entry listeners for one entry index.
     * @param index the entry index
     * @return the listsners list
     */
    private List<MetaserverEntryListener> getMetaserverEntryListeners(final int index)
    {
        synchronized (metaserverEntryListeners)
        {
            final List<MetaserverEntryListener> existingListeners = metaserverEntryListeners.get(index);
            if (existingListeners != null)
            {
                return existingListeners;
            }

            final List<MetaserverEntryListener> newListeners = new ArrayList<MetaserverEntryListener>();
            metaserverEntryListeners.put(index, newListeners);
            return newListeners;
        }
    }
}
