package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandItem2Event extends EventObject
{
    private CfItem myitem;
    public CrossfireCommandItem2Event(Object src, CfItem it)
    {
        super(src);
        myitem = it;
    }
    public CfItem getItem()
    {
        return myitem;
    }
}
