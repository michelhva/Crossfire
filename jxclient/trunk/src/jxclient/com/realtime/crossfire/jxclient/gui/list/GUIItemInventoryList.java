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

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.GUIElementChangedListener;
import com.realtime.crossfire.jxclient.gui.item.GUIItemInventory;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.items.LocationsListener;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import javax.swing.JList;

/**
 * A {@link GUIItemList} that displays the character's inventory.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryList extends GUIItemList
{
    private final JXCWindow window;

    private final CommandQueue commandQueue;

    private final String name;

    private final BufferedImage cursedImage;

    private final BufferedImage damnedImage;

    private final BufferedImage magicImage;

    private final BufferedImage blessedImage;

    private final BufferedImage appliedImage;

    private final BufferedImage selectorImage;

    private final BufferedImage lockedImage;

    private final BufferedImage unpaidImage;

    private final Color cursedColor;

    private final Color damnedColor;

    private final Color magicColor;

    private final Color blessedColor;

    private final Color appliedColor;

    private final Color selectorColor;

    private final Color lockedColor;

    private final Color unpaidColor;

    private final CrossfireServerConnection crossfireServerConnection;

    private final FacesManager facesManager;

    private final ItemsManager itemsManager;

    private final Font font;

    private final Color nrofColor;

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
     */
    public GUIItemInventoryList(final JXCWindow window, final CommandQueue commandQueue, final String name, final int x, final int y, final int w, final int h, final int cellHeight, final BufferedImage cursedImage, final BufferedImage damnedImage, final BufferedImage magicImage, final BufferedImage blessedImage, final BufferedImage appliedImage, final BufferedImage selectorImage, final BufferedImage lockedImage, final BufferedImage unpaidImage, final Color cursedColor, final Color damnedColor, final Color magicColor, final Color blessedColor, final Color appliedColor, final Color selectorColor, final Color lockedColor, final Color unpaidColor, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final ItemsManager itemsManager, final Font font, final Color nrofColor)
    {
        super(window, name, x, y, w, h, cellHeight, new ItemInventoryCellRenderer(new GUIItemInventory(window, commandQueue, name+"_template", 0, 0, cellHeight, cellHeight, cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, -1, crossfireServerConnection, facesManager, itemsManager, font, nrofColor)));
        this.window = window;
        this.commandQueue = commandQueue;
        this.name = name;
        this.cursedImage = cursedImage;
        this.damnedImage = damnedImage;
        this.magicImage = magicImage;
        this.blessedImage = blessedImage;
        this.appliedImage = appliedImage;
        this.selectorImage = selectorImage;
        this.lockedImage = lockedImage;
        this.unpaidImage = unpaidImage;
        this.cursedColor = cursedColor;
        this.damnedColor = damnedColor;
        this.magicColor = magicColor;
        this.blessedColor = blessedColor;
        this.appliedColor = appliedColor;
        this.selectorColor = selectorColor;
        this.lockedColor = lockedColor;
        this.unpaidColor = unpaidColor;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.itemsManager = itemsManager;
        this.font = font;
        this.nrofColor = nrofColor;
        setLayoutOrientation(JList.HORIZONTAL_WRAP, -1);
        itemsManager.getInventoryManager().addLocationsListener(locationsListener);
        rebuildList();
    }

    /**
     * Rebuilds the list cells.
     */
    private void rebuildList()
    {
        final List<CfItem> inventory = itemsManager.getInventory();
        final int newSize = inventory.size();
        final int oldSize = resizeElements(newSize);
        for (int i = oldSize; i < newSize; i++)
        {
            final GUIItemInventory item = new GUIItemInventory(window, commandQueue, name+i, 0, 0, 1, 1, cursedImage, damnedImage, magicImage, blessedImage, appliedImage, selectorImage, lockedImage, unpaidImage, cursedColor, damnedColor, magicColor, blessedColor, appliedColor, selectorColor, lockedColor, unpaidColor, i, crossfireServerConnection, facesManager, itemsManager, font, nrofColor);
            addElement(item);
            item.setChangedListener(itemChangedListener);
            assert item.isElementVisible();
            item.resetChanged();
            assert !item.isChanged();
        }
        setChanged();
    }

    /** {@inheritDoc} */
    protected void selectionChanged(final int selectedIndex)
    {
    }

    /** {@inheritDoc} */
    protected void updateTooltip(final int index)
    {
        final List<CfItem> inventory = itemsManager.getInventory();
        final CfItem item = index < inventory.size() ? inventory.get(index) : null;
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
            crossfireServerConnection.sendMove(itemsManager.getCurrentFloorManager().getCurrentFloor(), item.getTag(), commandQueue.getRepeatCount());
        }
    }
}
