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

package com.realtime.crossfire.jxclient.spells;

import com.realtime.crossfire.jxclient.skills.Skill;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import com.realtime.crossfire.jxclient.util.StringSplitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a Crossfire spell.
 * @author Lauwenmark
 */
public class Spell {

    /**
     * The spell name.
     */
    @NotNull
    private final String name;

    /**
     * The {@link SpellListener SpellListeners} to be notified of changes.
     */
    @NotNull
    private final EventListenerList2<SpellListener> listeners = new EventListenerList2<SpellListener>(SpellListener.class);

    /**
     * The face number.
     */
    private int faceNum;

    /**
     * The tag ID.
     */
    private int tag;

    /**
     * The description.
     */
    @NotNull
    private String message = "";

    /**
     * The spell level.
     */
    private int level;

    /**
     * The casting time.
     */
    private int castingTime;

    /**
     * The mana needed to cast the spell.
     */
    private int mana;

    /**
     * The grace needed to cast the spell.
     */
    private int grace;

    /**
     * The damage done by the spell.
     */
    private int damage;

    /**
     * The spell's skill. See {@link Stats#CS_STAT_SKILLINFO}.
     */
    private int skill;

    /**
     * The spell path.
     */
    private int path;

    /**
     * Whether this spell is unknown to the character.
     */
    private boolean unknown = false;

    /**
     * To get the skill's name, see {@link SkillSet}.
     */
    private final SkillSet skillSet;

    /**
     * Attuned, repelled or denied information.
     */
    private final Stats stats;

    /**
     * Creates a new instance.
     * @param name the spell name
     * @param skillSet the skills
     */
    public Spell(@NotNull final String name, @NotNull final SkillSet skillSet, @NotNull final Stats stats) {
        this.name = name;
        this.skillSet = skillSet;
        this.stats = stats;
    }

    /**
     * Returns the tag ID.
     * @return the tag ID
     */
    public int getTag() {
        return tag;
    }

    /**
     * Returns the spell level.
     * @return the spell level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the casting time.
     * @return the casting time
     */
    public int getCastingTime() {
        return castingTime;
    }

    /**
     * Returns the mana needed to cast the spell.
     * @return the mana
     */
    public int getMana() {
        return mana;
    }

    /**
     * Returns the grace needed to cast the spell.
     * @return the grace
     */
    public int getGrace() {
        return grace;
    }

    /**
     * Returns the damage done by the spell.
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the spell's skill. See {@link Stats#CS_STAT_SKILLINFO}.
     * @return the spell's skill
     */
    public int getSkill() {
        return skill;
    }

    /**
     * Returns the spell path.
     * @return the spell path
     */
    public int getPath() {
        return path;
    }

    /**
     * Returns the spell name.
     * @return the spell name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the description.
     * @return the description
     */
    @NotNull
    public String getMessage() {
        return message;
    }

    /**
     * Returns the face number.
     * @return the face number
     */
    public int getFaceNum() {
        return faceNum;
    }

    /**
     * Returns whether this spell is unknown to the character.
     * @return whether this spell is unknown
     */
    public boolean isUnknown() {
        return unknown;
    }

    /**
     * Marks this spell as known or unknown for the character.
     * @param unknown whether this spell is unknown
     */
    public void setUnknown(final boolean unknown) {
        if (this.unknown != unknown) {
            this.unknown = unknown;
            fireChanged();
        }
    }

    /**
     * Updates the spell's parameters.
     * @param faceNum the face number
     * @param tag the tag ID
     * @param message the description
     * @param level the spell level
     * @param castingTime the casting time
     * @param mana the mana needed to cast the spell
     * @param grace the grace needed to cast the spell
     * @param damage the damage done by the spell
     * @param skill the spell's skill
     * @param path the spell path
     */
    public void setParameters(final int faceNum, final int tag, @NotNull final String message, final int level, final int castingTime, final int mana, final int grace, final int damage, final int skill, final int path) {
        boolean changed = false;

        if (this.faceNum != faceNum) {
            this.faceNum = faceNum;
            changed = true;
        }

        if (this.tag != tag) {
            this.tag = tag;
            changed = true;
        }

        if (!this.message.equals(message)) {
            this.message = message;
            changed = true;
        }

        if (this.level != level) {
            this.level = level;
            changed = true;
        }

        if (this.castingTime != castingTime) {
            this.castingTime = castingTime;
            changed = true;
        }

        if (this.mana != mana) {
            this.mana = mana;
            changed = true;
        }

        if (this.grace != grace) {
            this.grace = grace;
            changed = true;
        }

        if (this.damage != damage) {
            this.damage = damage;
            changed = true;
        }

        if (this.skill != skill) {
            this.skill = skill;
            changed = true;
        }

        if (this.path != path) {
            this.path = path;
            changed = true;
        }

        if (unknown) {
            unknown = false;
            changed = true;
        }

        if (changed) {
            fireChanged();
        }
    }

    /**
     * Updates the spell's parameters.
     * @param updateMana whether to update the mana
     * @param mana the mana needed to cast the spell
     * @param updateGrace whether to update the grace
     * @param grace the grace needed to cast the spell
     * @param updateDamage whether to update the damage
     * @param damage the damage done by the spell
     */
    public void updateParameters(final boolean updateMana, final int mana, final boolean updateGrace, final int grace, final boolean updateDamage, final int damage) {
        boolean changed = false;

        if (updateMana && this.mana != mana) {
            this.mana = mana;
            changed = true;
        }

        if (updateGrace && this.grace != grace) {
            this.grace = grace;
            changed = true;
        }

        if (updateDamage && this.damage != damage) {
            this.damage = damage;
            changed = true;
        }

        if (changed) {
            fireChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "Name:"+name+" ID:"+tag+" Level:"+level+" Time:"+castingTime+" Mana:"+mana+" Grace:"+grace+" Damage:"+damage+" Skill:"+skill+" Path:"+path+" Unknown:"+unknown;
    }

    /**
     * Returns a description for this spell to be used in tooltips.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText() {
        final StringBuilder sb = new StringBuilder("<b>");
        sb.append(name.substring(0, 1).toUpperCase()).append(name.substring(1));
        sb.append("</b>");
        if (unknown) {
            sb.append(" (unknown)");
        }
        if ((path&stats.getStat(Stats.CS_STAT_SPELL_DENY)) == 0) {
            final Skill sk = skillSet.getSkill(skill);
            if (sk != null) {
                sb.append("<br>Skill: ").append(sk.toString());
            }
            if (level > 0) {
                sb.append("<br>Level: ");
                if (sk != null && level <= sk.getLevel()) {
                    int effective = sk.getLevel()-level;
                    @Nullable final String supp;
                    if ((path&stats.getStat(Stats.CS_STAT_SPELL_ATTUNE)) != 0) {
                        effective += 2;
                        supp = " (attuned)";
                    } else if ((path&stats.getStat(Stats.CS_STAT_SPELL_REPEL)) != 0) {
                        effective -= 2;
                        supp = " (repelled)";
                    } else {
                        supp = null;
                    }
                    sb.append(effective);
                    if (supp != null) {
                        sb.append(supp);
                    }
                } else {
                    sb.append(level);
                }
            }
            if (mana > 0) {
                sb.append("<br>Mana: ").append(mana);
            }
            if (grace > 0) {
                sb.append("<br>Grace: ").append(grace);
            }
            if (damage > 0) {
                sb.append("<br>Damage: ").append(damage);
            }
        } else {
            sb.append("<br><b>Denied</b>");
        }
        if (message.length() > 0) {
            sb.append("<br>");
            sb.append(StringSplitter.splitAsHtml(message));
        }
        return sb.toString();
    }

    /**
     * Notifies all listeners.
     */
    private void fireChanged() {
        for (final SpellListener listener : listeners.getListeners()) {
            listener.spellChanged();
        }
    }

    /**
     * Adds a {@link SpellListener} to be notified of changes.
     * @param listener the listener to add
     */
    public void addSpellListener(@NotNull final SpellListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a {@link SpellListener} to be notified of changes.
     * @param listener the listener to remove
     */
    public void removeSpellListener(@NotNull final SpellListener listener) {
        listeners.remove(listener);
    }

}
