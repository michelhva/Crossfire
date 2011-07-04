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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for {@link Gui} related functions.
 * @author Andreas Kirschbaum
 */
public class GuiUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private GuiUtils() {
    }

    /**
     * Returns the extents of a string when rendered in a given {@link Font} on
     * this component.
     * @param text the text
     * @param fontMetrics the font metrics for the font
     * @return the extends
     */
    @NotNull
    public static Dimension getTextDimension(@NotNull final String text, @NotNull final FontMetrics fontMetrics) {
        final int width = fontMetrics.stringWidth(text);
        final int height = fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent();
        return new Dimension(width, height);
    }

    /**
     * Returns an  element's absolute screen coordinate.
     * @param element the element
     * @return the element's absolute x coordinate
     */
    public static int getElementX(@NotNull final Component element) {
        final Component gui = getGui(element);
        int x = gui != null ? gui.getX() : 0;
        for (Component component = element; component != null && !(component instanceof Gui); component = component.getParent()) {
            x += component.getX();
        }
        return x;
    }

    /**
     * Returns an element's absolute screen coordinate.
     * @param element the element
     * @return the element's absolute y coordinate
     */
    public static int getElementY(@NotNull final Component element) {
        final Component gui = getGui(element);
        int y = gui != null ? gui.getY() : 0;
        for (Component component = element; component != null && !(component instanceof Gui); component = component.getParent()) {
            y += component.getY();
        }
        return y;
    }

    /**
     * Returns the {@link Gui} an element is part of.
     * @param element the element
     * @return the gui or <code>null</code>
     */
    @Nullable
    public static Gui getGui(@NotNull final Component element) {
        for (Component component = element; component != null; component = component.getParent()) {
            if (component instanceof Gui) {
                return (Gui)component;
            }
        }
        return null;
    }

    /**
     * Sets the active state of a GUI element.
     * @param element the element
     * @param active the active state
     */
    public static void setActive(@NotNull final ActivatableGUIElement element, final boolean active) {
        final Gui gui = getGui(element);
        if (gui != null) {
            gui.setActiveElement(element, active);
        }
    }

    /**
     * Returns whether a GUI element is active.
     * @param element the element
     * @return whether the element is active
     */
    public static boolean isActive(@NotNull final Component element) {
        final Gui gui = getGui(element);
        return gui != null && gui.getActiveElement() == element;
    }

}
