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

import java.awt.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for {@link GUIElement} related events.
 * @author Andreas Kirschbaum
 */
public interface GUIElementListener {

    /**
     * The {@link Gui} of a {@link Component} should be raised.
     * @param component the component
     */
    void raiseDialog(@NotNull Component component);

    /**
     * The active state of an {@link AbstractGUIElement} has changed.
     * @param element the changed element
     * @param active the new active state
     */
    void activeChanged(@NotNull ActivatableGUIElement element, boolean active);

    /**
     * Returns whether an {@link ActivatableGUIElement} is active.
     * @param element the element to check
     * @return whether the element is active
     */
    boolean isActive(@NotNull ActivatableGUIElement element);

}
