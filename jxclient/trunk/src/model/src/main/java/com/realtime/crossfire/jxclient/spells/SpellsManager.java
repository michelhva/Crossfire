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
 * Copyright (C) 2006-2012 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.spells;

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all known spells. Spells are filtered by skill through the function
 * filterSkill().
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class SpellsManager implements Iterable<Spell> {

    /**
     * Flag for updspell command: mana is present.
     */
    public static final int UPD_SP_MANA = 1;

    /**
     * Flag for updspell command: grace is present.
     */
    public static final int UPD_SP_GRACE = 2;

    /**
     * Flag for updspell command: damage is present.
     */
    public static final int UPD_SP_DAMAGE = 4;

    /**
     * All known spells.
     */
    @NotNull
    private final List<Spell> spells = new CopyOnWriteArrayList<>();

    /**
     * Spells currently exposed, based on the filter.
     */
    @NotNull
    private final List<Spell> filteredSpells = new CopyOnWriteArrayList<>();

    /**
     * Skill to filter spells by, as a skill id in {@link SkillSet}. -1 means to
     * not filter.
     */
    private int skillFilter = -1;

    /**
     * All unknown spells that have been referenced before. Maps spell name to
     * {@link Spell} instance. Accesses are synchronized on this instance.
     */
    @NotNull
    private final Map<String, Spell> unknownSpells = new HashMap<>();

    /**
     * The {@link SpellsManagerListener SpellsManagerListeners} to notify about
     * changes.
     */
    @NotNull
    private final EventListenerList2<SpellsManagerListener> listeners = new EventListenerList2<>();

    /**
     * A {@link Comparator} to compare {@link Spell} instances by spell path and
     * name.
     */
    @NotNull
    private final Comparator<Spell> spellNameComparator = new SpellComparator();

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            // ignore
        }

        @Override
        public void metaserver() {
            // ignore
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            spells.clear();
            spellSkills.clear();
            filteredSpells.clear();
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * The {@link SkillSet} containing skills from the server.
     */
    private final SkillSet skillSet;

    /**
     * The {@link Stats} for the player.
     */
    private final Stats stats;

    /**
     * Dummy skill for "all skills".
     */
    private final Skill skillAll = new Skill("All skills");

    /**
     * Skills used by the spell, including the "all" skill.
     */
    private final List<Skill> spellSkills = new ArrayList<>();

    /**
     * Compare 2 skills.
     */
    private static final Comparator<Skill> SKILL_COMPARATOR = (o1, o2) -> o1.toString().compareTo(o2.toString());

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to watch
     * @param skillSet skills the players knows
     * @param stats the stats for the player
     */
    public SpellsManager(@NotNull final GuiStateManager guiStateManager, @NotNull final SkillSet skillSet, @NotNull final Stats stats) {
        guiStateManager.addGuiStateListener(guiStateListener);
        this.skillSet = skillSet;
        this.stats = stats;
    }

    /**
     * Adds a {@link SpellsManagerListener} to notify about changes.
     * @param listener the listener to add
     */
    public void addCrossfireSpellChangedListener(@NotNull final SpellsManagerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link SpellsManagerListener} to notify about changes.
     * @param listener the listener to remove
     */
    public void removeCrossfireSpellChangedListener(@NotNull final SpellsManagerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a new spell. Re-uses entries from {@link #unknownSpells} if
     * possible.
     * @param tag the spell's tag
     * @param level the spell's level
     * @param castingTime the spell's casting time
     * @param mana the spell's mana cost
     * @param grace the spell's grace cost
     * @param damage the spell's damage
     * @param skill the spell's skill
     * @param path the spell's path
     * @param faceNum the spell's face number
     * @param spellName the spell's name
     * @param message the spells' description
     */
    public void addSpell(final int tag, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path, final int faceNum, @NotNull final String spellName, @NotNull final String message) {
        final Spell key = new Spell(spellName, skillSet, stats);
        key.setParameters(faceNum, tag, message, level, castingTime, mana, grace, damage, skill, path); // set spell path which is used in the comparator

        int index = Collections.binarySearch(spells, key, spellNameComparator);
        final Spell spell;
        if (index < 0) {
            final Spell existingSpell;
            synchronized (unknownSpells) {
                existingSpell = unknownSpells.remove(spellName);
            }
            if (existingSpell == null) {
                spell = key;
            } else {
                spell = existingSpell;
                spell.setParameters(faceNum, tag, message, level, castingTime, mana, grace, damage, skill, path);
            }
            index = -index-1;
            spells.add(index, spell);
        } else {
            spell = spells.get(index);
            spell.setParameters(faceNum, tag, message, level, castingTime, mana, grace, damage, skill, path);
        }

        rebuildSkills();
        filterSpells();

        for (final SpellsManagerListener listener : listeners) {
            listener.spellAdded(index);
        }
    }

    /**
     * Updates spell information.
     * @param flags specifies which fields to update
     * @param tag the spell's tag
     * @param mana the spell's new mana cost
     * @param grace the spell's new grace cost
     * @param damage the spell's new damage
     */
    public void updateSpell(final int flags, final int tag, final int mana, final int grace, final int damage) {
        for (final Spell spell : spells) {
            if (spell.getTag() == tag) {
                spell.updateParameters((flags&UPD_SP_MANA) != 0, mana, (flags&UPD_SP_GRACE) != 0, grace, (flags&UPD_SP_DAMAGE) != 0, damage);
                break;
            }
        }
    }

    /**
     * Deletes a spell.
     * @param tag the spell's tag
     */
    public void deleteSpell(final int tag) {
        int index = 0;
        for (final Spell spell : spells) {
            if (spell.getTag() == tag) {
                deleteSpellByIndex(index);
                break;
            }
            index++;
        }
        rebuildSkills();
        filterSpells();
    }

    /**
     * Deletes a spell by index into {@link #spells}.
     * @param index the index to delete
     */
    private void deleteSpellByIndex(final int index) {
        final Spell spell = spells.remove(index);
        synchronized (unknownSpells) {
            unknownSpells.put(spell.getName(), spell);
        }

        for (final SpellsManagerListener listener : listeners) {
            listener.spellRemoved(index);
        }

        spell.setUnknown(true);

        rebuildSkills();
    }

    /**
     * Returns a {@link Spell} instance by spell name. Creates a new instance if
     * the spell is unknown.
     * @param spellName the spell name to find
     * @return the spell instance
     */
    @NotNull
    public Spell getSpell(@NotNull final String spellName) {
        for (final Spell spell : spells) {
            if (spell.getName().equals(spellName)) {
                return spell;
            }
        }

        final Spell spell = new Spell(spellName, skillSet, stats);
        spell.setUnknown(true);
        synchronized (unknownSpells) {
            unknownSpells.put(spell.getName(), spell);
        }
        return spell;
    }

    @NotNull
    @Override
    public Iterator<Spell> iterator() {
        return Collections.unmodifiableList(filteredSpells).iterator();
    }

    /**
     * Returns the number of known spells.
     * @return the number of spells
     */
    public int getSpells() {
        return filteredSpells.size();
    }

    /**
     * Returns a {@link Spell} instance by index.
     * @param index the index
     * @return the spell or {@code null} if the index is invalid
     */
    @Nullable
    public Spell getSpell(final int index) {
        try {
            return filteredSpells.get(index);
        } catch (final IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Returns whether any spell has the given face.
     * @param faceNum the face
     * @return whether the face was found
     */
    public boolean displaysFace(final int faceNum) {
        for (final Spell spell : spells) {
            if (spell.getFaceNum() == faceNum) {
                return true;
            }
        }

        return false;
    }

    /**
     * An character name was sent to the server.
     */
    public void selectCharacter() {
        spells.clear();
        filteredSpells.clear();
    }

    /**
     * Filters spells to display by the specified skill index.
     * @param index the skill index, should be less than getSpellSkills()
     */
    public void filterSkill(final int index) {
        if (index < 0 || index >= spellSkills.size()) {
            return;
        }

        final int id = skillSet.getSkillId(spellSkills.get(index).toString());

        if (skillFilter == id) {
            return;
        }

        skillFilter = id;
        filterSpells();

        for (final SpellsManagerListener listener : listeners) {
            listener.spellAdded(0);
        }
    }

    /**
     * Rebuilds the list of spells to display.
     */
    private void filterSpells() {
        filteredSpells.clear();
        filteredSpells.addAll(spells.stream().filter(spell -> skillFilter == -1 || spell.getSkill() == skillFilter).collect(Collectors.toList()));
    }

    /**
     * Rebuilds the list of skills from the spells.
     */
    private void rebuildSkills() {
        spellSkills.clear();
        for (final Spell spell : spells) {
            final Skill skill = skillSet.getSkill(spell.getSkill());
            if (skill != null && !spellSkills.contains(skill)) {
                spellSkills.add(skill);
            }
        }
        Collections.sort(spellSkills, SKILL_COMPARATOR);
        spellSkills.add(0, skillAll);
    }

    /**
     * Returns the number of spell skills.
     * @return the number of skills
     */
    public int getSpellSkills() {
        return spellSkills.size();
    }

    /**
     * Returns the specified spell skill.
     * @param index the skill index, from 0 to getSpellSkills()-1
     * @return the specified skill, {@code null} if index is invalid
     */
    @Nullable
    public Skill getSpellSkill(final int index) {
        return 0 <= index && index < spellSkills.size() ? spellSkills.get(index) : null;
    }

}
