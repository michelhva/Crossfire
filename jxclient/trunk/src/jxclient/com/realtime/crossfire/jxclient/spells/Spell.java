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

import com.realtime.crossfire.jxclient.faces.Face;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Spell
{
    private final ImageIcon imageIcon;

    private final String internalName;

    private final int tag;

    private int level;

    private int castingTime;

    private int mana;

    private int grace;

    private int damage;

    private int skill;

    private int path;

    private final Face face;

    private final String name;

    private final String message;

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

    public Face getFace()
    {
        return face;
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

    public void setPath(final int nv)
    {
        this.path = path;
    }

    public Spell(final Face face, final int tag, final String name, final String message)
    {
        imageIcon = null;
        this.face = face;
        this.tag = tag;
        this.name = name;
        this.message = message;
        internalName = Integer.toString(tag);
    }

    public Spell(final String filename, final String name, final String internalName)
    {
        final URL url = getClass().getClassLoader().getResource(filename);
        if (url == null)
        {
            throw new IllegalArgumentException("resource '"+filename+"' does not exist");
        }
        imageIcon = new ImageIcon(url);
        face = null;
        tag = 0;
        this.name = name;
        message = "";
        this.internalName = internalName;
    }

    public String getInternalName()
    {
        return internalName;
    }

    public ImageIcon getImageIcon()
    {
        return imageIcon != null ? imageIcon : face.getOriginalImageIcon();
    }

    public String toString()
    {
        String str = "Name:"+name+" ID:"+tag+" Level:"+level;
        str = str+" Time:"+castingTime+" Mana:"+mana+" Grace:"+grace;
        str = str+" Damage:"+damage+" Skill:"+skill+" Path:"+path;
        return str;
    }

    /**
     * Return a description for this spell to be used in tooltips.
     *
     * @return The tooltip text.
     */
    public String getTooltipText()
    {
        return message.length() > 0 ? name+"\n"+message : name;
    }
}
