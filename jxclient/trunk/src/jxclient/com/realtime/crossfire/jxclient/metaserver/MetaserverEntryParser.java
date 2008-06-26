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

import com.realtime.crossfire.jxclient.util.NumberParser;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Parser for response lines of metaserver response lines.
 *
 * @author Andreas Kirschbaum
 */
public class MetaserverEntryParser
{
    /**
     * The default server version if none specified.
     */
    private static final String UNKNOWN_VERSION = "?";

    /**
     * The default for archbase, mapbase, and codebase if none specified.
     */
    private static final String DEFAULT_BASE = "not specified";

    /**
     * The pattern to split a metaserver response line into fields.
     */
    private static final Pattern fieldSeparatorPattern = Pattern.compile("\\|");

    /**
     * Whether response parsing is withing a server entry.
     */
    private boolean inSection = false;

    /**
     * The "update seconds" value for the current server entry.
     */
    private int updateSeconds = 0;

    /**
     * The "hostname" value for the current server entry.
     */
    private String hostname = null;

    /**
     * The "players" value for the current server entry.
     */
    private int players = 0;

    /**
     * The "server version" value for the current server entry.
     */
    private String version = UNKNOWN_VERSION;

    /**
     * The "comment" value for the current server entry. html_command is
     * preferred over text_comment.
     */
    private String comment = "";

    /**
     * The "bytes in" value for the current server entry.
     */
    private long bytesIn = 0;

    /**
     * The "bytes out" value for the current server entry.
     */
    private long bytesOut = 0;

    /**
     * The "uptime seconds" value for the current server entry.
     */
    private int uptimeSeconds = 0;

    /**
     * The "archetype base" value for the current server entry.
     */
    private String archbase = DEFAULT_BASE;

    /**
     * The "map base" value for the current server entry.
     */
    private String mapbase = DEFAULT_BASE;

    /**
     * The "code base" value for the current server entry.
     */
    private String codebase = DEFAULT_BASE;

    /**
     * Creates a new instance.
     */
    public MetaserverEntryParser()
    {
        clear();
    }

    /**
     * Parses a metaserver response line.
     * @param entry the response line to parse
     * @return the metaserver entry or <code>null</code> if the line is
     * invalid
     */
    public static MetaserverEntry parseEntry(final String entry)
    {
        final String[] entries = fieldSeparatorPattern.split(entry, -1);
        if (entries.length != 11)
        {
            return null;
        }

        final int updateSeconds;
        final String hostname;
        final int players;
        final String version;
        final String comment;
        final long bytesIn;
        final long bytesOut;
        final int uptimeSeconds;
        final String archbase;
        final String mapbase;
        final String codebase;
        try
        {
            updateSeconds = Integer.parseInt(entries[0]);
            hostname = entries[1];
            players = Integer.parseInt(entries[2]);
            version = entries[3];
            comment = entries[4];
            bytesIn = Long.parseLong(entries[5]);
            bytesOut = Long.parseLong(entries[6]);
            uptimeSeconds = Integer.parseInt(entries[7]);
            archbase = entries[8];
            codebase = entries[9];
            mapbase = entries[10];
        }
        catch (final NumberFormatException ex)
        {
            return null;
        }

        return new MetaserverEntry(updateSeconds, hostname, players, version, comment, bytesIn, bytesOut, uptimeSeconds, archbase, codebase, mapbase);
    }

    /**
     * Parses a metaserver response line.
     * @param line the response line to parse
     * @return the metaserver entry, or <code>null</code> if the line is
     * invalid
     * @throws IOException if the response line is invalid
     */
    public MetaserverEntry parseLine(final String line) throws IOException
    {
        if (!inSection)
        {
            if (line.equals("START_SERVER_DATA"))
            {
                inSection = true;
            }
            else
            {
                throw new IOException("syntax error: "+line);
            }
        }
        else
        {
            if (line.equals("END_SERVER_DATA"))
            {
                final MetaserverEntry metaserverEntry;
                if (hostname == null)
                {
                    System.err.println("Warning: metaserver response missing hostname field, skipping");
                    metaserverEntry = null;
                }
                else
                {
                    metaserverEntry = new MetaserverEntry(updateSeconds, hostname, players, version, comment, bytesIn, bytesOut, uptimeSeconds, archbase, mapbase, codebase);
                }
                clear();
                inSection = false;
                return metaserverEntry;
            }
            else
            {
                final String[] tmp = line.split("=", 2);
                if (tmp.length == 2)
                {
                    final String key = tmp[0];
                    final String value = tmp[1];
                    if (key.equals("hostname"))
                    {
                        hostname = value;
                    }
                    else if (key.equals("port"))
                    {
                    }
                    else if (key.equals("html_comment"))
                    {
                        comment = value;
                    }
                    else if (key.equals("text_comment"))
                    {
                        if (comment.length() == 0)
                        {
                            comment = value;
                        }
                    }
                    else if (key.equals("archbase"))
                    {
                        archbase = value;
                    }
                    else if (key.equals("mapbase"))
                    {
                        mapbase = value;
                    }
                    else if (key.equals("codebase"))
                    {
                        codebase = value;
                    }
                    else if (key.equals("num_players"))
                    {
                        players = NumberParser.parseInt(value, 0);
                    }
                    else if (key.equals("in_bytes"))
                    {
                        bytesIn = NumberParser.parseLong(value, 0);
                    }
                    else if (key.equals("out_bytes"))
                    {
                        bytesOut = NumberParser.parseLong(value, 0);
                    }
                    else if (key.equals("uptime"))
                    {
                        uptimeSeconds = NumberParser.parseInt(value, 0);
                    }
                    else if (key.equals("version"))
                    {
                        version = value;
                    }
                    else if (key.equals("sc_version"))
                    {
                    }
                    else if (key.equals("cs_version"))
                    {
                    }
                    else if (key.equals("last_update"))
                    {
                        final long now = (System.currentTimeMillis()+500)/1000;
                        final long uptime = NumberParser.parseLong(value, now);
                        updateSeconds = Math.max((int)((uptime-now)/1000), 0);
                    }
                    else
                    {
                        System.err.println("Ignoring unknown key: "+key);
                    }
                }
                else
                {
                    throw new IOException("syntax error: "+line);
                }
            }
        }

        return null;
    }

    /**
     * Resets values for the current server entry. Will be called whenever
     * parsing of a new entry starts.
     */
    private void clear()
    {
        updateSeconds = 0;
        hostname = null;
        players = 0;
        version = UNKNOWN_VERSION;
        comment = "";
        bytesIn = 0;
        bytesOut = 0;
        uptimeSeconds = 0;
        archbase = DEFAULT_BASE;
        mapbase = DEFAULT_BASE;
        codebase = DEFAULT_BASE;
    }

    /**
     * Format a metaserver entry that returns the metaserver entry when parse
     * with {@link #parseEntry(String)}.
     *
     * @param entry The metaserver entry to format.
     *
     * @return The formatted entry.
     */
    public static String format(final MetaserverEntry entry)
    {
        return entry.getUpdateSeconds()+"|"+replace(entry.getHostname())+"|"+entry.getPlayers()+"|"+replace(entry.getVersion())+"|"+replace(entry.getComment())+"|"+entry.getBytesIn()+"|"+entry.getBytesOut()+"|"+entry.getUptimeSeconds()+"|"+replace(entry.getArchbase())+"|"+replace(entry.getCodebase())+"|"+replace(entry.getMapbase());
    }

    /**
     * Replace characters with may cause parsing issues.
     *
     * @param str The string to replace.
     *
     * @return The replaced string.
     */
    private static String replace(final String str)
    {
        return str.replaceAll("[\\|\r\n]", " ");
    }
}
