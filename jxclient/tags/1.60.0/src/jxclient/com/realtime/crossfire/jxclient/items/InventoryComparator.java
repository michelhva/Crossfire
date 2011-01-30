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

import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Comparator} that compares {@link CfItem} instances in inventory view
 * order.
 * @author Andreas Kirschbaum
 */
public class InventoryComparator implements Comparator<CfItem> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(@NotNull final CfItem o1, @NotNull final CfItem o2) {
        if (o1.getType() < o2.getType()) {
            return -1;
        }
        if (o1.getType() > o2.getType()) {
            return +1;
        }
        final int cmp1 = o1.getName().compareTo(o2.getName());
        if (cmp1 != 0) {
            return cmp1;
        }
        if (o1.getTag() < o2.getTag()) {
            return -1;
        }
        if (o1.getTag() > o2.getTag()) {
            return +1;
        }
        return 0;
    }

}
