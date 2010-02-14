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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.sound;

import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireSoundListener;
import org.jetbrains.annotations.NotNull;

/**
 * Monitors sound and sound2 commands received from the server to generate
 * sound effects.
 *
 * @author Andreas Kirschbaum
 */
public class SoundWatcher
{
    /**
     * The {@link SoundManager} instance to watch.
     */
    @NotNull
    private final SoundManager soundManager;

    /**
     * The crossfire sound listener.
     */
    @NotNull
    private final CrossfireSoundListener crossfireSoundListener = new CrossfireSoundListener()
    {
        /** {@inheritDoc} */
        @Override
        public void commandSoundReceived(final int x, final int y, final int num, final int type)
        {
            // ignored
        }

        @Override
        public void commandSound2Received(final int x, final int y, final int dir, final int volume, final int type, @NotNull final String action, @NotNull final String name)
        {
            soundManager.playClip(Sounds.CHARACTER, name, action);
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to watch
     * @param soundManager the sound manager instance to watch
     */
    public SoundWatcher(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final SoundManager soundManager)
    {
        crossfireServerConnection.addCrossfireSoundListener(crossfireSoundListener);
        this.soundManager = soundManager;
    }
}
