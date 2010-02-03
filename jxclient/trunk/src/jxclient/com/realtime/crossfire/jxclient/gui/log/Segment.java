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

package com.realtime.crossfire.jxclient.gui.log;

import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.RectangularShape;
import org.jetbrains.annotations.NotNull;

/**
 * One segment of a {@link Line} which should be displayed without changing
 * text attributes.
 *
 * @author Andreas Kirschbaum
 */
public interface Segment
{
    /**
     * Sets the x-coordinate to display the segment.
     * @param x the x-coordinate
     */
    void setX(int x);

    /**
     * Sets the y-coordinate to display the segment.
     * @param y the y-coordinate
     */
    void setY(int y);

    /**
     * Sets the width to display the segment.
     * @param width the width
     */
    void setWidth(int width);

    /**
     * Draws this segment to a {@link Graphics} instance.
     * @param g the graphics to draw to
     * @param y the y-coordinate to draw to
     * @param fonts the fonts to use
     */
    void draw(@NotNull Graphics g, int y, @NotNull Fonts fonts);

    /**
     * Updates the cached attributes of this segment.
     * @param fonts the fonts instance to use
     * @param context the font render context to use
     */
    void updateAttributes(@NotNull Fonts fonts, @NotNull FontRenderContext context);

    /**
     * Returns the size of this segment in pixels.
     * @param fonts the fonts instance to use
     * @param context the font render context to use
     * @return the size
     */
    @NotNull
    RectangularShape getSize(@NotNull Fonts fonts, @NotNull FontRenderContext context);
}
