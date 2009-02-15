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
package com.realtime.crossfire.jxclient.skills;

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireSkillInfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintain the set of skills as sent by the server.
 * @author Andreas Kirschbaum
 */
public class SkillSet
{
    /**
     * Maps stat number to skill instance. Entries may be <code>null</code> if
     * the server did not provide a mapping.
     */
    private final Skill[] numberedSkills = new Skill[CrossfireStatsListener.CS_NUM_SKILLS];

    /**
     * Maps skill name to skill instance.
     */
    private final Map<String, Skill> namedSkills = new HashMap<String, Skill>();

    /**
     * The {@link CrossfireSkillInfoListener} attached to the server connection
     * for detecting changed skill info.
     */
    private final CrossfireSkillInfoListener crossfireSkillInfoListener = new CrossfireSkillInfoListener()
    {
        /** {@inheritDoc} */
        @Override
        public void clearSkills()
        {
            clearNumberedSkills();
            Arrays.fill(numberedSkills, null);
        }

        /** {@inheritDoc} */
        @Override
        public void addSkill(final int skillId, final String skillName)
        {
            SkillSet.this.addSkill(skillId, skillName);
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
        public void main()
        {
            clearNumberedSkills();
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the server connection to monitor
     * @param window the window to attach to
     */
    public SkillSet(final CrossfireServerConnection crossfireServerConnection, final JXCWindow window)
    {
        crossfireServerConnection.addCrossfireSkillInfoListener(crossfireSkillInfoListener);
        window.addConnectionStateListener(guiStateListener);
    }

    /**
     * Adds a new skill to the list of known skills.
     * @param id The numerical identifier for the new skill.
     * @param skillName The skill name.
     */
    private void addSkill(final int id, final String skillName)
    {
        final int index = id-CrossfireStatsListener.CS_STAT_SKILLINFO;
        final Skill oldSkill = numberedSkills[index];
        final Skill newSkill = getNamedSkill(skillName);
        if (oldSkill == newSkill)
        {
            return;
        }

        if (oldSkill != null)
        {
            oldSkill.set(0, 0);
        }
        numberedSkills[index] = newSkill;
    }

    /**
     * Return the skill instance for a given skill name.
     *
     * @param skillName The skill name to look up.
     *
     * @return The skill instance.
     */
    public Skill getNamedSkill(final String skillName)
    {
        final Skill oldSkill = namedSkills.get(skillName);
        if (oldSkill != null)
        {
            return oldSkill;
        }

        final Skill newSkill = new Skill(skillName);
        namedSkills.put(skillName, newSkill);
        return newSkill;
    }

    /**
     * Clears all stat info in {@link #numberedSkills}.
     */
    public void clearNumberedSkills()
    {
        for (final Skill skill : numberedSkills)
        {
            if (skill != null)
            {
                skill.set(0, 0);
            }
        }
    }

    /**
     * Returns the given skill as a Skill object.
     * @param id The numerical skill identifier.
     * @return The Skill object matching the given identifier; may be
     * <code>null</code> for undefined skills.
     */
    public Skill getSkill(final int id)
    {
        return numberedSkills[id-CrossfireStatsListener.CS_STAT_SKILLINFO];
    }
}
