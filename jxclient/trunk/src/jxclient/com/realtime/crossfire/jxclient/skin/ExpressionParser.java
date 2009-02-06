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
package com.realtime.crossfire.jxclient.skin;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for integer expressions.
 * @author Andreas Kirschbaum
 */
public class ExpressionParser
{
    /**
     * The identifier evaluating to the width in pixels of the current
     * resolution.
     */
    private static final String WIDTH = "WIDTH";

    /**
     * The identifier evaluating to the height in pixels of the current
     * resolution.
     */
    private static final String HEIGHT = "HEIGHT";

    /**
     * Pattern to parse integer constants.
     */
    private static final Pattern patternExpr = Pattern.compile("([0-9]+|"+WIDTH+"|"+HEIGHT+"|"+WIDTH+"/2|"+HEIGHT+"/2)([-+])(.+)");

    /**
     * The current resolution for {@link #WIDTH} and {@link #HEIGHT} constants.
     */
    private final Resolution resolution;

    /**
     * Creates a new instance.
     * @param resolution the current resolution
     */
    public ExpressionParser(final Resolution resolution)
    {
        this.resolution = resolution;
    }

    /**
     * Parses an integer constant. Valid constants are "3", "3+4", and
     * "1+2-3+4".
     * @param str the integer constant string to parse
     * @return the integer value
     * @throws IOException if a parsing error occurs
     */
    public int parseInt(final String str) throws IOException
    {
        try
        {
            return parseIntegerConstant(str);
        }
        catch (final NumberFormatException ex)
        {
            // ignore
        }

        Matcher matcher = patternExpr.matcher(str);
        if (!matcher.matches())
        {
            throw new IOException("invalid number: "+str);
        }
        int value;
        try
        {
            value = parseIntegerConstant(matcher.group(1));
            for (;;)
            {
                final boolean negative = matcher.group(2).equals("-");
                final String rest = matcher.group(3);

                matcher = patternExpr.matcher(rest);
                if (!matcher.matches())
                {
                    final int valueRest = Integer.parseInt(rest);
                    if (negative)
                    {
                        value -= valueRest;
                    }
                    else
                    {
                        value += valueRest;
                    }
                    break;
                }

                final int valueRest = parseIntegerConstant(matcher.group(1));
                if (negative)
                {
                    value -= valueRest;
                }
                else
                {
                    value += valueRest;
                }
            }
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalid number: "+str);
        }

        return value;
    }

    /**
     * Parses an integer constant string.
     * @param str the string
     * @return the integer value
     * @throws NumberFormatException if the string cannot be parsed
     */
    private int parseIntegerConstant(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException ex)
        {
            if (str.equals(WIDTH))
            {
                return resolution.getWidth();
            }

            if (str.equals(HEIGHT))
            {
                return resolution.getHeight();
            }

            if (str.equals(WIDTH+"/2"))
            {
                return resolution.getWidth()/2;
            }

            if (str.equals(HEIGHT+"/2"))
            {
                return resolution.getHeight()/2;
            }

            throw ex;
        }
    }
}
