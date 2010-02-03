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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import javax.swing.ImageIcon;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static FaceImages newFaceImages(@NotNull final ImageIcon originalImageIcon)
    {
        final ImageIcon scaledImageIcon = new ImageScale2x(originalImageIcon).getScaledImage();
        final ImageIcon magicMapImageIcon = new ImageScale8d(originalImageIcon).getScaledImage();
        return new FaceImages(originalImageIcon, scaledImageIcon, magicMapImageIcon);
    }

    /**
     * Creates a new {@link FaceImages} instance consisting of empty imgaes.
     * @return the face images instance
     */
    @NotNull
    public static FaceImages newEmptyFaceImages()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();

        final ImageIcon emptyOriginalImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE, Face.SQUARE_SIZE, Transparency.OPAQUE));
        final ImageIcon emptyScaledImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE*2, Face.SQUARE_SIZE*2, Transparency.OPAQUE));
        final ImageIcon emptyMagicMapImageIcon = new ImageIcon(gconf.createCompatibleImage(Face.SQUARE_SIZE/8, Face.SQUARE_SIZE/8, Transparency.OPAQUE));
        return new FaceImages(emptyOriginalImageIcon, emptyScaledImageIcon, emptyMagicMapImageIcon);
    }
}
