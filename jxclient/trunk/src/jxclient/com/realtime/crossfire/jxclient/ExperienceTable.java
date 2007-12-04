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
package com.realtime.crossfire.jxclient;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores experience &lt;-&gt; level mappings.
 *
 * @author Andreas Kirschbaum
 */
public class ExperienceTable
{
    /**
     * Maps level to experience needed to reach the level.
     */
    private final Map<Integer, Long> info = new HashMap<Integer, Long>();

    /**
     * The minimum level value in {@link #info}.
     */
    private int minLevel = 0;

    /**
     * The maximum level value in {@link #info}.
     */
    private int maxLevel = 0;

    /**
     * Forget about all level-$&gt; mappings.
     */
    public void clear()
    {
        info.clear();
        minLevel = Integer.MAX_INT;
        maxLevel = Integer.MIN_INT;
    }

    /**
     * Add a new level-&gt;experience mapping.
     *
     * @param level The level to update.
     *
     * @param exp The experience needed to reach level <code>level</code>.
     */
    public void add(final int level, final long exp)
    {
        if (level < 1)
        {
            return;
        }

        info.put(level, exp);
        if (minLevel > level)
        {
            minLevel = level;
        }
        if (maxLevel < level)
        {
            maxLevel = level;
        }
    }

    /**
     * Return the experience needed for a given level.
     *
     * @param level The level to reach.
     *
     * @return The needed experience.
     */
    public long getExperience(final int level)
    {
        if (minLevel >= maxLevel)
        {
            return 0;
        }

        final Long exp = info.get(level);
        if(exp != null)
        {
            return exp;
        }

        if (level < minLevel)
        {
            return info.get(minLevel);
        }

        if (level > maxLevel)
        {
            return maxLevel;
        }

        for (int i = level; i < maxLevel; i++)
        {
            final Long tmp = info.get(i);
            if (tmp != null)
            {
                return tmp;
            }
        }

        throw new AssertionError();
    }

    /**
     * Return the experience needed to reach the next level.
     *
     * @param currentLevel The current level.
     *
     * @param currentExp The current experience.
     *
     * @return The experience to reach level <code>currentLevel+1</code>.
     */
    public long getExperienceToNextLevel(final int currentLevel, final long currentExp)
    {
        final long expNextLevel = getExperience(currentLevel+1);
        return Math.max(0, expNextLevel-currentExp);
    }
}
