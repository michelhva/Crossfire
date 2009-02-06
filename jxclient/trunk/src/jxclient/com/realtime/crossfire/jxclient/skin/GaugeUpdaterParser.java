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
package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.gui.gauge.ActiveSkillGaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.GaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.SkillGaugeUpdater;
import com.realtime.crossfire.jxclient.gui.gauge.StatGaugeUpdater;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsParser;
import java.io.IOException;

/**
 * Creates {@link GaugeUpdater} instances from strig representations.
 * @author Andreas Kirschbaum
 */
public class GaugeUpdaterParser
{
    /**
     * The {@link Stats} instance to use.
     */
    private final Stats stats;

    /**
     * The {@link ItemsManager} instance to use.
     */
    private final ItemsManager itemsManager;

    /**
     * Creates a new instance.
     * @param stats the stats instance to use
     * @param itemsManager the items manager instance to use
     */
    public GaugeUpdaterParser(final Stats stats, final ItemsManager itemsManager)
    {
        this.stats = stats;
        this.itemsManager = itemsManager;
    }

    /**
     * Parses a gauge updater value.
     * @param name the gauge updater value to parse
     * @param experienceTable the experience table to query
     * @return the gauge updater
     * @throws IOException if the gauge updater value does not exist
     */
    public GaugeUpdater parseGaugeUpdater(final String name, final ExperienceTable experienceTable) throws IOException
    {
        try
        {
            return new StatGaugeUpdater(experienceTable, StatsParser.parseStat(name), stats, itemsManager);
        }
        catch (final IllegalArgumentException ex)
        {
            // ignore
        }

        if (name.startsWith("SKILL_"))
        {
            return new SkillGaugeUpdater(experienceTable, SkillSet.getNamedSkill(name.substring(6).replaceAll("_", " ")));
        }

        if (name.startsWith("ACTIVE_SKILL_"))
        {
            return new ActiveSkillGaugeUpdater(experienceTable, name.substring(13).replaceAll("_", " "), stats);
        }

        throw new IOException("invalid stat name: "+name);
    }
}
