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

package com.realtime.crossfire.jxclient.gui.label;

import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import com.realtime.crossfire.jxclient.util.Formatter;
import java.awt.Color;
import java.awt.Font;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUILabel} that displays a value of the last received "stats"
 * command.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class GUILabelStats extends GUIOneLineLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link Stats} instance to use.
     */
    @NotNull
    private final Stats stats;

    /**
     * The stat to display.
     */
    private final int stat;

    /**
     * The {@link StatsListener} registered to be notified about stat changes.
     */
    @NotNull
    private final StatsListener statsListener = new StatsListener() {

        @Override
        public void reset() {
            // ignore
        }

        @Override
        public void statChanged(final int statNo, final int value) {
            switch (stat) {
            case Stats.CS_STAT_SPEED:
                if (stat == statNo) {
                    setText(Formatter.formatFloat(stats.getFloatStat(stat), 2));
                }
                break;

            case Stats.CS_STAT_WEAP_SP:
                if (stat == statNo) {
                    setText(Formatter.formatFloat(stats.getWeaponSpeed(), 2));
                }
                break;

            case Stats.CS_STAT_WEIGHT_LIM:
            case Stats.C_STAT_WEIGHT:
                if (stat == statNo) {
                    //noinspection IntegerDivisionInFloatingPointContext
                    setText(Formatter.formatFloat(((value+50)/100)/10.0, 1));
                    setTooltipText(Formatter.formatFloat(value/1000.0, 3)+"kg");
                }
                break;

            default:
                if (stat == statNo) {
                    setText(String.valueOf(value));
                }
                break;
            }
        }

        @Override
        public void simpleWeaponSpeedChanged(final boolean simpleWeaponSpeed) {
            // ignore
        }

        @Override
        public void titleChanged(@NotNull final String title) {
            if (stat == Stats.CS_STAT_TITLE) {
                setText(title);
            }
        }

        @Override
        public void rangeChanged(@NotNull final String range) {
            if (stat != Stats.CS_STAT_RANGE) {
                return;
            }

            final String text;
            if (range.startsWith("Range: spell ")) {
                text = range.substring(13);
            } else if (range.startsWith("Range: ")) {
                text = range.substring(7);
            } else if (range.startsWith("Skill: ")) {
                text = range.substring(7);
            } else {
                text = range;
            }
            setText(text);
        }

        @Override
        public void activeSkillChanged(@NotNull final String activeSkill) {
            // ignore
        }

        @Override
        public void experienceChanged(final long exp) {
            if (stat == Stats.CS_STAT_EXP || stat == Stats.CS_STAT_EXP64) {
                setText(String.valueOf(exp));
            }
        }

        @Override
        public void experienceNextLevelChanged(final long expNextLevel) {
            if (stat == Stats.C_STAT_EXP_NEXT_LEVEL) {
                setText(String.valueOf(expNextLevel));
            }
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param font the font to use
     * @param color the color to use
     * @param backgroundColor the background color
     * @param stat the stat to display
     * @param alignment the text alignment
     * @param stats the stats instance to use
     */
    public GUILabelStats(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Font font, @NotNull final Color color, @Nullable final Color backgroundColor, final int stat, @NotNull final Alignment alignment, @NotNull final Stats stats) {
        super(tooltipManager, elementListener, name, null, font, color, backgroundColor, alignment, "");
        this.stats = stats;
        this.stat = stat;
        this.stats.addCrossfireStatsListener(statsListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        stats.removeCrossfireStatsListener(statsListener);
    }

}
