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
 * Copyright (C) 2006-2010 Andreas Kirschbaum.
 */

package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementChangedListener;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItemFactory;
import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import com.realtime.crossfire.jxclient.items.CfItem;
import com.realtime.crossfire.jxclient.items.FloorView;
import com.realtime.crossfire.jxclient.items.ItemView;
import com.realtime.crossfire.jxclient.items.LocationsListener;
import com.realtime.crossfire.jxclient.queue.CommandQueue;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIList} instance that displays {@link GUIItemItem} instances.
 * @author Andreas Kirschbaum
 */
public class GUIItemList extends GUIList {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GUIItemItemFactory} for creating new {@link GUIItemItem}
     * instances.
     */
    @NotNull
    private final GUIItemItemFactory itemItemFactory;

    /**
     * The {@link CommandQueue} to sending commands to the server.
     */
    @NotNull
    private final CommandQueue commandQueue;

    /**
     * The {@link CrossfireServerConnection} for sending commands to the
     * server.
     */
    @NotNull
    private final CrossfireServerConnection crossfireServerConnection;

    /**
     * The {@link FloorView} to use.
     */
    @NotNull
    private final FloorView floorView;

    /**
     * The {@link ItemView} to monitor.
     */
    @NotNull
    private final ItemView itemView;

    /**
     * The label to update with information about the selected item.
     */
    @Nullable
    private final AbstractLabel currentItem;

    /**
     * The {@link LocationsListener} to be notified about inventory changes.
     */
    @NotNull
    private final LocationsListener locationsListener = new LocationsListener() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void locationsModified(@NotNull final Integer[] changedSlots) {
            rebuildList(changedSlots);
        }

    };

    /**
     * The {@link GUIElementChangedListener} attached to all {@link
     * GUIItemItem} instances in the list.
     */
    @NotNull
    private final GUIElementChangedListener itemChangedListener = new GUIElementChangedListener() {
        /** {@inheritDoc} */
        @Override
        public void notifyChanged(@NotNull final GUIElement element) {
            element.resetChanged();
            selectionChanged();
            setChanged();
        }
    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param commandQueue the command queue for sending commands to the server
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param cellHeight the height of each cell
     * @param crossfireServerConnection the crossfire server connection for
     * sending commands to the server
     * @param floorView the floor view to use
     * @param itemView the item view to monitor
     * @param currentItem the label to update with information about the
     * selected item.
     * @param itemItemFactory the factory for creating item instances
     */
    public GUIItemList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final CommandQueue commandQueue, @NotNull final String name, final int x, final int y, final int w, final int h, final int cellHeight, @NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final FloorView floorView, @NotNull final ItemView itemView, @Nullable final AbstractLabel currentItem, @NotNull final GUIItemItemFactory itemItemFactory) {
        super(tooltipManager, elementListener, name, x, y, w, h, cellHeight, new ItemItemCellRenderer(itemItemFactory.newTemplateItem(cellHeight)));
        this.itemView = itemView;
        this.itemItemFactory = itemItemFactory;
        this.commandQueue = commandQueue;
        this.crossfireServerConnection = crossfireServerConnection;
        this.floorView = floorView;
        this.currentItem = currentItem;
        setLayoutOrientation(JList.HORIZONTAL_WRAP, -1);
        this.itemView.addLocationsListener(locationsListener);
        rebuildList(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        itemView.removeLocationsListener(locationsListener);
    }

    /**
     * Rebuilds the list cells.
     * @param changedSlots the changed slots; <code>null</code>=all slots
     */
    private void rebuildList(@Nullable final Integer[] changedSlots) {
        synchronized (getTreeLock()) {
            final int newSize = itemView.getSize();
            final int oldSize = resizeElements(newSize);
            if (oldSize < newSize) {
                for (int i = oldSize; i < newSize; i++) {
                    final GUIElement item = itemItemFactory.newItem(i);
                    addElement(item);
                    item.setChangedListener(itemChangedListener);
                    assert item.isElementVisible();
                    item.resetChanged();
                    assert !item.isChanged();
                }
                setChanged(changedSlots, oldSize);
            } else {
                setChanged(changedSlots, newSize);
            }
            selectionChanged();
        }
        setChanged();
    }

    /**
     * Marks some slots as modified.
     * @param changedSlots the slots to mark as modified; <code>null</code>=all
     * @param limit the limit; only slots less than this index are affected
     */
    private void setChanged(@Nullable final Integer[] changedSlots, final int limit) {
        if (changedSlots == null) {
            for (int i = 0; i < limit; i++) {
                setChanged(i);
            }
        } else {
            for (final int i : changedSlots) {
                if (i < limit) {
                    setChanged(i);
                }
            }
        }
    }

    /**
     * Marks one slot as modified.
     * @param index the slot index
     */
    private void setChanged(final int index) {
        getElement(index).setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void selectionChanged(final int selectedIndex) {
        if (currentItem != null) {
            final CfItem item = itemView.getItem(selectedIndex);
            if (item == null) {
                currentItem.setText("");
                currentItem.setTooltipText("");
            } else {
                final String tooltipText1 = item.getTooltipText1();
                final String tooltipText2 = item.getTooltipText2();
                final String tooltipText3 = item.getTooltipText3();
                currentItem.setText(tooltipText1+" ["+tooltipText2+"] "+tooltipText3);
                currentItem.setTooltipText(item.getTooltipText());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTooltip(final int index) {
        final CfItem item = itemView.getItem(index);
        setTooltipText(item == null ? null : item.getTooltipText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activeChanged() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            setActive(true);
            button1Clicked(e.getModifiersEx());
            break;

        case MouseEvent.BUTTON2:
            button2Clicked(e.getModifiersEx());
            break;

        case MouseEvent.BUTTON3:
            button3Clicked(e.getModifiersEx());
            break;
        }
    }

    /**
     * Called if the user has clicked the left mouse button.
     * @param modifiers the active modifiers
     */
    private void button1Clicked(final int modifiers) {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null) {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null) {
            return;
        }

        if ((modifiers&InputEvent.SHIFT_DOWN_MASK) == 0) {
            crossfireServerConnection.sendExamine(item.getTag());
        } else {
            crossfireServerConnection.sendLock(!item.isLocked(), item.getTag());
        }
    }

    /**
     * Called if the user has clicked the middle mouse button.
     * @param modifiers the active modifiers
     */
    private void button2Clicked(final int modifiers) {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null) {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null) {
            return;
        }

        if ((modifiers&InputEvent.SHIFT_DOWN_MASK) == 0) {
            crossfireServerConnection.sendApply(item.getTag());
        } else {
            crossfireServerConnection.sendMark(item.getTag());
        }
    }

    /**
     * Called if the user has clicked the right mouse button.
     * @param modifiers the active modifiers
     */
    private void button3Clicked(final int modifiers) {
        final GUIItemItem guiItem = getSelectedItem();
        if (guiItem == null) {
            return;
        }

        final CfItem item = guiItem.getItem();
        if (item == null) {
            return;
        }

        if (item.isLocked()) {
            crossfireServerConnection.drawInfo("This item is locked. To drop it, first unlock by SHIFT+leftclicking on it.", 3);
        } else {
            commandQueue.sendMove(floorView.getCurrentFloor(), item.getTag());
        }
    }

    /**
     * Returns the selected {@link GUIItemItem} instance.
     * @return the selected instance or <code>null</code> if none is selected
     */
    @Nullable
    public GUIItemItem getSelectedItem() {
        return (GUIItemItem)getSelectedObject();
    }

}
