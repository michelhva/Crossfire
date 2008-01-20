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

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * A utility to scale down an image by 8 in both dimensions.
 *
 * @author Andreas Kirschbaum
 */
public class ImageScale8d
{
    /** The src data from the image */
    private final int[] srcData;

    /** The width of the image */
    private final int width;

    /** The height of the image */
    private final int height;

    /**
     * Create a instance.
     *
     * @param srcImageIcon The image to scale.
     */
    public ImageScale8d(final ImageIcon srcImageIcon)
    {
        width = srcImageIcon.getIconWidth();
        height = srcImageIcon.getIconHeight();

        srcData = new int[width*height];
        final BufferedImage srcBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImageIcon.paintIcon(null, srcBufferedImage.getGraphics(), 0, 0);
        srcBufferedImage.getRGB(0, 0, width, height, srcData, 0, width);
    }

    /**
     * Retrieve the scaled image. Note this is the method that actually does
     * the work so it may take some time to return.
     *
     * @return The scaled image.
     */
    public ImageIcon getScaledImage()
    {
        final RawScale8d scaler = new RawScale8d(srcData, width, height);

        final BufferedImage image = new BufferedImage(width/8, height/8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width/8, height/8, scaler.getScaledData(), 0, width/8);

        return new ImageIcon(image);
    }
}
