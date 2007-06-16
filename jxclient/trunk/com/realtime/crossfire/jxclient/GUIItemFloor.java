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

import java.awt.Font;
import java.io.IOException;
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

    public GUIItemFloor(String nn, int nx, int ny, int nw, int nh, String picture, String pic_cursed, String pic_applied, String pic_selector, String pic_locked, int index, CrossfireServerConnection msc, Font mft) throws IOException
    {
        super(nn, nx, ny, nw, nh, picture, pic_cursed, pic_applied, pic_selector, pic_locked, msc, mft);
        ItemsList.addCurrentFloorListener(currentFloorListener);
        setIndex(index, false);
        render();
    }

    public int getIndex()
    {
        return myindex;
    }

    public void scrollUp()
    {
        setIndex(myindex-1, false);
    }

    public void scrollDown()
    {
        setIndex(myindex+1, false);
    }

    protected void button1Clicked(JXCWindow jxcw)
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

    protected void button3Clicked(JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }
        try
        {
            if (ItemsList.getPlayer() != null)
            {
                jxcw.getCrossfireServerConnection().sendMove(ItemsList.getPlayer().getTag(), item.getTag(), jxcw.getRepeatCount());
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
            ItemsList.removeFloorLocationListener(myindex, floorLocationListener);
        }
        myindex = index;
        if (myindex >= 0)
        {
            ItemsList.addFloorLocationListener(myindex, floorLocationListener);
        }

        final List<CfItem> list = ItemsList.getItems(ItemsList.getCurrentFloor());
        setItem(0 <= myindex && myindex < list.size() ? list.get(myindex) : null);
    }
}
