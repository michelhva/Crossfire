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
package com.realtime.crossfire.jxclient.stats;

import com.realtime.crossfire.jxclient.skills.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * The Global Experience needed to reach next level stat; the value is in
     * XP.
     */
    public static final int C_STAT_EXP_NEXT_LEVEL = 0x10000;

    /**
     * The Global Experience needed to reach next level stat; the value is in
     * percent%10.
     */
    public static final int C_STAT_EXP_NEXT_LEVEL_0X = 0x10001;

    /**
     * The Global Experience needed to reach next level stat; the value is in
     * percent/10.
     */
    public static final int C_STAT_EXP_NEXT_LEVEL_X0 = 0x10002;

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
     * The Range stat - this is what is currently readied by the player to fire.
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
     * The Global Experience (64bit encoding) stat.
     */
    public static final int CS_STAT_EXP64 = 28;

    public static final int CS_STAT_SPELL_ATTUNE = 29;

    public static final int CS_STAT_SPELL_REPEL = 30;

    public static final int CS_STAT_SPELL_DENY = 31;

    /* Start & end of resistances, inclusive. */

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
    public static final int CS_STAT_TURN_UNDEAD = 112;

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

    /* Start & end of skill experience + skill level, inclusive. */

    /**
     * Beginning index of skill experience stats.
     */
    public static final int CS_STAT_SKILLEXP_START = 118;

    /**
     * End index of skill experience stats.
     */
    public static final int CS_STAT_SKILLEXP_END = 129;

    /**
     * Agility skills experience.
     */
    public static final int CS_STAT_SKILLEXP_AGILITY = 118;

    /**
     * Agility skills level.
     */
    public static final int CS_STAT_SKILLEXP_AGLEVEL = 119;

    /**
     * Personal skills experience.
     */
    public static final int CS_STAT_SKILLEXP_PERSONAL = 120;

    /**
     * Personal skills level.
     */
    public static final int CS_STAT_SKILLEXP_PELEVEL = 121;

    /**
     * Mental skills experience.
     */
    public static final int CS_STAT_SKILLEXP_MENTAL = 122;

    /**
     * Mental skills level.
     */
    public static final int CS_STAT_SKILLEXP_MELEVEL = 123;

    /**
     * Physical skills experience.
     */
    public static final int CS_STAT_SKILLEXP_PHYSIQUE = 124;

    /**
     * Physical skills level.
     */
    public static final int CS_STAT_SKILLEXP_PHLEVEL = 125;

    /**
     * Magical skills experience.
     */
    public static final int CS_STAT_SKILLEXP_MAGIC = 126;

    /**
     * Magical skills level.
     */
    public static final int CS_STAT_SKILLEXP_MALEVEL = 127;

    /**
     * Wisdom skills experience.
     */
    public static final int CS_STAT_SKILLEXP_WISDOM = 128;

    /**
     * Wisdom skills level.
     */
    public static final int CS_STAT_SKILLEXP_WILEVEL = 129;

    /**
     * CS_STAT_SKILLINFO is used as the starting index point.  Skill number->name
     * map is generated dynamically for the client, so a bunch of entries will
     * be used here.
     */
    public static final int CS_STAT_SKILLINFO = 140;

    /**
     * CS_NUM_SKILLS does not match how many skills there really
     * are - instead, it is used as a range of values so that the client
     * can have some idea how many skill categories there may be.
     */
    public static final int CS_NUM_SKILLS = 50;

    /**
     * Factor used to convert float int int values.
     */
    public static final int FLOAT_MULTI = 100000;

    /**
     * Maps stat number to skill instance. Entries may be <code>null</code> if
     * the server did not provide a mapping.
     */
    private static final Skill[] numberedSkills = new Skill[CS_NUM_SKILLS];

    /**
     * Maps skill name to skill instance.
     */
    private static final Map<String, Skill> namedSkills = new HashMap<String, Skill>();

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
     * The listeners to inform of stat changes.
     */
    private final List<CrossfireStatsListener> statListeners = new ArrayList<CrossfireStatsListener>();

    private final int[] stats = new int[257];

    private long exp = 0;

    private String range = "";

    private String title = "";

    /**
     * Forget about all skill name mappings.
     */
    public static void clearSkills()
    {
        for (int i = 0; i < numberedSkills.length; i++)
        {
            if (numberedSkills[i] != null)
            {
                numberedSkills[i].set(0, 0);
                numberedSkills[i] = null;
            }
        }
    }

    /**
     * Forget about all stats.
     */
    public void reset()
    {
        for (int i = 0; i < numberedSkills.length; i++)
        {
            if (numberedSkills[i] != null)
            {
                numberedSkills[i].set(0, 0);
            }
        }
        Arrays.fill(stats, 0);
        exp = 0;
        range = "";
        title = "";
        setStatsProcessed();
    }

    /**
     * Adds a new skill to the list of known skills.
     * @param id The numerical identifier for the new skill.
     * @param n The skill name.
     */
    public static void addSkill(final int id, final String skillName)
    {
        final int index = id-Stats.CS_STAT_SKILLINFO;
        final Skill oldSkill = numberedSkills[index];
        final Skill newSkill = getNamedSkill(skillName);
        if (oldSkill == newSkill)
        {
            return;
        }

        if (oldSkill != null)
        {
            oldSkill.set(0, 0);
        }
        numberedSkills[index] = newSkill;
    }

    /**
     * Return the skill instance for a given skill name.
     *
     * @param skillName The skill name to look up.
     *
     * @return The skill instance.
     */
    public static Skill getNamedSkill(final String skillName)
    {
        final Skill oldSkill = namedSkills.get(skillName);
        if (oldSkill != null)
        {
            return oldSkill;
        }

        final Skill newSkill = new Skill(skillName);
        namedSkills.put(skillName, newSkill);
        return newSkill;
    }

    /**
     * Returns the given skill as a Skill object.
     * @param id The numerical skill identifier.
     * @return The Skill object matching the given identifier; may be
     * <code>null</code> for undefined skills.
     */
    public static Skill getSkill(final int id)
    {
        return numberedSkills[id-CS_STAT_SKILLINFO];
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
        return (double)stats[statnr]/Stats.FLOAT_MULTI;
    }

    /**
     * Sets the given statistic numerical value.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @param value The value to assign to the chosen statistic.
     */
    public void setStat(final int statnr, final int value)
    {
        stats[statnr] = value;
    }

    /**
     * Returns the current Title.
     * @return A String representation of the Title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Returns the current content of the Range stat. This is basically the
     * current active skill for the player.
     * @return A String representation of the Range.
     */
    public String getRange()
    {
        return range;
    }

    /**
     * Sets the current Title.
     * @param tile The new Title content.
     */
    public void setTitle(final String title)
    {
        this.title = title;
    }

    /**
     * Sets the current value for the Range - this is basically the currently
     * active skill for the player.
     * @param range The new content of Range.
     */
    public void setRange(final String range)
    {
        this.range = range;
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
    public void setExperience(final long exp)
    {
        this.exp = exp;
    }

    public void addCrossfireStatsListener(final CrossfireStatsListener listener)
    {
        statListeners.add(listener);
    }

    public void removeCrossfireStatsListener(final CrossfireStatsListener listener)
    {
        statListeners.remove(listener);
    }

    public void setStatsProcessed()
    {
        final CrossfireCommandStatsEvent event = new CrossfireCommandStatsEvent(new Object(), this);
        for (final CrossfireStatsListener listener : statListeners)
        {
            listener.commandStatsReceived(event);
        }
    }
}
