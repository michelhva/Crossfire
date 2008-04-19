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
    private final Map<Integer, CfItem> allItems  = new HashMap<Integer, CfItem>();

    /**
     * The current floor manager used to track the player's current floor
     * location.
     */
    private final CurrentFloorManager currentFloorManager = new CurrentFloorManager(this);

    /**
     * The floor manager used to maintain floor object states.
     */
    private final FloorManager floorManager = new FloorManager();

    /**
     * The inventory manager used to maintain player inventory state.
     */
    private final InventoryManager inventoryManager = new InventoryManager();

    /**
     * The list of {@link CrossfirePlayerListener}s to be notified about
     * changes of the current player.
     */
    private final EventListenerList playerListeners = new EventListenerList();

    /**
     * The current player object this client controls.
     */
    private CfPlayer player = null;

    /**
     * Reset the manager's state.
     */
    public synchronized void reset()
    {
        final Set<CfItem> tmp = new HashSet<CfItem>(allItems.values());
        for (final CfItem item : tmp)
        {
            removeItem(item);
        }
        assert items.size() == 0;
        currentFloorManager.setCurrentFloor(0);
        floorManager.reset();
        inventoryManager.reset();
        setPlayer(null);
    }

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
     * Return the number of items in a given location. Undefined locations
     * return <code>0</code>.
     *
     * @param location The location to check.
     *
     * @return The number of items.
     */
    public synchronized int getNumberOfItems(final int location)
    {
        final List<CfItem> result = items.get(location);
        return result == null ? 0 : result.size();
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
        return allItems.get(tag);
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
        final CfItem item = allItems.remove(tag);
        if (item == null)
        {
            System.err.println("removeItem: item "+tag+" does not exist");
            return;
        }

        removeItemFromLocation(item, true);
    }

    /**
     * Delete an item.
     *
     * @param item the item to delete
     */
    public synchronized void removeItem(final CfItem item)
    {
        final CfItem deletedItem = allItems.remove(item.getTag());
        if (deletedItem == null)
        {
            throw new AssertionError("cannot find item "+item.getTag());
        }
        if (deletedItem != item)
        {
            throw new AssertionError("deleted wrong item "+item.getTag());
        }

        removeItemFromLocation(item, true);
    }

    /**
     * Add an item.
     *
     * @param item the item to add
     */
    public synchronized void addItem(final CfItem item)
    {
        final CfItem oldItem = allItems.get(item.getTag());
        if (oldItem != null)
        {
            System.err.println("addItem: duplicate item "+item.getTag());
            removeItem(oldItem);
        }

        if (allItems.put(item.getTag(), item) != null)
        {
            throw new AssertionError("duplicate item "+item.getTag());
        }

        addItemToLocation(item);
    }

    /**
     * Move an item to a new location.
     *
     * @param item The item to move.
     *
     * @param newLocation The location to move to.
     */
    public synchronized void moveItem(final CfItem item, final int newLocation)
    {
        if (allItems.get(item.getTag()) != item)
        {
            throw new AssertionError("invalid item "+item.getTag());
        }

        removeItemFromLocation(item, false);
        item.setLocation(newLocation);
        addItemToLocation(item);
    }

    /**
     * Remove an item from {@link #items}. The item must exist.
     *
     * @param item the item to remove
     *
     * @param removeItem whether the item is removed (<code>true</code>) or
     * moved to a new location (<code>false</code>)
     */
    private void removeItemFromLocation(final CfItem item, final boolean removeItem)
    {
        if (currentFloorManager.isCurrentFloor(item.getTag()))
        {
            currentFloorManager.setCurrentFloor(0);
        }

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

        if (currentFloorManager.isCurrentFloor(where))
        {
            floorManager.addModified(index, list.size()+1);
        }
        else if (player != null && where == player.getTag())
        {
            inventoryManager.addModified(index, list.size()+1);
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

        if (currentFloorManager.isCurrentFloor(where))
        {
            list.add(item);
            floorManager.addModified(list.size()-1);
        }
        else if (player != null && where == player.getTag())
        {
            // inventory order differs from server order, so insert at correct
            // position
            final int index = inventoryManager.getInsertionIndex(list, item);
            list.add(index, item);
            inventoryManager.addModified(index, list.size());
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
        floorManager.fireEvents(getItems(currentFloorManager.getCurrentFloor()));
        if (player != null)
        {
            inventoryManager.fireEvents(getItems(player.getTag()));
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
            if (this.player != null)
            {
                for (final CrossfirePlayerListener listener : playerListeners.getListeners(CrossfirePlayerListener.class))
                {
                    listener.playerReceived(this.player);
                }
            }
            return;
        }

        if (this.player != null)
        {
            inventoryManager.addModified(items.get(this.player.getTag()));
            for (final CrossfirePlayerListener listener : playerListeners.getListeners(CrossfirePlayerListener.class))
            {
                listener.playerRemoved(this.player);
            }
        }
        this.player = player;
        if (this.player != null)
        {
            inventoryManager.addModified(items.get(this.player.getTag()));
            for (final CrossfirePlayerListener listener : playerListeners.getListeners(CrossfirePlayerListener.class))
            {
                listener.playerAdded(this.player);
                listener.playerReceived(this.player);
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
     * Return the current floor manager.
     *
     * @return The current floor manager.
     */
    public CurrentFloorManager getCurrentFloorManager()
    {
        return currentFloorManager;
    }

    /**
     * Return the floor manager.
     *
     * @return The floor manager.
     */
    public FloorManager getFloorManager()
    {
        return floorManager;
    }

    /**
     * Return the inventory manager.
     *
     * @return The inventory manager.
     */
    public InventoryManager getInventoryManager()
    {
        return inventoryManager;
    }
}
