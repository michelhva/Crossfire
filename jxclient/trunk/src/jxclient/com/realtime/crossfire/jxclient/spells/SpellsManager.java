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

import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireSpellListener;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages all known spells.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class SpellsManager
{
    /**
     * All known spells.
     */
    private final List<Spell> spells = new ArrayList<Spell>();

    private final List<SpellsManagerListener> listeners = new ArrayList<SpellsManagerListener>();

    /**
     * A {@link Comparator} to compare {@link Spell} instances by spell path
     * and name.
     */
    private final Comparator<Spell> spellNameComparator = new SpellComparator();

    /**
     * The listener to receive updates for spell information.
     */
    private final CrossfireSpellListener crossfireSpellListener = new CrossfireSpellListener()
    {
        /** {@inheritDoc} */
        @Override
        public void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int face, final String name, final String message)
        {
            SpellsManager.this.addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
        }

        /** {@inheritDoc} */
        @Override
        public void deleteSpell(final int tag)
        {
            SpellsManager.this.deleteSpell(tag);
        }

        /** {@inheritDoc} */
        @Override
        public void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage)
        {
            SpellsManager.this.updateSpell(flags, tag, mana, grace, damage);
        }
    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            spells.clear();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }
    };

    /**
     * Create a new instance.
     * @param crossfireServerConnection the connection to listen on
     * @param window the window to attach to
     */
    public SpellsManager(final CrossfireServerConnection crossfireServerConnection, final JXCWindow window)
    {
        initSpells();
        crossfireServerConnection.addCrossfireSpellListener(crossfireSpellListener);
        window.addConnectionStateListener(guiStateListener);
    }

    public void addCrossfireSpellChangedListener(final SpellsManagerListener listener)
    {
        listeners.add(listener);
    }

    public void removeCrossfireSpellChangedListener(final SpellsManagerListener listener)
    {
        listeners.remove(listener);
    }

    public List<Spell> getSpellList()
    {
        return spells;
    }

    private void initSpells()
    {
        for (int i = spells.size()-1; i >= 0; i--)
        {
            final Spell spell = spells.remove(i);
            for (final SpellsManagerListener listener : listeners)
            {
                listener.spellRemoved(spell, i);
            }
        }
    }

    public void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int faceNum, final String spellName, final String message)
    {
        final Spell spell = new Spell(spellName);
        spell.setParameters(faceNum, tag, message, level, castingTime, mana, grace, damage, skill, path);
        addSpell(spell);
    }

    private void addSpell(final Spell spell)
    {
        final int index = Collections.binarySearch(spells, spell, spellNameComparator);
        if (index < 0)
        {
            spells.add(-index-1, spell);
        }
        else
        {
            spells.set(index, spell);
        }

        for (final SpellsManagerListener listener : listeners)
        {
            listener.spellAdded(spell, index);
        }
    }

    public void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage)
    {
        int index = 0;
        for (final Spell spell : spells)
        {
            if (spell.getTag() == tag)
            {
                spell.updateParameters((flags&CrossfireSpellListener.UPD_SP_MANA) != 0, mana, (flags&CrossfireSpellListener.UPD_SP_GRACE) != 0, grace, (flags&CrossfireSpellListener.UPD_SP_DAMAGE) != 0, damage);
                break;
            }
            index++;
        }
    }

    public void deleteSpell(final int tag)
    {
        int index = 0;
        for (final Spell spell : spells)
        {
            if (spell.getTag() == tag)
            {
                spells.remove(index);

                for (final SpellsManagerListener listener : listeners)
                {
                    listener.spellRemoved(spell, index);
                }
                break;
            }
            index++;
        }
    }

    /**
     * Find a spell by name.
     *
     * @param spellName The spell name to find.
     *
     * @return The spell, or <code>null</code> if the spell name is undefined.
     */
    public Spell getSpell(final String spellName)
    {
        for (final Spell spell : spells)
        {
            if (spell.getName().equals(spellName))
            {
                return spell;
            }
        }

        return null;
    }
}
