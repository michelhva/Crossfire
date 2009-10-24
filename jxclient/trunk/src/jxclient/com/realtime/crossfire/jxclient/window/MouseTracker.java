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

import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.Gui;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    /*XXX:@NotNull*/
    private JXCWindowRenderer windowRenderer = null;

    /**
     * The gui element in which the mouse is.
     */
    @Nullable
    private GUIElement mouseElement = null;

    /**
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     */
    public MouseTracker(final boolean debugGui)
    {
        this.debugGui = debugGui;
    }

    public void init(@NotNull final JXCWindowRenderer windowRenderer)
    {
        this.windowRenderer = windowRenderer;
    }

    /**
     * Return the gui element in which the mouse is.
     *
     * @return The gui element in which the mouse is.
     */
    @Nullable
    public Component getMouseElement()
    {
        return mouseElement;
    }

    /** {@inheritDoc} */
    @Override
    public void mouseDragged(@NotNull final MouseEvent e)
    {
        final GUIElement element = mouseElement;
        if (element != null)
        {
            e.translatePoint(-element.getElementX()-windowRenderer.getOffsetX(), -element.getElementY()-windowRenderer.getOffsetY());
            element.mouseMoved(e);
            element.mouseDragged(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseMoved(@NotNull final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
        if (mouseElement != null)
        {
            mouseElement.mouseMoved(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(@NotNull final MouseEvent e)
    {
        // ignore
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(@NotNull final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
        if (mouseElement != null)
        {
            mouseElement.mousePressed(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(@NotNull final MouseEvent e)
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
            // cannot use mouseElement here: it might be invalid if the
            // previous call to mouseReleased() did close the owner dialog
            assert element!= null;
            element.mouseClicked(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseEntered(@NotNull final MouseEvent e)
    {
        final GUIElement element = findElement(e);
        enterElement(element, e);
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(@NotNull final MouseEvent e)
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
    private void enterElement(@Nullable final GUIElement element, @NotNull final MouseEvent e)
    {
        if (mouseElement == element)
        {
            return;
        }

        final GUIElement tmp = mouseElement;
        if (tmp != null)
        {
            tmp.mouseExited(e);
            if (debugGui)
            {
                tmp.setChanged();
            }
        }

        mouseElement = element;

        if (element != null)
        {
            if (debugGui)
            {
                element.setChanged();
            }
            element.mouseEntered(e);
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
    @Nullable
    private GUIElement findElement(@NotNull final MouseEvent e)
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
            e.translatePoint(-elected.getElementX()-windowRenderer.getOffsetX(), -elected.getElementY()-windowRenderer.getOffsetY());
        }

        return elected;
    }

    @Nullable
    private GUIElement manageMouseEvents(@NotNull final Gui gui, @NotNull final MouseEvent e)
    {
        final int x = e.getX()-windowRenderer.getOffsetX();
        final int y = e.getY()-windowRenderer.getOffsetY();
        return gui.getElementFromPoint(x, y);
    }
}
