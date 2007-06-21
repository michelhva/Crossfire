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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
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

    private final int myid;
    private String myname;

    public Face(final int id, final String name, final ImageIcon imageIcon)
    {
        super();
        myid = id;
        myname = name;
        this.imageIcon = imageIcon;
        originalImageIcon = imageIcon;
    }

    public Face(final int id, final String name, final ImageIcon imageIcon, final ImageIcon originalImageIcon)
    {
        super();
        myid = id;
        myname = name;
        this.imageIcon = imageIcon;
        this.originalImageIcon = originalImageIcon;
    }

    public void setImageIcon(final ImageIcon pic)
    {
        imageIcon = pic;
    }
    public void setOriginalImageIcon(final ImageIcon pic)
    {
        originalImageIcon = pic;
    }
    public void setName(final String n)
    {
        myname = n;
    }
    public int getID()
    {
        return myid;
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
        return myname;
    }
    public void storeInCache(final String basedir) throws IllegalArgumentException, IOException
    {
        saveImageIcon(imageIcon, new File("cache/"+myname+".x2.png"));
        saveImageIcon(originalImageIcon, new File("cache/"+myname+".x1.png"));
    }

    /**
     * Save an image to a file.
     *
     * @param imageIcon the image to save
     *
     * @param outputFile the file to save to
     *
     * @throws IOException if the image cannot be saved
     */
    public static void saveImageIcon(final ImageIcon imageIcon, final File outputFile) throws IOException
    {
        final BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        imageIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        ImageIO.write(bufferedImage, "png", outputFile);
    }

    public String toString()
    {
        return myname;
    }
}
