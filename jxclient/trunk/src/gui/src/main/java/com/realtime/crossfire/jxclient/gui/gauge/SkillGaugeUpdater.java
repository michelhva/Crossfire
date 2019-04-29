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

package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillListener;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GaugeUpdater} which monitors a skill.
 * @author Andreas Kirschbaum
 */
public class SkillGaugeUpdater extends GaugeUpdater {

    /**
     * The monitored skill.
     */
    @NotNull
    private final Skill skill;

    /**
     * The {@link SkillListener} registered to be notified about skill changes.
     */
    @NotNull
    private final SkillListener skillListener = new SkillListener() {

        @Override
        public void gainedSkill() {
            changedSkill();
        }

        @Override
        public void lostSkill() {
            setValues(0, 0, 0, "", "");
        }

        @Override
        public void changedSkill() {
            final int percents = getPercentsToNextLevel(skill.getLevel(), skill.getExperience());
            setValues(percents, 0, 99, Integer.toString(skill.getLevel()), percents+"% "+skill.getExperience()+" (lvl "+skill.getLevel()+")");
        }

    };

    /**
     * Creates a new instance.
     * @param experienceTable the experience table to query
     * @param skill the skill to monitor
     */
    public SkillGaugeUpdater(@NotNull final ExperienceTable experienceTable, @NotNull final Skill skill) {
        super(experienceTable, false);
        this.skill = skill;
        this.skill.addSkillListener(skillListener);
    }

    @Override
    public void dispose() {
        skill.removeSkillListener(skillListener);
    }

}
