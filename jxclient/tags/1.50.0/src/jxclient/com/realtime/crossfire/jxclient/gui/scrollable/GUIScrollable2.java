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

package com.realtime.crossfire.jxclient.gui.scrollable;

import com.realtime.crossfire.jxclient.gui.gui.GUIScrollBar;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIScrollable} that can be attached to {@link GUIScrollBar}s.
 */
public interface GUIScrollable2 extends GUIScrollable {

    /**
     * Add a scrollable listener to be informed about changes.
     * @param listener The listener to add.
     */
    void addScrollableListener(@NotNull ScrollableListener listener);

    /**
     * Remove a scrollable listener.
     * @param listener The listener to remove.
     */
    void removeScrollableListener(@NotNull ScrollableListener listener);

    /**
     * Scroll to the given location. The possible range is given by a previous
     * notification through a listener registered with {@link
     * #addScrollableListener(ScrollableListener)}.
     * @param pos The location to scroll to.
     */
    void scrollTo(int pos);

}
