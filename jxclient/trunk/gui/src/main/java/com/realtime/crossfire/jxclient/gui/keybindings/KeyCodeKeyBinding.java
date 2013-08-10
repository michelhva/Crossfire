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

package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.gui.commandlist.CommandList;
import java.awt.event.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link KeyBinding} that matches by key code/modifiers pair.
 * @author Andreas Kirschbaum
 */
public class KeyCodeKeyBinding extends KeyBinding {

    /**
     * The key to match.
     */
    @NotNull
    private final KeyEvent2 keyEvent;

    /**
     * Creates a {@link KeyBinding} that matches by key code/modifiers pair.
     * @param keyEvent the key to match
     * @param commands the commands to associate with this binding
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     */
    public KeyCodeKeyBinding(@NotNull final KeyEvent2 keyEvent, @NotNull final CommandList commands, final boolean isDefault) {
        super(commands, isDefault);
        this.keyEvent = keyEvent;
    }

    /**
     * Returns the key to match.
     * @return the key to match
     */
    @NotNull
    public KeyEvent2 getKeyEvent2() {
        return keyEvent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null || !(obj instanceof KeyCodeKeyBinding)) {
            return false;
        }

        final KeyCodeKeyBinding keyBinding = (KeyCodeKeyBinding)obj;
        return keyBinding.keyEvent.equalsKeyCode(keyEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return keyEvent.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesKeyCode(@NotNull final KeyEvent2 keyEvent) {
        return this.keyEvent.equalsKeyCode(keyEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesKeyChar(final char keyChar) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String getBindingDescription() {
        final StringBuilder sb = new StringBuilder();
        final int modifiers = keyEvent.getModifiers();
        if (modifiers != KeyEvent2.NONE) {
            sb.append(KeyEvent.getKeyModifiersText(modifiers)).append("+");
        }
        sb.append(KeyEvent.getKeyText(keyEvent.getKeyCode()));
        return sb.toString();
    }
}
