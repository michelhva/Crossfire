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
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * General information for creating new characters.
 * @author Andreas Kirschbaum
 */
public class NewCharInfo {

    /**
     * The total number of points the character has to spend.
     */
    private final int points;

    /**
     * The minimum value for stats.
     */
    private final int minValue;

    /**
     * The maximum value for stats.
     */
    private final int maxValue;

    /**
     * The stat names to set.
     */
    @NotNull
    private final List<String> statNames = new ArrayList<String>();

    /**
     * Whether a race should be selected.
     */
    private final boolean raceChoice;

    /**
     * Whether a class should be selected.
     */
    private final boolean classChoice;

    /**
     * Whether a starting map should be selected.
     */
    private final boolean startingMapChoice;

    /**
     * Creates a new instance.
     * @param points the number of points the character has to spend
     * @param minValue the minimum stat value
     * @param maxValue the maximum stat value
     * @param statNames the stat names to spend on
     * @param raceChoice whether a race should be selected
     * @param classChoice whether a class should be selected
     * @param startingMapChoice whether a starting map should be selected
     */
    public NewCharInfo(final int points, final int minValue, final int maxValue, @NotNull final Collection<String> statNames, final boolean raceChoice, final boolean classChoice, final boolean startingMapChoice) {
        this.points = points;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.raceChoice = raceChoice;
        this.classChoice = classChoice;
        this.startingMapChoice = startingMapChoice;
        this.statNames.addAll(statNames);
    }

    /**
     * Returns the number of points the character has to spend.
     * @return the number of points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the minimum stat value.
     * @return the minimum stat value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Returns the maximum stat value.
     * @return the maximum stat value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Returns the stat names to spend on.
     * @return the stat names
     */
    @NotNull
    public List<String> getStatNames() {
        return Collections.unmodifiableList(statNames);
    }

    /**
     * Returns whether a race should be selected.
     * @return whether a race should be selected
     */
    public boolean isRaceChoice() {
        return raceChoice;
    }

    /**
     * Returns whether a class should be selected.
     * @return whether a class should be selected
     */
    public boolean isClassChoice() {
        return classChoice;
    }

    /**
     * Returns whether a starting map should be selected.
     * @return whether a starting map should be selected
     */
    public boolean isStartingMapChoice() {
        return startingMapChoice;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "points="+points+", statRange="+minValue+".."+maxValue+", stats="+statNames+", race="+raceChoice+", class="+classChoice+", startingMap="+startingMapChoice;
    }

}
