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

import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a response line from the metaserver.
 * @author Andreas Kirschbaum
 */
public class MetaserverEntry implements Comparable<MetaserverEntry> {

    /**
     * Matches html tags.
     */
    @NotNull
    private static final Pattern HTML_TAG_MATCHER = Pattern.compile("<[^>]*>");

    private final int updateSeconds;

    @NotNull
    private final String hostname;

    private final int players;

    @NotNull
    private final String version;

    @NotNull
    private final String comment;

    private final long bytesIn;

    private final long bytesOut;

    private final int uptimeSeconds;

    @NotNull
    private final String archBase;

    @NotNull
    private final String mapBase;

    @NotNull
    private final String codeBase;

    public MetaserverEntry(final int updateSeconds, @NotNull final String hostname, final int players, @NotNull final String version, @NotNull final String comment, final long bytesIn, final long bytesOut, final int uptimeSeconds, @NotNull final String archBase, @NotNull final String mapBase, @NotNull final String codeBase) {
        this.updateSeconds = updateSeconds;
        this.hostname = hostname;
        this.players = players;
        this.version = version;
        this.comment = comment;
        this.bytesIn = bytesIn;
        this.bytesOut = bytesOut;
        this.uptimeSeconds = uptimeSeconds;
        this.archBase = archBase;
        this.mapBase = mapBase;
        this.codeBase = codeBase;
    }

    public int getUpdateSeconds() {
        return updateSeconds;
    }

    @NotNull
    public String getHostname() {
        return hostname;
    }

    public int getPlayers() {
        return players;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    @NotNull
    public String getComment() {
        return comment;
    }

    public long getBytesIn() {
        return bytesIn;
    }

    public long getBytesOut() {
        return bytesOut;
    }

    public int getUptimeSeconds() {
        return uptimeSeconds;
    }

    @NotNull
    public String getArchBase() {
        return archBase;
    }

    @NotNull
    public String getMapBase() {
        return mapBase;
    }

    @NotNull
    public String getCodeBase() {
        return codeBase;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString() {
        return "Host:"+hostname+" Version:"+version+" Players:"+players+" Comment:"+comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NotNull final MetaserverEntry o) {
        return hostname.compareTo(o.hostname);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hostname.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final MetaserverEntry m = (MetaserverEntry)obj;
        return m.hostname.equals(hostname);
    }

    /**
     * Return a formatted string using the given format.
     * <p/>
     * Supported format strings: <ul> <li>%% - a literal % character <li>%A -
     * arch base <li>%C - comment <li>%E - code base <li>%H - hostname <li>%I -
     * bytes in <li>%M - map base <li>%O - bytes out <li>%P - number of players
     * <li>%T - uptime <li>%U - time since last update <li>%V - server version
     * </ul>
     * @param format The format.
     * @return The formatted string.
     */
    @NotNull
    public String format(@NotNull final String format) {
        final StringBuilder sb = new StringBuilder();
        final char[] formatChars = format.toCharArray();
        int i = 0;
        while (i < formatChars.length) {
            final char ch = formatChars[i++];
            if (ch != '%' || i >= formatChars.length) {
                sb.append(ch);
            } else {
                switch (formatChars[i++]) {
                case '%':
                    sb.append('%');
                    break;

                case 'A':
                    sb.append(archBase);
                    break;

                case 'C':
                    sb.append(comment);
                    break;

                case 'D':
                    sb.append(HTML_TAG_MATCHER.matcher(comment).replaceAll(" "));
                    break;

                case 'E':
                    sb.append(codeBase);
                    break;

                case 'H':
                    sb.append(hostname);
                    break;

                case 'I':
                    sb.append(bytesIn);
                    break;

                case 'M':
                    sb.append(mapBase);
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
                    sb.append(formatChars[i-1]);
                    break;
                }
            }
        }
        return sb.toString();
    }

}
