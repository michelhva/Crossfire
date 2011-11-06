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
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.misc.Modifiers;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIElement} representing an in-game object in the ground view.
 * @author Andreas Kirschbaum
 */
public class GUIItemFloor extends GUIItemItem {

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
     * The connection instance.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The floor view to watch.
     */
    @NotNull
    private final ItemView floorView;

    /**
     * The {@link ItemSet} to use.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link FacesManager} instance to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The face to substitute into "Click here for next group of items".
     */
    @NotNull
    private final Image nextGroupFace;

    /**
     * The face to substitute into "Click here for previous group of items".
     */
    @NotNull
    private final Image prevGroupFace;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The currently shown index. It is the item's index for ground view, and +1
     * for container view; index 0 for container view is the container itself.
     */
    private int index = -1;

    /**
     * The {@link LocationListener} used to detect items added to or removed
     * from this floor tile.
     */
    @NotNull
    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void locationChanged() {
            setIndex(index, true);
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue for sending commands
     * @param name the name of this element
     * @param itemPainter the item painter for painting the icon
     * @param index the initial scroll index
     * @param crossfireServerConnection the connection instance
     * @param floorView the floor view to use
     * @param itemSet the item set to use
     * @param facesManager the faces manager instance to use
     * @param nextGroupFace the image for "prev group of items"
     * @param prevGroupFace the image for "next group of items"
     */
    public GUIItemFloor(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int index, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final ItemView floorView, @NotNull final ItemSet itemSet, @NotNull final FacesManager facesManager, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.floorView = floorView;
        this.itemSet = itemSet;
        this.facesManager = facesManager;
        this.nextGroupFace = nextGroupFace;
        this.prevGroupFace = prevGroupFace;
        defaultIndex = index;
        setIndex(index, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        setIndex(-1, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        } else if (distance > 0) {
            return index+distance < floorView.getSize();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scroll(final int distance) {
        setIndex(index+distance, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setIndex(defaultIndex, false);
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
            if (item.isItemGroupButton()) {
                crossfireServerConnection.sendApply(item.getTag());
            } else {
                crossfireServerConnection.sendExamine(item.getTag());
            }
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
            break;
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

        final CfItem player = itemSet.getPlayer();
        if (player == null) {
            return;
        }

        switch (modifiers&Modifiers.MASK) {
        case Modifiers.NONE:
            commandQueue.sendMove(player.getTag(), item.getTag());
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
    public int getIndex() {
        return index;
    }

    /**
     * Set the floor tile to display.
     * @param index the floor tile
     * @param forced if unset, do nothing if the <code>index</code> is
     * unchanged; if set, always render the item
     */
    private void setIndex(final int index, final boolean forced) {
        if (this.index != index) {
            if (this.index >= 0) {
                floorView.removeLocationListener(this.index, locationListener);
            }
            this.index = index;
            if (this.index >= 0) {
                floorView.addLocationListener(this.index, locationListener);
            }
        } else if (!forced) {
            return;
        }

        setItem(floorView.getItem(index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexNoListeners(final int index) {
        this.index = index;

        setItemNoListeners(floorView.getItem(index));
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        if (!item.isItemGroupButton()) {
            return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
        }

        /*
         * replace empty.111 with arrows for "Click here for next/previous
         * group of items". When a container is opened, the first item
         * will be the 'prev' arrow, so take that into account.
         */
        final int min = itemSet.getOpenContainer() == 0 ? 0 : 1;
        return index > min ? nextGroupFace : prevGroupFace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final boolean selected) {
        // ignore: floor objects are never selected
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSelected() {
        return false;
    }

}
