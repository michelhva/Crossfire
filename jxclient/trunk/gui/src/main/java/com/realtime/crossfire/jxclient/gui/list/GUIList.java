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

import com.realtime.crossfire.jxclient.gui.commands.CommandList;
import com.realtime.crossfire.jxclient.gui.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElementListener;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.gui.item.GUIItemItem;
import com.realtime.crossfire.jxclient.gui.scrollable.GUIScrollable;
import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link GUIElement} that displays a list of entries.
 * @author Andreas Kirschbaum
 */
public abstract class GUIList extends ActivatableGUIElement implements GUIScrollable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The height of a list cell in pixels.
     */
    private final int cellHeight;

    /**
     * The {@link GUIListCellRenderer} for the {@link #list}.
     */
    @NotNull
    private final GUIListCellRenderer listCellRenderer;

    /**
     * The {@link CommandList} to execute on double-clicks or <code>null</code>
     * to ignore double-clicks.
     */
    @Nullable
    private final CommandList doubleClickCommandList;

    /**
     * The list model of {@link #list}.
     */
    @NotNull
    private final DefaultListModel model = new DefaultListModel();

    /**
     * The list used to display the cells.
     */
    @NotNull
    private final JList list = new JList(model);

    /**
     * The viewport used by {@link #scrollPane}.
     */
    @NotNull
    private final GUIListViewport viewport = new GUIListViewport();

    /**
     * The scroll pane instance used to display the list.
     */
    @NotNull
    private final JScrollPane scrollPane;

    /**
     * The index of the currently shown tooltip. Set to <code>-1</code> if no
     * tooltip is shown.
     */
    private int tooltipIndex = -1;

    /**
     * The location of the tooltip. Set to <code>null</code> if no tooltip is
     * shown.
     */
    @Nullable
    private Rectangle tooltipRectangle = null;

    /**
     * The {@link ListSelectionListener} attached to {@link #list}.
     */
    @NotNull
    private final ListSelectionListener listSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(@NotNull final ListSelectionEvent e) {
            selectionChanged();
        }

    };

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param cellWidth the width of each cell
     * @param cellHeight the height of each cell
     * @param listCellRenderer the renderer for the list
     * @param doubleClickCommandList the command list to execute on double-click
     * or <code>null</code> to ignore double-clicks
     */
    protected GUIList(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int cellWidth, final int cellHeight, @NotNull final GUIListCellRenderer listCellRenderer, @Nullable final CommandList doubleClickCommandList) {
        super(tooltipManager, elementListener, name, Transparency.TRANSLUCENT);
        this.cellHeight = cellHeight;
        this.listCellRenderer = listCellRenderer;
        this.doubleClickCommandList = doubleClickCommandList;

        list.setCellRenderer(listCellRenderer);
        list.setFixedCellWidth(cellWidth);
        list.setFixedCellHeight(cellHeight);
        list.setOpaque(false);
        list.setFocusable(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(listSelectionListener);

        viewport.setView(list);
        viewport.setScrollMode(JViewport.BLIT_SCROLL_MODE);
        viewport.setOpaque(false);
        viewport.setFocusable(false);

        scrollPane = new JScrollPane(null, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewport(viewport);
        scrollPane.setOpaque(false);
        scrollPane.setFocusable(false);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        add(scrollPane);

        listCellRenderer.setSize(getWidth(), cellHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        list.removeListSelectionListener(listSelectionListener);
        synchronized (getTreeLock()) {
            resizeElements(0);
        }
    }

    /**
     * Returns the {@link GUIElement} for a given index.
     * @param index the index
     * @return the gui element
     */
    @NotNull
    public GUIElement getElement(final int index) {
        return (GUIElement)model.get(index);
    }

    /**
     * Adds an {@link GUIElement} to the list.
     * @param element the element to add
     */
    protected void addElement(@NotNull final GUIElement element) {
        assert Thread.holdsLock(getTreeLock());
        model.addElement(element);
        list.setSize(getWidth(), Integer.MAX_VALUE);
        viewport.update();
        if (model.getSize() == 1) {
            setSelectedIndex(0);
        }
    }

    /**
     * Changes the number of list elements. If the new element count is less
     * than the current count, excess elements are cut off. Otherwise the caller
     * has to add elements with {@link #addElement(GUIElement)}.
     * @param newSize the new element count
     * @return the number of elements to add by the caller
     */
    protected int resizeElements(final int newSize) {
        assert Thread.holdsLock(getTreeLock());
        final int index = list.getSelectedIndex();
        final int oldSize = model.getSize();
        if (newSize < oldSize) {
            for (int i = newSize; i < oldSize; i++) {
                final GUIElement element = (GUIElement)model.get(i);
                if (element instanceof GUIItemItem) {
                    element.dispose();
                }
            }
            model.removeRange(newSize, oldSize-1);
            list.setSize(getWidth(), Integer.MAX_VALUE);
            if (index >= newSize && newSize > 0) {
                setSelectedIndex(newSize-1);
            }
            setChanged();
        }
        return oldSize;
    }

    /**
     * Returns whether the selection can be moved.
     * @param diffLines the distance in lines to move
     * @param diffElements the distance in elements to move
     * @return whether moving is possible
     */
    public boolean canMoveSelection(final int diffLines, final int diffElements) {
        synchronized (getTreeLock()) {
            final int distance;
            switch (list.getLayoutOrientation()) {
            case JList.HORIZONTAL_WRAP:
                distance = (list.getWidth()/cellHeight)*diffLines+diffElements;
                break;

            default:
                distance = diffLines+diffElements;
                break;
            }
            final int index = list.getSelectedIndex();
            if (distance > 0) {
                return index == -1 || index+distance < list.getModel().getSize();
            } else if (distance < 0) {
                return index == -1 || index >= -distance;
            } else {
                return false;
            }
        }
    }

    /**
     * Moves the selection.
     * @param diffLines the distance in lines to move
     * @param diffElements the distance in elements to move
     */
    public void moveSelection(final int diffLines, final int diffElements) {
        synchronized (getTreeLock()) {
            final int distance;
            switch (list.getLayoutOrientation()) {
            case JList.HORIZONTAL_WRAP:
                distance = (list.getWidth()/cellHeight)*diffLines+diffElements;
                break;

            default:
                distance = diffLines+diffElements;
                break;
            }
            final int index = list.getSelectedIndex();
            final int newIndex;
            if (distance > 0) {
                if (index == -1) {
                    newIndex = 0;
                } else {
                    newIndex = Math.min(index+distance, list.getModel().getSize()-1);
                }
            } else if (distance < 0) {
                if (index == -1) {
                    newIndex = list.getModel().getSize()-1;
                } else {
                    newIndex = Math.max(index+distance, 0);
                }
            } else {
                if (index == -1) {
                    newIndex = 0;
                } else {
                    newIndex = index;
                }
            }
            setSelectedIndex(newIndex);
        }
    }

    /**
     * Returns whether the list can be scrolled.
     * @param distance the distance to scroll
     * @return whether scrolling is possible
     */
    @Override
    public boolean canScroll(final int distance) {
        synchronized (getTreeLock()) {
            final Adjustable scrollBar = scrollPane.getVerticalScrollBar();
            if (distance > 0) {
                return scrollBar.getValue() < scrollBar.getMaximum()-scrollBar.getVisibleAmount();
            } else if (distance < 0) {
                return scrollBar.getValue() > scrollBar.getMinimum();
            } else {
                return false;
            }
        }
    }

    /**
     * Moves the list.
     * @param distance the distance to scroll
     */
    @Override
    public void scroll(final int distance) {
        synchronized (getTreeLock()) {
            final Adjustable scrollBar = scrollPane.getVerticalScrollBar();
            final int value = scrollBar.getValue()+distance*cellHeight;
            scrollBar.setValue(value);
            final int index = list.getSelectedIndex();
            if (index != -1) {
                final int firstIndex = list.getFirstVisibleIndex();
                if (index < firstIndex) {
                    switch (list.getLayoutOrientation()) {
                    case JList.HORIZONTAL_WRAP:
                        final int columns = list.getWidth()/cellHeight;
                        setSelectedIndex(firstIndex+index%columns);
                        break;

                    default:
                        setSelectedIndex(firstIndex);
                        break;
                    }
                } else {
                    final int lastIndex = list.getLastVisibleIndex();
                    if (index > lastIndex) {
                        switch (list.getLayoutOrientation()) {
                        case JList.HORIZONTAL_WRAP:
                            final int columns = list.getWidth()/cellHeight;
                            final int newTmpColumn = lastIndex-lastIndex%columns+index%columns;
                            final int newColumn;
                            if (newTmpColumn <= lastIndex) {
                                newColumn = newTmpColumn;
                            } else if (newTmpColumn >= columns) {
                                newColumn = newTmpColumn-columns;
                            } else {
                                newColumn = lastIndex;
                            }
                            setSelectedIndex(newColumn);
                            break;

                        default:
                            setSelectedIndex(lastIndex);
                            break;
                        }
                    }
                }
            }
        }
        setChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetScroll() {
        setSelectedIndex(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
        doSelect(e);
        if (doubleClickCommandList != null && e.getClickCount() > 1) {
            doubleClickCommandList.execute();
        }
        super.mouseClicked(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(@NotNull final MouseEvent e, final boolean debugGui) {
        super.mouseEntered(e, debugGui);
        doTooltip(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        super.mouseExited(e);
        doTooltip(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        doSelect(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(@NotNull final MouseEvent e) {
        super.mouseMoved(e);
        doTooltip(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
        super.mouseClicked(e);
        doSelect(e);
    }

    /**
     * Selects the list entry corresponding to a {@link MouseEvent} instance.
     * @param e the mouse event instance
     */
    private void doSelect(@NotNull final MouseEvent e) {
        synchronized (getTreeLock()) {
            setSelectedIndex(list.getFirstVisibleIndex()+list.locationToIndex(e.getPoint()));
        }
    }

    /**
     * Updates the tooltip text corresponding to a {@link MouseEvent} instance.
     * @param e the mouse event instance
     */
    private void doTooltip(@NotNull final MouseEvent e) {
        synchronized (getTreeLock()) {
            final int index = list.locationToIndex(e.getPoint());
            if (index == -1) {
                tooltipIndex = -1;
                tooltipRectangle = null;
                updateTooltip();
                return;
            }

            final Rectangle rectangle = list.getCellBounds(index, index);
            if (rectangle == null || !rectangle.contains(e.getPoint())) {
                tooltipIndex = -1;
                tooltipRectangle = null;
                updateTooltip();
                return;
            }

            tooltipIndex = list.getFirstVisibleIndex()+index;
            tooltipRectangle = rectangle;
            updateTooltip();
        }
    }

    /**
     * Update the selected list entry.
     * @param newIndex the new selected list entry
     */
    protected void setSelectedIndex(final int newIndex) {
        synchronized (getTreeLock()) {
            final int newIndex2 = Math.min(Math.max(newIndex, 0), list.getModel().getSize()-1);
            final int index = list.getSelectedIndex();
            if (newIndex2 == index) {
                return;
            }

            list.setSelectedIndex(newIndex2);
            if (newIndex2 >= 0) {
                list.ensureIndexIsVisible(newIndex2);
            }
        }
        setChanged();
    }

    /**
     * Called whenever the selected list entry has changed.
     */
    protected void selectionChanged() {
        synchronized (getTreeLock()) {
            selectionChanged(list.getSelectedIndex());
        }
    }

    /**
     * Called whenever the selected list entry has changed.
     * @param selectedIndex the selected list entry
     */
    protected abstract void selectionChanged(final int selectedIndex);

    /**
     * Updates the current tooltip text.
     */
    private void updateTooltip() {
        if (tooltipIndex == -1) {
            setTooltipText(null);
        } else {
            final Rectangle rectangle = tooltipRectangle;
            if (rectangle == null) {
                setTooltipText(null);
            } else {
                final Component gui = GuiUtils.getGui(this);
                if (gui == null) {
                    tooltipIndex = -1;
                    tooltipRectangle = null;
                    setTooltipText(null);
                } else {
                    updateTooltip(tooltipIndex, gui.getX()+getX()+rectangle.x, gui.getY()+getY()+rectangle.y, rectangle.width, rectangle.height);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChanged() {
        super.setChanged();
        updateTooltip();
    }

    /**
     * Updates the tooltip text.
     * @param index the index to use
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param w the width of the cell
     * @param h the height of the cell
     */
    protected abstract void updateTooltip(final int index, final int x, final int y, final int w, final int h);

    /**
     * Sets the layout orientation. See {@link JList#setLayoutOrientation(int)}
     * and {@link JList#setVisibleRowCount(int)}.
     * @param layoutOrientation the layout orientation
     * @param visibleRowCount the number of visible rows
     */
    protected void setLayoutOrientation(final int layoutOrientation, final int visibleRowCount) {
        synchronized (getTreeLock()) {
            list.setLayoutOrientation(layoutOrientation);
            list.setVisibleRowCount(visibleRowCount);
        }
    }

    /**
     * Returns the selected list object.
     * @return the selected object or <code>null</code> if none is selected
     */
    @NotNull
    protected Object getSelectedObject() {
        synchronized (getTreeLock()) {
            return list.getSelectedValue();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        final Dimension result = list.getPreferredSize();
        return result == null ? super.getPreferredSize() : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize() {
        final Dimension result = list.getMinimumSize();
        return result == null ? super.getMinimumSize() : result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        scrollPane.setSize(width, height);
        listCellRenderer.setSize(width, cellHeight);
    }

}
