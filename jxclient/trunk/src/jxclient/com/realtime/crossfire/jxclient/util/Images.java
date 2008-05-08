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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Utility class for manipulating images.
 * @author Andreas Kirschbaum
 */
public class Images
{
    /**
     * Private constructor to prevent instantiation.
     */
    private Images()
    {
    }

    /**
     * Saves an {@link ImageIcon} to a file. I/O errors are reported to
     * {@link System#err} but otherwise ignored.
     * @param outputFile the file to save to
     * @param imageIcon the image to save
     */
    public static void saveImageIcon(final File outputFile, final ImageIcon imageIcon)
    {
        final BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        imageIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        try
        {
            ImageIO.write(bufferedImage, "png", outputFile);
        }
        catch (final IOException ex)
        {
            System.err.println("Cannot write cache file "+outputFile+": "+ex.getMessage());
        }
    }
}
