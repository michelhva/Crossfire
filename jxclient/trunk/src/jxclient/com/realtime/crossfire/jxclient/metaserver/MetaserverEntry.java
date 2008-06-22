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
 * Represents a response line from the metaserver.
 *
 * @author Andreas Kirschbaum
 */
public class MetaserverEntry implements Comparable<MetaserverEntry>
{
    /**
     * Matches html tags.
     */
    private static final Pattern htmlTagMatcher = Pattern.compile("<[^>]*>");

    private final String ipAddress;

    private final int updateSeconds;

    private final String hostname;

    private final int players;

    private final String version;

    private final String comment;

    private final long bytesIn;

    private final long bytesOut;

    private final int uptimeSeconds;

    public MetaserverEntry(final String ipAddress, final int updateSeconds, final String hostname, final int players, final String version, final String comment, final long bytesIn, final long bytesOut, final int uptimeSeconds)
    {
        this.ipAddress = ipAddress;
        this.updateSeconds = updateSeconds;
        this.hostname = hostname;
        this.players = players;
        this.version = version;
        this.comment = comment;
        this.bytesIn = bytesIn;
        this.bytesOut = bytesOut;
        this.uptimeSeconds = uptimeSeconds;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public int getUpdateSeconds()
    {
        return updateSeconds;
    }

    public String getHostname()
    {
        return hostname;
    }

    public int getPlayers()
    {
        return players;
    }

    public String getVersion()
    {
        return version;
    }

    public String getComment()
    {
        return comment;
    }

    public long getBytesIn()
    {
        return bytesIn;
    }

    public long getBytesOut()
    {
        return bytesOut;
    }

    public int getUptimeSeconds()
    {
        return uptimeSeconds;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "IP:"+ipAddress+" Host:"+hostname+" Version:"+version+" Players:"+players+" Comment:"+comment;
    }

    /** {@inheritDoc} */
    public int compareTo(final MetaserverEntry o)
    {
        return hostname.compareTo(o.hostname);
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return hostname.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object o)
    {
        if(o == null) return false;
        if(o.getClass() != getClass()) return false;
        final MetaserverEntry m = (MetaserverEntry)o;
        return m.hostname.equals(hostname);
    }
    /**
     * Return a formatted string using the given format.
     *
     * <p>Supported format strings:
     * <ul>
     * <li>%% - a literal % character
     * <li>%A - ip address
     * <li>%C - comment
     * <li>%H - hostname
     * <li>%I - bytes in
     * <li>%O - bytes out
     * <li>%P - number of players
     * <li>%T - uptime
     * <li>%U - time since last update
     * <li>%V - server version
     * </ul>
     *
     * @param format The format.
     *
     * @return The formatted string.
     */
    public String format(final String format)
    {
        final StringBuilder sb = new StringBuilder();
        final char[] formatChars = format.toCharArray();
        for (int i = 0; i < formatChars.length; i++)
        {
            if (formatChars[i] != '%' || i+1 >= formatChars.length)
            {
                sb.append(formatChars[i]);
            }
            else
            {
                i++;
                switch (formatChars[i])
                {
                case '%':
                    sb.append('%');
                    break;

                case 'A':
                    sb.append(ipAddress);
                    break;

                case 'C':
                    sb.append(comment);
                    break;

                case 'D':
                    sb.append(htmlTagMatcher.matcher(comment).replaceAll(" "));
                    break;

                case 'H':
                    sb.append(hostname);
                    break;

                case 'I':
                    sb.append(bytesIn);
                    break;

                case 'O':
                    sb.append(bytesOut);
                    break;

                case 'P':
                    sb.append(players);
                    break;

                case 'U':
                    sb.append(updateSeconds);
                    break;

                case 'T':
                    sb.append(uptimeSeconds);
                    break;

                case 'V':
                    sb.append(version);
                    break;

                default:
                    sb.append('%');
                    sb.append(formatChars[i]);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
