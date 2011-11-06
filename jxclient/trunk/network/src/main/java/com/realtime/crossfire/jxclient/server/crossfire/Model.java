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

import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.spells.SpellsManager;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import org.jetbrains.annotations.NotNull;

/**
 * Combines all model classes that are updated.
 * @author Andreas Kirschbaum
 */
public class Model {

    @NotNull
    private final GuiStateManager guiStateManager = new GuiStateManager();

    @NotNull
    private final SkillSet skillSet = new SkillSet(guiStateManager);

    @NotNull
    private final ExperienceTable experienceTable = new ExperienceTable();

    @NotNull
    private final Stats stats = new Stats(experienceTable, skillSet, guiStateManager);

    @NotNull
    private final SpellsManager spellsManager = new SpellsManager(guiStateManager, skillSet, stats);

    @NotNull
    private final QuestsManager questsManager = new QuestsManager(guiStateManager);

    @NotNull
    private final FaceCache faceCache = new FaceCache();

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

    @NotNull
    public SpellsManager getSpellsManager() {
        return spellsManager;
    }

    @NotNull
    public QuestsManager getQuestsManager() {
        return questsManager;
    }

    @NotNull
    public FaceCache getFaceCache() {
        return faceCache;
    }

}
