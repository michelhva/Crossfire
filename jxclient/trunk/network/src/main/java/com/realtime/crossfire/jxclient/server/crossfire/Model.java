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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import org.jetbrains.annotations.NotNull;

/**
 * Combines all model classes that are updated.
 * @author Andreas Kirschbaum
 */
public class Model {

    @NotNull
    private SkillSet skillSet;

    @NotNull
    private Stats stats;

    @NotNull
    private final ExperienceTable experienceTable = new ExperienceTable();

    @NotNull
    private final GuiStateManager guiStateManager = new GuiStateManager();

    @Deprecated
    public void setSkillSet(@NotNull final SkillSet skillSet) {
        this.skillSet = skillSet;
    }

    @Deprecated
    public void setStats(@NotNull final Stats stats) {
        this.stats = stats;
    }

    @NotNull
    public SkillSet getSkillSet() {
        return skillSet;
    }

    @NotNull
    public Stats getStats() {
        return stats;
    }

    @NotNull
    public ExperienceTable getExperienceTable() {
        return experienceTable;
    }

    @NotNull
    public GuiStateManager getGuiStateManager() {
        return guiStateManager;
    }

}
