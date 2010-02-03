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

package com.realtime.crossfire.jxclient.skin;

import com.realtime.crossfire.jxclient.gui.GUICheckBox;
import com.realtime.crossfire.jxclient.gui.gui.GUIElement;
import com.realtime.crossfire.jxclient.gui.gui.TooltipManager;
import com.realtime.crossfire.jxclient.settings.options.CheckBoxOption;
import com.realtime.crossfire.jxclient.window.JXCWindowRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;

/**
 * A factory class to create "checkbox" instances.
 *
 * @author Andreas Kirschbaum
 */
public class CheckBoxFactory
{
    /**
     * The image for the "checked" state.
     */
    @NotNull
    private final BufferedImage checked;

    /**
     * The image for the "unchecked" state.
     */
    @NotNull
    private final BufferedImage unchecked;

    /**
     * The font to use.
     */
    @NotNull
    private final Font font;

    /**
     * The text color.
     */
    @NotNull
    private final Color color;

    /**
     * Create a new instance.
     *
     * @param checked The image for the "checked" state.
     *
     * @param unchecked The image for the "unchecked" state.
     *
     * @param font The font to use.
     *
     * @param color The text color.
     */
    public CheckBoxFactory(@NotNull final BufferedImage checked, @NotNull final BufferedImage unchecked, @NotNull final Font font, @NotNull final Color color)
    {
        this.checked = checked;
        this.unchecked = unchecked;
        this.font = font;
        this.color = color;
    }

    /**
     * Create a new checkbox.
     *
     * @param tooltipManager the tooltip manager to update
     *
     * @param windowRenderer the window renderer to notify
     *
     * @param name The name of this element.
     *
     * @param x The x-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param y The y-coordinate for drawing this element to screen; it is
     * relative to <code>gui</code>.
     *
     * @param w The width for drawing this element to screen.
     *
     * @param h The height for drawing this element to screen.
     *
     * @param option The option to affect.
     *
     * @param text The button text.
     *
     * @return The new checkbox.
     */
    @NotNull
    public GUIElement newCheckBox(@NotNull final TooltipManager tooltipManager, @NotNull final JXCWindowRenderer windowRenderer, @NotNull final String name, final int x, final int y, final int w, final int h, @NotNull final CheckBoxOption option, @NotNull final String text)
    {
            return new GUICheckBox(tooltipManager, windowRenderer, name, x, y, w, h, checked, unchecked, font, color, option, text);
    }
}
