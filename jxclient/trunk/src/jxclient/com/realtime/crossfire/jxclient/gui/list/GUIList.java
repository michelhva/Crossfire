//
// This file is part of JXClient, the Fullscreen Java Crossfire Client.
//
//    JXClient is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    JXClient is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JXClient; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//
// JXClient is (C)2005 by Yann Chachkoff.
//
package com.realtime.crossfire.jxclient.gui.list;

import com.realtime.crossfire.jxclient.gui.ActivatableGUIElement;
import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.util.MathUtils;
import com.realtime.crossfire.jxclient.window.JXCWindow;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A {@link GUIElement} that displays a list of entries.
 * @author Andreas Kirschbaum
 */
public abstract class GUIList extends ActivatableGUIElement
{
    /**
     * The height of a list cell in pixels.
     */
    private final int cellHeight;

    /**
     * The list model of {@link #list}.
     */
    private final DefaultListModel model = new DefaultListModel();

    /**
     * The list used to display the cells.
     */
    private final JList list = new JList(model);

    /**
     * The scroll pane instance used to display the list.
     */
    private final JScrollPane scrollPane;

    /**
     * The {@link ListSelectionListener} attached to {@link #list}.
     */
    private final ListSelectionListener listSelectionListener = new ListSelectionListener()
    {
        /** {@inheritDoc} */
        public void valueChanged(final ListSelectionEvent e)
        {
            selectionChanged(list.getSelectedIndex());
        }
    };

    /**
     * Creates a new instance.
     * @param window the <code>JXCWindow</code> this element belongs to
     * @param name the name of this element
     * @param x the x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param y the y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>
     * @param w the width for drawing this element to screen
     * @param h the height for drawing this element to screen
     * @param cellHeight the height of each cell
     * @param listCellRenderer the renderer for the list
     */
    protected GUIList(final JXCWindow window, final String name, final int x, final int y, final int w, final int h, final int cellHeight, final ListCellRenderer listCellRenderer)
    {
        super(window, name, x, y, w, h, Transparency.TRANSLUCENT);

        this.cellHeight = cellHeight;

        list.setCellRenderer(listCellRenderer);
        list.setFixedCellHeight(cellHeight);
        list.setOpaque(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final Dimension size = new Dimension(w, h);

        scrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(size);
        scrollPane.setMinimumSize(size);
        scrollPane.setMaximumSize(size);
        scrollPane.setSize(size);
        scrollPane.setLocation(x, y);
        scrollPane.getViewport().setSize(size);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        list.addListSelectionListener(listSelectionListener);

        synchronized (bufferedImageSync)
        {
            final Graphics2D g = bufferedImage.createGraphics();
            try
            {
                render(g);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void render(final Graphics g)
    {
        final Graphics2D g2 = (Graphics2D)g;
        final Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.Clear);
        g.setColor(new Color(0, 255, 0, 255));
        g.fillRect(0, 0, getWidth(), getHeight());
        g2.setComposite(composite);

        scrollPane.paint(g);
        scrollPane.getViewport().paint(g);
    }

    /**
     * Adds an {@link GUIElement} to the list.
     * @param element the element to add
     */
    public void addElement(final GUIElement element)
    {
        model.addElement(element);
        list.setSize(list.getPreferredSize());
        setChanged();
    }

    /**
     * Changes the number of list elements. If the new element count is less
     * than the current count, excess elements are cut off. Otherwise the
     * caller has to add elements with {@link #addElement(GUIElement)}.
     * @param newSize the new element count
     * @return the number of elements to add by the caller
     */
    protected int resizeElements(final int newSize)
    {
        final int oldSize = model.getSize();
        if (newSize < oldSize)
        {
            model.removeRange(newSize, oldSize-1);
            list.setSize(list.getPreferredSize());
            setChanged();
        }
        return oldSize;
    }

    /**
     * Returns whether the selection can be moved.
     * @param distance the distance to move
     * @return whether moving is possible
     */
    public boolean canMoveSelection(final int distance)
    {
        final int index = list.getSelectedIndex();
        if (distance > 0)
        {
            return index == -1 || index < list.getModel().getSize()-1;
        }
        else if (distance < 0)
        {
            return index == -1 || index > 0;
        }
        else
        {
            return false;
        }
    }

    /**
     * Moves the selection.
     * @param distance the distance to move
     */
    public void moveSelection(final int distance)
    {
        final int index = list.getSelectedIndex();
        final int newIndex;
        if (distance > 0)
        {
            if (index == -1)
            {
                newIndex = 0;
            }
            else
            {
                newIndex = Math.min(index+distance, list.getModel().getSize()-1);
            }
        }
        else if (distance < 0)
        {
            if (index == -1)
            {
                newIndex = list.getModel().getSize()-1;
            }
            else
            {
                newIndex = Math.max(index+distance, 0);
            }
        }
        else
        {
            if (index == -1)
            {
                newIndex = 0;
            }
            else
            {
                newIndex = index;
            }
        }
        setSelectedIndex(newIndex);
    }

    /**
     * Returns whether the list can be scrolled.
     * @param distance the distance to scroll
     * @return whether scrolling is possible
     */
    public boolean canScroll(final int distance)
    {
        final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        if (distance > 0)
        {
            return scrollBar.getValue() < scrollBar.getMaximum()-scrollBar.getVisibleAmount();
        }
        else if (distance < 0)
        {
            return scrollBar.getValue() > scrollBar.getMinimum();
        }
        else
        {
            return false;
        }
    }

    /**
     * Moves the list.
     * @param distance the distance to scroll
     */
    public void scroll(final int distance)
    {
        final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        final int value = scrollBar.getValue()+distance*cellHeight;
        scrollBar.setValue(value);
        final int index = list.getSelectedIndex();
        if (index != -1)
        {
            final int firstIndex = list.getFirstVisibleIndex();
            if (index < firstIndex)
            {
                setSelectedIndex(firstIndex);
            }
            else
            {
                final int lastIndex = list.getLastVisibleIndex();
                if (index > lastIndex)
                {
                    setSelectedIndex(lastIndex);
                }
            }
        }
        setChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        super.mouseClicked(e);
        doSelect(e);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        super.mouseEntered(e);
        doTooltip(e);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        super.mouseExited(e);
        doTooltip(e);
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        super.mouseClicked(e);
        doSelect(e);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseMoved(final MouseEvent e)
    {
        super.mouseMoved(e);
        doTooltip(e);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseDragged(final MouseEvent e)
    {
        super.mouseClicked(e);
        doSelect(e);
    }

    /**
     * Selects the list entry corresponding to a {@link MouseEvent} instance.
     * @param e the mouse event instance
     */
    private void doSelect(final MouseEvent e)
    {
        setSelectedIndex(list.getFirstVisibleIndex()+MathUtils.div(e.getY(), cellHeight));
    }

    /**
     * Updates the tooltip text corresponding to a {@link MouseEvent} instance.
     * @param e the mouse event instance
     */
    private void doTooltip(final MouseEvent e)
    {
        updateTooltip(list.getFirstVisibleIndex()+MathUtils.div(e.getY(), cellHeight));
    }

    /**
     * Update the selected list entry.
     * @param newIndex the new selected list entry
     */
    protected void setSelectedIndex(final int newIndex)
    {
        final int newIndex2 = Math.min(Math.max(newIndex, 0), list.getModel().getSize()-1);
        final int index = list.getSelectedIndex();
        if (newIndex2 == index)
        {
            return;
        }

        list.setSelectedIndex(newIndex2);
        if (newIndex2 >= 0)
        {
            list.ensureIndexIsVisible(newIndex2);
        }
        setChanged();
    }

    /**
     * Called whenever the selected list entry has changed.
     * @param selectedIndex the selected list entry
     */
    protected abstract void selectionChanged(final int selectedIndex);

    /**
     * Updates the tooltip text.
     * @param index the index to use
     */
    protected abstract void updateTooltip(final int index);
}
