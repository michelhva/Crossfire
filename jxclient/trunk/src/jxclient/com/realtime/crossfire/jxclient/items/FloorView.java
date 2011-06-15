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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a view to all items comprising the current floor view.
 * <p/>
 * If an opened container exists, the floor view consists of the container in
 * the first slot followed by the contained items. Otherwise all objects in
 * location <code>0</code> are returned.
 * @author Andreas Kirschbaum
 */
public class FloorView extends AbstractItemView {

    /**
     * The {@link ItemSet} to monitor.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The tag of the currently shown container or <code>0</code> if no
     * container is open.
     */
    private int currentFloor = 0;

    /**
     * The {@link ItemSetListener} for detecting opened or closed containers.
     */
    @NotNull
    private final ItemSetListener itemSetListener = new ItemSetListener() {

        @Override
        public void itemAdded(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemMoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemChanged(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void itemRemoved(@NotNull final CfItem item) {
            // ignore
        }

        @Override
        public void playerChanged(@Nullable final CfItem player) {
            // ignore
        }

        @Override
        public void openContainerChanged(final int tag) {
            setCurrentFloor(tag);
        }

    };

    /**
     * The {@link ItemListener} attached to the current floor object.
     */
    @NotNull
    private final ItemListener itemListener = new ItemListener() {

        @Override
        public void itemChanged(final int tag) {
            if (currentFloor != 0) {
                addModified(0);
            }
        }

        @Override
        public void itemRemoved(final int tag) {
            setCurrentFloor(0);
        }

        @Override
        public void inventoryAdded(final int tag, final int index, @NotNull final CfItem item) {
            final int offset = getOffset();
            addModifiedRange(index+offset, itemSet.getNumberOfItemsByLocation(tag)-1+offset);
        }

        @Override
        public void inventoryRemoved(final int tag, final int index) {
            final int offset = getOffset();
            addModifiedRange(index+offset, itemSet.getNumberOfItemsByLocation(tag)+offset);
        }

    };

    /**
     * Creates a new instance.
     * @param itemSet the item set to use
     */
    public FloorView(@NotNull final ItemSet itemSet) {
        this.itemSet = itemSet;
        itemSet.addInventoryListener(currentFloor, itemListener);
        setCurrentFloor(itemSet.getOpenContainer());
        itemSet.addItemSetListener(itemSetListener);
    }

    /**
     * Returns the current floor location.
     * @return the current floor location: a container's tag or <code>0</code>
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Updates the currently opened container that's contents are shown in the
     * floor view.
     * @param currentFloor the current floor tag
     */
    private void setCurrentFloor(final int currentFloor) {
        if (this.currentFloor == currentFloor) {
            return;
        }

        final int prevLastIndex = getSize()-1;
        itemSet.removeInventoryListener(this.currentFloor, itemListener);
        this.currentFloor = currentFloor;
        itemSet.addInventoryListener(this.currentFloor, itemListener);
        final int nextLastIndex = getSize()-1;
        addModifiedRange(0, Math.max(prevLastIndex, nextLastIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return itemSet.getNumberOfItemsByLocation(currentFloor)+getOffset();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CfItem getItem(final int index) {
        final int index2;
        if (currentFloor == 0) {
            index2 = index;
        } else {
            if (index == 0) {
                return itemSet.getItemByTag(currentFloor);
            }

            index2 = index-1;
        }

        return itemSet.getInventoryItem(currentFloor, index2);
    }

    /**
     * Returns the number of non-inventory items to be displayed on the floor.
     * Currently this can be zero or one (virtual container to close the
     * currently opened container).
     * @return the number of non-inventory items to show
     */
    private int getOffset() {
        return currentFloor == 0 ? 0 : 1;
    }

}
