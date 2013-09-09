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

package com.realtime.crossfire.jxclient.skills;

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintain the set of skills as sent by the server.
 * @author Andreas Kirschbaum
 */
public class SkillSet {

    /**
     * Maps stat number to skill instance. Entries may be <code>null</code> if
     * the server did not provide a mapping.
     */
    @NotNull
    private final Skill[] numberedSkills = new Skill[Stats.CS_NUM_SKILLS];

    /**
     * Maps skill name to skill instance.
     */
    @NotNull
    private final Map<String, Skill> namedSkills = new HashMap<String, Skill>();

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
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
            clearNumberedSkills();
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
     * Creates a new instance.
     * @param guiStateManager the gui state manager to watch
     */
    public SkillSet(@NotNull final GuiStateManager guiStateManager) {
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Clears all skills.
     */
    public void clearSkills() {
        clearNumberedSkills();
        Arrays.fill(numberedSkills, null);
    }

    /**
     * Adds a new skill to the list of known skills.
     * @param id the numerical identifier for the new skill
     * @param skillName the skill name
     * @param face the skill's face
     */
    public void addSkill(final int id, @NotNull final String skillName, final int face) {
        final int index = id-Stats.CS_STAT_SKILLINFO;
        final Skill oldSkill = numberedSkills[index];
        final Skill newSkill = getNamedSkill(skillName, face);
        if (oldSkill == newSkill) {
            return;
        }

        if (oldSkill != null) {
            oldSkill.set(0, 0);
        }
        numberedSkills[index] = newSkill;
    }

    /**
     * Returns the skill instance for a given skill name.
     * @param skillName the skill name to look up
     * @param face the skill face to set.
     * @return the skill instance
     */
    public Skill getNamedSkill(final String skillName, final int face) {
        final Skill oldSkill = namedSkills.get(skillName);
        if (oldSkill != null) {
            oldSkill.setFace(face);
            return oldSkill;
        }

        final Skill newSkill = new Skill(skillName);
        newSkill.setFace(face);
        namedSkills.put(skillName, newSkill);
        return newSkill;
    }

    /**
     * Clears all stat info in {@link #numberedSkills}.
     */
    public void clearNumberedSkills() {
        for (final Skill skill : numberedSkills) {
            if (skill != null) {
                skill.set(0, 0);
            }
        }
    }

    /**
     * Returns the given skill as a Skill object.
     * @param id the numerical skill identifier
     * @return the skill object matching the given identifier; may be
     *         <code>null</code> for undefined skills
     */
    @Nullable
    public Skill getSkill(final int id) {
        return numberedSkills[id-Stats.CS_STAT_SKILLINFO];
    }

    /**
     * Get a skill identifier from the skill name. This identifier can be used
     * through {@link #getSkill}.
     * @param name skill's name
     * @return skill's identifier, -1 if invalid skill
     */
    public int getSkillId(final String name) {
        for (int i = 0; i < numberedSkills.length; i++) {
            if (numberedSkills[i] != null && numberedSkills[i].toString().equals(name)) {
                return i+Stats.CS_STAT_SKILLINFO;
            }
        }
        return -1;
    }
}
