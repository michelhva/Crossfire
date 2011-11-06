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
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allocates new sound clips.
 * @author Andreas Kirschbaum
 */
public class ClipCache {

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
     * Creates a new instance.
     * @param audioFileLoader the audio file loader for loading audio files
     * @param debugSound the writer for logging sound related information or
     * <code>null</code> to not log
     */
    public ClipCache(@NotNull final AudioFileLoader audioFileLoader, @Nullable final DebugWriter debugSound) {
        this.audioFileLoader = audioFileLoader;
        this.debugSound = debugSound;
    }

    /**
     * Allocates a new clip.
     * @param name an optional prefix for the action name
     * @param action the action name of the clip to allocate
     * @return the new clip, or <code>null</code> if an error occurs
     */
    @Nullable
    public DataLine allocateClip(@Nullable final String name, @NotNull final String action) {
        return newClip(name, action);
    }

    /**
     * Deallocates a clip.
     * @param clip the clip to deallocate
     */
    public void freeClip(@NotNull final Line clip) {
        if (debugSound != null) {
            debugSound.debugProtocolWrite("freeClip: "+System.identityHashCode(clip));
        }
        clip.close();
    }

    /**
     * Allocates a new clip.
     * @param name an optional prefix for the action name
     * @param action the action name of the clip to allocate
     * @return the new clip, or <code>null</code> if an error occurs
     */
    @Nullable
    private DataLine newClip(@Nullable final String name, @NotNull final String action) {
        try {
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFileLoader.getInputStream(name, action));
            try {
                final Clip clip = AudioSystem.getClip();
                try {
                    clip.open(audioInputStream);
                } catch (final IllegalArgumentException ex) {
                    throw new UnsupportedAudioFileException(ex.getMessage());
                }
                if (debugSound != null) {
                    debugSound.debugProtocolWrite("newClip: "+System.identityHashCode(clip)+" "+name+"/"+action);
                }
                return clip;
            } finally {
                audioInputStream.close();
            }
        } catch (final UnsupportedAudioFileException ex) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("newClip["+name+"/"+action+"]: "+ex.getMessage());
            }
            return null;
        } catch (final LineUnavailableException ex) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("newClip["+name+"/"+action+"]: "+ex.getMessage());
            }
            return null;
        } catch (final IOException ex) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("newClip["+name+"/"+action+"]: "+ex.getMessage());
            }
            return null;
        }
    }

}
