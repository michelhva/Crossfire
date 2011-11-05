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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class IndexedEventListenerList<T extends EventListener> {

    /**
     * The listener class.
     */
    @NotNull
    private final Class<T> class_;

    /**
     * The registered listeners to be notified about changes.
     */
    @NotNull
    private final List<EventListenerList2<T>> locationListeners = new ArrayList<EventListenerList2<T>>();

    /**
     * Creates a new instance.
     * @param class_ the listener class
     */
    public IndexedEventListenerList(@NotNull final Class<T> class_) {
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
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        synchronized (locationListeners) {
            try {
                return locationListeners.get(index);
            } catch (final IndexOutOfBoundsException ignored) {
                for (int i = locationListeners.size(); i <= index; i++) {
                    locationListeners.add(new EventListenerList2<T>(class_));
                }
                return locationListeners.get(index);
            }
        }
    }

}
