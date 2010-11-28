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

import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GUIElement {

    /**
     * Releases all allocated resources.
     */
    void dispose();

    /**
     * Returns the {@link Gui} this element is part of.
     * @return the gui or <code>null</code>
     */
    @Nullable
    Gui getGui();

    /**
     * Sets the {@link Gui} this element is part of.
     * @param gui the gui or <code>null</code>
     */
    void setGui(@Nullable Gui gui);

    /**
     * Returns the element's absolute screen coordinate.
     * @return the element's absolute x coordinate
     */
    int getElementX();

    /**
     * Returns the element's absolute screen coordinate.
     * @return the element's absolute y coordinate
     */
    int getElementY();

    /**
     * Returns whether this element is visible.
     * @return whether this element is visible
     */
    boolean isElementVisible();

    /**
     * Sets whether this element is visible.
     * @param visible whether this element is visible
     */
    void setElementVisible(boolean visible);

    /**
     * Returns whether this element is the default element. The default element
     * is selected with the ENTER key.
     * @return whether this element is the default element
     */
    boolean isDefault();

    /**
     * Sets whether this element is the default element. The default element is
     * selected with the ENTER key.
     * @param isDefault whether this element is the default element
     */
    void setDefault(boolean isDefault);

    /**
     * Returns whether this gui element should be ignored for user interaction.
     * @return whether this gui element should be ignored for user interaction
     */
    boolean isIgnore();

    /**
     * Marks this gui element to be ignored for user interaction.
     */
    void setIgnore();

    @NotNull
    String getName();

    /**
     * Will be called when the user has clicked (pressed+released) this element.
     * This event will be delivered after {@link #mouseReleased(MouseEvent)}.
     * @param e the mouse event relative to this element
     */
    void mouseClicked(@NotNull MouseEvent e);

    /**
     * Will be called when the mouse has entered the bounding box of this
     * element.
     * @param e the mouse event relative to this element
     */
    void mouseEntered(@NotNull MouseEvent e);

    /**
     * Will be called when the mouse has left the bounding box of this element.
     * This function will not be called unless {@link #mouseEntered(MouseEvent)}
     * has been called before.
     * @param e the mouse event relative to this element
     */
    void mouseExited(@NotNull MouseEvent e);

    /**
     * Will be called when the user has pressed the mouse inside this element.
     * @param e the mouse event relative to this element
     */
    void mousePressed(@NotNull MouseEvent e);

    /**
     * Will be called when the user has released the mouse. This event may be
     * delivered even if no previous {@link #mousePressed(MouseEvent)} has been
     * delivered before.
     * @param e the mouse event relative to this element
     */
    void mouseReleased(@NotNull MouseEvent e);

    /**
     * Will be called when the mouse moves within this component. before.
     * @param e the mouse event relative to this element
     */
    void mouseMoved(@NotNull MouseEvent e);

    /**
     * Will be called when the mouse moves within this component while the
     * button is pressed. This event will be delivered after {@link
     * #mouseMoved(MouseEvent)}.
     * <p/>
     * Note: if the mouse leaves this element's bounding box while the mouse
     * button is still pressed, further <code>mouseDragged</code> (but no
     * <code>mouseMoved</code>) events will be generated.
     * @param e the mouse event relative to this element
     */
    void mouseDragged(@NotNull MouseEvent e);

    /**
     * Records that the contents have changed and must be repainted.
     */
    void setChanged();

    /**
     * Returns the changed flag.
     * @return the changed flag
     */
    boolean isChanged();

    /**
     * Clears the changed flag.
     */
    void resetChanged();

    /**
     * Sets the tooltip text to show when the mouse is inside this element.
     * @param tooltipText the text to show or <code>null</cod> to disable the
     * tooltip for this element
     */
    void setTooltipText(@Nullable String tooltipText);

    /**
     * Sets the tooltip text to show when the mouse is inside this element.
     * @param tooltipText the text to show, or <code>null</cod> to disable the
     * tooltip for this element
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h the height
     */
    void setTooltipText(@Nullable String tooltipText, int x, int y, int w, int h);

    /**
     * Returns the tooltip text to show when the mouse is inside this element.
     * @return the text to show or <code>null</cod> to disable the tooltip for
     *         this element
     */
    @Nullable
    TooltipText getTooltipText();

    /**
     * Sets the {@link GUIElementChangedListener} to be notified. Note that at
     * most one such listener may be set per gui element.
     * @param changedListener the listener or <code>null</code> to unset
     */
    void setChangedListener(@Nullable GUIElementChangedListener changedListener);

    int getWidth();

    int getHeight();

    void setBounds(int x, int y, int w, int h);

    /**
     * Returns whether this element includes a given point.
     * @param x the point's x coordinate
     * @param y the point's y coordinate
     * @return whether this element includes the point
     */
    boolean isElementAtPoint(int x, int y);

}
