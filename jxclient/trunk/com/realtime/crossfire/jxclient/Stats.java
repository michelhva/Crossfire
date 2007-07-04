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
package com.realtime.crossfire.jxclient;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the representation of all the statistics of a player, like its speed
 * or its experience.
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Stats
{
    /**
     * The Hit Points stat.
     * @since 1.0
     */
    public static final int CS_STAT_HP = 1;

    /**
     * The Maximum Hit Points stat.
     * @since 1.0
     */
    public static final int CS_STAT_MAXHP = 2;

    /**
     * The Spell Points stat.
     * @since 1.0
     */
    public static final int CS_STAT_SP = 3;

    /**
     * The Maximum Spell Points stat.
     * @since 1.0
     */
    public static final int CS_STAT_MAXSP = 4;

    /**
     * The Strength Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_STR = 5;

    /**
     * The Intelligence Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_INT = 6;

    /**
     * The Wisdom Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_WIS = 7;

    /**
     * The Dexterity Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_DEX = 8;

    /**
     * The Constitution Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_CON = 9;

    /**
     * The Charisma Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_CHA = 10;

    /**
     * The Global Experience (32bit encoding) stat.
     * @since 1.0
     */
    public static final int CS_STAT_EXP = 11;

    /**
     * The Global Level stat.
     * @since 1.0
     */
    public static final int CS_STAT_LEVEL = 12;

    /**
     * The Weapon Class stat.
     * @since 1.0
     */
    public static final int CS_STAT_WC = 13;

    /**
     * The Armor Class stat.
     * @since 1.0
     */
    public static final int CS_STAT_AC = 14;

    /**
     * The Damage stat.
     * @since 1.0
     */
    public static final int CS_STAT_DAM = 15;

    /**
     * The Armour stat.
     * @since 1.0
     */
    public static final int CS_STAT_ARMOUR = 16;

    /**
     * The Speed stat.
     * @since 1.0
     */
    public static final int CS_STAT_SPEED = 17;

    /**
     * The Food stat.
     * @since 1.0
     */
    public static final int CS_STAT_FOOD = 18;

    /**
     * The Weapon Speed stat.
     * @since 1.0
     */
    public static final int CS_STAT_WEAP_SP = 19;

    /**
     * The Range stat - this is what is currently readied by the player to fire.
     * @since 1.0
     */
    public static final int CS_STAT_RANGE = 20;

    /**
     * The Title stat.
     * @since 1.0
     */
    public static final int CS_STAT_TITLE = 21;

    /**
     * The Power Primary stat.
     * @since 1.0
     */
    public static final int CS_STAT_POW = 22;

    /**
     * The Grace stat.
     * @since 1.0
     */
    public static final int CS_STAT_GRACE = 23;

    /**
     * The Maximum Grace stat.
     * @since 1.0
     */
    public static final int CS_STAT_MAXGRACE = 24;

    /**
     * The various flags used in stats.
     * @since 1.0
     */
    public static final int CS_STAT_FLAGS = 25;

    /**
     * The Weight Limit stat.
     * @since 1.0
     */
    public static final int CS_STAT_WEIGHT_LIM = 26;

    /**
     * The Global Experience (64bit encoding) stat.
     * @since 1.0
     */
    public static final int CS_STAT_EXP64 = 28;

    public static final int CS_STAT_SPELL_ATTUNE = 29;

    public static final int CS_STAT_SPELL_REPEL = 30;

    public static final int CS_STAT_SPELL_DENY = 31;

    /* Start & end of resistances, inclusive. */

    /**
     * Beginning index of the resistances.
     * @since 1.0
     */
    public static final int CS_STAT_RESIST_START = 100;

    /**
     * End index of the resistances.
     * @since 1.0
     */
    public static final int CS_STAT_RESIST_END = 117;

    /**
     * Resistance to physical attacks.
     * @since 1.0
     */
    public static final int CS_STAT_RES_PHYS = 100;

    /**
     * Resistance to magical attacks.
     * @since 1.0
     */
    public static final int CS_STAT_RES_MAG = 101;

    /**
     * Resistance to fire.
     * @since 1.0
     */
    public static final int CS_STAT_RES_FIRE = 102;

    /**
     * Resistance to electricity.
     * @since 1.0
     */
    public static final int CS_STAT_RES_ELEC = 103;

    /**
     * Resistance to cold.
     * @since 1.0
     */
    public static final int CS_STAT_RES_COLD = 104;

    /**
     * Resistance to confusion.
     * @since 1.0
     */
    public static final int CS_STAT_RES_CONF = 105;

    /**
     * Resistance to acid.
     * @since 1.0
     */
    public static final int CS_STAT_RES_ACID = 106;

    /**
     * Resistance to drain life.
     * @since 1.0
     */
    public static final int CS_STAT_RES_DRAIN = 107;

    /**
     * Resistance to ghost hit.
     * @since 1.0
     */
    public static final int CS_STAT_RES_GHOSTHIT = 108;

    /**
     * Resistance to poison.
     * @since 1.0
     */
    public static final int CS_STAT_RES_POISON = 109;

    /**
     * Resistance to slowness.
     * @since 1.0
     */
    public static final int CS_STAT_RES_SLOW = 110;

    /**
     * Resistance to paralysis.
     * @since 1.0
     */
    public static final int CS_STAT_RES_PARA = 111;

    /**
     * Resistance to turn undead.
     * @since 1.0
     */
    public static final int CS_STAT_TURN_UNDEAD = 112;

    /**
     * Resistance to fear.
     * @since 1.0
     */
    public static final int CS_STAT_RES_FEAR = 113;

    /**
     * Resistance to depletion.
     * @since 1.0
     */
    public static final int CS_STAT_RES_DEPLETE = 114;

    /**
     * Resistance to death.
     * @since 1.0
     */
    public static final int CS_STAT_RES_DEATH = 115;

    /**
     * Resistance to holy word.
     * @since 1.0
     */
    public static final int CS_STAT_RES_HOLYWORD = 116;

    /**
     * Resistance to blindness.
     * @since 1.0
     */
    public static final int CS_STAT_RES_BLIND = 117;

    /* Start & end of skill experience + skill level, inclusive. */

    /**
     * Beginning index of skill experience stats.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_START = 118;

    /**
     * End index of skill experience stats.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_END = 129;

    /**
     * Agility skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_AGILITY = 118;

    /**
     * Agility skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_AGLEVEL = 119;

    /**
     * Personal skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_PERSONAL = 120;

    /**
     * Personal skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_PELEVEL = 121;

    /**
     * Mental skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_MENTAL = 122;

    /**
     * Mental skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_MELEVEL = 123;

    /**
     * Physical skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_PHYSIQUE = 124;

    /**
     * Physical skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_PHLEVEL = 125;

    /**
     * Magical skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_MAGIC = 126;

    /**
     * Magical skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_MALEVEL = 127;

    /**
     * Wisdom skills experience.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_WISDOM = 128;

    /**
     * Wisdom skills level.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLEXP_WILEVEL = 129;

    /**
     * CS_STAT_SKILLINFO is used as the starting index point.  Skill number->name
     * map is generated dynamically for the client, so a bunch of entries will
     * be used here.
     * @since 1.0
     */
    public static final int CS_STAT_SKILLINFO = 140;

    /**
     * CS_NUM_SKILLS does not match how many skills there really
     * are - instead, it is used as a range of values so that the client
     * can have some idea how many skill categories there may be.
     * @since 1.0
     */
    public static final int CS_NUM_SKILLS = 50;

    /**
     * The table of all known skills.
     * @since 1.0
     */
    protected static Skill[] myskills = new Skill[CS_NUM_SKILLS];

    /**
     * The total number of resistances.
     * @since 1.0
     */
    public static final int RESIST_TYPES = 18;

    private int[] mystats = new int[512];

    private long myexp = 0;

    private String myrange = "";

    private String mytitle = "";

    private static final int[] skill_level = new int[CS_NUM_SKILLS];

    private static final long[] skill_exp = new long[CS_NUM_SKILLS];

    static
    {
        for (int i = 0; i < CS_NUM_SKILLS; i++)
        {
            myskills[i] = null;
        }
    }

    /**
     * Maps stat names to stat index values. Only stats useful in skin files
     * are included.
     */
    private static Map<String, Integer> statTable = new HashMap<String, Integer>();
    static
    {
        statTable.put("AC", CS_STAT_AC);
        statTable.put("CHA", CS_STAT_CHA);
        statTable.put("CON", CS_STAT_CON);
        statTable.put("DAM", CS_STAT_DAM);
        statTable.put("DEX", CS_STAT_DEX);
        statTable.put("EXP", CS_STAT_EXP64);
        statTable.put("FOOD", CS_STAT_FOOD);
        statTable.put("GRACE", CS_STAT_GRACE);
        statTable.put("HP", CS_STAT_HP);
        statTable.put("INT", CS_STAT_INT);
        statTable.put("LEVEL", CS_STAT_LEVEL);
        statTable.put("POW", CS_STAT_POW);
        statTable.put("RANGE", CS_STAT_RANGE);
        statTable.put("RES_ACID", CS_STAT_RES_ACID);
        statTable.put("RES_BLIND", CS_STAT_RES_BLIND);
        statTable.put("RES_COLD", CS_STAT_RES_COLD);
        statTable.put("RES_CONF", CS_STAT_RES_CONF);
        statTable.put("RES_DEATH", CS_STAT_RES_DEATH);
        statTable.put("RES_DEPLETE", CS_STAT_RES_DEPLETE);
        statTable.put("RES_DRAIN", CS_STAT_RES_DRAIN);
        statTable.put("RES_ELEC", CS_STAT_RES_ELEC);
        statTable.put("RES_FEAR", CS_STAT_RES_FEAR);
        statTable.put("RES_FIRE", CS_STAT_RES_FIRE);
        statTable.put("RES_GHOSTHIT", CS_STAT_RES_GHOSTHIT);
        statTable.put("RES_HOLYWORD", CS_STAT_RES_HOLYWORD);
        statTable.put("RES_MAG", CS_STAT_RES_MAG);
        statTable.put("RES_PARA", CS_STAT_RES_PARA);
        statTable.put("RES_PHYS", CS_STAT_RES_PHYS);
        statTable.put("RES_POISON", CS_STAT_RES_POISON);
        statTable.put("RES_SLOW", CS_STAT_RES_SLOW);
        statTable.put("SP", CS_STAT_SP);
        statTable.put("SPEED", CS_STAT_SPEED);
        statTable.put("STR", CS_STAT_STR);
        statTable.put("TITLE", CS_STAT_TITLE);
        statTable.put("TURN_UNDEAD", CS_STAT_TURN_UNDEAD);
        statTable.put("WC", CS_STAT_WC);
        statTable.put("WIS", CS_STAT_WIS);
    }

    /**
     * Adds a new skill to the list of known skills.
     * @param id The numerical identifier for the new skill.
     * @param n The skill name.
     * @since 1.0
     */
    public static void addSkill(int id, String n)
    {
        Skill sk = new Skill(id, n);
        myskills[id-Stats.CS_STAT_SKILLINFO] = sk;
    }

    /**
     * Returns the given skill as a Skill object.
     * @param id The numerical skill identifier.
     * @return The Skill object matching the given identifier.
     * @since 1.0
     */
    public static Skill getSkill(int id)
    {
        return myskills[id-CS_STAT_SKILLINFO];
    }

    /**
     * Returns the level in a given skill.
     * @param skill The skill identifier.
     * @return The skill level.
     * @since 1.0
     */
    public int getSkillLevel(int skill)
    {
        return skill_level[skill-CS_STAT_SKILLINFO];
    }

    /**
     * Returns the amount of experience assigned to a given skill.
     * @param skill The skill identifier.
     * @return The skill experience.
     * @since 1.0
     */
    public long getSkillExperience(int skill)
    {
        return skill_exp[skill-CS_STAT_SKILLINFO];
    }

    /**
     * Sets the level and experience of the given skill.
     * Note that although the amount of experience and the level are linked to
     * each other, the client has no way to know the level=f(exp) function the
     * server uses. Don't forget that the client is mostly a passive interface
     * for whatever is related to gaming rules.
     * @param skill The skill identifier.
     * @param level The level to assign to the skill.
     * @param exp The amount of experience to assign to the skill.
     * @since 1.0
     */
    public void setSkill(int skill, int level, long exp)
    {
        skill_level[skill-CS_STAT_SKILLINFO] = level;
        skill_exp[skill-CS_STAT_SKILLINFO] = exp;
    }

    /**
     * Returns the numerical value of the given statistic.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @return The statistic value (or "score").
     * @since 1.0
     */
    public int getStat(int statnr)
    {
        return mystats[statnr];
    }

    /**
     * Sets the given statistic numerical value.
     * @param statnr The stat identifier. See the CS_STAT constants.
     * @param value The value to assign to the chosen statistic.
     * @since 1.0
     */
    public void setStat(int statnr, int value)
    {
        mystats[statnr] = value;
    }

    /**
     * Returns the current Title.
     * @return A String representation of the Title.
     * @since 1.0
     */
    public String getTitle()
    {
        return mytitle;
    }

    /**
     * Returns the current content of the Range stat. This is basically the
     * current active skill for the player.
     * @return A String representation of the Range.
     * @since 1.0
     */
    public String getRange()
    {
        return myrange;
    }

    /**
     * Sets the current Title.
     * @param nt The new Title content.
     * @since 1.0
     */
    public void setTitle(String nt)
    {
        mytitle = nt;
    }

    /**
     * Sets the current value for the Range - this is basically the currently
     * active skill for the player.
     * @param nr The new content of Range.
     * @since 1.0
     */
    public void setRange(String nr)
    {
        myrange = nr;
    }

    /**
     * Returns the amount of global experience.
     * @return Amount of global experience.
     * @since 1.0
     */
    public long getExperience()
    {
        return myexp;
    }

    /**
     * Sets the amount of global experience.
     * @param ne The new amount of global experience.
     */
    public void setExperience(long ne)
    {
        myexp = ne;
    }

    /**
     * The default constructor for a Stats object.
     * @since 1.0
     */
    public Stats()
    {
        for (int i = 0; i < 250; i++)
        {
            mystats[i] = 0;
        }
    }

    /**
     * Convert a stat name into a stat index.
     *
     * @param name The stat name.
     *
     * @return The stat index.
     *
     * @throws IllegalArgumentException if the stat name is undefined
     */
    public static int parseStat(final String name)
    {
        if (!statTable.containsKey(name))
        {
            throw new IllegalArgumentException();
        }

        return statTable.get(name);
    }
}
