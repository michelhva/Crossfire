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
import com.realtime.crossfire.jxclient.faces.FacesManagerListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.list.GUIKnowledgeTypeList;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeListener;
import com.realtime.crossfire.jxclient.knowledge.KnowledgeManager;
import com.realtime.crossfire.jxclient.skin.skin.GuiFactory;
import java.awt.Dimension;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;

/**
 * @author nicolas
 */
public class GUIItemKnowledgeType extends GUIItemItem {

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
     * The {@link KnowledgeManager} instance being watched.
     */
    @NotNull
    private final KnowledgeManager knowledgeManager;

    /**
     * The default scroll index.
     */
    private final int defaultIndex;

    /**
     * The currently selected spell or {@code -1} if none is selected.
     * Corresponds to {@link #currentFace}.
     */
    private int index = -1;

    /**
     * Whether this element is selected in its {@link GUIKnowledgeTypeList}.
     */
    private boolean selected;

    private int currentFace = -1;

    /**
     * The {@link ItemView} to use.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    @SuppressWarnings("FieldCanBeLocal")
    private final FacesManagerListener facesManagerListener = face -> {
        if (currentFace == face.getFaceNum()) {
            setChanged();
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
     * @param itemView the item view to use
     * @param size the size of the component or {@code 0} for undefined
     * @param guiFactory the global GUI factory instance
     */
    public GUIItemKnowledgeType(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, final int defaultIndex, @NotNull final FacesManager facesManager, @NotNull final KnowledgeManager knowledgeManager, @NotNull final ItemView itemView, final int size, @NotNull final GuiFactory guiFactory) {
        super(tooltipManager, elementListener, name, itemPainter, facesManager, guiFactory);
        this.defaultIndex = defaultIndex;
        this.facesManager = facesManager;
        this.knowledgeManager = knowledgeManager;
        this.itemView = itemView;
        setIndex(defaultIndex);

        knowledgeManager.addKnowledgeListener(new KnowledgeListener() {

            @Override
            public void typeAdded(final int index) {
                if (GUIItemKnowledgeType.this.index >= index) {
                    setSkill();
                }
            }

            @Override
            public void knowledgeAdded(final int index) {
            }

        });
        facesManager.addFacesManagerListener(facesManagerListener);
        if (size != 0) {
            setSize(0, 0);
        }
    }

    @Override
    public boolean canScroll(final int distance) {
        if (distance < 0) {
            return index >= -distance;
        }
        //noinspection SimplifiableIfStatement
        if (distance > 0) {
            return index+distance < knowledgeManager.getTypes();
        }
        return false;
    }

    @Override
    public void scroll(final int distance) {
        setIndex(index+distance);
        setChanged();
    }

    @Override
    public void resetScroll() {
        setIndex(defaultIndex);
    }

    @NotNull
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSizeInt();
    }

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
     * Sets the currently selected knowledge type.
     */
    private void setSkill() {
        currentFace = knowledgeManager.getTypeFace(index);
        setChanged();
        /*
  setTooltipText(newSkill == null ? null : newSkill.toString());*/
    }

    /**
     * Sets the {@link #index} of the currently selected {@link #currentFace}.
     * Updates the currently selected spell.
     * @param index the index to set
     */
    private void setIndex(final int index) {
        if (this.index == index) {
            return;
        }
        this.index = index;

        setSkill();
    }

    @NotNull
    @Override
    protected Image getFace(@NotNull final CfItem item) {
        //      if (item.getFace() == null) {
        //        return defaultSkillIcon.getOriginalImageIcon().getImage();
        //  }
        return facesManager.getOriginalImageIcon(item.getFace().getFaceNum(), null).getImage();
    }

    @Override
    public void setSelected(final boolean selected) {
        if (this.selected == selected) {
            return;
        }

        this.selected = selected;
        setChanged();
    }

    @Override
    protected boolean isSelected() {
        return selected || isActive();
    }

    @Override
    public int getIndex() {
        synchronized (sync) {
            return index;
        }
    }

    @Override
    public void setIndexNoListeners(final int index) {
        synchronized (sync) {
            this.index = index;
        }

        setItemNoListeners(itemView.getItem(this.index));
    }

    @Override
    public void button1Clicked(final int modifiers) {
    }

    @Override
    public void button2Clicked(final int modifiers) {
    }

    @Override
    public void button3Clicked(final int modifiers) {
    }

    @Override
    public void notifyOpen() {
    }

}
