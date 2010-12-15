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
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     */
    public MouseTracker(final boolean debugGui) {
        this.debugGui = debugGui;
    }

    public void mouseDragged(@Nullable final GUIElement element, @NotNull final MouseEvent e) {
        if (element != null) {
            element.mouseMoved(e);
            element.mouseDragged(e);
        }
    }

    public void mouseMoved(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        enterElement(element, e);
        if (mouseElement != null) {
            mouseElement.mouseMoved(e);
        }
    }

    public void mousePressed(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        enterElement(element, e);
        if (mouseElement != null) {
            mouseElement.mousePressed(e);
        }
    }

    public void mouseReleased(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        final boolean isClicked = element != null && mouseElement == element;
        enterElement(element, e);
        if (isClicked) {
            // cannot use mouseElement here: it might be invalid if the
            // previous call to mouseReleased() did close the owner dialog
            assert element != null;
            element.mouseClicked(e);
        }
        if (mouseElement != null) {
            mouseElement.mouseReleased(e);
        }
    }

    public void mouseEntered(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        enterElement(element, e);
    }

    public void mouseExited(@NotNull final MouseEvent e) {
        enterElement(null, e);
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
            final Dimension dimension = GuiUtils.getTextDimension(text, g.getFont());
            g.setColor(Color.BLACK);
            g.fillRect(0, 2, dimension.width+4, dimension.height+8);
            g.setColor(Color.RED);
            g.drawString(text, 2, 16);
            g.drawRect(GuiUtils.getElementX(component), GuiUtils.getElementY(component), component.getWidth()-1, component.getHeight()-1);
        }
    }

}
