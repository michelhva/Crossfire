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

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.Modifiers;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemListener;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIItem} for displaying inventory objects.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventory extends GUIItemItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The server instance.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FacesManager} instance to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The inventory view to watch.
     */
    @NotNull
    private final ItemView inventoryView;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The object used for synchronization on {@link #index}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The inventory slot index.
     */
    private int index = -1;

    /**
     * If set, paint the element in "selected" state.
     */
    private boolean selected = false;

    /**
     * The {@link ItemListener} used to detect items added to or removed from
     * this inventory slot.
     */
    @NotNull
    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void locationChanged() {
            setChanged();
            updateTooltipText();
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue for sending commands
     * @param name the name of this element
     * @param index the default scroll index
     * @param crossfireServerConnection the connection instance
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager instance to use
     * @param floorView the floor view to use
     * @param inventoryView the inventory view to watch
     */
    public GUIItemInventory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, final String name, @NotNull final ItemPainter itemPainter, final int index, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final FloorView floorView, @NotNull final ItemView inventoryView) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.floorView = floorView;
        defaultIndex = index;
        this.inventoryView = inventoryView;
        setIndex(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        setIndex(-1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            synchronized (sync) {
                return index >= -distance;
            }
        } else if (distance > 0) {
            synchronized (sync) {
                return index+distance < inventoryView.getSize();
            }
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scroll(final int distance) {
        synchronized (sync) {
            setIndex(index+distance);
        }
        setChanged();
        updateTooltipText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button1Clicked(final int modifiers) {
        final CfItem item = getItem();
        if (item == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            crossfireServerConnection.sendExamine(item.getTag());
            break;

        case Modifiers.SHIFT:
            crossfireServerConnection.sendLock(!item.isLocked(), item.getTag());
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(final int modifiers) {
        final CfItem item = getItem();
        if (item == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            crossfireServerConnection.sendApply(item.getTag());
            return;

        case Modifiers.SHIFT:
            crossfireServerConnection.sendMark(item.getTag());
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(final int modifiers) {
        final CfItem item = getItem();
        if (item == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            if (item.isLocked()) {
                crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+left-clicking on it.", 3);
                return;
            }

            commandQueue.sendMove(floorView.getCurrentFloor(), item.getTag());
            break;

        case Modifiers.SHIFT:
            crossfireServerConnection.sendApply(item.getTag());
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    /**
     * Set the inventory slot to display.
     * @param index the inventory slot
     */
    private void setIndex(final int index) {
        synchronized (sync) {
            if (this.index == index) {
                return;
            }

            if (this.index >= 0) {
                inventoryView.removeLocationListener(this.index, locationListener);
            }
            this.index = index;
            if (this.index >= 0) {
                inventoryView.addLocationListener(this.index, locationListener);
            }
        }

        setItem(inventoryView.getItem(this.index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(inventoryView.getItem(this.index));
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChanged() {
        super.setChanged();
        setItem(inventoryView.getItem(index));
    }

}
