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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Maintains a set of known servers backed up in a file.
 * @author Andreas Kirschbaum
 */
public class ServerCache {

    /**
     * The default entry for the "localhost" server.
     */
    @NotNull
    public static final CharSequence DEFAULT_ENTRY_LOCALHOST = "0|localhost|0|--|Local server. Start server before you try to connect.|0|0|0|||";

    /**
     * The cached entries. Maps key (see {@link #makeKey(MetaserverEntry)}) to
     * {@link Info} instance for the metaserver entry.
     */
    @NotNull
    private final Map<String, Info> entries = new HashMap<String, Info>();

    /**
     * The backing file.
     */
    @Nullable
    private final File file;

    /**
     * Creates a new instance.
     * @param file the backing file or <code>null</code> to not cache on disk
     */
    public ServerCache(@Nullable final File file) {
        this.file = file;

        load();
    }

    /**
     * Adds an entry to the cache. Overwrites old entries for the same
     * hostname.
     * @param metaserverEntry the entry to add
     */
    public void put(@NotNull final MetaserverEntry metaserverEntry) {
        entries.put(makeKey(metaserverEntry), new Info(metaserverEntry));
    }

    /**
     * Expires entries older than a given timestamp from the cache.
     * @param timestamp the timestamp
     */
    public void expire(final long timestamp) {
        final long now = System.currentTimeMillis();

        final Iterator<Info> it = entries.values().iterator();
        while (it.hasNext()) {
            final Info info = it.next();
            if (now-info.getTimestamp() > timestamp) {
                it.remove();
            }
        }
    }

    /**
     * Returns all cached entries. The returned set may be modified by the
     * caller.
     * @return the cached entries; maps key to metaserver entry
     */
    @NotNull
    public Map<String, MetaserverEntry> getAll() {
        final Map<String, MetaserverEntry> result = new HashMap<String, MetaserverEntry>();
        for (final Map.Entry<String, Info> entry : entries.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getMetaserverEntry());
        }
        return result;
    }

    /**
     * Returns the key for a metaserver entry.
     * @param metaserverEntry the metaserver entry
     * @return the key
     */
    @NotNull
    public static String makeKey(@NotNull final MetaserverEntry metaserverEntry) {
        return metaserverEntry.getHostname();
    }

    /**
     * Loads the entries from the backing file.
     */
    private void load() {
        if (file == null) {
            return;
        }

        try {
            final FileInputStream fis = new FileInputStream(file);
            try {
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                try {
                    final LineNumberReader lnr = new LineNumberReader(isr);
                    try {
                        while (true) {
                            final String line = lnr.readLine();
                            if (line == null) {
                                break;
                            }

                            final String[] tmp = line.split(" ", 2);
                            if (tmp.length != 2) {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }

                            final long timestamp;
                            try {
                                timestamp = Long.parseLong(tmp[0]);
                            } catch (final NumberFormatException ignored) {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }
                            if (!addEntry(tmp[1], timestamp)) {
                                System.err.println(file+":"+lnr.getLineNumber()+": syntax error");
                                continue;
                            }
                        }
                    } finally {
                        lnr.close();
                    }
                } finally {
                    isr.close();
                }
            } finally {
                fis.close();
            }
        } catch (final FileNotFoundException ignored) {
            // add default entries if the cache files does not exist
            final long now = System.currentTimeMillis()-1;
            addEntry(DEFAULT_ENTRY_LOCALHOST, now);
            addEntry("0|crossfire.metalforge.net|0|--|Latest SVN 1.x branch.<br>Eden Prairie, MN US<br>4.65Mb link<br><a href=\"http://crossfire.real-time.com\">crossfire.real-time.com</a>|0|0|0|Standard|Standard|Standard", now);
            addEntry("0|invidious.meflin.net|0|--|<b>Welcome, we are testing 2.0 come on in the water is fine.</b>|0|0|0|Standard|Standard|Standard + Testing", now);
        } catch (final IOException ex) {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Parses a metaserver entry line and adds the result to {@link #entries}.
     * @param metaserverEntryLine the metaserver entry line to parse
     * @param timestamp the query timestamp
     * @return whether the line was parsed correctly
     */
    private boolean addEntry(@NotNull final CharSequence metaserverEntryLine, final long timestamp) {
        final MetaserverEntry metaserverEntry = MetaserverEntryParser.parseEntry(metaserverEntryLine);
        if (metaserverEntry == null) {
            return false;
        }

        entries.put(makeKey(metaserverEntry), new Info(metaserverEntry, timestamp));
        return true;
    }

    /**
     * Saves all entries to the backing file.
     */
    public void save() {
        if (file == null) {
            return;
        }

        try {
            saveInternal(file);
        } catch (final IOException ex) {
            System.err.println(file+": "+ex.getMessage());
        }
    }

    /**
     * Saves all entries to the backing file.
     * @param file the file to write
     * @throws IOException if an I/O error occurs
     */
    private void saveInternal(@NotNull final File file) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        try {
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            try {
                final BufferedWriter bw = new BufferedWriter(osw);
                try {
                    for (final Info info : entries.values()) {
                        bw.write(Long.toString(info.getTimestamp()));
                        bw.write(' ');
                        bw.write(MetaserverEntryParser.format(info.getMetaserverEntry()));
                        bw.write('\n');
                    }
                } finally {
                    bw.close();
                }
            } finally {
                osw.close();
            }
        } finally {
            fos.close();
        }
    }

}
