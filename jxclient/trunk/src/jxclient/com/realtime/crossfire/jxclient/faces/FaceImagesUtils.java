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

import javax.swing.ImageIcon;

/**
 * Utility class for creating {@link FaceImages} instances.
 * @author Andreas Kirschbaum
 */
public class FaceImagesUtils
{
    /**
     * Private constructor to prevent instantiation.
     */
    private FaceImagesUtils()
    {
    }

    /**
     * Creates a new {@link FaceImages} instance from an "original" face; the
     * "scaled" and "magic map" sized images are calculated.
     * @param originalImageIcon the original face
     * @return the face images instance
     */
    public static FaceImages newFaceImages(final ImageIcon originalImageIcon)
    {
        final ImageIcon scaledImageIcon = new ImageScale2x(originalImageIcon).getScaledImage();
        final ImageIcon magicMapImageIcon = new ImageScale8d(originalImageIcon).getScaledImage();
        return new FaceImages(originalImageIcon, scaledImageIcon, magicMapImageIcon);
    }
}
