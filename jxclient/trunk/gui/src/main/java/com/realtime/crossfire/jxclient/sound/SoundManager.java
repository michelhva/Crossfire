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

import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.util.DebugWriter;
import java.util.Collection;
import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all sounds. Each sound has a sound type ({@link Sounds}) attached.
 * Sound types can be disabled (by the user) or muted (by the application). A
 * sound is played only if it is neither disabled nor muted.
 * @author Andreas Kirschbaum
 */
public class SoundManager {

    /**
     * The clip manager for playing sound effects.
     */
    @NotNull
    private final ClipManager clipManager;

    /**
     * The writer for logging sound related information or <code>null</code> to
     * not log.
     */
    @Nullable
    private final DebugWriter debugSound;

    /**
     * The music manager for playing background music.
     */
    @NotNull
    private final MusicManager musicManager;

    /**
     * Whether sound is enabled.
     */
    private boolean enabled;

    /**
     * The muted sounds.
     */
    @NotNull
    private final Collection<Sounds> mutedSounds = EnumSet.allOf(Sounds.class);

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener() {

        @Override
        public void start() {
            muteMusic(true);
            mute(Sounds.CHARACTER, true);
        }

        @Override
        public void metaserver() {
            muteMusic(true);
            mute(Sounds.CHARACTER, true);
        }

        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        @Override
        public void connecting(@NotNull final String serverInfo) {
            muteMusic(true);
            mute(Sounds.CHARACTER, true);
        }

        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        @Override
        public void connected() {
            muteMusic(false);
            mute(Sounds.CHARACTER, false);
        }

        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }

    };

    /**
     * Creates a new instance.
     * @param guiStateManager the gui state manager to watch
     * @param debugSound the writer for logging sound related information or
     * <code>null</code> to not log
     */
    public SoundManager(@NotNull final GuiStateManager guiStateManager, @Nullable final DebugWriter debugSound) {
        final AudioFileLoader audioFileLoader = new AudioFileLoader(debugSound);
        clipManager = new ClipManager(audioFileLoader, debugSound);
        musicManager = new MusicManager(audioFileLoader, debugSound);
        this.debugSound = debugSound;
        guiStateManager.addGuiStateListener(guiStateListener);
    }

    /**
     * Sets whether the sound system is enabled.
     * @param enabled whether the sound system is enabled
     */
    public void setEnabled(final boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }

        this.enabled = enabled;
        musicManager.setEnabled(enabled);
    }

    /**
     * Plays a sound clip.
     * @param type the sound type
     * @param name an optional prefix for the action name
     * @param action the sound action name
     */
    public void playClip(@NotNull final Sounds type, @Nullable final String name, @NotNull final String action) {
        if (!enabled) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("playClip(type="+type+", name="+name+", action="+action+"): sound is muted");
            }
            return;
        }

        if (mutedSounds.contains(type)) {
            if (debugSound != null) {
                debugSound.debugProtocolWrite("playClip(type="+type+", name="+name+", action="+action+"): sound type is muted");
            }
            return;
        }

        if (debugSound != null) {
            debugSound.debugProtocolWrite("playClip(type="+type+", name="+name+", action="+action+")");
        }
        clipManager.play(name, action);
    }

    /**
     * Mutes or unmutes sound effects.
     * @param type the sound type to affect
     * @param mute whether to mute (<code>true</code>) or unmute
     * (<code>false</code>)
     */
    private void mute(@NotNull final Sounds type, final boolean mute) {
        if (mute) {
            mutedSounds.add(type);
        } else {
            mutedSounds.remove(type);
            // XXX: stop running sounds of type
        }
    }

    /**
     * Plays a background music. If the new music name is unchanged, continue
     * playing.
     * @param name the music name
     */
    public void playMusic(@Nullable final String name) {
        musicManager.play(name);
    }

    /**
     * Mutes or unmutes background music.
     * @param muted whether to mute (<code>true</code>) or unmute
     * (<code>false</code>)
     */
    private void muteMusic(final boolean muted) {
        musicManager.setMuted(muted);
    }

    /**
     * Terminates all sounds and free resources.
     */
    public void shutdown() {
        if (debugSound != null) {
            debugSound.debugProtocolWrite("shutdown");
        }
        musicManager.shutdown();
        clipManager.shutdown();
    }

}
