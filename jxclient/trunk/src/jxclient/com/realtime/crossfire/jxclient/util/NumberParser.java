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
package com.realtime.crossfire.jxclient.util;

/**
 * Utitlity class for parsing strings into numbers.
 * @author Andreas Kirschbaum
 */
public class NumberParser
{
    /**
     * Private constructor to prevent instantiation.
     */
    private NumberParser()
    {
    }

    /**
     * Converts a string into an int value.
     * @param string the string to convert
     * @param defaultValue the value to return if the number is not a string
     * @return the int value
     */
    public static int parseInt(final String string, final int defaultValue)
    {
        try
        {
            return Integer.parseInt(string);
        }
        catch (final NumberFormatException ex)
        {
            System.err.println("Warning: invalid value "+string+", using "+defaultValue+" instead.");
            return defaultValue;
        }
    }

    /**
     * Converts a string into an int value in the given bounds.
     * @param string the string to convert
     * @param defaultValue the value to return if the number is not a string or
     * not within bounds
     * @param minValue the bound's minimum value
     * @param maxValue the bound's maximum value
     * @return the int value
     */
    public static int parseInt(final String string, final int defaultValue, final int minValue, final int maxValue)
    {
        final int value = parseInt(string, defaultValue);
        if (value < minValue || value > maxValue)
        {
            System.err.println("Warning: invalid value "+string+", using "+defaultValue+" instead.");
            return defaultValue;
        }

        return value;
    }

    /**
     * Converts a string into a long value.
     * @param string the string to convert
     * @param defaultValue the value to return if the number is not a string
     * @return the long value
     */
    public static long parseLong(final String string, final long defaultValue)
    {
        try
        {
            return Long.parseLong(string);
        }
        catch (final NumberFormatException ex)
        {
            System.err.println("Warning: invalid value "+string+", using "+defaultValue+" instead.");
            return defaultValue;
        }
    }
}
