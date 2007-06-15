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
     * Remove all {@link GUIElements} from this gui.
     */
    public void clear()
    {
        elements.clear();
    }

    /**
     * Add a {@link GUIElement} to this gui.
     *
     * @param element The <code>GUIElement</code> to add.
     */
    public void add(final GUIElement element)
    {
        elements.add(element);
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
                if (element instanceof GUIMap)
                {
                    final GUIMap mel = (GUIMap)element;
                    mel.redraw(g);
                }
                else
                {
                    g.drawImage(element.getBuffer(), element.getX(), element.getY(), jxcWindow);
                }
                element.resetChanged();
            }
        }
    }

    /**
     * Repaint the gui and clear the changed flags of all repainted elements.
     *
     * @param g The <code>Graphics</code> to paint into.
     *
     * @param jxWindow The window to deliver change events to.
     */
    public void redrawElements(final Graphics g, final JXCWindow jxcWindow)
    {
        for (final GUIElement element : elements)
        {
            if (element.isVisible())
            {
                if (element instanceof GUIMap)
                {
                    final GUIMap mel = (GUIMap)element;
                    final Graphics gg = element.getBuffer().createGraphics();
                    mel.redraw(gg);
                    gg.dispose();
                }
                g.drawImage(element.getBuffer(), element.getX(), element.getY(), jxcWindow);
                element.resetChanged();
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

        return false;
    }

    /**
     * Return the first {@link GUIText} gui element of this gui.
     *
     * @return The <code>GUIText</code> element, or <code>null</code> if this
     * gui does not contain any <code>GUIText</code> gui elements.
     */
    public GUIElement getFirstTextArea()
    {
        for (final GUIElement element : elements)
        {
            if ((element instanceof GUIText) && element.isVisible())
            {
                return element;
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
}
