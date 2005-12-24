package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandMapscrollEvent extends EventObject
{
    int mydx, mydy;
    public CrossfireCommandMapscrollEvent(Object src, int dx, int dy)
    {
        super(src);
        mydx = dx;
        mydy = dy;
    }
    public int getDX()
    {
        return mydx;
    }
    public int getDY()
    {
        return mydy;
    }
}
