package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandDrawextinfoEvent extends EventObject
{
    private int mycolor;
    private int mytype;
    private int mysubtype;
    private String mymessage;
    public CrossfireCommandDrawextinfoEvent(Object src, int color, int type,
                                            int subtype, String message)
    {
        super(src);
        mycolor = color;
        mytype = type;
        mysubtype = subtype;
        mymessage = message;
    }
    public int getColor()
    {
        return mycolor;
    }
    public int getType()
    {
        return mytype;
    }
    public int getSubType()
    {
        return mysubtype;
    }
    public String getMessage()
    {
        return mymessage;
    }
}
