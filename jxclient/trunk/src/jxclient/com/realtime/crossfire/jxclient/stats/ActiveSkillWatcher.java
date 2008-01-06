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
package com.realtime.crossfire.jxclient.stats;

/**
 * Helper class to synthesize an "active skill" stat value. The Crossfire
 * server currently does not send this information, therefore range stat
 * messages are tracked.
 *
 * @author Andreas Kirschbaum
 */
public class ActiveSkillWatcher
{
    /**
     * The range name prefix for skills.
     */
    private static final String prefix = "Skill: ";

    /**
     * The stats instance to notify.
     */
    private final Stats stats;

    /**
     * The last known active skill name.
     */
    private String activeSkill = "";

    /**
     * The stats listener to detect the range stat.
     */
    private final CrossfireStatsListener statsListener = new CrossfireStatsListener()
    {
        /** {@inheritDoc} */
        public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
        {
            check();
        }
    };

    /**
     * Create a new instance.
     *
     * @param stats The stats instance to notify/watch.
     */
    public ActiveSkillWatcher(final Stats stats)
    {
        this.stats = stats;
        stats.addCrossfireStatsListener(statsListener);
        setActive("");
    }

    /**
     * Check whether the range attribute has changed.
     */
    private void check()
    {
        final String range = stats.getRange();
        final String activeSkill = range.startsWith(prefix) ? range.substring(prefix.length()) : "";
        setActive(activeSkill);
    }

    /**
     * Set the active skill name.
     *
     * @param activeSkill The active skill name.
     */
    private void setActive(final String activeSkill)
    {
        synchronized (this)
        {
            if (this.activeSkill.equals(activeSkill))
            {
                return;
            }

            this.activeSkill = activeSkill;
            stats.setActiveSkill(activeSkill);
        }
        stats.setStatsProcessed();
    }
}
