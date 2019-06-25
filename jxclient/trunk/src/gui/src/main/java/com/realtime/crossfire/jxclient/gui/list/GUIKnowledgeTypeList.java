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
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemKnowledgeType;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author nicolas
 */
public class GUIKnowledgeTypeList extends GUIItemList<GUIItemKnowledgeType> {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@link TooltipManager} to update.
     */
    @NotNull
    private final TooltipManager tooltipManager;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * The base name for created elements.
     */
    @NotNull
    private final String name;

    /**
     * The {@link ItemView} to use.
     */
    @NotNull
    private final ItemView itemView;

    @NotNull
    private final KnowledgeManager knowledgeManager;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The global {@link GuiFactory} instance.
     */
    @NotNull
    private final GuiFactory guiFactory;

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
     * @param knowledgeManager the knowledge manager to use
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     * @param guiFactory the global GUI factory instance
     */
    public GUIKnowledgeTypeList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final KnowledgeManager knowledgeManager, @NotNull final ItemPainter itemPainter, @NotNull final FacesManager facesManager, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, new GUIItemKnowledgeType(tooltipManager, elementListener, name+"_template", itemPainter, -1, facesManager, knowledgeManager, itemView, cellHeight, guiFactory), guiFactory);
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemView = itemView;
        this.knowledgeManager = knowledgeManager;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.guiFactory = guiFactory;
    }

    @Override
    protected void selectionChanged(final int selectedIndex) {
        if (knowledgeManager == null) {
            return;
        }
        knowledgeManager.filterType(selectedIndex);
        super.selectionChanged(selectedIndex);
    }

    @Nullable
    @Override
    protected String getTooltip(final int index) {
        final CfItem item = itemView.getItem(index);
        return item == null ? null : item.getTooltipText();
    }

    @NotNull
    @Override
    protected GUIItemKnowledgeType newItem(final int index) {
        return new GUIItemKnowledgeType(tooltipManager, elementListener, name+index, itemPainter, index, facesManager, knowledgeManager, itemView, 0, guiFactory);
    }

    @Override
    public void notifyOpen() {
    }

}
