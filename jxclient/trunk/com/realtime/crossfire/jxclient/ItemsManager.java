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
import java.util.Collections;
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
     * Maps location to list of items.
     */
    private static Map<Integer, ArrayList<CfItem>> items = new HashMap<Integer, ArrayList<CfItem>>();

    /**
     * Maps item tags to items.
     */
    private static Map<Integer, CfItem> myitems  = new HashMap<Integer, CfItem>();

    /**
     * Return a list of items in a given location. The returned list may not be
     * modified by the caller.
     *
     * @param location the location
     *
     * @return the list of items
     */
    public List<CfItem> getItems(final int location)
    {
        final List<CfItem> result = items.get(location);
        if (result == null)
        {
            return Collections.<CfItem>emptyList();
        }

        return new ArrayList<CfItem>(result);
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

        removeItemFromLocation(item);
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

        removeItemFromLocation(item);
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

        addItemToLocation(item);
    }

    /**
     * Remove an item from {@link #items}. The item must exist.
     *
     * @param item the item to remove
     */
    private void removeItemFromLocation(final CfItem item)
    {
        final List<CfItem> list = items.get(item.getLocation());
        if (list == null)
        {
            throw new AssertionError("cannot find item "+item.getTag());
        }

        if (!list.remove(item))
        {
            throw new AssertionError("cannot find item "+item.getTag());
        }

        if (list.isEmpty())
        {
            if (items.remove(item.getLocation()) != list)
            {
                throw new AssertionError();
            }
        }
    }

    /**
     * Add an item to {@link #items}.
     *
     * @param item the item to add
     */
    private void addItemToLocation(final CfItem item)
    {
        final List<CfItem> list = items.get(item.getLocation());
        if (list != null)
        {
            list.add(item);
            return;
        }

        final ArrayList<CfItem> newList = new ArrayList<CfItem>();
        if (items.put(item.getLocation(), newList) != null)
        {
            throw new AssertionError();
        }
        newList.add(item);
    }
}
