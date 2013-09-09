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

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import com.realtime.crossfire.jxclient.util.IndexedEventListenerList;
import java.util.Collection;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for {@link ItemView} implementing classes.
 * @author Andreas Kirschbaum
 */
public abstract class AbstractItemView implements ItemView {

    /**
     * The registered {@link LocationsListener LocationsListeners} to be
     * notified about changes.
     */
    @NotNull
    private final EventListenerList2<LocationsListener> locationsListeners = new EventListenerList2<LocationsListener>(LocationsListener.class);

    /**
     * The registered {@link ItemListener ItemListeners} to be notified about
     * changes.
     */
    @NotNull
    private final IndexedEventListenerList<LocationListener> locationListeners = new IndexedEventListenerList<LocationListener>(LocationListener.class);

    /**
     * The pending modified floor slots to be reported to listeners.
     */
    @NotNull
    private final Collection<Integer> modifiedSlots = new HashSet<Integer>();

    /**
     * The synchronization object for accesses to {@link #modifiedSlots}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The event scheduler callback for delaying event generation. This is
     * needed because the Crossfire server sends multiple item2 commands for one
     * "get all" command.
     */
    @NotNull
    private final Runnable fireEventCallback = new Runnable() {

        @Override
        public void run() {
            deliverEvents();
        }

    };

    /**
     * Creates a new instance.
     */
    protected AbstractItemView() {
        fireEventScheduler.start();
    }

    /**
     * The {@link EventScheduler} for delaying event generation.
     */
    @NotNull
    private final EventScheduler fireEventScheduler = new EventScheduler(100, 1, fireEventCallback);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLocationsListener(@NotNull final LocationsListener locationsListener) {
        locationsListeners.add(locationsListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLocationsListener(@NotNull final LocationsListener locationsListener) {
        locationsListeners.remove(locationsListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLocationListener(final int index, @NotNull final LocationListener locationListener) {
        locationListeners.add(index, locationListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLocationListener(final int index, @NotNull final LocationListener locationListener) {
        locationListeners.remove(index, locationListener);
    }

    /**
     * Marks a range of slots as modified.
     * @param firstIndex the first modified slot index
     * @param lastIndex the last modified slot index
     */
    protected void addModifiedRange(final int firstIndex, final int lastIndex) {
        synchronized (sync) {
            for (int i = firstIndex; i <= lastIndex; i++) {
                modifiedSlots.add(i);
            }
        }
        fireEvents();
    }

    /**
     * Marks a slot as modified.
     * @param index the modified slot index
     */
    protected void addModified(final int index) {
        synchronized (sync) {
            modifiedSlots.add(index);
        }
        fireEvents();
    }

    /**
     * Delivers all pending events.
     */
    private void deliverEvents() {
        final Integer[] tmpModifiedSlots;
        synchronized (sync) {
            tmpModifiedSlots = modifiedSlots.toArray(new Integer[modifiedSlots.size()]);
            modifiedSlots.clear();
        }
        if (tmpModifiedSlots.length > 0) {
            for (final LocationsListener locationsListener : locationsListeners.getListeners()) {
                locationsListener.locationsModified(tmpModifiedSlots);
            }
            for (final int index : tmpModifiedSlots) {
                for (final LocationListener locationListener : locationListeners.getListeners(index)) {
                    locationListener.locationChanged();
                }
            }
        }
    }

    /**
     * Delivers outstanding change events.
     */
    private void fireEvents() {
        fireEventScheduler.trigger();
    }

}
