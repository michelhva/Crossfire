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

import java.io.IOException;
import javax.swing.ImageIcon;

/**
 * Manage information for one face. The face is uniquely identified by a face
 * id, has a face name, and two images (original as sent by the server and
 * scaled for use in map view).
 *
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 */
public class Face
{
    /**
     * The image scaled to be used in map view.
     */
    private ImageIcon imageIcon;

    /**
     * The original (unscaled) image.
     */
    private ImageIcon originalImageIcon;

    /**
     * The face id as sent by the server.
     */
    private final int id;

    /**
     * The face name as sent by the server.
     */
    private String name;

    /**
     * Create a new face.
     *
     * @param id The unique face id.
     *
     * @param name The face name.
     *
     * @param imageIcon The image to use for map view.
     *
     * @param originalImageIcon The unscaled image as sent by the server.
     */
    public Face(final int id, final String name, final ImageIcon imageIcon, final ImageIcon originalImageIcon)
    {
        if (name == null) throw new IllegalArgumentException();
        if (imageIcon == null) throw new IllegalArgumentException();
        if (originalImageIcon == null) throw new IllegalArgumentException();

        this.id = id;
        this.name = name;
        this.imageIcon = imageIcon;
        this.originalImageIcon = originalImageIcon;
    }

    /**
     * Replace the images to use in map view.
     *
     * @param imageIcon The new image icon.
     */
    public void setImageIcon(final ImageIcon imageIcon)
    {
        if (imageIcon == null) throw new IllegalArgumentException();

        this.imageIcon = imageIcon;
    }

    /**
     * Replace the original image as sent by the server.
     *
     * @param originalImageIcon The new image icon.
     */
    public void setOriginalImageIcon(final ImageIcon originalImageIcon)
    {
        if (originalImageIcon == null) throw new IllegalArgumentException();

        this.originalImageIcon = originalImageIcon;
    }

    /**
     * Replace the face name.
     *
     * <p>XXX: this function may break the cache since it is used as a filename
     *
     * @param name The new face name.
     */
    public void setName(final String name)
    {
        if (name == null) throw new IllegalArgumentException();

        this.name = name;
    }

    /**
     * Return the unique face id.
     *
     * @return The face id.
     */
    public int getID()
    {
        return id;
    }

    /**
     * Return the image to be used in map view. If <code>useBigImages</code> is
     * set, return {@link #getImageIcon()}, else return {@link
     * #getOriginalImageIcon()}.
     *
     * @param useBigImages If set, return big images, else return small images.
     *
     * @return The image for map display.
     */
    public ImageIcon getImageIcon(final boolean useBigImages)
    {
        return useBigImages ? getImageIcon() : getOriginalImageIcon();
    }

    /**
     * Return the image scaled to be used in map view.
     *
     * @return The scaled image.
     */
    public ImageIcon getImageIcon()
    {
        return imageIcon;
    }

    /**
     * Return the original (unscaled) image.
     *
     * @return The unscaled image.
     */
    public ImageIcon getOriginalImageIcon()
    {
        return originalImageIcon;
    }

    /**
     * Return the face name.
     *
     * @return The face name.
     */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return name;
    }
}
