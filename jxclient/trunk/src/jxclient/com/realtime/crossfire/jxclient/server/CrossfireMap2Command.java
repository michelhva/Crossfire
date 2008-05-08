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
package com.realtime.crossfire.jxclient.server;

/**
 * Utility class defining constants used by the "map2" Crossfire command.
 * @author Andreas Kirschbaum
 */
public class CrossfireMap2Command
{
    /**
     * The total number of map layers to display.
     */
    public static final int NUM_LAYERS = 10;
    /**
     * Offset for coordinate values in map2 command.
     */
    public static final int MAP2_COORD_OFFSET = 15;

    /**
     * Private constructor to prevent instantiation.
     */
    private CrossfireMap2Command()
    {
    }
}
