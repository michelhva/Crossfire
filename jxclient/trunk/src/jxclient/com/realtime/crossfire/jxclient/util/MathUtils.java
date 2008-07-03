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
 * Utility class for mathematical functions.
 * @author Andreas Kirschbaum
 */
public class MathUtils
{
    /**
     * Private constructor to prevent instantiation.
     */
    private MathUtils()
    {
    }

    /**
     * Calculates \floor(<code>a/b</code>).
     * @param numerator the numerator
     * @param denominator the denominator
     * @return the quotient
     */
    public static int div(final int numerator, final int denominator)
    {
        if (numerator >= 0)
        {
            return numerator/denominator;
        }
        else
        {
            return -((denominator-1-numerator)/denominator);
        }
    }
}
