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

package com.realtime.crossfire.jxclient.skin.events;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillListener;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SkinEvent} that executes a {@link CommandList} whenever a new skill
 * has been lost.
 * @author Andreas Kirschbaum
 */
public class SkillRemovedSkinEvent implements SkinEvent {

    /**
     * The {@link CommandList} to execute.
     */
    @NotNull
    private final CommandList commandList;

    /**
     * The {@link Skill} to monitor.
     */
    @NotNull
    private final Skill skill;

    /**
     * The {@link SkillListener} attached to {@link #skill}.
     */
    @NotNull
    private final SkillListener skillListener = new SkillListener() {

        @Override
        public void gainedSkill() {
            // ignore
        }

        @Override
        public void lostSkill() {
            commandList.execute();
        }

        @Override
        public void changedSkill() {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param commandList the command list to execute
     * @param skill the skill to monitor
     */
    public SkillRemovedSkinEvent(@NotNull final CommandList commandList, @NotNull final Skill skill) {
        this.commandList = commandList;
        this.skill = skill;
        skill.addSkillListener(skillListener);
    }

    @Override
    public void dispose() {
        skill.removeSkillListener(skillListener);
    }

}
