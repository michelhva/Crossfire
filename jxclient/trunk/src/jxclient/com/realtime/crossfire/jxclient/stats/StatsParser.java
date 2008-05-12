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
package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to parse stat names.
 *
 * @author Andreas Kirschbaum
 */
public class StatsParser
{
    /**
     * Maps stat names to stat index values. Only stats useful in skin files
     * are included.
     */
    private static final Map<String, Integer> statTable = new HashMap<String, Integer>();
    static
    {
        statTable.put("AC", CrossfireStatsListener.CS_STAT_AC);
        statTable.put("ARM", CrossfireStatsListener.CS_STAT_ARMOUR);
        statTable.put("CHA", CrossfireStatsListener.CS_STAT_CHA);
        statTable.put("CON", CrossfireStatsListener.CS_STAT_CON);
        statTable.put("DAM", CrossfireStatsListener.CS_STAT_DAM);
        statTable.put("DEX", CrossfireStatsListener.CS_STAT_DEX);
        statTable.put("EXP", CrossfireStatsListener.CS_STAT_EXP64);
        statTable.put("EXP_NEXT_LEVEL", CrossfireStatsListener.C_STAT_EXP_NEXT_LEVEL);
        statTable.put("FOOD", CrossfireStatsListener.CS_STAT_FOOD);
        statTable.put("GRACE", CrossfireStatsListener.CS_STAT_GRACE);
        statTable.put("HP", CrossfireStatsListener.CS_STAT_HP);
        statTable.put("INT", CrossfireStatsListener.CS_STAT_INT);
        statTable.put("LEVEL", CrossfireStatsListener.CS_STAT_LEVEL);
        statTable.put("LOWFOOD", CrossfireStatsListener.C_STAT_LOWFOOD);
        statTable.put("POISONED", CrossfireStatsListener.C_STAT_POISONED);
        statTable.put("POW", CrossfireStatsListener.CS_STAT_POW);
        statTable.put("RANGE", CrossfireStatsListener.CS_STAT_RANGE);
        statTable.put("RES_ACID", CrossfireStatsListener.CS_STAT_RES_ACID);
        statTable.put("RES_BLIND", CrossfireStatsListener.CS_STAT_RES_BLIND);
        statTable.put("RES_COLD", CrossfireStatsListener.CS_STAT_RES_COLD);
        statTable.put("RES_CONF", CrossfireStatsListener.CS_STAT_RES_CONF);
        statTable.put("RES_DEATH", CrossfireStatsListener.CS_STAT_RES_DEATH);
        statTable.put("RES_DEPLETE", CrossfireStatsListener.CS_STAT_RES_DEPLETE);
        statTable.put("RES_DRAIN", CrossfireStatsListener.CS_STAT_RES_DRAIN);
        statTable.put("RES_ELEC", CrossfireStatsListener.CS_STAT_RES_ELEC);
        statTable.put("RES_FEAR", CrossfireStatsListener.CS_STAT_RES_FEAR);
        statTable.put("RES_FIRE", CrossfireStatsListener.CS_STAT_RES_FIRE);
        statTable.put("RES_GHOSTHIT", CrossfireStatsListener.CS_STAT_RES_GHOSTHIT);
        statTable.put("RES_HOLYWORD", CrossfireStatsListener.CS_STAT_RES_HOLYWORD);
        statTable.put("RES_MAG", CrossfireStatsListener.CS_STAT_RES_MAG);
        statTable.put("RES_PARA", CrossfireStatsListener.CS_STAT_RES_PARA);
        statTable.put("RES_PHYS", CrossfireStatsListener.CS_STAT_RES_PHYS);
        statTable.put("RES_POISON", CrossfireStatsListener.CS_STAT_RES_POISON);
        statTable.put("RES_SLOW", CrossfireStatsListener.CS_STAT_RES_SLOW);
        statTable.put("SP", CrossfireStatsListener.CS_STAT_SP);
        statTable.put("SPEED", CrossfireStatsListener.CS_STAT_SPEED);
        statTable.put("STR", CrossfireStatsListener.CS_STAT_STR);
        statTable.put("TITLE", CrossfireStatsListener.CS_STAT_TITLE);
        statTable.put("TURN_UNDEAD", CrossfireStatsListener.CS_STAT_TURN_UNDEAD);
        statTable.put("WC", CrossfireStatsListener.CS_STAT_WC);
        statTable.put("WEIGHT", CrossfireStatsListener.C_STAT_WEIGHT);
        statTable.put("WEIGHT_LIMIT", CrossfireStatsListener.CS_STAT_WEIGHT_LIM);
        statTable.put("WIS", CrossfireStatsListener.CS_STAT_WIS);
        statTable.put("WSPEED", CrossfireStatsListener.CS_STAT_WEAP_SP);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private StatsParser()
    {
    }

    /**
     * Convert a stat name into a stat index.
     *
     * @param name The stat name.
     *
     * @return The stat index.
     *
     * @throws IllegalArgumentException if the stat name is undefined
     */
    public static int parseStat(final String name)
    {
        if (!statTable.containsKey(name))
        {
            throw new IllegalArgumentException();
        }

        return statTable.get(name);
    }
}
