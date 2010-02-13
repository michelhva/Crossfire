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

import org.jetbrains.annotations.Nullable;

/**
 * Plays background music. At most one background music can be concurrently
 * active (except for fading in/out effects).
 *
 * @author Andreas Kirschbaum
 */
public class MusicManager
{
    /**
     * The currently running processor, or <code>null</code> if none is active
     * or if music is disabled.
     */
    @Nullable
    private Processor processor = null;

    /**
     * Whether background music is enabled. (User setting)
     */
    private boolean enabled = false;

    /**
     * Whether background music is muted. (Depends on connection state)
     */
    private boolean muted = true;

    /**
     * The currently playing music name. Set to <code>null</code> if no music
     * is playing.
     */
    @Nullable
    private String name = null;

    /**
     * Play the given music. If the new music name is unchanged, continue
     * playing.
     *
     * @param name The music name, or <code>null</code> to stop playing music.
     */
    public void play(@Nullable final String name)
    {
        if (this.name == null ? name == null : this.name.equals(name))
        {
            return;
        }

        this.name = name;
        restart();
    }

    /**
     * Set whether background music is enabled.
     *
     * @param enabled Whether background music is enabled.
     */
    public void setEnabled(final boolean enabled)
    {
        if (this.enabled == enabled)
        {
            return;
        }

        this.enabled = enabled;
        restart();
    }

    /**
     * Set whether background music is muted.
     *
     * @param muted Whether background music is muted.
     */
    public void setMuted(final boolean muted)
    {
        if (this.muted == muted)
        {
            return;
        }

        this.muted = muted;
        restart();
    }

    /**
     * Restart the current music. Take into account {@link #enabled} and {@link
     * #muted} settings.
     */
    private void restart()
    {
        if (processor != null)
        {
            processor.terminate(enabled, false);
            processor = null;
        }

        if (enabled && !muted && name != null)
        {
            processor = new Processor(name);
            processor.start();
        }
    }

    /**
     * Terminate a playing background music and free resources.
     */
    public void shutdown()
    {
        if (processor != null)
        {
            processor.terminate(false, true);
            processor = null;
        }
    }
}
