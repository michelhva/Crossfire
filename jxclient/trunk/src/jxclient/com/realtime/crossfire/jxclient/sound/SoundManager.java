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

import java.io.File;
import java.util.EnumSet;

/**
 * Manages all sounds. Each sound has a sound type ({@link Sounds}) atatched.
 * Sound types can be disabled (by the user) or muted (by the application). A
 * sound is played only if it is neither disabled nor muted.
 *
 * @author Andreas Kirschbaum
 */
public class SoundManager
{
    /**
     * The singleton instance.
     */
    public static SoundManager instance = null;

    /**
     * The muted sounds.
     */
    private final EnumSet<Sounds> mutedSounds = EnumSet.allOf(Sounds.class);

    /**
     * Play a sound clip.
     *
     * @param type The sound type.
     *
     * @param name The sound name.
     */
    public void play(final Sounds type, final String name)
    {
        if (type == null) throw new IllegalArgumentException();
        if (name == null) throw new IllegalArgumentException();

        if (!mutedSounds.contains(type))
        {
            new SoundClip(name);
        }
    }

    /**
     * Mute or unmute sound effects.
     *
     * @param type The sound type to affect.
     *
     * @param mute Whether to mute (<code>true</code>) or unmute
     * (<code>false</code>).
     */
    public void mute(final Sounds type, final boolean mute)
    {
        if (mute)
        {
            mutedSounds.add(type);
        }
        else
        {
            mutedSounds.remove(type);
            // XXX: stop running sounds of type
        }
    }
}
