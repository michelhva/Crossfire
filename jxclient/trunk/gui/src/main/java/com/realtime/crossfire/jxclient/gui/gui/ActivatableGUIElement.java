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

package com.realtime.crossfire.jxclient.gui.gui;

import java.awt.event.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link GUIElement} that can be set to active or inactive.
 * @author Andreas Kirschbaum
 */
public abstract class ActivatableGUIElement extends AbstractGUIElement {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * The {@link GUIElementListener} to notify.
     */
    @NotNull
    private final GUIElementListener elementListener;

    /**
     * Creates a new instance.
     * @param tooltipManager the tooltip manager to update
     * @param elementListener the element listener to notify
     * @param name the name of this element
     * @param transparency the transparency value for the backing buffer
     */
    protected ActivatableGUIElement(@NotNull final TooltipManager tooltipManager, @NotNull final GUIElementListener elementListener, @NotNull final String name, final int transparency) {
        super(tooltipManager, elementListener, name, transparency);
        this.elementListener = elementListener;
    }

    /**
     * Will be called whenever the active state has changed.
     */
    protected abstract void activeChanged();

    /**
     * Will be called when the user has pressed the mouse inside this element.
     * @param e the mouse event relative to this element
     */
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        setActive(true);
    }

    /**
     * Sets the active state of a GUI element.
     * @param active the active state
     */
    public void setActive(final boolean active) {
        elementListener.activeChanged(this, active);
    }

    /**
     * Returns whether a GUI element is active.
     * @return whether the element is active
     */
    public boolean isActive() {
        return elementListener.isActive(this);
    }

}
