package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandDrawinfoEvent extends EventObject
{
    private String mytext;
    private int mytype;

    public CrossfireCommandDrawinfoEvent(Object src, String msg, int type)
    {
        super(src);
        mytext = msg;
        mytype = type;
    }
    public int getTextType()
    {
        return mytype;
    }
    public String getText()
    {
        return mytext;
    }
}
