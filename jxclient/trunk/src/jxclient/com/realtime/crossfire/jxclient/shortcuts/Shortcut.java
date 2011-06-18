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

package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for shortcut commands.
 * @author Andreas Kirschbaum
 */
public abstract class Shortcut {

    /**
     * The listeners to be notified.
     */
    @NotNull
    private final EventListenerList2<ShortcutListener> listeners = new EventListenerList2<ShortcutListener>(ShortcutListener.class);

    /**
     * Releases all allocated resources.
     */
    public abstract void dispose();

    /**
     * Executes the shortcut.
     */
    public abstract void execute();

    /**
     * Registers a shortcut listener.
     * @param listener the listener to register
     */
    public void addShortcutListener(@NotNull final ShortcutListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a shortcut listener.
     * @param listener the listener to unregister
     */
    public void removeShortcutListener(@NotNull final ShortcutListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all listeners about a modification.
     */
    protected void fireModifiedEvent() {
        for (final ShortcutListener listener : listeners.getListeners()) {
            listener.shortcutModified();
        }
    }

    /**
     * Returns the current tooltip text.
     * @return the tooltip text
     */
    @NotNull
    public abstract String getTooltipText();

    /**
     * Calls a {@link ShortcutVisitor}'s <code>visit()</code> function for this
     * instance.
     * @param visitor the visitor to call
     */
    public abstract void visit(@NotNull final ShortcutVisitor visitor);

    /**
     * Returns whether this shortcut displays the given face.
     * @param face the face to check for
     * @return whether the face is displayed
     */
    public abstract boolean displaysFace(final Face face);

}
