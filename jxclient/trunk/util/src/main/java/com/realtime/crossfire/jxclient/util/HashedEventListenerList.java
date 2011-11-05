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

package com.realtime.crossfire.jxclient.util;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class HashedEventListenerList<T extends EventListener> {

    /**
     * The listener class.
     */
    @NotNull
    private final Class<T> class_;

    /**
     * The registered listener to be notified about changes.
     */
    @NotNull
    private final Map<Integer, EventListenerList2<T>> locationListeners = new HashMap<Integer, EventListenerList2<T>>();

    /**
     * Creates a new instance.
     * @param class_ the listener class
     */
    public HashedEventListenerList(@NotNull final Class<T> class_) {
        this.class_ = class_;
    }

    /**
     * Adds a listener.
     * @param index the listener's index
     * @param listener the listener
     */
    public void add(final int index, @NotNull final T listener) {
        getLocationListeners(index).add(listener);
    }

    /**
     * Removes a listener.
     * @param index the listener's index
     * @param listener the listener
     */
    public void remove(final int index, @NotNull final T listener) {
        getLocationListeners(index).remove(listener);
    }

    /**
     * Returns an array of all the listeners.
     * @param index the listener's index
     * @return all the listeners
     */
    @NotNull
    public T[] getListeners(final int index) {
        return getLocationListeners(index).getListeners();
    }

    @NotNull
    private EventListenerList2<T> getLocationListeners(final int index) {
        synchronized (locationListeners) {
            final EventListenerList2<T> existingEventListenerList = locationListeners.get(index);
            if (existingEventListenerList != null) {
                return existingEventListenerList;
            }

            final EventListenerList2<T> eventListenerList = new EventListenerList2<T>(class_);
            locationListeners.put(index, eventListenerList);
            return eventListenerList;
        }
    }

}
