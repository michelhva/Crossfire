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

package com.realtime.crossfire.jxclient.spells;

import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the currently selected spell.
 * @author Andreas Kirschbaum
 */
public class CurrentSpellManager {

    /**
     * The listeners to notify object changed spell objects.
     */
    @NotNull
    private final EventListenerList2<CurrentSpellManagerListener> listeners = new EventListenerList2<CurrentSpellManagerListener>(CurrentSpellManagerListener.class);

    /**
     * The currently selected spell, or <code>null</code>.
     */
    @Nullable
    private Spell currentSpell = null;

    /**
     * Sets the currently selected spell.
     * @param spell the spell to selected
     */
    public void setCurrentSpell(@Nullable final Spell spell) {
        if (currentSpell == spell) {
            return;
        }

        currentSpell = spell;
        for (final CurrentSpellManagerListener listener : listeners.getListeners()) {
            listener.spellChanged(spell);
        }
    }

    /**
     * Returns the currently selected spell object.
     * @return the spell object, or <code>null</code> if no spell is selected
     */
    @Nullable
    public Spell getCurrentSpell() {
        return currentSpell;
    }

    /**
     * Adds a spell listener to be notified if the current spell object has
     * changed.
     * @param listener the listener to add
     */
    public void addSpellListener(@NotNull final CurrentSpellManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a spell listener.
     * @param listener the listener to remove
     */
    public void removeSpellListener(@NotNull final CurrentSpellManagerListener listener) {
        listeners.remove(listener);
    }

}
