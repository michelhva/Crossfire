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

package com.realtime.crossfire.jxclient.util;

import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for manipulating filenames.
 * @author Andreas Kirschbaum
 */
public class Filenames
{
    /**
     * Replaces "unsafe" characters in file names (see {@link
     * #UNSAFE_FILENAME_CHARACTERS}).
     */
    @NotNull
    private static final String REPLACEMENT_CHARACTER = "_";

    /**
     * Matches all chracters that are considered "unsafe" for file names. These
     * characters will be replaced with {@link #REPLACEMENT_CHARACTER}.
     */
    @NotNull
    private static final Pattern UNSAFE_FILENAME_CHARACTERS = Pattern.compile("[^a-zA-Z0-9_.]");

    /**
     * Private constructor to prevent instantiation.
     */
    private Filenames()
    {
    }

    /**
     * Converts a file name to a "safe" form. The returned file name will not
     * contain any "unsafe" characters (see {@link #UNSAFE_FILENAME_CHARACTERS}),
     * and it will not be empty.
     * @param name the file name to convert
     * @return the converted file name
     */
    @NotNull
    public static String quoteName(@NotNull final String name)
    {
        final CharSequence trimmedName = name.endsWith(".png") ? name.substring(0, name.length()-4) : name;
        final String replacedName = UNSAFE_FILENAME_CHARACTERS.matcher(trimmedName).replaceAll(REPLACEMENT_CHARACTER);
        return replacedName.length() > 0 ? replacedName : REPLACEMENT_CHARACTER;
    }
}
