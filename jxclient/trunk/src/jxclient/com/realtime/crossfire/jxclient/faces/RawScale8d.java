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
package com.realtime.crossfire.jxclient.faces;

import java.util.HashMap;
import java.util.Map;

/**
 * Scale down a raw image to an eigth in both dimensions.
 *
 * @author Andreas Kirschbaum
 */
public class RawScale8d
{
    /** The source image data. */
    private final int[] srcImage;

    /** The destination image data. */
    private final int[] dstImage;

    /** The width of the source image. */
    private final int width;

    /** The height of the source image. */
    private final int height;

    /**
     * Maps pixel value to number of pixels.
     */
    private final Map<Integer, Integer> pixels = new HashMap<Integer, Integer>();

    /**
     * Create a new instance.
     *
     * @param imageData The source image data to process.
     *
     * @param dataWidth The width of the source image.
     *
     * @param dataHeight The height of the source image.
     */
    public RawScale8d(final int[] imageData, final int dataWidth, final int dataHeight)
    {
        this.width = dataWidth;
        this.height = dataHeight;
        this.srcImage = imageData;
        dstImage = new int[imageData.length*4];
    }

    /**
     * Set a pixel in the destination image data.
     *
     * @param x The x location of the pixel to set.
     *
     * @param y The y location of the pixel to set.
     *
     * @param p The value of the pixel to set.
     */
    private void setDestPixel(final int x, final int y, final int p)
    {
        dstImage[x+(y*width/8)] = p;
    }

    /**
     * Get a pixel from the source image.
     *
     * @param x The x location of the pixel to retrieve.
     *
     * @param y The y location of the pixel to retrieve.
     *
     * @return The pixel value at the specified location.
     */
    private int getSourcePixel(final int x, final int y)
    {
        return srcImage[x+y*width];
    }

    /**
     * Check if two pixels are different. Place holder for maybe some clever
     * code about tolerance checking.
     *
     * @param a The first pixel value.
     *
     * @param b The second pixel value.
     *
     * @return <code>true</code> if the pixels are different.
     */
    private static boolean different(final int a, final int b)
    {
        return a != b;
    }

    /**
     * Process a specific destination pixel.
     *
     * @param x The x location in the source image of the pixel to process.
     *
     * @param y The y location in the source image of the pixel to process.
     */
    private void process(final int x, final int y)
    {
        pixels.clear();
        for (int dx = 0; dx < 8; dx++)
        {
            for (int dy = 0; dy < 8; dy++)
            {
                final int value = getSourcePixel(8*x+dx, 8*y+dy);
                final Integer count = pixels.get(value);
                pixels.put(value, count == null ? 1 : count+1);
            }
        }
        int maxCount = 0;
        int maxPixel = 0;
        for (final Map.Entry<Integer, Integer> e : pixels.entrySet())
        {
            final int thisCount = e.getValue();
            if (thisCount > maxCount)
            {
                maxCount = thisCount;
                maxPixel = e.getKey();
            }
        }
        assert maxCount > 0;
        pixels.clear();

        setDestPixel(x, y, maxPixel);
    }

    /**
     * Get the scale image data. Note this is the method that does the work so
     * it might take some time to process.
     *
     * @return An array of pixels 64 times smaller than the input array
     * containing the scaled down image.
     */
    public int[] getScaledData()
    {
        for (int x = 0; x < width/8; x++)
        {
            for (int y = 0; y < height/8; y++)
            {
                process(x, y);
            }
        }

        return dstImage;
    }
}
