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

import com.realtime.crossfire.jxclient.util.DoubleMapping;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a view of all items in the current player's inventory.
 * <p/>
 * If no player object is known an empty inventory view is generated.
 * @author Andreas Kirschbaum
 */
public class InventoryView extends AbstractItemView {

    /**
     * The {@link ItemSet} to monitor.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link Comparator} for sorting.
     */
    @NotNull
    private final Comparator<CfItem> comparator;

    /**
     * The tag of the current player object or <code>-1</code>.
     */
    private int currentPlayerTag = -1;

    /**
     * The items in the inventory ordered by {@link #comparator}.
     */
    @NotNull
    private final List<CfItem> items = new ArrayList<CfItem>();

    /**
     * Maps external index to original index.
     */
    @NotNull
    private final DoubleMapping mapping = new DoubleMapping();

    /**
     * The {@link ItemSetListener} attached to {@link #itemSet} to track the
     * current player object.
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
            setCurrentPlayerTag(player == null ? -1 : player.getTag());
        }

        @Override
        public void openContainerChanged(final int tag) {
            // ignore
        }

    };

    /**
     * The {@link ItemListener} attached to the current player object.
     */
    @NotNull
    private final ItemListener playerInventoryListener = new ItemListener() {

        @Override
        public void itemChanged(final int tag) {
            // ignore
        }

        @Override
        public void itemRemoved(final int tag) {
            // ignore; will be detected by itemSetListener
        }

        @Override
        public void inventoryAdded(final int tag, final int index, @NotNull final CfItem item) {
            assert tag == currentPlayerTag;

            final int index2 = findInsertionIndex(item);
            mapping.insert(index2, index);
            items.add(index2, item);
            addModifiedRange(index2, itemSet.getNumberOfItemsByLocation(tag)-1);
        }

        @Override
        public void inventoryRemoved(final int tag, final int index) {
            assert tag == currentPlayerTag;

            final int index2 = mapping.getSrc(index);
            mapping.remove(index2);
            items.remove(index2);
            addModifiedRange(index2, itemSet.getNumberOfItemsByLocation(tag));
        }

    };

    /**
     * Creates a new instance.
     * @param itemSet the item set to monitor
     * @param comparator the comparator for sorting
     */
    public InventoryView(@NotNull final ItemSet itemSet, @NotNull final Comparator<CfItem> comparator) {
        this.itemSet = itemSet;
        this.comparator = comparator;
        itemSet.addItemSetListener(itemSetListener);
        final CfItem player = itemSet.getPlayer();
        currentPlayerTag = player == null ? -1 : player.getTag();
    }

    /**
     * Updates the current player object.
     * @param currentPlayerTag the tag of the player object or <code>-1</code>
     */
    private void setCurrentPlayerTag(final int currentPlayerTag) {
        final int prevSize;
        if (this.currentPlayerTag == -1) {
            prevSize = 0;
        } else {
            itemSet.removeInventoryListener(this.currentPlayerTag, playerInventoryListener);
            prevSize = itemSet.getNumberOfItemsByLocation(currentPlayerTag);
        }
        this.currentPlayerTag = currentPlayerTag;
        final int nextSize;
        if (this.currentPlayerTag == -1) {
            nextSize = 0;
        } else {
            itemSet.addInventoryListener(this.currentPlayerTag, playerInventoryListener);
            nextSize = itemSet.getNumberOfItemsByLocation(currentPlayerTag);
        }

        items.clear();
        mapping.clear();
        if (currentPlayerTag != -1) {
            for (int i = 0; i < nextSize; i++) {
                final CfItem item = itemSet.getInventoryItem(currentPlayerTag, i);
                assert item != null;
                playerInventoryListener.inventoryAdded(currentPlayerTag, i, item);
            }
        }
        addModifiedRange(nextSize, prevSize-1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return items.size();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CfItem getItem(final int index) {
        if (index < 0) {
            return null;
        }

        try {
            return items.get(index);
        } catch (final IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    /**
     * Returns the insertion index of a {@link CfItem}.
     * @param item the item to insert into the inventory
     * @return the index to insert at
     */
    private int findInsertionIndex(@NotNull final CfItem item) {
        int i;
        for (i = 0; i < items.size(); i++) {
            if (comparator.compare(items.get(i), item) >= 0) {
                break;
            }
        }
        return i;
    }

}
