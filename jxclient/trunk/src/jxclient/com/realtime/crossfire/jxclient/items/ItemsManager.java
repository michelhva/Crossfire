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

import com.realtime.crossfire.jxclient.faces.FaceCache;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
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
     * The {@link FaceCache} instance for looking up faces.
     */
    private final FaceCache faceCache;

    /**
     * The {@link Stats} instance to update.
     */
    private final Stats stats;

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
     * The list of {@link PlayerListener}s to be notified about
     * changes of the current player.
     */
    private final EventListenerList playerListeners = new EventListenerList();

    /**
     * The current player object this client controls.
     */
    private CfPlayer player = null;

    /**
     * The {@link CrossfireUpdateItemListener} to receive item updates.
     */
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener()
    {
        /** {@inheritDoc} */
        public void delinvReceived(final int tag)
        {
            cleanInventory(tag);
        }

        /** {@inheritDoc} */
        public void delitemReceived(final int[] tags)
        {
            removeItems(tags);
        }

        /** {@inheritDoc} */
        public void additemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, final String name, final String namePl, final int anim, final int animSpeed, final int nrof, final int type)
        {
            addItem(new CfItem(location, tag, flags, weight, faceCache.getFace(faceNum), name, namePl, anim, animSpeed, nrof, type));
        }

        /** {@inheritDoc} */
        public void additemFinished()
        {
            fireEvents();
        }

        /** {@inheritDoc} */
        public void playerReceived(final int tag, final int weight, final int faceNum, final String name)
        {
            stats.resetSkills();
            SkillSet.clearNumberedSkills();
            setPlayer(new CfPlayer(tag, weight, faceCache.getFace(faceNum), name));
            stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, weight);
            stats.setStatsProcessed(false);
        }

        /** {@inheritDoc} */
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, final String valName, final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
        {
            updateItem(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
            if ((flags&CfItem.UPD_WEIGHT) != 0)
            {
                final CfPlayer player = getPlayer();
                if (player != null && player.getTag() == tag)
                {
                    stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, valWeight);
                    stats.setStatsProcessed(false);
                }
            }
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param faceCache the instance for looking up faces
     * @param stats the instance to update
     */
    public ItemsManager(final CrossfireServerConnection crossfireServerConnection, final FaceCache faceCache, final Stats stats)
    {
        this.faceCache = faceCache;
        this.stats = stats;
        crossfireServerConnection.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
    }

    /**
     * Reset the manager's state.
     */
    public synchronized void reset()
    {
        if (player != null)
        {
            cleanInventory(player.getTag());
        }
        cleanInventory(currentFloorManager.getCurrentFloor());
        final Set<CfItem> tmp = new HashSet<CfItem>(allItems.values());
        for (final CfItem item : tmp)
        {
            removeItem(item);
        }
        assert items.isEmpty();
        fireEvents();
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
    private void cleanInventory(final int tag)
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
    private void removeItems(final int[] tags)
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

        removeItemFromLocation(item);
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

        removeItemFromLocation(item);
    }

    /**
     * Add an item.
     *
     * @param item the item to add
     */
    private synchronized void addItem(final CfItem item)
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

        removeItemFromLocation(item);
        item.setLocation(newLocation);
        addItemToLocation(item);
    }

    /**
     * Remove an item from {@link #items}. The item must exist.
     *
     * @param item the item to remove
     */
    private void removeItemFromLocation(final CfItem item)
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
            final int index = InventoryManager.getInsertionIndex(list, item);
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
    public void fireEvents()
    {
        floorManager.fireEvents(getItems(currentFloorManager.getCurrentFloor()));
        final List<CfItem> newItems;
        synchronized (this)
        {
            newItems = player != null ? getItems(player.getTag()) : null;
        }
        if (newItems != null)
        {
            inventoryManager.fireEvents(newItems);
        }
    }

    /**
     * Set the player object this client controls.
     *
     * @param player the new player object
     */
    private synchronized void setPlayer(final CfPlayer player)
    {
        if (this.player == player)
        {
            if (this.player != null)
            {
                for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class))
                {
                    listener.playerReceived(this.player);
                }
            }
            return;
        }

        if (this.player != null)
        {
            inventoryManager.addModified(items.get(this.player.getTag()));
            for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class))
            {
                listener.playerRemoved(this.player);
            }
        }
        this.player = player;
        if (this.player != null)
        {
            inventoryManager.addModified(items.get(this.player.getTag()));
            for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class))
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
     * Add a {@link PlayerListener} to be notified about changes of
     * the current player.
     *
     * @param listener the listener to add
     */
    public void addCrossfirePlayerListener(final PlayerListener listener)
    {
        playerListeners.add(PlayerListener.class, listener);
    }

    /**
     * Remove a {@link PlayerListener} to be notified about changes of
     * the current player.
     *
     * @param listener the listener to remove
     */
    public void removeCrossfirePlayerListener(final PlayerListener listener)
    {
        playerListeners.remove(PlayerListener.class, listener);
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

    private void updateItem(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, final String valName, final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
    {
        final CfItem item = getItemOrPlayer(tag);
        if (item == null)
        {
            System.err.println("updateItem: undefined item "+tag);
            return;
        }

        final boolean wasOpen = (flags&CfItem.UPD_FLAGS) != 0 && currentFloorManager.getCurrentFloor() == item.getTag() && item.isOpen();
        item.update(flags, valFlags, valWeight, faceCache.getFace(valFaceNum), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&CfItem.UPD_LOCATION) != 0)
        {
            moveItem(item, valLocation);
        }
        if ((flags&CfItem.UPD_FLAGS) != 0)
        {
            if (item.isOpen())
            {
                currentFloorManager.setCurrentFloor(item.getTag());
            }
            else if (wasOpen)
            {
                currentFloorManager.setCurrentFloor(0);
            }
        }
    }
}
