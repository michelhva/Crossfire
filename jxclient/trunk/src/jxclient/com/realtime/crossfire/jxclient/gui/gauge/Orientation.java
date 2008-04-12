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
package com.realtime.crossfire.jxclient.gui.gauge;

/**
 * Interface for orientation images.
 * @author Andreas Kirschbaum
 */
public interface Orientation
{
    /**
     * Sets whether the gauge can display negative images. By default negative
     * images are not supported.
     * @param hasNegativeImage whether the gauge can display negative images
     */
    void setHasNegativeImage(boolean hasNegativeImage);

    /**
     * Sets the gauge's values.
     * @param cur the current value
     * @param min the minimum value
     * @param max the maximum value
     */
    boolean setValues(int cur, int min, int max);

    /**
     * Sets the extends of the image.
     * @param width the width
     * @param height the height
     */
    void setExtends(int width, int height);

    /**
     * Returns the x-coordinate of the highlighted part of the image.
     * @return the x-coordinate
     */
    int getX();

    /**
     * Returns the y-coordinate of the highlighted part of the image.
     * @return the y-coordinate
     */
    int getY();

    /**
     * Returns the width of the highlighted part of the image.
     * @return the width
     */
    int getW();

    /**
     * Returns the height of the highlighted part of the image.
     * @return the height
     */
    int getH();

    /**
     * Returns whether the negative image should be shown.
     * @return whether the negative image should be shown
     */
    boolean isNegativeImage();

    /**
     * Returns whether the gauge's values are valid.
     * @return whether the values are valid
     */
    boolean isValid();
}
