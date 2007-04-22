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
import  com.realtime.crossfire.jxclient.*;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.*;
import com.sixlegs.png.*;

/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
public class Faces
{
    private static Hashtable<String,Face>  myfaces = new Hashtable<String,Face>();
    public final static int NRFACES = 6000;
    private static Face[]                  faces = new Face[NRFACES];
    static
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice      gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gconf = gd.getDefaultConfiguration();
        /*for(int i=0; i<NRFACES; i++)
        {
            faces[i] = new Face(0, "empty",gconf.createCompatibleImage(
                    ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT),
                    gconf.createCompatibleImage(
                            ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT));
        }*/
            faces[0] = new Face(0, "empty",gconf.createCompatibleImage(
                    ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT),
                    gconf.createCompatibleImage(
                            ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT));
    }

    /**
     * Face numbers for which "askface" commands have been sent without having
     * received a response from the server.
     */
    private static Set<Integer> pendingAskfaces = new HashSet<Integer>();

    public static Face getFace(int index)
    {
        if (faces[index]==null)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice      gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            faces[index] = new Face(0, "empty",gconf.createCompatibleImage(
                    ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT),
                    gconf.createCompatibleImage(
                            ServerConnection.SQUARE_SIZE,
                    ServerConnection.SQUARE_SIZE,
                    Transparency.TRANSLUCENT));
        }
        return faces[index];
    }
    public static Face[] getFaces()
    {
        return faces;
    }
    public static int setImage(DataInputStream dis) throws IOException
    {
        int pixnum   = dis.readInt();
        int pixlen   = dis.readInt();

        if (!pendingAskfaces.remove(pixnum))
        {
            System.err.println("received unexpected image for "+pixnum);
        }

        try
        {
            BufferedImage img = new PngImage().read(dis, true);
            BufferedImage imx2 = null;
            try
            {
                ImageScale2x scaler = new ImageScale2x(img);
                imx2 = scaler.getScaledImage();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(0);
            }
            Face f = faces[pixnum];
            f.setPicture(imx2);
            f.setOriginalPicture(img);
            f.storeInCache("cache/");
        }
        catch(java.lang.IllegalArgumentException e)
        {
            System.out.println("Unable to get face:"+pixnum);
        }
        return pixnum;
    }
    public static void setFace1(DataInputStream dis) throws IOException
    {
        int len      = dis.available();
        int pixnum   = dis.readUnsignedShort();
        int checksum = dis.readInt();
        int plen     = dis.available();
        byte[] buf   = new byte[plen];
        dis.readFully(buf);
        String pixname = new String(buf);
        //System.out.println("len:"+len+" plen:"+plen);
        try
        {
            //BufferedImage im = ImageIO.read(new File("cache/"+pixname+".x2.png"));
            //BufferedImage oim = ImageIO.read(new File("cache/"+pixname+".x1.png"));
            BufferedImage im = new PngImage().read(new File("cache/"+pixname+".x2.png"));
            BufferedImage oim = new PngImage().read(new File("cache/"+pixname+".x1.png"));

            //BufferedImage im = ImageIO.read(new File("cache/"+pixname+".png"));
            Face f = new Face(pixnum, pixname,im, oim);
            myfaces.put(pixname, f);
            faces[pixnum] = f;
        }
        catch (IOException e)
        {
            askface(pixnum);
            Face f = new Face(pixnum, pixname,null);
            myfaces.put(pixname, f);
            faces[pixnum] = f;
        }
    }
    public static void ensureFaceExists(int val)
    {
        getFace(val);
        if (faces[val].getPicture()==null)
        {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice      gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gconf = gd.getDefaultConfiguration();
            faces[val].setPicture(gconf.createCompatibleImage(
                    ServerConnection.SQUARE_SIZE,
            ServerConnection.SQUARE_SIZE,
            Transparency.TRANSLUCENT));
        }
    }

    /**
     * Ask the server to send image info.
     *
     * @param face the face to query
     */
    public static void askface(final int face) throws IOException
    {
        assert face > 0;

        if (pendingAskfaces.add(face))
        {
            ServerConnection.writePacket("askface "+face);
        }
    }
}
