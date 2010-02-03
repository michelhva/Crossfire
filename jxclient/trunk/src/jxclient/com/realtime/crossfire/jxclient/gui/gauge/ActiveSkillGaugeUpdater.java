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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private final String skill;

    /**
     * The stats instance to watch.
     */
    @NotNull
    private final Stats stats;

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
            // ignore
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
            setValues(activeSkill.equals(skill) ? 1 : 0, 0, 1);
        }

        /** {@inheritDoc} */
        @Override
        public void experienceChanged(final long exp)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void experienceNextLevelChanged(final long expNextLevel)
        {
            // ignore
        }
    };

    /**
     * Create a new instance.
     *
     * @param experienceTable The experience table to query.
     *
     * @param skill The skill name to monitor.
     *
     * @param stats the instance to watch
     */
    public ActiveSkillGaugeUpdater(@NotNull final ExperienceTable experienceTable, @NotNull final String skill, @NotNull final Stats stats)
    {
        super(experienceTable);
        this.skill = skill;
        this.stats = stats;
        this.stats.addCrossfireStatsListener(statsListener);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        stats.removeCrossfireStatsListener(statsListener);
    }
}
