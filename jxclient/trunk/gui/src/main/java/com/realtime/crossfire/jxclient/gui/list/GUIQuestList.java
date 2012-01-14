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
 * Copyright (C) 2011 Nicolas Weeger.
 */

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemQuest;
import com.realtime.crossfire.jxclient.gui.item.ItemPainter;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.quests.Quest;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} to display quests.
 * @author Nicolas Weeger
 */
public class GUIQuestList extends GUIItemList {

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

    /**
     * The quests to display.
     */
    @NotNull
    private final QuestsManager questsManager;

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
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of cells
     * @param cellHeight the height of cells
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param questsManager the quests to display
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     */
    public GUIQuestList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final QuestsManager questsManager, @NotNull final ItemPainter itemPainter, @NotNull final FacesManager facesManager) {
        super(tooltipManager, elementListener, name, cellWidth, cellHeight, itemView, currentItem, new GUIItemQuest(tooltipManager, elementListener, name+"_template", itemPainter, -1, facesManager, questsManager, itemView, cellHeight));
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemView = itemView;
        this.questsManager = questsManager;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTooltip(final int index, final int x, final int y, final int w, final int h) {
        final Quest quest = questsManager.getQuest(index);
        if (quest == null) {
            setTooltipText(null, x, y, w, h);
            return;
        }

        setTooltipText(quest.getTooltipText());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected GUIElement newItem(final int index) {
        return new GUIItemQuest(tooltipManager, elementListener, name+index, itemPainter, index, facesManager, questsManager, itemView, 0);
    }

}
