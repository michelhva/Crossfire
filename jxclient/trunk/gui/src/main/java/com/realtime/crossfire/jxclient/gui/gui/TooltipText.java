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

import org.jetbrains.annotations.NotNull;

/**
 * Information for displaying tooltips.
 * @author Andreas Kirschbaum
 */
public class TooltipText {

    /**
     * The text.
     */
    @NotNull
    private final String text;

    /**
     * The x coordinate of the associated gui element.
     */
    private final int x;

    /**
     * The y coordinate of the associated gui element.
     */
    private final int y;

    /**
     * The width of the associated gui element.
     */
    private final int w;

    /**
     * The height of the associated gui element.
     */
    private final int h;

    /**
     * Creates a new instance.
     * @param text the text
     * @param x the x coordinate of the associated gui element
     * @param y the y coordinate of the associated gui element
     * @param w the width of the associated gui element
     * @param h the height of the associated gui element
     */
    public TooltipText(@NotNull final String text, final int x, final int y, final int w, final int h) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the text.
     * @return the text
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * Returns the x coordinate of the associated gui element.
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the associated gui element.
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the width of the associated gui element.
     * @return the width
     */
    public int getW() {
        return w;
    }

    /**
     * Returns the height of the associated gui element.
     * @return the height
     */
    public int getH() {
        return h;
    }

}
