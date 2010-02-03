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

package com.realtime.crossfire.jxclient.spells;

import com.realtime.crossfire.jxclient.util.StringSplitter;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Spell
{
    @NotNull
    private final String name;

    @NotNull
    private final Collection<SpellListener> listeners = new ArrayList<SpellListener>();

    private int faceNum;

    private int tag;

    @NotNull
    private String message = "";

    private int level;

    private int castingTime;

    private int mana;

    private int grace;

    private int damage;

    private int skill;

    private int path;

    private boolean unknown = false;

    public Spell(@NotNull final String name)
    {
        this.name = name;
    }

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

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getMessage()
    {
        return message;
    }

    public int getFaceNum()
    {
        return faceNum;
    }

    /**
     * Returns whether this spell is unknown to the character.
     * @return whether this spell is unknown
     */
    public boolean isUnknown()
    {
        return unknown;
    }

    /**
     * Marks this spell as known or unknown for the character.
     * @param unknown whether this spell is unkonwn
     */
    public void setUnknown(final boolean unknown)
    {
        if (this.unknown != unknown)
        {
            this.unknown = unknown;
            fireChanged();
        }
    }

    public void setParameters(final int faceNum, final int tag, @NotNull final String message, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path)
    {
        boolean changed = false;

        if (this.faceNum != faceNum)
        {
            this.faceNum = faceNum;
            changed = true;
        }

        if (this.tag != tag)
        {
            this.tag = tag;
            changed = true;
        }

        if (!this.message.equals(message))
        {
            this.message = message;
            changed = true;
        }

        if (this.level != level)
        {
            this.level = level;
            changed = true;
        }

        if (this.castingTime != castingTime)
        {
            this.castingTime = castingTime;
            changed = true;
        }

        if (this.mana != mana)
        {
            this.mana = mana;
            changed = true;
        }

        if (this.grace != grace)
        {
            this.grace = grace;
            changed = true;
        }

        if (this.damage != damage)
        {
            this.damage = damage;
            changed = true;
        }

        if (this.skill != skill)
        {
            this.skill = skill;
            changed = true;
        }

        if (this.path != path)
        {
            this.path = path;
            changed = true;
        }

        if (unknown)
        {
            unknown = false;
            changed = true;
        }

        if (changed)
        {
            fireChanged();
        }
    }

    /**
     * Updates the spell's parameters from another {@link Spell} instance. The
     * name and unknown flag values are not copied.
     * @param spell the spell instance to copy from
     */
    public void setParameters(@NotNull final Spell spell)
    {
        setParameters(spell.faceNum, spell.tag, spell.message, spell.level, spell.castingTime, spell.mana, spell.grace, spell.damage, spell.skill, spell.path);
    }

    public void updateParameters(final boolean updateMana, final int mana, final boolean updateGrace, final int grace, final boolean updateDamage, final int damage)
    {
        boolean changed = false;

        if (updateMana && this.mana != mana)
        {
            this.mana = mana;
            changed = true;
        }

        if (updateGrace && this.grace != grace)
        {
            this.grace = grace;
            changed = true;
        }

        if (updateDamage && this.damage != damage)
        {
            this.damage = damage;
            changed = true;
        }

        if (changed)
        {
            fireChanged();
        }
    }

    @NotNull
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
            +" Path:"+path
            +" Unknown:"+unknown;
    }

    /**
     * Return a description for this spell to be used in tooltips.
     *
     * @return The tooltip text.
     */
    @NotNull
    public String getTooltipText()
    {
        final StringBuilder sb = new StringBuilder(name);
        if (unknown)
        {
            sb.append(" (unknown)");
        }
        if (message.length() > 0)
        {
            sb.append("<br>");
            sb.append(StringSplitter.splitAsHtml(message));
        }
        return sb.toString();
    }

    /**
     * Notifies all listeners.
     */
    private void fireChanged()
    {
        for (final SpellListener listener : listeners)
        {
            listener.spellChanged();
        }
    }

    /**
     * Adds a {@link SpellListener} to be notified of changes.
     * @param listener the listener to add
     */
    public void addSpellListener(@NotNull final SpellListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Removes a {@link SpellListener} to be notified of changes.
     * @param listener the listener to remove
     */
    public void removeSpellListener(@NotNull final SpellListener listener)
    {
        listeners.remove(listener);
    }
}
