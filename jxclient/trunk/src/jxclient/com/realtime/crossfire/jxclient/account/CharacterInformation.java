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
 * Copyright (C) 2010 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Information for one character for an account.
 * @author Nicolas Weeger
 */
public class CharacterInformation implements Comparable<CharacterInformation> {

    /**
     * The character's name.
     */
    @NotNull
    private final String name;

    /**
     * The character's race.
     */
    @NotNull
    private final String race;

    /**
     * The character's level.
     */
    private int level;

    /**
     * Creates a new instance.
     * @param name the character's name
     * @param race the character's race
     */
    public CharacterInformation(@NotNull final String name, @NotNull final String race) {
        this.name = name;
        this.race = race;
    }

    /**
     * Returns the character's name.
     * @return the name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the character's race.
     * @return the race
     */
    @NotNull
    public String getRace() {
        return race;
    }

    /**
     * Returns the character's level.
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the character's level.
     * @param level the level to set
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NotNull final CharacterInformation o) {
        return name.compareTo(o.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null || !(obj instanceof CharacterInformation)) {
            return false;
        }

        final CharacterInformation characterInformation = (CharacterInformation)obj;
        return characterInformation.getName().equals(name);
    }

}
