package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
public class Face
{
    private BufferedImage mypicture;
    private BufferedImage myorigpicture;
    private int myid;
    private String myname;

    public Face(int id, String name, BufferedImage pic)
    {
        super();
        myid = id;
        myname = name;
        mypicture = pic;
        myorigpicture = pic;
    }
    public Face(int id, String name, BufferedImage pic, BufferedImage opic)
    {
        super();
        myid = id;
        myname = name;
        mypicture = pic;
        myorigpicture = opic;
    }
    public void setID(int id)
    {
        myid = id;
    }
    public void setPicture(BufferedImage pic)
    {
        mypicture = pic;
    }
    public void setOriginalPicture(BufferedImage pic)
    {
        myorigpicture = pic;
    }
    public void setName(String n)
    {
        myname = n;
    }
    public int getID()
    {
        return myid;
    }
    public BufferedImage getPicture()
    {
        return mypicture;
    }
    public BufferedImage getOriginalPicture()
    {
        return myorigpicture;
    }
    public String getName()
    {
        return myname;
    }
    public void storeInCache(String basedir) throws IllegalArgumentException, IOException
    {
        ImageIO.write(mypicture, "png", new File("cache/"+myname+".x2.png"));
        ImageIO.write(myorigpicture, "png", new File("cache/"+myname+".x1.png"));
    }
    public String toString()
    {
        return myname;
    }
}