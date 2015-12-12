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
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * A list of event listeners.
 * @param <T> the type of the listeners
 * @author Andreas Kirschbaum
 */
public class EventListenerList2<T extends EventListener> implements Iterable<T>, Serializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link CopyOnWriteArrayList} holding all listener.
     */
    @NotNull
    private final CopyOnWriteArrayList<T> eventListenerList = new CopyOnWriteArrayList<>();

    /**
     * Adds a listener.
     * @param listener the listener
     */
    public void add(@NotNull final T listener) {
        eventListenerList.add(listener);
    }

    /**
     * Removes a listener.
     * @param listener the listener
     */
    public void remove(@NotNull final T listener) {
        eventListenerList.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return eventListenerList.toString();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(eventListenerList).iterator();
    }

}
