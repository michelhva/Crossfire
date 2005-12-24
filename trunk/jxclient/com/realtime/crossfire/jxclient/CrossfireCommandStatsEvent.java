package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;
import java.util.*;

public class CrossfireCommandStatsEvent extends EventObject
{
    private Stats mystats;
    public CrossfireCommandStatsEvent(Object src, Stats st)
    {
        super(src);
        mystats = st;
    }
    public Stats getStats()
    {
        return mystats;
    }
}
