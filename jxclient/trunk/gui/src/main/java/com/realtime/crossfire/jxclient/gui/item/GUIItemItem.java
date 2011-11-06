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
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.CfItemListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIElement} instance representing an in-game item.
 * @author Andreas Kirschbaum
 */
public abstract class GUIItemItem extends GUIItem {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link FacesManager} instance to use.
     */
    @NotNull
    private final FacesManager facesManager;

    /**
     * The {@link ItemPainter} for painting the icon.
     */
    @NotNull
    private final ItemPainter itemPainter;

    /**
     * The current item instance.
     */
    @Nullable
    private CfItem item = null;

    /**
     * The {@link CfItemListener} used to detect attribute changes of the
     * displayed item.
     */
    @NotNull
    private final CfItemListener itemListener = new CfItemListener() {

        @Override
        public void itemModified() {
            setChanged();
            updateTooltipText();
        }

    };

    /**
     * The {@link FacesManagerListener} registered to detect updated faces.
     */
    @NotNull
    private final FacesManagerListener facesManagerListener = new FacesManagerListener() {

        @Override
        public void faceUpdated(@NotNull final Face face) {
            if (item != null && face.equals(item.getFace())) {
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
     * @param facesManager the faces manager instance to use
     */
    protected GUIItemItem(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, @NotNull final ItemPainter itemPainter, @NotNull final FacesManager facesManager) {
        super(tooltipManager, elementListener, name);
        this.itemPainter = itemPainter;
        this.facesManager = facesManager;
        this.facesManager.addFacesManagerListener(facesManagerListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        facesManager.removeFacesManagerListener(facesManagerListener);
        setItem(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        final CfItem tmpItem = item;
        if (tmpItem == null) {
            return;
        }

        final Graphics2D g2 = (Graphics2D)g;
        itemPainter.paint(g2, tmpItem, isSelected(), getFace(tmpItem), getWidth(), getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getPreferredSize() {
        return itemPainter.getMinimumSize();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public Dimension getMinimumSize() {
        return itemPainter.getMinimumSize();
    }

    /**
     * Returns the face for a {@link CfItem} instance.
     * @param item the item instance
     * @return the face
     */
    @NotNull
    protected abstract Image getFace(@NotNull final CfItem item);

    /**
     * Returns the current item instance.
     * @return the current item instance
     */
    @Nullable
    public CfItem getItem() {
        return item;
    }

    /**
     * Sets the current item instance.
     * @param item the new current item instance
     */
    protected void setItem(@Nullable final CfItem item) {
        if (this.item == item) {
            return;
        }

        if (this.item != null) {
            this.item.removeCfItemModifiedListener(itemListener);
        }
        this.item = item;
        if (this.item != null) {
            this.item.addCfItemModifiedListener(itemListener);
        }

        setChanged();
        updateTooltipText();
    }

    /**
     * Sets the current item instance without registering listeners for
     * updates.
     * @param item the new current item instance
     */
    protected void setItemNoListeners(@Nullable final CfItem item) {
        this.item = item;
    }

    /**
     * Updates the tooltip text for the current {@link #item}.
     */
    protected void updateTooltipText() {
        setTooltipText(item == null ? null : item.getTooltipText());
    }

    /**
     * Sets the selected state.
     * @param selected whether this element should drawn as "selected"
     */
    public abstract void setSelected(final boolean selected);

    /**
     * Returns whether this element should drawn as "selected".
     * @return whether this element is selected
     */
    protected abstract boolean isSelected();

    /**
     * Returns the slot index.
     * @return the slot index
     */
    public abstract int getIndex();

    /**
     * Sets the slot index to display without registering listeners for
     * updates.
     * @param index the slot index
     */
    public abstract void setIndexNoListeners(final int index);

}
