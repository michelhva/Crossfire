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

package com.realtime.crossfire.jxclient.server.crossfire;

import com.realtime.crossfire.jxclient.character.StartingMap;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for {@link StartingMap} instances while parsing a "replyinfo
 * startingmap" response packet.
 * @author Andreas Kirschbaum
 */
public class StartingMapBuilder {

    /**
     * The archetype name of the current entry. Set to <code>null</code> until
     * the first entry was started.
     */
    @Nullable
    private byte[] archName;

    /**
     * The proper name of this entry. Set to <code>null</code> if no proper name
     * has been set yet.
     */
    @Nullable
    private String name;

    /**
     * The description of this entry. Set to <code>null</code> if no description
     * has been set yet.
     */
    @Nullable
    private String description;

    /**
     * The {@link StartingMap} entries parsed so far.
     */
    @NotNull
    private final Collection<StartingMap> startingMaps = new ArrayList<StartingMap>();

    /**
     * Starts a new starting map entry.
     * @param archName the archetype name of the entry
     * @noinspection NullableProblems
     */
    public void setArchName(@NotNull final byte[] archName) {
        addStartingMap();
        this.archName = archName.clone();
        name = null;
        description = null;
    }

    /**
     * Sets the name of the current entry.
     * @param name the name
     * @noinspection NullableProblems
     */
    public void setName(@NotNull final String name) {
        if (archName == null) {
            System.err.println("missing archetype name for name '"+name+"' in startingmap block");
            return;
        }
        if (this.name != null) {
            System.err.println("duplicate name '"+name+"' in startingmap block");
            return;
        }
        this.name = name;
    }

    /**
     * Sets the description of the current entry.
     * @param description the description
     * @noinspection NullableProblems
     */
    public void setDescription(@NotNull final String description) {
        if (archName == null) {
            System.err.println("missing archetype name for description '"+description+"' in startingmap block");
            return;
        }
        if (this.description != null) {
            System.err.println("duplicate description '"+description+"' in startingmap block");
            return;
        }
        this.description = description;
    }

    /**
     * Finishes parsing.
     * @return all starting map entries
     */
    @NotNull
    public Collection<StartingMap> finish() {
        addStartingMap();
        return startingMaps;
    }

    /**
     * Adds a new {@link StartingMap} entry to {@link #startingMaps} for the
     * current entry. Resets the current entry.
     */
    private void addStartingMap() {
        if (archName == null) {
            return;
        }

        startingMaps.add(new StartingMap(archName, name == null ? "" : name, description == null ? "" : description));
        archName = null;
        name = null;
        description = null;
    }

}
