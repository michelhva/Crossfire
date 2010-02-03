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

package com.realtime.crossfire.jxclient.faces;

import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

/**
 * A utility to scale down an image by 8 in both dimensions.
 *
 * @author Andreas Kirschbaum
 */
public class ImageScale8d
{
    /**
     * The source data from the image.
     */
    @NotNull
    private final int[] srcData;

    /**
     * The width of the image.
     */
    private final int width;

    /**
     * The height of the image.
     */
    private final int height;

    /**
     * Creates a new instance.
     * @param srcImageIcon the image to scale
     */
    public ImageScale8d(@NotNull final Icon srcImageIcon)
    {
        width = srcImageIcon.getIconWidth();
        height = srcImageIcon.getIconHeight();

        srcData = new int[width*height];
        final BufferedImage srcBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImageIcon.paintIcon(null, srcBufferedImage.getGraphics(), 0, 0);
        srcBufferedImage.getRGB(0, 0, width, height, srcData, 0, width);
    }

    /**
     * Returns the scaled image. Note this is the method that actually does the
     * work so it may take some time to return.
     * @return the scaled image
     */
    @NotNull
    public ImageIcon getScaledImage()
    {
        final RawScale8d scaler = new RawScale8d(srcData, width, height);

        final BufferedImage image = new BufferedImage(width/8, height/8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width/8, height/8, scaler.getScaledData(), 0, width/8);

        return new ImageIcon(image);
    }
}
