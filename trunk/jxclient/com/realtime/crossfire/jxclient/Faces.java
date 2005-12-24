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

public class Faces
{
    private static Hashtable<String,Face>  myfaces = new Hashtable<String,Face>();
    public final static int NRFACES = 6000;
    private static Face[]                  faces = new Face[NRFACES];
    static
    {
        System.out.println("Static Faces");
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
        try
        {
            BufferedImage img = new PngImage().read(dis, true);
            //BufferedImage img = ImageIO.read(dis);
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
            ServerConnection.writePacket("askface "+pixnum);
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
}
