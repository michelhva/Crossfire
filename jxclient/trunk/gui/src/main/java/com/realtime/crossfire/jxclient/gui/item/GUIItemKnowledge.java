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

import com.realtime.crossfire.jxclient.faces.Face;
import com.realtime.crossfire.jxclient.faces.FacesManager;
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeItem;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author nicolas
 */
public class GUIItemKnowledge extends GUIItemItem {

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

    @NotNull
    private final KnowledgeManager knowledgeManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The currently selected spell or <code>-1</code> if none is selected.
     * Corresponds to {@link #skill}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUISpellSkillList}.
     */
    private boolean selected;

    /**
     * The command queue for sending commands.
     */
    @NotNull
    private final CommandQueue commandQueue;

    @NotNull
    private final ItemView view;

    @Nullable
    private KnowledgeItem item;

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (item != null && item.getFaceNum() == face.getFaceNum()) {
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
     * @param knowledgeManager the knowledge manager instance to watch
     * @param size the size of the component or <code>0</code> for undefined
     */
    public GUIItemKnowledge(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager, @NotNull final ItemView view, @NotNull final CommandQueue commandQueue, final int size) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager);
        this.defaultIndex = defaultIndex;
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        this.view = view;
        this.commandQueue = commandQueue;
        setIndex(defaultIndex);

        knowledgeManager.addKnowledgeListener(new KnowledgeManager.KnowledgeListener() {
            @Override
            public void typeAdded(int index) {
            }

            @Override
            public void knowledgeAdded(int index) {
                if (GUIItemKnowledge.this.index >= index) {
                    setKnowledgeItem();
                }
            }
        });
        facesManager.addFacesManagerListener(facesManagerListener);
        if (size != 0) {
            setSize(size, size);
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
            return index+distance < knowledgeManager.getTypes();
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
     * Sets the currently selected {@link Skill}.
     */
    private void setKnowledgeItem() {
        final KnowledgeItem newItem = knowledgeManager.getKnowledge(index);

        if (newItem == item) {
            return;
        }

        item = newItem;
        setChanged();
    }

    /**
     * Sets the {@link #index} of the currently selected {@link #skill}. Updates
     * the currently selected spell.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setKnowledgeItem();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        //      if (item.getFace() == null) {
        //        return defaultSkillIcon.getOriginalImageIcon().getImage();
        //  }
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

        setItemNoListeners(view.getItem(this.index));
        setKnowledgeItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button1Clicked(int modifiers) {
        if (item == null) {
            return;
        }

        commandQueue.sendNcom(false, "knowledge show "+item.getKnowledgeIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button2Clicked(int modifiers) {
        if (!knowledgeManager.canAttemptType(item.getType())) {
            return;
        }

        commandQueue.sendNcom(false, "knowledge attempt "+item.getKnowledgeIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void button3Clicked(int modifiers) {
    }
}
