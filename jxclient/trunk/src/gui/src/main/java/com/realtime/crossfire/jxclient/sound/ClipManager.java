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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.DataLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages a set of sound clips (short sound effects). Multiple sound effects
 * can be played simultaneously.
 * @author Andreas Kirschbaum
 */
public class ClipManager {

    /**
     * The writer for logging sound related information or {@code null} to not
     * log.
     */
    @Nullable
    private final DebugWriter debugSound;

    /**
     * The global {@link SoundTaskExecutor} instance.
     */
    @NotNull
    private final SoundTaskExecutor soundTaskExecutor;

    /**
     * The executor service used to play sound clips.
     */
    @NotNull
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * The clip cache used to allocate new clips.
     */
    @NotNull
    private final ClipCache clipCache;

    /**
     * Creates a new instance.
     * @param audioFileLoader the audio file loader for loading audio files
     * @param debugSound the writer for logging sound related information or
     * {@code null} to not log
     * @param soundTaskExecutor the global sound task executor
     */
    public ClipManager(@NotNull final AudioFileLoader audioFileLoader, @Nullable final DebugWriter debugSound, @NotNull final SoundTaskExecutor soundTaskExecutor) {
        this.debugSound = debugSound;
        this.soundTaskExecutor = soundTaskExecutor;
        clipCache = new ClipCache(audioFileLoader, debugSound);
    }

    /**
     * Plays the given sound effect. This function returns immediately.
     * @param name an optional prefix for the action name
     * @param action the action name of the sound effect
     */
    public void play(@Nullable final String name, @NotNull final String action) {
        final CompletableFuture<DataLine> future = new CompletableFuture<>();
        soundTaskExecutor.execute(() -> future.complete(clipCache.allocateClip(name, action)));
        final DataLine clip;
        try {
            clip = future.get();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (debugSound != null) {
                debugSound.debugProtocolWrite("interrupted while allocating clip ["+name+", "+action+"]", ex);
            }
            return;
        } catch (final ExecutionException ex) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("failed to allocate clip ["+name+", "+action+"]", ex);
            }
            return;
        }
        if (clip == null) {
            return;
        }

        executorService.execute(() -> {
            try {
                clip.start();
                try {
                    clip.drain();
                } finally {
                    clip.stop();
                }
            } finally {
                try {
                    soundTaskExecutor.executeAndWait(() -> clipCache.freeClip(clip));
                } catch (final InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /**
     * Terminates all running clips and free resources.
     * @throws InterruptedException if the current thread was interrupted while
     * waiting for the shutdown
     */
    public void shutdown() throws InterruptedException {
        soundTaskExecutor.executeAndWait(executorService::shutdownNow);
    }

}
