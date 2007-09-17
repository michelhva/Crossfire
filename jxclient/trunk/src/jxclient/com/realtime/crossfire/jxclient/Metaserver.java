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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Metaserver
{
    private static final String metaserver_name = "crossfire.real-time.com";

    private static final int metaserver_port = 13326;

    private static final List<MetaserverEntry> metalist = new ArrayList<MetaserverEntry>();

    public static synchronized List<MetaserverEntry> query()
    {
        if (!metalist.isEmpty())
        {
            return metalist;
        }

        metalist.clear();
        parseEntry("127.0.0.1|0|localhost|0|1.8.0|localhost|0|0|0");
        try
        {
            final Socket socket = new Socket(metaserver_name, metaserver_port);
            try
            {
                final DataInputStream in = new DataInputStream(socket.getInputStream());
                try
                {
                    final BufferedReader bin = new BufferedReader(new InputStreamReader(in));
                    try
                    {
                        for (;;)
                        {
                            final String entry = bin.readLine();
                            if (entry == null)
                            {
                                break;
                            }
                            parseEntry(entry);
                        }
                    }
                    finally
                    {
                        bin.close();
                    }
                }
                finally
                {
                    in.close();
                }
            }
            finally
            {
                socket.close();
            }
        }
        catch (final UnknownHostException e)
        {
            // ignore
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        Collections.sort(metalist);
        return metalist;
    }

    /**
     * Parse a metaserver response line and add an entry to {@link #metalist}.
     *
     * @param entry The metaserver response lines to parse.
     */
    private static void parseEntry(final String entry)
    {
        final String[] entries = entry.split("\\|");
        if (entries.length != 9)
        {
            System.err.println("Dropping invalid metaserver response line: "+entry);
            return;
        }

        final MetaserverEntry me;
        try
        {
            me = new MetaserverEntry(entries[0], entries[2], entries[5], entries[4], Integer.parseInt(entries[3]), Integer.parseInt(entries[1]));
        }
        catch (final NumberFormatException ex)
        {
            System.err.println("Dropping invalid metaserver response line: "+entry);
            return;
        }
        metalist.add(me);
    }
}
