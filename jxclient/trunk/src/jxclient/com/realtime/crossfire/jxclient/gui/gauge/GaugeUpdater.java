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

package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.stats.ExperienceTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Updates the displayed values in a {@link GUIGauge}.
 * @author Andreas Kirschbaum
 */
public abstract class GaugeUpdater {

    /**
     * The experience table to query.
     */
    @NotNull
    private final ExperienceTable experienceTable;

    /**
     * Set the gauge to update.
     */
    @Nullable
    private GUIGaugeListener gauge = null;

    /**
     * If true then the gauge should be hidden if all values are 0.
     */
    private final boolean hideIfEmpty;

    /**
     * Creates a new instance.
     * @param experienceTable the experience table to query
     * @param hideIfEmpty if true the gauge will be hidden if all values are 0
     */
    protected GaugeUpdater(@NotNull final ExperienceTable experienceTable, final boolean hideIfEmpty) {
        this.experienceTable = experienceTable;
        this.hideIfEmpty = hideIfEmpty;
    }

    /**
     * Frees allocated resources.
     */
    public abstract void dispose();

    /**
     * Sets the gauge to update.
     * @param gauge the gauge
     */
    @SuppressWarnings("NullableProblems")
    public void setGauge(@NotNull final GUIGaugeListener gauge) {
        this.gauge = gauge;
        // hide the gauge initially, so if it's never updated it doesn't appear
        if (hideIfEmpty) {
            gauge.setHidden(true);
        }
    }

    /**
     * Updates the gauge values.
     * @param curValue the current value
     * @param minValue the minimum value
     * @param maxValue the maximum value
     */
    protected void setValues(final int curValue, final int minValue, final int maxValue) {
        if (gauge != null) {
            final String curValueString = Integer.toString(curValue);
            final String tooltipText;
            if (minValue == 0) {
                tooltipText = curValue+"/"+maxValue;
            } else {
                tooltipText = curValueString;
            }
            assert gauge != null;
            gauge.setValues(curValue, minValue, maxValue, curValueString, tooltipText);
            if (hideIfEmpty) {
                assert gauge != null;
                gauge.setHidden(curValue == 0 && minValue == 0 && maxValue == 0);
            }
        }
    }

    /**
     * Updates the gauge values.
     * @param curValue the current value
     * @param minValue the minimum value
     * @param maxValue the maximum value
     * @param labelText the text to draw on the gauge
     * @param tooltipText the tooltip suffix
     */
    protected void setValues(final int curValue, final int minValue, final int maxValue, @NotNull final String labelText, @NotNull final String tooltipText) {
        if (gauge != null) {
            gauge.setValues(curValue, minValue, maxValue, labelText, tooltipText);
        }
    }

    /**
     * Returns the experience fraction of the current level.
     * @param level the level
     * @param experience the experience
     * @return the fraction in percents
     */
    protected int getPercentsToNextLevel(final int level, final long experience) {
        return experienceTable.getPercentsToNextLevel(level, experience);
    }

    /**
     * Returns the experience needed to reach next level.
     * @param level the level
     * @param experience the experience
     * @return the needed experience
     */
    protected long getExperienceToNextLevel(final int level, final long experience) {
        return experienceTable.getExperienceToNextLevel(level, experience);
    }

}
