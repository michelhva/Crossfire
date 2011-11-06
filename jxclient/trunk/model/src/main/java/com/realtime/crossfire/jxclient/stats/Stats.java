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

package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.guistate.ClientSocketState;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import java.util.Collection;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/**
 * This is the representation of all the statistics of a player, like its speed
 * or its experience.
 * <p/>
 * <p>Constants named <code>C_STAT_xxx</code> are client-sided; constants named
 * <code>CS_STAT_xxx</code> are stats as sent by the server.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Stats {

    /**
     * The Hit Points stat.
     */
    public static final int CS_STAT_HP = 1;

    /**
     * The Maximum Hit Points stat.
     */
    public static final int CS_STAT_MAXHP = 2;

    /**
     * The Spell Points stat.
     */
    public static final int CS_STAT_SP = 3;

    /**
     * The Maximum Spell Points stat.
     */
    public static final int CS_STAT_MAXSP = 4;

    /**
     * The Strength Primary stat.
     */
    public static final int CS_STAT_STR = 5;

    /**
     * The Intelligence Primary stat.
     */
    public static final int CS_STAT_INT = 6;

    /**
     * The Wisdom Primary stat.
     */
    public static final int CS_STAT_WIS = 7;

    /**
     * The Dexterity Primary stat.
     */
    public static final int CS_STAT_DEX = 8;

    /**
     * The Constitution Primary stat.
     */
    public static final int CS_STAT_CON = 9;

    /**
     * The Charisma Primary stat.
     */
    public static final int CS_STAT_CHA = 10;

    /**
     * The Global Experience (32bit encoding) stat.
     */
    public static final int CS_STAT_EXP = 11;

    /**
     * The global experience needed to reach next level stat.
     */
    public static final int C_STAT_EXP_NEXT_LEVEL = 0x10000;

    /**
     * The Global Level stat.
     */
    public static final int CS_STAT_LEVEL = 12;

    /**
     * The Weapon Class stat.
     */
    public static final int CS_STAT_WC = 13;

    /**
     * The Armor Class stat.
     */
    public static final int CS_STAT_AC = 14;

    /**
     * The Damage stat.
     */
    public static final int CS_STAT_DAM = 15;

    /**
     * The Armour stat.
     */
    public static final int CS_STAT_ARMOUR = 16;

    /**
     * The Speed stat.
     */
    public static final int CS_STAT_SPEED = 17;

    /**
     * The Food stat.
     */
    public static final int CS_STAT_FOOD = 18;

    /**
     * The Low Food indicator. It ranges from 0 (ok) to 1 (low food).
     */
    public static final int C_STAT_LOWFOOD = 0x10003;

    /**
     * The Weapon Speed stat.
     */
    public static final int CS_STAT_WEAP_SP = 19;

    /**
     * The Range stat - this is what is currently readied by the player to
     * fire.
     */
    public static final int CS_STAT_RANGE = 20;

    /**
     * The Title stat.
     */
    public static final int CS_STAT_TITLE = 21;

    /**
     * The Power Primary stat.
     */
    public static final int CS_STAT_POW = 22;

    /**
     * The Grace stat.
     */
    public static final int CS_STAT_GRACE = 23;

    /**
     * The Maximum Grace stat.
     */
    public static final int CS_STAT_MAXGRACE = 24;

    /**
     * The various flags used in stats.
     */
    public static final int CS_STAT_FLAGS = 25;

    /**
     * The Weight Limit stat.
     */
    public static final int CS_STAT_WEIGHT_LIM = 26;

    /**
     * The character's weight.
     */
    public static final int C_STAT_WEIGHT = 257;

    /**
     * The Global Experience (64bit encoding) stat.
     */
    public static final int CS_STAT_EXP64 = 28;

    /**
     * Attuned spell paths of a spell.
     */
    public static final int CS_STAT_SPELL_ATTUNE = 29;

    /**
     * Repelled spell paths of a spell.
     */
    public static final int CS_STAT_SPELL_REPEL = 30;

    /**
     * Denied spell paths of a spell.
     */
    public static final int CS_STAT_SPELL_DENY = 31;

    /**
     * The race's maximum strength primary stat.
     */
    public static final int CS_STAT_RACE_STR = 32;

    /**
     * The race's maximum intelligence primary stat.
     */
    public static final int CS_STAT_RACE_INT = 33;

    /**
     * The race's maximum wisdom primary stat.
     */
    public static final int CS_STAT_RACE_WIS = 34;

    /**
     * The race's maximum dexterity primary stat.
     */
    public static final int CS_STAT_RACE_DEX = 35;

    /**
     * The race's maximum constitution primary stat.
     */
    public static final int CS_STAT_RACE_CON = 36;

    /**
     * The race's maximum charisma primary stat.
     */
    public static final int CS_STAT_RACE_CHA = 37;

    /**
     * The race's maximum power primary stat.
     */
    public static final int CS_STAT_RACE_POW = 38;

    /**
     * The strength primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_STR = 39;

    /**
     * The integer primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_INT = 40;

    /**
     * The wisdom primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_WIS = 41;

    /**
     * The dexterity primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_DEX = 42;

    /**
     * The constitution primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_CON = 43;

    /**
     * The charisma primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_CHA = 44;

    /**
     * The power primary stat without boosts or depletions.
     */
    public static final int CS_STAT_BASE_POW = 45;

    /**
     * The strength primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_STR = 46;

    /**
     * The integer primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_INT = 47;

    /**
     * The wisdom primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_WIS = 48;

    /**
     * The dexterity primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_DEX = 49;

    /**
     * The constitution primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_CON = 50;

    /**
     * The charisma primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_CHA = 51;

    /**
     * The power primary stat changes due to gear or skills.
     */
    public static final int CS_STAT_APPLIED_POW = 52;

    /**
     * The golem's hitpoints, 0 if no golem.
     */
    public static final int CS_STAT_GOLEM_HP = 53;

    /**
     * The golem's maximum hitpoints, 0 if no golem.
     */
    public static final int CS_STAT_GOLEM_MAXHP = 54;

    /**
     * Beginning index of the resistances.
     */
    public static final int CS_STAT_RESIST_START = 100;

    /**
     * End index of the resistances.
     */
    public static final int CS_STAT_RESIST_END = 117;

    /**
     * Resistance to physical attacks.
     */
    public static final int CS_STAT_RES_PHYS = 100;

    /**
     * Resistance to magical attacks.
     */
    public static final int CS_STAT_RES_MAG = 101;

    /**
     * Resistance to fire.
     */
    public static final int CS_STAT_RES_FIRE = 102;

    /**
     * Resistance to electricity.
     */
    public static final int CS_STAT_RES_ELEC = 103;

    /**
     * Resistance to cold.
     */
    public static final int CS_STAT_RES_COLD = 104;

    /**
     * Resistance to confusion.
     */
    public static final int CS_STAT_RES_CONF = 105;

    /**
     * Resistance to acid.
     */
    public static final int CS_STAT_RES_ACID = 106;

    /**
     * Resistance to drain life.
     */
    public static final int CS_STAT_RES_DRAIN = 107;

    /**
     * Resistance to ghost hit.
     */
    public static final int CS_STAT_RES_GHOSTHIT = 108;

    /**
     * Resistance to poison.
     */
    public static final int CS_STAT_RES_POISON = 109;

    /**
     * Resistance to slowness.
     */
    public static final int CS_STAT_RES_SLOW = 110;

    /**
     * Resistance to paralysis.
     */
    public static final int CS_STAT_RES_PARA = 111;

    /**
     * Resistance to turn undead.
     */
    public static final int CS_STAT_RES_TURN_UNDEAD = 112;

    /**
     * Resistance to fear.
     */
    public static final int CS_STAT_RES_FEAR = 113;

    /**
     * Resistance to depletion.
     */
    public static final int CS_STAT_RES_DEPLETE = 114;

    /**
     * Resistance to death.
     */
    public static final int CS_STAT_RES_DEATH = 115;

    /**
     * Resistance to holy word.
     */
    public static final int CS_STAT_RES_HOLYWORD = 116;

    /**
     * Resistance to blindness.
     */
    public static final int CS_STAT_RES_BLIND = 117;

    /**
     * Factor used to convert float to int values.
     */
    public static final int FLOAT_MULTI = 100000;

    /**
     * The total number of resistances.
     */
    public static final int RESIST_TYPES = 18;

    /**
     * The "is poisoned" indicator. It ranges from 0 (not poisoned) to 1
     * (poisoned).
     */
    public static final int C_STAT_POISONED = 256;

    /**
     * CS_NUM_SKILLS does not match how many skills there really are - instead,
     * it is used as a range of values so that the client can have some idea how
     * many skill categories there may be.
     */
    public static final int CS_NUM_SKILLS = 50;

    /**
     * CS_STAT_SKILLINFO is used as the starting index point.  Skill
     * number->name map is generated dynamically for the client, so a bunch of
     * entries will be used here.
     */
    public static final int CS_STAT_SKILLINFO = 140;

    /**
     * Whether the {@link #CS_STAT_WEAP_SP} value contains the weapon speed
     * directly.
     */
    private boolean simpleWeaponSpeed = false;

    /**
     * The listeners to inform of stat changes.
     */
    @NotNull
    private final EventListenerList2<StatsListener> statsListeners = new EventListenerList2<StatsListener>(StatsListener.class);

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
     * All unhandled stat values for which an error has been printed.
     */
    @NotNull
    private final Collection<String> unhandledStats = new HashSet<String>(0);

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            // ignore
        }

        @Override
        public void metaserver() {
            // ignore
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            reset();
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            // ignore
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param experienceTable the experience table instance to use
     * @param skillSet the skill set instance to use
     * @param guiStateManager the gui state manager to watch
     */
    public Stats(@NotNull final ExperienceTable experienceTable, @NotNull final SkillSet skillSet, @NotNull final GuiStateManager guiStateManager) {
        this.experienceTable = experienceTable; // XXX: should detect changed information
        this.skillSet = skillSet;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Sets whether the {@link #CS_STAT_WEAP_SP} value contains the weapon speed
     * directly.
     * @param simpleWeaponSpeed whether <code>CS_STAT_WEAP_SP</code> is the
     * weapon speed value
     */
    public void setSimpleWeaponSpeed(final boolean simpleWeaponSpeed) {
        if (this.simpleWeaponSpeed == simpleWeaponSpeed) {
            return;
        }

        this.simpleWeaponSpeed = simpleWeaponSpeed;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.simpleWeaponSpeedChanged(this.simpleWeaponSpeed);
        }
    }

    /**
     * Forgets about all stats.
     */
    private void reset() {
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.reset();
        }
        for (int statNo = 0; statNo < stats.length; statNo++) {
            setStat(statNo, 0);
        }
        setExperience(0);
        setRange("");
        setTitle("");
        setActiveSkill("");
    }

    /**
     * Returns the numerical value of the given statistic.
     * @param statNo the stat identifier; see the CS_STAT constants
     * @return the statistic value (or "score")
     */
    public int getStat(final int statNo) {
        return stats[statNo];
    }

    /**
     * Returns the numerical value of the given statistic.
     * @param statNo the stat identifier; see the CS_STAT constants
     * @return the statistic value
     */
    public double getFloatStat(final int statNo) {
        return (double)stats[statNo]/FLOAT_MULTI;
    }

    /**
     * Sets the given statistic numerical value.
     * @param statNo the stat identifier; see the CS_STAT constants
     * @param value the value to assign to the chosen statistic
     */
    public void setStat(final int statNo, final int value) {
        if (stats[statNo] == value) {
            return;
        }

        stats[statNo] = value;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.statChanged(statNo, stats[statNo]);
        }
    }

    /**
     * Returns the current title.
     * @return a string representation of the title
     */
    @NotNull
    public String getTitle() {
        return title;
    }

    /**
     * Returns the current content of the range stat. This is basically the
     * current active skill for the player.
     * @return a string representation of the range
     */
    @NotNull
    public String getRange() {
        return range;
    }

    /**
     * Returns the active skill name.
     * @return the active skill name
     */
    @NotNull
    public String getActiveSkill() {
        return activeSkill;
    }

    /**
     * Sets the current Title.
     * @param title the new Title content
     */
    private void setTitle(@NotNull final String title) {
        if (this.title.equals(title)) {
            return;
        }

        this.title = title;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.titleChanged(this.title);
        }
    }

    /**
     * Sets the current value for the Range - this is basically the currently
     * active skill for the player.
     * @param range the new content of range
     */
    private void setRange(@NotNull final String range) {
        if (this.range.equals(range)) {
            return;
        }

        this.range = range;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.rangeChanged(this.range);
        }
    }

    /**
     * Sets the active skill name.
     * @param activeSkill the active skill name
     */
    public void setActiveSkill(@NotNull final String activeSkill) {
        if (this.activeSkill.equals(activeSkill)) {
            return;
        }

        this.activeSkill = activeSkill;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.activeSkillChanged(this.activeSkill);
        }
    }

    /**
     * Returns the amount of global experience.
     * @return the amount of global experience
     */
    public long getExperience() {
        return exp;
    }

    /**
     * Sets the amount of global experience.
     * @param exp the new amount of global experience
     */
    private void setExperience(final long exp) {
        if (this.exp == exp) {
            return;
        }

        this.exp = exp;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.experienceChanged(this.exp);
        }

        calculateExperienceToNextLevel();
    }

    /**
     * Returns the experience needed to reach the next level.
     * @return the experience needed
     */
    public long getExperienceNextLevel() {
        return expNextLevel;
    }

    /**
     * Calculates the experience needed to reach the next level.
     */
    private void calculateExperienceToNextLevel() {
        final long newExpNextLevel = experienceTable.getExperienceToNextLevel(stats[CS_STAT_LEVEL], exp);
        if (expNextLevel == newExpNextLevel) {
            return;
        }

        expNextLevel = newExpNextLevel;
        for (final StatsListener statsListener : statsListeners.getListeners()) {
            statsListener.experienceNextLevelChanged(expNextLevel);
        }
    }

    /**
     * Adds a {@link StatsListener} to be notified about stat changes.
     * @param statsListener the listener to add
     */
    public void addCrossfireStatsListener(@NotNull final StatsListener statsListener) {
        statsListeners.add(statsListener);
    }

    /**
     * Removes a {@link StatsListener} to be notified about stat changes.
     * @param statsListener the listener to remove
     */
    public void removeCrossfireStatsListener(@NotNull final StatsListener statsListener) {
        statsListeners.remove(statsListener);
    }

    /**
     * Returns the weapon speed stat.
     * @return the weapon speed stat
     */
    public double getWeaponSpeed() {
        final double weaponSpeed = getFloatStat(CS_STAT_WEAP_SP);
        if (simpleWeaponSpeed) {
            return weaponSpeed;
        }

        if (weaponSpeed < 0.001) {
            return 0;
        }

        return getFloatStat(CS_STAT_SPEED)/weaponSpeed;
    }

    /**
     * Updates a stat value with a two-byte int value.
     * @param stat the stat
     * @param param the stat value
     */
    public void setStatInt2(final int stat, final short param) {
        switch (stat) {
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
        case CS_STAT_RACE_STR:
        case CS_STAT_RACE_INT:
        case CS_STAT_RACE_WIS:
        case CS_STAT_RACE_DEX:
        case CS_STAT_RACE_CON:
        case CS_STAT_RACE_CHA:
        case CS_STAT_RACE_POW:
        case CS_STAT_BASE_STR:
        case CS_STAT_BASE_INT:
        case CS_STAT_BASE_WIS:
        case CS_STAT_BASE_DEX:
        case CS_STAT_BASE_CON:
        case CS_STAT_BASE_CHA:
        case CS_STAT_BASE_POW:
        case CS_STAT_APPLIED_STR:
        case CS_STAT_APPLIED_INT:
        case CS_STAT_APPLIED_WIS:
        case CS_STAT_APPLIED_DEX:
        case CS_STAT_APPLIED_CON:
        case CS_STAT_APPLIED_CHA:
        case CS_STAT_APPLIED_POW:
        case CS_STAT_GOLEM_HP:
        case CS_STAT_GOLEM_MAXHP:
            setStat(stat, param);
            if (stat == CS_STAT_LEVEL) {
                calculateExperienceToNextLevel();
            }
            break;

        case CS_STAT_FLAGS:
            setStat(stat, param&0xFFFF);
            break;

        default:
            if (CS_STAT_RESIST_START <= stat && stat < CS_STAT_RESIST_START+RESIST_TYPES) {
                setStat(stat, param);
            } else {
                reportUnhandledStat(stat, "int2");
            }
            break;
        }
    }

    /**
     * Updates a stat value with a four-byte int value.
     * @param stat the stat
     * @param param the stat value
     */
    public void setStatInt4(final int stat, final int param) {
        switch (stat) {
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

    /**
     * Updates a stat value with an eight-byte int value.
     * @param stat the stat
     * @param param the stat value
     */
    public void setStatInt8(final int stat, final long param) {
        switch (stat) {
        case CS_STAT_EXP64:
            setExperience(param);
            break;

        default:
            reportUnhandledStat(stat, "int8");
            break;
        }
    }

    /**
     * Updates a stat value with a string value.
     * @param stat the stat
     * @param param the stat value
     */
    public void setStatString(final int stat, @NotNull final String param) {
        switch (stat) {
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

    /**
     * Updates a stat value with a skill value.
     * @param stat the stat
     * @param level the stat value
     * @param experience the stat value
     */
    public void setStatSkill(final int stat, final int level, final long experience) {
        if (CS_STAT_SKILLINFO <= stat && stat < CS_STAT_SKILLINFO+CS_NUM_SKILLS) {
            final Skill sk = skillSet.getSkill(stat);
            if (sk == null) {
                System.err.println("ignoring skill value for unknown skill "+stat);
            } else {
                sk.set(level, experience);
            }
        } else {
            reportUnhandledStat(stat, "skill");
        }
    }

    /**
     * Report an unhandled stat value.
     * @param stat the stat value
     * @param type the stat type
     */
    private void reportUnhandledStat(final int stat, @NotNull final String type) {
        if (unhandledStats.add(type+"-"+stat)) {
            System.err.println("Warning: unhandled stat "+stat+" of type "+type);
        }
    }

}
