/*
 * This file is part of JXClient, the Fullscreen Java Crossfire Client.
 *
 * JXClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JXClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JXClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2005-2008 Yann Chachkoff.
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */


package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.ItemsManager;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * A factory for creating {@link GUIItemInventory} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryFactory
{
    /**
     * The tooltip manager to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    @NotNull
    private final CommandQueue commandQueue;

    @NotNull
    private final String name;

    @NotNull
    private final ItemPainter itemPainter;

    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    @NotNull
    private final FacesManager facesManager;

    @NotNull
    private final ItemsManager itemsManager;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     */
    public GUIItemInventoryFactory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @NotNull final ItemPainter itemPainter, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final ItemsManager itemsManager)
    {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
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
    @NotNull
    public GUIElement newItemInventory(final int index)
    {
        return new GUIItemInventory(tooltipManager, elementListener, commandQueue, name+index, 0, 0, 1, 1, itemPainter, index, crossfireServerConnection, facesManager, itemsManager);
    }

    /**
     * Creates a new {@link GUIItemInventory} instance.
     * @param cellHeight the cell size
     * @return the new instance
     */
    @NotNull
    public GUIItemInventory newTemplateItemInventory(final int cellHeight)
    {
        return new GUIItemInventory(tooltipManager, elementListener, commandQueue, name+"_template", 0, 0, cellHeight, cellHeight, itemPainter, -1, crossfireServerConnection, facesManager, itemsManager);
    }
}
