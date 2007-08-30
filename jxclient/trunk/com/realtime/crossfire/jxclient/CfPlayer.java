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

import com.realtime.crossfire.jxclient.faces.Face;
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
public class CfPlayer extends CfItem
{
    private static final List<CrossfireStatsListener> mylisteners_stats = new ArrayList<CrossfireStatsListener>();

    private static final Stats mystats = new Stats();

    public CfPlayer(final int tag, final int weight, final Face face, final String name)
    {
        super(0, tag, 0, weight, face, name, name, 1);
    }

    public static Stats getStats()
    {
        return mystats;
    }

    public static void addCrossfireStatsListener(final CrossfireStatsListener listener)
    {
        mylisteners_stats.add(listener);
    }

    public static void removeCrossfireStatsListener(final CrossfireStatsListener listener)
    {
        mylisteners_stats.remove(listener);
    }

    public static void setStatsProcessed()
    {
        final CrossfireCommandStatsEvent evt = new CrossfireCommandStatsEvent(new Object(), mystats);
        for (final CrossfireStatsListener listener : mylisteners_stats)
        {
            listener.commandStatsReceived(evt);
        }
    }
}
