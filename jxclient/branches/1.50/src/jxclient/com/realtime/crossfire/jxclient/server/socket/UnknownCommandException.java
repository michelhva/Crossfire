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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.server.socket;

import com.realtime.crossfire.jxclient.util.HexCodec;
import org.jetbrains.annotations.NotNull;

/**
 * An UnknownCommandException is generated whenever an unknown message packet is
 * received from the server.
 * @author Lauwenmark
 * @version 1.0
 * @since 1.0
 */
public class UnknownCommandException extends Exception {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Details about the unparsable command.
     */
    @NotNull
    private String details = "";

    /**
     * Creates a new exception with the given text message as a parameter.
     * @param str the message to assign to this exception
     */
    public UnknownCommandException(@NotNull final String str) {
        super(str);
    }

    /**
     * Returns details about the unparsable command.
     * @return the details
     */
    @NotNull
    public String getDetails() {
        return details;
    }

    /**
     * Set the detail information.
     * @param data contains the raw data bytes of the command
     * @param start the starting index into <code>data</code>
     * @param end the end index into <code>data</code>
     */
    public void setDetails(@NotNull final byte[] data, final int start, final int end) {
        details = HexCodec.hexDump(data, start, end-start);
    }

}
