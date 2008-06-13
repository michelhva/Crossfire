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
package com.realtime.crossfire.jxclient.window;

import com.realtime.crossfire.jxclient.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.Gui;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

/**
 * Tracks mouse actions and delivers mouse events to affected {@link
 * GUIElement}.
 *
 * <p>XXX: some delivered MouseEvents are not relative to the underlying
 * GUIElement.
 *
 * @author Andreas Kirschbaum
 */
public class MouseTracker implements MouseInputListener
{
    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The renderer to access dialogs/gui elements.
     */
    private JXCWindowRenderer windowRenderer = null;

    /**
     * The gui element in which the mouse is.
     */
    private GUIElement mouseElement = null;

    /**
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     */
    public MouseTracker(final boolean debugGui)
    {
        this.debugGui = debugGui;
    }

    public void init(final JXCWindowRenderer windowRenderer)
    {
        this.windowRenderer = windowRenderer;
    }

    /**
     * Return the gui element in which the mouse is.
     *
     * @return The gui element in which the mouse is.
     */
    public GUIElement getMouseElement()
    {
        return mouseElement;
    }

    /** {@inheritDoc} */
    public void mouseDragged(final MouseEvent e)
    {
        if (mouseElement != null)
        {
            e.translatePoint(-mouseElement.setElementX()-windowRenderer.getOffsetX(), -mouseElement.setElementY()-windowRenderer.getOffsetY());
            mouseElement.mouseMoved(e);
            mouseElement.mouseDragged(e);
        }
    }

    /** {@inheritDoc} */
    public void mouseMoved(final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
        if (mouseElement != null)
        {
            mouseElement.mouseMoved(e);
        }
    }

    /** {@inheritDoc} */
    public void mouseClicked(final MouseEvent e)
    {
        // ignore
    }

    /** {@inheritDoc} */
    public void mousePressed(final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
        if (mouseElement != null)
        {
            mouseElement.mousePressed(e);
        }
    }

    /** {@inheritDoc} */
    public void mouseReleased(final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        final boolean isClicked = element != null && mouseElement == element;
        enterElement(element, e);
        if (mouseElement != null)
        {
            mouseElement.mouseReleased(e);
        }
        if (isClicked)
        {
            assert mouseElement != null;
            mouseElement.mouseClicked(e);
        }
    }

    /** {@inheritDoc} */
    public void mouseEntered(final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
    }

    /** {@inheritDoc} */
    public void mouseExited(final MouseEvent e)
    {
        enterElement(null, e);
    }

    /**
     * Set a new {@link #mouseElement} and generate entered/exited events.
     *
     * @param element The new element; it may be <code>null</code>.
     *
     * @param e The event parameter.
     */
    private void enterElement(final GUIElement element, final MouseEvent e)
    {
        if (mouseElement == element)
        {
            return;
        }

        if (mouseElement != null)
        {
            mouseElement.mouseExited(e);
            if (debugGui)
            {
                mouseElement.setChanged();
            }
        }

        mouseElement = element;

        if (mouseElement != null)
        {
            if (debugGui)
            {
                mouseElement.setChanged();
            }
            mouseElement.mouseEntered(e);
        }
    }

    /**
     * Find the gui element for a given {@link MouseEvent}. If a gui element
     * was found, update the event mouse coordinates to be relative to the gui
     * element.
     *
     * @param e The mouse event to process.
     *
     * @return The gui element found, or <code>null</code> if none was found.
     */
    private GUIElement findElement(final MouseEvent e)
    {
        GUIElement elected = null;

        for (final Gui dialog : windowRenderer.getOpenDialogs())
        {
            if (!dialog.isHidden(windowRenderer.getGuiState()))
            {
                elected = manageMouseEvents(dialog, e);
                if (elected != null)
                {
                    break;
                }
            }
            if (dialog.isModal())
            {
                return null;
            }
        }

        if (elected == null)
        {
            elected = manageMouseEvents(windowRenderer.getCurrentGui(), e);
        }

        if (elected != null)
        {
            e.translatePoint(-elected.setElementX()-windowRenderer.getOffsetX(), -elected.setElementY()-windowRenderer.getOffsetY());
        }

        return elected;
    }

    private GUIElement manageMouseEvents(final Gui gui, final MouseEvent e)
    {
        final int x = e.getX()-windowRenderer.getOffsetX();
        final int y = e.getY()-windowRenderer.getOffsetY();
        return gui.getElementFromPoint(x, y);
    }
}
