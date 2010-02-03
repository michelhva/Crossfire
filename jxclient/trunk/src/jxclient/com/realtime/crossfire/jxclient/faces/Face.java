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

import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A <code>Face</code> represents one image received from a Crossfire server.
 * The face is uniquely identified by a face id; it has a face name and three
 * images (original as sent by the server, scaled for use in map view, scaled
 * for use in magic map view) attached.
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Face
{
    /**
     * The size of one square in pixels.
     */
    public static final int SQUARE_SIZE = 32;

    /**
     * The face id as sent by the server.
     */
    private final int faceNum;

    /**
     * The face name as sent by the server.
     */
    @NotNull
    private final String faceName;

    /**
     * The image checksum as sent by the server.
     */
    private final int faceChecksum;

    /**
     * The face width in tiles.
     */
    private int tileWidth = 1;

    /**
     * The face height in tiles.
     */
    private int tileHeight = 1;

    /**
     * The images for this face. Set to <code>null</code> if unknown.
     */
    @Nullable
    private FaceImages faceImages = null;

    /**
     * Creates a new instance.
     * @param faceNum the unique face id
     * @param faceName the face name
     * @param faceChecksum the image checksum as sent by the server
     */
    public Face(final int faceNum, @NotNull final String faceName, final int faceChecksum)
    {
        this.faceNum = faceNum;
        this.faceName = faceName;
        this.faceChecksum = faceChecksum;
    }

    /**
     * Sets the images.
     * @param faceImages the images
     */
    public void setFaceImages(@NotNull final FaceImages faceImages)
    {
        this.faceImages = faceImages;
        final Icon imageIcon = faceImages.getOriginalImageIcon();
        final int width = imageIcon.getIconWidth();
        final int height = imageIcon.getIconHeight();
        tileWidth = (width+SQUARE_SIZE-1)/SQUARE_SIZE;
        tileHeight = (height+SQUARE_SIZE-1)/SQUARE_SIZE;
    }

    /**
     * Returns the unique face id.
     * @return the face id
     */
    public int getFaceNum()
    {
        return faceNum;
    }

    /**
     * Returns the images. May return <code>null</code> if the images are not
     * yet known, or if they have been dropped from the cache.
     * @return the images or <code>null</code>
     */
    @Nullable
    public FaceImages getFaceImages()
    {
        return faceImages;
    }

    /**
     * Returns the face name.
     * @return the face name
     */
    @NotNull
    public String getFaceName()
    {
        return faceName;
    }

    /**
     * Returns the face checksum.
     * @return the face checksum
     */
    public int getFaceChecksum()
    {
        return faceChecksum;
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String toString()
    {
        return faceName;
    }

    /**
     * Returns the face width in tiles.
     * @return the tile width
     */
    public int getTileWidth()
    {
        return tileWidth;
    }

    /**
     * Returns the face height in tiles.
     * @return the tile height
     */
    public int getTileHeight()
    {
        return tileHeight;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return faceChecksum;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (obj == null) return false;
        if (obj.getClass() != Face.class) return false;
        final Face face = (Face)obj;
        return faceNum == face.faceNum;
    }
}
