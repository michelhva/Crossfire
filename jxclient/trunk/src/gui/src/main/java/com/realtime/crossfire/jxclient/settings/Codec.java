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

package com.realtime.crossfire.jxclient.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to encode arbitrary Strings to fit in a single text line. For
 * any string <code>s</code>, <code>encode(s)</code> is a string that does not
 * contain \r or \n characters and <code>s.equals(decode(encode(s)))</code>
 * holds.
 * @author Andreas Kirschbaum
 */
public class Codec {

    /**
     * Patterns that must be encoded. The corresponding replacement strings are
     * {@link #REPLACEMENTS_ENCODE}.
     */
    @NotNull
    private static final Pattern[] PATTERNS_ENCODE = {
        Pattern.compile("\\\\"),
        Pattern.compile("\r"),
        Pattern.compile("\n"),
    };

    /**
     * The replacement strings for {@link #PATTERNS_ENCODE}.
     */
    @NotNull
    private static final String[] REPLACEMENTS_ENCODE = {
        Matcher.quoteReplacement("\\\\"),
        Matcher.quoteReplacement("\\r"),
        Matcher.quoteReplacement("\\n"),
    };

    /**
     * Patterns that must be decoded. The corresponding replacement strings are
     * {@link #REPLACEMENTS_DECODE}.
     */
    @NotNull
    private static final Pattern[] PATTERNS_DECODE = {
        Pattern.compile("\\\\n"),
        Pattern.compile("\\\\r"),
        Pattern.compile("\\\\\\\\"),
    };

    /**
     * The replacement strings for {@link #PATTERNS_DECODE}.
     */
    @NotNull
    private static final String[] REPLACEMENTS_DECODE = {
        Matcher.quoteReplacement("\n"),
        Matcher.quoteReplacement("\r"),
        Matcher.quoteReplacement("\\"),
    };

    /**
     * Private constructor to prevent instantiation.
     */
    private Codec() {
    }

    /**
     * Encodes a string to make it fit into one line.
     * @param str the string to be encoded
     * @return the encoded string
     * @see #decode(String)
     */
    @NotNull
    public static String encode(@NotNull final String str) {
        assert PATTERNS_ENCODE.length == REPLACEMENTS_ENCODE.length;
        String tmp = str;
        for (int i = 0; i < PATTERNS_ENCODE.length; i++) {
            tmp = PATTERNS_ENCODE[i].matcher(tmp).replaceAll(REPLACEMENTS_ENCODE[i]);
        }
        return tmp;
    }

    /**
     * Decodes a string which was encoded by {@link #encode(String)}.
     * @param str the string to be decoded
     * @return the decoded string
     * @see #encode(String)
     */
    @NotNull
    public static String decode(@NotNull final String str) {
        assert PATTERNS_DECODE.length == REPLACEMENTS_DECODE.length;
        String tmp = str;
        for (int i = 0; i < PATTERNS_DECODE.length; i++) {
            tmp = PATTERNS_DECODE[i].matcher(tmp).replaceAll(REPLACEMENTS_DECODE[i]);
        }
        return tmp;
    }

}
