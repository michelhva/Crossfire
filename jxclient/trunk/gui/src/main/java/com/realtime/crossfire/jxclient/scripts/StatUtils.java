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

package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireStatsListener;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for converting stat values to stat names.
 * @author Andreas Kirschbaum
 */
public class StatUtils {

    /**
     * The stat names: maps stat value to stat name.
     */
    @NotNull
    private static final Map<Integer, String> STAT_NAMES = new HashMap<Integer, String>();

    static {
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_HP, "hp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_MAXHP, "maxhp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_SP, "sp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_MAXSP, "maxsp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_STR, "str");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_STR, "str_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_STR, "str_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_STR, "str_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_INT, "int");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_INT, "int_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_INT, "int_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_INT, "int_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_WIS, "wis");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_WIS, "wis_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_WIS, "wis_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_WIS, "wis_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_DEX, "dex");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_DEX, "dex_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_DEX, "dex_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_DEX, "dex_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_CON, "con");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_CON, "con_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_CON, "con_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_CON, "con_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_CHA, "cha");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_CHA, "cha_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_CHA, "cha_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_CHA, "cha_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_LEVEL, "level");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_WC, "wc");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_AC, "ac");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_DAM, "dam");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_ARMOUR, "armour");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_FOOD, "food");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_POW, "pow");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RACE_POW, "pow_race");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_BASE_POW, "pow_base");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_APPLIED_POW, "pow_applied");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_GRACE, "grace");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_MAXGRACE, "maxgrace");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_FLAGS, "flags");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_EXP, "exp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_SPEED, "speed");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_WEAP_SP, "weap_sp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_WEIGHT_LIM, "weight_lim");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_SPELL_ATTUNE, "spell_attune");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_SPELL_REPEL, "spell_repel");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_SPELL_DENY, "spell_deny");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_EXP64, "exp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_RANGE, "range");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_TITLE, "title");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_GOLEM_HP, "golem_hp");
        STAT_NAMES.put(CrossfireStatsListener.CS_STAT_GOLEM_MAXHP, "golem_maxhp");
        for (int stat = CrossfireStatsListener.CS_STAT_RESIST_START; stat < CrossfireStatsListener.CS_STAT_RESIST_START+CrossfireStatsListener.RESIST_TYPES; stat++) {
            STAT_NAMES.put(stat, "resists");
        }
        for (int skill = CrossfireStatsListener.CS_STAT_SKILLINFO; skill < CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS; skill++) {
            STAT_NAMES.put(skill, "skill");
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private StatUtils() {
    }

    /**
     * Returns the stat name for a stat value.
     * @param stat the stat value
     * @return the stat name
     */
    @NotNull
    public static String getStatNames(final int stat) {
        final String statName = STAT_NAMES.get(stat);
        if (statName == null) {
            throw new AssertionError();
        }
        return statName;
    }

}
