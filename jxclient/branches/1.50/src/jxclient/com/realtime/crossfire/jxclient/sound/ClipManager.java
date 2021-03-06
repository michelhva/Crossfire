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
     * The executor service used to play sound clips.
     */
    @NotNull
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * The clip cache used to allocate new clips.
     */
    @NotNull
    private final ClipCache clipCache = new ClipCache();

    /**
     * Play the given sound effect. This function returns immediately.
     * @param name An optional prefix for the action name.
     * @param action The action name of the sound effect.
     */
    public void play(@Nullable final String name, @NotNull final String action) {
        final DataLine clip = clipCache.allocateClip(name, action);
        if (clip == null) {
            return;
        }

        executorService.execute(new Runnable() {
            /** {@inheritDoc} */
            @Override
            public void run() {
                try {
                    clip.start();
                    try {
                        clip.drain();
                    } finally {
                        clip.stop();
                    }
                } finally {
                    clipCache.freeClip(clip);
                }
            }
        });
    }

    /**
     * Terminate all running clips and free resources.
     */
    public void shutdown() {
        executorService.shutdownNow();
    }

}
