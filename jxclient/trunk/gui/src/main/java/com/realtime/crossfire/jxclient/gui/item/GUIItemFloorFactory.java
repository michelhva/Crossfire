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
import com.realtime.crossfire.jxclient.items.ItemSet;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * A factory for creating {@link GUIItemFloor} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemFloorFactory implements GUIItemItemFactory {

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
     * The {@link CommandQueue} for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The base name.
     */
    @NotNull
    private final String name;

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
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

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
     * @param commandQueue the command queue for sending commands
     * @param name the base name
     * @param itemPainter the item painter to use
     * @param crossfireServerConnection the crossfire server connection to use
     * @param facesManager the faces manager to use
     * @param floorView the floor view to use
     * @param itemSet the item set to use
     * @param nextGroupFace the image for "next group of items"
     * @param prevGroupFace the image for "prev group of items"
     */
    public GUIItemFloorFactory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, @NotNull final ItemPainter itemPainter, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FacesManager facesManager, @NotNull final FloorView floorView, @NotNull final ItemSet itemSet, @NotNull final Image nextGroupFace, @NotNull final Image prevGroupFace) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.commandQueue = commandQueue;
        this.name = name;
        this.itemPainter = itemPainter;
        this.crossfireServerConnection = crossfireServerConnection;
        this.facesManager = facesManager;
        this.floorView = floorView;
        this.itemSet = itemSet;
        this.nextGroupFace = nextGroupFace;
        this.prevGroupFace = prevGroupFace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIElement newItem(final int index) {
        return new GUIItemFloor(tooltipManager, elementListener, commandQueue, name+index, itemPainter, index, crossfireServerConnection, floorView, itemSet, facesManager, nextGroupFace, prevGroupFace);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIItemItem newTemplateItem(final int cellHeight) {
        final GUIItemItem result = new GUIItemFloor(tooltipManager, elementListener, commandQueue, name+"_template", itemPainter, -1, crossfireServerConnection, floorView, itemSet, facesManager, nextGroupFace, prevGroupFace);
        //noinspection SuspiciousNameCombination
        result.setSize(cellHeight, cellHeight);
        return result;
    }

}
