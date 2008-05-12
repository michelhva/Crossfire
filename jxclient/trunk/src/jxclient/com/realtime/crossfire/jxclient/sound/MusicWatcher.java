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

import com.realtime.crossfire.jxclient.server.CrossfireMusicListener;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;

/**
 * Monitors music commands and generates appropriate sound effects.
 *
 * @author Andreas Kirschbaum
 */
public class MusicWatcher
{
    /**
     * The {@link SoundManager} instance to watch.
     */
    private final SoundManager soundManager;

    /**
     * The crossfire stats listener.
     */
    private final CrossfireMusicListener crossfireMusicListener = new CrossfireMusicListener()
    {
        /** {@inheritDoc} */
        public void commandMusicReceived(final String music)
        {
            soundManager.playMusic(music.equals("NONE") ? null : music);
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection instance
     * @param soundManager the sound manager instance to watch
     */
    public MusicWatcher(final CrossfireServerConnection crossfireServerConnection, final SoundManager soundManager)
    {
        crossfireServerConnection.addCrossfireMusicListener(crossfireMusicListener);
        this.soundManager = soundManager;
    }
}
