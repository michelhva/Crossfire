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

import com.realtime.crossfire.jxclient.util.DebugWriter;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Locates audio files.
 * @author Andreas Kirschbaum
 */
public class AudioFileLoader {

    /**
     * The writer for logging sound related information or <code>null</code> to
     * not log.
     */
    @Nullable
    private final DebugWriter debugSound;

    /**
     * Private constructor to prevent instantiation.
     * @param debugSound the writer for logging sound related information or
     * <code>null</code> to not log
     */
    public AudioFileLoader(@Nullable final DebugWriter debugSound) {
        this.debugSound = debugSound;
    }

    /**
     * Returns an input stream for an audio file. <code>action</code> identifies
     * the audio file. <code>name</code> is an optional prefix.
     * @param name the name
     * @param action the action; may be <code>null</code>
     * @return the input stream
     * @throws IOException if the file cannot be located
     */
    @NotNull
    public InputStream getInputStream(@Nullable final String name, @NotNull final String action) throws IOException {
        @Nullable final IOException savedException;
        if (name != null) {
            try {
                return getResource(name+"/"+action);
            } catch (final IOException ex) {
                savedException = ex;
            }
        } else {
            savedException = null;
        }

        try {
            return getResource(action);
        } catch (final IOException ex) {
            throw savedException != null ? savedException : ex;
        }
    }

    /**
     * Returns an input stream for an audio file.
     * @param name the resource name
     * @return the input stream
     * @throws IOException if the file cannot be located
     */
    @NotNull
    private InputStream getResource(@NotNull final String name) throws IOException {
        final String resource = "resource/sounds/"+name+".wav";

        final InputStream inputStream = AudioFileLoader.class.getClassLoader().getResourceAsStream(resource);
        if (inputStream != null) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("resource: ["+resource+"] found");
            }
            return inputStream;
        }
        if (debugSound != null) {
            debugSound.debugProtocolWrite("resource: ["+resource+"] not found");
        }

        throw new IOException("resource "+resource+" does not exist");
    }

}
