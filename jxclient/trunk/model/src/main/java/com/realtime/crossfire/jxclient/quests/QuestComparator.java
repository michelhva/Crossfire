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
 * Copyright (C) 2011 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.quests;

import java.io.Serializable;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Comparator} to compare {@link Quest} instances by title and code.
 * @author Nicolas Weeger
 */
public class QuestComparator implements Comparator<Quest>, Serializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(@NotNull final Quest o1, @NotNull final Quest o2) {
        final int cmp = String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle(), o2.getTitle());
        if (cmp != 0) {
            return cmp;
        }

        return o1.getCode() < o2.getCode() ? -1 : o1.getCode() > o2.getCode() ? 1 : 0;
    }
}
