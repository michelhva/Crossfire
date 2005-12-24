package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandQueryEvent extends EventObject
{
    private String myquery;
    private int myquerytype;
    public CrossfireCommandQueryEvent(Object src, String msg, int type)
    {
        super(src);
        myquery = msg;
        myquerytype = type;
    }
    public String getPrompt()
    {
        return myquery;
    }
}
