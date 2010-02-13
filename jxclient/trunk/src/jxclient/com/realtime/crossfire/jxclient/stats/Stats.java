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

package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.GuiStateListener;
import com.realtime.crossfire.jxclient.server.GuiStateManager;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/**
 * This is the representation of all the statistics of a player, like its speed
 * or its experience.
 *
 * <p>Constants named <code>C_STAT_xxx</code> are client-sided; constants named
 * <code>CS_STAT_xxx</code> are stats as sent by the server.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Stats
{
    /**
     * Whether the {@link CrossfireStatsListener#CS_STAT_WEAP_SP} value
     * contains the weapon speed directly.
     */
    private boolean simpleWeaponSpeed = false;

    /**
     * The listeners to inform of stat changes.
     */
    @NotNull
    private final Collection<StatsListener> statsListeners = new ArrayList<StatsListener>();

    /**
     * The {@link ExperienceTable} instance to use.
     */
    @NotNull
    private final ExperienceTable experienceTable;

    /**
     * The {@link SkillSet} instance to use.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * The current stat values.
     */
    @NotNull
    private final int[] stats = new int[258];

    /**
     * The total experience.
     */
    private long exp = 0;

    /**
     * The experience needed to reach the next level.
     */
    private long expNextLevel = 0;

    /**
     * The current value of the range stat.
     */
    @NotNull
    private String range = "";

    /**
     * The current value of the title stat.
     */
    @NotNull
    private String title = "";

    /**
     * The active skill name.
     */
    @NotNull
    private String activeSkill = "";

    /**
     * The {@link CrossfireStatsListener} attached to the server connection for
     * detecting stat changes.
     */
    @NotNull
    private final CrossfireStatsListener crossfireStatsListener = new CrossfireStatsListener()
    {
        /**
         * All unhandled stat values for which an error has been printed.
         */
        @NotNull
        private final Collection<String> unhandledStats = new HashSet<String>(0);

        /** {@inheritDoc} */
        @Override
        public void setSimpleWeaponSpeed(final boolean simpleWeaponSpeed)
        {
            Stats.this.setSimpleWeaponSpeed(simpleWeaponSpeed);
        }

        /** {@inheritDoc} */
        @Override
        public void statInt2Received(final int stat, final short param)
        {
            switch (stat)
            {
            case CS_STAT_HP:
            case CS_STAT_MAXHP:
            case CS_STAT_SP:
            case CS_STAT_MAXSP:
            case CS_STAT_STR:
            case CS_STAT_INT:
            case CS_STAT_WIS:
            case CS_STAT_DEX:
            case CS_STAT_CON:
            case CS_STAT_CHA:
            case CS_STAT_LEVEL:
            case CS_STAT_WC:
            case CS_STAT_AC:
            case CS_STAT_DAM:
            case CS_STAT_ARMOUR:
            case CS_STAT_FOOD:
            case CS_STAT_POW:
            case CS_STAT_GRACE:
            case CS_STAT_MAXGRACE:
                setStat(stat, param);
                if (stat == CS_STAT_LEVEL)
                {
                    calcExperienceToNextLevel();
                }
                break;

            case CS_STAT_FLAGS:
                setStat(stat, param&0xFFFF);
                break;

            default:
                if (CS_STAT_RESIST_START <= stat && stat < CS_STAT_RESIST_START+RESIST_TYPES)
                {
                    setStat(stat, param);
                }
                else
                {
                    reportUnhandledStat(stat, "int2");
                }
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void statInt4Received(final int stat, final int param)
        {
            switch (stat)
            {
            case CS_STAT_EXP:
                setExperience(param&0xFFFFFFFFL);
                break;

            case CS_STAT_SPEED:
            case CS_STAT_WEAP_SP:
            case CS_STAT_WEIGHT_LIM:
            case CS_STAT_SPELL_ATTUNE:
            case CS_STAT_SPELL_REPEL:
            case CS_STAT_SPELL_DENY:
                setStat(stat, param);
                break;

            default:
                reportUnhandledStat(stat, "int4");
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void statInt8Received(final int stat, final long param)
        {
            switch (stat)
            {
            case CS_STAT_EXP64:
                setExperience(param);
                break;

            default:
                reportUnhandledStat(stat, "int8");
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void statStringReceived(final int stat, @NotNull final String param)
        {
            switch (stat)
            {
            case CS_STAT_RANGE:
                setRange(param);
                break;

            case CS_STAT_TITLE:
                setTitle(param);
                break;

            default:
                reportUnhandledStat(stat, "string");
                break;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void statSkillReceived(final int stat, final int level, final long experience)
        {
            if (CS_STAT_SKILLINFO <= stat && stat < CS_STAT_SKILLINFO+CS_NUM_SKILLS)
            {
                final Skill sk = skillSet.getSkill(stat);
                if (sk == null)
                {
                    System.err.println("ignoring skill value for unknown skill "+stat);
                }
                else
                {
                    sk.set(level, experience);
                }
            }
            else
            {
                reportUnhandledStat(stat, "skill");
            }
        }

        /**
         * Report an unhandled stat value.
         * @param stat the stat value
         * @param type the stat type
         */
        private void reportUnhandledStat(final int stat, @NotNull final String type)
        {
            if (unhandledStats.add(type+"-"+stat))
            {
                System.err.println("Warning: unhandled stat "+stat+" of type "+type);
            }
        }
    };

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo)
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            // ignore
        }
    };

    /**
     * Create a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param experienceTable the experience table instance to use
     * @param skillSet the skill set instance to use
     * @param guiStateManager the gui state manager to watch
     */
    public Stats(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, @NotNull final GuiStateManager guiStateManager)
    {
        this.experienceTable = experienceTable; // XXX: should detect changed information
        this.skillSet = skillSet;
        crossfireServerConnection.addCrossfireStatsListener(crossfireStatsListener);
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Set whether the {@link CrossfireStatsListener#CS_STAT_WEAP_SP} value contains the weapon speed
     * directly.
     *
     * @param simpleWeaponSpeed Whether <code>CS_STAT_WEAP_SP</code> is the
     * weapon speed value.
     */
    private void setSimpleWeaponSpeed(final boolean simpleWeaponSpeed)
    {
        if (this.simpleWeaponSpeed == simpleWeaponSpeed)
        {
            return;
        }

        this.simpleWeaponSpeed = simpleWeaponSpeed;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.simpleWeaponSpeedChanged(this.simpleWeaponSpeed);
        }
    }

    /**
     * Forget about all stats.
     */
    private void reset()
    {
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.reset();
        }
        for (int statnr = 0; statnr < stats.length; statnr++)
        {
            setStat(statnr, 0);
        }
        setExperience(0);
        setRange("");
        setTitle("");
        setActiveSkill("");
    }

    /**
     * Returns the numerical value of the given statistic.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @return The statistic value (or "score").
     */
    public int getStat(final int statnr)
    {
        return stats[statnr];
    }

    /**
     * Returns the numerical value of the given statistic.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @return The statistic value.
     */
    public double getFloatStat(final int statnr)
    {
        return (double)stats[statnr]/CrossfireStatsListener.FLOAT_MULTI;
    }

    /**
     * Sets the given statistic numerical value.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @param value The value to assign to the chosen statistic.
     */
    public void setStat(final int statnr, final int value)
    {
        if (stats[statnr] == value) {
            return;
        }

        stats[statnr] = value;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.statChanged(statnr, stats[statnr]);
        }
    }

    /**
     * Returns the current Title.
     * @return A String representation of the Title.
     */
    @NotNull
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns the current content of the Range stat. This is basically the
     * current active skill for the player.
     * @return A String representation of the Range.
     */
    @NotNull
    public String getRange()
    {
        return range;
    }

    /**
     * Returns the active skill name.
     *
     * @return The active skill name.
     */
    @NotNull
    public String getActiveSkill()
    {
        return activeSkill;
    }

    /**
     * Sets the current Title.
     * @param title The new Title content.
     */
    private void setTitle(@NotNull final String title)
    {
        if (this.title.equals(title))
        {
            return;
        }

        this.title = title;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.titleChanged(this.title);
        }
    }

    /**
     * Sets the current value for the Range - this is basically the currently
     * active skill for the player.
     * @param range The new content of Range.
     */
    private void setRange(@NotNull final String range)
    {
        if (this.range.equals(range))
        {
            return;
        }

        this.range = range;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.rangeChanged(this.range);
        }
    }

    /**
     * Set the active skill name.
     *
     * @param activeSkill The active skill name.
     */
    public void setActiveSkill(@NotNull final String activeSkill)
    {
        if (this.activeSkill.equals(activeSkill))
        {
            return;
        }

        this.activeSkill = activeSkill;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.activeSkillChanged(this.activeSkill);
        }
    }

    /**
     * Returns the amount of global experience.
     * @return Amount of global experience.
     */
    public long getExperience()
    {
        return exp;
    }

    /**
     * Sets the amount of global experience.
     * @param exp The new amount of global experience.
     */
    private void setExperience(final long exp)
    {
        if (this.exp == exp)
        {
            return;
        }

        this.exp = exp;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.experienceChanged(this.exp);
        }

        calcExperienceToNextLevel();
    }

    /**
     * Returns the experience needed to reach the next level.
     * @return the experience needed
     */
    public long getExperienceNextLevel()
    {
        return expNextLevel;
    }

    /**
     * Calculates experience needed to reach the next level.
     */
    private void calcExperienceToNextLevel()
    {
        final long newExpNextLevel = experienceTable.getExperienceToNextLevel(stats[CrossfireStatsListener.CS_STAT_LEVEL], exp);
        if (expNextLevel == newExpNextLevel)
        {
            return;
        }

        expNextLevel = newExpNextLevel;
        for (final StatsListener statsListener : statsListeners)
        {
            statsListener.experienceNextLevelChanged(expNextLevel);
        }
    }

    /**
     * Adds a {@link StatsListener} to be notified about stat changes.
     * @param statsListener the listener to add
     */
    public void addCrossfireStatsListener(@NotNull final StatsListener statsListener)
    {
        statsListeners.add(statsListener);
    }

    /**
     * Removes a {@link StatsListener} to be notified about stat changes.
     * @param statsListener the listener to remove
     */
    public void removeCrossfireStatsListener(@NotNull final StatsListener statsListener)
    {
        statsListeners.remove(statsListener);
    }

    /**
     * Return the weapon speed stat.
     *
     * @return The weapon speed stat.
     */
    public double getWeaponSpeed()
    {
        final double weaponSpeed = getFloatStat(CrossfireStatsListener.CS_STAT_WEAP_SP);
        if (simpleWeaponSpeed)
        {
            return weaponSpeed;
        }

        if (weaponSpeed < 0.001)
        {
            return 0;
        }

        return getFloatStat(CrossfireStatsListener.CS_STAT_SPEED)/weaponSpeed;
    }
}
