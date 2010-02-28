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

package com.realtime.crossfire.jxclient.items;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Model class maintaining the {@link CfItem}s known to the player. Access is
 * not synchronized.
 * @author Andreas Kirschbaum
 */
public class ItemSet {

    /**
     * Maps item tags to items. The map contains all items currently known to
     * the client.
     */
    @NotNull
    private final Map<Integer, CfItem> allItems = new HashMap<Integer, CfItem>();

    /**
     * Maps location (=tag) to list of items in that location.
     */
    @NotNull
    private final Map<Integer, List<CfItem>> items = new HashMap<Integer, List<CfItem>>();

    /**
     * Returns a list of items in a given location. The returned list may not
     * be modified by the caller.
     * @param location the location
     * @return the list of items
     */
    @NotNull
    public List<CfItem> getItemsByLocation(final int location) {
        final List<CfItem> result = items.get(location);
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the number of items in a given location. Undefined locations
     * return <code>0</code>.
     * @param location the location to check
     * @return the number of items
     */
    public int getNumberOfItemsByLocation(final int location) {
        final Collection<CfItem> result = items.get(location);
        return result == null ? 0 : result.size();
    }

    /**
     * Adds an item.
     * @param item the item
     */
    public void addItem(@NotNull final CfItem item) {
        if (allItems.put(item.getTag(), item) != null) {
            throw new AssertionError("duplicate item "+item.getTag());
        }
    }

    /**
     * Removes a {@link CfItem}.
     * @param item the item to remove
     * @return the index where the item has been inserted
     */
    public int removeItem(@NotNull final CfItem item) {
        final int where = item.getLocation();
        final List<CfItem> list = items.get(where);
        if (list == null) {
            throw new AssertionError("cannot find item "+item.getTag());
        }

        final int index = list.indexOf(item);
        if (list.remove(index) == null) {
            throw new AssertionError("cannot find item "+item.getTag());
        }

        if (list.isEmpty()) {
            if (items.remove(item.getLocation()) != list) {
                throw new AssertionError();
            }
        }

        return index;
    }

    /**
     * Adds a {@link CfItem}.
     * @param item the item to add
     * @return the index where the item has been inserted
     */
    public int addItem2(@NotNull final CfItem item) {
        final int where = item.getLocation();
        List<CfItem> list = items.get(where);
        if (list == null) {
            list = new CopyOnWriteArrayList<CfItem>();
            if (items.put(where, list) != null) {
                throw new AssertionError();
            }
        }

        list.add(item);
        return list.size()-1;
    }

    /**
     * Adds an inventory {@link CfItem}.
     * @param item the item to add
     * @return the index where the item has been inserted
     */
    public int addInventoryItem(final CfItem item) {
        final int where = item.getLocation();
        List<CfItem> list = items.get(where);
        if (list == null) {
            list = new CopyOnWriteArrayList<CfItem>();
            if (items.put(where, list) != null) {
                throw new AssertionError();
            }
        }

        // inventory order differs from server order, so insert at correct
        // position
        final int index = InventoryManager.getInsertionIndex(list, item);
        list.add(index, item);
        return index;
    }

    /**
     * Returns the inventory of an item.
     * @param tag the item's tag
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    public List<CfItem> getInventoryByTag(final int tag) {
        final List<CfItem> inventory = items.get(tag);
        if (inventory == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(inventory);
    }

    /**
     * Returns an item by tag.
     * @param tag the tag
     * @return the item or <code>null</code> if no such items exists
     */
    @Nullable
    public CfItem getItemByTag(final int tag) {
        return allItems.get(tag);
    }

    /**
     * Removes an item by tag.
     * @param tag the item's tag
     * @return the removed item or <code>null</code>
     */
    @Nullable
    public CfItem removeItemByTag(final int tag) {
        return allItems.remove(tag);
    }

    /**
     * Removes all items.
     * @return the removed items; may be modified by the caller
     */
    @NotNull
    public Iterable<CfItem> removeAllItems() {
        return new HashSet<CfItem>(allItems.values());
    }

}
