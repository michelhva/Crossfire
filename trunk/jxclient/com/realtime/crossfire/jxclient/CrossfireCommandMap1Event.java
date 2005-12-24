package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandMap1Event extends EventObject
{
    private List<MapSquare> mylist = null;

    public CrossfireCommandMap1Event(Object src, List<MapSquare> l)
    {
        super(src);
        mylist = l;
    }

    public List<MapSquare> getChangedSquares()
    {
        return mylist;
    }
}
