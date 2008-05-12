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

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireSpellListener;
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
    private final Comparator<Spell> spellNameComparator = new Comparator<Spell>()
    {
        /** {@inheritDoc} */
        public int compare(final Spell spell1, final Spell spell2)
        {
            final int path1 = spell1.getPath();
            final int path2 = spell2.getPath();
            if (path1 < path2) return -1;
            if (path1 > path2) return +1;
            return String.CASE_INSENSITIVE_ORDER.compare(spell1.getName(), spell2.getName());
        }
    };

    /**
     * The listener to receive updates for spell information.
     */
    private final CrossfireSpellListener crossfireSpellListener = new CrossfireSpellListener()
    {
        /** {@inheritDoc} */
        public void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int face, final String name, final String message)
        {
            SpellsManager.this.addSpell(tag, level, castingTime, mana, grace, damage, skill, path, face, name, message);
        }

        /** {@inheritDoc} */
        public void deleteSpell(final int tag)
        {
            SpellsManager.this.deleteSpell(tag);
        }

        /** {@inheritDoc} */
        public void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage)
        {
            SpellsManager.this.updateSpell(flags, tag, mana, grace, damage);
        }
    };

    /**
     * Create a new instance.
     * @param crossfireServerConnection the connection to listen on
     */
    public SpellsManager(final CrossfireServerConnection crossfireServerConnection)
    {
        initSpells();
        crossfireServerConnection.addCrossfireSpellListener(crossfireSpellListener);
    }

    public void reset()
    {
        spells.clear();
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
        final Spell spell = new Spell(faceNum, tag, spellName, message);
        spell.setLevel(level);
        spell.setCastingTime(castingTime);
        spell.setMana(mana);
        spell.setGrace(grace);
        spell.setDamage(damage);
        spell.setSkill(skill);
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
                if ((flags&CrossfireSpellListener.UPD_SP_MANA) != 0)
                {
                    spell.setMana(mana);
                }

                if ((flags&CrossfireSpellListener.UPD_SP_GRACE) != 0)
                {
                    spell.setGrace(mana);
                }

                if ((flags&CrossfireSpellListener.UPD_SP_DAMAGE) != 0)
                {
                    spell.setDamage(mana);
                }

                for (final SpellsManagerListener listener : listeners)
                {
                    listener.spellModified(spell, index);
                }
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
            if (spell.getInternalName().equals(spellName))
            {
                return spell;
            }
        }

        return null;
    }
}
