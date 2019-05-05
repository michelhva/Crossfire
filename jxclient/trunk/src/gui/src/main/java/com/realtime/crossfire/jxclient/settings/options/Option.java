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

package com.realtime.crossfire.jxclient.settings.options;

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * The base class for all options. It manages a set of {@link OptionListener
 * OptionListeners}.
 * @author Andreas Kirschbaum
 */
public abstract class Option {

    /**
     * The listeners to be notified.
     */
    @NotNull
    private final EventListenerList2<OptionListener> listeners = new EventListenerList2<>();

    /**
     * Notifies all listeners that the state has changed.
     */
    protected void fireStateChangedEvent() {
        for (OptionListener listener : listeners) {
            listener.stateChanged();
        }
    }

    /**
     * Adds a listener for state changes.
     * @param listener the listener to add
     */
    public void addOptionListener(@NotNull final OptionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for state changes.
     * @param listener the listener to remove
     */
    public void removeOptionListener(@NotNull final OptionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns whether the option should not be saved. This function can be
     * overridden for options that are otherwise saved, or for options that
     * should not be saved at all.
     * @return whether the option value should not be saved
     */
    public boolean inhibitSave() {
        return false;
    }

}
