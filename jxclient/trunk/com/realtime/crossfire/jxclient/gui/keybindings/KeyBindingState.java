//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.gui.keybindings;

import com.realtime.crossfire.jxclient.GUICommandList;
import com.realtime.crossfire.jxclient.JXCWindow;

/**
 * Manage the state for the key binding dialog.
 *
 * @author Andreas Kirschbaum
 */
public class KeyBindingState
{
    /**
     * The commands to bind, or <code>null</code> to unbind.
     */
    private final GUICommandList commands;

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
     * Create a new instance.
     *
     * @param commands The commands to bind, or <code>null</code> to unbind.
     */
    public KeyBindingState(final GUICommandList commands)
    {
        this.commands = commands;
    }

    /**
     * Record a binding by key code.
     *
     * @param keyCode The key code that was pressed.
     *
     * @param modifiers The bindings that are active.
     */
    public void keyPressed(final int keyCode, final int modifiers)
    {
        state = 1;
        type = 0;
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    /**
     * Record a binding by key character.
     *
     * @param keyChar The key character that was typed.
     */
    public void keyTyped(final char keyChar)
    {
        if (state != 0)
        {
            type = 1;
            this.keyChar = keyChar;
        }
    }

    /**
     * Record a key released event.
     *
     * @param keyBindings The <code>KeyBindings</code> to modify.
     *
     * @return <code>true</code> if the dialog has finished, or
     * <code>false</code> if the dialog is still active.
     */
    public boolean keyReleased(final KeyBindings keyBindings)
    {
        if (state == 0)
        {
            return false;
        }

        assert type != -1;
        if (commands != null)
        {
            if (type == 0)
            {
                keyBindings.addKeyBindingAsKeyCode(keyCode, modifiers, commands);
            }
            else
            {
                keyBindings.addKeyBindingAsKeyChar(keyChar, commands);
            }
        }
        else
        {
            if (type == 0)
            {
                keyBindings.deleteKeyBindingAsKeyCode(keyCode, modifiers);
            }
            else
            {
                keyBindings.deleteKeyBindingAsKeyChar(keyChar);
            }
        }

        return true;
    }
}
