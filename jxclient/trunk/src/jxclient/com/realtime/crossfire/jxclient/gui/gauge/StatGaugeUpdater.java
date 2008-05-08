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
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.CrossfirePlayerListener;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.stats.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.stats.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;

/**
 * A {@link GaugeUpdater} which monitors a stat value.
 *
 * @author Andreas Kirschbaum
 */
public class StatGaugeUpdater extends GaugeUpdater
{
    /**
     * The LOWFOOD indicator is turned on if the FOOD value falls below this
     * value.
     */
    private static final int LOWFOOD_LIMIT = 100;

    /**
     * The stat value to monitor.
     */
    private final int stat;

    /**
     * Whether the low food event should be generated.
     */
    private boolean active = false;

    /**
     * The {@link CrossfireStatsListener} registered to be notified about stat
     * changes.
     */
    private final CrossfireStatsListener crossfireStatsListener = new CrossfireStatsListener()
    {
        /** {@inheritDoc} */
        public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
        {
            final Stats s = evt.getStats();
            switch (stat)
            {
            case Stats.CS_STAT_HP:
                setValues(s.getStat(stat), 0, s.getStat(Stats.CS_STAT_MAXHP));
                break;

            case Stats.CS_STAT_SP:
                setValues(s.getStat(stat), 0, s.getStat(Stats.CS_STAT_MAXSP));
                break;

            case Stats.CS_STAT_FOOD:
                setValues(s.getStat(stat), 0, 999);
                break;

            case Stats.C_STAT_LOWFOOD:
                setValues(active && s.getStat(Stats.CS_STAT_FOOD) < LOWFOOD_LIMIT ? 1 : 0, 0, 1);
                break;

            case Stats.CS_STAT_GRACE:
                setValues(s.getStat(stat), 0, s.getStat(Stats.CS_STAT_MAXGRACE));
                break;

            case Stats.C_STAT_EXP_NEXT_LEVEL:
                final int level = s.getStat(Stats.CS_STAT_LEVEL);
                final int perc = getPercentsToNextLevel(level, s.getExperience());
                setValues(perc, 0, 99, perc+"%", s.getExperience()+"/"+getExperience(level+1));
                break;

            case Stats.C_STAT_POISONED:
                setValues(s.getStat(Stats.C_STAT_POISONED), 0, 1);
                break;

            default:
                if (Stats.CS_STAT_RESIST_START <= stat && stat <= Stats.CS_STAT_RESIST_END)
                {
                    setValues(s.getStat(stat), 0, 100);
                }
                break;
            }
        }
    };

    /**
     * The listener to detect a changed player name.
     */
    private final CrossfirePlayerListener crossfirePlayerListener = new CrossfirePlayerListener()
    {
        /** {@inheritDoc} */
        public void playerReceived(final CfPlayer player)
        {
            active = true;
        }

        /** {@inheritDoc} */
        public void playerAdded(final CfPlayer player)
        {
            active = true;
        }

        /** {@inheritDoc} */
        public void playerRemoved(final CfPlayer player)
        {
            active = false;
        }
    };

    /**
     * Create a new instance.
     *
     * @param experienceTable The experience table to query.
     *
     * @param stat The stat value to monitor.
     *
     * @param stats the instance to watch
     *
     * @param itemsManager the instance to watch
     */
    public StatGaugeUpdater(final ExperienceTable experienceTable, final int stat, final Stats stats, final ItemsManager itemsManager)
    {
        super(experienceTable);
        this.stat = stat;
        stats.addCrossfireStatsListener(crossfireStatsListener);
        itemsManager.addCrossfirePlayerListener(crossfirePlayerListener);
    }
}
