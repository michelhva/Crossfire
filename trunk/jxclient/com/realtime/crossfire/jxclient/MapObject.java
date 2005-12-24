package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class MapObject
{
    private Face myface;

    private int x,y,z,w,h;

    public MapObject(Face f, int px, int py, int pz)
    {
        z = pz;
        myface = f;
        w = myface.getPicture().getWidth();
        h = myface.getPicture().getHeight();

        //px and py are the lower-right square coordinate - let's convert
        //that to top-left ones.

        x = px - (w-32);
        y = py - (h-32);
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getZ()
    {
        return z;
    }
    public int getWidth()
    {
        return w;
    }
    public int getHeight()
    {
        return h;
    }
    public Face getFace()
    {
        return myface;
    }
}
