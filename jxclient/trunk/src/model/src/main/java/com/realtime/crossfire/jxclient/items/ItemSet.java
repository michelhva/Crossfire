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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.protocol.UpdItem;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import com.realtime.crossfire.jxclient.util.HashedEventListenerList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Model class maintaining the {@link CfItem CfItems} known to the player.
 * @author Andreas Kirschbaum
 */
public class ItemSet {

    /**
     * Maps item tags to items. The map contains all items currently known to
     * the client.
     */
    @NotNull
    private final Map<Integer, CfItem> allItems = new HashMap<>();

    /**
     * Maps location (=tag) to list of items in that location.
     */
    @NotNull
    private final Map<Integer, List<CfItem>> items = new HashMap<>();

    /**
     * The synchronization object for {@link #allItems}, {@link #items}, {@link
     * #player}, or {@link #openContainerFloor}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The current player object this client controls.
     */
    @Nullable
    private CfItem player;

    /**
     * The currently opened container or {@code 0}.
     */
    private int openContainerFloor;

    /**
     * The list of {@link ItemSetListener ItemSetListeners} to be notified about
     * changes.
     */
    @NotNull
    private final EventListenerList2<ItemSetListener> itemSetListeners = new EventListenerList2<>();

    /**
     * The registered {@link ItemListener ItemListeners} to be notified about
     * changes.
     */
    @NotNull
    private final HashedEventListenerList<ItemListener> itemListeners = new HashedEventListenerList<>();

    /**
     * Adds an {@link ItemSetListener} to be notified about changes.
     * @param listener the listener to add
     */
    public void addItemSetListener(@NotNull final ItemSetListener listener) {
        itemSetListeners.add(listener);
    }

    /**
     * Removes an {@link ItemSetListener} to be notified about changes.
     * @param listener the listener to remove
     */
    public void removeItemSetListener(@NotNull final ItemSetListener listener) {
        itemSetListeners.remove(listener);
    }

    /**
     * Adds an {@link ItemListener} to be notified about changes.
     * @param tag the item tag to watch
     * @param listener the listener to add
     */
    public void addInventoryListener(final int tag, @NotNull final ItemListener listener) {
        itemListeners.add(tag, listener);
    }

    /**
     * Removes an {@link ItemListener} to be notified about changes.
     * @param tag the item tag to watch
     * @param listener the listener to add
     */
    public void removeInventoryListener(final int tag, @NotNull final ItemListener listener) {
        itemListeners.remove(tag, listener);
    }

    /**
     * Returns a list of items in a given location. The returned list may not be
     * modified by the caller.
     * @param location the location
     * @return the list of items
     */
    @NotNull
    public List<CfItem> getItemsByLocation(final int location) {
        synchronized (sync) {
            final List<CfItem> result = items.get(location);
            if (result == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(result);
        }
    }

    /**
     * Returns the number of items in a given location. Undefined locations
     * return {@code 0}.
     * @param location the location to check
     * @return the number of items
     */
    public int getNumberOfItemsByLocation(final int location) {
        synchronized (sync) {
            final Collection<CfItem> result = items.get(location);
            return result == null ? 0 : result.size();
        }
    }

    /**
     * Removes a {@link CfItem}.
     * @param tag the item's tag to remove
     * @return the index where the item has been removed from or {@code -1}
     */
    private int removeItemByTag(final int tag) {
        final int where;
        final int index;
        synchronized (sync) {
            final CfItem item = allItems.remove(tag);
            if (item == null) {
                return -1;
            }

            where = item.getLocation();
            final List<CfItem> list = items.get(where);
            if (list == null) {
                throw new AssertionError("cannot find item "+item.getTag());
            }

            index = list.indexOf(item);
            if (list.remove(index) == null) {
                throw new AssertionError("cannot find item "+item.getTag());
            }

            if (list.isEmpty() && items.remove(item.getLocation()) != list) {
                throw new AssertionError();
            }
        }

        for (ItemListener itemListener : itemListeners.getListeners(where)) {
            itemListener.inventoryRemoved(where, index);
        }

        for (ItemListener itemListener : itemListeners.getListeners(tag)) {
            itemListener.itemRemoved(tag);
        }

        return index;
    }

    /**
     * Deletes items by tag.
     * @param tags the tags to delete
     */
    public void removeItems(@NotNull final int[] tags) {
        for (int tag : tags) {
            if (removeItemByTag(tag) == -1) {
                System.err.println("removeItem3: item "+tag+" does not exist");
            }
        }
    }

    /**
     * Adds an item.
     * @param item the item to add
     */
    public void addItem(@NotNull final CfItem item) {
        removeItemByTag(item.getTag());
        final int index;
        final int where;
        synchronized (sync) {
            if (allItems.put(item.getTag(), item) != null) {
                throw new AssertionError("duplicate item "+item.getTag());
            }

            where = item.getLocation();
            List<CfItem> list = items.get(where);
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
                if (items.put(where, list) != null) {
                    throw new AssertionError();
                }
            }

            list.add(item);
            index = list.size()-1;
        }

        for (ItemListener itemListener : itemListeners.getListeners(where)) {
            itemListener.inventoryAdded(where, index, item);
        }
    }

    /**
     * Returns the inventory of an item.
     * @param tag the item's tag
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    private List<CfItem> getInventoryByTag(final int tag) {
        synchronized (sync) {
            final List<CfItem> inventory = items.get(tag);
            if (inventory == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(inventory);
        }
    }

    /**
     * Returns an item by tag.
     * @param tag the tag
     * @return the item or {@code null} if no such items exists
     */
    @Nullable
    public CfItem getItemByTag(final int tag) {
        synchronized (sync) {
            return allItems.get(tag);
        }
    }

    /**
     * Returns the player object this client controls.
     * @return the player object
     */
    @Nullable
    public CfItem getPlayer() {
        synchronized (sync) {
            return player;
        }
    }

    /**
     * Sets the player object this client controls.
     * @param player the new player object
     */
    public void setPlayer(@Nullable final CfItem player) {
        synchronized (sync) {
            if (this.player == player) {
                return;
            }

            this.player = player;
        }

        for (ItemSetListener listener : itemSetListeners) {
            listener.playerChanged(player);
        }
    }

    /**
     * Returns the player's inventory.
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    public Iterable<CfItem> getPlayerInventory() {
        synchronized (sync) {
            return player == null ? Collections.emptyList() : getInventoryByTag(player.getTag());
        }
    }

    /**
     * Returns an item by tag. This function may return the player object.
     * @param tag the tag
     * @return the item or {@code null} if no such item exists
     */
    @Nullable
    private CfItem getItemOrPlayer(final int tag) {
        synchronized (sync) {
            if (player != null && player.getTag() == tag) {
                return player;
            }

            return allItems.get(tag);
        }
    }

    /**
     * Clears the inventory of an item.
     * @param tag the item tag
     */
    public void cleanInventory(final int tag) {
        final List<CfItem> inventoryItems = getItemsByLocation(tag);
        final ListIterator<CfItem> it = inventoryItems.listIterator(inventoryItems.size());
        while (it.hasPrevious()) {
            final CfItem item = it.previous();
            removeItemByTag(item.getTag());
        }
    }

    /**
     * Processes an "upditem" command.
     * @param flags the changed values
     * @param tag the item's tag
     * @param valLocation the item's location
     * @param valFlags the item's flags
     * @param valWeight the item's weight
     * @param valFace the item's face
     * @param valName the item's singular name
     * @param valNamePl the item's plural name
     * @param valAnim the item's animation ID
     * @param valAnimSpeed the item's animation speed
     * @param valNrof the number of items
     */
    public void updateItem(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final Face valFace, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
        final CfItem item = getItemOrPlayer(tag);
        if (item == null) {
            //System.err.println("updateItem: undefined item "+tag); // XXX: this is a server bug
            return;
        }

        final boolean wasOpen;
        synchronized (sync) {
            wasOpen = (flags&UpdItem.UPD_FLAGS) != 0 && openContainerFloor == item.getTag() && item.isOpen();
        }
        item.update(flags, valFlags, valWeight, valFace, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&UpdItem.UPD_LOCATION) != 0 && item.getLocation() != valLocation) {
            removeItemByTag(item.getTag());
            item.setLocation(valLocation);
            addItem(item);
        }

        if ((flags&~UpdItem.UPD_LOCATION) != 0) {
            for (ItemListener itemListener : itemListeners.getListeners(tag)) {
                itemListener.itemChanged(tag);
            }
        }
        if ((flags&UpdItem.UPD_FLAGS) != 0) {
            if (item.isOpen()) {
                setOpenContainer(item.getTag());
            } else if (wasOpen) {
                setOpenContainer(0);
            }
        }
    }

    /**
     * Resets the manager's state.
     */
    public void reset() {
        final int playerTag;
        synchronized (sync) {
            playerTag = player == null ? -1 : player.getTag();
        }
        if (playerTag != -1) {
            cleanInventory(playerTag);
        }
        final Iterable<CfItem> tmp;
        synchronized (sync) {
            tmp = new HashSet<>(allItems.values());
        }
        for (CfItem item : tmp) {
            removeItemByTag(item.getTag());
        }
        setOpenContainer(0);
        setPlayer(null);
    }

    /**
     * Sets the currently opened container.
     * @param openContainerFloor the opened container's tag or {@code 0}
     */
    private void setOpenContainer(final int openContainerFloor) {
        synchronized (sync) {
            if (this.openContainerFloor == openContainerFloor) {
                return;
            }

            this.openContainerFloor = openContainerFloor;
        }

        for (ItemSetListener listener : itemSetListeners) {
            listener.openContainerChanged(openContainerFloor);
        }
    }

    /**
     * Returns the currently opened container.
     * @return the opened container's tag
     */
    public int getOpenContainer() {
        synchronized (sync) {
            return openContainerFloor;
        }
    }

    /**
     * Returns a {@link CfItem} from the inventory of an item.
     * @param tag the item's tag
     * @param index the index of the inventory item to return
     * @return the inventory item or {@code null} if the index does not exist
     */
    @Nullable
    public CfItem getInventoryItem(final int tag, final int index) {
        final List<CfItem> inventoryItems = getInventoryByTag(tag);
        try {
            return 0 <= index && index < inventoryItems.size() ? inventoryItems.get(index) : null;
        } catch (final ArrayIndexOutOfBoundsException ignored) {
            return null;
        }
    }

}
