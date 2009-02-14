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

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CurrentFloorListener;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Image;
import java.util.List;

/**
 * A {@link GUIElement} representing an in-game object in the ground view.
 * @author Andreas Kirschbaum
 */
public class GUIItemFloor extends GUIItemItem
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The command queue for sending commands.
     */
    private final CommandQueue commandQueue;

    /**
     * The connection instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link ItemsManager} instance to watch.
     */
    private final ItemsManager itemsManager;

    /**
     * The {@link FacesManager} instance to use.
     */
    private final FacesManager facesManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The currently shown index. It is the item's index for ground view, and
     * +1 for container view; index 0 for container view is the container
     * itself.
     */
    private int index = -1;

    /**
     * Whether currently a container is shown: 0=ground view, else=container
     * view.
     */
    private int containerTag;

    /**
     * The {@link LocationListener} used to detect items added to or removed
     * from this floor tile.
     */
    private final LocationListener floorLocationListener = new LocationListener()
    {
        /** {@inheritDoc} */
        @Override
        public void locationModified(final int index, final CfItem item)
        {
            if (containerTag != 0)
            {
                assert GUIItemFloor.this.index >= 1;
                assert index+1 == GUIItemFloor.this.index;
            }
            else
            {
                assert index == GUIItemFloor.this.index;
            }
            setItem(item);
        }
    };

    /**
     * The {@link CurrentFloorListener} used to be informed when the current
     * floor location changes.
     */
    private final CurrentFloorListener currentFloorListener = new CurrentFloorListener()
    {
        /** {@inheritDoc} */
        @Override
        public void currentFloorChanged(final int currentFloor)
        {
            setIndex(index, currentFloor, true);
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param windowRenderer the window renderer to notify
     * @param commandQueue the command queue for sending commands
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param itemPainter the item painter for painting the icon
     * @param index the initial scroll index
     * @param crossfireServerConnection the connection instance
     * @param itemsManager the items manager instance to use
     * @param facesManager the faces manager instance to use
     */
    public GUIItemFloor(final TooltipManager tooltipManager, final JXCWindowRenderer windowRenderer, final CommandQueue commandQueue, final String name, final int x, final int y, final int w, final int h, final ItemPainter itemPainter, final int index, final CrossfireServerConnection crossfireServerConnection, final ItemsManager itemsManager, final FacesManager facesManager)
    {
        super(tooltipManager, windowRenderer, name, x, y, w, h, crossfireServerConnection, itemPainter, facesManager);
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.itemsManager = itemsManager;
        this.facesManager = facesManager;
        defaultIndex = index;
        containerTag = itemsManager.getCurrentFloorManager().getCurrentFloor();
        this.itemsManager.getCurrentFloorManager().addCurrentFloorListener(currentFloorListener);
        setIndex(index, containerTag, false);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        super.dispose();
        setIndex(-1, 0, false);
        itemsManager.getCurrentFloorManager().removeCurrentFloorListener(currentFloorListener);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            return index+distance < itemsManager.getNumberOfItems(itemsManager.getCurrentFloorManager().getCurrentFloor());
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    @Override
    public void scroll(final int distance)
    {
        setIndex(index+distance, containerTag, false);
    }

    /* {@inheritDoc} */
    @Override
    public void resetScroll()
    {
        setIndex(defaultIndex, containerTag, false);
    }

    /* {@inheritDoc} */
    @Override
    public void button1Clicked(final JXCWindow window, final int modifiers)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        if (item.isItemGroupButton())
        {
            crossfireServerConnection.sendApply(item.getTag());
        }
        else
        {
            crossfireServerConnection.sendExamine(item.getTag());
        }
    }

    /* {@inheritDoc} */
    @Override
    public void button3Clicked(final JXCWindow window, final int modifiers)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        if (itemsManager.getPlayer() != null)
        {
            commandQueue.sendMove(itemsManager.getPlayer().getTag(), item.getTag());
        }
    }

    /**
     * Set the floor tile to display.
     *
     * @param index the floor tile
     *
     * @param containerTag The new container tag.
     *
     * @param forced if unset, do nothing if the <code>index</code> is
     * unchanged; if set, always render the item
     */
    private void setIndex(final int index, final int containerTag, final boolean forced)
    {
        if (!forced && this.index == index)
        {
            return;
        }

        if (this.index >= 0)
        {
            if (this.containerTag != 0)
            {
                if (this.index > 0)
                {
                    itemsManager.getFloorManager().removeLocationListener(this.index-1, floorLocationListener);
                }
                else
                {
                    // index 0 is the container itself -- no listener needed
                }
            }
            else
            {
                itemsManager.getFloorManager().removeLocationListener(this.index, floorLocationListener);
            }
        }
        this.index = index;
        this.containerTag = containerTag;
        if (this.index >= 0)
        {
            if (this.containerTag != 0)
            {
                if (this.index > 0)
                {
                    itemsManager.getFloorManager().addLocationListener(this.index-1, floorLocationListener);
                }
                else
                {
                    // index 0 is the container itself -- no listener needed
                }
            }
            else
            {
                itemsManager.getFloorManager().addLocationListener(this.index, floorLocationListener);
            }
        }

        if (this.containerTag == 0)
        {
            final List<CfItem> list = itemsManager.getItems(itemsManager.getCurrentFloorManager().getCurrentFloor());
            setItem(0 <= this.index && this.index < list.size() ? list.get(this.index) : null);
        }
        else if (this.index > 0)
        {
            final List<CfItem> list = itemsManager.getItems(itemsManager.getCurrentFloorManager().getCurrentFloor());
            setItem(this.index-1 < list.size() ? list.get(this.index-1) : null);
        }
        else
        {
            setItem(itemsManager.getItem(containerTag));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Image getFace(final CfItem item)
    {
        if (!item.isItemGroupButton())
        {
            return facesManager.getOriginalImageIcon(item.getFace().getFaceNum()).getImage();
        }

        /*
         * replace empty.111 with arrows for "Click here for next/previous
         * group of items".
         */
        return index > 0 ? facesManager.getNextGroupFace() : facesManager.getPrevGroupFace();
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(final boolean selected)
    {
        // ignore: floor objects are never selected
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isSelected()
    {
        return false;
    }
}
