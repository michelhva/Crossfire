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
 * Model class maintaining the {@link CfItem}s known to the player.
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
     * Maps location to list of items.
     */
    @NotNull
    private final Map<Integer, List<CfItem>> items = new HashMap<Integer, List<CfItem>>();

    /**
     * The synchronization object for accessing {@link #allItems} and {@link
     * #items}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * Returns a list of items in a given location. The returned list may not be
     * modified by the caller.
     * @param location the location
     * @return the list of items
     */
    @NotNull
    public List<CfItem> getItems(final int location) {
        final List<CfItem> result;
        synchronized (sync) {
            result = items.get(location);
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns whether no items are known.
     * @return whether no items are known
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns the number of items in a given location. Undefined locations
     * return <code>0</code>.
     * @param location the location to check
     * @return the number of items
     */
    public int getNumberOfItems(final int location) {
        final Collection<CfItem> result;
        synchronized (sync) {
            result = items.get(location);
        }
        return result == null ? 0 : result.size();
    }

    /**
     * Removes a {@link CfItem}.
     * @param item the item to remove
     * @param abstractManager the abstract manager to notify about changes
     */
    public void removeItemFromLocation(@NotNull final CfItem item, @Nullable final AbstractManager abstractManager) {
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

        if (abstractManager != null) {
            abstractManager.addModified(index, list.size()+1);
        }
    }

    /**
     * Adds a {@link CfItem}.
     * @param item the item to add
     * @param abstractManager the abstract manager to notify about changes
     */
    public void addItem(@NotNull final CfItem item, @Nullable final AbstractManager abstractManager) {
        final int where = item.getLocation();
        List<CfItem> list = items.get(where);
        if (list == null) {
            list = new CopyOnWriteArrayList<CfItem>();
            if (items.put(where, list) != null) {
                throw new AssertionError();
            }
        }

        list.add(item);
        if (abstractManager != null) {
            abstractManager.addModified(list.size()-1);
        }
    }

    /**
     * Adds an inventory {@link CfItem}.
     * @param item the item to add
     * @param abstractManager the abstract manager to notify about changes
     */
    public void addInventoryItem(final CfItem item, @NotNull final AbstractManager abstractManager) {
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
        abstractManager.addModified(index, list.size());
    }

    /**
     * Marks an item as modified.
     * @param tag the item's tag
     * @param abstractManager the abstract manager to notify
     */
    public void addModified(final int tag, @NotNull final AbstractManager abstractManager) {
        abstractManager.addModified(items.get(tag));
    }

    /**
     * Returns the inventory of an item.
     * @param tag the item's tag
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    public List<CfItem> getInventory(final int tag) {
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
    public CfItem getItem(final int tag) {
        synchronized (sync) {
            return allItems.get(tag);
        }
    }

    /**
     * Removes an item by tag.
     * @param tag the item's tag
     * @return the removed item or <code>null</code>
     */
    @Nullable
    public CfItem removeTag(final int tag) {
        synchronized (sync) {
            return allItems.remove(tag);
        }
    }

    /**
     * Removes all items.
     * @return the removed items; may be modified by the caller
     */
    @NotNull
    public Iterable<CfItem> removeAll() {
        synchronized (sync) {
            return new HashSet<CfItem>(allItems.values());
        }
    }

    /**
     * Adds an item.
     * @param item the item
     */
    public void addItem(@NotNull final CfItem item) {
        synchronized (sync) {
            if (allItems.put(item.getTag(), item) != null) {
                throw new AssertionError("duplicate item "+item.getTag());
            }
        }
    }

}
