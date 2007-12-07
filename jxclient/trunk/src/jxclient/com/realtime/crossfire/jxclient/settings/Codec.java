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
package com.realtime.crossfire.jxclient.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to encode arbitrary Strings to fit in a single text line. For
 * any string <code>s</code>, <code>encode(s)</code> is a string that does not
 * contain \r or \n characters and <code>s.equals(decode(encode(s)))</code>
 * holds.
 *
 * @author Andreas Kirschbaum
 */
public class Codec
{
    /**
     * Patterns that must be encoded. The corresponding replacement strings are
     * {@link #replacementsEncode}.
     */
    private static final Pattern[] patternsEncode =
    {
        Pattern.compile("\\\\"),
        Pattern.compile("\r"),
        Pattern.compile("\n"),
    };

    /**
     * The replacement strings for {@link #patternsEncode}.
     */
    private static final String[] replacementsEncode =
    {
        Matcher.quoteReplacement("\\\\"),
        Matcher.quoteReplacement("\\r"),
        Matcher.quoteReplacement("\\n"),
    };

    /**
     * Patterns that must be decoded. The corresponding replacement strings are
     * {@link #replacementsDecode}.
     */
    private static final Pattern[] patternsDecode =
    {
        Pattern.compile("\\\\n"),
        Pattern.compile("\\\\r"),
        Pattern.compile("\\\\\\\\"),
    };

    /**
     * The replacement strings for {@link #patternsDecode}.
     */
    private static final String[] replacementsDecode =
    {
        Matcher.quoteReplacement("\n"),
        Matcher.quoteReplacement("\r"),
        Matcher.quoteReplacement("\\"),
    };

    /**
     * Private constructor to prevent instantiation.
     */
    private Codec()
    {
    }

    /**
     * Encode a string to make it fit into one line.
     *
     * @param str The string to be encoded.
     *
     * @return The encoded string.
     *
     * @see #decode(String)
     */
    public static String encode(final String str)
    {
        assert patternsEncode.length == replacementsEncode.length;
        String tmp = str;
        for (int i = 0; i < patternsEncode.length; i++)
        {
            tmp = patternsEncode[i].matcher(tmp).replaceAll(replacementsEncode[i]);
        }
        return tmp;
    }

    /**
     * Decode a string which was encoded by {@link #encode(String)}.
     *
     * @param str The string to be decoded.
     *
     * @return The decoded string.
     *
     * @see #encode(String)
     */
    public static String decode(final String str)
    {
        assert patternsDecode.length == replacementsDecode.length;
        String tmp = str;
        for (int i = 0; i < patternsDecode.length; i++)
        {
            tmp = patternsDecode[i].matcher(tmp).replaceAll(replacementsDecode[i]);
        }
        return tmp;
    }
}
