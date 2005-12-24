package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;

public class Stats
{
    public static final int CS_STAT_HP     = 1;
    public static final int CS_STAT_MAXHP  = 2;
    public static final int CS_STAT_SP     = 3;
    public static final int CS_STAT_MAXSP  = 4;
    public static final int CS_STAT_STR    = 5;
    public static final int CS_STAT_INT    = 6;
    public static final int CS_STAT_WIS    = 7;
    public static final int CS_STAT_DEX    = 8;
    public static final int CS_STAT_CON    = 9;
    public static final int CS_STAT_CHA    =10;
    public static final int CS_STAT_EXP    =11;
    public static final int CS_STAT_LEVEL  =12;
    public static final int CS_STAT_WC     =13;
    public static final int CS_STAT_AC     =14;
    public static final int CS_STAT_DAM    =15;
    public static final int CS_STAT_ARMOUR =16;
    public static final int CS_STAT_SPEED  =17;
    public static final int CS_STAT_FOOD   =18;
    public static final int CS_STAT_WEAP_SP=19;
    public static final int CS_STAT_RANGE  =20;
    public static final int CS_STAT_TITLE  =21;
    public static final int CS_STAT_POW    =22;
    public static final int CS_STAT_GRACE  =23;
    public static final int CS_STAT_MAXGRACE      = 24;
    public static final int CS_STAT_FLAGS = 25;
    public static final int CS_STAT_WEIGHT_LIM    = 26;
    public static final int CS_STAT_EXP64 = 28;

    /* Start & end of resistances, inclusive. */
    public static final int CS_STAT_RESIST_START  = 100;
    public static final int CS_STAT_RESIST_END    = 117;
    public static final int CS_STAT_RES_PHYS      = 100;
    public static final int CS_STAT_RES_MAG       = 101;
    public static final int CS_STAT_RES_FIRE      = 102;
    public static final int CS_STAT_RES_ELEC      = 103;
    public static final int CS_STAT_RES_COLD      = 104;
    public static final int CS_STAT_RES_CONF      = 105;
    public static final int CS_STAT_RES_ACID      = 106;
    public static final int CS_STAT_RES_DRAIN     = 107;
    public static final int CS_STAT_RES_GHOSTHIT  = 108;
    public static final int CS_STAT_RES_POISON    = 109;
    public static final int CS_STAT_RES_SLOW      = 110;
    public static final int CS_STAT_RES_PARA      = 111;
    public static final int CS_STAT_TURN_UNDEAD   = 112;
    public static final int CS_STAT_RES_FEAR      = 113;
    public static final int CS_STAT_RES_DEPLETE   = 114;
    public static final int CS_STAT_RES_DEATH     = 115;
    public static final int CS_STAT_RES_HOLYWORD  = 116;
    public static final int CS_STAT_RES_BLIND     = 117;

    /* Start & end of skill experience + skill level, inclusive. */
    public static final int CS_STAT_SKILLEXP_START    = 118;
    public static final int CS_STAT_SKILLEXP_END      = 129;
    public static final int CS_STAT_SKILLEXP_AGILITY  = 118;
    public static final int CS_STAT_SKILLEXP_AGLEVEL  = 119;
    public static final int CS_STAT_SKILLEXP_PERSONAL = 120;
    public static final int CS_STAT_SKILLEXP_PELEVEL  = 121;
    public static final int CS_STAT_SKILLEXP_MENTAL   = 122;
    public static final int CS_STAT_SKILLEXP_MELEVEL  = 123;
    public static final int CS_STAT_SKILLEXP_PHYSIQUE = 124;
    public static final int CS_STAT_SKILLEXP_PHLEVEL  = 125;
    public static final int CS_STAT_SKILLEXP_MAGIC    = 126;
    public static final int CS_STAT_SKILLEXP_MALEVEL  = 127;
    public static final int CS_STAT_SKILLEXP_WISDOM   = 128;
    public static final int CS_STAT_SKILLEXP_WILEVEL  = 129;

/* CS_STAT_SKILLINFO is used as the starting index point.  Skill number->name
    * map is generated dynamically for the client, so a bunch of entries will
    * be used here.  CS_NUM_SKILLS does not match how many skills there really
    * are - instead, it is used as a range of values so that the client
    * can have some idea how many skill categories there may be.
 */
    public static final int CS_STAT_SKILLINFO          = 141;
    public static final int CS_NUM_SKILLS              = 50;

    protected static Skill[] myskills = new Skill[CS_NUM_SKILLS];

    public static final int RESIST_TYPES               = 18;
    private int[] mystats = new int[512];
    private long myexp = 0;
    private String myrange = new String("");
    private String mytitle = new String("");

    private static final int[] skill_level = new int[CS_NUM_SKILLS];
    private static final long[] skill_exp = new long[CS_NUM_SKILLS];

    static
    {
        for(int i=0;i<CS_NUM_SKILLS;i++)
        {
            myskills[i]=null;
        }
    }
    public static void addSkill(int id, String n)
    {
        Skill sk = new Skill(id, n);
        myskills[id]=sk;
    }
    public static Skill getSkill(int id)
    {
        return myskills[id];
    }
    public int getSkillLevel(int skill)
    {
        return skill_level[skill-CS_STAT_SKILLINFO];
    }
    public long getSkillExperience(int skill)
    {
        return skill_exp[skill-CS_STAT_SKILLINFO];
    }
    public void setSkill(int skill, int level, long exp)
    {
        skill_level[skill-CS_STAT_SKILLINFO]=level;
        skill_exp[skill-CS_STAT_SKILLINFO]=exp;
    }
    public int getStat(int statnr)
    {
        return mystats[statnr];
    }
    public void setStat(int statnr, int value)
    {
        mystats[statnr] = value;
    }
    public String getTitle()
    {
        return mytitle;
    }
    public String getRange()
    {
        return myrange;
    }
    public void setTitle(String nt)
    {
        mytitle = nt;
    }
    public void setRange(String nr)
    {
        myrange = nr;
    }
    public long getExperience()
    {
        return myexp;
    }
    public void setExperience(long ne)
    {
        myexp = ne;
    }
    public Stats()
    {
        for(int i=0;i<250;i++)
        {
            mystats[i]=0;
        }
    }
}
