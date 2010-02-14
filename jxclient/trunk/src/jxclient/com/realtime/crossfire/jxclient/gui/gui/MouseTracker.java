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

package com.realtime.crossfire.jxclient.gui.gui;

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
        final GUIElement element = windowRenderer.findElement(e);
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
        final GUIElement element = windowRenderer.findElement(e);
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
        final GUIElement element = windowRenderer.findElement(e);
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
        final GUIElement element = windowRenderer.findElement(e);
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
}
