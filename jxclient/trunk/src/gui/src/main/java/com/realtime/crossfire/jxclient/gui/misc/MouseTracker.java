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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
     * The maximum distance the mouse may have moved between the mouse pressed
     * and the mouse released even to generate a mouse clicked event.
     */
    private static final int CLICK_DISTANCE = 20;

    /**
     * Whether GUI elements should be highlighted.
     */
    private final boolean debugGui;

    /**
     * The {@link Writer} to write mouse debug to or <code>null</code>.
     */
    @Nullable
    private final Writer debugMouse;

    /**
     * A formatter for timestamps.
     */
    @NotNull
    private final DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS ");

    /**
     * The gui element in which the mouse is.
     */
    @Nullable
    private GUIElement mouseElement;

    /**
     * The active component.
     */
    @Nullable
    private AbstractGUIElement activeComponent;

    /**
     * Whether a dragging operation is in progress.
     */
    private boolean isDragging;

    /**
     * Whether a button release event is considered a "click".
     */
    private boolean isClicked;

    /**
     * The position that was clicked if {@link #isClicked} is set.
     */
    @NotNull
    private final Point clickPosition = new Point();

    /**
     * Creates a new instance.
     * @param debugGui whether GUI elements should be highlighted
     * @param debugMouse the writer to write mouse debug to or <code>null</code>
     */
    public MouseTracker(final boolean debugGui, @Nullable final Writer debugMouse) {
        this.debugGui = debugGui;
        this.debugMouse = debugMouse;
    }

    /**
     * Handles a mouse clicked event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseClicked(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mouseClicked: "+e+" ["+element+"]");
    }

    /**
     * Handles a mouse dragged event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    @SuppressWarnings("UnusedParameters")
    public void mouseDragged(@Nullable final GUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mouseDragged: "+e+" ["+element+"]");
        if (isClicked) {
            final double distance = clickPosition.distanceSq(e.getLocationOnScreen());
            if (distance > CLICK_DISTANCE) {
                debugMouseWrite("mouseDragged: distance "+distance+" is too far for a click event; click point="+clickPosition+", current point="+e.getLocationOnScreen());
                setClicked(false);
            }
        }
        if (mouseElement != null) {
            debugMouseWrite(mouseElement+".mouseMoved");
            mouseElement.mouseMoved(e);
        }
        if (isDragging && mouseElement != null) {
            debugMouseWrite(mouseElement+".mouseDragged");
            mouseElement.mouseDragged(e);
        }
    }

    /**
     * Handles a mouse moved event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseMoved(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mouseMoved: "+e+" ["+element+"]");
        enterElement(element, e);
        if (mouseElement != null) {
            debugMouseWrite(mouseElement+".mouseMoved");
            mouseElement.mouseMoved(e);
        }
    }

    /**
     * Handles a mouse pressed event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mousePressed(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mousePressed: "+e+" ["+element+"]");
        enterElement(element, e);
        if (mouseElement != null) {
            debugMouseWrite(mouseElement+".mousePressed");
            mouseElement.mousePressed(e);
        }
        setDragging(true);
        clickPosition.setLocation(e.getLocationOnScreen());
        setClicked(true);
    }

    /**
     * Handles a mouse released event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseReleased(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mouseReleased: "+e+" ["+element+"]");
        final boolean tmpIsClicked = isClicked;
        setDragging(false);
        enterElement(element, e);
        if (tmpIsClicked && element != null) {
            // cannot use mouseElement here: it might be invalid if the
            // previous call to mouseReleased() did close the owner dialog
            debugMouseWrite(element+".mouseClicked");
            element.mouseClicked(e);
        }
        if (mouseElement != null) {
            debugMouseWrite(mouseElement+".mouseReleased");
            mouseElement.mouseReleased(e);
        }
    }

    /**
     * Handles a mouse entered event.
     * @param element the affected GUI element
     * @param e the mouse event
     */
    public void mouseEntered(@Nullable final AbstractGUIElement element, @NotNull final MouseEvent e) {
        debugMouseWrite("mouseEntered: "+e+" ["+element+"]");
        setClicked(false);
        if (!isDragging) {
            enterElement(element, e);
        }
    }

    /**
     * Handles a mouse exited event.
     * @param e the mouse event
     */
    public void mouseExited(@NotNull final MouseEvent e) {
        debugMouseWrite("mouseExited: "+e);
        setClicked(false);
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
            setActiveComponent(null);
        }

        mouseElement = element;
        debugMouseWrite("mouseElement="+mouseElement);

        if (element != null) {
            element.mouseEntered(e, debugGui);
            if (debugGui) {
                setActiveComponent(element);
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

    /**
     * Writes a message to the mouse debug.
     * @param message the message to write
     */
    private void debugMouseWrite(@NotNull final CharSequence message) {
        if (debugMouse == null) {
            return;
        }

        try {
            debugMouse.append(simpleDateFormat.format(new Date()));
            debugMouse.append(message);
            debugMouse.append("\n");
            debugMouse.flush();
        } catch (final IOException ex) {
            System.err.println("Cannot write mouse debug: "+ex.getMessage());
            System.exit(1);
            throw new AssertionError();
        }
    }

    /**
     * Updates {@link #activeComponent}. Prints a debug message if the value
     * changes.
     * @param activeComponent the new value
     */
    private void setActiveComponent(@Nullable final AbstractGUIElement activeComponent) {
        if (this.activeComponent == activeComponent) {
            return;
        }

        if (this.activeComponent != null) {
            this.activeComponent.setChanged();
        }
        this.activeComponent = activeComponent;
        if (this.activeComponent != null) {
            this.activeComponent.setChanged();
        }
        debugMouseWrite("activeComponent="+activeComponent);
    }

    /**
     * Updates {@link #isDragging}. Prints a debug message if the value
     * changes.
     * @param isDragging the new value
     */
    private void setDragging(final boolean isDragging) {
        if (this.isDragging == isDragging) {
            return;
        }

        this.isDragging = isDragging;
        debugMouseWrite("isDragging="+isDragging);
    }

    /**
     * Updates {@link #isClicked}. Prints a debug message if the value changes.
     * @param isClicked the new value
     */
    private void setClicked(final boolean isClicked) {
        if (this.isClicked == isClicked) {
            return;
        }

        this.isClicked = isClicked;
        debugMouseWrite("isClicked="+isClicked);
    }

}
