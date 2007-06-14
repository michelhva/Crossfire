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
package com.realtime.crossfire.jxclient;

/**
 * A {@link KeyBinding} that matches by key code/modifiers pair.
 *
 * @author Andreas Kirschbaum
 */
public final class KeyCodeKeyBinding extends KeyBinding
{
    /**
     * The key code to match.
     */
    private final int keyCode;

    /**
     * The modifiers to match.
     */
    private final int modifiers;

    /**
     * Create a {@link KeyBinding} that matches by key code/modifiers pair.
     *
     * @param keyCode The key code to match.
     *
     * @param modifiers The modifiers to match.
     *
     * @param commands The commands to associate with this binding.
     */
    public KeyCodeKeyBinding(final int keyCode, final int modifiers, final GUICommandList commands)
    {
        super(commands);
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }

    /**
     * Return the key code to match.
     *
     * @return The key code to match.
     */
    public int getKeyCode()
    {
        return keyCode;
    }

    /**
     * The modifiers to match.
     *
     * @return The modifiers to match.
     */
    public int getModifiers()
    {
        return modifiers;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object op)
    {
        if (op instanceof KeyCodeKeyBinding)
        {
            final KeyCodeKeyBinding keyBinding = (KeyCodeKeyBinding)op;
            return keyBinding.getKeyCode() == keyCode && keyBinding.getModifiers() == modifiers;
        }
        else
        {
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean matchesKeyCode(final int keyCode, final int modifiers)
    {
        return this.keyCode == keyCode && this.modifiers == modifiers;
    }

    /** {@inheritDoc} */
    public boolean matchesKeyChar(final char keyChar)
    {
        return false;
    }
}
