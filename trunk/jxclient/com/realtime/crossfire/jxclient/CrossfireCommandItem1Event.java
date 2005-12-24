package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandItem1Event extends EventObject
{
    private CfItem myitem;
    public CrossfireCommandItem1Event(Object src, CfItem it)
    {
        super(src);
        myitem = it;
    }
    public CfItem getItem()
    {
        return myitem;
    }
}
