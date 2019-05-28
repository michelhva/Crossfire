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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import org.jetbrains.annotations.NotNull;

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

}
