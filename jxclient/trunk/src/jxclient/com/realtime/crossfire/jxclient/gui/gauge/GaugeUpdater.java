//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.gui.gauge;

import com.realtime.crossfire.jxclient.experience.ExperienceTable;

/**
 * Updates the displayed values in a {@link GUIGauge}.
 *
 * @author Andreas Kirschbaum
 */
public abstract class GaugeUpdater
{
    /**
     * The experience table to query.
     */
    private final ExperienceTable experienceTable;

    /**
     * Set the gauge to update.
     */
    private GUIGaugeListener gauge = null;

    /**
     * Create a new instance.
     *
     * @param experienceTable The experience table to query.
     */
    protected GaugeUpdater(final ExperienceTable experienceTable)
    {
        this.experienceTable = experienceTable;
    }

    /**
     * Set the gauge to update.
     *
     * @param gauge The gauge.
     */
    public void setGauge(final GUIGaugeListener gauge)
    {
        this.gauge = gauge;
    }

    /**
     * Update the gauge values.
     *
     * @param curValue The current value.
     *
     * @param minValue The minimum value.
     *
     * @param maxValue The maximum value.
     */
    protected void setValues(final int curValue, final int minValue, final int maxValue)
    {
        if (gauge != null)
        {
            final String tooltipText;
            if (minValue == 0)
            {
                tooltipText = curValue+"/"+maxValue;
            }
            else
            {
                tooltipText = Integer.toString(curValue);
            }
            gauge.setValues(curValue, minValue, maxValue, Integer.toString(curValue), tooltipText);
        }
    }

    /**
     * Update the gauge values.
     *
     * @param curValue The current value.
     *
     * @param minValue The minimum value.
     *
     * @param maxValue The maximum value.
     *
     * @param labelText The text to draw on the gauge.
     *
     * @param tooltipText The tooltip suffix.
     */
    protected void setValues(final int curValue, final int minValue, final int maxValue, final String labelText, final String tooltipText)
    {
        if (gauge != null)
        {
            gauge.setValues(curValue, minValue, maxValue, labelText, tooltipText);
        }
    }

    /**
     * Return the experience fraction of the current level.
     *
     * @param level The level.
     *
     * @param experience The experience.
     *
     * @return The fraction in percents.
     */
    protected int getPercentsToNextLevel(final int level, final long experience)
    {
        return experienceTable.getPercentsToNextLevel(level, experience);
    }

    /**
     * Return the experience needed to reach nextlevel.
     *
     * @param level The level.
     *
     * @param experience The experience.
     *
     * @return The needed experience.
     */
    protected long getExperienceToNextLevel(final int level, final long experience)
    {
        return experienceTable.getExperienceToNextLevel(level, experience);
    }

    /**
     * Return the experience for the given level.
     *
     * @param level The level.
     *
     * @return The experience.
     */
    protected long getExperience(final int level)
    {
        return experienceTable.getExperience(level);
    }
}
