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
package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.ExperienceTable;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.stats.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.stats.CrossfireStatsListener;

/**
 * A {@link GaugeUpdater} which monitors a stat value.
 *
 * @author Andreas Kirschbaum
 */
public class ActiveSkillGaugeUpdater extends GaugeUpdater
{
    /**
     * The skill name to monitor.
     */
    private final String skill;

    /**
     * The {@link CrossfireStatsListener} registered to be notified about stat
     * changes.
     */
    private final CrossfireStatsListener crossfireStatsListener = new CrossfireStatsListener()
    {
        /** {@inheritDoc} */
        public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
        {
            setValues(evt.getStats().getActiveSkill().equals(skill) ? 1 : 0, 0, 1);
        }
    };

    /**
     * Create a new instance.
     *
     * @param experienceTable The experience table to query.
     *
     * @param skill The skill name to monitor.
     */
    public ActiveSkillGaugeUpdater(final ExperienceTable experienceTable, final String skill)
    {
        super(experienceTable);
        this.skill = skill;
        ItemsList.getStats().addCrossfireStatsListener(crossfireStatsListener);
    }
}
