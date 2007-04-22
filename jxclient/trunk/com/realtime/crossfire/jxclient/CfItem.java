//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient;

import javax.swing.event.EventListenerList;

/**
 * The representation of a Crossfire Item, client-side.
 * @version 1.0
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @since 1.0
 */
public class CfItem
{
    private final int mytag;
    private int myflags;
    private int myweight;
    private Face myface;
    private String myname;
    private String mynamepl;
    private int mynrof;
    private final int mylocation;
    private final int mytype;

    private boolean applied = false;
    private boolean location = false;
    private boolean unpaid = false;
    private boolean magic = false;
    private boolean cursed = false;
    private boolean damned = false;
    private boolean open = false;
    private boolean nopick = false;
    private boolean locked = false;

    /**
     * Set if any attribute has changed since the last time listeners were
     * notified.
     */
    private boolean modified = true;

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

    /**
     * The listeners to be notified.
     */
    private final EventListenerList listeners = new EventListenerList();

    public void setFlags(int nv)
    {
        if (myflags != nv)
        {
            myflags = nv;
            computeFlags();
            modified = true;
        }
    }
    public void setWeight(int nv)
    {
        if (myweight != nv)
        {
            myweight = nv;
            modified = true;
        }
    }
    public void setFace(Face f)
    {
        if (myface != f)
        {
            myface = f;
            modified = true;
        }
    }
    public void setName(String n, String npl)
    {
        if (!myname.equals(n) || !npl.equals(npl))
        {
            myname = n;
            mynamepl = npl;
            modified = true;
        }
    }
    public void setNrOf(int nv)
    {
        if (mynrof != nv)
        {
            mynrof = nv;
            modified = true;
        }
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
        mytype = -1;
    }

    /**
     * Notify all listener
     */
    public void fireModified()
    {
        if (!modified)
        {
            return;
        }
        modified = false;

        for (final CfItemModifiedListener listener : listeners.getListeners(CfItemModifiedListener.class)) {
            listener.itemModified(this);
        }
    }

    /**
     * Add a <code>CfItemModifiedListener</code>. The listener will be notified
     * about attribute changes of this item.
     *
     * @param listener the listener to remove
     */
    public void addCfItemModifiedListener(final CfItemModifiedListener listener)
    {
        listeners.add(CfItemModifiedListener.class, listener);
    }

    /**
     * Remove a <code>CfItemModifiedListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeCfItemModifiedListener(final CfItemModifiedListener listener)
    {
        listeners.remove(CfItemModifiedListener.class, listener);
    }

    /**
     * Expand the flags bitmask {@link #myflags} into separate boolean values.
     */
    private void computeFlags()
    {
        applied = (myflags & CfItem.F_APPLIED) != 0;
        location = (myflags & CfItem.F_LOCATION) != 0;
        unpaid = (myflags & CfItem.F_UNPAID) != 0;
        magic = (myflags & CfItem.F_MAGIC) != 0;
        cursed = (myflags & CfItem.F_CURSED) != 0;
        damned = (myflags & CfItem.F_DAMNED) != 0;
        open = (myflags & CfItem.F_OPEN) != 0;
        nopick = (myflags & CfItem.F_NOPICK) != 0;
        locked = (myflags & CfItem.F_LOCKED) != 0;
    }
}
