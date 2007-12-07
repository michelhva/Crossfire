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
package com.realtime.crossfire.jxclient.shortcuts;

import javax.swing.event.EventListenerList;

/**
 * Abstract base class for shortcut commands.
 *
 * @author Andreas Kirschbaum
 */
public abstract class Shortcut
{
    /**
     * The listeners to be notified.
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * Execute the shortcut.
     */
    public abstract void execute();

    /**
     * Register a shortcut listener.
     *
     * @param The listener to register.
     */
    public void addShortcutListener(final ShortcutListener listener)
    {
        listeners.add(ShortcutListener.class, listener);
    }

    /**
     * Unregister a shortcut listener.
     *
     * @param The listener to unregister.
     */
    public void removeShortcutListener(final ShortcutListener listener)
    {
        listeners.add(ShortcutListener.class, listener);
    }

    /**
     * Notify all listeners about a modification.
     */
    protected void fireModifiedEvent()
    {
        for (final ShortcutListener listener : listeners.getListeners(ShortcutListener.class))
        {
            listener.shortcutModified(this);
        }
    }
}
