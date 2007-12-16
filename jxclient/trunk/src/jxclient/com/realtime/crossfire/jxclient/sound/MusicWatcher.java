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

import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireMusicListener;

/**
 * Monitors music commands and generates appropriate sound effects.
 *
 * @author Andreas Kirschbaum
 */
public class MusicWatcher
{
    /**
     * The name of the currently playing sound clip, or <code>"NONE"</code> if
     * none is active.
     */
    private String music = "NONE";

    /**
     * The crossfire stats listener attached to {@link #stats}.
     */
    private final CrossfireMusicListener crossfireMusicListener = new CrossfireMusicListener()
    {
        /** {@inheritDoc} */
        public void commandMusicReceived(final String music)
        {
            SoundManager.instance.playMusic(music.equals("NONE") ? null : music);
        }
    };

    /**
     * Create a new instance.
     */
    public MusicWatcher(final CrossfireServerConnection crossfireServerConnection)
    {
        crossfireServerConnection.addCrossfireMusicListener(crossfireMusicListener);
    }
}
