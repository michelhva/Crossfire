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

import javax.swing.event.EventListenerList;

/**
 * Manages the player's current floor location.
 *
 * @author Andreas Kirschbaum
 */
public class CurrentFloorManager
{
    /**
     * The items manager.
     */
    private final ItemsManager itemsManager;

    /**
     * The list of {@link CurrentFloorListener}s to be notified about changes
     * of the current floor location.
     */
    private final EventListenerList currentFloorListeners = new EventListenerList();

    /**
     * The location to show in the floor view.
     */
    private int currentFloor = 0;

    /**
     * Create a new instance.
     *
     * @param itemsManager The items manager to use.
     */
    public CurrentFloorManager(final ItemsManager itemsManager)
    {
        this.itemsManager = itemsManager;
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

        itemsManager.getFloorManager().addModified(itemsManager.getNumberOfItems(this.currentFloor));
        this.currentFloor = currentFloor;
        itemsManager.getFloorManager().addModified(itemsManager.getNumberOfItems(this.currentFloor));

        for (final CurrentFloorListener listener : currentFloorListeners.getListeners(CurrentFloorListener.class))
        {
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
     * Return whether a given location is the current floor.
     *
     * @param floor The location to compare.
     *
     * @return Whether the given location is the current floor.
     */
    public synchronized boolean isCurrentFloor(final int floor)
    {
        return currentFloor == floor;
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
}
