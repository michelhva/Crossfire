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

package com.realtime.crossfire.jxclient.gui.misc;

import com.realtime.crossfire.jxclient.gui.gui.AbstractGUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.GuiUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tracks mouse actions and delivers mouse events to affected {@link
 * GUIElement}.
 * <p/>
 * <p>XXX: some delivered MouseEvents are not relative to the underlying
 * GUIElement.
 * @author Andreas Kirschbaum
 */
public class MouseTracker {

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The gui element in which the mouse is.
     */
    @Nullable
    private GUIElement mouseElement = null;

    /**
     * The active component.
     */
    @Nullable
    private AbstractGUIElement activeComponent = null;

    /**
     * Whether a dragging operation is in progress.
     */
    private boolean isDragging = false;

    /**
     * Whether a button release event is considered a "click".
     */
    private boolean isClicked = false;

    /**
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     */
    public MouseTracker(final boolean debugGui) {
        this.debugGui = debugGui;
    }

    /**
     * Handles a mouse dragged event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    @SuppressWarnings("UnusedParameters")
    public void mouseDragged(@Nullable final GUIElement element, @NotNull final MouseEvent e) {
        isClicked = false;
        if (mouseElement != null) {
            mouseElement.mouseMoved(e);
        }
        if (isDragging && mouseElement != null) {
            mouseElement.mouseDragged(e);
        }
    }

    /**
     * Handles a mouse moved event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseMoved(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        enterElement(element, e);
        if (mouseElement != null) {
            mouseElement.mouseMoved(e);
        }
    }

    /**
     * Handles a mouse pressed event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mousePressed(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        enterElement(element, e);
        if (mouseElement != null) {
            mouseElement.mousePressed(e);
        }
        isDragging = true;
        isClicked = true;
    }

    /**
     * Handles a mouse released event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseReleased(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        final boolean tmpIsClicked = isClicked;
        isDragging = false;
        enterElement(element, e);
        if (tmpIsClicked && element != null) {
            // cannot use mouseElement here: it might be invalid if the
            // previous call to mouseReleased() did close the owner dialog
            element.mouseClicked(e);
        }
        if (mouseElement != null) {
            mouseElement.mouseReleased(e);
        }
    }

    /**
     * Handles a mouse entered event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseEntered(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        isClicked = false;
        if (!isDragging) {
            enterElement(element, e);
        }
    }

    /**
     * Handles a mouse exited event.
     * @param e the mouse event
     */
    public void mouseExited(@NotNull final MouseEvent e) {
        isClicked = false;
        if (!isDragging) {
            enterElement(null, e);
        }
    }

    /**
     * Sets a new {@link #mouseElement} and generate entered/exited events.
     * @param element the new element; it may be <code>null</code>
     * @param e the event parameter
     */
    private void enterElement(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        if (mouseElement == element) {
            return;
        }

        final GUIElement tmp = mouseElement;
        if (tmp != null) {
            tmp.mouseExited(e);
            if (activeComponent != null) {
                activeComponent.setChanged();
                activeComponent = null;
            }
        }

        mouseElement = element;

        if (element != null) {
            element.mouseEntered(e, debugGui);
            if (debugGui && activeComponent != element) {
                activeComponent = element;
                activeComponent.setChanged();
            }
        }
    }

    /**
     * Marks the active component in a {@link Graphics} instance.
     * @param g the graphics
     */
    public void paintActiveComponent(@NotNull final Graphics g) {
        final AbstractGUIElement component = activeComponent;
        if (component != null) {
            final String text = component.getName();
            final Dimension dimension = GuiUtils.getTextDimension(text, g.getFontMetrics());
            g.setColor(Color.BLACK);
            g.fillRect(0, 2, dimension.width+4, dimension.height+8);
            g.setColor(Color.RED);
            g.drawString(text, 2, 16);
            g.drawRect(GuiUtils.getElementX(component), GuiUtils.getElementY(component), component.getWidth()-1, component.getHeight()-1);
        }
    }

}
