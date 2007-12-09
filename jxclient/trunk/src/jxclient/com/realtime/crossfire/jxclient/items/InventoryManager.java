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

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.CfItem;
import com.realtime.crossfire.jxclient.CfPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;

/**
 * Manages the items in the player's inventory.
 *
 * @author Andreas Kirschbaum
 */
public class InventoryManager extends AbstractManager
{
    /**
     * Find the correct insertion position for an inventory object.
     *
     * @param list The inventory objects.
     *
     * @param item The item to add.
     *
     * @return The insertion index.
     */
    public int getInsertionIndex(final List<CfItem> list, final CfItem item)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (compareItem(list.get(i), item) >= 0)
            {
                return i;
            }
        }
        return list.size();
    }

    /**
     * Compare two items by inventory order.
     *
     * @param item1 The first item to compare.
     *
     * @param item2 The second item to compare.
     *
     * @return The comparision result: -1=<code>item1</code> before
     * </code>item2</code>, 0=<code>item1</code> == </code>item2</code>,
     * +1=<code>item1</code> after </code>item2</code>.
     */
    private int compareItem(final CfItem item1, final CfItem item2)
    {
        if (item1.getType() < item2.getType()) return -1;
        if (item1.getType() > item2.getType()) return +1;
        final int cmp1 = item1.getName().compareTo(item2.getName());
        if (cmp1 != 0) return cmp1;
        if (item1.getTag() < item2.getTag()) return -1;
        if (item1.getTag() > item2.getTag()) return +1;
        return 0;
    }
}
