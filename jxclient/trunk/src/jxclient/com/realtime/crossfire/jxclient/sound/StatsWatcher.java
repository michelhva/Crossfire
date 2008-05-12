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
package com.realtime.crossfire.jxclient.sound;

import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.PlayerListener;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.stats.StatsEvent;
import com.realtime.crossfire.jxclient.stats.StatsListener;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;

/**
 * Monitors stat changes and generates appropriate sound effects.
 *
 * @author Andreas Kirschbaum
 */
public class StatsWatcher
{
    /**
     * Duration for which to ignore level changes after login.
     */
    private static final long DELAY = 1000;

    /**
     * The {@link SoundManager} instance to watch.
     */
    private final SoundManager soundManager;

    /**
     * The stats instance to watch.
     */
    private final Stats stats;

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
     * The crossfire stats listener attached to {@link #stats}.
     */
    private final StatsListener statsListener = new StatsListener()
    {
        /** {@inheritDoc} */
        public void statChanged(final StatsEvent evt)
        {
            checkStats();
            if (evt.isReset())
            {
                ignoreLevelChange = System.currentTimeMillis()+DELAY;
            }
        }
    };

    /**
     * The gui state listener.
     */
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        public void guiStateChanged(final JXCWindowRenderer.GuiState guiState)
        {
            active = guiState == JXCWindowRenderer.GuiState.PLAYING;
            ignoreLevelChange = System.currentTimeMillis()+DELAY;
        }
    };

    /**
     * The player listener.
     */
    private final PlayerListener playerListener = new PlayerListener()
    {
        /** {@inheritDoc} */
        public void playerReceived(final CfPlayer player)
        {
            ignoreLevelChange = System.currentTimeMillis()+DELAY;
        }

        /** {@inheritDoc} */
        public void playerAdded(final CfPlayer player)
        {
            // ignore
        }

        /** {@inheritDoc} */
        public void playerRemoved(final CfPlayer player)
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param stats The stats instance to watch.
     * @param windowRenderer The window renderer instance.
     * @param itemsManager the instance to watch
     * @param soundManager the sound manager instance to watch
     */
    public StatsWatcher(final Stats stats, final JXCWindowRenderer windowRenderer, final ItemsManager itemsManager, final SoundManager soundManager)
    {
        this.stats = stats;
        this.soundManager = soundManager;
        poisoned = stats.getStat(CrossfireStatsListener.C_STAT_POISONED) != 0;
        level = stats.getStat(CrossfireStatsListener.CS_STAT_LEVEL);
        stats.addCrossfireStatsListener(statsListener);
        windowRenderer.addGuiStateListener(guiStateListener);
        guiStateListener.guiStateChanged(windowRenderer.getGuiState());
        itemsManager.addCrossfirePlayerListener(playerListener);
    }

    /**
     * Check for changed stats and generate sound effects.
     */
    private void checkStats()
    {
        final boolean newPoisoned = stats.getStat(CrossfireStatsListener.C_STAT_POISONED) != 0;
        if (poisoned != newPoisoned)
        {
            poisoned = newPoisoned;
            if (active)
            {
                playClip(newPoisoned ? Sounds.POISON_ON : Sounds.POISON_OFF);
            }
        }

        final int newLevel = stats.getStat(CrossfireStatsListener.CS_STAT_LEVEL);
        if (level != newLevel)
        {
            if (ignoreLevelChange != 0 && ignoreLevelChange <= System.currentTimeMillis())
            {
                ignoreLevelChange = 0;
            }
            if (ignoreLevelChange == 0 && level < newLevel && active)
            {
                playClip(Sounds.LEVEL_UP);
            }
            level = newLevel;
        }
    }

    /**
     * Play a clip if sounds should be generated.
     *
     * @param clip The sound clip to play.
     */
    private void playClip(final String clip)
    {
        if (active)
        {
            soundManager.playClip(Sounds.CHARACTER, null, clip);
        }
    }
}
