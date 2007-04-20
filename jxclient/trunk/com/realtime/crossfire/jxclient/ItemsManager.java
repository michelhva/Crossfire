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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all known items.
 *
 * @author Andreas Kirschbaum
 */
public class ItemsManager
{
    /**
     * List of all known items.
     */
    private static List<CfItem> items = new ArrayList<CfItem>();

    /**
     * Maps item tags to items.
     */
    private static Map<Integer, CfItem> myitems  = new HashMap<Integer, CfItem>();

    /**
     * Return a list of items in a given location. The returned list may be
     * modified by the caller.
     *
     * @param location the location
     *
     * @return the list of items
     */
    public List<CfItem> getItems(final int location)
    {
        final List<CfItem> result = new ArrayList<CfItem>();
        for (final CfItem item : items)
        {
            if (item.getLocation() == location)
            {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Return an item by tag.
     *
     * @param tag the tag
     *
     * @return the item or <code>null</code> if no such items exists
     */
    public CfItem getItem(final int tag)
    {
        return myitems.get(tag);
    }

    /**
     * Delete an item by tag.
     *
     * @param tag the tag of the item to delete
     */
    public void removeItem(final int tag)
    {
        final CfItem item = myitems.remove(tag);
        if (item == null) {
            System.err.println("removeItem: item "+tag+" does not exist");
            return;
        }

        if (!items.remove(item)) {
            throw new AssertionError("cannot find item "+tag);
        }
    }

    /**
     * Delete an item.
     *
     * @param item the item to delete
     */
    public void removeItem(final CfItem item)
    {
        final CfItem deletedItem = myitems.remove(item.getTag());
        if (deletedItem == null) {
            throw new AssertionError("cannot find item "+item.getTag());
        }
        if (deletedItem != item) {
            throw new AssertionError("deleted wrong item "+item.getTag());
        }

        if (!items.remove(item)) {
            throw new AssertionError("cannot find item "+item.getTag());
        }
    }

    /**
     * Add an item.
     *
     * @param item the item to add
     */
    public void addItem(final CfItem item)
    {
        final CfItem oldItem = myitems.get(item.getTag());
        if (oldItem != null)
        {
            System.err.println("addItem: duplicate item "+item.getTag());
            removeItem(oldItem);
        }

        if (myitems.put(item.getTag(), item) != null) {
            throw new AssertionError("duplicate item "+item.getTag());
        }
        items.add(item);
    }
}
