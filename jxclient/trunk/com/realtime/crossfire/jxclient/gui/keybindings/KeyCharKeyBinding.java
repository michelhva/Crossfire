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

/**
 * A {@link KeyBinding} that matches by key character.
 *
 * @author Andreas Kirschbaum
 */
public final class KeyCharKeyBinding extends KeyBinding
{
    /**
     * The key character to match.
     */
    private final int keyChar;

    /**
     * Create a {@link KeyBinding} that matches by key character.
     *
     * @param keyChar The key character to match.
     *
     * @param commands The commands to associate with this binding.
     */
    public KeyCharKeyBinding(final char keyChar, final GUICommandList commands)
    {
        super(commands);
        this.keyChar = keyChar;
    }

    /**
     * Return the key character to match.
     *
     * @return The key character to match.
     */
    public int getKeyChar()
    {
        return keyChar;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object op)
    {
        if (op instanceof KeyCharKeyBinding)
        {
            final KeyCharKeyBinding keyBinding = (KeyCharKeyBinding)op;
            return keyBinding.getKeyChar() == keyChar;
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean matchesKeyCode(final int keyCode, final int modifiers)
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean matchesKeyChar(final char keyChar)
    {
        return this.keyChar == keyChar;
    }
}
