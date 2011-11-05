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

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.jetbrains.annotations.NotNull;

/**
 * Type-safe version of {@link EventListenerList}.
 * @author Andreas Kirschbaum
 */
public class EventListenerList2<T extends EventListener> implements Serializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The listener's type.
     */
    @NotNull
    private final Class<T> t;

    /**
     * The {@link EventListenerList} flor delegation.
     */
    @NotNull
    private final EventListenerList eventListenerList = new EventListenerList();

    /**
     * Creates a new instance.
     * @param t the listener's type
     */
    //Assume the constructor call has passed the right type; can't use Class<T>
    //here since class literals do not work for parametrized types.
    @SuppressWarnings("unchecked")
    public EventListenerList2(@NotNull final Class<? extends EventListener> t) {
        this.t = (Class<T>)t;
    }

    /**
     * Returns an array of all the listeners.
     * @return all the listeners
     */
    @NotNull
    public T[] getListeners() {
        return eventListenerList.getListeners(t);
    }

    /**
     * Returns the number of registered listeners.
     * @return the number of registered listeners
     */
    public int getListenerCount() {
        return eventListenerList.getListenerCount(t);
    }

    /**
     * Adds a listener.
     * @param listener the listener
     */
    public void add(@NotNull final T listener) {
        eventListenerList.add(t, listener);
    }

    /**
     * Removes a listener.
     * @param listener the listener
     */
    public void remove(@NotNull final T listener) {
        eventListenerList.remove(t, listener);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return eventListenerList.toString();
    }

}
