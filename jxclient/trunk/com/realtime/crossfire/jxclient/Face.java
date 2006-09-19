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

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
/**
 *
 * @version 1.0
 * @author Lauwenmark
 * @since 1.0
 */
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