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
package com.realtime.crossfire.jxclient.sound;

import com.realtime.crossfire.jxclient.stats.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.stats.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;

/**
 * Monitors stat changes and generates appropriate sound effects.
 *
 * @author Andreas Kirschbaum
 */
public class StatsWatcher
{
    /**
     * The stats instance to watch.
     */
    private final Stats stats;

    /**
     * The last known poisoned state.
     */
    private boolean poisoned;

    /**
     * The crossfire stats listener attached to {@link #stats}.
     */
    private final CrossfireStatsListener crossfireStatsListener = new CrossfireStatsListener()
    {
        /** {@inheritDoc} */
        public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
        {
            checkStats();
        }
    };

    /**
     * Create a new instance.
     *
     * @param stats The stats instance to watch.
     */
    public StatsWatcher(final Stats stats)
    {
        this.stats = stats;
        poisoned = stats.getStat(Stats.C_STAT_POISONED) != 0;
        stats.addCrossfireStatsListener(crossfireStatsListener);
    }

    /**
     * Check for changed stats and generate sound effects.
     */
    private void checkStats()
    {
        final boolean poisoned = stats.getStat(Stats.C_STAT_POISONED) != 0;
        if (this.poisoned != poisoned)
        {
            this.poisoned = poisoned;
            SoundManager.instance.play(Sounds.CHARACTER, poisoned ? Sounds.POISON_ON : Sounds.POISON_OFF);
        }
    }
}
