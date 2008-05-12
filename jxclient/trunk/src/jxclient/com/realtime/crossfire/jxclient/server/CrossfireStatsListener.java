package com.realtime.crossfire.jxclient.server;

/**
 * Interface for listeners interested in "stats" commands.
 * @author Andreas Kirschbaum
 */
public interface CrossfireStatsListener
{
    /**
     * The Hit Points stat.
     */
    int CS_STAT_HP = 1;

    /**
     * The Maximum Hit Points stat.
     */
    int CS_STAT_MAXHP = 2;

    /**
     * The Spell Points stat.
     */
    int CS_STAT_SP = 3;

    /**
     * The Maximum Spell Points stat.
     */
    int CS_STAT_MAXSP = 4;

    /**
     * The Strength Primary stat.
     */
    int CS_STAT_STR = 5;

    /**
     * The Intelligence Primary stat.
     */
    int CS_STAT_INT = 6;

    /**
     * The Wisdom Primary stat.
     */
    int CS_STAT_WIS = 7;

    /**
     * The Dexterity Primary stat.
     */
    int CS_STAT_DEX = 8;

    /**
     * The Constitution Primary stat.
     */
    int CS_STAT_CON = 9;

    /**
     * The Charisma Primary stat.
     */
    int CS_STAT_CHA = 10;

    /**
     * The Global Experience (32bit encoding) stat.
     */
    int CS_STAT_EXP = 11;

    /**
     * The Global Experience needed to reach next level stat; the value is in
     * XP.
     */
    int C_STAT_EXP_NEXT_LEVEL = 0x10000;

    /**
     * The Global Level stat.
     */
    int CS_STAT_LEVEL = 12;

    /**
     * The Weapon Class stat.
     */
    int CS_STAT_WC = 13;

    /**
     * The Armor Class stat.
     */
    int CS_STAT_AC = 14;

    /**
     * The Damage stat.
     */
    int CS_STAT_DAM = 15;

    /**
     * The Armour stat.
     */
    int CS_STAT_ARMOUR = 16;

    /**
     * The Speed stat.
     */
    int CS_STAT_SPEED = 17;

    /**
     * The Food stat.
     */
    int CS_STAT_FOOD = 18;

    /**
     * The Low Food indicator. It ranges from 0 (ok) to 1 (low food).
     */
    int C_STAT_LOWFOOD = 0x10003;

    /**
     * The Weapon Speed stat.
     */
    int CS_STAT_WEAP_SP = 19;

    /**
     * The Range stat - this is what is currently readied by the player to fire.
     */
    int CS_STAT_RANGE = 20;

    /**
     * The Title stat.
     */
    int CS_STAT_TITLE = 21;

    /**
     * The Power Primary stat.
     */
    int CS_STAT_POW = 22;

    /**
     * The Grace stat.
     */
    int CS_STAT_GRACE = 23;

    /**
     * The Maximum Grace stat.
     */
    int CS_STAT_MAXGRACE = 24;

    /**
     * The various flags used in stats.
     */
    int CS_STAT_FLAGS = 25;

    /**
     * The Weight Limit stat.
     */
    int CS_STAT_WEIGHT_LIM = 26;

    /**
     * The character's weight.
     */
    int C_STAT_WEIGHT = 257;

    /**
     * The Global Experience (64bit encoding) stat.
     */
    int CS_STAT_EXP64 = 28;

    int CS_STAT_SPELL_ATTUNE = 29;

    int CS_STAT_SPELL_REPEL = 30;

    int CS_STAT_SPELL_DENY = 31;

    /**
     * Beginning index of the resistances.
     */
    int CS_STAT_RESIST_START = 100;

    /**
     * End index of the resistances.
     */
    int CS_STAT_RESIST_END = 117;

    /**
     * Resistance to physical attacks.
     */
    int CS_STAT_RES_PHYS = 100;

    /**
     * Resistance to magical attacks.
     */
    int CS_STAT_RES_MAG = 101;

    /**
     * Resistance to fire.
     */
    int CS_STAT_RES_FIRE = 102;

    /**
     * Resistance to electricity.
     */
    int CS_STAT_RES_ELEC = 103;

    /**
     * Resistance to cold.
     */
    int CS_STAT_RES_COLD = 104;

    /**
     * Resistance to confusion.
     */
    int CS_STAT_RES_CONF = 105;

    /**
     * Resistance to acid.
     */
    int CS_STAT_RES_ACID = 106;

    /**
     * Resistance to drain life.
     */
    int CS_STAT_RES_DRAIN = 107;

    /**
     * Resistance to ghost hit.
     */
    int CS_STAT_RES_GHOSTHIT = 108;

    /**
     * Resistance to poison.
     */
    int CS_STAT_RES_POISON = 109;

    /**
     * Resistance to slowness.
     */
    int CS_STAT_RES_SLOW = 110;

    /**
     * Resistance to paralysis.
     */
    int CS_STAT_RES_PARA = 111;

    /**
     * Resistance to turn undead.
     */
    int CS_STAT_TURN_UNDEAD = 112;

    /**
     * Resistance to fear.
     */
    int CS_STAT_RES_FEAR = 113;

    /**
     * Resistance to depletion.
     */
    int CS_STAT_RES_DEPLETE = 114;

    /**
     * Resistance to death.
     */
    int CS_STAT_RES_DEATH = 115;

    /**
     * Resistance to holy word.
     */
    int CS_STAT_RES_HOLYWORD = 116;

    /**
     * Resistance to blindness.
     */
    int CS_STAT_RES_BLIND = 117;

    /**
     * Beginning index of skill experience stats.
     */
    int CS_STAT_SKILLEXP_START = 118;

    /**
     * End index of skill experience stats.
     */
    int CS_STAT_SKILLEXP_END = 129;

    /**
     * Agility skills experience.
     */
    int CS_STAT_SKILLEXP_AGILITY = 118;

    /**
     * Agility skills level.
     */
    int CS_STAT_SKILLEXP_AGLEVEL = 119;

    /**
     * Personal skills experience.
     */
    int CS_STAT_SKILLEXP_PERSONAL = 120;

    /**
     * Personal skills level.
     */
    int CS_STAT_SKILLEXP_PELEVEL = 121;

    /**
     * Mental skills experience.
     */
    int CS_STAT_SKILLEXP_MENTAL = 122;

    /**
     * Mental skills level.
     */
    int CS_STAT_SKILLEXP_MELEVEL = 123;

    /**
     * Physical skills experience.
     */
    int CS_STAT_SKILLEXP_PHYSIQUE = 124;

    /**
     * Physical skills level.
     */
    int CS_STAT_SKILLEXP_PHLEVEL = 125;

    /**
     * Magical skills experience.
     */
    int CS_STAT_SKILLEXP_MAGIC = 126;

    /**
     * Magical skills level.
     */
    int CS_STAT_SKILLEXP_MALEVEL = 127;

    /**
     * Wisdom skills experience.
     */
    int CS_STAT_SKILLEXP_WISDOM = 128;

    /**
     * Wisdom skills level.
     */
    int CS_STAT_SKILLEXP_WILEVEL = 129;

    /**
     * Factor used to convert float int int values.
     */
    int FLOAT_MULTI = 100000;

    /**
     * The total number of resistances.
     */
    int RESIST_TYPES = 18;

    /**
     * The "is poisoned" indicator. It ranges from 0 (not poisoned) to 1
     * (poisoned).
     */
    int C_STAT_POISONED = 256;

    /**
     * CS_NUM_SKILLS does not match how many skills there really
     * are - instead, it is used as a range of values so that the client
     * can have some idea how many skill categories there may be.
     */
    int CS_NUM_SKILLS = 50;

    /**
     * CS_STAT_SKILLINFO is used as the starting index point.  Skill number->name
     * map is generated dynamically for the client, so a bunch of entries will
     * be used here.
     */
    int CS_STAT_SKILLINFO = 140;

    /**
     * Sets whether the {@link CrossfireStatsListener#CS_STAT_WEAP_SP} value contains the weapon
     * speed directly.
     * @param simpleWeaponSpeed whether <code>CS_STAT_WEAP_SP</code> is the
     * weapon speed value
     */
    void setSimpleWeaponSpeed(boolean simpleWeaponSpeed);

    /**
     * Parsing of a "stats" command has started.
     */
    void statBegin();

    /**
     * Parsing of a "stats" command has finished.
     */
    void statEnd();

    /**
     * A "stats" command with a two-byte argument has been received.
     * @param stat the stat
     * @param param the parameter
     */
    void statInt2Received(int stat, short param);

    /**
     * A "stats" command with a four-byte argument has been received.
     * @param stat the stat
     * @param param the parameter
     */
    void statInt4Received(int stat, int param);

    /**
     * A "stats" command with an eight-byte argument has been received.
     * @param stat the stat
     * @param param the parameter
     */
    void statInt8Received(int stat, long param);

    /**
     * A "stats" command with a string argument has been received.
     * @param stat the stat
     * @param param the parameter
     */
    void statStringReceived(int stat, String param);

    /**
     * A "stats" command with a skill argument has been received.
     * @param stat the stat
     * @param level the level parameter
     * @param experience the experience parameter
     */
    void statSkillReceived(int stat, int level, long experience);
}
