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
 * Copyright (C) 2006-2011 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * A factory for creating {@link GUIItemInventory} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemInventoryFactory implements GUIItemItemFactory {

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

    /**
     * The {@link CommandQueue command queue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The base name for created elements.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemPainter item painter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link CrossfireServerConnection connection} instance.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FacesManager faces manager} instance to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link FloorView floor view} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The inventory view to use.
     */
    @NotNull
    private final ItemView inventoryView;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue for sending commands
     * @param name the base name for created elements
     * @param itemPainter the item painter for painting the icon
     * @param crossfireServerConnection the connection instance
     * @param facesManager the faces manager instance to use
     * @param floorView the floor view to use
     * @param inventoryView the inventory view to use
     */
    public GUIItemInventoryFactory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @NotNull final ItemPainter itemPainter, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final FloorView floorView, @NotNull final ItemView inventoryView) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.commandQueue = commandQueue;
        this.name = name;
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.floorView = floorView;
        this.inventoryView = inventoryView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIElement newItem(final int index) {
        return new GUIItemInventory(tooltipManager, elementListener, commandQueue, name+index, itemPainter, index, crossfireServerConnection, facesManager, floorView, inventoryView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIItemItem newTemplateItem(final int cellHeight) {
        final GUIItemItem result = new GUIItemInventory(tooltipManager, elementListener, commandQueue, name+"_template", itemPainter, -1, crossfireServerConnection, facesManager, floorView, inventoryView);
        //noinspection SuspiciousNameCombination
        result.setSize(cellHeight, cellHeight);
        return result;
    }

}
