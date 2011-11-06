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

import com.realtime.crossfire.jxclient.gui.label.AbstractLabel;
import org.jetbrains.annotations.NotNull;

/**
 * Manages the tooltip display. An {@link AbstractLabel} is moved/resized to
 * display a tooltip text for an "active" GUI element.
 * @author Andreas Kirschbaum
 */
public interface TooltipManager {

    /**
     * Displays the tooltip for a GUI element.
     * @param guiElement the GUI element to show the tooltip of
     */
    void setElement(@NotNull GUIElement guiElement);

    /**
     * Removes the tooltip of a GUI element. Does nothing if the given GUI
     * element is not active.
     * @param guiElement the gui element to remove the tooltip of
     */
    void unsetElement(@NotNull GUIElement guiElement);

    /**
     * Updates the tooltip text of a GUI element. Does nothing if the given GUI
     * element is not active.
     * @param guiElement the gui element to process
     */
    void updateElement(@NotNull GUIElement guiElement);

}
