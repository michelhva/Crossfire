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
import com.realtime.crossfire.jxclient.gui.gui.TooltipText;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
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
public class GUILabelStats2 extends GUIOneLineLabel {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The color for upgradable stats.
     */
    @NotNull
    private final Color colorUpgradable;

    /**
     * The color for depleted stats.
     */
    @NotNull
    private final Color colorDepleted;

    /**
     * The color for boosted stats.
     */
    @NotNull
    private final Color colorBoosted;

    /**
     * The color for boosted and upgradable stats.
     */
    @NotNull
    private final Color colorBoostedUpgradable;

    /**
     * The current stat.
     */
    private final int statCurrent;

    /**
     * The base stat without applied boosts or depletions.
     */
    private final int statBase;

    /**
     * The race's maximum stat.
     */
    private final int statRace;

    /**
     * The stat change due to gear or skills.
     */
    private final int statApplied;

    /**
     * The {@link Stats} instance to use.
     */
    @NotNull
    private final Stats stats;

    /**
     * The current color.
     */
    @NotNull
    private Color color;

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
            if (statNo == statCurrent || statNo == statBase || statNo == statRace || statNo == statApplied) {
                updateStat();
                tooltipChanged();
            }
        }

        @Override
        public void simpleWeaponSpeedChanged(final boolean simpleWeaponSpeed) {
            // ignore
        }

        @Override
        public void titleChanged(@NotNull final String title) {
            // ignore
        }

        @Override
        public void rangeChanged(@NotNull final String range) {
            // ignore
        }

        @Override
        public void activeSkillChanged(@NotNull final String activeSkill) {
            // ignore
        }

        @Override
        public void experienceChanged(final long exp) {
            // ignore
        }

        @Override
        public void experienceNextLevelChanged(final long expNextLevel) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param font the font to use
     * @param colorNormal the normal color
     * @param colorUpgradable the color for upgradable stats
     * @param colorDepleted the color for depleted stats
     * @param colorBoosted the color for boosted stats
     * @param colorBoostedUpgradable the color for boosted and upgradable stats
     * @param backgroundColor the background color
     * @param statCurrent the current stat to display
     * @param statBase the base stat without applied boosts or depletions
     * @param statRace the race's maximum stat
     * @param statApplied the stat change due to gear or skills
     * @param alignment the text alignment
     * @param stats the stats instance to use
     * @param guiFactory the global GUI factory instance
     */
    public GUILabelStats2(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final Font font, @NotNull final Color colorNormal, @NotNull final Color colorUpgradable, @NotNull final Color colorDepleted, @NotNull final Color colorBoosted, @NotNull final Color colorBoostedUpgradable, @Nullable final Color backgroundColor, final int statCurrent, final int statBase, final int statRace, final int statApplied, @NotNull final Alignment alignment, @NotNull final Stats stats, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, null, font, colorNormal, backgroundColor, alignment, "", guiFactory);
        this.colorUpgradable = colorUpgradable;
        this.colorDepleted = colorDepleted;
        this.colorBoosted = colorBoosted;
        this.colorBoostedUpgradable = colorBoostedUpgradable;
        this.statCurrent = statCurrent;
        this.statBase = statBase;
        this.statRace = statRace;
        this.statApplied = statApplied;
        this.stats = stats;
        this.stats.addCrossfireStatsListener(statsListener);
        color = colorNormal;
        updateStat();
    }

    /**
     * Updates the values to reflect the current stat value.
     */
    private void updateStat() {
        final int baseValue = stats.getStat(statBase);
        final int raceValue = stats.getStat(statRace);
        final int appliedValue = stats.getStat(statApplied);
        final int currValue = stats.getStat(statCurrent);
        final int currValueWithoutGear = currValue-appliedValue;
        if (baseValue == 0 && raceValue == 0) {
            // no server support
            color = GUILabelStats2.super.getTextColor();
            setText(String.valueOf(currValue));
            return;
        }

        final Color newColor;
        if (currValueWithoutGear < baseValue) {
            newColor = colorDepleted;
        } else if (currValueWithoutGear == baseValue) {
            newColor = baseValue < raceValue ? colorUpgradable : GUILabelStats2.super.getTextColor();
        } else {
            newColor = baseValue < raceValue ? colorBoostedUpgradable : colorBoosted;
        }
        if (color != newColor) {
            color = newColor;
            setChanged();
        }
        setText(String.valueOf(currValue));
    }

    @Override
    public void dispose() {
        super.dispose();
        stats.removeCrossfireStatsListener(statsListener);
    }

    @NotNull
    @Override
    protected Color getTextColor() {
        return color;
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public @Nullable TooltipText getTooltip() {
        final int baseValue = stats.getStat(statBase);
        final int raceValue = stats.getStat(statRace);
        final int currValue = stats.getStat(statCurrent);
        if (baseValue == 0 && raceValue == 0) {
            return newTooltipText("Current: "+currValue);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("<html>Current: ").append(currValue);
        final int appliedValue = stats.getStat(statApplied);
        final int currValueWithoutGear = currValue-appliedValue;
        if (currValueWithoutGear < baseValue) {
            sb.append("<br>Depleted by ").append(baseValue-currValueWithoutGear).append(" from ").append(baseValue).append(".");
        } else if (currValueWithoutGear > baseValue) {
            sb.append("<br>Increased by ").append(currValueWithoutGear-baseValue).append(" from ").append(baseValue).append(".");
        }
        if (appliedValue > 0) {
            sb.append("<br>Boosted by ").append(appliedValue).append(" by gear or skills.");
        } else if (appliedValue < 0) {
            sb.append("<br>Reduced by ").append(-appliedValue).append(" by gear or skills.");
        }
        if (baseValue < raceValue) {
            sb.append("<br>Upgradable to ").append(raceValue).append(" by drinking stat potions.");
        }
        return newTooltipText(sb.toString());
    }

}
