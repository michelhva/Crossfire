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
     * The clip manager for playing sound effects.
     */
    private static final ClipManager clipManager = new ClipManager();

    /**
     * The music manager for playing background music.
     */
    private static final MusicManager musicManager = new MusicManager();

    /**
     * Whether sound is enabled.
     */
    private boolean enabled;

    /**
     * The muted sounds.
     */
    private final EnumSet<Sounds> mutedSounds = EnumSet.allOf(Sounds.class);

    /**
     * Set whether the sound system is enabled.
     *
     * @param enabled Whether the sound system is enabled.
     */
    public void setEnabled(final boolean enabled)
    {
        if (this.enabled == enabled)
        {
            return;
        }

        this.enabled = enabled;
        musicManager.setEnabled(enabled);
    }

    /**
     * Play a sound clip.
     *
     * @param type The sound type.
     *
     * @param name An optional prefix for the action name.
     *
     * @param action The sound action name.
     */
    public void playClip(final Sounds type, final String name, final String action)
    {
        if (type == null) throw new IllegalArgumentException();
        if (action == null) throw new IllegalArgumentException();

        if (enabled && !mutedSounds.contains(type))
        {
            clipManager.play(name, action);
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

    /**
     * Play a background music. If the new music name is unchanged, continue
     * playing.
     *
     * @param name The music name.
     */
    public void playMusic(final String name)
    {
        musicManager.play(name);
    }

    /**
     * Mute or unmute background music.
     *
     * @param muted Whether to mute (<code>true</code>) or unmute
     * (<code>false</code>).
     */
    public void muteMusic(final boolean muted)
    {
        musicManager.setMuted(muted);
    }

    /**
     * Terminate all sounds and free resources.
     */
    public void shutdown()
    {
        musicManager.shutdown();
        clipManager.shutdown();
    }
}
