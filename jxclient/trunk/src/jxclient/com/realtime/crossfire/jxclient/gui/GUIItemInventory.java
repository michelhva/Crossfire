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
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemInventory extends GUIItemItem
{
    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    private int index = -1;

    /**
     * The {@link LocationListener} used to detect items added to or removed
     * from this inventory slot.
     */
    private final LocationListener inventoryLocationListener = new LocationListener()
    {
        /** {@inheritDoc} */
        public void locationModified(final int index, final CfItem item)
        {
            assert index == GUIItemInventory.this.index;
            setItem(item);
        }
    };

    public GUIItemInventory(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int index, final CrossfireServerConnection crossfireServerConnection, final Font font, final Color nrofColor)
    {
        super(jxcWindow, name, x, y, w, h, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font, nrofColor);
        defaultIndex = index;
        setIndex(index);
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
            final CfPlayer player = ItemsList.getItemsManager().getPlayer();
            if (player == null)
            {
                return false;
            }

            final List<CfItem> list = ItemsList.getItemsManager().getItems(player.getTag());
            return index+distance < list.size();
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    public void scroll(final int distance)
    {
        setIndex(index+distance);
        render();
    }

    /* {@inheritDoc} */
    public void resetScroll()
    {
        setIndex(defaultIndex);
    }

    /* {@inheritDoc} */
    @Override protected void button1Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT))
        {
            jxcw.getCrossfireServerConnection().sendLock(!item.isLocked(), item.getTag());
        }
        else
        {
            jxcw.getCrossfireServerConnection().sendExamine(item.getTag());
        }
    }

    /* {@inheritDoc} */
    @Override protected void button2Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item != null)
        {
            if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT))
            {
                jxcw.getCrossfireServerConnection().sendMark(item.getTag());
                return;
            }
        }

        super.button2Clicked(jxcw);
    }

    /* {@inheritDoc} */
    @Override protected void button3Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        if (item.isLocked())
        {
            jxcw.getCrossfireServerConnection().drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
            return;
        }

        jxcw.getCrossfireServerConnection().sendMove(ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor(), item.getTag(), jxcw.getCommandQueue().getRepeatCount());
    }

    /**
     * Set the inventory slot to display.
     *
     * @param index the inventory slot
     */
    private void setIndex(final int index)
    {
        if (this.index == index)
        {
            return;
        }

        if (this.index >= 0)
        {
            ItemsList.getItemsManager().getInventoryManager().removeLocationListener(this.index, inventoryLocationListener);
        }
        this.index = index;
        if (this.index >= 0)
        {
            ItemsList.getItemsManager().getInventoryManager().addLocationListener(this.index, inventoryLocationListener);
        }

        final CfPlayer player = ItemsList.getItemsManager().getPlayer();
        if (player != null)
        {
            final List<CfItem> list = ItemsList.getItemsManager().getItems(player.getTag());
            if (0 <= this.index && this.index < list.size())
            {
                setItem(list.get(this.index));
            }
            else
            {
                setItem(null);
            }
        }
        else
        {
            setItem(null);
        }
    }
}
