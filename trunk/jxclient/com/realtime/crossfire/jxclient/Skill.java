package com.realtime.crossfire.jxclient;
import com.realtime.crossfire.jxclient.*;
import java.util.*;

public class Skill
{
    private int myindex;
    private String myname;
    private long myexperience = 0;
    private int mylevel = 1;

    public Skill(int index, String name)
    {
        myindex = index;
        myname = name;
    }
    public void setLevel(int nv)
    {
//        System.out.println("Skill "+myname+": Setting level to "+nv);
        mylevel = nv;
    }
    public void setExperience(long exp)
    {
//        System.out.println("Skill "+myname+": Setting experience to "+exp);
        myexperience = exp;
    }
    public long getExperience()
    {
        return myexperience;
    }
    public int getLevel()
    {
        return mylevel;
    }
}
