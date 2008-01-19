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
package com.realtime.crossfire.jxclient.gui;

/**
 * Listener for {@link GUIScrollable} elements.
 *
 * @author Andreas Kirschbaum
 */
public interface ScrollableListener
{
    /**
     * Sets the scoll range.
     *
     * @param valueMin The minimum scroll value.
     *
     * @param valueMax The maximum scroll value.
     *
     * @param sliderPos The scroll location; it need not within the scroll
     * range.
     *
     * @param sliderSize The size of the slider itself.
     */
    void setRange(int valueMin, int valueMax, int sliderPos, int sliderSize);
}
