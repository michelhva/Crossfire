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
package com.realtime.crossfire.jxclient;

import java.io.IOException;
import javax.swing.ImageIcon;
/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Face
{
    /**
     * The image scaled to be used in map view. May be <code>null</code> if the
     * face is not known or invalid.
     */
    private ImageIcon imageIcon;

    /**
     * The original (unscaled) image. May be <code>null</code> if the face if
     * not known or invalid.
     */
    private ImageIcon originalImageIcon;

    private final int id;

    private String name;

    public Face(final int id, final String name, final ImageIcon imageIcon)
    {
        this.id = id;
        this.name = name;
        this.imageIcon = imageIcon;
        originalImageIcon = imageIcon;
    }

    public Face(final int id, final String name, final ImageIcon imageIcon, final ImageIcon originalImageIcon)
    {
        this.id = id;
        this.name = name;
        this.imageIcon = imageIcon;
        this.originalImageIcon = originalImageIcon;
    }

    public void setImageIcon(final ImageIcon imageIcon)
    {
        this.imageIcon = imageIcon;
    }

    public void setOriginalImageIcon(final ImageIcon originalImageIcon)
    {
        this.originalImageIcon = originalImageIcon;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public int getID()
    {
        return id;
    }

    /**
     * Return the image to be used in map view. If <code>useBigImages</code> is
     * set, return {@link #getImageIcon()}, else return {@link
     * getOriginalImageIcon()}.
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
     * @return the scaled image; returns {@link Faces#unknownImageIcon} if the
     * face is unknown or invalid.
     */
    public ImageIcon getImageIcon()
    {
        return imageIcon != null ? imageIcon : Faces.getUnknownImageIcon();
    }

    /**
     * Return the original (unscaled) image.
     *
     * @return the unscaled image; returns {@link Faces#unknownImageIcon} if
     * the face is unknown or invalid.
     */
    public ImageIcon getOriginalImageIcon()
    {
        return originalImageIcon != null ? originalImageIcon : Faces.getOriginalUnknownImageIcon();
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return name;
    }
}
