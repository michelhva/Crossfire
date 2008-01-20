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

package com.realtime.crossfire.jxclient.gui;

import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CurrentFloorListener;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemFloor extends GUIItemItem
{
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
        public void currentFloorChanged(final int currentFloor)
        {
            setIndex(index, currentFloor, true);
        }
    };

    public GUIItemFloor(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int index, final CrossfireServerConnection crossfireServerConnection, final Font font, final Color nrofColor)
    {
        super(jxcWindow, name, x, y, w, h, image, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font, nrofColor);
        defaultIndex = index;
        containerTag = ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor();
        ItemsList.getItemsManager().getCurrentFloorManager().addCurrentFloorListener(currentFloorListener);
        setIndex(index, containerTag, false);
        render();
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            return index >= -distance;
        }
        else if (distance > 0)
        {
            return index+distance < ItemsList.getItemsManager().getNumberOfItems(ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor());
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    public void scroll(final int distance)
    {
        setIndex(index+distance, containerTag, false);
    }

    /* {@inheritDoc} */
    public void resetScroll()
    {
        setIndex(defaultIndex, containerTag, false);
    }

    /* {@inheritDoc} */
    @Override protected void button1Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        jxcw.getCrossfireServerConnection().sendExamine(item.getTag());
    }

    /* {@inheritDoc} */
    @Override protected void button3Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        if (ItemsList.getItemsManager().getPlayer() != null)
        {
            jxcw.getCrossfireServerConnection().sendMove(ItemsList.getItemsManager().getPlayer().getTag(), item.getTag(), jxcw.getCommandQueue().getRepeatCount());
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
                    ItemsList.getItemsManager().getFloorManager().removeLocationListener(this.index-1, floorLocationListener);
                }
                else
                {
                    // index 0 is the container itself -- no listener needed
                }
            }
            else
            {
                ItemsList.getItemsManager().getFloorManager().removeLocationListener(this.index, floorLocationListener);
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
                    ItemsList.getItemsManager().getFloorManager().addLocationListener(this.index-1, floorLocationListener);
                }
                else
                {
                    // index 0 is the container itself -- no listener needed
                }
            }
            else
            {
                ItemsList.getItemsManager().getFloorManager().addLocationListener(this.index, floorLocationListener);
            }
        }

        if (this.containerTag == 0)
        {
            final List<CfItem> list = ItemsList.getItemsManager().getItems(ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor());
            setItem(0 <= this.index && this.index < list.size() ? list.get(this.index) : null);
        }
        else if(this.index > 0)
        {
            final List<CfItem> list = ItemsList.getItemsManager().getItems(ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor());
            setItem(this.index-1 < list.size() ? list.get(this.index-1) : null);
        }
        else
        {
            setItem(ItemsList.getItemsManager().getItem(containerTag));
        }
    }
}
