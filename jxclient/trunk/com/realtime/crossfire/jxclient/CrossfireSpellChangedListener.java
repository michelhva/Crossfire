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

public interface CrossfireSpellChangedListener
{
    /**
     * A new spell was added.
     *
     * @param The added spell.
     *
     * @param The current index of <code>spell</code> in the spells manager.
     */
    void spellAdded(Spell spell, int index);

    /**
     * A spell was removed.
     *
     * @param The removed spell.
     *
     * @param The former index of <code>spell</code> in the spells manager.
     */
    void spellRemoved(Spell spell, int index);

    /**
     * A spell was modified.
     *
     * @param The modified spell.
     *
     * @param The current index of <code>spell</code> in the spells manager.
     */
    void spellModified(Spell spell, int index);
}
