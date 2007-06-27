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
package com.realtime.crossfire.jxclient;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Combines a list of {@link GUIElement}s to for a gui.
 *
 * @author Andreas Kirschbaum
 */
public class Gui
{
    /**
     * The list of {@link GUIElement}s comprising this gui.
     */
    private final List<GUIElement> elements = new ArrayList<GUIElement>();

    /**
     * The tooltip to use, or <code>null</code> if no tooltips should be shown.
     */
    private GUILabel tooltip = null;

    /**
     * Remove all {@link GUIElements} from this gui.
     */
    public void clear()
    {
        for (final GUIElement element : elements)
        {
            element.setGui(null);
        }
        elements.clear();
    }

    /**
     * Add a {@link GUIElement} to this gui. The element must not be added to
     * more than one gui at a time.
     *
     * @param element The <code>GUIElement</code> to add.
     */
    public void add(final GUIElement element)
    {
        if (element.getGui() != null) throw new IllegalArgumentException();

        elements.add(element);
        element.setGui(this);
    }

    /**
     * Return the number of {@link GUIElement}s of this gui.
     *
     * @return The number gui elements.
     */
    public int size()
    {
        return elements.size();
    }

    /**
     * Return one {@link GUIElement} of this gui.
     *
     * @param index The gui element index.
     *
     * @return The gui element.
     */
    public GUIElement get(final int index)
    {
        return elements.get(index);
    }

    /**
     * Repaint the gui and clear the changed flags of all repainted elements.
     *
     * @param g The <code>Graphics</code> to paint into.
     *
     * @param jxWindow The window to deliver change events to.
     */
    public void redraw(final Graphics g, final JXCWindow jxcWindow)
    {
        for (final GUIElement element : elements)
        {
            if (element.isVisible())
            {
                final BufferedImage bufferedImage = element.getBuffer();
                synchronized(bufferedImage)
                {
                    g.drawImage(bufferedImage, element.getX(), element.getY(), jxcWindow);
                    element.resetChanged();
                }
            }
        }

        if (tooltip != null && tooltip.isVisible())
        {
            final BufferedImage bufferedImage = tooltip.getBuffer();
            synchronized(bufferedImage)
            {
                g.drawImage(bufferedImage, tooltip.getX(), tooltip.getY(), jxcWindow);
                tooltip.resetChanged();
            }
        }
    }

    /**
     * Check whether any visible gui element of this gui has been changed since
     * it was painted last time.
     *
     * @return <code>true</code> if any gui element has changed;
     * <code>false</code> otherwise.
     */
    public boolean needRedraw()
    {
        for (final GUIElement element : elements)
        {
            if (element.isVisible() && element.hasChanged())
            {
                return true;
            }
        }

        return tooltip != null && tooltip.isVisible() && tooltip.hasChanged();
    }

    /**
     * Return the first {@link GUIText} gui element of this gui.
     *
     * @return The <code>GUIText</code> element, or <code>null</code> if this
     * gui does not contain any <code>GUIText</code> gui elements.
     */
    public GUIText getFirstTextArea()
    {
        for (final GUIElement element : elements)
        {
            if ((element instanceof GUIText) && element.isVisible())
            {
                return (GUIText)element;
            }
        }

        return null;
    }

    /**
     * Determine the {@link GUIElement} for a given coordinate.
     *
     * @param x The x-coordinate to check.
     *
     * @param y The y-coordinate to check.
     *
     * @return The <code>GUIElement</code> at the given coordinate, or
     * <code>null</code> if none was found.
     */
    public GUIElement getElementFromPoint(final int x, final int y)
    {
        GUIElement elected = null;
        for (final GUIElement element : elements)
        {
            if (element.isVisible())
            {
                if (element.getX() <= x && x < element.getX()+element.getWidth())
                {
                    if (element.getY() <= y && y < element.getY()+element.getHeight())
                    {
                        elected = element;
                    }
                }
            }
        }

        return elected;
    }

    /**
     * Set the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @param tooltip The tooltip to use, or <code>null</code>.
     */
    public void setTooltip(final GUILabel tooltip)
    {
        this.tooltip = tooltip;
    }

    /**
     * Return the tooltip to use, or <code>null</code> if no tooltips should be
     * shown.
     *
     * @return The tooltip, or <code>null</code>.
     */
    public GUILabel getTooltip()
    {
        return tooltip;
    }
}
