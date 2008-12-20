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
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfPlayer;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.LocationListener;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import com.realtime.crossfire.jxclient.window.KeyHandler;
import java.awt.Image;
import java.util.List;

/**
 * A {@link GUIItem} for displaying inventory objects.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventory extends GUIItemItem
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
     * The server instance.
     */
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FacesManager} instance to use.
     */
    private final FacesManager facesManager;

    /**
     * The {@link ItemsManager} instance to watch.
     */
    private final ItemsManager itemsManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The object used for synchronization on {@link #index}.
     */
    private final Object sync = new Object();

    /**
     * The inventory slot index.
     */
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
            synchronized (sync)
            {
                assert index == GUIItemInventory.this.index;
            }
            setItem(item);
        }
    };

    public GUIItemInventory(final JXCWindow window, final CommandQueue commandQueue, final String name, final int x, final int y, final int w, final int h, final ItemPainter itemPainter, final int index, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final ItemsManager itemsManager)
    {
        super(window, name, x, y, w, h, crossfireServerConnection, itemPainter, facesManager);
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.itemsManager = itemsManager;
        defaultIndex = index;
        setIndex(index);
    }

    /** {@inheritDoc} */
    @Override
    public void destroy()
    {
        super.destroy();
        setIndex(-1);
    }

    /** {@inheritDoc} */
    public boolean canScroll(final int distance)
    {
        if (distance < 0)
        {
            synchronized (sync)
            {
                return index >= -distance;
            }
        }
        else if (distance > 0)
        {
            final CfPlayer player = itemsManager.getPlayer();
            if (player == null)
            {
                return false;
            }

            final List<CfItem> list = itemsManager.getItems(player.getTag());
            synchronized (sync)
            {
                return index+distance < list.size();
            }
        }
        else
        {
            return false;
        }
    }

    /* {@inheritDoc} */
    public void scroll(final int distance)
    {
        synchronized (sync)
        {
            setIndex(index+distance);
        }
        setChanged();
    }

    /* {@inheritDoc} */
    public void resetScroll()
    {
        setIndex(defaultIndex);
    }

    /* {@inheritDoc} */
    @Override
    public void button1Clicked(final JXCWindow window)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        if (window.getKeyHandler().getKeyShift(KeyHandler.KEY_SHIFT_SHIFT))
        {
            crossfireServerConnection.sendLock(!item.isLocked(), item.getTag());
        }
        else
        {
            crossfireServerConnection.sendExamine(item.getTag());
        }
    }

    /* {@inheritDoc} */
    @Override
    public void button2Clicked(final JXCWindow window)
    {
        final CfItem item = getItem();
        if (item != null)
        {
            if (window.getKeyHandler().getKeyShift(KeyHandler.KEY_SHIFT_SHIFT))
            {
                crossfireServerConnection.sendMark(item.getTag());
                return;
            }
        }

        super.button2Clicked(window);
    }

    /* {@inheritDoc} */
    @Override
    public void button3Clicked(final JXCWindow window)
    {
        final CfItem item = getItem();
        if (item == null)
        {
            return;
        }

        if (item.isLocked())
        {
            crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
            return;
        }

        commandQueue.sendMove(itemsManager.getCurrentFloorManager().getCurrentFloor(), item.getTag());
    }

    /**
     * Returns the slot index.
     * @return the slow tindex
     */
    public int getIndex()
    {
        synchronized (sync)
        {
            return index;
        }
    }

    /**
     * Set the inventory slot to display.
     * @param index the inventory slot
     */
    public void setIndex(final int index)
    {
        synchronized (sync)
        {
            if (this.index == index)
            {
                return;
            }

            if (this.index >= 0)
            {
                itemsManager.getInventoryManager().removeLocationListener(this.index, inventoryLocationListener);
            }
            this.index = index;
            if (this.index >= 0)
            {
                itemsManager.getInventoryManager().addLocationListener(this.index, inventoryLocationListener);
            }
        }

        final CfPlayer player = itemsManager.getPlayer();
        if (player != null)
        {
            final List<CfItem> list = itemsManager.getItems(player.getTag());
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

    /** {@inheritDoc} */
    @Override
    protected Image getFace(final CfItem item)
    {
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum()).getImage();
    }
}
