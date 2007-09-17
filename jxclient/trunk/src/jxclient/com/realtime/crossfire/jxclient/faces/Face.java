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
import java.lang.ref.SoftReference;
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
     * The image icon to display for unknown or invalid faces. It is never
     * <code>null</code>.
     */
    private static ImageIcon originalUnknownImageIcon = null;

    /**
     * The scaled version of {@link #unknownImageIcon}. It is never
     * <code>null</code>.
     */
    private static ImageIcon unknownImageIcon = null;

    /**
     * The askface manager to query unknown images.
     */
    private static AskfaceManager askfaceManager = null;

    /**
     * The file cache used for loading images from disk.
     */
    private static FileCache fileCache = null;

    /**
     * The image scaled to be used in map view; may be <code>null</code> if
     * unknown.
     */
    private SoftReference<ImageIcon> imageIcon;

    /**
     * The original (unscaled) image; may be <code>null</code> if unknown.
     */
    private SoftReference<ImageIcon> originalImageIcon;

    /**
     * The face id as sent by the server.
     */
    private final int id;

    /**
     * The face name as sent by the server.
     */
    private String name;

    /**
     * Initialize the module.
     *
     * @param unknownImageIcon The face to return if an image is unknown.
     *
     * @param originalUnknownImageIcon The face to return if an original image
     * is unknown.
     *
     * @param askfaceManager The askface manager to query unknown images.
     *
     * @param fileCache The file cache used for loading image files from disk.
     */
    static void init(final ImageIcon unknownImageIcon, final ImageIcon originalUnknownImageIcon, final AskfaceManager askfaceManager, final FileCache fileCache)
    {
        Face.unknownImageIcon = unknownImageIcon;
        Face.originalUnknownImageIcon = originalUnknownImageIcon;
        Face.askfaceManager = askfaceManager;
        Face.fileCache = fileCache;
    }

    /**
     * Create a new face.
     *
     * @param id The unique face id.
     *
     * @param name The face name.
     *
     * @param imageIcon The image to use for map view; may be <code>null</code>
     * if unknown.
     *
     * @param originalImageIcon The unscaled image as sent by the server; may
     * be <code>null</code> if unknown.
     */
    public Face(final int id, final String name, final ImageIcon imageIcon, final ImageIcon originalImageIcon)
    {
        if (name == null) throw new IllegalArgumentException();

        this.id = id;
        this.name = name;
        this.imageIcon = imageIcon == null ? null : new SoftReference<ImageIcon>(imageIcon);
        this.originalImageIcon = originalImageIcon == null ? null : new SoftReference<ImageIcon>(originalImageIcon);
    }

    /**
     * Replace the images to use in map view.
     *
     * @param imageIcon The new image icon.
     */
    public void setImageIcon(final ImageIcon imageIcon)
    {
        this.imageIcon = imageIcon == null ? null : new SoftReference<ImageIcon>(imageIcon);
    }

    /**
     * Replace the original image as sent by the server.
     *
     * @param originalImageIcon The new image icon.
     */
    public void setOriginalImageIcon(final ImageIcon originalImageIcon)
    {
        this.originalImageIcon = originalImageIcon == null ? null : new SoftReference<ImageIcon>(originalImageIcon);
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
        if (imageIcon != null)
        {
            final ImageIcon result = imageIcon.get();
            if (result != null)
            {
                return result;
            }

            imageIcon = null;
        }

        return loadImageIcon();
    }

    /**
     * Return the original (unscaled) image.
     *
     * @return The unscaled image.
     */
    public ImageIcon getOriginalImageIcon()
    {
        if (originalImageIcon != null)
        {
            final ImageIcon result = originalImageIcon.get();
            if (result != null)
            {
                return result;
            }

            originalImageIcon = null;
        }

        return loadOriginalImageIcon();
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

    /**
     * Load {@link #imageIcon} from the backing storage. If loading fails,
     * return {@link #unknownImageIcon} and request the image from the server.
     *
     * @return The image.
     */
    private ImageIcon loadImageIcon()
    {
        final ImageIcon imageIcon = fileCache.load(name+".x2.png");
        if (imageIcon != null)
        {
            this.imageIcon = new SoftReference<ImageIcon>(imageIcon);
            return imageIcon;
        }

        askfaceManager.queryFace(id);
        return unknownImageIcon;
    }

    /**
     * Load {@link #originalImageIcon} from the backing storage. If loading
     * fails, return {@link #originalUnknownImageIcon} and request the image
     * from the server.
     *
     * @return The original image.
     */
    private ImageIcon loadOriginalImageIcon()
    {
        final ImageIcon originalImageIcon = fileCache.load(name+".x1.png");
        if (originalImageIcon != null)
        {
            this.originalImageIcon = new SoftReference<ImageIcon>(originalImageIcon);
            return originalImageIcon;
        }

        askfaceManager.queryFace(id);
        return originalUnknownImageIcon;
    }
}
