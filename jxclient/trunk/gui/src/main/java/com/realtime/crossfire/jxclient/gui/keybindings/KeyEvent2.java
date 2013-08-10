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

import org.jetbrains.annotations.NotNull;

/**
 * Represents a pressed or released key.
 * @author Andreas Kirschbaum
 */
public class KeyEvent2 {

    /**
     * The key code. See {@link java.awt.event.KeyEvent VK_xxx} constants.
     */
    private final int keyCode;

    /**
     * The key as a char value.
     */
    private final char keyChar;

    /**
     * The modifiers. A combination of {@link java.awt.event.InputEvent#SHIFT_MASK},
     * {@link java.awt.event.InputEvent#CTRL_MASK}, {@link
     * java.awt.event.InputEvent#META_MASK}, {@link java.awt.event.InputEvent#ALT_MASK},
     * and {@link java.awt.event.InputEvent#ALT_GRAPH_MASK}.
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
     * Returns the modifiers. A combination of {@link java.awt.event.InputEvent#SHIFT_MASK},
     * {@link java.awt.event.InputEvent#CTRL_MASK}, {@link
     * java.awt.event.InputEvent#META_MASK}, {@link java.awt.event.InputEvent#ALT_MASK},
     * and {@link java.awt.event.InputEvent#ALT_GRAPH_MASK}.
     * @return the modifiers
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "code="+keyCode+", char="+keyChar+", modifiers="+modifiers;
    }

}
