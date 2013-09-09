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

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemFloor;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIItemList} for floor views.
 * @author Andreas Kirschbaum
 */
public class GUIFloorList extends GUIItemList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

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
     * The base name.
     */
    @NotNull
    private final String name;

    /**
     * The width of cells.
     */
    private final int cellWidth;

    /**
     * The height of cells.
     */
    private final int cellHeight;

    /**
     * The {@link ItemView} to use.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link ItemPainter} to use.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link CrossfireServerConnection} to use.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link ItemSet} to use.
     */
    @NotNull
    private final ItemSet itemSet;

    /**
     * The {@link Image} for "next group of items".
     */
    @NotNull
    private final Image nextGroupFace;

    /**
     * The {@link Image} for "prev group of items".
     */
    @NotNull
    private final Image prevGroupFace;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param commandQueue the command queue for sending commands
     * @param itemPainter the item painter to use
     * @param crossfireServerConnection the crossfire server connection to use
     * @param facesManager the faces manager to use
     * @param itemSet the item set to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     */
    public GUIFloorList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final CommandQueue commandQueue, @NotNull final ItemPainter itemPainter, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final ItemSet itemSet, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, new GUIItemFloor(tooltipManager, elementListener, commandQueue, name+"_template", itemPainter, -1, crossfireServerConnection, itemView, itemSet, facesManager, nextGroupFace, prevGroupFace, cellHeight));
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.itemView = itemView;
        this.commandQueue = commandQueue;
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.itemSet = itemSet;
        this.nextGroupFace = nextGroupFace;
        this.prevGroupFace = prevGroupFace;
    }

    /**
     * Returns the preferred number of objects to display.
     * @return the number of objects
     */
    public int getNumLookObjects() {
        return Math.max(getWidth()/cellWidth, 1)*Math.max(getHeight()/cellHeight, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    protected GUIElement newItem(final int index) {
        return new GUIItemFloor(tooltipManager, elementListener, commandQueue, name+index, itemPainter, index, crossfireServerConnection, itemView, itemSet, facesManager, nextGroupFace, prevGroupFace, 0);
    }

}
