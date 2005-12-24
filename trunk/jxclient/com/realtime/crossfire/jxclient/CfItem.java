package com.realtime.crossfire.jxclient;
import  com.realtime.crossfire.jxclient.*;

public class CfItem
{
    int mytag;
    int myflags;
    int myweight;
    Face myface;
    String myname;
    String mynamepl;
    int mynrof;
    int mylocation;
    int mytype = -1;

    boolean applied = false;
    boolean location = false;
    boolean unpaid = false;
    boolean magic = false;
    boolean cursed = false;
    boolean damned = false;
    boolean open = false;
    boolean nopick = false;
    boolean locked = false;

    public static final int F_APPLIED      = 0x000F;
    public static final int F_LOCATION     = 0x00F0;
    public static final int F_UNPAID       = 0x0200;
    public static final int F_MAGIC        = 0x0400;
    public static final int F_CURSED       = 0x0800;
    public static final int F_DAMNED       = 0x1000;
    public static final int F_OPEN         = 0x2000;
    public static final int F_NOPICK       = 0x4000;
    public static final int F_LOCKED       = 0x8000;

    public static final int UPD_LOCATION   = 0x01;
    public static final int UPD_FLAGS      = 0x02;
    public static final int UPD_WEIGHT     = 0x04;
    public static final int UPD_FACE       = 0x08;
    public static final int UPD_NAME       = 0x10;
    public static final int UPD_ANIM       = 0x20;
    public static final int UPD_ANIMSPEED  = 0x40;
    public static final int UPD_NROF       = 0x80;

    public void setLocation(int nl)
    {
        mylocation = nl;
    }
    public void setFlags(int nv)
    {
        myflags = nv;
        computeFlags();
    }
    public void setWeight(int nv)
    {
        myweight = nv;
    }
    public void setFace(Face f)
    {
        myface = f;
    }
    public void setName(String n, String npl)
    {
        myname = n;
        mynamepl = npl;
    }
    public void setNrOf(int nv)
    {
        mynrof = nv;
    }

    public int getTag()
    {
        return mytag;
    }
    public int getWeight()
    {
        return myweight;
    }
    public Face getFace()
    {
        return myface;
    }
    public String getName()
    {
        if (mynrof > 1)
            return mynamepl;
        else
            return myname;
    }
    public int getNrOf()
    {
        return mynrof;
    }
    public boolean isApplied()
    {
        return applied;
    }
    public boolean isLocation()
    {
        return location;
    }
    public boolean isUnpaid()
    {
        return unpaid;
    }
    public boolean isMagic()
    {
        return magic;
    }
    public boolean isCursed()
    {
        return cursed;
    }
    public boolean isDamned()
    {
        return damned;
    }
    public boolean isOpen()
    {
        return open;
    }
    public boolean isNoPick()
    {
        return nopick;
    }
    public boolean isLocked()
    {
        return locked;
    }
    public int getLocation()
    {
        return mylocation;
    }
    public int getType()
    {
        return mytype;
    }
    public CfItem(int loc, int tag, int flags, int weight, Face face,
                  String name, String namepl, int nrof, int type)
    {
        mylocation = loc;
        mytag   = tag;
        myflags = flags;
        computeFlags();

        myweight = weight;
        myface = face;
        myname = name;
        mynrof = nrof;
        mynamepl = namepl;
        mytype = type;
    }
    public CfItem(int loc, int tag, int flags, int weight, Face face,
                  String name, String namepl, int nrof)
    {
        mylocation = loc;
        mytag   = tag;
        myflags = flags;
        computeFlags();

        myweight = weight;
        myface = face;
        myname = name;
        mynrof = nrof;
        mynamepl = namepl;
    }    protected void computeFlags()
    {        if ((myflags & CfItem.F_APPLIED)!=0)
        applied = true;
        else
            applied = false;

        if ((myflags & CfItem.F_LOCATION)!=0)
            location = true;
        else
            location = false;

        if ((myflags & CfItem.F_UNPAID)!=0)
            unpaid = true;
        else
            unpaid = false;

        if ((myflags & CfItem.F_MAGIC)!=0)
            magic = true;
        else
            magic = false;

        if ((myflags & CfItem.F_CURSED)!=0)
            cursed = true;
        else
            cursed = false;

        if ((myflags & CfItem.F_DAMNED)!=0)
            damned = true;
        else
            damned = false;

        if ((myflags & CfItem.F_OPEN)!=0)
            open = true;
        else
            open = false;

        if ((myflags & CfItem.F_NOPICK)!=0)
            nopick = true;
        else
            nopick = false;

        if ((myflags & CfItem.F_LOCKED)!=0)
            locked = true;
        else
            locked = false;
    }
}
