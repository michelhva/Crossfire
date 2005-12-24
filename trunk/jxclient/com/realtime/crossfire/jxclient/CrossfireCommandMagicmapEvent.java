package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandMagicmapEvent extends EventObject
{
    private int mywidth, myheight;
    private int mypx, mypy;
    private byte[] mydata;

    public CrossfireCommandMagicmapEvent(Object src, int w, int h, int px, int py, byte[] d)
    {
        super(src);
        mywidth = w;
        myheight = h;
        mypx = px;
        mypy = py;
        mydata = d;
    }
    public int getWidth()
    {
        return mywidth;
    }
    public int getHeight()
    {
        return myheight;
    }
    public int getPX()
    {
        return mypx;
    }
    public int getPY()
    {
        return mypy;
    }
    public byte[] getData()
    {
        return mydata;
    }
}
