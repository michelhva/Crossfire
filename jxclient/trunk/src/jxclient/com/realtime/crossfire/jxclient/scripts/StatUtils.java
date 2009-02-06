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
package com.realtime.crossfire.jxclient.scripts;

import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for converting stat values to stat names.
 * @author Andreas Kirschbaum
 */
public class StatUtils
{
    /**
     * The stat names: maps stat value to stat name.
     */
    private static final Map<Integer, String> statNames = new HashMap<Integer, String>();
    static
    {
        statNames.put(CrossfireStatsListener.CS_STAT_HP, "hp");
        statNames.put(CrossfireStatsListener.CS_STAT_MAXHP, "maxhp");
        statNames.put(CrossfireStatsListener.CS_STAT_SP, "sp");
        statNames.put(CrossfireStatsListener.CS_STAT_MAXSP, "maxsp");
        statNames.put(CrossfireStatsListener.CS_STAT_STR, "str");
        statNames.put(CrossfireStatsListener.CS_STAT_INT, "int");
        statNames.put(CrossfireStatsListener.CS_STAT_WIS, "wis");
        statNames.put(CrossfireStatsListener.CS_STAT_DEX, "dex");
        statNames.put(CrossfireStatsListener.CS_STAT_CON, "con");
        statNames.put(CrossfireStatsListener.CS_STAT_CHA, "cha");
        statNames.put(CrossfireStatsListener.CS_STAT_LEVEL, "level");
        statNames.put(CrossfireStatsListener.CS_STAT_WC, "wc");
        statNames.put(CrossfireStatsListener.CS_STAT_AC, "ac");
        statNames.put(CrossfireStatsListener.CS_STAT_DAM, "dam");
        statNames.put(CrossfireStatsListener.CS_STAT_ARMOUR, "armour");
        statNames.put(CrossfireStatsListener.CS_STAT_FOOD, "food");
        statNames.put(CrossfireStatsListener.CS_STAT_POW, "pow");
        statNames.put(CrossfireStatsListener.CS_STAT_GRACE, "grace");
        statNames.put(CrossfireStatsListener.CS_STAT_MAXGRACE, "maxgrace");
        statNames.put(CrossfireStatsListener.CS_STAT_FLAGS, "flags");
        statNames.put(CrossfireStatsListener.CS_STAT_EXP, "exp");
        statNames.put(CrossfireStatsListener.CS_STAT_SPEED, "speed");
        statNames.put(CrossfireStatsListener.CS_STAT_WEAP_SP, "weap_sp");
        statNames.put(CrossfireStatsListener.CS_STAT_WEIGHT_LIM, "weight_lim");
        statNames.put(CrossfireStatsListener.CS_STAT_SPELL_ATTUNE, "spell_attune");
        statNames.put(CrossfireStatsListener.CS_STAT_SPELL_REPEL, "spell_repel");
        statNames.put(CrossfireStatsListener.CS_STAT_SPELL_DENY, "spell_deny");
        statNames.put(CrossfireStatsListener.CS_STAT_EXP64, "exp");
        statNames.put(CrossfireStatsListener.CS_STAT_RANGE, "range");
        statNames.put(CrossfireStatsListener.CS_STAT_TITLE, "title");
        for (int stat = CrossfireStatsListener.CS_STAT_RESIST_START; stat < CrossfireStatsListener.CS_STAT_RESIST_START+CrossfireStatsListener.RESIST_TYPES; stat++)
        {
            statNames.put(stat, "resists");
        }
        for (int skill = CrossfireStatsListener.CS_STAT_SKILLINFO; skill < CrossfireStatsListener.CS_STAT_SKILLINFO+CrossfireStatsListener.CS_NUM_SKILLS; skill++)
        {
            statNames.put(skill, "skill");
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private StatUtils()
    {
    }

    /**
     * Returns the stat name for a stat value.
     * @param stat the stat value
     * @return the stat name
     */
    public static String getStatNames(final int stat)
    {
        final String statName = statNames.get(stat);
        if (statName == null)
        {
            throw new AssertionError();
        }
        return statName;
    }
}
