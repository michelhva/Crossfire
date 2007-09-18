/* $Id$ */

package com.realtime.crossfire.jxclient;

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
        statTable.put("AC", Stats.CS_STAT_AC);
        statTable.put("CHA", Stats.CS_STAT_CHA);
        statTable.put("CON", Stats.CS_STAT_CON);
        statTable.put("DAM", Stats.CS_STAT_DAM);
        statTable.put("DEX", Stats.CS_STAT_DEX);
        statTable.put("EXP", Stats.CS_STAT_EXP64);
        statTable.put("FOOD", Stats.CS_STAT_FOOD);
        statTable.put("GRACE", Stats.CS_STAT_GRACE);
        statTable.put("HP", Stats.CS_STAT_HP);
        statTable.put("INT", Stats.CS_STAT_INT);
        statTable.put("LEVEL", Stats.CS_STAT_LEVEL);
        statTable.put("POW", Stats.CS_STAT_POW);
        statTable.put("RANGE", Stats.CS_STAT_RANGE);
        statTable.put("RES_ACID", Stats.CS_STAT_RES_ACID);
        statTable.put("RES_BLIND", Stats.CS_STAT_RES_BLIND);
        statTable.put("RES_COLD", Stats.CS_STAT_RES_COLD);
        statTable.put("RES_CONF", Stats.CS_STAT_RES_CONF);
        statTable.put("RES_DEATH", Stats.CS_STAT_RES_DEATH);
        statTable.put("RES_DEPLETE", Stats.CS_STAT_RES_DEPLETE);
        statTable.put("RES_DRAIN", Stats.CS_STAT_RES_DRAIN);
        statTable.put("RES_ELEC", Stats.CS_STAT_RES_ELEC);
        statTable.put("RES_FEAR", Stats.CS_STAT_RES_FEAR);
        statTable.put("RES_FIRE", Stats.CS_STAT_RES_FIRE);
        statTable.put("RES_GHOSTHIT", Stats.CS_STAT_RES_GHOSTHIT);
        statTable.put("RES_HOLYWORD", Stats.CS_STAT_RES_HOLYWORD);
        statTable.put("RES_MAG", Stats.CS_STAT_RES_MAG);
        statTable.put("RES_PARA", Stats.CS_STAT_RES_PARA);
        statTable.put("RES_PHYS", Stats.CS_STAT_RES_PHYS);
        statTable.put("RES_POISON", Stats.CS_STAT_RES_POISON);
        statTable.put("RES_SLOW", Stats.CS_STAT_RES_SLOW);
        statTable.put("SP", Stats.CS_STAT_SP);
        statTable.put("SPEED", Stats.CS_STAT_SPEED);
        statTable.put("STR", Stats.CS_STAT_STR);
        statTable.put("TITLE", Stats.CS_STAT_TITLE);
        statTable.put("TURN_UNDEAD", Stats.CS_STAT_TURN_UNDEAD);
        statTable.put("WC", Stats.CS_STAT_WC);
        statTable.put("WIS", Stats.CS_STAT_WIS);
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
