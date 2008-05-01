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
package com.realtime.crossfire.jxclient.shortcuts;

import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.spells.Spell;

/**
 * A {@link Shortcut} that casts a spell.
 *
 * @author Andreas Kirschbaum
 */
public class ShortcutSpell extends Shortcut
{
    /**
     * Command prefix to "cast" a spell.
     */
    private static final String CAST = "cast ";

    /**
     * Command prefix to "invoke" a spell.
     */
    private static final String INVOKE = "invoke ";

    /**
     * The window to cast the spell in.
     */
    private final JXCWindow jxcWindow;

    /**
     * The spell to cast.
     */
    private final Spell spell;

    /**
     * The command for casting the spell.
     */
    private String command = CAST;

    /**
     * Create a new instance.
     *
     * @param jxcWindow The window to cast the spell in.
     *
     * @param spell The spell to cast.
     */
    public ShortcutSpell(final JXCWindow jxcWindow, final Spell spell)
    {
        this.jxcWindow = jxcWindow;
        this.spell = spell;
    }

    /**
     * Return the spell to cast.
     *
     * @return The spell.
     */
    public Spell getSpell()
    {
        return spell;
    }

    /**
     * Return whether the spell should be "cast" or "invoked".
     *
     * @return <code>true</code> for "cast", or <code>false</code> for
     * "invoke".
     */
    public boolean isCast()
    {
        return command == CAST;
    }

    /**
     * Set whether the spell should be "cast" or "invoked".
     *
     * @param cast <code>true</code> for "cast", or <code>false</code> for
     * "invoke".
     */
    public void setCast(final boolean cast)
    {
        final String newCommand = cast ? CAST : INVOKE;
        if (command == newCommand)
        {
            return;
        }

        command = newCommand;
        fireModifiedEvent();
    }

    /** {@inheritDoc} */
    @Override public void execute()
    {
        jxcWindow.getCommandQueue().sendNcom(false, command+spell.getInternalName());
    }
}
