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

import com.realtime.crossfire.jxclient.items.ItemListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jetbrains.annotations.NotNull;

public class IndexedEventListenerList {

    /**
     * The registered {@link ItemListener}s to be notified about changes.
     */
    @NotNull
    private final List<EventListenerList> locationListeners = new ArrayList<EventListenerList>();

    public <T extends EventListener> void add(final int index, @NotNull final Class<T> class_, @NotNull final T listener) {
        getLocationListeners(index).add(class_, listener);
    }

    public <T extends EventListener> void remove(final int index, @NotNull final Class<T> class_, @NotNull final T listener) {
        getLocationListeners(index).remove(class_, listener);
    }

    @NotNull
    public <T extends EventListener> T[] getListeners(final int index, @NotNull final Class<T> class_) {
        return getLocationListeners(index).getListeners(class_);
    }

    @NotNull
    private EventListenerList getLocationListeners(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        synchronized (locationListeners) {
            try {
                return locationListeners.get(index);
            } catch (final IndexOutOfBoundsException ignored) {
                for (int i = locationListeners.size(); i <= index; i++) {
                    locationListeners.add(new EventListenerList());
                }
                return locationListeners.get(index);
            }
        }
    }

}
