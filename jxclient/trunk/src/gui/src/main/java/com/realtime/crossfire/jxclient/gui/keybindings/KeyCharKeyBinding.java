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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link KeyBinding} that matches by key character.
 * @author Andreas Kirschbaum
 */
public class KeyCharKeyBinding extends KeyBinding {

    /**
     * The key character to match.
     */
    private final char keyChar;

    /**
     * Creates a {@link KeyBinding} that matches by key character.
     * @param keyChar the key character to match
     * @param commands the commands to associate with this binding
     * @param isDefault whether the key binding is a "default" binding which
     * should not be saved
     */
    public KeyCharKeyBinding(final char keyChar, @NotNull final CommandList commands, final boolean isDefault) {
        super(commands, isDefault);
        this.keyChar = keyChar;
    }

    /**
     * Returns the key character to match.
     * @return the key character to match
     */
    public char getKeyChar() {
        return keyChar;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof KeyCharKeyBinding)) {
            return false;
        }

        final KeyCharKeyBinding keyBinding = (KeyCharKeyBinding)obj;
        return keyBinding.keyChar == keyChar;
    }

    @Override
    public int hashCode() {
        return keyChar;
    }

    @Override
    public boolean matchesKeyCode(@NotNull final KeyEvent2 keyEvent) {
        return false;
    }

    @Override
    public boolean matchesKeyChar(final char keyChar) {
        return this.keyChar == keyChar;
    }

    @NotNull
    @Override
    public String getBindingDescription() {
        if (keyChar == '\n') {
            return "return";
        }
        if (keyChar == '\t') {
            return "tab";
        }
        if (keyChar == ' ') {
            return "space";
        }
        return new String(new char[] { keyChar });
    }

}
