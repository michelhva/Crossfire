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

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.server.ClientSocketState;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import com.realtime.crossfire.jxclient.window.GuiStateListener;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.event.EventListenerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages items known to the character. This includes items on the floor, in
 * the character's inventory, the character object itself, and items within
 * containers known to the character.
 * @author Andreas Kirschbaum
 */
public class ItemsManager
{
    /**
     * The {@link FacesManager} instance for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link Stats} instance to update.
     */
    @NotNull
    private final Stats stats;

    /**
     * The {@link SkillSet} instance to update.
     */
    @NotNull
    private final SkillSet skillSet;

    /**
     * Maps location to list of items.
     */
    @NotNull
    private final Map<Integer, List<CfItem>> items = new HashMap<Integer, List<CfItem>>();

    /**
     * Maps item tags to items. The map contains all items currently known to
     * the client.
     */
    @NotNull
    private final Map<Integer, CfItem> allItems  = new HashMap<Integer, CfItem>();

    /**
     * The current floor manager used to track the player's current floor
     * location.
     */
    @NotNull
    private final CurrentFloorManager currentFloorManager = new CurrentFloorManager(this);

    /**
     * The floor manager used to maintain floor object states.
     */
    @NotNull
    private final AbstractManager floorManager = new FloorManager();

    /**
     * The inventory manager used to maintain player inventory state.
     */
    @NotNull
    private final AbstractManager inventoryManager = new InventoryManager();

    /**
     * The list of {@link PlayerListener}s to be notified about
     * changes of the current player.
     */
    @NotNull
    private final EventListenerList playerListeners = new EventListenerList();

    /**
     * The synchronization object for XXX.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The current player object this client controls.
     */
    @Nullable
    private CfPlayer player = null;

    /**
     * The {@link CrossfireUpdateItemListener} to receive item updates.
     */
    @NotNull
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener()
    {
        /** {@inheritDoc} */
        @Override
        public void delinvReceived(final int tag)
        {
            cleanInventory(tag);
        }

        /** {@inheritDoc} */
        @Override
        public void delitemReceived(@NotNull final int[] tags)
        {
            removeItems(tags);
        }

        /** {@inheritDoc} */
        @Override
        public void additemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type)
        {
            addItem(new CfItem(location, tag, flags, weight, facesManager.getFace(faceNum), name, namePl, anim, animSpeed, nrof, type));
        }

        /** {@inheritDoc} */
        @Override
        public void additemFinished()
        {
            fireEvents();
        }

        /** {@inheritDoc} */
        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name)
        {
            stats.setActiveSkill("");
            skillSet.clearNumberedSkills();
            setPlayer(new CfPlayer(tag, weight, facesManager.getFace(faceNum), name));
            stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, weight);
        }

        /** {@inheritDoc} */
        @Override
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
        {
            updateItem(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
            if ((flags&CfItem.UPD_WEIGHT) != 0)
            {
                final CfItem player = getPlayer();
                if (player != null && player.getTag() == tag)
                {
                    stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, valWeight);
                }
            }
        }
    };

    /**
     * The {@link EventSchedulerCallback} for delaying event generation. This
     * is needed because the Crossfire server sends multiple item2 commands
     * for one "get all" command.
     */
    @NotNull
    private final Runnable fireEventCallback = new Runnable()
    {
        /** {@inheritDoc} */
        @Override
        public void run()
        {
            floorManager.fireEvents(getItems(currentFloorManager.getCurrentFloor()));
            @Nullable final List<CfItem> newItems;
            synchronized (sync)
            {
                newItems = player != null ? getItems(player.getTag()) : null;
            }
            if (newItems != null)
            {
                inventoryManager.fireEvents(newItems);
            }
        }
    };

    /**
     * The {@link EventScheduler} for delaying event generation.
     */
    @NotNull
    private final EventScheduler fireEventScheduler = new EventScheduler(100, 500, fireEventCallback);

    /**
     * The {@link GuiStateListener} for detecting established or dropped
     * connections.
     */
    @NotNull
    private final GuiStateListener guiStateListener = new GuiStateListener()
    {
        /** {@inheritDoc} */
        @Override
        public void start()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting()
        {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState)
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected()
        {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason)
        {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager for looking up faces
     * @param stats the instance to update
     * @param skillSet the skill set instance to update
     * @param window the window to attach to
     */
    public ItemsManager(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final Stats stats, @NotNull final SkillSet skillSet, @NotNull final JXCWindow window)
    {
        this.facesManager = facesManager;
        this.stats = stats;
        this.skillSet = skillSet;
        crossfireServerConnection.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
        window.addConnectionStateListener(guiStateListener);
        fireEventScheduler.start();
    }

    /**
     * Resets the manager's state.
     */
    private void reset()
    {
        synchronized (sync)
        {
            if (player != null)
            {
                cleanInventory(player.getTag());
            }
            cleanInventory(currentFloorManager.getCurrentFloor());
            final Iterable<CfItem> tmp = new HashSet<CfItem>(allItems.values());
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
    }

    /**
     * Returns a list of items in a given location. The returned list may not
     * be modified by the caller.
     * @param location the location
     * @return the list of items
     */
    @NotNull
    public List<CfItem> getItems(final int location)
    {
        final List<CfItem> result;
        synchronized (sync)
        {
            result = items.get(location);
        }
        if (result == null)
        {
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
    public int getNumberOfItems(final int location)
    {
        final Collection<CfItem> result;
        synchronized (sync)
        {
            result = items.get(location);
        }
        return result == null ? 0 : result.size();
    }

    /**
     * Returns an item by tag. This function may return the player object.
     * @param tag The tag.
     * @return the item or <code>null</code> if no such item exists
     */
    @Nullable
    private CfItem getItemOrPlayer(final int tag)
    {
        synchronized (sync)
        {
            if (player != null && player.getTag() == tag)
            {
                return player;
            }

            return getItem(tag);
        }
    }

    /**
     * Returns an item by tag.
     * @param tag the tag
     * @return the item or <code>null</code> if no such items exists
     */
    @Nullable
    public CfItem getItem(final int tag)
    {
        synchronized (sync)
        {
            return allItems.get(tag);
        }
    }

    /**
     * Clears the inventory of an item.
     * @param tag the item tag
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
     * Deletes items by tag.
     * @param tags the tags to delete
     */
    private void removeItems(@NotNull final int[] tags)
    {
        for (final int tag : tags)
        {
            removeItem(tag);
        }
        fireEvents();
    }

    /**
     * Deletes an item by tag.
     * @param tag the tag of the item to delete
     */
    private void removeItem(final int tag)
    {
        synchronized (sync)
        {
            final CfItem item = allItems.remove(tag);
            if (item != null)
            {
                removeItemFromLocation(item);
                return;
            }
        }
        System.err.println("removeItem: item "+tag+" does not exist");
    }

    /**
     * Deletes an item.
     * @param item the item to delete
     */
    private void removeItem(@NotNull final CfItem item)
    {
        synchronized (sync)
        {
            final Object deletedItem = allItems.remove(item.getTag());
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
    }

    /**
     * Adds an item.
     * @param item the item to add
     */
    private void addItem(@NotNull final CfItem item)
    {
        synchronized (sync)
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
    }

    /**
     * Moves an item to a new location.
     * @param item the item to move
     * @param newLocation the location to move to
     */
    private void moveItem(@NotNull final CfItem item, final int newLocation)
    {
        synchronized (sync)
        {
            if (allItems.get(item.getTag()) != item)
            {
                throw new AssertionError("invalid item "+item.getTag());
            }

            removeItemFromLocation(item);
            item.setLocation(newLocation);
            addItemToLocation(item);
        }
    }

    /**
     * Removes an item from {@link #items}. The item must exist.
     * @param item the item to remove
     */
    private void removeItemFromLocation(@NotNull final CfItem item)
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
     * Adds an item to {@link #items}.
     * @param item the item to add
     */
    private void addItemToLocation(@NotNull final CfItem item)
    {
        final int where = item.getLocation();

        List<CfItem> list = items.get(where);
        if (list == null)
        {
            list = new CopyOnWriteArrayList<CfItem>();
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
     * Delivers outstanding change events.
     */
    private void fireEvents()
    {
        fireEventScheduler.trigger();
    }

    /**
     * Sets the player object this client controls.
     * @param player the new player object
     */
    private void setPlayer(@Nullable final CfPlayer player)
    {
        synchronized (sync)
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
    }

    /**
     * Returns the player object this client controls.
     * @return the player object
     */
    @Nullable
    public CfItem getPlayer()
    {
        synchronized (sync)
        {
            return player;
        }
    }

    /**
     * Adds a {@link PlayerListener} to be notified about changes of the
     * current player.
     * @param listener the listener to add
     */
    public void addCrossfirePlayerListener(@NotNull final PlayerListener listener)
    {
        playerListeners.add(PlayerListener.class, listener);
    }

    /**
     * Removes a {@link PlayerListener} to be notified about changes of the
     * current player.
     * @param listener the listener to remove
     */
    public void removeCrossfirePlayerListener(@NotNull final PlayerListener listener)
    {
        playerListeners.remove(PlayerListener.class, listener);
    }

    /**
     * Returns the current floor manager.
     * @return the current floor manager
     */
    @NotNull
    public CurrentFloorManager getCurrentFloorManager()
    {
        return currentFloorManager;
    }

    /**
     * Returns the floor manager.
     * @return the floor manager
     */
    @NotNull
    public AbstractManager getFloorManager()
    {
        return floorManager;
    }

    /**
     * Returns the inventory manager.
     * @return the inventory manager
     */
    @NotNull
    public AbstractManager getInventoryManager()
    {
        return inventoryManager;
    }

    /**
     * Processes an "upditem" command.
     * @param flags the changed values
     * @param tag the item's tag
     * @param valLocation the item's location
     * @param valFlags the item's flags
     * @param valWeight the item's weight
     * @param valFaceNum the item's face ID
     * @param valName the item's singular name
     * @param valNamePl the item's plural name
     * @param valAnim the item's animation ID
     * @param valAnimSpeed the item's animation speed
     * @param valNrof the number of items
     */
    private void updateItem(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof)
    {
        final CfItem item = getItemOrPlayer(tag);
        if (item == null)
        {
            if (flags != CfItem.UPD_FACE) // XXX: suppress frequent error message due to server bug
            {
                System.err.println("updateItem: undefined item "+tag);
            }
            return;
        }

        final boolean wasOpen = (flags&CfItem.UPD_FLAGS) != 0 && currentFloorManager.getCurrentFloor() == item.getTag() && item.isOpen();
        item.update(flags, valFlags, valWeight, facesManager.getFace(valFaceNum), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
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

    /**
     * Returns the player's inventory.
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    public List<CfItem> getInventory()
    {
        if (player == null)
        {
            return Collections.emptyList();
        }
        final List<CfItem> inventory = items.get(player.getTag());
        if (inventory == null)
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(inventory);
    }
}
