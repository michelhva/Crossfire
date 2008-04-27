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
package com.realtime.crossfire.jxclient;

/**
 * Information about jxclient's screen/window resolution. It consists of a size
 * (width and height) and whether the exact resolution should be used, or if a
 * similar resolution is allowed.
 * @author Andreas Kirschbaum
 */
public class Resolution
{
    /**
     * Whether the resolution is exact (<code>true</code>), or a choosing
     * similar resolution if allowed (<code>false</code>).
     */
    private final boolean exact;

    /**
     * The width in pixel.
     */
    private final int width;

    /**
     * The height in pixel.
     */
    private final int height;

    /**
     * Creates a new instance.
     * @param exact whether the resolution is exact (<code>true</code>), or a
     * choosing similar resolution if allowed (<code>false</code>)
     * @param width the width in pixels
     * @param height the height in pixels
     */
    public Resolution(final boolean exact, final int width, final int height)
    {
        this.exact = exact;
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a new instance from string representation. The string
     * representation is of the format "1024x768"; it is the format returned
     * from {@link #toString()}.
     * @param exact whether the resolution is exact
     * @param str the string representation
     * @return the <code>Resolution instance</code>, or <code>null</code> if
     * the string representation is invalid
     */
    public static Resolution parse(final boolean exact, final String str)
    {
        final String[] tmp = str.split("x", -1);
        if (tmp.length != 2)
        {
            return null;
        }
        final int width;
        final int height;
        try
        {
            width = Integer.parseInt(tmp[0]);
            height = Integer.parseInt(tmp[1]);
        }
        catch (final NumberFormatException ex)
        {
            return null;
        }

        return new Resolution(exact, width, height);
    }

    /**
     * Returns whether the resolution is exact (<code>true</code>), or a
     * choosing similar resolution if allowed (<code>false</code>).
     * @return whether the resolution is exact
     */
    public boolean isExact()
    {
        return exact;
    }

    /**
     * Returns the width in pixels.
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height in pixels.
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Returns the area in pixels.
     * @return the area
     */
    public int getArea()
    {
        return width*height;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (obj.getClass() != Resolution.class) return false;
        final Resolution resolution = (Resolution)obj;
        return resolution.width == width && resolution.height == height;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return width^(height<<16)^(height>>16);
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return width+"x"+height;
    }
}
