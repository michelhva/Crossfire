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

package com.realtime.crossfire.jxclient.spells;

import java.io.Serializable;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Comparator} to compare {@link Spell} instances by spell path and
 * name.
 * @author Andreas Kirschbaum
 */
public class SpellComparator implements Comparator<Spell>, Serializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    @Override
    public int compare(@NotNull final Spell o1, @NotNull final Spell o2) {
        final int path1 = o1.getPath();
        final int path2 = o2.getPath();
        if (path1 < path2) {
            return -1;
        }
        if (path1 > path2) {
            return +1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    }

}
