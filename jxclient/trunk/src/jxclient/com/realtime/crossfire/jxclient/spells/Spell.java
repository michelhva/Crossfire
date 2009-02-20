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
package com.realtime.crossfire.jxclient.spells;

import com.realtime.crossfire.jxclient.util.StringSplitter;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Spell
{
    private final String name;

    private int faceNum;

    private int tag;

    private String message;

    private int level;

    private int castingTime;

    private int mana;

    private int grace;

    private int damage;

    private int skill;

    private int path;

    public int getTag()
    {
        return tag;
    }

    public int getLevel()
    {
        return level;
    }

    public int getCastingTime()
    {
        return castingTime;
    }

    public int getMana()
    {
        return mana;
    }

    public int getGrace()
    {
        return grace;
    }

    public int getDamage()
    {
        return damage;
    }

    public int getSkill()
    {
        return skill;
    }

    public int getPath()
    {
        return path;
    }

    public String getName()
    {
        return name;
    }

    public String getMessage()
    {
        return message;
    }

    public int getFaceNum()
    {
        return faceNum;
    }

    public void setLevel(final int level)
    {
        this.level = level;
    }

    public void setCastingTime(final int castingTime)
    {
        this.castingTime = castingTime;
    }

    public void setMana(final int mana)
    {
        this.mana = mana;
    }

    public void setGrace(final int grace)
    {
        this.grace = grace;
    }

    public void setDamage(final int damage)
    {
        this.damage = damage;
    }

    public void setSkill(final int skill)
    {
        this.skill = skill;
    }

    public void setPath(final int path)
    {
        this.path = path;
    }

    public Spell(final String name)
    {
        this.name = name;
    }

    public void setParameters(final int faceNum, final int tag, final String message, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path)
    {
        this.faceNum = faceNum;
        this.tag = tag;
        this.message = message;
        this.level = level;
        this.castingTime = castingTime;
        this.mana = mana;
        this.grace = grace;
        this.damage = damage;
        this.skill = skill;
        this.path = path;
    }

    public void updateParameters(final boolean updateMana, final int mana, final boolean updateGrace, final int grace, final boolean updateDamage, final int damage)
    {
        if (updateMana)
        {
            this.mana = mana;
        }

        if (updateGrace)
        {
            this.grace = grace;
        }

        if (updateDamage)
        {
            this.damage = damage;
        }
    }

    @Override
    public String toString()
    {
        return "Name:"+name
            +" ID:"+tag
            +" Level:"+level
            +" Time:"+castingTime
            +" Mana:"+mana
            +" Grace:"+grace
            +" Damage:"+damage
            +" Skill:"+skill
            +" Path:"+path;
    }

    /**
     * Return a description for this spell to be used in tooltips.
     *
     * @return The tooltip text.
     */
    public String getTooltipText()
    {
        return message.length() <= 0 ? name : name+"<br>"+StringSplitter.splitAsHtml(message);
    }
}
