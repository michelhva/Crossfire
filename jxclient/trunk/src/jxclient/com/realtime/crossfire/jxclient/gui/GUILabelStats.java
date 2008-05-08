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

import com.realtime.crossfire.jxclient.stats.CrossfireCommandStatsEvent;
import com.realtime.crossfire.jxclient.stats.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;

/**
 * A {@link GUILabel} that displays a value of the last received "stats"
 * command.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUILabelStats extends GUIOneLineLabel
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
                text = formatFloatStat(s.getFloatStat(stat), 2);
                break;

            case Stats.CS_STAT_WEAP_SP:
                text = formatFloatStat(s.getWeaponSpeed(), 2);
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

            case Stats.C_STAT_EXP_NEXT_LEVEL:
                text = String.valueOf(getJXCWindow().getExperienceTable().getExperienceToNextLevel(s.getStat(Stats.CS_STAT_LEVEL), s.getExperience()));
                break;

            case Stats.CS_STAT_WEIGHT_LIM:
            case Stats.C_STAT_WEIGHT:
                {
                    final int weight = s.getStat(stat);
                    text = formatFloatStat(((weight+50)/100)/10.0, 1);
                    setTooltipText(formatFloatStat(weight/1000.0, 3)+"kg");
                }
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
     * @param backgroundColor The background color.
     *
     * @param stat The stat to display.
     *
     * @param alignment The text alignment.
     */
    public GUILabelStats(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final Font font, final Color color, final Color backgroundColor, final int stat, final Alignment alignment, final Stats stats)
    {
        super(jxcWindow, name, x, y, w, h, null, font, color, backgroundColor, alignment, "");
        this.stat = stat;
        stats.addCrossfireStatsListener(crossfireStatsListener);
    }

    /**
     * Format a float stat value for display.
     *
     * @param value The float stat value.
     *
     * @param digits The number of fraction digits; must be between 1..3
     * inclusive.
     *
     * @return The formatted value.
     */
    private static String formatFloatStat(final double value, final int digits)
    {
        final int tmp;
        switch (digits)
        {
        case 1:
            tmp = (int)(value*10+0.5);
            return tmp/10+"."+tmp%10;

        case 2:
            tmp = (int)(value*100+0.5);
            return tmp/100+"."+tmp/10%10+tmp%10;

        case 3:
            tmp = (int)(value*1000+0.5);
            return tmp/1000+"."+tmp/100%10+tmp/10%10+tmp%10;
        }

        throw new IllegalArgumentException();
    }
}
