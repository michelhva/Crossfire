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

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.guistate.GuiStateListener;
import com.realtime.crossfire.jxclient.guistate.GuiStateManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireStatsListener;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireUpdateItemListener;
import com.realtime.crossfire.jxclient.server.socket.ClientSocketState;
import com.realtime.crossfire.jxclient.skills.SkillSet;
import com.realtime.crossfire.jxclient.stats.Stats;
import java.util.Collections;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages items known to the character. This includes items on the floor, in
 * the character's inventory, the character object itself, and items within
 * containers known to the character.
 * @author Andreas Kirschbaum
 */
public class ItemsManager {

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
     * The inventory manager used to maintain player inventory state.
     */
    @NotNull
    private final AbstractManager inventoryManager;
    /**
     * The floor manager used to maintain floor object states.
     */
    @NotNull
    private final AbstractManager floorManager;

    /**
     * The current floor manager used to track the player's current floor
     * location.
     */
    @NotNull
    private final CurrentFloorManager currentFloorManager;

    /**
     * The list of {@link PlayerListener}s to be notified about changes of the
     * current player.
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
     * The known {@link CfItem}s.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link CrossfireUpdateItemListener} to receive item updates.
     */
    @NotNull
    private final CrossfireUpdateItemListener crossfireUpdateItemListener = new CrossfireUpdateItemListener() {
        /** {@inheritDoc} */
        @Override
        public void delinvReceived(final int tag) {
            cleanInventory(tag);
        }

        /** {@inheritDoc} */
        @Override
        public void delitemReceived(@NotNull final int[] tags) {
            removeItems(tags);
        }

        /** {@inheritDoc} */
        @Override
        public void additemReceived(final int location, final int tag, final int flags, final int weight, final int faceNum, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
            addItem(new CfItem(location, tag, flags, weight, facesManager.getFace(faceNum), name, namePl, anim, animSpeed, nrof, type));
        }

        /** {@inheritDoc} */
        @Override
        public void additemFinished() {
            fireEvents();
        }

        /** {@inheritDoc} */
        @Override
        public void playerReceived(final int tag, final int weight, final int faceNum, @NotNull final String name) {
            stats.setActiveSkill("");
            skillSet.clearNumberedSkills();
            setPlayer(new CfPlayer(tag, weight, facesManager.getFace(faceNum), name));
            stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, weight);
        }

        /** {@inheritDoc} */
        @Override
        public void upditemReceived(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
            updateItem(flags, tag, valLocation, valFlags, valWeight, valFaceNum, valName, valNamePl, valAnim, valAnimSpeed, valNrof);
            if ((flags&CfItem.UPD_WEIGHT) != 0) {
                final CfItem player = getPlayer();
                if (player != null && player.getTag() == tag) {
                    stats.setStat(CrossfireStatsListener.C_STAT_WEIGHT, valWeight);
                }
            }
        }
    };

    /**
     * The event scheduler callback for delaying event generation. This is
     * needed because the Crossfire server sends multiple item2 commands for one
     * "get all" command.
     */
    @NotNull
    private final Runnable fireEventCallback = new Runnable() {
        /** {@inheritDoc} */
        @Override
        public void run() {
            floorManager.fireEvents(currentFloorManager.getCurrentFloor());
            final int playerTag;
            final boolean hasPlayer;
            synchronized (sync) {
                hasPlayer = player != null;
                playerTag = player != null ? player.getTag() : 0;
            }
            if (hasPlayer) {
                inventoryManager.fireEvents(playerTag);
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
    private final GuiStateListener guiStateListener = new GuiStateListener() {
        /** {@inheritDoc} */
        @Override
        public void start() {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void metaserver() {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void preConnecting(@NotNull final String serverInfo) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final String serverInfo) {
            reset();
        }

        /** {@inheritDoc} */
        @Override
        public void connecting(@NotNull final ClientSocketState clientSocketState) {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connected() {
            // ignore
        }

        /** {@inheritDoc} */
        @Override
        public void connectFailed(@NotNull final String reason) {
            // ignore
        }
    };

    /**
     * Creates a new instance.
     * @param crossfireServerConnection the connection to monitor
     * @param facesManager the faces manager for looking up faces
     * @param stats the instance to update
     * @param skillSet the skill set instance to update
     * @param inventoryManager the inventory manager to use
     * @param floorManager the floor manager instance to use
     * @param guiStateManager the gui state manager to watch
     * @param itemSet the item set to use
     */
    public ItemsManager(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final Stats stats, @NotNull final SkillSet skillSet, @NotNull final AbstractManager inventoryManager, @NotNull final AbstractManager floorManager, @NotNull final GuiStateManager guiStateManager, @NotNull final ItemSet itemSet) {
        this.facesManager = facesManager;
        this.stats = stats;
        this.skillSet = skillSet;
        this.inventoryManager = inventoryManager;
        this.floorManager = floorManager;
        this.itemSet = itemSet;
        currentFloorManager = new CurrentFloorManager(itemSet, floorManager);
        crossfireServerConnection.addCrossfireUpdateItemListener(crossfireUpdateItemListener);
        guiStateManager.addGuiStateListener(guiStateListener);
        fireEventScheduler.start();
    }

    /**
     * Resets the manager's state.
     */
    private void reset() {
        synchronized (sync) {
            if (player != null) {
                cleanInventory(player.getTag());
            }
            cleanInventory(currentFloorManager.getCurrentFloor());
            final Iterable<CfItem> tmp = itemSet.removeAllItems();
            for (final CfItem item : tmp) {
                removeItem(item);
            }
            fireEvents();
            currentFloorManager.setCurrentFloor(0);
            floorManager.reset();
            inventoryManager.reset();
            setPlayer(null);
        }
    }

    /**
     * Returns an item by tag. This function may return the player object.
     * @param tag The tag.
     * @return the item or <code>null</code> if no such item exists
     */
    @Nullable
    private CfItem getItemOrPlayer(final int tag) {
        synchronized (sync) {
            if (player != null && player.getTag() == tag) {
                return player;
            }

            return itemSet.getItemByTag(tag);
        }
    }

    /**
     * Clears the inventory of an item.
     * @param tag the item tag
     */
    private void cleanInventory(final int tag) {
        for (final CfItem item : itemSet.getItemsByLocation(tag)) {
            removeItem(item);
        }
        fireEvents();
    }

    /**
     * Deletes items by tag.
     * @param tags the tags to delete
     */
    private void removeItems(@NotNull final int[] tags) {
        for (final int tag : tags) {
            removeItem(tag);
        }
        fireEvents();
    }

    /**
     * Deletes an item by tag.
     * @param tag the tag of the item to delete
     */
    private void removeItem(final int tag) {
        synchronized (sync) {
            final CfItem item = itemSet.removeItemByTag(tag);
            if (item != null) {
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
    private void removeItem(@NotNull final CfItem item) {
        synchronized (sync) {
            final Object deletedItem = itemSet.removeItemByTag(item.getTag());
            if (deletedItem == null) {
                throw new AssertionError("cannot find item "+item.getTag());
            }
            if (deletedItem != item) {
                throw new AssertionError("deleted wrong item "+item.getTag());
            }

            removeItemFromLocation(item);
        }
    }

    /**
     * Adds an item.
     * @param item the item to add
     */
    private void addItem(@NotNull final CfItem item) {
        synchronized (sync) {
            final CfItem deletedItem = itemSet.removeItemByTag(item.getTag());
            if (deletedItem != null) {
                //XXX: Do not complain about duplicate items as the Crossfire server sometimes does not correctly remove items from the ground when a player picks up items
                //System.err.println("addItem: duplicate item "+item.getTag());

                removeItemFromLocation(deletedItem);
            }

            itemSet.addItem(item);
            addItemToLocation(item);
        }
    }

    /**
     * Moves an item to a new location.
     * @param item the item to move
     * @param newLocation the location to move to
     */
    private void moveItem(@NotNull final CfItem item, final int newLocation) {
        synchronized (sync) {
            if (itemSet.getItemByTag(item.getTag()) != item) {
                throw new AssertionError("invalid item "+item.getTag());
            }

            removeItemFromLocation(item);
            item.setLocation(newLocation);
            addItemToLocation(item);
        }
    }

    /**
     * Removes an item from the set of known items. The item must exist.
     * @param item the item to remove
     */
    private void removeItemFromLocation(@NotNull final CfItem item) {
        if (currentFloorManager.isCurrentFloor(item.getTag())) {
            currentFloorManager.setCurrentFloor(0);
        }

        final int where = item.getLocation();
        @Nullable final AbstractManager abstractManager;
        if (currentFloorManager.isCurrentFloor(where)) {
            abstractManager = floorManager;
        } else if (player != null && where == player.getTag()) {
            abstractManager = inventoryManager;
        } else {
            abstractManager = null;
        }
        itemSet.removeItem(item, abstractManager);
    }

    /**
     * Adds an item to the set of known items.
     * @param item the item to add
     */
    private void addItemToLocation(@NotNull final CfItem item) {
        final int where = item.getLocation();
        if (currentFloorManager.isCurrentFloor(where)) {
            itemSet.addItem(item, floorManager);
        } else if (player != null && where == player.getTag()) {
            itemSet.addInventoryItem(item, inventoryManager);
        } else {
            itemSet.addItem(item, null);
        }
    }

    /**
     * Delivers outstanding change events.
     */
    private void fireEvents() {
        fireEventScheduler.trigger();
    }

    /**
     * Sets the player object this client controls.
     * @param player the new player object
     */
    private void setPlayer(@Nullable final CfPlayer player) {
        synchronized (sync) {
            final CfPlayer oldPlayer = this.player;
            if (oldPlayer == player) {
                if (oldPlayer != null) {
                    for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class)) {
                        listener.playerReceived(oldPlayer);
                    }
                }
                return;
            }

            if (oldPlayer != null) {
                itemSet.addModified(oldPlayer.getTag(), inventoryManager);
                for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class)) {
                    listener.playerRemoved(oldPlayer);
                }
            }
            this.player = player;
            if (player != null) {
                itemSet.addModified(player.getTag(), inventoryManager);
                for (final PlayerListener listener : playerListeners.getListeners(PlayerListener.class)) {
                    listener.playerAdded(player);
                    listener.playerReceived(player);
                }
            }
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
     * Adds a {@link PlayerListener} to be notified about changes of the current
     * player.
     * @param listener the listener to add
     */
    public void addCrossfirePlayerListener(@NotNull final PlayerListener listener) {
        playerListeners.add(PlayerListener.class, listener);
    }

    /**
     * Removes a {@link PlayerListener} to be notified about changes of the
     * current player.
     * @param listener the listener to remove
     */
    public void removeCrossfirePlayerListener(@NotNull final PlayerListener listener) {
        playerListeners.remove(PlayerListener.class, listener);
    }

    /**
     * Returns the current floor manager.
     * @return the current floor manager
     */
    @NotNull
    public CurrentFloorManager getCurrentFloorManager() {
        return currentFloorManager;
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
    private void updateItem(final int flags, final int tag, final int valLocation, final int valFlags, final int valWeight, final int valFaceNum, @NotNull final String valName, @NotNull final String valNamePl, final int valAnim, final int valAnimSpeed, final int valNrof) {
        final CfItem item = getItemOrPlayer(tag);
        if (item == null) {
            if (flags != CfItem.UPD_FACE) // XXX: suppress frequent error message due to server bug
            {
                System.err.println("updateItem: undefined item "+tag);
            }
            return;
        }

        final boolean wasOpen = (flags&CfItem.UPD_FLAGS) != 0 && currentFloorManager.getCurrentFloor() == item.getTag() && item.isOpen();
        item.update(flags, valFlags, valWeight, facesManager.getFace(valFaceNum), valName, valNamePl, valAnim, valAnimSpeed, valNrof);
        if ((flags&CfItem.UPD_LOCATION) != 0) {
            moveItem(item, valLocation);
        }
        if ((flags&CfItem.UPD_FLAGS) != 0) {
            if (item.isOpen()) {
                currentFloorManager.setCurrentFloor(item.getTag());
            } else if (wasOpen) {
                currentFloorManager.setCurrentFloor(0);
            }
        }
    }

    /**
     * Returns the player's inventory.
     * @return the inventory items; the list cannot be modified
     */
    @NotNull
    public List<CfItem> getInventory() {
        return player == null ? Collections.<CfItem>emptyList() : itemSet.getInventoryByTag(player.getTag());
    }

}
