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
    /**
     * The tag.
     */
    private final int tag;

    /**
     * The flags.
     */
    private int flags;

    /**
     * The weight.
     */
    private int weight;

    /**
     * The face.
     */
    private Face face;

    /**
     * The singular name.
     */
    private String name;

    /**
     * The plural name.
     */
    private String namePl;

    /**
     * The animation.
     */
    private int anim;

    /**
     * The animatin speed.
     */
    private int animSpeed;

    /**
     * The number of objects in the stack.
     */
    private int nrof;

    /**
     * The location.
     */
    private int location;

    /**
     * The type.
     */
    private final int type;

    /**
     * Set if any attribute has changed since the last time listeners were
     * notified.
     */
    private boolean modified = true;

    /**
     * The flags mask for applied states.
     */
    public static final int F_APPLIED      = 0x000F;

    /**
     * The location mask.
     */
    public static final int F_LOCATION     = 0x00F0;

    /**
     * The flags value for blessed items.
     */
    public static final int F_BLESSED      = 0x0100;

    /**
     * The flags value for unpaid items.
     */
    public static final int F_UNPAID       = 0x0200;

    /**
     * The flags value for magical items.
     */
    public static final int F_MAGIC        = 0x0400;

    /**
     * The flags value for cursed items.
     */
    public static final int F_CURSED       = 0x0800;

    /**
     * The flags value for damned items.
     */
    public static final int F_DAMNED       = 0x1000;

    /**
     * The flags value for opened items.
     */
    public static final int F_OPEN         = 0x2000;

    /**
     * The flags value for non-pickable items.
     */
    public static final int F_NOPICK       = 0x4000;

    /**
     * The flags value for locked items.
     */
    public static final int F_LOCKED       = 0x8000;

    /**
     * The update flags value for location updates.
     */
    public static final int UPD_LOCATION   = 0x01;

    /**
     * The update flags value for flags updates.
     */
    public static final int UPD_FLAGS      = 0x02;

    /**
     * The update flags value for weight updates.
     */
    public static final int UPD_WEIGHT     = 0x04;

    /**
     * The update flags value for face updates.
     */
    public static final int UPD_FACE       = 0x08;

    /**
     * The update flags value for name updates.
     */
    public static final int UPD_NAME       = 0x10;

    /**
     * The update flags value for animation updates.
     */
    public static final int UPD_ANIM       = 0x20;

    /**
     * The update flags value for animation speed updates.
     */
    public static final int UPD_ANIMSPEED  = 0x40;

    /**
     * The update flags value for nrof updates.
     */
    public static final int UPD_NROF       = 0x80;

    /**
     * The listeners to be notified.
     */
    private final EventListenerList listeners = new EventListenerList();

    /**
     * Creates a new instance.
     * @param location the location
     * @param tag the tag
     * @param flags the flags
     * @param weight the weight
     * @param face the face
     * @param name the singular name
     * @param namePl the plural name
     * @param anim the animation
     * @param animSpeed the animation speed
     * @param nrof the number of objects in the stack
     * @param type the type
     */
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

    /**
     * Updates the flags.
     * @param flags the new flags
     */
    public void setFlags(final int flags)
    {
        if (this.flags != flags)
        {
            this.flags = flags;
            modified = true;
        }
    }

    /**
     * Updates the weight.
     * @param weight the new weight
     */
    public void setWeight(final int weight)
    {
        if (this.weight != weight)
        {
            this.weight = weight;
            modified = true;
        }
    }

    /**
     * Updates the face.
     * @param face the new face
     */
    public void setFace(final Face face)
    {
        if (this.face != face)
        {
            this.face = face;
            modified = true;
        }
    }

    /**
     * Updates the name.
     * @param name the new singular name
     * @param namePl the new plural name
     */
    public void setName(final String name, final String namePl)
    {
        if (!this.name.equals(name) || !this.namePl.equals(namePl))
        {
            this.name = name;
            this.namePl = namePl;
            modified = true;
        }
    }

    /**
     * Updates the animation.
     * @param anim the new animation
     */
    public void setAnim(final int anim)
    {
        if (this.anim != anim)
        {
            this.anim = anim;
            modified = true;
        }
    }

    /**
     * Updates the animation speed.
     * @param animSpeed the new animation speed
     */
    public void setAnimSpeed(final int animSpeed)
    {
        if (this.animSpeed != animSpeed)
        {
            this.animSpeed = animSpeed;
            modified = true;
        }
    }

    /**
     * Updates the number of objects in the stack.
     * @param nrof the new number of objects
     */
    public void setNrOf(final int nrof)
    {
        if (this.nrof != nrof)
        {
            this.nrof = nrof;
            modified = true;
        }
    }

    /**
     * Updates the location.
     * @param location the new location
     */
    public void setLocation(final int location)
    {
        this.location = location;
    }

    /**
     * Returns the tag.
     * @return the tag
     */
    public int getTag()
    {
        return tag;
    }

    /**
     * Returns the weight.
     * @return the weight
     */
    public int getWeight()
    {
        return weight;
    }

    /**
     * Returns the face.
     * @return the face
     */
    public Face getFace()
    {
        return face;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName()
    {
        return nrof > 1 ? namePl : name;
    }

    /**
     * Returns the number of objects in this item stack.
     * @return whether the number of objects
     */
    public int getNrOf()
    {
        return nrof;
    }

    /**
     * Returns whether this item is applied.
     * @return whether this item is applied
     */
    public boolean isApplied()
    {
        return (flags&F_APPLIED) != 0;
    }

    public boolean isLocation()
    {
        return (flags&F_LOCATION) != 0;
    }

    /**
     * Returns whether this item is blessed.
     * @return whether this item is blessed
     */
    public boolean isBlessed()
    {
        return (flags&F_BLESSED) != 0;
    }

    /**
     * Returns whether this item is unpaid.
     * @return whether this item is unpaid
     */
    public boolean isUnpaid()
    {
        return (flags&F_UNPAID) != 0;
    }

    /**
     * Returns whether this item is magical.
     * @return whether this item is magical
     */
    public boolean isMagic()
    {
        return (flags&F_MAGIC) != 0;
    }

    /**
     * Returns whether this item is cursed.
     * @return whether this item is cursed
     */
    public boolean isCursed()
    {
        return (flags&F_CURSED) != 0;
    }

    /**
     * Returns whether this item is damned.
     * @return whether this item is damned
     */
    public boolean isDamned()
    {
        return (flags&F_DAMNED) != 0;
    }

    /**
     * Returns whether this item is an opened container.
     * @return whether this item is an opened container
     */
    public boolean isOpen()
    {
        return (flags&F_OPEN) != 0;
    }

    /**
     * Returns whether this item cannot be picked up.
     * @return whether this item cannot be picked up
     */
    public boolean isNoPick()
    {
        return (flags&F_NOPICK) != 0;
    }

    /**
     * Returns whether this item is locked.
     * @return whether this item is locked
     */
    public boolean isLocked()
    {
        return (flags&F_LOCKED) != 0;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public int getLocation()
    {
        return location;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * Processes an "upditem" command.
     * @param updateFlags the changed values
     * @param flags the new flags
     * @param weight the new weight
     * @param face the new face
     * @param name the new singular name
     * @param namePl the new plural name
     * @param anim the new animation ID
     * @param animSpeed the new animation speed
     * @param nrof the new number of items
     */
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
     * Notify all listener.
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
        final String tooltipText1 = getTooltipText1();
        final String tooltipText2 = getTooltipText2();
        final String tooltipText3 = getTooltipText3();
        final StringBuilder sb = new StringBuilder(tooltipText1);
        if (tooltipText2.length() > 0)
        {
            sb.append("<br>");
            sb.append(tooltipText2);
        }
        if (tooltipText3.length() > 0)
        {
            sb.append("<br>");
            sb.append(tooltipText3);
        }
        return sb.toString();
    }

    /**
     * Returns the first line of the tooltip text.
     * @return the tooltip text
     */
    public String getTooltipText1()
    {
        return nrof > 1 ? nrof+" "+namePl : name;
    }

    /**
     * Returns the second line of the tooltip text.
     * @return the tooltip text
     */
    public String getTooltipText2()
    {
        final int totalWeight = nrof > 0 ? weight*nrof : weight;
        if (totalWeight <= 0)
        {
            return "";
        }
        if (totalWeight < 1000)
        {
            return totalWeight+" g";
        }
        if(totalWeight < 10000)
        {
            final int tmp = (totalWeight+50)/100;
            return tmp/10+"."+tmp%10+" kg";
        }
        final int tmp = (totalWeight+500)/1000;
        return tmp+" kg";
    }

    /**
     * Returns the third line of the tooltip text.
     * @return the tooltip text
     */
    public String getTooltipText3()
    {
        final StringBuilder sb = new StringBuilder();
        appendFlag(sb, F_BLESSED, "blessed");
        appendFlag(sb, F_MAGIC, "magic");
        appendFlag(sb, F_CURSED, "cursed");
        appendFlag(sb, F_DAMNED, "damned");
        appendFlag(sb, F_UNPAID, "unpaid");
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

    /**
     * Returns whether this object is a fake object for selecting object groups
     * in the ground view.
     * @return whether this object is a group button
     */
    public boolean isItemGroupButton()
    {
        return flags == 0 && weight == -1 && nrof == 0 && type == 0 && name.startsWith("Click here to see ");
    }
}
