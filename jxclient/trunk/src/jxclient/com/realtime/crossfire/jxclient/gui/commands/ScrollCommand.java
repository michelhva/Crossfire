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
package com.realtime.crossfire.jxclient.gui.commands;

import com.realtime.crossfire.jxclient.gui.GUIScrollable;

/**
 * A {@link GUICommand} which scrolls a {@link GUIScrollable} gui element by a
 * given distance.
 * @author Andreas Kirschbaum
 */
public class ScrollCommand implements GUICommand
{
    /**
     * The distance to scroll.
     */
    private final int distance;

    /**
     * The scrollable element.
     */
    private final GUIScrollable scrollable;

    /**
     * Creates a new instance.
     * @param distance the distance to scroll
     * @param scrollable the scrollable element
     */
    public ScrollCommand(final int distance, final GUIScrollable scrollable)
    {
        this.distance = distance;
        this.scrollable = scrollable;
    }

    /** {@inheritDoc} */
    public boolean canExecute()
    {
        return scrollable.canScroll(distance);
    }

    /** {@inheritDoc} */
    public void execute()
    {
        scrollable.scroll(distance);
    }
}
