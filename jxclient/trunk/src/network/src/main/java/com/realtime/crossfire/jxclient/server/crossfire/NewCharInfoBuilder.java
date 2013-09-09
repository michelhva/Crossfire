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

import com.realtime.crossfire.jxclient.character.NewCharInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link NewCharInfo} instances while parsing a "replyinfo
 * newcharinfo" response packet.
 * @author Andreas Kirschbaum
 */
public class NewCharInfoBuilder {

    /**
     * The total number of points the character has to spend.
     */
    private int points;

    /**
     * The minimum value for stats.
     */
    private int minValue;

    /**
     * The maximum value for stats.
     */
    private int maxValue;

    /**
     * The stat names to set.
     */
    @NotNull
    private final List<String> statNames = new ArrayList<String>();

    /**
     * Whether a race should be selected.
     */
    private boolean raceChoice;

    /**
     * Whether a class should be selected.
     */
    private boolean classChoice;

    /**
     * Whether a starting map should be selected.
     */
    private boolean startingMapChoice;

    /**
     * Finished parsing and returns the {@link NewCharInfo} instance for the
     * parsed entry.
     * @return the new char info instance
     */
    @NotNull
    public NewCharInfo finish() {
        return new NewCharInfo(points, minValue, maxValue, statNames, raceChoice, classChoice, startingMapChoice);
    }

    /**
     * Sets the number of points the character has to spend.
     * @param points the number of points
     */
    public void setPoints(final int points) {
        this.points = points;
    }

    /**
     * Sets the allowed stat value range.
     * @param minValue the minimum stat value
     * @param maxValue the maximum stat value
     */
    public void setStatRange(final int minValue, final int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Sets the stat names to spend on.
     * @param statNames the stat names
     */
    public void setStatNames(@NotNull final String[] statNames) {
        this.statNames.clear();
        this.statNames.addAll(Arrays.asList(statNames));
    }

    /**
     * Sets that a race should be selected.
     */
    public void setRaceChoice() {
        raceChoice = true;
    }

    /**
     * Sets that a class should be selected.
     */
    public void setClassChoice() {
        classChoice = true;
    }

    /**
     * Sets that a starting map should be selected.
     */
    public void setStartingMapChoice() {
        startingMapChoice = true;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "XXX";
    }

}
