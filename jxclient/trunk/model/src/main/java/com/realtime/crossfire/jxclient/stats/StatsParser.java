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

package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireStatsListener;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to parse stat names.
 * @author Andreas Kirschbaum
 */
public class StatsParser {

    /**
     * Maps stat names to stat index values. Only stats useful in skin files are
     * included.
     */
    @NotNull
    private static final Map<String, Integer> STAT_TABLE = new HashMap<String, Integer>();

    static {
        STAT_TABLE.put("AC", CrossfireStatsListener.CS_STAT_AC);
        STAT_TABLE.put("ARM", CrossfireStatsListener.CS_STAT_ARMOUR);
        STAT_TABLE.put("CHA", CrossfireStatsListener.CS_STAT_CHA);
        STAT_TABLE.put("CHA_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_CHA);
        STAT_TABLE.put("CHA_BASE", CrossfireStatsListener.CS_STAT_BASE_CHA);
        STAT_TABLE.put("CHA_RACE", CrossfireStatsListener.CS_STAT_RACE_CHA);
        STAT_TABLE.put("CON", CrossfireStatsListener.CS_STAT_CON);
        STAT_TABLE.put("CON_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_CON);
        STAT_TABLE.put("CON_BASE", CrossfireStatsListener.CS_STAT_BASE_CON);
        STAT_TABLE.put("CON_RACE", CrossfireStatsListener.CS_STAT_RACE_CON);
        STAT_TABLE.put("DAM", CrossfireStatsListener.CS_STAT_DAM);
        STAT_TABLE.put("DEX", CrossfireStatsListener.CS_STAT_DEX);
        STAT_TABLE.put("DEX_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_DEX);
        STAT_TABLE.put("DEX_BASE", CrossfireStatsListener.CS_STAT_BASE_DEX);
        STAT_TABLE.put("DEX_RACE", CrossfireStatsListener.CS_STAT_RACE_DEX);
        STAT_TABLE.put("EXP", CrossfireStatsListener.CS_STAT_EXP64);
        STAT_TABLE.put("EXP_NEXT_LEVEL", CrossfireStatsListener.C_STAT_EXP_NEXT_LEVEL);
        STAT_TABLE.put("FOOD", CrossfireStatsListener.CS_STAT_FOOD);
        STAT_TABLE.put("GOLEM_HP", CrossfireStatsListener.CS_STAT_GOLEM_HP);
        STAT_TABLE.put("GRACE", CrossfireStatsListener.CS_STAT_GRACE);
        STAT_TABLE.put("HP", CrossfireStatsListener.CS_STAT_HP);
        STAT_TABLE.put("INT", CrossfireStatsListener.CS_STAT_INT);
        STAT_TABLE.put("INT_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_INT);
        STAT_TABLE.put("INT_BASE", CrossfireStatsListener.CS_STAT_BASE_INT);
        STAT_TABLE.put("INT_RACE", CrossfireStatsListener.CS_STAT_RACE_INT);
        STAT_TABLE.put("LEVEL", CrossfireStatsListener.CS_STAT_LEVEL);
        STAT_TABLE.put("LOWFOOD", CrossfireStatsListener.C_STAT_LOWFOOD);
        STAT_TABLE.put("POISONED", CrossfireStatsListener.C_STAT_POISONED);
        STAT_TABLE.put("POW", CrossfireStatsListener.CS_STAT_POW);
        STAT_TABLE.put("POW_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_POW);
        STAT_TABLE.put("POW_BASE", CrossfireStatsListener.CS_STAT_BASE_POW);
        STAT_TABLE.put("POW_RACE", CrossfireStatsListener.CS_STAT_RACE_POW);
        STAT_TABLE.put("RANGE", CrossfireStatsListener.CS_STAT_RANGE);
        STAT_TABLE.put("RES_ACID", CrossfireStatsListener.CS_STAT_RES_ACID);
        STAT_TABLE.put("RES_BLIND", CrossfireStatsListener.CS_STAT_RES_BLIND);
        STAT_TABLE.put("RES_COLD", CrossfireStatsListener.CS_STAT_RES_COLD);
        STAT_TABLE.put("RES_CONF", CrossfireStatsListener.CS_STAT_RES_CONF);
        STAT_TABLE.put("RES_DEATH", CrossfireStatsListener.CS_STAT_RES_DEATH);
        STAT_TABLE.put("RES_DEPLETE", CrossfireStatsListener.CS_STAT_RES_DEPLETE);
        STAT_TABLE.put("RES_DRAIN", CrossfireStatsListener.CS_STAT_RES_DRAIN);
        STAT_TABLE.put("RES_ELEC", CrossfireStatsListener.CS_STAT_RES_ELEC);
        STAT_TABLE.put("RES_FEAR", CrossfireStatsListener.CS_STAT_RES_FEAR);
        STAT_TABLE.put("RES_FIRE", CrossfireStatsListener.CS_STAT_RES_FIRE);
        STAT_TABLE.put("RES_GHOSTHIT", CrossfireStatsListener.CS_STAT_RES_GHOSTHIT);
        STAT_TABLE.put("RES_HOLYWORD", CrossfireStatsListener.CS_STAT_RES_HOLYWORD);
        STAT_TABLE.put("RES_MAG", CrossfireStatsListener.CS_STAT_RES_MAG);
        STAT_TABLE.put("RES_PARA", CrossfireStatsListener.CS_STAT_RES_PARA);
        STAT_TABLE.put("RES_PHYS", CrossfireStatsListener.CS_STAT_RES_PHYS);
        STAT_TABLE.put("RES_POISON", CrossfireStatsListener.CS_STAT_RES_POISON);
        STAT_TABLE.put("RES_SLOW", CrossfireStatsListener.CS_STAT_RES_SLOW);
        STAT_TABLE.put("RES_TURN_UNDEAD", CrossfireStatsListener.CS_STAT_RES_TURN_UNDEAD);
        STAT_TABLE.put("SP", CrossfireStatsListener.CS_STAT_SP);
        STAT_TABLE.put("SPEED", CrossfireStatsListener.CS_STAT_SPEED);
        STAT_TABLE.put("STR", CrossfireStatsListener.CS_STAT_STR);
        STAT_TABLE.put("STR_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_STR);
        STAT_TABLE.put("STR_BASE", CrossfireStatsListener.CS_STAT_BASE_STR);
        STAT_TABLE.put("STR_RACE", CrossfireStatsListener.CS_STAT_RACE_STR);
        STAT_TABLE.put("TITLE", CrossfireStatsListener.CS_STAT_TITLE);
        STAT_TABLE.put("WC", CrossfireStatsListener.CS_STAT_WC);
        STAT_TABLE.put("WEIGHT", CrossfireStatsListener.C_STAT_WEIGHT);
        STAT_TABLE.put("WEIGHT_LIMIT", CrossfireStatsListener.CS_STAT_WEIGHT_LIM);
        STAT_TABLE.put("WIS", CrossfireStatsListener.CS_STAT_WIS);
        STAT_TABLE.put("WIS_APPLIED", CrossfireStatsListener.CS_STAT_APPLIED_WIS);
        STAT_TABLE.put("WIS_BASE", CrossfireStatsListener.CS_STAT_BASE_WIS);
        STAT_TABLE.put("WIS_RACE", CrossfireStatsListener.CS_STAT_RACE_WIS);
        STAT_TABLE.put("WEAPON_SPEED", CrossfireStatsListener.CS_STAT_WEAP_SP);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private StatsParser() {
    }

    /**
     * Converts a stat name into a stat index.
     * @param name the stat name
     * @return the stat index
     * @throws IllegalArgumentException if the stat name is undefined
     */
    public static int parseStat(@NotNull final String name) {
        if (!STAT_TABLE.containsKey(name)) {
            throw new IllegalArgumentException();
        }

        return STAT_TABLE.get(name);
    }

}
