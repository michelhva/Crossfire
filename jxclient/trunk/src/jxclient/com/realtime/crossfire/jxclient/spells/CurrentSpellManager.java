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
package com.realtime.crossfire.jxclient.spells;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the currently selected spell.
 *
 * @author Andreas Kirschbaum
 */
public class CurrentSpellManager
{
    /**
     * The listeners to notify object changed spell objects.
     */
    private final List<SpellListener> listeners = new ArrayList<SpellListener>();

    /**
     * The currently selected spell, or <code>null</code>.
     */
    private Spell currentSpell = null;

    /**
     * Set the currently selected spell.
     *
     * @param spell The spell to selected.
     */
    public void setCurrentSpell(final Spell spell)
    {
        if (currentSpell == spell)
        {
            return;
        }

        currentSpell = spell;
        final SpellChangedEvent event = new SpellChangedEvent(this, currentSpell);
        for (final SpellListener listener : listeners)
        {
            listener.spellChanged(event);
        }
    }

    /**
     * Return the currently selected spell object.
     *
     * @return The spell object, or <code>null</code> if no spell is selected.
     */
    public Spell getCurrentSpell()
    {
        return currentSpell;
    }

    /**
     * Add a spell listener to be notified if the current spell object has
     * changed.
     *
     * @param listener The listener to add.
     */
    public void addSpellListener(final SpellListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a spell listener.
     *
     * @param listener The listener to remove.
     */
    public void removeSpellListener(final SpellListener listener)
    {
        listeners.remove(listener);
    }
}
