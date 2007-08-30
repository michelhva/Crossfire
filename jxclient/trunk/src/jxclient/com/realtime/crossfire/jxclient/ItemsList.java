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

import com.realtime.crossfire.jxclient.faces.Faces;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class ItemsList
{
    private static final ItemsManager itemsManager = new ItemsManager();

    private static final SpellsManager spellsManager = new SpellsManager();

    public static void updateItem(final int flags, final int tag, final int valFlags, final int valWeight, final int valFace, final String valName, final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
    {
        final CfItem item = itemsManager.getItemOrPlayer(tag);
        if (item == null)
        {
            System.err.println("updateItem: undefined item "+tag);
            return;
        }

        final boolean wasopen = (flags&CfItem.UPD_FLAGS) != 0 && itemsManager.getCurrentFloor() == item.getTag() && item.isOpen();
        item.update(flags, tag, valFlags, valWeight, Faces.getFace(valFace), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&CfItem.UPD_FLAGS) != 0)
        {
            if (item.isOpen())
            {
                itemsManager.setCurrentFloor(item.getTag());
            }
            else if (wasopen)
            {
                itemsManager.setCurrentFloor(0);
            }
        }
    }

    public static ItemsManager getItemsManager()
    {
        return itemsManager;
    }

    public static SpellsManager getSpellsManager()
    {
        return spellsManager;
    }
}
