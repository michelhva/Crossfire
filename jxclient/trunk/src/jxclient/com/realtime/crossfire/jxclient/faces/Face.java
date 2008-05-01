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

import java.lang.ref.SoftReference;
import javax.swing.ImageIcon;

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
     * The image icon to display for unknown or invalid faces. It is never
     * <code>null</code>.
     */
    private static ImageIcon originalUnknownImageIcon = null;

    /**
     * The scaled version of {@link #originalUnknownImageIcon}. It is never
     * <code>null</code>.
     */
    private static ImageIcon scaledUnknownImageIcon = null;

    /**
     * The scaled version of {@link #originalUnknownImageIcon}. It is never
     * <code>null</code>.
     */
    private static ImageIcon magicMapUnknownImageIcon = null;

    /**
     * The askface manager to query unknown images.
     */
    private static AskfaceManager askfaceManager = null;

    /**
     * The file cache used for loading orignal images from disk.
     */
    private static FileCache fileCacheOriginal = null;

    /**
     * The file cache used for loading scaled images from disk.
     */
    private static FileCache fileCacheScaled = null;

    /**
     * The file cache used for loading magic map images from disk.
     */
    private static FileCache fileCacheMagicMap = null;

    /**
     * The original (unscaled) image; may be <code>null</code> if unknown.
     */
    private SoftReference<ImageIcon> originalImageIcon;

    /**
     * The image scaled to be used in map view; may be <code>null</code> if
     * unknown.
     */
    private SoftReference<ImageIcon> scaledImageIcon;

    /**
     * The image scaled to be used in magic map view; may be <code>null</code>
     * if unknown.
     */
    private SoftReference<ImageIcon> magicMapImageIcon;

    /**
     * The face id as sent by the server.
     */
    private final int id;

    /**
     * The face name as sent by the server.
     */
    private String name;

    /**
     * The image checksum as sent by the server.
     */
    private int checksum;

    /**
     * Initialize the module.
     *
     * @param originalUnknownImageIcon The face to return if an original image
     * is unknown.
     *
     * @param scaledUnknownImageIcon The face to return if a scaled image is
     * unknown.
     *
     * @param magicMapUnknownImageIcon The face to return if a magic map image
     * is unknown.
     *
     * @param askfaceManager The askface manager to query unknown images.
     *
     * @param fileCacheOriginal The file cache used for loading original image
     * files from disk.
     *
     * @param fileCacheScaled The file cache used for loading scaled image
     * files from disk.
     *
     * @param fileCacheMagicMap The file cache used for loading magic map image
     * files from disk.
     */
    static void init(final ImageIcon originalUnknownImageIcon, final ImageIcon scaledUnknownImageIcon, final ImageIcon magicMapUnknownImageIcon, final AskfaceManager askfaceManager, final FileCache fileCacheOriginal, final FileCache fileCacheScaled, final FileCache fileCacheMagicMap)
    {
        Face.originalUnknownImageIcon = originalUnknownImageIcon;
        Face.scaledUnknownImageIcon = scaledUnknownImageIcon;
        Face.magicMapUnknownImageIcon = magicMapUnknownImageIcon;
        Face.askfaceManager = askfaceManager;
        Face.fileCacheOriginal = fileCacheOriginal;
        Face.fileCacheScaled = fileCacheScaled;
        Face.fileCacheMagicMap = fileCacheMagicMap;
    }

    /**
     * Create a new face.
     *
     * @param id The unique face id.
     *
     * @param name The face name.
     *
     * @param checksum The image checksum as sent by the server.
     *
     * @param originalImageIcon The unscaled image as sent by the server; may
     * be <code>null</code> if unknown.
     *
     * @param scaledImageIcon The image to use for map view; may be
     * <code>null</code> if unknown.
     *
     * @param magicMapImageIcon The image to use for magic map view; may be
     * <code>null</code> if unknown.
     */
    public Face(final int id, final String name, final int checksum, final ImageIcon originalImageIcon, final ImageIcon scaledImageIcon, final ImageIcon magicMapImageIcon)
    {
        if (name == null) throw new IllegalArgumentException();

        this.id = id;
        this.name = name;
        this.checksum = checksum;
        this.originalImageIcon = originalImageIcon == null ? null : new SoftReference<ImageIcon>(originalImageIcon);
        this.scaledImageIcon = scaledImageIcon == null ? null : new SoftReference<ImageIcon>(scaledImageIcon);
        this.magicMapImageIcon = magicMapImageIcon == null ? null : new SoftReference<ImageIcon>(magicMapImageIcon);
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
     * Replace the scaled image to use in map view.
     *
     * @param scaledImageIcon The new image icon.
     */
    public void setScaledImageIcon(final ImageIcon scaledImageIcon)
    {
        this.scaledImageIcon = scaledImageIcon == null ? null : new SoftReference<ImageIcon>(scaledImageIcon);
    }

    /**
     * Replace the scaled image to use in magic map view.
     *
     * @param magicMapImageIcon The new image icon.
     */
    public void setMagicMapImageIcon(final ImageIcon magicMapImageIcon)
    {
        this.magicMapImageIcon = magicMapImageIcon == null ? null : new SoftReference<ImageIcon>(magicMapImageIcon);
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
     * set, return {@link #getScaledImageIcon()}, else return {@link
     * #getOriginalImageIcon()}.
     *
     * @param useBigImages If set, return big images, else return small images.
     *
     * @return The image for map display.
     */
    public ImageIcon getImageIcon(final boolean useBigImages)
    {
        return useBigImages ? getScaledImageIcon() : getOriginalImageIcon();
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
     * Return the image scaled to be used in map view.
     *
     * @return The scaled image.
     */
    public ImageIcon getScaledImageIcon()
    {
        if (scaledImageIcon != null)
        {
            final ImageIcon result = scaledImageIcon.get();
            if (result != null)
            {
                return result;
            }

            scaledImageIcon = null;
        }

        return loadScaledImageIcon();
    }

    /**
     * Return the image scaled to be used in magic map view.
     *
     * @return The scaled image.
     */
    public ImageIcon getMagicMapImageIcon()
    {
        if (magicMapImageIcon != null)
        {
            final ImageIcon result = magicMapImageIcon.get();
            if (result != null)
            {
                return result;
            }

            magicMapImageIcon = null;
        }

        return loadMagicMapImageIcon();
    }

    /**
     * Return the width in tiles.
     *
     * @return The width.
     */
    public int getTileWidth()
    {
        final ImageIcon img = getOriginalImageIcon();
        return (img.getIconWidth()+31)/32;
    }

    /**
     * Return the height in tiles.
     *
     * @return The height.
     */
    public int getTileHeight()
    {
        final ImageIcon img = getOriginalImageIcon();
        return (img.getIconHeight()+31)/32;
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

    /**
     * Return the image checksum.
     *
     * @return The image checksum.
     */
    public int getChecksum()
    {
        return checksum;
    }

    /** {@inheritDoc} */
    @Override public String toString()
    {
        return name;
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
        final ImageIcon originalImageIcon = fileCacheOriginal.load(name, checksum);
        if (originalImageIcon != null)
        {
            this.originalImageIcon = new SoftReference<ImageIcon>(originalImageIcon);
            return originalImageIcon;
        }

        askfaceManager.queryFace(id);
        return originalUnknownImageIcon;
    }

    /**
     * Load {@link #scaledImageIcon} from the backing storage. If loading
     * fails, return {@link #scaledUnknownImageIcon} and request the image from
     * the server.
     *
     * @return The image.
     */
    private ImageIcon loadScaledImageIcon()
    {
        final ImageIcon scaledImageIcon = fileCacheScaled.load(name, checksum);
        if (scaledImageIcon != null)
        {
            this.scaledImageIcon = new SoftReference<ImageIcon>(scaledImageIcon);
            return scaledImageIcon;
        }

        askfaceManager.queryFace(id);
        return scaledUnknownImageIcon;
    }

    /**
     * Load {@link #magicMapImageIcon} from the backing storage. If loading
     * fails, return {@link #magicMapUnknownImageIcon} and request the image
     * from the server.
     *
     * @return The image.
     */
    private ImageIcon loadMagicMapImageIcon()
    {
        final ImageIcon magicMapImageIcon = fileCacheMagicMap.load(name, checksum);
        if (magicMapImageIcon != null)
        {
            this.magicMapImageIcon = new SoftReference<ImageIcon>(magicMapImageIcon);
            return magicMapImageIcon;
        }

        askfaceManager.queryFace(id);
        return magicMapUnknownImageIcon;
    }
}
