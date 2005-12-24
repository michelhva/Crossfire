package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandUpditemEvent extends EventObject
{
    private CfItem myitem;
    public CrossfireCommandUpditemEvent(Object src, CfItem it)
    {
        super(src);
        myitem = it;
    }
    public CfItem getItem()
    {
        return myitem;
    }

}
