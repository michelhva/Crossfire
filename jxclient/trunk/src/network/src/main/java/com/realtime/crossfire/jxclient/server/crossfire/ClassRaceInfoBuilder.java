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

import com.realtime.crossfire.jxclient.character.Choice;
import com.realtime.crossfire.jxclient.character.ClassRaceInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link ClassRaceInfo} instances while parsing an "replyinfo
 * race_info" packet.
 * @author Andreas Kirschbaum
 */
public class ClassRaceInfoBuilder {

    /**
     * The name of the race being parsed.
     */
    @NotNull
    private final String archName;

    /**
     * The human readable race name.
     */
    @NotNull
    private String name = "";

    /**
     * The long description.
     */
    @NotNull
    private String msg = "";

    /**
     * The stat adjustments. Maps stat number to adjustment.
     */
    @NotNull
    private final Map<Integer, Long> stats = new HashMap<Integer, Long>();

    /**
     * The available choices.
     */
    @NotNull
    private final List<Choice> choices = new ArrayList<Choice>();

    /**
     * Creates a new instance.
     * @param archName the archetype name of the race being parsed
     */
    public ClassRaceInfoBuilder(@NotNull final String archName) {
        this.archName = archName;
    }

    /**
     * Finishes parsing an entry an returns the {@link ClassRaceInfo} for the
     * entry.
     * @return the class race info for the parsed entry
     */
    @NotNull
    public ClassRaceInfo finish() {
        return new ClassRaceInfo(archName, name, msg, stats, choices);
    }

    /**
     * Sets the human readable stat name.
     * @param name the stat name
     */
    public void setName(@NotNull final String name) {
        this.name = name;
    }

    /**
     * Sets the long description.
     * @param msg the long description
     */
    public void setMsg(@NotNull final String msg) {
        this.msg = msg;
    }

    /**
     * Sets a stat adjustment.
     * @param statNo the stat number
     * @param adjustment the adjustment
     */
    public void setStatAdjustment(final int statNo, final long adjustment) {
        stats.put(statNo, adjustment);
    }

    /**
     * Adds a choice.
     * @param choice the choice
     */
    public void addChoice(@NotNull final Choice choice) {
        choices.add(choice);
    }

}
