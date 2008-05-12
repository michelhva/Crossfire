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

import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawextinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireCommandDrawinfoEvent;
import com.realtime.crossfire.jxclient.server.CrossfireDrawextinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireDrawinfoListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

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
    private final StatsListener statsListener = new StatsListener()
    {
        /** {@inheritDoc} */
        public void statChanged(final StatsEvent evt)
        {
            checkRange();
        }
    };

    /**
     * The drawinfo listener to receive drawinfo messages.
     */
    private final CrossfireDrawinfoListener drawinfoListener = new CrossfireDrawinfoListener()
    {
        /** {@inheritDoc} */
        public void commandDrawinfoReceived(final CrossfireCommandDrawinfoEvent evt)
        {
            checkMessage(evt.getText());
        }
    };

    /**
     * The drawextinfo listener to receive drawextinfo messages.
     */
    private final CrossfireDrawextinfoListener drawextinfoListener = new CrossfireDrawextinfoListener()
    {
        /** {@inheritDoc} */
        public void commandDrawextinfoReceived(final CrossfireCommandDrawextinfoEvent evt)
        {
            checkMessage(evt.getMessage());
        }
    };

    /**
     * Create a new instance.
     *
     * @param stats The stats instance to notify/watch.
     *
     * @param crossfireServerConnection The connection to watch.
     */
    public ActiveSkillWatcher(final Stats stats, final CrossfireServerConnection crossfireServerConnection)
    {
        this.stats = stats;
        stats.addCrossfireStatsListener(statsListener);
        crossfireServerConnection.addCrossfireDrawinfoListener(drawinfoListener);
        crossfireServerConnection.addCrossfireDrawextinfoListener(drawextinfoListener);
        setActive("");
    }

    /**
     * Check whether the range attribute has changed.
     */
    private void checkRange()
    {
        final String range = stats.getRange();
        if (range.startsWith("Skill: "))
        {
            setActive(range.substring(7));
        }
    }

    /**
     * Check whether a drawinfo message is skill related.
     * @param message the message
     */
    private void checkMessage(final String message)
    {
        if (message.startsWith("Readied skill: "))
        {
            final String tmp = message.substring(15);
            setActive(tmp.endsWith(".") ? tmp.substring(0, tmp.length()-1) : tmp);
        }
    }

    /**
     * Set the active skill name.
     *
     * @param activeSkill The active skill name.
     */
    private void setActive(final String activeSkill)
    {
        // Normalize skill name: the Crossfire server sometimes sends "Skill:
        // <skill item name>" rather than "Skill: <skill name>".
        final String normalizedActiveSkill;
        if (activeSkill.equals("lockpicks"))
        {
            normalizedActiveSkill = "lockpicking";
        }
        else if (activeSkill.equals("writing pen"))
        {
            normalizedActiveSkill = "inscription";
        }
        else
        {
            normalizedActiveSkill = activeSkill;
        }

        synchronized (this)
        {
            if (this.activeSkill.equals(normalizedActiveSkill))
            {
                return;
            }

            this.activeSkill = normalizedActiveSkill;
            stats.setActiveSkill(this.activeSkill);
        }
        stats.setStatsProcessed(false);
    }
}
