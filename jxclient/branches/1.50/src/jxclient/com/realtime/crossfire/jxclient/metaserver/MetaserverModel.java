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

package com.realtime.crossfire.jxclient.metaserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains the metaserver information.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class MetaserverModel {

    /**
     * The current entries.
     */
    @NotNull
    private final List<MetaserverEntry> metalist = new ArrayList<MetaserverEntry>();

    /**
     * The pending entries. Only valid between {@link #begin()} and {@link
     * #commit()}.
     */
    @NotNull
    private final Collection<MetaserverEntry> metalistPending = new ArrayList<MetaserverEntry>();

    /**
     * Object used for synchronization.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * All registered metaserver listeners.
     */
    @NotNull
    private final Collection<MetaserverListener> metaserverListeners = new CopyOnWriteArrayList<MetaserverListener>();

    /**
     * All registered metaserver entry listeners. Maps entry index to list of
     * listeners.
     */
    @NotNull
    private final Map<Integer, List<MetaserverEntryListener>> metaserverEntryListeners = new HashMap<Integer, List<MetaserverEntryListener>>();

    /**
     * Returns a metaserver entry by index.
     * @param index the index
     * @return the metaserver entry, or <code>null</code> if the index is
     *         invalid
     */
    @Nullable
    public MetaserverEntry getEntry(final int index) {
        try {
            synchronized (sync) {
                return metalist.get(index);
            }
        } catch (final IndexOutOfBoundsException ex) {
            return null;
        }
    }

    /**
     * Returns the index of an entry by server name.
     * @param serverName the server name
     * @return the index, or <code>-1</code> if not found
     */
    public int getServerIndex(@NotNull final String serverName) {
        synchronized (sync) {
            int index = 0;
            for (final MetaserverEntry metaserverEntry : metalist) {
                if (metaserverEntry.getHostname().equals(serverName)) {
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
    public int size() {
        synchronized (sync) {
            return metalist.size();
        }
    }

    /**
     * Adds an entry.
     * @param metaserverEntry the entry to add
     */
    public void add(@NotNull final MetaserverEntry metaserverEntry) {
        synchronized (sync) {
            metalistPending.add(metaserverEntry);
        }
    }

    /**
     * Starts an update transaction.
     */
    public void begin() {
        metalistPending.clear();
    }

    /**
     * Finishes an update transaction.
     */
    public void commit() {
        final int oldMetalistSize;
        final int newMetalistSize;
        synchronized (sync) {
            oldMetalistSize = metalist.size();
            metalist.clear();
            metalist.addAll(metalistPending);
            Collections.sort(metalist);
            newMetalistSize = metalist.size();
        }
        metalistPending.clear();

        for (final MetaserverListener metaserverListener : metaserverListeners) {
            metaserverListener.numberOfEntriesChanged();
        }

        for (int i = 0, imax = Math.max(oldMetalistSize, newMetalistSize); i < imax; i++) {
            for (final MetaserverEntryListener metaserverEntryListener : getMetaserverEntryListeners(i)) {
                metaserverEntryListener.entryChanged();
            }
        }
    }

    /**
     * Adds a metaserver listener.
     * @param listener the listener to add
     */
    public void addMetaserverListener(@NotNull final MetaserverListener listener) {
        metaserverListeners.add(listener);
    }

    /**
     * Removes a metaserver listener.
     * @param listener the listener to remove
     */
    public void removeMetaserverListener(@NotNull final MetaserverListener listener) {
        metaserverListeners.remove(listener);
    }

    /**
     * Adds a metaserver entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to add
     */
    public void addMetaserverEntryListener(final int index, @NotNull final MetaserverEntryListener listener) {
        getMetaserverEntryListeners(index).add(listener);
    }

    /**
     * Removes a metaserver entry listener for one entry.
     * @param index the entry index to monitor
     * @param listener the listener to remove
     */
    public void removeMetaserverEntryListener(final int index, @NotNull final MetaserverEntryListener listener) {
        getMetaserverEntryListeners(index).remove(listener);
    }

    /**
     * Returns the metaserver entry listeners for one entry index.
     * @param index the entry index
     * @return the listeners list
     */
    @NotNull
    private Collection<MetaserverEntryListener> getMetaserverEntryListeners(final int index) {
        synchronized (metaserverEntryListeners) {
            final Collection<MetaserverEntryListener> existingListeners = metaserverEntryListeners.get(index);
            if (existingListeners != null) {
                return existingListeners;
            }

            final List<MetaserverEntryListener> newListeners = new CopyOnWriteArrayList<MetaserverEntryListener>();
            metaserverEntryListeners.put(index, newListeners);
            return newListeners;
        }
    }

}
