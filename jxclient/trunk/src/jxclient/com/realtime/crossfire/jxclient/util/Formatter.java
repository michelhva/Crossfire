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

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for formatting values into strings.
 * @author Andreas Kirschbaum
 */
public class Formatter
{
    /**
     * Private constructor to prevent instantiation.
     */
    private Formatter()
    {
    }

    /**
     * Returns a <code>long</code> value formatted as a human readable string.
     * @param value the value
     * @return return the formatted value
     */
    @NotNull
    public static String formatLong(final long value)
    {
        if (value < 1000000L)
        {
            return Long.toString(value);
        }

        if (value < 10000000L)
        {
            final long tmp = (value+50000L)/100000L;
            return tmp/10+"."+tmp%10+" million";
        }

        if (value < 1000000000L)
        {
            final long tmp = (value+500000L)/1000000L;
            return tmp+" million";
        }

        if (value < 10000000000L)
        {
            final long tmp = (value+50000000L)/100000000L;
            return tmp/10+"."+tmp%10+" billion";
        }

         final long tmp = (value+500000000L)/1000000000L;
         return tmp+" billion";
    }

    /**
     * Formats a float value for display.
     * @param value the float value
     * @param digits the number of fraction digits; must be between 1..3
     * inclusive
     * @return the formatted value
     */
    @NotNull
    public static String formatFloat(final double value, final int digits)
    {
        final int tmp;
        switch (digits)
        {
        case 1:
            tmp = (int)(value*10+0.5);
            return tmp/10+"."+tmp%10;

        case 2:
            tmp = (int)(value*100+0.5);
            return tmp/100+"."+tmp/10%10+tmp%10;

        case 3:
            tmp = (int)(value*1000+0.5);
            return tmp/1000+"."+tmp/100%10+tmp/10%10+tmp%10;
        }

        throw new IllegalArgumentException();
    }
}
