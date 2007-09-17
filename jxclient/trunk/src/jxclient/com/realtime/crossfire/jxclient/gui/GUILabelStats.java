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
package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.CfPlayer;
import com.realtime.crossfire.jxclient.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.Stats;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A {@link GUILabel} that displays a value of the last received "stats"
 * command.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUILabelStats extends GUILabel
{
    /**
     * The stat to display.
     */
    private final int stat;

    /**
     * The {@link CrossfireStatsListener} registered to be notified about stat
     * changes.
     */
    private final CrossfireStatsListener crossfireStatsListener = new CrossfireStatsListener()
    {
        /** {@inheritDoc} */
        public void commandStatsReceived(final CrossfireCommandStatsEvent evt)
        {
            final Stats s = evt.getStats();
            final String text;
            switch (stat)
            {
            case Stats.CS_STAT_SPEED:
            case Stats.CS_STAT_WEAP_SP:
                final int statValue = s.getStat(stat);
                final int tmp = (statValue*100+Stats.FLOAT_MULTI/2)/Stats.FLOAT_MULTI;
                text = tmp/100+"."+tmp/10%10+tmp%10;
                break;

            case Stats.CS_STAT_RANGE:
                final String rangeString = s.getRange();
                if (rangeString.startsWith("Range: spell "))
                {
                    text = rangeString.substring(13);
                }
                else if (rangeString.startsWith("Range: "))
                {
                    text = rangeString.substring(7);
                }
                else if (rangeString.startsWith("Skill: "))
                {
                    text = rangeString.substring(7);
                }
                else
                {
                    text = rangeString;
                }
                break;

            case Stats.CS_STAT_TITLE:
                text = s.getTitle();
                break;

            case Stats.CS_STAT_EXP64:
            case Stats.CS_STAT_EXP:
                text = String.valueOf(s.getExperience());
                break;

            default:
                text = String.valueOf(s.getStat(stat));
                break;
            }
            setText(text);
        }
    };

    /**
     * Create a new instance.
     *
     * @param jxcWindow The <code>JXCWindow</code> this element belongs to.
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen.
     *
     * @param y The y-coordinate for drawing this element to screen.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param font The font to use.
     *
     * @param color The color to use.
     *
     * @param stat The stat to display.
     *
     * @throws IOException If an I/O error occurs.
     */
    public GUILabelStats(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final Font font, final Color color, final int stat) throws IOException
    {
        super(jxcWindow, name, x, y, w, h, null, font, color, "");
        this.stat = stat;
        CfPlayer.addCrossfireStatsListener(crossfireStatsListener);
    }
}
