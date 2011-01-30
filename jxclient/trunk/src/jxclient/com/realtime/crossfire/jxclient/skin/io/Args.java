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

package com.realtime.crossfire.jxclient.skin.io;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * Iterates over a list of <code>String</code> arguments.
 * @author Andreas Kirschbaum
 */
public class Args {

    /**
     * The string arguments.
     */
    @NotNull
    private final String[] args;

    /**
     * The current index into {@link #args}.
     */
    private int index = 0;

    /**
     * Creates a new instance.
     * @param args the string arguments
     */
    public Args(@NotNull final String[] args) {
        this.args = args;
    }

    /**
     * Returns the next argument.
     * @return the next argument
     * @throws IOException if no next argument exists
     */
    @NotNull
    public String get() throws IOException {
        try {
            return args[index++];
        } catch (final ArrayIndexOutOfBoundsException ignored) {
            throw new IOException("missing argument");
        }
    }

    /**
     * Returns the current argument. This is the same value that was returned by
     * the preceding call to {@link #get()}.
     * @return the current argument
     */
    @NotNull
    public String getPrev() {
        return args[index-1];
    }

    /**
     * Returns whether more arguments exist.
     * @return whether more arguments exist
     */
    public boolean hasMore() {
        return index < args.length;
    }

}
