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
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.server.CommandQueue;
import com.realtime.crossfire.jxclient.server.CrossfireServerConnection;
import com.realtime.crossfire.jxclient.window.JXCWindow;

/**
 * A factory for creating {@link GUIItemInventory} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryFactory
{
    private final JXCWindow window;

    private final CommandQueue commandQueue;

    private final String name;

    private final ItemPainter itemPainter;

    private final CrossfireServerConnection crossfireServerConnection;

    private final FacesManager facesManager;

    private final ItemsManager itemsManager;

    /**
     * Creates a new instance.
     */
    public GUIItemInventoryFactory(final JXCWindow window, final CommandQueue commandQueue, final String name, final ItemPainter itemPainter, final CrossfireServerConnection crossfireServerConnection, final FacesManager facesManager, final ItemsManager itemsManager)
    {
        this.window = window;
        this.commandQueue = commandQueue;
        this.name = name;
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.itemsManager = itemsManager;
    }

    /**
     * Creates a new {@link GUIItemInventory} instance.
     * @param index the item inventory's index
     * @return the new instance
     */
    public GUIItemInventory newItemInventory(final int index)
    {
        return new GUIItemInventory(window, commandQueue, name+index, 0, 0, 1, 1, itemPainter, index, crossfireServerConnection, facesManager, itemsManager);
    }

    /**
     * Creates a new {@link GUIItemInventory} instance.
     * @param cellHeight the cell size
     * @return the new instance
     */
    public GUIItemInventory newTemplateItemInventory(final int cellHeight)
    {
        return new GUIItemInventory(window, commandQueue, name+"_template", 0, 0, cellHeight, cellHeight, itemPainter, -1, crossfireServerConnection, facesManager, itemsManager);
    }
}
