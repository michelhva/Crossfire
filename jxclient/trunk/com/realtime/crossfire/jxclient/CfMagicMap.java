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
package com.realtime.crossfire.jxclient;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class CfMagicMap
{
    private static final List<CrossfireMagicmapListener> mylisteners_magicmap = new ArrayList<CrossfireMagicmapListener>();

    public static void addCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        mylisteners_magicmap.add(listener);
    }

    public static void removeCrossfireMagicmapListener(final CrossfireMagicmapListener listener)
    {
        mylisteners_magicmap.remove(listener);
    }

    public static void magicmap(final DataInputStream dis) throws IOException
    {
        final int len = dis.available();
        final byte buf[] = new byte[len];

        dis.readFully(buf);

        final String str = new String(buf);
        final String packs[] = str.split(" ", 5);
        final CrossfireCommandMagicmapEvent evt = new CrossfireCommandMagicmapEvent(new Object(), Integer.parseInt(packs[0]), Integer.parseInt(packs[1]), Integer.parseInt(packs[2]), Integer.parseInt(packs[3]), packs[4].getBytes());
        for (final CrossfireMagicmapListener listener : mylisteners_magicmap)
        {
            listener.commandMagicmapReceived(evt);
        }
    }
}
