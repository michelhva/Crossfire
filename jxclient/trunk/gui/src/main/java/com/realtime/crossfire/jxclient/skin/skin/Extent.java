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

package com.realtime.crossfire.jxclient.skin.skin;

import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates the extent of a GUI element. It consists of a location and a
 * size.
 * @author Andreas Kirschbaum
 */
public class Extent {

    /**
     * The x coordinate.
     */
    @NotNull
    private final Expression x;

    /**
     * The y coordinate.
     */
    @NotNull
    private final Expression y;

    /**
     * The width.
     */
    @NotNull
    private final Expression w;

    /**
     * The height.
     */
    @NotNull
    private final Expression h;

    /**
     * Creates a new instance.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param w the width
     * @param h thr height
     */
    public Extent(@NotNull final Expression x, @NotNull final Expression y, @NotNull final Expression w, @NotNull final Expression h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Returns the x coordinate.
     * @param width the screen width
     * @param height the screen height
     * @param prefWidth the preferred dialog width
     * @param prefHeight the preferred dialog height
     * @return the x coordinate
     */
    public int getX(final int width, final int height, final int prefWidth, final int prefHeight) {
        return x.evaluate(width, height, prefWidth, prefHeight);
    }

    /**
     * Returns the y coordinate.
     * @param width the screen width
     * @param height the screen height
     * @param prefWidth the preferred dialog width
     * @param prefHeight the preferred dialog height
     * @return the y coordinate
     */
    public int getY(final int width, final int height, final int prefWidth, final int prefHeight) {
        return y.evaluate(width, height, prefWidth, prefHeight);
    }

    /**
     * Returns the width.
     * @param width the screen width
     * @param height the screen height
     * @param prefWidth the preferred dialog width
     * @param prefHeight the preferred dialog height
     * @return the width
     */
    public int getW(final int width, final int height, final int prefWidth, final int prefHeight) {
        return w.evaluate(width, height, prefWidth, prefHeight);
    }

    /**
     * Returns the height.
     * @param width the screen width
     * @param height the screen height
     * @param prefWidth the preferred dialog width
     * @param prefHeight the preferred dialog height
     * @return the height
     */
    public int getH(final int width, final int height, final int prefWidth, final int prefHeight) {
        return h.evaluate(width, height, prefWidth, prefHeight);
    }

}
