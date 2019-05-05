/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.items;

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.protocol.UpdItem;
import com.realtime.crossfire.jxclient.util.EventListenerList2;
import org.jetbrains.annotations.NotNull;

/**
 * The representation of a Crossfire Item, client-side.
 * @author Lauwenmark
 * @author Andreas Kirschbaum
 * @version 1.0
 * @since 1.0
 */
public class CfItem {

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
    @NotNull
    private Face face;

    /**
     * The singular name.
     */
    @NotNull
    private String name;

    /**
     * The plural name.
     */
    @NotNull
    private String namePl;

    /**
     * The animation.
     */
    private int anim;

    /**
     * The animation speed.
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
    public static final int F_APPLIED = 0x000F;

    /**
     * The flags value for unidentified items.
     */
    public static final int F_UNIDENTIFIED = 0x0010;

    /**
     * The flags value for blessed items.
     */
    public static final int F_BLESSED = 0x0100;

    /**
     * The flags value for unpaid items.
     */
    public static final int F_UNPAID = 0x0200;

    /**
     * The flags value for magical items.
     */
    public static final int F_MAGIC = 0x0400;

    /**
     * The flags value for cursed items.
     */
    public static final int F_CURSED = 0x0800;

    /**
     * The flags value for damned items.
     */
    public static final int F_DAMNED = 0x1000;

    /**
     * The flags value for opened items.
     */
    public static final int F_OPEN = 0x2000;

    /**
     * The flags value for non-pickable items.
     */
    public static final int F_NOPICK = 0x4000;

    /**
     * The flags value for locked items.
     */
    public static final int F_LOCKED = 0x8000;

    /**
     * The listeners to be notified.
     */
    private final EventListenerList2<CfItemListener> listeners = new EventListenerList2<>();

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
    public CfItem(final int location, final int tag, final int flags, final int weight, @NotNull final Face face, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof, final int type) {
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
    private void setFlags(final int flags) {
        if (this.flags != flags) {
            this.flags = flags;
            modified = true;
        }
    }

    /**
     * Updates the weight.
     * @param weight the new weight
     */
    private void setWeight(final int weight) {
        if (this.weight != weight) {
            this.weight = weight;
            modified = true;
        }
    }

    /**
     * Updates the face.
     * @param face the new face
     */
    private void setFace(@NotNull final Face face) {
        if (this.face != face) {
            this.face = face;
            modified = true;
        }
    }

    /**
     * Updates the name.
     * @param name the new singular name
     * @param namePl the new plural name
     */
    private void setName(@NotNull final String name, @NotNull final String namePl) {
        if (!this.name.equals(name) || !this.namePl.equals(namePl)) {
            this.name = name;
            this.namePl = namePl;
            modified = true;
        }
    }

    /**
     * Updates the animation.
     * @param anim the new animation
     */
    private void setAnim(final int anim) {
        if (this.anim != anim) {
            this.anim = anim;
            modified = true;
        }
    }

    /**
     * Updates the animation speed.
     * @param animSpeed the new animation speed
     */
    private void setAnimSpeed(final int animSpeed) {
        if (this.animSpeed != animSpeed) {
            this.animSpeed = animSpeed;
            modified = true;
        }
    }

    /**
     * Updates the number of objects in the stack.
     * @param nrof the new number of objects
     */
    private void setNrOf(final int nrof) {
        if (this.nrof != nrof) {
            this.nrof = nrof;
            modified = true;
        }
    }

    /**
     * Updates the location.
     * @param location the new location
     */
    public void setLocation(final int location) {
        this.location = location;
    }

    /**
     * Returns the tag.
     * @return the tag
     */
    public int getTag() {
        return tag;
    }

    /**
     * Returns the weight.
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Returns the face.
     * @return the face
     */
    @NotNull
    public Face getFace() {
        return face;
    }

    /**
     * Returns the name.
     * @return the name
     */
    @NotNull
    public String getName() {
        return nrof > 1 ? namePl : name;
    }

    /**
     * Returns the number of objects in this item stack.
     * @return whether the number of objects
     */
    public int getNrOf() {
        return nrof;
    }

    /**
     * Returns whether this item is applied.
     * @return whether this item is applied
     */
    public boolean isApplied() {
        return (flags&F_APPLIED) != 0;
    }

    /**
     * Returns whether this item is unidentified.
     * @return whether this item is unidentified
     */
    public boolean isUnidentified() {
        return (flags&F_UNIDENTIFIED) != 0;
    }

    /**
     * Returns whether this item is blessed.
     * @return whether this item is blessed
     */
    public boolean isBlessed() {
        return (flags&F_BLESSED) != 0;
    }

    /**
     * Returns whether this item is unpaid.
     * @return whether this item is unpaid
     */
    public boolean isUnpaid() {
        return (flags&F_UNPAID) != 0;
    }

    /**
     * Returns whether this item is magical.
     * @return whether this item is magical
     */
    public boolean isMagic() {
        return (flags&F_MAGIC) != 0;
    }

    /**
     * Returns whether this item is cursed.
     * @return whether this item is cursed
     */
    public boolean isCursed() {
        return (flags&F_CURSED) != 0;
    }

    /**
     * Returns whether this item is damned.
     * @return whether this item is damned
     */
    public boolean isDamned() {
        return (flags&F_DAMNED) != 0;
    }

    /**
     * Returns whether this item is an opened container.
     * @return whether this item is an opened container
     */
    public boolean isOpen() {
        return (flags&F_OPEN) != 0;
    }

    /**
     * Returns whether this item cannot be picked up.
     * @return whether this item cannot be picked up
     */
    public boolean isNoPick() {
        return (flags&F_NOPICK) != 0;
    }

    /**
     * Returns whether this item is locked.
     * @return whether this item is locked
     */
    public boolean isLocked() {
        return (flags&F_LOCKED) != 0;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public int getLocation() {
        return location;
    }

    /**
     * Returns the type.
     * @return the type
     */
    public int getType() {
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
    public void update(final int updateFlags, final int flags, final int weight, @NotNull final Face face, @NotNull final String name, @NotNull final String namePl, final int anim, final int animSpeed, final int nrof) {
        if ((updateFlags&UpdItem.UPD_FLAGS) != 0) {
            setFlags(flags);
        }
        if ((updateFlags&UpdItem.UPD_WEIGHT) != 0) {
            setWeight(weight);
        }
        if ((updateFlags&UpdItem.UPD_FACE) != 0) {
            setFace(face);
        }
        if ((updateFlags&UpdItem.UPD_NAME) != 0) {
            setName(name, namePl);
        }
        if ((updateFlags&UpdItem.UPD_ANIM) != 0) {
            setAnim(anim);
        }
        if ((updateFlags&UpdItem.UPD_ANIMSPEED) != 0) {
            setAnimSpeed(animSpeed);
        }
        if ((updateFlags&UpdItem.UPD_NROF) != 0) {
            setNrOf(nrof);
        }
        fireModified();
    }

    /**
     * Notify all listener.
     */
    private void fireModified() {
        if (!modified) {
            return;
        }
        modified = false;

        for (CfItemListener listener : listeners) {
            listener.itemModified();
        }
    }

    /**
     * Add a {@code CfItemModifiedListener}. The listener will be notified about
     * attribute changes of this item.
     * @param listener the listener to remove
     */
    public void addCfItemModifiedListener(@NotNull final CfItemListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a {@code CfItemModifiedListener}.
     * @param listener the listener to remove
     */
    public void removeCfItemModifiedListener(@NotNull final CfItemListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a description suitable for a tooltip text.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText() {
        final String tooltipText1 = getTooltipText1();
        final String tooltipText2 = getTooltipText2();
        final String tooltipText3 = getTooltipText3();
        final StringBuilder sb = new StringBuilder(tooltipText1);
        if (!tooltipText2.isEmpty()) {
            sb.append("<br>");
            sb.append(tooltipText2);
        }
        if (!tooltipText3.isEmpty()) {
            sb.append("<br>");
            sb.append(tooltipText3);
        }
        return sb.toString();
    }

    /**
     * Returns the first line of the tooltip text.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText1() {
        return nrof > 1 ? nrof+" "+namePl : name;
    }

    /**
     * Returns the second line of the tooltip text.
     * @return the tooltip text
     */
    @NotNull
    public String getTooltipText2() {
        final int totalWeight = nrof > 0 ? weight*nrof : weight;
        if (totalWeight <= 0) {
            return "";
        }
        if (totalWeight < 1000) {
            return totalWeight+" g";
        }
        if (totalWeight < 10000) {
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
    @NotNull
    public String getTooltipText3() {
        final StringBuilder sb = new StringBuilder();
        appendFlag(sb, F_UNIDENTIFIED, "unidentified");
        appendFlag(sb, F_BLESSED, "blessed");
        appendFlag(sb, F_MAGIC, "magic");
        appendFlag(sb, F_CURSED, "cursed");
        appendFlag(sb, F_DAMNED, "damned");
        appendFlag(sb, F_UNPAID, "unpaid");
        return sb.toString();
    }

    /**
     * Appends "(&lt;ident&gt;)" if this item has the flag {@code flag} set.
     * @param sb the string builder to append to
     * @param flag the flag to check
     * @param ident the ident string to append
     */
    private void appendFlag(@NotNull final StringBuilder sb, final int flag, @NotNull final String ident) {
        if ((flags&flag) != 0) {
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
    public boolean isItemGroupButton() {
        return flags == 0 && weight == -1 && nrof == 0 && type == 0 && name.startsWith("Click here to see ");
    }

}
