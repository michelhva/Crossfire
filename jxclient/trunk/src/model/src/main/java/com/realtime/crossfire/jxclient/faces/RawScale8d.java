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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.faces;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Scales down a raw image to an eighth in both dimensions.
 * @author Andreas Kirschbaum
 */
public class RawScale8d {

    /**
     * The source image data.
     */
    @NotNull
    private final int[] srcImage;

    /**
     * The destination image data.
     */
    @NotNull
    private final int[] dstImage;

    /**
     * The width of the source image.
     */
    private final int width;

    /**
     * The height of the source image.
     */
    private final int height;

    /**
     * Maps pixel value to number of pixels.
     */
    @NotNull
    private final Map<Integer, Integer> pixels = new HashMap<>();

    /**
     * Creates a new instance.
     * @param imageData the source image data to process
     * @param dataWidth the width of the source image
     * @param dataHeight the height of the source image
     */
    public RawScale8d(@NotNull final int[] imageData, final int dataWidth, final int dataHeight) {
        width = dataWidth;
        height = dataHeight;
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        srcImage = imageData;
        dstImage = new int[imageData.length*4];
    }

    /**
     * Sets a pixel in the destination image data.
     * @param x the x location of the pixel to set
     * @param y the y location of the pixel to set
     * @param p the value of the pixel to set
     */
    private void setDestPixel(final int x, final int y, final int p) {
        dstImage[x+y*width/8] = p;
    }

    /**
     * Gets a pixel from the source image.
     * @param x the x location of the pixel to retrieve
     * @param y the y location of the pixel to retrieve
     * @return the pixel value at the specified location
     */
    private int getSourcePixel(final int x, final int y) {
        return srcImage[x+y*width];
    }

    /**
     * Processes a specific destination pixel.
     * @param x the x location in the source image of the pixel to process
     * @param y the y location in the source image of the pixel to process
     */
    private void process(final int x, final int y) {
        pixels.clear();
        for (int dx = 0; dx < 8; dx++) {
            for (int dy = 0; dy < 8; dy++) {
                final int value = getSourcePixel(8*x+dx, 8*y+dy);
                if (value != 0) {
                    final Integer count = pixels.get(value);
                    pixels.put(value, count == null ? 1 : count+1);
                }
            }
        }
        int maxCount = 0;
        int maxPixel = 0;
        for (Entry<Integer, Integer> e : pixels.entrySet()) {
            final int thisCount = e.getValue();
            if (thisCount > maxCount) {
                maxCount = thisCount;
                maxPixel = e.getKey();
            }
        }
        pixels.clear();

        setDestPixel(x, y, maxPixel);
    }

    /**
     * Returns the scale image data. Note this is the method that does the work
     * so it might take some time to process.
     * @return an array of pixels 64 times smaller than the input array
     * containing the scaled down image
     */
    @NotNull
    public int[] getScaledData() {
        for (int x = 0; x < width/8; x++) {
            for (int y = 0; y < height/8; y++) {
                process(x, y);
            }
        }

        //noinspection ReturnOfCollectionOrArrayField
        return dstImage;
    }

}
