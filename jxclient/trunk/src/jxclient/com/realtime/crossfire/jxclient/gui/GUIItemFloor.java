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

import com.realtime.crossfire.jxclient.CfItem;
import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.items.CurrentFloorListener;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemFloor extends GUIItemItem
{
    private int myindex = -1;

    /**
     * The {@link LocationListener} used to detect items added to or removed
     * from this floor tile.
     */
    private final LocationListener floorLocationListener = new LocationListener()
    {
        /** {@inheritDoc} */
        public void locationModified(final int index, final CfItem item)
        {
            assert index == myindex;
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
            setIndex(myindex, true);
        }
    };

    public GUIItemFloor(final JXCWindow jxcWindow, final String nn, final int nx, final int ny, final int nw, final int nh, final BufferedImage picture, final BufferedImage pic_cursed, final BufferedImage pic_applied, final BufferedImage pic_selector, final BufferedImage pic_locked, final int index, final CrossfireServerConnection msc, final Font mft, final Color nrofColor)
    {
        super(jxcWindow, nn, nx, ny, nw, nh, picture, pic_cursed, pic_applied, pic_selector, pic_locked, msc, mft, nrofColor);
        ItemsList.getItemsManager().addCurrentFloorListener(currentFloorListener);
        setIndex(index, false);
        render();
    }

    public int getIndex()
    {
        return myindex;
    }

    /** {@inheritDoc} */
    public boolean canScrollUp()
    {
        return myindex > 0;
    }

    public void scrollUp()
    {
        setIndex(myindex-1, false);
    }

    /** {@inheritDoc} */
    public boolean canScrollDown()
    {
        return myindex+1 < ItemsList.getItemsManager().getItems(ItemsList.getItemsManager().getCurrentFloor()).size();
    }

    public void scrollDown()
    {
        setIndex(myindex+1, false);
    }

    protected void button1Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        try
        {
            jxcw.getCrossfireServerConnection().sendExamine(item.getTag());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    protected void button3Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        try
        {
            if (ItemsList.getItemsManager().getPlayer() != null)
            {
                jxcw.getCrossfireServerConnection().sendMove(ItemsList.getItemsManager().getPlayer().getTag(), item.getTag(), jxcw.getRepeatCount());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Set the floor tile to display.
     *
     * @param index the floor tile
     *
     * @param forced if unset, do nothing if the <code>index</code> is
     * unchanged; if set, always render the item
     */
    private void setIndex(final int index, final boolean forced)
    {
        if (!forced && myindex == index)
        {
            return;
        }

        if (myindex >= 0)
        {
            ItemsList.getItemsManager().removeFloorLocationListener(myindex, floorLocationListener);
        }
        myindex = index;
        if (myindex >= 0)
        {
            ItemsList.getItemsManager().addFloorLocationListener(myindex, floorLocationListener);
        }

        final List<CfItem> list = ItemsList.getItemsManager().getItems(ItemsList.getItemsManager().getCurrentFloor());
        setItem(0 <= myindex && myindex < list.size() ? list.get(myindex) : null);
    }
}
