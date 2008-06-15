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
 * Utilit class for en-/decoding hexadecimal strings.
 * @author Andreas Kirschbaum
 */
public class HexCodec
{
    /**
     * Hex characters for values 0-15.
     */
    private static final String HEX_CHARS = "0123456789abcdef";

    /**
     * Private constructor to prevent instantiation.
     */
    private HexCodec()
    {
    }

    /**
     * Append a given value as a two digits hexadecimal number.
     * @param sb the <code>StringBuilder</code> to append to
     * @param value the value to append
     */
    public static void hexEncode2(final StringBuilder sb, final int value)
    {
        sb.append(HEX_CHARS.charAt((value>>4)&15));
        sb.append(HEX_CHARS.charAt(value&15));
    }

    /**
     * Append a given value as a foure digits hexadecimal number.
     * @param sb the <code>StringBuilder</code> to append to
     * @param value the value to append
     */
    public static void hexEncode4(final StringBuilder sb, final int value)
    {
        hexEncode2(sb, value>>16);
        hexEncode2(sb, value);
    }

    /**
     * Returns a hex dump of a part of a byte array.
     * @param data the byte array to dump
     * @param start the starting index
     * @param end the end index
     * @return the hex dump
     */
    public static String hexDump(final byte[] data, final int start, final int end)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i += 16)
        {
            hexEncode4(sb, i-start);
            sb.append(':');
            for (int j = i; j < i+16 && j < end; j++)
            {
                sb.append(' ');
                hexEncode2(sb, data[j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
