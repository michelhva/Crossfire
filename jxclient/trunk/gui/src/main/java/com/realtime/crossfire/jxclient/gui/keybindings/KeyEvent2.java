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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a pressed or released key.
 * @author Andreas Kirschbaum
 */
public class KeyEvent2 {

    /**
     * The mask for "no modifier".
     */
    public static final int NONE = 0;

    /**
     * The mask for "shift".
     */
    public static final int ALT = InputEvent.ALT_MASK;

    /**
     * The mask for "alt_graph".
     */
    public static final int ALT_GRAPH = InputEvent.ALT_GRAPH_MASK;

    /**
     * The mask for "ctrl".
     */
    public static final int CTRL = InputEvent.CTRL_MASK;

    /**
     * The mask for "meta".
     */
    public static final int META = InputEvent.META_MASK;

    /**
     * The mask for "shift".
     */
    public static final int SHIFT = InputEvent.SHIFT_MASK;

    /**
     * The mask for all used modifiers.
     */
    public static final int MASK = ALT|ALT_GRAPH|CTRL|META|SHIFT;

    /**
     * The key code. See {@link java.awt.event.KeyEvent VK_xxx} constants.
     */
    private final int keyCode;

    /**
     * The key as a char value.
     */
    private final char keyChar;

    /**
     * The modifiers. A combination of {@link #ALT}, {@link #ALT_GRAPH}, {@link
     * #CTRL}, {@link #META}, and {@link #SHIFT}.
     */
    private final int modifiers;

    /**
     * Creates a new instance.
     * @param keyCode the key code
     * @param keyChar the key as a char
     * @param modifiers the modifiers
     */
    public KeyEvent2(final int keyCode, final char keyChar, final int modifiers) {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.modifiers = modifiers;
    }

    /**
     * Returns the key code. See {@link java.awt.event.KeyEvent VK_xxx}
     * constants.
     * @return the key code
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * Returns the key as a char.
     * @return the key as a char
     */
    public char getKeyChar() {
        return keyChar;
    }

    /**
     * Returns the modifiers. A combination of {@link #ALT}, {@link #ALT_GRAPH},
     * {@link #CTRL}, {@link #META}, and {@link #SHIFT}.
     * @return the modifiers
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns whether this key event matches the same key code as another key
     * event.
     * @param keyEvent the other key event
     * @return whether both instances match the same key code
     */
    public boolean equalsKeyCode(@NotNull final KeyEvent2 keyEvent) {
        return keyCode == keyEvent.keyCode && modifiers == keyEvent.modifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        final KeyEvent2 keyEvent = (KeyEvent2)obj;
        return keyCode == keyEvent.keyCode && keyChar == keyEvent.keyChar && modifiers == keyEvent.modifiers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return keyCode^keyChar^modifiers;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "code="+keyCode+", char="+(keyChar == KeyEvent.CHAR_UNDEFINED ? "undefined" : keyChar)+", modifiers="+modifiers;
    }

}
