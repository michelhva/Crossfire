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
import com.realtime.crossfire.jxclient.CfPlayer;
import com.realtime.crossfire.jxclient.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.ItemsList;
import com.realtime.crossfire.jxclient.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

public class GUIItemInventory extends GUIItemItem
{
    private int myindex = -1;

    /**
     * The {@link LocationListener} used to detect items added to or removed
     * from this inventory slot.
     */
    private final LocationListener inventoryLocationListener = new LocationListener()
    {
        /** {@inheritDoc} */
        public void locationModified(final int index, final CfItem item)
        {
            assert index == myindex;
            setItem(item);
        }
    };

    public GUIItemInventory(final JXCWindow jxcWindow, final String name, final int x, final int y, final int w, final int h, final BufferedImage image, final BufferedImage cursedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final int index, final CrossfireServerConnection crossfireServerConnection, final Font font, final Color nrofColor)
    {
        super(jxcWindow, name, x, y, w, h, image, cursedImage, appliedImage, selectorImage, lockedImage, crossfireServerConnection, font, nrofColor);
        setIndex(index);
        render();
    }

    public int getIndex()
    {
        return myindex;
    }

    /** {@inheritDoc} */
    @Override public boolean canScrollUp()
    {
        return myindex > 0;
    }

    /* {@inheritDoc} */
    @Override public void scrollUp()
    {
        setIndex(myindex-1);
        render();
    }

    /** {@inheritDoc} */
    @Override public boolean canScrollDown()
    {
        final CfPlayer player = ItemsList.getItemsManager().getPlayer();
        if (player == null)
        {
            return false;
        }

        final List<CfItem> list = ItemsList.getItemsManager().getItems(player.getTag());
        return myindex+1 < list.size();
    }

    /* {@inheritDoc} */
    @Override public void scrollDown()
    {
        setIndex(myindex+1);
        render();
    }

    /* {@inheritDoc} */
    @Override protected void button1Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        try
        {
            if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT))
            {
                jxcw.getCrossfireServerConnection().sendLock(!item.isLocked(), item.getTag());
            }
            else
            {
                jxcw.getCrossfireServerConnection().sendExamine(item.getTag());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /* {@inheritDoc} */
    @Override protected void button2Clicked(final JXCWindow jxcw)
    {
        final CfItem item = getItem();
        if (item != null)
        {
            try
            {
                if (jxcw.getKeyShift(JXCWindow.KEY_SHIFT_SHIFT))
                {
                    jxcw.getCrossfireServerConnection().sendMark(item.getTag());
                    return;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(0);
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

        try
        {
            jxcw.getCrossfireServerConnection().sendMove(ItemsList.getItemsManager().getCurrentFloorManager().getCurrentFloor(), item.getTag(), jxcw.getRepeatCount());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Set the inventory slot to display.
     *
     * @param index the inventory slot
     */
    private void setIndex(final int index)
    {
        if (myindex == index)
        {
            return;
        }

        if (myindex >= 0)
        {
            ItemsList.getItemsManager().getInventoryManager().removeLocationListener(myindex, inventoryLocationListener);
        }
        myindex = index;
        if (myindex >= 0)
        {
            ItemsList.getItemsManager().getInventoryManager().addLocationListener(myindex, inventoryLocationListener);
        }

        final CfPlayer player = ItemsList.getItemsManager().getPlayer();
        if (player != null)
        {
            final List<CfItem> list = ItemsList.getItemsManager().getItems(player.getTag());
            if (0 <= myindex && myindex < list.size())
            {
                setItem(list.get(myindex));
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
