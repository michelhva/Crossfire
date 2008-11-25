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
package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.AbstractLabel;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIElementChangedListener;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventoryFactory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.LocationsListener;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.List;
import javax.swing.JList;

/**
 * A {@link GUIItemList} that displays the character's inventory.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryList extends GUIItemList
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GUIItemInventoryFactory} for creating new {@link
     * GUIItemInventory} instances.
     */
    private final GUIItemInventoryFactory itemInventoryFactory;

    private final CommandQueue commandQueue;

    private final CrossfireServerConnection crossfireServerConnection;

    private final ItemsManager itemsManager;

    /**
     * The label to update with information about the selected item.
     */
    private final AbstractLabel currentItem;

    /**
     * The {@link LocationsListener} to be notified about inventory changes.
     */
    private final LocationsListener locationsListener = new LocationsListener()
    {
        /** {@inheritDoc} */
        public void locationsModified(final Collection<Integer> index)
        {
            rebuildList();
        }
    };

    /**
     * The {@link GUIElementChangedListener} attached to all {@link
     * GUIItemInventory} instance in the list.
     */
    private final GUIElementChangedListener itemChangedListener = new GUIElementChangedListener()
    {
        /** {@inheritDoc} */
        public void notifyChanged(final GUIElement element)
        {
            element.resetChanged();
            selectionChanged();
            setChanged();
        }
    };

    /**
     * Creates a new instance.
     * @param window the <code>JXCWindow</code> this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is relative
     * to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is relative
     * to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param cellHeight the height of each cell
     * @param currentItem the label to update with information about the selected item.
     * @param itemInventoryFactory the factory for creating item inventory
     * instances
     */
    public GUIItemInventoryList(final JXCWindow window, final CommandQueue commandQueue, final String name, final int x, final int y, final int w, final int h, final int cellHeight, final CrossfireServerConnection crossfireServerConnection, final ItemsManager itemsManager, final AbstractLabel currentItem, final GUIItemInventoryFactory itemInventoryFactory)
    {
        super(window, name, x, y, w, h, cellHeight, new ItemInventoryCellRenderer(itemInventoryFactory.newTemplateItemInventory(cellHeight)));
        this.itemInventoryFactory = itemInventoryFactory;
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.itemsManager = itemsManager;
        this.currentItem = currentItem;
        setLayoutOrientation(JList.HORIZONTAL_WRAP, -1);
        itemsManager.getInventoryManager().addLocationsListener(locationsListener);
        rebuildList();
    }

    /**
     * Rebuilds the list cells.
     */
    private void rebuildList()
    {
        synchronized (getTreeLock())
        {
            final List<CfItem> inventory = itemsManager.getInventory();
            final int newSize = inventory.size();
            final int oldSize = resizeElements(newSize);
            for (int i = oldSize; i < newSize; i++)
            {
                final GUIItemInventory item = itemInventoryFactory.newItemInventory(i);
                addElement(item);
                item.setChangedListener(itemChangedListener);
                assert item.isElementVisible();
                item.resetChanged();
                assert !item.isChanged();
            }
            selectionChanged();
        }
        setChanged();
    }

    /** {@inheritDoc} */
    protected void selectionChanged(final int selectedIndex)
    {
        if (currentItem != null)
        {
            final List<CfItem> inventory = itemsManager.getInventory();
            final CfItem item = selectedIndex >= 0 && selectedIndex < inventory.size() ? inventory.get(selectedIndex) : null;
            if (item == null)
            {
                currentItem.setText("");
                currentItem.setTooltipText("");
            }
            else
            {
                final String tooltipText1 = item.getTooltipText1();
                final String tooltipText2 = item.getTooltipText2();
                final String tooltipText3 = item.getTooltipText3();
                currentItem.setText(tooltipText1+" ["+tooltipText2+"] "+tooltipText3);
                currentItem.setTooltipText(item.getTooltipText());
            }
        }
    }

    /** {@inheritDoc} */
    protected void updateTooltip(final int index)
    {
        final List<CfItem> inventory = itemsManager.getInventory();
        final CfItem item = 0 <= index && index < inventory.size() ? inventory.get(index) : null;
        setTooltipText(item == null ? null : item.getTooltipText());
    }

    /** {@inheritDoc} */
    protected void activeChanged()
    {
    }

    /* {@inheritDoc} */
    @Override
    public void button1Clicked(final int modifiers)
    {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null)
        {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null)
        {
            return;
        }

        if ((modifiers&InputEvent.SHIFT_DOWN_MASK) != 0)
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
    public void button2Clicked(final int modifiers)
    {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null)
        {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null)
        {
            return;
        }

        if ((modifiers&InputEvent.SHIFT_DOWN_MASK) != 0)
        {
            crossfireServerConnection.sendMark(item.getTag());
        }
        else
        {
            crossfireServerConnection.sendApply(item.getTag());
        }
    }

    /* {@inheritDoc} */
    @Override
    public void button3Clicked(final int modifiers)
    {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null)
        {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null)
        {
            return;
        }

        if (item.isLocked())
        {
            crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
        }
        else
        {
            commandQueue.sendMove(itemsManager.getCurrentFloorManager().getCurrentFloor(), item.getTag());
        }
    }
}
