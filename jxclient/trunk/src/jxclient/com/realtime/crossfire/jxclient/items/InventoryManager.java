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

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the items in the player's inventory.
 * @author Andreas Kirschbaum
 */
public class InventoryManager extends AbstractManager {

    /**
     * The {@link ItemSet} for looking up items.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * Creates a new instance.
     * @param itemSet the item set for looking up items
     */
    public InventoryManager(@NotNull final ItemSet itemSet) {
        super(itemSet);
        this.itemSet = itemSet;
    }

    /**
     * Find the correct insertion position for an inventory object.
     * @param list The inventory objects.
     * @param item The item to add.
     * @return The insertion index.
     */
    public static int getInsertionIndex(@NotNull final List<CfItem> list, @NotNull final CfItem item) {
        for (int i = 0; i < list.size(); i++) {
            if (compareItem(list.get(i), item) >= 0) {
                return i;
            }
        }
        return list.size();
    }

    /**
     * Compare two items by inventory order.
     * @param item1 The first item to compare.
     * @param item2 The second item to compare.
     * @return The comparision result: -1=<code>item1</code> before
     *         </code>item2</code>, 0=<code>item1</code> == </code>item2</code>,
     *         +1=<code>item1</code> after </code>item2</code>.
     */
    private static int compareItem(@NotNull final CfItem item1, @NotNull final CfItem item2) {
        if (item1.getType() < item2.getType()) {
            return -1;
        }
        if (item1.getType() > item2.getType()) {
            return +1;
        }
        final int cmp1 = item1.getName().compareTo(item2.getName());
        if (cmp1 != 0) {
            return cmp1;
        }
        if (item1.getTag() < item2.getTag()) {
            return -1;
        }
        if (item1.getTag() > item2.getTag()) {
            return +1;
        }
        return 0;
    }

    /**
     * Adds an {@link CfItem}.
     * @param item the item to add
     */
    public void addInventoryItem(@NotNull final CfItem item) {
        final int startIndex = itemSet.addInventoryItem(item);
        final int endIndex = itemSet.getNumberOfItemsByLocation(item.getLocation());
        addModified(startIndex, endIndex);
    }

    /**
     * Marks all inventory items as modified.
     * @param playerTag the player's tag
     */
    public void updatePlayer(final int playerTag) {
        addModified(itemSet.getItemsByLocation(playerTag));
    }

}
