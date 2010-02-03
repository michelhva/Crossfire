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

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

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
    public static int parseInt(@NotNull final String string, final int defaultValue)
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
    public static int parseInt(@NotNull final String string, final int defaultValue, final int minValue, final int maxValue)
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
    public static long parseLong(@NotNull final String string, final long defaultValue)
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

    /**
     * Parses a float constant.
     * @param str the floating constant string to parse
     * @return the floating value
     * @throws IOException if a parsing error occurs
     */
    public static float parseFloat(@NotNull final String str) throws IOException
    {
        try
        {
            return Float.parseFloat(str);
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalid number: "+str);
        }
    }

    /**
     * Parses a boolean constant.
     * @param str the boolean constant string to parse
     * @return the boolean value
     * @throws IOException if a parsing error occurs
     */
    public static boolean parseBoolean(@NotNull final String str) throws IOException
    {
        try
        {
            return Boolean.parseBoolean(str);
        }
        catch (final NumberFormatException ex)
        {
            throw new IOException("invalid boolean: "+str);
        }
    }

    /**
     * Parses an enum constant.
     * @param class_ the enum class the enum constant belongs to
     * @param name the enum constant to parse
     * @param ident the description of the enum class for building error
     * messages
     * @return the enum constant
     * @throws IOException if the enum constant does not exist
     */
    @NotNull
    public static <T extends Enum<T>> T parseEnum(@NotNull final Class<T> class_, @NotNull final String name, @NotNull final String ident) throws IOException
    {
        try
        {
            return Enum.valueOf(class_, name);
        }
        catch (final IllegalArgumentException ex)
        {
            throw new IOException("no such "+ident+" type: "+name);
        }
    }
}
