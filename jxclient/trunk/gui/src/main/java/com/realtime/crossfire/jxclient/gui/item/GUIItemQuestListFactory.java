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
 * Copyright (C) 2011 Nicolas Weeger
 */

package com.realtime.crossfire.jxclient.gui.item;

import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.QuestsView;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import org.jetbrains.annotations.NotNull;

/**
 * A factory for creating {@link GUIItemQuest} instances.
 * @author Nicolas Weeger
 */
public class GUIItemQuestListFactory implements GUIItemItemFactory {

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
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The {@link QuestsManager} instance to watch.
     */
    @NotNull
    private final QuestsManager questsManager;

    /**
     * The {@link QuestsView} to use.
     */
    @NotNull
    private final QuestsView questsView;

    /**
     * The {@link FacesManager} to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the base name for created elements
     * @param itemPainter the item painter for painting the icon
     * @param facesManager the faces manager to use
     * @param questsManager the quests manager instance to watch
     * @param questsView the quests view to use
     */
    public GUIItemQuestListFactory(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final ItemPainter itemPainter, @NotNull final FacesManager facesManager, @NotNull final QuestsManager questsManager, @NotNull final QuestsView questsView) {
        this.tooltipManager = tooltipManager;
        this.elementListener = elementListener;
        this.name = name;
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.questsManager = questsManager;
        this.questsView = questsView;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public GUIElement newItem(final int index) {
        return new GUIItemQuest(tooltipManager, elementListener, name+index, itemPainter, index, facesManager, questsManager, questsView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public GUIItemItem newTemplateItem(final int cellHeight) {
        final GUIItemItem result = new GUIItemQuest(tooltipManager, elementListener, name+"_template", itemPainter, -1, facesManager, questsManager, questsView);
        //noinspection SuspiciousNameCombination
        result.setSize(cellHeight, cellHeight);
        return result;
    }

}
