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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.io.File;
import java.io.IOException;
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

    private static final FileCache fileCache = new FileCache(new File("cache"));

    /**
     * The face cache which holds all known faces.
     */
    private static final FaceCache faceCache = new FaceCache();

    /**
     * The empty face.
     */
    private static final ImageIcon originalEmptyImageIcon;

    /**
     * The scaled version of an empty face.
     */
    private static final ImageIcon emptyImageIcon;

    static
    {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        emptyImageIcon = new ImageIcon(gconf.createCompatibleImage(2*SQUARE_SIZE, SQUARE_SIZE, Transparency.OPAQUE));
        originalEmptyImageIcon = new ImageIcon(gconf.createCompatibleImage(SQUARE_SIZE, 2*SQUARE_SIZE, Transparency.OPAQUE));
        faceCache.addFace(new Face(0, "empty", emptyImageIcon, originalEmptyImageIcon));
    }

    /**
     * The askface manager to use.
     */
    private static AskfaceManager askfaceManager;

    /**
     * Set the callback functions to use.
     *
     * @param facesCallback The callback functions to use.
     */
    public static void setFacesCallback(final FacesCallback facesCallback)
    {
        askfaceManager = new AskfaceManager(facesCallback);

        final ImageIcon originalUnknownImageIcon = new ImageIcon(Faces.class.getClassLoader().getResource("unknown.png"));
        if (originalUnknownImageIcon.getIconWidth() <= 0 || originalUnknownImageIcon.getIconHeight() <= 0)
        {
            System.err.println("cannot find unknown.png");
            System.exit(0);
            throw new AssertionError();
        }
        final ImageIcon unknownImageIcon = getScaledImageIcon(originalUnknownImageIcon);
        Face.init(unknownImageIcon, originalUnknownImageIcon, askfaceManager, fileCache);
    }

    public static Face getFace(final int index)
    {
        final Face face = faceCache.getFace(index);
        if (face != null)
        {
            return face;
        }

        System.err.println("Warning: creating face object for unknown face "+index);
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        final Face newFace = new Face(index, "face#"+index, null, null);
        faceCache.addFace(newFace);
        return newFace;
    }

    // TODO: implement faceset
    public static int setImage(final int pixnum, final int faceset, final byte[] packet, final int start, final int pixlen)
    {
        askfaceManager.faceReceived(pixnum);

        final byte[] data = new byte[pixlen];
        System.arraycopy(packet, start, data, 0, pixlen);
        try
        {
            final ImageIcon img = new ImageIcon(data);
            if (img.getIconWidth() <= 0 || img.getIconHeight() <= 0)
            {
                System.err.println("face data for face "+pixnum+" is invalid, using unknown.png instead");
                final Face f = faceCache.getFace(pixnum);
                f.setImageIcon(null);
                f.setOriginalImageIcon(null);
            }
            else
            {
                final Face f = faceCache.getFace(pixnum);
                f.setImageIcon(getScaledImageIcon(img));
                f.setOriginalImageIcon(img);
                fileCache.save(f.getName()+".x1.png", f.getOriginalImageIcon());
                fileCache.save(f.getName()+".x2.png", f.getImageIcon());
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
    private static ImageIcon getScaledImageIcon(final ImageIcon img)
    {
        final ImageIcon imx2;
        try
        {
            final ImageScale2x scaler = new ImageScale2x(img);
            imx2 = scaler.getScaledImage();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            System.exit(0);
            throw new AssertionError();
        }
        return imx2;
    }

    // TODO: implement faceset
    // TODO: handle checksum
    public static void setFace(final int pixnum, final int faceset, final int checksum, final String pixname)
    {
        final ImageIcon imageIcon = fileCache.load(pixname+".x2.png");
        final ImageIcon originalImageIcon = fileCache.load(pixname+".x1.png");
        faceCache.addFace(new Face(pixnum, pixname, imageIcon, originalImageIcon));
    }

    /**
     * Ask the server to send image info.
     *
     * @param face the face to query
     */
    public static void askface(final int face)
    {
        askfaceManager.queryFace(face);
    }
}
