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

package com.realtime.crossfire.jxclient.character;

import org.jetbrains.annotations.NotNull;

/**
 * One possible starting map for character creation.
 * @author Andreas Kirschbaum
 */
public class StartingMap {

    /**
     * The archetype name.
     */
    @NotNull
    private final byte[] archName;

    /**
     * The proper name.
     */
    @NotNull
    private final String name;

    /**
     * The description.
     */
    @NotNull
    private final String description;

    /**
     * Creates a new instance.
     * @param archName the archetype name
     * @param name the proper name
     * @param description the description
     */
    public StartingMap(@NotNull final byte[] archName, @NotNull final String name, @NotNull final String description) {
        this.archName = archName.clone();
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the archetype name.
     * @return the archetype name
     */
    @NotNull
    public byte[] getArchName() {
        return archName.clone();
    }

    /**
     * Returns the proper name.
     * @return the proper name
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
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return name;
    }

}
