package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandDelitemEvent extends EventObject
{
    private CfItem myitem;

    public CrossfireCommandDelitemEvent(Object src, CfItem m)
    {
        super(src);
        myitem = m;
    }

    public CfItem getItem()
    {
        return myitem;
    }
}
