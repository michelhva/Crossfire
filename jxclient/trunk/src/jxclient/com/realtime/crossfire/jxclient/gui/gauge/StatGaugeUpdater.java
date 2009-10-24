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

import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.PlayerListener;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import com.realtime.crossfire.jxclient.util.Formatter;
import org.jetbrains.annotations.NotNull;

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
     * The {@link Stats} instance to watch.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link ItemsManager} instance to watch.
     */
    @NotNull
    private final ItemsManager itemsManager;

    /**
     * Whether the low food event should be generated.
     */
    private boolean active = false;

    /**
     * The {@link StatsListener} registered to be notified about stat
     * changes.
     */
    @NotNull
    private final StatsListener statsListener = new StatsListener()
    {
        /** {@inheritDoc} */
        @Override
        public void reset()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void statChanged(final int statnr, final int value)
        {
            switch (stat)
            {
            case CrossfireStatsListener.CS_STAT_HP:
                if (statnr == CrossfireStatsListener.CS_STAT_HP)
                {
                    setValues(value, 0, stats.getStat(CrossfireStatsListener.CS_STAT_MAXHP));
                }
                else if (statnr == CrossfireStatsListener.CS_STAT_MAXHP)
                {
                    setValues(stats.getStat(CrossfireStatsListener.CS_STAT_HP), 0, value);
                }
                break;

            case CrossfireStatsListener.CS_STAT_SP:
                if (statnr == CrossfireStatsListener.CS_STAT_SP)
                {
                    setValues(value, 0, stats.getStat(CrossfireStatsListener.CS_STAT_MAXSP));
                }
                else if (statnr == CrossfireStatsListener.CS_STAT_MAXSP)
                {
                    setValues(stats.getStat(CrossfireStatsListener.CS_STAT_SP), 0, value);
                }
                break;

            case CrossfireStatsListener.CS_STAT_FOOD:
                if (statnr == CrossfireStatsListener.CS_STAT_FOOD)
                {
                    setValues(value, 0, 999);
                }
                break;

            case CrossfireStatsListener.C_STAT_LOWFOOD:
                if (statnr == CrossfireStatsListener.C_STAT_LOWFOOD)
                {
                    setValues(active && stats.getStat(CrossfireStatsListener.CS_STAT_FOOD) < LOWFOOD_LIMIT ? 1 : 0, 0, 1);
                }
                break;

            case CrossfireStatsListener.CS_STAT_GRACE:
                if (statnr == CrossfireStatsListener.CS_STAT_GRACE)
                {
                    setValues(value, 0, stats.getStat(CrossfireStatsListener.CS_STAT_MAXGRACE));
                }
                else if (statnr == CrossfireStatsListener.CS_STAT_MAXGRACE)
                {
                    setValues(stats.getStat(CrossfireStatsListener.CS_STAT_GRACE), 0, value);
                }
                break;

            case CrossfireStatsListener.C_STAT_POISONED:
                if (statnr == CrossfireStatsListener.C_STAT_POISONED)
                {
                    setValues(value, 0, 1);
                }
                break;

            default:
                if (CrossfireStatsListener.CS_STAT_RESIST_START <= stat && stat <= CrossfireStatsListener.CS_STAT_RESIST_END)
                {
                    if (statnr == stat)
                    {
                        setValues(value, 0, 100);
                    }
                }
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void simpleWeaponSpeedChanged(final boolean simpleWeaponSpeed)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void titleChanged(@NotNull final String title)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void rangeChanged(@NotNull final String range)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void activeSkillChanged(@NotNull final String activeSkill)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void experienceChanged(final long exp)
        {
            if (stat == CrossfireStatsListener.C_STAT_EXP_NEXT_LEVEL)
            {
                updateExperienceNextLevel();
            }
        }

        /** {@inheritDoc} */
        @Override
        public void experienceNextLevelChanged(final long expNextLevel)
        {
            if (stat == CrossfireStatsListener.C_STAT_EXP_NEXT_LEVEL)
            {
                updateExperienceNextLevel();
            }
        }
    };

    /**
     * The listener to detect a changed player name.
     */
    @NotNull
    private final PlayerListener playerListener = new PlayerListener()
    {
        /** {@inheritDoc} */
        @Override
        public void playerReceived(@NotNull final CfPlayer player)
        {
            active = true;
        }

        /** {@inheritDoc} */
        @Override
        public void playerAdded(@NotNull final CfPlayer player)
        {
            active = true;
        }

        /** {@inheritDoc} */
        @Override
        public void playerRemoved(@NotNull final CfPlayer player)
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
    public StatGaugeUpdater(@NotNull final ExperienceTable experienceTable, final int stat, @NotNull final Stats stats, @NotNull final ItemsManager itemsManager)
    {
        super(experienceTable);
        this.stat = stat;
        this.stats = stats;
        this.itemsManager = itemsManager;
        this.stats.addCrossfireStatsListener(statsListener);
        this.itemsManager.addCrossfirePlayerListener(playerListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        itemsManager.removeCrossfirePlayerListener(playerListener);
        stats.removeCrossfireStatsListener(statsListener);
    }

    /**
     * Updates information for {@link
     * CrossfireStatsListener#C_STAT_EXP_NEXT_LEVEL}.
     */
    private void updateExperienceNextLevel()
    {
        final int level = stats.getStat(CrossfireStatsListener.CS_STAT_LEVEL);
        final long experience = stats.getExperience();
        final int perc = getPercentsToNextLevel(level, experience);
        setValues(perc, 0, 99, perc+"%", level+"<br>Experience:"+Formatter.formatLong(experience)+"<br>Next level:"+Formatter.formatLong(getExperienceToNextLevel(level, experience)));
    }
}
