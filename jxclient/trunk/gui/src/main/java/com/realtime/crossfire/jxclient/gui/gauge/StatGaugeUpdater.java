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

package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemSetListener;
import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import com.realtime.crossfire.jxclient.util.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GaugeUpdater} which monitors a stat value.
 * @author Andreas Kirschbaum
 */
public class StatGaugeUpdater extends GaugeUpdater {

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
     * The {@link ItemSet} instance to watch.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * Whether the low food event should be generated.
     */
    private boolean active = false;

    /**
     * The {@link StatsListener} registered to be notified about stat changes.
     */
    @NotNull
    private final StatsListener statsListener = new StatsListener() {

        @Override
        public void reset() {
            // ignore
        }

        @Override
        public void statChanged(final int statNo, final int value) {
            switch (stat) {
            case Stats.CS_STAT_HP:
                if (statNo == Stats.CS_STAT_HP) {
                    setValues(value, 0, stats.getStat(Stats.CS_STAT_MAXHP));
                } else if (statNo == Stats.CS_STAT_MAXHP) {
                    setValues(stats.getStat(Stats.CS_STAT_HP), 0, value);
                }
                break;

            case Stats.CS_STAT_SP:
                if (statNo == Stats.CS_STAT_SP) {
                    setValues(value, 0, stats.getStat(Stats.CS_STAT_MAXSP));
                } else if (statNo == Stats.CS_STAT_MAXSP) {
                    setValues(stats.getStat(Stats.CS_STAT_SP), 0, value);
                }
                break;

            case Stats.CS_STAT_FOOD:
                if (statNo == Stats.CS_STAT_FOOD) {
                    setValues(value, 0, 999);
                }
                break;

            case Stats.C_STAT_LOWFOOD:
                if (statNo == Stats.C_STAT_LOWFOOD) {
                    setValues(active && stats.getStat(Stats.CS_STAT_FOOD) < LOWFOOD_LIMIT ? 1 : 0, 0, 1);
                }
                break;

            case Stats.CS_STAT_GRACE:
                if (statNo == Stats.CS_STAT_GRACE) {
                    setValues(value, 0, stats.getStat(Stats.CS_STAT_MAXGRACE));
                } else if (statNo == Stats.CS_STAT_MAXGRACE) {
                    setValues(stats.getStat(Stats.CS_STAT_GRACE), 0, value);
                }
                break;

            case Stats.C_STAT_POISONED:
                if (statNo == Stats.C_STAT_POISONED) {
                    setValues(value, 0, 1);
                }
                break;

            case Stats.CS_STAT_GOLEM_HP:
                if (statNo == Stats.CS_STAT_GOLEM_HP) {
                    setValues(value, 0, stats.getStat(Stats.CS_STAT_GOLEM_MAXHP));
                } else if (statNo == Stats.CS_STAT_GOLEM_MAXHP) {
                    setValues(stats.getStat(Stats.CS_STAT_GOLEM_HP), 0, value);
                }
                break;

            default:
                if (Stats.CS_STAT_RESIST_START <= stat && stat <= Stats.CS_STAT_RESIST_END && statNo == stat) {
                    setValues(value, 0, 100);
                }
                break;
            }
        }

        @Override
        public void simpleWeaponSpeedChanged(final boolean simpleWeaponSpeed) {
            // ignore
        }

        @Override
        public void titleChanged(@NotNull final String title) {
            // ignore
        }

        @Override
        public void rangeChanged(@NotNull final String range) {
            // ignore
        }

        @Override
        public void activeSkillChanged(@NotNull final String activeSkill) {
            // ignore
        }

        @Override
        public void experienceChanged(final long exp) {
            if (stat == Stats.C_STAT_EXP_NEXT_LEVEL) {
                updateExperienceNextLevel();
            }
        }

        @Override
        public void experienceNextLevelChanged(final long expNextLevel) {
            if (stat == Stats.C_STAT_EXP_NEXT_LEVEL) {
                updateExperienceNextLevel();
            }
        }

    };

    /**
     * The listener to detect a changed player name.
     */
    @NotNull
    private final ItemSetListener itemSetListener = new ItemSetListener() {

        @Override
        public void itemAdded(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemMoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemChanged(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemRemoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void playerChanged(@Nullable final CfItem player) {
            active = player != null;
        }

        @Override
        public void openContainerChanged(final int tag) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param experienceTable the experience table to query
     * @param stat the stat value to monitor
     * @param stats the instance to watch
     * @param itemSet the item set to watch
     */
    public StatGaugeUpdater(@NotNull final ExperienceTable experienceTable, final int stat, @NotNull final Stats stats, @NotNull final ItemSet itemSet) {
        super(experienceTable, stat == Stats.CS_STAT_GOLEM_HP);
        this.stat = stat;
        this.stats = stats;
        this.itemSet = itemSet;
        this.stats.addCrossfireStatsListener(statsListener);
        this.itemSet.addItemSetListener(itemSetListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        itemSet.removeItemSetListener(itemSetListener);
        stats.removeCrossfireStatsListener(statsListener);
    }

    /**
     * Updates information for {@link Stats#C_STAT_EXP_NEXT_LEVEL}.
     */
    private void updateExperienceNextLevel() {
        final int level = stats.getStat(Stats.CS_STAT_LEVEL);
        final long experience = stats.getExperience();
        final int percents = getPercentsToNextLevel(level, experience);
        setValues(percents, 0, 99, percents+"%", level+"<br>Experience:"+Formatter.formatLong(experience)+"<br>Next level:"+Formatter.formatLong(getExperienceToNextLevel(level, experience)));
    }

}
