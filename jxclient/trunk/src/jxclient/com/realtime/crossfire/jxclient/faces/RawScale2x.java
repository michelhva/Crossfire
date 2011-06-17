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

import org.jetbrains.annotations.NotNull;

/**
 * A simple implementation of the Scale2x algorithm for scaling raw image data.
 * @author Kevin Glass
 */
public class RawScale2x {

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
     * Creates a new scaler based on some raw data. Right now it doesn't matter
     * what order the channels in, just that its an int per pixel
     * @param imageData the source image data
     * @param dataWidth the width of the source image
     * @param dataHeight the height of the source image
     */
    public RawScale2x(@NotNull final int[] imageData, final int dataWidth, final int dataHeight) {
        width = dataWidth;
        height = dataHeight;
        srcImage = imageData;
        dstImage = new int[imageData.length*4];
    }

    /**
     * Checks if two pixels are different. Place holder for maybe some clever
     * code about tolerance checking.
     * @param a the first pixel value
     * @param b the second pixel value
     * @return <code>true</code> if the pixels are different
     */
    private static boolean different(final int a, final int b) {
        return a != b;
    }

    /**
     * Sets a pixel in the destination image data.
     * @param x the x location of the pixel to set
     * @param y the y location of the pixel to set
     * @param p the value of the pixel to set
     */
    private void setDestPixel(final int x, final int y, final int p) {
        dstImage[x+y*width*2] = p;
    }

    /**
     * Gets a pixel from the source image. This handles bonds checks and
     * resolves to edge pixels.
     * @param x the x location of the pixel to retrieve
     * @param y the y location of the pixel to retrieve
     * @return the pixel value at the specified location
     */
    private int getSourcePixel(final int x, final int y) {
        final int xx = Math.min(width-1, Math.max(0, x));
        final int yy = Math.min(height-1, Math.max(0, y));
        return srcImage[xx+yy*width];
    }

    /**
     * Processes a specific pixel. This will generate 4 pixels in the
     * destination image based on the scale2x algorithm.
     * @param x the x location in the source image of the pixel to process
     * @param y the y location in the source image of the pixel to process
     */
    private void process(final int x, final int y) {
        //final int a = getSourcePixel(x-1, y-1);
        final int b = getSourcePixel(x, y-1);
        //final int c = getSourcePixel(x+1, y-1);
        final int d = getSourcePixel(x-1, y);
        final int e = getSourcePixel(x, y);
        final int f = getSourcePixel(x+1, y);
        //final int g = getSourcePixel(x-1, y+1);
        final int h = getSourcePixel(x, y+1);
        //final int i = getSourcePixel(x+1, y+1);
        final int e0;
        final int e1;
        final int e2;
        final int e3;
        if (different(b, h) && different(d, f)) {
            e0 = different(d, b) ? e : d;
            e1 = different(b, f) ? e : f;
            e2 = different(d, h) ? e : d;
            e3 = different(h, f) ? e : f;
        } else {
            e0 = e;
            e1 = e;
            e2 = e;
            e3 = e;
        }

        setDestPixel(x*2, y*2, e0);
        setDestPixel(x*2+1, y*2, e1);
        setDestPixel(x*2, y*2+1, e2);
        setDestPixel(x*2+1, y*2+1, e3);
    }

    /**
     * Gets the scale image data. Note this is the method that does the work so
     * it might take some time to process.
     * @return an array of pixels 4 times the size of the input array containing
     *         the smoothly scaled image
     */
    @NotNull
    public int[] getScaledData() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                process(x, y);
            }
        }

        return dstImage;
    }

}
