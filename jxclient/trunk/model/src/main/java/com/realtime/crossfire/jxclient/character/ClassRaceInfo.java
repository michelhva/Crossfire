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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * One possible class or race for character creation.
 * @author Andreas Kirschbaum
 */
public class ClassRaceInfo {

    /**
     * The archetype name.
     */
    @NotNull
    private final String archName;

    /**
     * The human readable race name.
     */
    @NotNull
    private final String name;

    /**
     * The long description.
     */
    @NotNull
    private final String msg;

    /**
     * The available choices.
     */
    @NotNull
    private final List<Choice> choices = new ArrayList<Choice>();

    /**
     * The stat adjustments. Maps stat number to adjustment.
     */
    @NotNull
    private final Map<Integer, Long> stats = new HashMap<Integer, Long>();

    /**
     * Creates a new instance.
     * @param archName the archetype name
     * @param name the human readable race name
     * @param msg the long description
     * @param stats the stat adjustments
     * @param choices the available choices
     */
    public ClassRaceInfo(@NotNull final String archName, @NotNull final String name, @NotNull final String msg, @NotNull final Map<Integer, Long> stats, @NotNull final Collection<Choice> choices) {
        this.archName = archName;
        this.name = name;
        this.msg = msg;
        this.stats.putAll(stats);
        this.choices.addAll(choices);
    }

    /**
     * Returns the archetype name.
     * @return the archetype name
     */
    @NotNull
    public String getArchName() {
        return archName;
    }

    /**
     * Returns the human readable race name.
     * @return the human readable race name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the long description.
     * @return the long description
     */
    @NotNull
    public String getMsg() {
        return msg;
    }

    /**
     * Returns a stat adjustment.
     * @param statNo the stat number
     * @return the adjustment
     */
    public long getStatAdjustment(final int statNo) {
        try {
            return stats.get(statNo);
        } catch (final NullPointerException ignored) {
            return 0;
        }
    }

    /**
     * Returns the available choices.
     * @return the available choices
     */
    @NotNull
    public List<Choice> getChoices() {
        return Collections.unmodifiableList(choices);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "arch="+archName+", name="+name+", stats="+stats+", choices="+choices;
    }

}
