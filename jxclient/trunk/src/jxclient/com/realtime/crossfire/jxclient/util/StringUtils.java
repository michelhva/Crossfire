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

import com.realtime.crossfire.jxclient.skin.JXCSkinException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for string manipulation.
 * @author Andreas Kirschbaum
 */
public class StringUtils
{
    /**
     * A pattern matching leading whitespace.
     */
    @NotNull
    private static final Pattern PATTERN_LEADING_WHITESPACE = Pattern.compile("^[ \t]+");

    /**
     * Private constructor to prevent instantiation.
     */
    private StringUtils()
    {
    }

    /**
     * Removes leading whitespace from a string.
     * @param str the string
     * @return the string without leading whitespace
     */
    @NotNull
    public static String trimLeading(@NotNull final CharSequence str)
    {
        return PATTERN_LEADING_WHITESPACE.matcher(str).replaceAll("");
    }

    /**
     * Splits a line into tokens. Handles quoting ("...").
     * @param line the line
     * @return the tokens
     * @throws JXCSkinException if the skin cannot be loaded
     */
    @NotNull
    public static String[] splitFields(@NotNull final String line) throws JXCSkinException
    {
        final List<String> tokens = new ArrayList<String>(64);

        final char[] chars = line.toCharArray();

        int i = 0;
        while (i < chars.length)
        {
            while (i < chars.length && (chars[i] == ' ' || chars[i] == '\t'))
            {
                i++;
            }
            final int start;
            final int end;
            if (i < chars.length && (chars[i] == '"' || chars[i] == '\''))
            {
                // quoted token
                final char quoteChar = chars[i++];
                start = i;
                while (i < chars.length && chars[i] != quoteChar)
                {
                    i++;
                }
                if (i >= chars.length)
                {
                    throw new JXCSkinException("unterminated token: "+line.substring(start-1));
                }
                end = i;
                i++;
            }
            else
            {
                // unquoted token
                start = i;
                while (i < chars.length && (chars[i] != ' ' && chars[i] != '\t'))
                {
                    i++;
                }
                end = i;
            }
            tokens.add(line.substring(start, end));
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}
