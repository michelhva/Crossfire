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

import com.realtime.crossfire.jxclient.util.DebugWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Plays background music. At most one background music can be concurrently
 * active (except for fading in/out effects).
 * @author Andreas Kirschbaum
 */
public class MusicManager {

    /**
     * The {@link AudioFileLoader} for loading audio files.
     */
    @NotNull
    private final AudioFileLoader audioFileLoader;

    /**
     * The writer for logging sound related information or <code>null</code> to
     * not log.
     */
    @Nullable
    private final DebugWriter debugSound;

    /**
     * The currently running processor, or <code>null</code> if none is active
     * or if music is disabled.
     */
    @Nullable
    private Processor processor = null;

    /**
     * The {@link Thread} executing {@link #processor}. Set to <code>null</code>
     * if none is executing.
     */
    @Nullable
    private Thread thread = null;

    /**
     * Whether background music is enabled. (User setting)
     */
    private boolean enabled = false;

    /**
     * Whether background music is muted. (Depends on connection state)
     */
    private boolean muted = true;

    /**
     * The currently playing music name. Set to <code>null</code> if no music is
     * playing.
     */
    @Nullable
    private String name = null;

    /**
     * Creates a new instance.
     * @param audioFileLoader the audio file loader for loading audio files
     * @param debugSound the writer for logging sound related information or
     * <code>null</code> to not log
     */
    public MusicManager(@NotNull final AudioFileLoader audioFileLoader, @Nullable final DebugWriter debugSound) {
        this.audioFileLoader = audioFileLoader;
        this.debugSound = debugSound;
    }

    /**
     * Plays the given music. If the new music name is unchanged, continue
     * playing.
     * @param name the music name, or <code>null</code> to stop playing music
     */
    public void play(@Nullable final String name) {
        if (this.name == null ? name == null : this.name.equals(name)) {
            return;
        }

        if (debugSound != null) {
            debugSound.debugProtocolWrite("play: "+name);
        }
        this.name = name;
        restart();
    }

    /**
     * Sets whether background music is enabled.
     * @param enabled whether background music is enabled
     */
    public void setEnabled(final boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        if (debugSound != null) {
            debugSound.debugProtocolWrite("setEnabled: "+enabled);
        }
        this.enabled = enabled;
        restart();
    }

    /**
     * Sets whether background music is muted.
     * @param muted whether background music is muted
     */
    public void setMuted(final boolean muted) {
        if (this.muted == muted) {
            return;
        }

        if (debugSound != null) {
            debugSound.debugProtocolWrite("setMuted: "+muted);
        }
        this.muted = muted;
        restart();
    }

    /**
     * Restarts the current music. Take into account {@link #enabled} and {@link
     * #muted} settings.
     */
    private void restart() {
        if (processor != null) {
            processor.terminate(enabled);
            processor = null;
        }

        if (enabled && !muted && name != null) {
            processor = new Processor(name, audioFileLoader);
            thread = new Thread(processor, "JXClient:MusicManager");
            thread.start();
        }
    }

    /**
     * Terminates a playing background music and free resources.
     */
    public void shutdown() {
        if (processor != null) {
            processor.terminate(false);
            processor = null;
        }
        if (thread != null) {
            try {
                thread.join();
            } catch (final InterruptedException ex) {
                throw new AssertionError(ex);
            }
            thread = null;
        }
    }

}
