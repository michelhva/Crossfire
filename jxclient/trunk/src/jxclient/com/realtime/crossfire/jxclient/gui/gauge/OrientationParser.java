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
package com.realtime.crossfire.jxclient.gui.gauge;

/**
 * Utility class to parse orientation names.
 * @author Andreas Kirschbaum
 */
public class OrientationParser
{
    /**
     * Private constructor to prevent instantiation.
     */
    private OrientationParser()
    {
    }

    /**
     * Converts an orientation name into an {@link Orientation} instance. Each
     * parsing creates a new instance.
     * @param name the orientation name
     * @return the orientation instance
     * @throws IllegalArgumentException if the orientation name is undefined
     */
    public static Orientation parseOrientation(final String name)
    {
        if (name.equals("EW")) return new OrientationEW();
        if (name.equals("NS")) return new OrientationNS();
        if (name.equals("SN")) return new OrientationSN();
        if (name.equals("WE")) return new OrientationWE();
        throw new IllegalArgumentException();
    }
}
