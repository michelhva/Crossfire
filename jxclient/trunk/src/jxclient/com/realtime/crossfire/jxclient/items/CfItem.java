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
package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
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
    private final int tag;

    private int flags;

    private int weight;

    private Face face;

    private String name;

    private String namePl;

    private int anim;

    private int animSpeed;

    private int nrof;

    private int location;

    private final int type;

    /**
     * Set if any attribute has changed since the last time listeners were
     * notified.
     */
    private boolean modified = true;

    public static final int F_APPLIED      = 0x000F;
    public static final int F_LOCATION     = 0x00F0;
    public static final int F_BLESSED      = 0x0100;
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

    public CfItem(final int location, final int tag, final int flags, final int weight, final Face face, final String name, final String namePl, final int anim, final int animSpeed, final int nrof, final int type)
    {
        this.location = location;
        this.tag = tag;
        this.flags = flags;
        this.weight = weight;
        this.face = face;
        this.name = name;
        this.namePl = namePl;
        this.anim = anim;
        this.animSpeed = animSpeed;
        this.nrof = nrof;
        this.type = type;
    }

    public void setFlags(final int flags)
    {
        if (this.flags != flags)
        {
            this.flags = flags;
            modified = true;
        }
    }

    public void setWeight(final int weight)
    {
        if (this.weight != weight)
        {
            this.weight = weight;
            modified = true;
        }
    }

    public void setFace(final Face face)
    {
        if (this.face != face)
        {
            this.face = face;
            modified = true;
        }
    }

    public void setName(final String name, final String namePl)
    {
        if (!this.name.equals(name) || !this.namePl.equals(namePl))
        {
            this.name = name;
            this.namePl = namePl;
            modified = true;
        }
    }

    public void setAnim(final int anim)
    {
        if (this.anim != anim)
        {
            this.anim = anim;
            modified = true;
        }
    }

    public void setAnimSpeed(final int animSpeed)
    {
        if (this.animSpeed != animSpeed)
        {
            this.animSpeed = animSpeed;
            modified = true;
        }
    }

    public void setNrOf(final int nrof)
    {
        if (this.nrof != nrof)
        {
            this.nrof = nrof;
            modified = true;
        }
    }

    /**
     * Update the location.
     *
     * @param location The new location.
     */
    public void setLocation(final int location)
    {
        this.location = location;
    }

    public int getTag()
    {
        return tag;
    }

    public int getWeight()
    {
        return weight;
    }

    public Face getFace()
    {
        return face;
    }

    public String getName()
    {
        return nrof > 1 ? namePl : name;
    }

    public int getNrOf()
    {
        return nrof;
    }

    public boolean isApplied()
    {
        return (flags&F_APPLIED) != 0;
    }

    public boolean isLocation()
    {
        return (flags&F_LOCATION) != 0;
    }

    public boolean isBlessed()
    {
        return (flags&F_BLESSED) != 0;
    }

    public boolean isUnpaid()
    {
        return (flags&F_UNPAID) != 0;
    }

    public boolean isMagic()
    {
        return (flags&F_MAGIC) != 0;
    }

    public boolean isCursed()
    {
        return (flags&F_CURSED) != 0;
    }

    public boolean isDamned()
    {
        return (flags&F_DAMNED) != 0;
    }

    public boolean isOpen()
    {
        return (flags&F_OPEN) != 0;
    }

    public boolean isNoPick()
    {
        return (flags&F_NOPICK) != 0;
    }

    public boolean isLocked()
    {
        return (flags&F_LOCKED) != 0;
    }

    public int getLocation()
    {
        return location;
    }

    public int getType()
    {
        return type;
    }

    public void update(final int updateFlags, final int flags, final int weight, final Face face, final String name, final String namePl, final int anim, final int animSpeed, final int nrof)
    {
        if ((updateFlags&UPD_FLAGS) != 0)
        {
            setFlags(flags);
        }
        if ((updateFlags&UPD_WEIGHT) != 0)
        {
            setWeight(weight);
        }
        if ((updateFlags&UPD_FACE) != 0)
        {
            setFace(face);
        }
        if ((updateFlags&UPD_NAME) != 0)
        {
            setName(name, namePl);
        }
        if ((updateFlags&UPD_ANIM) != 0)
        {
            setAnim(anim);
        }
        if ((updateFlags&UPD_ANIMSPEED) != 0)
        {
            setAnimSpeed(animSpeed);
        }
        if ((updateFlags&UPD_NROF) != 0)
        {
            setNrOf(nrof);
        }
        fireModified();
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

        for (final CfItemListener listener : listeners.getListeners(CfItemListener.class))
        {
            listener.itemModified();
        }
    }

    /**
     * Add a <code>CfItemModifiedListener</code>. The listener will be notified
     * about attribute changes of this item.
     *
     * @param listener the listener to remove
     */
    public void addCfItemModifiedListener(final CfItemListener listener)
    {
        listeners.add(CfItemListener.class, listener);
    }

    /**
     * Remove a <code>CfItemModifiedListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeCfItemModifiedListener(final CfItemListener listener)
    {
        listeners.remove(CfItemListener.class, listener);
    }

    /**
     * Returns a description suitable for a tooltip text.
     * @return the tooltip text
     */
    public String getTooltipText()
    {
        final StringBuilder sb = new StringBuilder(128);
        if (nrof > 1)
        {
            sb.append(nrof);
            sb.append(' ');
            sb.append(namePl);
        }
        else
        {
            sb.append(name);
        }
        final int totalWeight = nrof > 0 ? weight*nrof : weight;
        if (totalWeight > 0)
        {
            sb.append("<br>");
            if (totalWeight < 1000)
            {
                sb.append(totalWeight);
                sb.append(" g");
            }
            else if(totalWeight < 10000)
            {
                final int tmp = (totalWeight+50)/100;
                sb.append(tmp/10);
                sb.append('.');
                sb.append(tmp%10);
                sb.append(" kg");
            }
            else
            {
                final int tmp = (totalWeight+500)/1000;
                sb.append(tmp);
                sb.append(" kg");
            }
        }
        if ((flags&(F_BLESSED|F_MAGIC|F_CURSED|F_DAMNED|F_UNPAID)) != 0)
        {
            sb.append("<br>");
            appendFlag(sb, F_BLESSED, "blessed");
            appendFlag(sb, F_MAGIC, "magic");
            appendFlag(sb, F_CURSED, "cursed");
            appendFlag(sb, F_DAMNED, "damned");
            appendFlag(sb, F_UNPAID, "unpaid");
        }
        return sb.toString();
    }

    /**
     * Appends "(&lt;ident&gt;)" if this item has the flag <code>flag</code>
     * set.
     * @param sb the string builder to append to
     * @param flag the flag to check
     * @param ident the ident string to append
     */
    private void appendFlag(final StringBuilder sb, final int flag, final String ident)
    {
        if ((flags&flag) != 0)
        {
            sb.append('(');
            sb.append(ident);
            sb.append(')');
        }
    }
}
