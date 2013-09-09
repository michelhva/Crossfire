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

package com.realtime.crossfire.jxclient.util;

import org.jetbrains.annotations.NotNull;

/**
 * Maintains a bidirectional mapping from a set of integers to the same range of
 * integers.
 * @author Andreas Kirschbaum
 */
public class DoubleMapping {

    /**
     * The forward mappings. Maps source index to destination index.
     */
    @NotNull
    private final Mapping forward = new Mapping();

    /**
     * The backward mappings. Maps destination index to source index.
     */
    @NotNull
    private final Mapping backward = new Mapping();

    /**
     * Removes all mappings.
     */
    public void clear() {
        forward.clear();
        backward.clear();
    }

    /**
     * Adds a mapping.
     * @param src the source index
     * @param dst the destination index
     */
    public void insert(final int src, final int dst) {
        forward.insert(src, dst);
        backward.insert(dst, src);
    }

    /**
     * Removes a mapping.
     * @param src the source index
     */
    public void remove(final int src) {
        final int dst = forward.get(src);
        forward.remove(src);
        backward.remove(dst);
    }

    /**
     * Returns a mapping.
     * @param src the source index
     * @return the destination index
     */
    public int getDst(final int src) {
        return forward.get(src);
    }

    /**
     * Returns a mapping.
     * @param dst the destination index
     * @return the source index
     */
    public int getSrc(final int dst) {
        return backward.get(dst);
    }

}
