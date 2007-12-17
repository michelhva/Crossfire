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

import java.util.regex.Pattern;

/**
 * Parser for response lines of metaserver response lines.
 *
 * @author Andreas Kirschbaum
 */
public class MetaserverEntryParser
{
    /**
     * The pattern to split a metaserver response line into fields.
     */
    private static final Pattern fieldSeparatorPattern = Pattern.compile("\\|");

    /**
     * Private constructor to prevent instantiation.
     */
    private MetaserverEntryParser()
    {
    }

    /**
     * Parse a metaserver response line.
     *
     * @param entry The response line to parse.
     *
     * @return The metaserver entry, or <code>null</code> if the line is
     * invalid.
     */
    public static MetaserverEntry parse(final String entry)
    {
        final String[] entries = fieldSeparatorPattern.split(entry, -1);
        if (entries.length != 9)
        {
            return null;
        }

        final String ipAddress;
        final int updateSeconds;
        final String hostname;
        final int players;
        final String version;
        final String comment;
        final long bytesIn;
        final long bytesOut;
        final int uptimeSeconds;
        try
        {
            ipAddress = entries[0];
            updateSeconds = Integer.parseInt(entries[1]);
            hostname = entries[2];
            players = Integer.parseInt(entries[3]);
            version = entries[4];
            comment = entries[5];
            bytesIn = Long.parseLong(entries[6]);
            bytesOut = Long.parseLong(entries[7]);
            uptimeSeconds = Integer.parseInt(entries[8]);
        }
        catch (final NumberFormatException ex)
        {
            return null;
        }

        return new MetaserverEntry(ipAddress, updateSeconds, hostname, players, version, comment, bytesIn, bytesOut, uptimeSeconds);
    }

    /**
     * Format a metaserver entry that returns the metaserver entry when parse
     * with {@link #parse(MetaserverEntry)}.
     *
     * @param entry The metaserver entry to format.
     *
     * @return The formatted entry.
     */
    public static String format(final MetaserverEntry entry)
    {
        return entry.getIpAddress()+"|"+entry.getUpdateSeconds()+"|"+replace(entry.getHostname())+"|"+entry.getPlayers()+"|"+replace(entry.getVersion())+"|"+replace(entry.getComment())+"|"+entry.getBytesIn()+"|"+entry.getBytesOut()+"|"+entry.getUptimeSeconds();
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
