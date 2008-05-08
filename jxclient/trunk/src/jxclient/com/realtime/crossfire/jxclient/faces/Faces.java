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

import com.realtime.crossfire.jxclient.settings.Filenames;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.io.IOException;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Faces
{
    /**
     * The pixel size of the gaming squares. Notice that they are supposed to
     * be *squares*, so only a single value is needed :)
     */
    public static final int SQUARE_SIZE = 64;

    /**
     * The image cache for original .png files as received from the server.
     */
    private final FileCache fileCacheOriginal = new FileCache(Filenames.getOriginalImageCacheDir());

    /**
     * The image cache for x2 scaled .png files.
     */
    private final FileCache fileCacheScaled = new FileCache(Filenames.getScaledImageCacheDir());

    /**
     * The image cache for /8 scaled .png files.
     */
    private final FileCache fileCacheMagicMap = new FileCache(Filenames.getMagicMapImageCacheDir());

    /**
     * The face cache which holds all known faces.
     */
    private final FaceCache faceCache = new FaceCache();

    /**
     * The askface manager to use.
     */
    private AskfaceManager askfaceManager;

    /**
     * Creates a new instance.
     */
    public Faces()
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        final ImageIcon originalEmptyImageIcon = new ImageIcon(gconf.createCompatibleImage(SQUARE_SIZE, SQUARE_SIZE, Transparency.OPAQUE));
        final ImageIcon scaledEmptyImageIcon = new ImageIcon(gconf.createCompatibleImage(2*SQUARE_SIZE, 2*SQUARE_SIZE, Transparency.OPAQUE));
        final ImageIcon magicMapEmptyImageIcon = new ImageIcon(gconf.createCompatibleImage(SQUARE_SIZE/8, SQUARE_SIZE/8, Transparency.OPAQUE));
        faceCache.addFace(new Face(0, "empty", 0, originalEmptyImageIcon, scaledEmptyImageIcon, magicMapEmptyImageIcon));
    }

    /**
     * Set the callback functions to use.
     *
     * @param facesCallback The callback functions to use.
     *
     * @throws IOException if a resource cannot be found
     */
    public void setFacesCallback(final FacesCallback facesCallback) throws IOException
    {
        askfaceManager = new AskfaceManager(facesCallback);

        final URL url = Faces.class.getClassLoader().getResource("resource/unknown.png");
        if (url == null)
        {
            throw new IOException("cannot find unknown.png");
        }
        final ImageIcon originalUnknownImageIcon = new ImageIcon(url);
        if (originalUnknownImageIcon.getIconWidth() <= 0 || originalUnknownImageIcon.getIconHeight() <= 0)
        {
            throw new IOException("cannot load unknown.png");
        }
        final ImageIcon scaledUnknownImageIcon = getScaledImageIcon(originalUnknownImageIcon);
        final ImageIcon magicMapUnknownImageIcon = getMagicMapImageIcon(originalUnknownImageIcon);
        Face.init(originalUnknownImageIcon, scaledUnknownImageIcon, magicMapUnknownImageIcon, askfaceManager, fileCacheOriginal, fileCacheScaled, fileCacheMagicMap);
    }

    public Face getFace(final int index)
    {
        final Face face = faceCache.getFace(index);
        if (face != null)
        {
            return face;
        }

        System.err.println("Warning: creating face object for unknown face "+index);
        final Face newFace = new Face(index, "face#"+index, 0, null, null, null);
        faceCache.addFace(newFace);
        return newFace;
    }

    public int setImage(final int pixnum, final byte[] packet, final int start, final int pixlen)
    {
        final byte[] data = new byte[pixlen];
        System.arraycopy(packet, start, data, 0, pixlen);
        try
        {
            final ImageIcon img = new ImageIcon(data);
            if (img.getIconWidth() <= 0 || img.getIconHeight() <= 0)
            {
                System.err.println("face data for face "+pixnum+" is invalid, using unknown.png instead");
                final Face f = faceCache.getFace(pixnum);
                f.setOriginalImageIcon(null);
                f.setScaledImageIcon(null);
                f.setMagicMapImageIcon(null);
            }
            else
            {
                final Face f = faceCache.getFace(pixnum);
                f.setOriginalImageIcon(img);
                f.setScaledImageIcon(getScaledImageIcon(img));
                f.setMagicMapImageIcon(getMagicMapImageIcon(img));
                fileCacheOriginal.save(f.getName(), f.getChecksum(), f.getOriginalImageIcon());
                fileCacheScaled.save(f.getName(), f.getChecksum(), f.getScaledImageIcon());
                fileCacheMagicMap.save(f.getName(), f.getChecksum(), f.getMagicMapImageIcon());
            }
        }
        catch (final IllegalArgumentException e)
        {
            System.out.println("Unable to get face:"+pixnum);
        }
        return pixnum;
    }

    /**
     * Create a copy of a given image with double width and height.
     *
     * @param img the image icon to process
     *
     * @return the scaled image icon
     */
    private ImageIcon getScaledImageIcon(final ImageIcon img)
    {
        final ImageScale2x scaler = new ImageScale2x(img);
        return scaler.getScaledImage();
    }

    /**
     * Create a copy of a given image with eigth width and height.
     *
     * @param img the image icon to process
     *
     * @return the scaled image icon
     */
    private ImageIcon getMagicMapImageIcon(final ImageIcon img)
    {
        final ImageScale8d scaler = new ImageScale8d(img);
        return scaler.getScaledImage();
    }

    public void setFace(final int pixnum, final int checksum, final String pixname)
    {
        final ImageIcon originalImageIcon = fileCacheOriginal.load(pixname, checksum);
        final ImageIcon scaledImageIcon = fileCacheScaled.load(pixname, checksum);
        final ImageIcon magicMapImageIcon = fileCacheMagicMap.load(pixname, checksum);
        faceCache.addFace(new Face(pixnum, pixname, checksum, originalImageIcon, scaledImageIcon, magicMapImageIcon));
    }

    /**
     * Reset the askface queue. Must be called when a server connection is
     * established.
     */
    public void reset()
    {
        askfaceManager.reset();
    }

    /**
     * Ask the server to send image info.
     *
     * @param face the face to query
     */
    public void askface(final int face)
    {
        askfaceManager.queryFace(face);
    }
}
