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
package com.realtime.crossfire.jxclient.metaserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Metaserver
{
    /**
     * The minimal interval (in seconds) between two metasever queries.
     */
    public static final long MIN_QUERY_INTERVAL = 30;

    private static final String metaserver_name = "crossfire.real-time.com";

    private static final int metaserver_port = 13326;

    private final List<MetaserverEntry> metalist = new ArrayList<MetaserverEntry>();

    /**
     * All registered metaserver listeners.
     */
    private final List<MetaserverListener> metaserverListeners = new ArrayList<MetaserverListener>();

    /**
     * All registered metaserver entry listeners. Maps entry index to list of listeners.
     */
    private final Map<Integer, List<MetaserverEntryListener>> metaserverEntryListeners = new HashMap<Integer, List<MetaserverEntryListener>>();

    /**
     * Do not query th metaserver before time time has been reached. This is to
     * prevent unneccessary queries. It also prevents getting empty results
     * from the metaserver.
     */
    private long nextQuery = System.currentTimeMillis();

    /**
     * Return an metaserver entry by index.
     *
     * @param index The index.
     *
     * @return The metaserver entry, or <code>null</code> if the index is
     * invalid.
     */
    public synchronized MetaserverEntry getEntry(final int index)
    {
        try
        {
            return metalist.get(index);
        }
        catch (IndexOutOfBoundsException ex)
        {
            return null;
        }
    }

    /**
     * Return the number of metaserver entries.
     *
     * @return The number of metaserver entries.
     */
    public synchronized int size()
    {
        return metalist.size();
    }

    public synchronized void query()
    {
        if (nextQuery > System.currentTimeMillis())
        {
            return;
        }

        final int metalistSize = metalist.size();
        metalist.clear();
        for (int i = metalistSize-1; i >= 0; i--)
        {
            for (final MetaserverEntryListener metaserverEntryListener : getMetaserverEntryListeners(i))
            {
                metaserverEntryListener.entryRemoved();
            }
        }

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
        catch (final IOException ex)
        {
            // ignore (but keep already parsed entries)
        }
        Collections.sort(metalist);

        nextQuery = System.currentTimeMillis()+MIN_QUERY_INTERVAL*1000;

        for (final MetaserverListener metaserverListener : metaserverListeners)
        {
            metaserverListener.numberOfEntriesChanged();
        }

        for (int i = 0; i < metalist.size(); i++)
        {
            for (final MetaserverEntryListener metaserverEntryListener : getMetaserverEntryListeners(i))
            {
                metaserverEntryListener.entryAdded();
            }
        }
    }

    /**
     * Parse a metaserver response line and add an entry to {@link #metalist}.
     *
     * @param entry The metaserver response lines to parse.
     */
    private void parseEntry(final String entry)
    {
        final MetaserverEntry metaserverEntry = MetaserverEntryParser.parse(entry);
        if (metaserverEntry == null)
        {
            System.err.println("Dropping invalid metaserver response line: "+entry);
            return;
        }
        metalist.add(metaserverEntry);
    }

    /**
     * Add a metaserver listener.
     *
     * @param listener The listener to add.
     */
    public void addMetaserverListener(final MetaserverListener listener)
    {
        metaserverListeners.add(listener);
    }

    /**
     * Remove a metaserver listener.
     *
     * @param listener The listener to add.
     */
    public void removeMetaserverListener(final MetaserverListener listener)
    {
        metaserverListeners.remove(listener);
    }

    /**
     * Add a metaserver entry listener for one entry.
     *
     * @param index The entry index to monitor.
     *
     * @param listener The listener to add.
     */
    public void addMetaserverEntryListener(final int index, final MetaserverEntryListener listener)
    {
        getMetaserverEntryListeners(index).add(listener);
    }

    /**
     * Remove a metaserver entry listener for one entry.
     *
     * @param index The entry index to monitor.
     *
     * @param listener The listener to add.
     */
    public void removeMetaserverEntryListener(final int index, final MetaserverEntryListener listener)
    {
        getMetaserverEntryListeners(index).remove(listener);
    }

    /**
     * Return the metaserver entry listeners for one entry index.
     *
     * @param index The entry index.
     *
     * @return The listsners list.
     */
    private synchronized List<MetaserverEntryListener> getMetaserverEntryListeners(final int index)
    {
        final List<MetaserverEntryListener> existingListeners = metaserverEntryListeners.get(index);
        if (existingListeners != null)
        {
            return existingListeners;
        }

        final List<MetaserverEntryListener> newListeners = new ArrayList<MetaserverEntryListener>();
        metaserverEntryListeners.put(index, newListeners);
        return newListeners;
    }
}
