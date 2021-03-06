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

package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the state for the key binding dialog.
 * @author Andreas Kirschbaum
 */
public class KeyBindingState {

    /**
     * The {@link KeyBindings} to modify.
     */
    @Nullable
    private final KeyBindings keyBindings;

    /**
     * The {@link KeyBindings} to modify.
     */
    @Nullable
    private final KeyBindings keyBindings2;

    /**
     * The commands to bind, or <code>null</code> to unbind.
     */
    @Nullable
    private final CommandList commands;

    /**
     * The dialog state: 0=waiting for initial key press, 1=waiting for key
     * release.
     */
    private int state = 0;

    /**
     * The type of key binding: -1=invalid, 0=key code ({@link #keyCode} and
     * {@link #modifiers} are valid), 1=key char ({@link #keyChar} is valid).
     */
    private int type = -1;

    /**
     * The key code. Only valid if <code>{@link #type} == 0</code>.
     */
    private int keyCode = 0;

    /**
     * The modifiers. Only valid if <code>{@link #type} == 0</code>.
     */
    private int modifiers = 0;

    /**
     * The key character. Only valid if <code>{@link #type} == 1</code>.
     */
    private char keyChar = '\0';

    /**
     * Creates a new instance.
     * @param keyBindings the <code>KeyBindings</code> to modify; may be
     * <code>null</code> when removing bindings
     * @param keyBindings2 the <code>KeyBindings</code> to modify; only used
     * when removing bindings; may be <code>null</code> when removing bindings
     * @param commands the commands to bind, or <code>null</code> to unbind
     */
    public KeyBindingState(@Nullable final KeyBindings keyBindings, @Nullable final KeyBindings keyBindings2, @Nullable final CommandList commands) {
        this.keyBindings = keyBindings;
        this.keyBindings2 = keyBindings2;
        this.commands = commands;
    }

    /**
     * Records a binding by key code.
     * @param keyCode the key code that was pressed
     * @param modifiers the bindings that are active
     */
    public void keyPressed(final int keyCode, final int modifiers) {
        state = 1;
        type = 0;
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    /**
     * Records a binding by key character.
     * @param keyChar the key character that was typed
     */
    public void keyTyped(final char keyChar) {
        if (state != 0) {
            type = 1;
            this.keyChar = keyChar;
        }
    }

    /**
     * Records a key released event.
     * @return <code>true</code> if the dialog has finished, or
     *         <code>false</code> if the dialog is still active
     */
    public boolean keyReleased() {
        if (state == 0) {
            return false;
        }

        assert type != -1;
        if (commands != null) {
            if (type == 0) {
                keyBindings.addKeyBindingAsKeyCode(keyCode, modifiers, commands, false);
            } else {
                keyBindings.addKeyBindingAsKeyChar(keyChar, commands, false);
            }
        } else {
            if (type == 0) {
                if (keyBindings != null) {
                    keyBindings.deleteKeyBindingAsKeyCode(keyCode, modifiers);
                }

                if (keyBindings2 != null) {
                    keyBindings2.deleteKeyBindingAsKeyCode(keyCode, modifiers);
                }
            } else {
                if (keyBindings != null) {
                    keyBindings.deleteKeyBindingAsKeyChar(keyChar);
                }

                if (keyBindings2 != null) {
                    keyBindings2.deleteKeyBindingAsKeyChar(keyChar);
                }
            }
        }

        return true;
    }

}
