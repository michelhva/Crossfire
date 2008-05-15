package com.realtime.crossfire.jxclient.util;

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
}
