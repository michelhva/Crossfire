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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;

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
    private final Map<Integer, ArrayList<CfItem>> items = new HashMap<Integer, ArrayList<CfItem>>();

    /**
     * Maps item tags to items. The map contains all items currently known to
     * the client.
     */
    private final Map<Integer, CfItem> myitems  = new HashMap<Integer, CfItem>();

    /**
     * Maps floor index to list of {@link LocationListener}s to be notified
     * about added or removed items in a floor tile.
     */
    private final Map<Integer, EventListenerList> floorListeners = new HashMap<Integer, EventListenerList>();

    /**
     * Maps floor index to list of {@link LocationListener}s to be notified
     * about added or removed items in an inventory slot.
     */
    private final Map<Integer, EventListenerList> inventoryListeners = new HashMap<Integer, EventListenerList>();

    /**
     * The list of {@link CurrentFloorListener}s to be notified about changes
     * of the current floor location.
     */
    private final EventListenerList currentFloorListeners = new EventListenerList();

    /**
     * The list of {@link CrossfirePlayerListener}s to be notified about
     * changes of the current player.
     */
    private final EventListenerList playerListeners = new EventListenerList();

    /**
     * Modified floor tiles for which no events have been delivered.
     */
    private final Set<Integer> modifiedFloors = new HashSet<Integer>();

    /**
     * Modified inventory tiles for which no events have been delivered.
     */
    private final Set<Integer> modifiedInventories = new HashSet<Integer>();

    /**
     * The current player object this client controls.
     */
    private CfPlayer player = null;

    /**
     * The stats information.
     */
    private final Stats stats = new Stats();

    /**
     * The location to show in the floor view.
     */
    private int currentFloor = 0;

    /**
     * Return a list of items in a given location. The returned list may not be
     * modified by the caller.
     *
     * @param location the location
     *
     * @return the list of items
     */
    public synchronized List<CfItem> getItems(final int location)
    {
        final List<CfItem> result = items.get(location);
        if (result == null)
        {
            return Collections.emptyList();
        }

        return new ArrayList<CfItem>(result);
    }

    /**
     * Return an item by tag. This function may return the player object.
     *
     * @param tag The tag.
     *
     * @return The item or <code>null</code> if no such item exists.
     */
    public synchronized CfItem getItemOrPlayer(final int tag)
    {
        if (player != null && player.getTag() == tag)
        {
            return player;
        }

        return getItem(tag);
    }

    /**
     * Return an item by tag.
     *
     * @param tag the tag
     *
     * @return the item or <code>null</code> if no such items exists
     */
    public synchronized CfItem getItem(final int tag)
    {
        return myitems.get(tag);
    }

    /**
     * Clear the inventory of an item.
     *
     * @param tag The item tag.
     */
    public void cleanInventory(final int tag)
    {
        for (final CfItem item : getItems(tag))
        {
            removeItem(item);
        }
        fireEvents();
    }

    /**
     * Delete items by tag.
     *
     * @param tags The tags to delete.
     */
    public void removeItems(final int[] tags)
    {
        for (final int tag : tags)
        {
            removeItem(tag);
        }
        fireEvents();
    }

    /**
     * Delete an item by tag.
     *
     * @param tag the tag of the item to delete
     */
    public synchronized void removeItem(final int tag)
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
    public synchronized void removeItem(final CfItem item)
    {
        // safeguard against broken servers
        if (item.getTag() == currentFloor)
        {
            setCurrentFloor(0);
        }

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
    public synchronized void addItem(final CfItem item)
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
        final int where = item.getLocation();

        final List<CfItem> list = items.get(where);
        if (list == null)
        {
            throw new AssertionError("cannot find item "+item.getTag());
        }

        final int index = list.indexOf(item);
        if (list.remove(index) == null)
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

        if (where == currentFloor)
        {
            addModified(modifiedFloors, index, list.size()+1);
        }
        else if (player != null && where == player.getTag())
        {
            addModified(modifiedInventories, index, list.size()+1);
        }
    }

    /**
     * Add an item to {@link #items}.
     *
     * @param item the item to add
     */
    private void addItemToLocation(final CfItem item)
    {
        final int where = item.getLocation();

        ArrayList<CfItem> list = items.get(where);
        if (list == null)
        {
            list = new ArrayList<CfItem>();
            if (items.put(where, list) != null)
            {
                throw new AssertionError();
            }
        }

        if (where == currentFloor)
        {
            list.add(item);
            modifiedFloors.add(list.size()-1);
        }
        else if (player != null && where == player.getTag())
        {
            // inventory order differs from server order, so insert at correct
            // position
            final int index = getInsertionIndex(list, item);
            list.add(index, item);
            for (int i = index; i < list.size(); i++)
            {
                modifiedInventories.add(i);
            }
        }
        else
        {
            list.add(item);
        }
    }

    /**
     * Deliver outstanding change events.
     */
    public synchronized void fireEvents()
    {
        fireEvents(modifiedFloors, floorListeners, currentFloor);
        if (player != null)
        {
            fireEvents(modifiedInventories, inventoryListeners, player.getTag());
        }
    }

    /**
     * Set the player object this client controls.
     *
     * @param player the new player object
     */
    public synchronized void setPlayer(final CfPlayer player)
    {
        if (this.player == player)
        {
            return;
        }

        if (this.player != null)
        {
            addModified(modifiedInventories, items.get(this.player.getTag()));
            for (final CrossfirePlayerListener listener : playerListeners.getListeners(CrossfirePlayerListener.class))
            {
                listener.playerRemoved(this.player);
            }
        }
        this.player = player;
        if (this.player != null)
        {
            addModified(modifiedInventories, items.get(this.player.getTag()));
            for (final CrossfirePlayerListener listener : playerListeners.getListeners(CrossfirePlayerListener.class))
            {
                listener.playerAdded(this.player);
            }
        }
    }

    /**
     * Return the player object this client controls.
     *
     * @return the player object
     */
    public synchronized CfPlayer getPlayer()
    {
        return player;
    }

    /**
     * Return the stats object for this client.
     *
     * @return The stats object.
     */
    public Stats getStats()
    {
        return stats;
    }

    /**
     * Set the location to show in the floor view.
     *
     * @param currentFloor the new location of the floor view
     */
    public synchronized void setCurrentFloor(final int currentFloor)
    {
        if (this.currentFloor == currentFloor)
        {
            return;
        }

        addModified(modifiedFloors, items.get(this.currentFloor));
        this.currentFloor = currentFloor;
        addModified(modifiedFloors, items.get(this.currentFloor));

        for (final CurrentFloorListener listener : currentFloorListeners.getListeners(CurrentFloorListener.class)) {
            listener.currentFloorChanged(this.currentFloor);
        }
    }

    /**
     * Return the location to show in the floor view.
     *
     * @return the floor location
     */
    public synchronized int getCurrentFloor()
    {
        return currentFloor;
    }

    /**
     * Add pending modified events.
     *
     * @param modified the set of pending modified events
     *
     * @param items the pending events to add; may be <code>null</code>
     */
    private void addModified(final Set<Integer> modified, final List<CfItem> items)
    {
        if (items != null)
        {
            addModified(modified, 0, items.size());
        }
    }

    /**
     * Add pending modified events.
     *
     * @param modified the set of pending modified events
     *
     * @param start the first index to add
     *
     * @param end the first index not to add
     */
    private void addModified(final Set<Integer> modified, final int start, final int end)
    {
        for (int i = start; i < end; i++)
        {
            modified.add(i);
        }
    }

    /**
     * Deliver pending modified events.
     *
     * @param modified the set of pending events
     *
     * @param listeners the listeners to be modified
     *
     * @param location the location that has changed
     */
    private void fireEvents(final Set<Integer> modified, final Map<Integer, EventListenerList> listeners, final int location)
    {
        for (final int index : modified)
        {
            final EventListenerList tileListeners = listeners.get(index);
            if (tileListeners != null)
            {
                final List<CfItem> items = getItems(location);
                final CfItem item = 0 <= index && index < items.size() ? items.get(index) : null;
                for (final LocationListener listener : tileListeners.getListeners(LocationListener.class)) {
                    listener.locationModified(index, item);
                }
            }
        }
        modified.clear();
    }

    /**
     * Add a {@link LocationListener}s to be notified about changes in a floor
     * tile.
     *
     * @param index the floor tile
     *
     * @param listener the listener
     */
    public void addFloorLocationListener(final int index, final LocationListener listener)
    {
        EventListenerList listeners = floorListeners.get(index);
        if (listeners == null)
        {
            listeners = new EventListenerList();
            floorListeners.put(index, listeners);
        }

        listeners.add(LocationListener.class, listener);
    }

    /**
     * Remove a {@link LocationListener}s to be notified about changes in a
     * floor tile.
     *
     * @param index the floor tile
     *
     * @param listener the listener
     */
    public void removeFloorLocationListener(final int index, final LocationListener listener)
    {
        EventListenerList listeners = floorListeners.get(index);
        assert listeners != null;
        listeners.remove(LocationListener.class, listener);
        if (listeners.getListenerCount() <= 0)
        {
            floorListeners.remove(index);
        }
    }

    /**
     * Add a {@link LocationListener}s to be notified about changes in an
     * inventory slot.
     *
     * @param index the inventory slot
     *
     * @param listener the listener
     */
    public void addInventoryLocationListener(final int index, final LocationListener listener)
    {
        EventListenerList listeners = inventoryListeners.get(index);
        if (listeners == null)
        {
            listeners = new EventListenerList();
            inventoryListeners.put(index, listeners);
        }

        listeners.add(LocationListener.class, listener);
    }

    /**
     * Remove a {@link LocationListener}s to be notified about changes in an
     * inventory slot.
     *
     * @param index the inventory slot
     *
     * @param listener the listener
     */
    public void removeInventoryLocationListener(final int index, final LocationListener listener)
    {
        EventListenerList listeners = inventoryListeners.get(index);
        assert listeners != null;
        listeners.remove(LocationListener.class, listener);
        if (listeners.getListenerCount() <= 0)
        {
            inventoryListeners.remove(index);
        }
    }

    /**
     * Add a {@link CurrentFloorListener} to be notified about current floor
     * changes.
     *
     * @param listener the listener to add
     */
    public void addCurrentFloorListener(final CurrentFloorListener listener)
    {
        currentFloorListeners.add(CurrentFloorListener.class, listener);
    }

    /**
     * Remove a {@link CurrentFloorListener} to be notified about current floor
     * changes.
     *
     * @param listener the listener to remove
     */
    public void removeCurrentFloorListener(final CurrentFloorListener listener)
    {
        currentFloorListeners.remove(CurrentFloorListener.class, listener);
    }

    /**
     * Add a {@link CrossfirePlayerListener} to be notified about changes of
     * the current player.
     *
     * @param listener the listener to add
     */
    public void addCrossfirePlayerListener(final CrossfirePlayerListener listener)
    {
        playerListeners.add(CrossfirePlayerListener.class, listener);
    }

    /**
     * Remove a {@link CrossfirePlayerListener} to be notified about changes of
     * the current player.
     *
     * @param listener the listener to remove
     */
    public void removeCrossfirePlayerListener(final CrossfirePlayerListener listener)
    {
        playerListeners.remove(CrossfirePlayerListener.class, listener);
    }

    /**
     * Find the correct insertion position for an inventory object.
     *
     * @param list The inventory objects.
     *
     * @param item The item to add.
     *
     * @return The insertion index.
     */
    private int getInsertionIndex(final List<CfItem> list, final CfItem item)
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
