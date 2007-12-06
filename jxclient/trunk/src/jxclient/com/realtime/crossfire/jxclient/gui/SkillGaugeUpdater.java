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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.ExperienceTable;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.Skill;
import com.realtime.crossfire.jxclient.SkillListener;

/**
 * A {@link GaugeUpdater} which monitors a skill.
 *
 * @author Andreas Kirschbaum
 */
public class SkillGaugeUpdater extends GaugeUpdater
{
    /**
     * The skill to monitor.
     */
    private final Skill skill;

    /**
     * The {@link SkillListener} registered to be notified about skill changes.
     */
    private final SkillListener skillListener = new SkillListener()
    {
        /** {@inheritDoc} */
        public void addSkill(final Skill skill)
        {
            updSkill(skill);
        }

        /** {@inheritDoc} */
        public void delSkill(final Skill skill)
        {
            setValues(0, 0, 0, "", "");
        }

        /** {@inheritDoc} */
        public void updSkill(final Skill skill)
        {
            final int percents = getPercentsToNextLevel(skill.getLevel(), skill.getExperience());
            setValues(percents, 0, 99, Integer.toString(skill.getLevel()), percents+"% "+skill.getExperience()+" (lvl "+skill.getLevel()+")");
        }
    };

    /**
     * Create a new instance.
     *
     * @param experienceTable The experience table to query.
     *
     * @param skill The skill to monitor.
     */
    public SkillGaugeUpdater(final ExperienceTable experienceTable, final Skill skill)
    {
        super(experienceTable);
        this.skill = skill;
        skill.addSkillListener(skillListener);
    }
}
