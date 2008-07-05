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

import com.realtime.crossfire.jxclient.experience.ExperienceTable;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsEvent;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import com.realtime.crossfire.jxclient.util.Formatter;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;

/**
 * A {@link GUILabel} that displays a value of the last received "stats"
 * command.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUILabelStats extends GUIOneLineLabel
{
    /**
     * The experience table to use.
     */
    private final ExperienceTable experienceTable;

    /**
     * The stat to display.
     */
    private final int stat;

    /**
     * The {@link StatsListener} registered to be notified about stat changes.
     */
    private final StatsListener statsListener = new StatsListener()
    {
        /** {@inheritDoc} */
        public void statChanged(final StatsEvent evt)
        {
            final Stats s = evt.getStats();
            final String text;
            switch (stat)
            {
            case CrossfireStatsListener.CS_STAT_SPEED:
                text = Formatter.formatFloat(s.getFloatStat(stat), 2);
                break;

            case CrossfireStatsListener.CS_STAT_WEAP_SP:
                text = Formatter.formatFloat(s.getWeaponSpeed(), 2);
                break;

            case CrossfireStatsListener.CS_STAT_RANGE:
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

            case CrossfireStatsListener.CS_STAT_TITLE:
                text = s.getTitle();
                break;

            case CrossfireStatsListener.CS_STAT_EXP64:
            case CrossfireStatsListener.CS_STAT_EXP:
                text = String.valueOf(s.getExperience());
                break;

            case CrossfireStatsListener.C_STAT_EXP_NEXT_LEVEL:
                text = String.valueOf(experienceTable.getExperienceToNextLevel(s.getStat(CrossfireStatsListener.CS_STAT_LEVEL), s.getExperience()));
                break;

            case CrossfireStatsListener.CS_STAT_WEIGHT_LIM:
            case CrossfireStatsListener.C_STAT_WEIGHT:
                {
                    final int weight = s.getStat(stat);
                    text = Formatter.formatFloat(((weight+50)/100)/10.0, 1);
                    setTooltipText(Formatter.formatFloat(weight/1000.0, 3)+"kg");
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
     * Creates a new instance.
     * @param window the <code>JXCWindow</code> this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen
     * @param y the y-coordinate for drawing this element to screen
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param font the font to use
     * @param color the color to use
     * @param backgroundColor the background color
     * @param stat the stat to display
     * @param alignment the text alignment
     * @param stats the stats instance to use
     * @param experienceTable the experience table to use
     */
    public GUILabelStats(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final Font font, final Color color, final Color backgroundColor, final int stat, final Alignment alignment, final Stats stats, final ExperienceTable experienceTable)
    {
        super(window, name, x, y, w, h, null, font, color, backgroundColor, alignment, "");
        this.experienceTable = experienceTable;
        this.stat = stat;
        stats.addCrossfireStatsListener(statsListener);
    }
}
