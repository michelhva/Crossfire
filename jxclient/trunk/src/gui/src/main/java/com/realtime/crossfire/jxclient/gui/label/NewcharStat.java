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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.stats.Stats;
import org.jetbrains.annotations.NotNull;

/**
 * A stat value.
 * @author Andreas Kirschbaum
 */
public enum NewcharStat {

    /**
     * Strength.
     */
    STR(Stats.CS_STAT_STR, "strength", "Str", NewCharModel.PRIORITY_INVALID_STAT_STR),

    /**
     * Dexterity.
     */
    DEX(Stats.CS_STAT_DEX, "dexterity", "Dex", NewCharModel.PRIORITY_INVALID_STAT_DEX),

    /**
     * Constitution.
     */
    CON(Stats.CS_STAT_CON, "constitution", "Con", NewCharModel.PRIORITY_INVALID_STAT_CON),

    /**
     * Intelligence.
     */
    INT(Stats.CS_STAT_INT, "intelligence", "Int", NewCharModel.PRIORITY_INVALID_STAT_INT),

    /**
     * Wisdom.
     */
    WIS(Stats.CS_STAT_WIS, "wisdom", "Wis", NewCharModel.PRIORITY_INVALID_STAT_WIS),

    /**
     * Power.
     */
    POW(Stats.CS_STAT_POW, "power", "Pow", NewCharModel.PRIORITY_INVALID_STAT_POW),

    /**
     * Charisma.
     */
    CHA(Stats.CS_STAT_CHA, "charisma", "Cha", NewCharModel.PRIORITY_INVALID_STAT_CHA);

    /**
     * The stat.
     */
    private final int stat;

    /**
     * The display name.
     */
    @NotNull
    private final String name;

    /**
     * The statname in the createplayer command.
     */
    @NotNull
    private final String statName;

    /**
     * The display priority of the related error message.
     */
    private final int priority;

    /**
     * Creates a new instance.
     * @param stat the stat
     * @param name the display name
     * @param statName the statname in the createplayer command
     * @param priority the display priority of the related error message
     */
    NewcharStat(final int stat, @NotNull final String name, @NotNull final String statName, final int priority) {
        this.stat = stat;
        this.name = name;
        this.statName = statName;
        this.priority = priority;
    }

    /**
     * Returns the stat.
     * @return the stat
     */
    public int getStat() {
        return stat;
    }

    /**
     * Returns the display name.
     * @return the display name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the statname in the ceateplayer command.
     * @return the statname
     */
    @NotNull
    public String getStatName() {
        return statName;
    }

    /**
     * Returns the display priority of the related error message.
     * @return the display priority
     */
    public int getPriority() {
        return priority;
    }

}
