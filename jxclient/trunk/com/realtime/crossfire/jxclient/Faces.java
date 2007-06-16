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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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
     * The maximum number of concurrently sent "askface" commands.
     */
    public static final int CONCURRENT_ASKFACE_COMMANDS = 8;

    private static Hashtable<String, Face>  myfaces = new Hashtable<String, Face>();

    public final static int NRFACES = 6000;

    private static Face[] faces = new Face[NRFACES];

    /**
     * The image icon to display for unknown or invalid faces. It is never
     * <code>null</code>.
     */
    private static final ImageIcon originalUnknownImageIcon;

    /**
     * The scaled version of {@link #unknownImageIcon}. It is never
     * <code>null</code>.
     */
    private static final ImageIcon unknownImageIcon;

    static
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        /*for(int i = 0; i < NRFACES; i++)
        {
            faces[i] = new Face(0, "empty", gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT), gconf.createCompatibleImage( CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT));
        }*/
            faces[0] = new Face(0, "empty", new ImageIcon(gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT)), new ImageIcon(gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT)));
            originalUnknownImageIcon = new ImageIcon(Faces.class.getClassLoader().getResource("unknown.png"));
            if (originalUnknownImageIcon.getIconWidth() <= 0 || originalUnknownImageIcon.getIconHeight() <= 0)
            {
                System.err.println("cannot find unknown.png");
                System.exit(0);
                throw new AssertionError();
            }
            unknownImageIcon = getScaledImageIcon(originalUnknownImageIcon);
    }

    /**
     * Face numbers for which "askface" commands have been sent without having
     * received a response from the server.
     */
    private static Set<Integer> pendingAskfaces = new HashSet<Integer>();

    /**
     * Face numbers for which an "askface" command should be sent. It includes
     * all elements of {@link #pendingAskfaces}.
     */
    private static Set<Integer> pendingFaces = new HashSet<Integer>();

    /**
     * The server connection to send "askface" commands to.
     */
    private static CrossfireServerConnection crossfireServerConnection;

    /**
     * Set the server connection to send "askface" commands to.
     *
     * @param crossfireServerConnection the server connection
     */
    public static void setCrossfireServerConnection(final CrossfireServerConnection crossfireServerConnection)
    {
        Faces.crossfireServerConnection = crossfireServerConnection;
    }

    public static Face getFace(int index)
    {
        if (faces[index] == null)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            faces[index] = new Face(0, "empty", new ImageIcon(gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT)), new ImageIcon(gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT)));
        }
        return faces[index];
    }

    public static Face[] getFaces()
    {
        return faces;
    }

    // TODO: implement faceset
    public static int setImage(final int pixnum, final int faceset, final byte[] packet, final int start, final int pixlen) throws IOException
    {
        if (!pendingAskfaces.remove(pixnum))
        {
            System.err.println("received unexpected image for "+pixnum);
        }
        else if (!pendingFaces.remove(pixnum))
        {
            assert false;
        }

        final byte[] data = new byte[pixlen];
        System.arraycopy(packet, start, data, 0, pixlen);
        try
        {
            ImageIcon img = new ImageIcon(data);
            if (img.getIconWidth() <= 0 || img.getIconHeight() <= 0)
            {
                System.err.println("face data for face "+pixnum+" is invalid, using unknown.png instead");
                Face f = faces[pixnum];
                f.setImageIcon(unknownImageIcon);
                f.setOriginalImageIcon(originalUnknownImageIcon);
            }
            else
            {
                Face f = faces[pixnum];
                f.setImageIcon(getScaledImageIcon(img));
                f.setOriginalImageIcon(img);
                f.storeInCache("cache/");
            }
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("Unable to get face:"+pixnum);
        }
        sendAskface();
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
    public static void setFace(final int pixnum, final int faceset, final int checksum, final String pixname) throws IOException
    {
        ImageIcon im = new ImageIcon("cache/"+pixname+".x2.png");
        ImageIcon oim = new ImageIcon("cache/"+pixname+".x1.png");
        if (im.getIconWidth() <= 0 || im.getIconHeight() <= 0 || oim.getIconWidth() <= 0 || oim.getIconHeight() <= 0)
        {
            askface(pixnum);
            Face f = new Face(pixnum, pixname, null);
            myfaces.put(pixname, f);
            faces[pixnum] = f;
        }
        else
        {
            Face f = new Face(pixnum, pixname, im, oim);
            myfaces.put(pixname, f);
            faces[pixnum] = f;
        }
    }
    public static void ensureFaceExists(int val)
    {
        getFace(val);
        if (faces[val].getImageIcon() == null)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            faces[val].setImageIcon(new ImageIcon(gconf.createCompatibleImage(CrossfireServerConnection.SQUARE_SIZE, CrossfireServerConnection.SQUARE_SIZE, Transparency.TRANSLUCENT)));
        }
    }

    /**
     * Return the image to use for unknown or invalid faces.
     *
     * @return the image to use for unknown or invalid faces
     */
    public static ImageIcon getUnknownImageIcon()
    {
        return unknownImageIcon;
    }

    /**
     * Return the image to use for unknown or invalid faces.
     *
     * @return the image to use for unknown or invalid faces
     */
    public static ImageIcon getOriginalUnknownImageIcon()
    {
        return originalUnknownImageIcon;
    }

    /**
     * Ask the server to send image info.
     *
     * @param face the face to query
     *
     * @throws IOException if the command cannot be sent
     */
    public static void askface(final int face) throws IOException
    {
        assert face > 0;

        if (!pendingFaces.add(face))
        {
            return;
        }

        sendAskface();
    }

    /**
     * Send some pending "askface" commands.
     *
     * @throws IOException if the command cannot be sent
     */
    private static void sendAskface() throws IOException
    {
        final Iterator<Integer> it = pendingFaces.iterator();
        while (it.hasNext() && pendingAskfaces.size() < CONCURRENT_ASKFACE_COMMANDS)
        {
            final int face = it.next();
            if (!pendingAskfaces.contains(face))
            {
                crossfireServerConnection.sendAskface(face);
                pendingAskfaces.add(face);
            }
        }
    }
}
