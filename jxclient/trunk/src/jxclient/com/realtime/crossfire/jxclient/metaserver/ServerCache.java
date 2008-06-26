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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Maintains a set of known servers backed up in a file.
 *
 * @author Andreas Kirschbaum
 */
public class ServerCache
{
    /**
     * The cached entries. Maps key (see {@link #makeKey(MetaserverEntry)}) to
     * {@link Info} instance for the metaserver entry.
     */
    private final Map<String, Info> entries = new HashMap<String, Info>();

    /**
     * The backing file.
     */
    private final File file;

    /**
     * Create a new instance.
     *
     * @param file The backing file.
     */
    public ServerCache(final File file)
    {
        this.file = file;

        load();
    }

    /**
     * Add an entry to the cache. Overwrites old entries for the same hostname.
     *
     * @param metaserverEntry The entry to add.
     */
    public void put(final MetaserverEntry metaserverEntry)
    {
        entries.put(makeKey(metaserverEntry), new Info(metaserverEntry));
    }

    /**
     * Expire entries older than a given timestamp from the cache.
     *
     * @param timestamp The timestamp.
     */
    public void expire(final long timestamp)
    {
        final long now = System.currentTimeMillis();

        final Iterator<Info> it = entries.values().iterator();
        while (it.hasNext())
        {
            final Info info = it.next();
            if (now-info.getTimestamp() > timestamp)
            {
                it.remove();
            }
        }
    }

    /**
     * Return all cached entries. The returned set may be modified by the
     * caller.
     *
     * @return The cached entries; maps key to metaserver entry.
     */
    public Map<String, MetaserverEntry> getAll()
    {
        final Map<String, MetaserverEntry> result = new HashMap<String, MetaserverEntry>();
        for (final Map.Entry<String, Info> entry : entries.entrySet())
        {
            result.put(entry.getKey(), entry.getValue().getMetaserverEntry());
        }
        return result;
    }

    /**
     * Return the key for a metaserver entry.
     *
     * @param metaserverEntry The metaserver entry.
     *
     * @return The key.
     */
    public static String makeKey(final MetaserverEntry metaserverEntry)
    {
        return metaserverEntry.getHostname();
    }

    /**
     * Load the entries from the backing file.
     */
    private void load()
    {
        if (file == null)
        {
            return;
        }

        try
        {
            final FileInputStream fis = new FileInputStream(file);
            try
            {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try
                {
                    final LineNumberReader lnr = new LineNumberReader(isr);
                    try
                    {
                        for (;;)
                        {
                            final String line = lnr.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            final String[] tmp = line.split(" ", 2);
                            if (tmp.length != 2)
                            {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }

                            final long timestamp;
                            try
                            {
                                timestamp = Long.parseLong(tmp[0]);
                            }
                            catch (final NumberFormatException ex)
                            {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }
                            final MetaserverEntry metaserverEntry = MetaserverEntryParser.parseEntry(tmp[1]);
                            if (metaserverEntry == null)
                            {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }

                            entries.put(makeKey(metaserverEntry), new Info(metaserverEntry, timestamp));
                        }
                    }
                    finally
                    {
                        lnr.close();
                    }
                }
                finally
                {
                    isr.close();
                }
            }
            finally
            {
                fis.close();
            }
        }
        catch (final FileNotFoundException ex)
        {
            // ignore
        }
        catch (final IOException ex)
        {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Save all entries to the backing file.
     */
    public void save()
    {
        if (file == null)
        {
            return;
        }

        try
        {
            final FileOutputStream fos = new FileOutputStream(file);
            try
            {
                final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                try
                {
                    final BufferedWriter bw = new BufferedWriter(osw);
                    try
                    {
                        for (final Info info : entries.values())
                        {
                            bw.write(Long.toString(info.getTimestamp()));
                            bw.write(' ');
                            bw.write(MetaserverEntryParser.format(info.getMetaserverEntry()));
                            bw.write('\n');
                        }
                    }
                    finally
                    {
                        bw.close();
                    }
                }
                finally
                {
                    osw.close();
                }
            }
            finally
            {
                fos.close();
            }
        }
        catch (final IOException ex)
        {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * An entry in the cache. It consists of a {@link MetaserverEntry} and a
     * timestamp of last update.
     */
    private static class Info
    {
        /**
         * The metaserver entry.
         */
        private final MetaserverEntry metaserverEntry;

        /**
         * The timestamp of last update.
         */
        private final long timestamp;

        /**
         * Create a new instance. Sets the timestamp to "now".
         *
         * @param metaserverEntry The metaserver entry.
         */
        public Info(final MetaserverEntry metaserverEntry)
        {
            this(metaserverEntry, System.currentTimeMillis());
        }

        /**
         * Create a new instance.
         *
         * @param metaserverEntry The metaserver entry.
         *
         * @param timestamp The timestamp.
         */
        public Info(final MetaserverEntry metaserverEntry, final long timestamp)
        {
            this.metaserverEntry = metaserverEntry;
            this.timestamp = timestamp;
        }

        /**
         * Return the metaserver entry.
         *
         * @return The metaserver entry.
         */
        public MetaserverEntry getMetaserverEntry()
        {
            return metaserverEntry;
        }

        /**
         * Return the timestamp.
         *
         * @return The timestamp.
         */
        public long getTimestamp()
        {
            return timestamp;
        }
    }
}
