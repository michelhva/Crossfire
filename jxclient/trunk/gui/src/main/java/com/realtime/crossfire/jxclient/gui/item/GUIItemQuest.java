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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.list.GUIQuestList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.quests.Quest;
import com.realtime.crossfire.jxclient.quests.QuestListener;
import com.realtime.crossfire.jxclient.quests.QuestsManager;
import com.realtime.crossfire.jxclient.quests.QuestsManagerListener;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIItemItem} that represents an entry in a {@link GUIQuestList}.
 * @author Nicolas Weeger
 */
public class GUIItemQuest extends GUIItemItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The object used for synchronization on {@link #index}.
     */
    @NotNull
    private final Object sync = new Object();

    /**
     * The {@link FacesManager} for looking up faces.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The {@link QuestsManager} instance to watch.
     */
    @NotNull
    private final QuestsManager questsManager;

    /**
     * The currently selected {@link Quest}, null if none.
     */
    @Nullable
    private Quest quest = null;

    /**
     * The currently selected quest or <code>-1</code> if none is selected.
     * Corresponds to {@link #quest}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUIQuestList}.
     */
    private boolean selected;

    /**
     * The spells view to use.
     */
    @NotNull
    private final ItemView questsView;

    /**
     * The {@link QuestsManagerListener} used to detect spell changes.
     */
    @NotNull
    private final QuestsManagerListener questsManagerListener = new QuestsManagerListener() {

        @Override
        public void questAdded(final int index) {
            if (GUIItemQuest.this.index >= index) {
                setQuest();
            }
        }

    };

    /**
     * The {@link QuestListener} attached to {@link #quest}.
     */
    @NotNull
    private final QuestListener questListener = new QuestListener() {

        @Override
        public void questChanged() {
            setQuest();
        }

    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (quest != null && quest.getFace() == face.getFaceNum()) {
                setChanged();
            }
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param itemPainter the item painter for painting the icon
     * @param defaultIndex the default scroll index
     * @param facesManager the faces manager for looking up faces
     * @param questsManager the quests manager instance to watch
     * @param questsView the quests view to use
     */
    public GUIItemQuest(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final QuestsManager questsManager, @NotNull final ItemView questsView) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.facesManager = facesManager;
        this.defaultIndex = defaultIndex;
        this.questsManager = questsManager;
        setIndex(defaultIndex);
        this.questsManager.addCrossfireQuestChangedListener(questsManagerListener);
        this.facesManager.addFacesManagerListener(facesManagerListener);
        this.questsView = questsView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        questsManager.removeCrossfireQuestChangedListener(questsManagerListener);
        facesManager.removeFacesManagerListener(facesManagerListener);
        if (quest != null) {
            quest.removeQuestListener(questListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        } else if (distance > 0) {
            return index+distance < questsManager.getQuests();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scroll(final int distance) {
        setIndex(index+distance);
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Dimension getMinimumSize() {
        return getMinimumSizeInt();
    }

    /**
     * Returns the minimal size to display this component.
     * @return the minimal size
     */
    @NotNull
    private static Dimension getMinimumSizeInt() {
        return new Dimension(32, 32);
    }

    /**
     * Sets the currently selected {@link #quest}. Attaches or detaches {@link
     * #questListener} as needed.
     */
    private void setQuest() {
        final Quest newQuest = questsManager.getQuest(index);
        if (quest == newQuest) {
            return;
        }

        if (quest != null) {
            quest.removeQuestListener(questListener);
        }

        quest = newQuest;

        if (quest != null) {
            quest.addQuestListener(questListener);
        }

        setChanged();

        setTooltipText(newQuest == null ? null : newQuest.getTooltipText());
    }

    /**
     * Sets the {@link #index} of the currently selected {@link #quest}. Updates
     * the currently selected quest.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setQuest();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(questsView.getItem(this.index));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button1Clicked(final int modifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(final int modifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(final int modifiers) {
    }

}
