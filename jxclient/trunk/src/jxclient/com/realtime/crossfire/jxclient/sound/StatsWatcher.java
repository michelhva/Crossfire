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

package com.realtime.crossfire.jxclient.sound;

import com.realtime.crossfire.jxclient.gui.gui.JXCWindowRenderer;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiState;
import com.realtime.crossfire.jxclient.gui.gui.RendererGuiStateListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import org.jetbrains.annotations.NotNull;

/**
 * Monitors stat changes and generates appropriate sound effects.
 * @author Andreas Kirschbaum
 */
public class StatsWatcher {

    /**
     * Duration for which to ignore level changes after login.
     */
    private static final long DELAY = 1000;

    /**
     * The {@link SoundManager} instance to watch.
     */
    @NotNull
    private final SoundManager soundManager;

    /**
     * Whether sounds should be generated.
     */
    private boolean active;

    /**
     * The last known poisoned state.
     */
    private boolean poisoned;

    /**
     * The last know level.
     */
    private int level;

    /**
     * Ignore level changes until this time has reached. This is used to
     * suppress false positives right after login. The value <code>0</code>
     * means not to ignore level changes.
     */
    private long ignoreLevelChange = System.currentTimeMillis()+DELAY;

    /**
     * The crossfire stats listener.
     */
    @NotNull
    private final StatsListener statsListener = new StatsListener() {

        @Override
        public void reset() {
            ignoreLevelChange = System.currentTimeMillis()+DELAY;
        }

        @Override
        public void statChanged(final int statNo, final int value) {
            checkStats(statNo, value);
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
     * The gui state listener.
     */
    @NotNull
    private final RendererGuiStateListener rendererGuiStateListener = new RendererGuiStateListener() {

        @Override
        public void guiStateChanged(@NotNull final RendererGuiState rendererGuiState) {
            active = rendererGuiState == RendererGuiState.PLAYING;
            ignoreLevelChange = System.currentTimeMillis()+DELAY;
        }

    };

    /**
     * The {@link CrossfireUpdateItemListener} to receive item updates.
     */
    @NotNull
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener() {

        @Override
        public void delinvReceived(final int tag) {
            // ignore
        }

        @Override
        public void delitemReceived(@NotNull final int[] tags) {
            // ignore
        }

        @Override
        public void addItemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
            // ignore
        }

        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
            ignoreLevelChange = System.currentTimeMillis()+DELAY;
        }

        @Override
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param stats the stats instance to watch
     * @param windowRenderer the window renderer instance
     * @param server the crossfire server connection to watch
     * @param soundManager the sound manager instance to watch
     */
    public StatsWatcher(@NotNull final Stats stats, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final CrossfireServerConnection server, @NotNull final SoundManager soundManager) {
        this.soundManager = soundManager;
        poisoned = stats.getStat(CrossfireStatsListener.C_STAT_POISONED) != 0;
        level = stats.getStat(CrossfireStatsListener.CS_STAT_LEVEL);
        stats.addCrossfireStatsListener(statsListener);
        windowRenderer.addGuiStateListener(rendererGuiStateListener);
        rendererGuiStateListener.guiStateChanged(windowRenderer.getGuiState());
        server.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
    }

    /**
     * Checks for changed stats and generate sound effects.
     * @param statNo the changed stat number
     * @param value the new stat value
     */
    private void checkStats(final int statNo, final int value) {
        if (statNo == CrossfireStatsListener.C_STAT_POISONED) {
            final boolean newPoisoned = value != 0;
            if (poisoned != newPoisoned) {
                poisoned = newPoisoned;
                if (active) {
                    playClip(newPoisoned ? Sounds.POISON_ON : Sounds.POISON_OFF);
                }
            }
        } else if (statNo == CrossfireStatsListener.CS_STAT_LEVEL) {
            final int newLevel = value;
            if (level != newLevel) {
                if (ignoreLevelChange != 0 && ignoreLevelChange <= System.currentTimeMillis()) {
                    ignoreLevelChange = 0;
                }
                if (ignoreLevelChange == 0 && level < newLevel && active) {
                    playClip(Sounds.LEVEL_UP);
                }
                level = newLevel;
            }
        }
    }

    /**
     * Plays a clip if sounds should be generated.
     * @param clip the sound clip to play
     */
    private void playClip(@NotNull final String clip) {
        if (active) {
            soundManager.playClip(Sounds.CHARACTER, null, clip);
        }
    }

}
